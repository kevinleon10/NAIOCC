package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.Bean;
import com.ci1330.ecci.ucr.ac.cr.bean.Scope;
import com.ci1330.ecci.ucr.ac.cr.exception.BeanTypeConflictException;
import com.ci1330.ecci.ucr.ac.cr.exception.IdNotFoundException;

import java.util.HashMap;

/**
 * Created by Josue Leon on 13/09/2017
 *
 */

public abstract class BeanFactory {

    protected HashMap<String,Bean> beansMap;

    public BeanFactory(){
        beansMap = new HashMap<>();
    }

    /**
     * Adds a bean to the container before initializing it.
     * @param bean
     */
    public void addBean(Bean bean){
        this.beansMap.put(bean.getId(), bean);
    }

    /**
     * Returns the instance of the bean, already injected. If it is singleton it
     * returns the only instance, otherwise creates a new one (prototype).
     * @param id
     * @return
     */
    public Object getBean(String id) {
        try {

            if (!this.beansMap.containsKey(id)) {
                throw new IdNotFoundException("Exception error: The id: " + id + " does not exist.");
            }

            Bean currBean = this.beansMap.get(id);
            if (currBean.getBeanScope() == Scope.Prototype || currBean.getInstance() == null) {

                currBean.createNewInstance(); //agrega la nueva instancia a la lista del bean
                currBean.injectDependencies();
                currBean.initialize();

            }

            return currBean.getInstance(); // devuelve la ultima instancia de la lista

        } catch (IdNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }

    /**
     * Iterates through all beans and checks if they are Sigleton to initialize and inject its dependencies.
     */
    protected void initContainer(){

        for(HashMap.Entry<String,Bean> beanEntry: beansMap.entrySet()){
            Bean currBean = beanEntry.getValue();

            if(currBean.getBeanScope() == Scope.Singleton && !currBean.isLazyGen()
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
     * @param beanType
     * @return
     */
    public Bean findBean(Class beanType) throws BeanTypeConflictException {
        Bean bean = null;

        for(HashMap.Entry<String,Bean> beanEntry: beansMap.entrySet()){

            if(beanEntry.getValue().getBeanClass().equals(beanType)){
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
     * Finds a bean by its name for autowiring purposes. If there's no bean
     * with this name in the container, it returns null.
     * @param beanId
     * @return
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
     * @param beanId
     * @return
     */
    public boolean containsBean(String beanId){
        if(this.beansMap.containsKey(beanId)){
            return true;
        }
        return false;
    }

    /**
     * Destroys all beans of the container.
     */
    public void shutDownHook(){
        for(HashMap.Entry<String,Bean> beanEntry: beansMap.entrySet()){
            beanEntry.getValue().destroyAllInstances();
        }
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
