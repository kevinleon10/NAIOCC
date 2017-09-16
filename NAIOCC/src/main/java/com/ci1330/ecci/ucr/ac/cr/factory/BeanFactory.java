package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.Bean;

import java.util.HashMap;

/**
 * Created by Josue Leon on 13/09/2017
 *
 */

public abstract class BeanFactory {

    protected HashMap<String,Bean> beansMap;
    protected BeanCreator beanCreator;

    public BeanFactory(){
        beansMap = new HashMap<String,Bean>();
        beanCreator = new BeanCreator(this);
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
    public Object getBean(String id) throws IdNotFoundException{
        try {
            if(!this.getBeansMap().containsKey(id)){
                throw new IdNotFoundException("Exception error: The id: " + id + " does not exist.");
            }
            if (this.getBeansMap().get(id).getScope().equals("Singleton")) {
                return this.beansMap.get(id).getInstance();
            } else {
                this.beansMap.get(id).initializeNewBean(); //agrega la nueva instancia a la lista del bean
                this.beansMap.get(id).injectBean();
                return this.beansMap.get(id).getInstance(); // devuelve la ultima instancia de la lista
            }
        }catch(IdNotFoundException i){
            i.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    /**
     * Iterates through all beans and checks if they are Sigleton to initialize and inject its dependencies.
     */
    public void initContainer(){
        for(HashMap.Entry<String,Bean> beanEntry: beansMap.entrySet()){
            if(beanEntry.getValue().getScope().equals("Singleton") && !beanEntry.getValue().isLazyGen()
                    && beanEntry.getValue() == null){
                beanEntry.getValue().initializeNewBean();
                beanEntry.getValue().getInstance().injectDependencies();
            }
        }
    }

    public void beansReferencesChecker(){
        for(HashMap.Entry<String,Bean> beanEntry: beansMap.entrySet()){
            if(beanEntry.getValue().getScope().equals("Singleton") && !beanEntry.getValue().isLazyGen()
                    && beanEntry.getValue() == null){
                beanEntry.getValue().initializeNewBean();
                beanEntry.getValue().getInstance().injectDependencies();
            }
        }
    }

    /**
     * Destroys all beans of the container.
     */
    public void shutDownHook(){
        for(HashMap.Entry<String,Bean> beanEntry: beansMap.entrySet()){
            beanEntry.getValue().destroyAll();
        }
    }

    public void registerConfig(){}

    //----------------------------------------------------------------
    // Standard Setters and Getters section
    //----------------------------------------------------------------

    public HashMap<String, Bean> getBeansMap() {
        return this.beansMap
    }

    public void setBeansMap(HashMap<String, Bean> beansMap) {
        this.beansMap = beansMap;
    }

    public BeanCreator getBeanCreator() {
        return beanCreator;
    }

    public void setBeanCreator(BeanCreator beanCreator) {
        this.beanCreator = beanCreator;
    }

}
