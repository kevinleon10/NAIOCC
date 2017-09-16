package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.Bean;
import com.ci1330.ecci.ucr.ac.cr.readers.AnnotationsBeanReader;

import java.util.HashMap;
import java.util.List;

public class AnnotationsFactory extends BeanFactory{

    private List annotationsClasses;

    public AnnotationsFactory() {
        super();
    }

    /**
     * El usuario puede especificar una clase de configuracion al crear el AnnotationsFactory
     * @param classConfig
     */
    public AnnotationsFactory(String classConfig) {
        super();
        this.registerAnnotations(classConfig);
    }

    /**
     * Decirle al reader de cual clase debe leer la configuracion del bean
     */
    public void registerAnnotations(String classConfig){
        //AnnotationsBeanReader.readBean(classConfig);
    }

    public List getAnnotationsClasses() {
        return annotationsClasses;
    }

    public void setAnnotationsClasses(List annotationsClasses) {
        this.annotationsClasses = annotationsClasses;
    }

    @Override
    public HashMap<String, Bean> getBeansMap() {
        return super.getBeansMap();
    }

    @Override
    public void setBeansMap(HashMap<String, Bean> beansMap) {
        super.setBeansMap(beansMap);
    }

    @Override
    public Object getBean(String id) {
        return super.getBean(id);
    }

    @Override
    public void addBean(Bean bean) {
        super.addBean(bean);
    }

    @Override
    public void shutDownHook() {
        super.shutDownHook();
    }
}
