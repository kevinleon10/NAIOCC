package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.Bean;

import java.util.HashMap;

public abstract class BeanContainer {

    protected HashMap<String,Bean> beansMap;

    public HashMap<String, Bean> getBeansMap() {
        return beansMap;
    }

    public void setBeansMap(HashMap<String, Bean> beansMap) {
        this.beansMap = beansMap;
    }

    public BeanContainer(HashMap<String, Bean> beansMap) {
        this.beansMap = new HashMap<String, Bean>();
    }

    /**
     * Adds a bean to the container before initializing it.
     * @param bean
     */
    public void addBean(Bean bean){
        this.getBeansMap().put(bean.getId(), bean);
    }

    /**
     * Returns the instance of the bean, already injected
     * @param id
     * @return
     */
    public Object getBean(String id){
        //si es singleton devuelve el unico, si es prototype crea una nueva instancia
        // inyecta las dependencias
        if(this.getBeansMap().get(id).getScope().equals("Singleton")){
            return  this.getBeansMap().get(id).getInstance();
        }
        else {
            this.getBeansMap().get(id).initializeNewBean(); //agrega la nueva instancia a la lista del bean
            this.getBeansMap().get(id).injectBean();
            return  this.getBeansMap().get(id).getInstance(); // devuelve la ultima instancia de la lista
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
                        this.getBeansMap().get(id).injectBean();
                        beanEntry.getValue().getInstance().injectDependencies();
            }
        }
    }

    public void shutDownHook(){
        for(HashMap.Entry<String,Bean> beanEntry: beansMap.entrySet()){
            beanEntry.getValue().destroyAll();
        }
    }

}
