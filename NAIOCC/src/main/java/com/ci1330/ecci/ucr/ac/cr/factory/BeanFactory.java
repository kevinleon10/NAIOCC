package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.Bean;
import com.ci1330.ecci.ucr.ac.cr.bean.BeanAttribute;
import com.ci1330.ecci.ucr.ac.cr.bean.BeanParameter;
import com.ci1330.ecci.ucr.ac.cr.bean.Scope;
import com.ci1330.ecci.ucr.ac.cr.exception.BeanTypeConflictException;
import com.ci1330.ecci.ucr.ac.cr.exception.IdNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 *
 * BeanFactory parent class which has the container and manages
 * the control flow of NAIoCC. User's request for beans via an
 * instance of this class.
 */
public abstract class BeanFactory {

    protected HashMap<String,Bean> beansMap; // The container in which beans are stored. A map with beans' id as key and the respective Bean as value

    private boolean nonFatalCycle;

    /**
     * Constructor of the class, initializes the container
     */
    public BeanFactory(){
        beansMap = new HashMap<>();
    }

    /**
     * Adds a bean to the container before initializing it.
     * @param bean the bean to add
     */
    public void addBean(Bean bean){
        this.beansMap.put(bean.getId(), bean);
    }

    /**
     * Returns the instance of the bean, already injected. If it is singleton it
     * returns the only instance, otherwise creates a new one (prototype).
     * @param id the bean's id
     * @return the requested bean's instance
     */
    public Object getBean(String id) {
        try {

            if (!this.beansMap.containsKey(id)) {
                throw new IdNotFoundException("Exception error: The id: " + id + " does not exist.");
            }

            Bean currBean = this.beansMap.get(id);
            if (currBean.getBeanScope() == Scope.Prototype || currBean.getInstance() == null) {

                currBean.createNewInstance(); // Adds the new instance to the bean's list
                currBean.injectDependencies();
                currBean.initialize();

            }

            return currBean.getInstance(); // Returns the last instance of the bean's list

        } catch (IdNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }

    /**
     * Iterates through all beans and checks their scope to initialize and inject its dependencies.
     */
    protected void initContainer(){
        for(HashMap.Entry<String,Bean> beanEntry: beansMap.entrySet()) {    // Iterates through the container to autowire dependencies
            Bean currBean = beanEntry.getValue();
            currBean.autowire();                    // Autowires the bean, if indicated as such
            currBean.checkBeanProperties();         // Checks there are no conflicts in its properties
        }

        cycleDetection();       // Checks if there a cycles between the dependencies of the beans

        for(HashMap.Entry<String,Bean> beanEntry: beansMap.entrySet()){     // Iterates through the container to initialize beans
            Bean currBean = beanEntry.getValue();

            if(currBean.getBeanScope() == Scope.Singleton && !currBean.isLazyGen()  // Instantiates the bean only if it is Singleton, without lazy generation and haven't been initialized
                    && currBean.getInstance() == null){
                currBean.createNewInstance();
                currBean.injectDependencies();
                currBean.initialize();
            }

        }

    }

    /**
     * Finds a bean by its type for autowiring purposes. If there's no bean
     * with this type in the container or if there are more than one, it returns null.
     * @param beanType the bean's type
     * @return the Bean with the type requested, null if not found
     */
    public Bean findBean(Class beanType) throws BeanTypeConflictException {
        Bean bean = null;

        for(HashMap.Entry<String,Bean> beanEntry: beansMap.entrySet()){     //Iterates through the container

            if(beanEntry.getValue().getBeanClass().equals(beanType)){       //Checks if it is of the requested type
                if (bean == null) {
                    bean = beanEntry.getValue();
                } else {
                    throw new BeanTypeConflictException("Injection by type error: two or more beans share the same type.");
                }
            }

        }

        return bean;
    }

    /**
     * Finds a bean by its name. If there's no bean
     * with this name in the container, it returns null.
     * @param beanId the bean's id
     * @return The bean with the corresponding id, null if it wasn't found
     */
    public Bean findBean(String beanId){
        Bean bean = null;
        if(this.beansMap.containsKey(beanId)){
            bean = this.beansMap.get(beanId);
        }
        return bean;
    }

    /**
     * Checks if the specified bean is in the container.
     * @param beanId the bean's id
     * @return true if the bean is in the container, false otherwise
     */
    public boolean containsBean(String beanId){
        return this.beansMap.containsKey(beanId);
    }

    /**
     * Destroys all beans' instances of the container.
     */
    public void shutDownHook(){
        for(HashMap.Entry<String,Bean> beanEntry: beansMap.entrySet()){
            beanEntry.getValue().destroyAllInstances();
        }
    }

    /**
     * Iterates all the references of all the beans and checks if there is a cycle
     */
    private void cycleDetection() {
        HashMap< String, List<String> > setterReferences = new HashMap<>();
        HashMap< String, List<String> > constructorReferences = new HashMap<>();

        for (Map.Entry<String, Bean> currEntry : this.beansMap.entrySet()) {
            Bean currBean = currEntry.getValue();

            this.insertConstructorReferences(currBean, constructorReferences);
            this.insertSetterReferences(currBean, setterReferences);
        }

        //Checks if any of those maps has a cycle
        this.thereIsCycle(constructorReferences, true);
        this.thereIsCycle(setterReferences, false);
    }

    /**
     * Registers the constructor references for a bean
     * @param currBean the bean to search
     * @param constructorReferences a list of the references
     */
    private void insertConstructorReferences(Bean currBean, HashMap< String, List<String> > constructorReferences) {
        List<String> referencesList = new ArrayList<>(); //If there is no dependency the list will be empty

        //If the bean has a constructor
        if (currBean.getBeanConstructor() != null) {

            //Iterate every parameter that has a reference and put it on the map
            for (BeanParameter currBeanParameter : currBean.getBeanConstructor().getBeanParameterList()) {

                String currReference = currBeanParameter.getBeanRef();
                //If the parameter has a beanRef, append it
                if (currReference != null) {
                    referencesList.add(currReference);
                }
            }

        }

        constructorReferences.put(currBean.getId(), referencesList);
    }

    /**
     * Registers the setter references for a bean
     * @param currBean the bean to search
     * @param setterReferences a list of the references
     */
    private void insertSetterReferences(Bean currBean, HashMap< String, List<String> > setterReferences) {
        List<String> referenceList = new ArrayList<>(); //If there is no dependency the list will be empty

        //Iterate every attribute that has a reference and put it on the map
        for (BeanAttribute currBeanAttribute : currBean.getBeanAttributeList()) {

            String currReference = currBeanAttribute.getBeanRef();
            //If the parameter has a beanRef, append it
            if (currReference != null) {
                referenceList.add(currReference);
            }
        }

        setterReferences.put(currBean.getId(), referenceList);
    }

    /**
     * For every entry in the map, checks the cycles, if there is an invalid one, the program exits.
     * @param referenceMap all the references for all beans
     * @param isConstructorInjection indicates if it is checking constructor injection or not.
     */
    private void thereIsCycle(HashMap< String, List<String> > referenceMap, boolean isConstructorInjection) {
        List<String> cycleLessReferences = new ArrayList<>(); //References that were already confirmed as cycle-less
        List<String> currentTrail = new ArrayList<>(); //The reference trail

        for (String beanEntry : referenceMap.keySet()) {
            this.nonFatalCycle = false;
            if (checkCycle(beanEntry, referenceMap, currentTrail, cycleLessReferences, isConstructorInjection)) {
                System.err.println("CYCLE DETECTED: A reference or chain of references of " + beanEntry + " causes an invalid cycle.");
                System.exit(1);
            } else if (this.nonFatalCycle) {
                System.err.println("CYCLE DETECTED (WARNING): The cycle is not fatal! But keep track of the cycles...");
            }
        }

    }

    /**
     * Recursively check if a chain of references causes a cycle.
     * @param reference The reference to check
     * @param referenceMap Map of all references
     * @param currentTrail The current trail of the recursive call
     * @param cycleLessReferences The trail of cycle less references, so we don't repeat searches
     * @param isConstructorInjection indicates if it's checking constructor injection.
     * @return true if there was a cycle, false if not.
     */
    private boolean checkCycle(String reference, HashMap< String, List<String> > referenceMap,
                              List<String> currentTrail, List<String> cycleLessReferences, boolean isConstructorInjection) {
        boolean cycleDetected = false;

        if ( cycleLessReferences.contains(reference) || referenceMap.get(reference).isEmpty() )  {
            //If the reference was already checked or doesn't have associated references, there is no cycle
            cycleDetected = false;
        } else if (currentTrail.contains(reference)) {
            //If the dependency was already in the trail, there is a cycle
            //But in setter injection, only a pure prototype cycle causes trouble
            System.err.println("CYCLE DETECTED: Checking if the cycle is fatal...");
            this.nonFatalCycle = true;
            if (isConstructorInjection) {
                cycleDetected = true;
            } else {
                cycleDetected =  checkIfInvalid(currentTrail, reference);
            }

        } else {
            //If the reference has associated references and is not in the trail
            //For every associated reference check if it causes a cycle

            currentTrail.add(reference); //Add the current dependency to the trail

            String associatedReference;
            List<String> associatedReferences = referenceMap.get(reference);

            for (int index = 0; index < associatedReferences.size() && !cycleDetected; index++) {

                associatedReference = associatedReferences.get(index);
                cycleDetected = checkCycle(associatedReference, referenceMap, currentTrail,
                        cycleLessReferences, isConstructorInjection);

            }

            currentTrail.remove(reference); //Remove the current dependency to the trail
        }

        if (!cycleDetected) {
            //If the reference doesn't cause a cycle, register it as cycle-less
            cycleLessReferences.add(reference);
        }

        return cycleDetected;
    }

    /**
     * Checks if the cycle has only prototypes
     * @param trail the trail of the recursive call
     * @return true if illegal cycle, false if not.
     */
    private boolean checkIfInvalid (List<String> trail, String reference) {
        int prototypeCount = 0;
        int referenceCount = 0;

        String dependency;

        //Start from the reference that causes the cycle
        for (int index = trail.indexOf(reference); index < trail.size(); index++) {
            dependency = trail.get(index);
            Bean currBean = this.findBean(dependency);

            if (currBean.getBeanScope() == Scope.Prototype) {
                prototypeCount++;
            }

            referenceCount++;
        }

        return prototypeCount == referenceCount;
    }

    //----------------------------------------------------------------
    // Standard Setters and Getters section
    //----------------------------------------------------------------

    public HashMap<String, Bean> getBeansMap() {
        return this.beansMap;
    }

    public void setBeansMap(HashMap<String, Bean> beansMap) {
        this.beansMap = beansMap;
    }

}
