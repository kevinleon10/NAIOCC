package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.Bean;
import com.ci1330.ecci.ucr.ac.cr.exception.IdNotFoundException;
import com.ci1330.ecci.ucr.ac.cr.readers.AnnotationsBeanReader;

import java.util.HashMap;

/**
 * Created by Josue Leon on 13/09/2017
 *
 */
public class AnnotationsFactory extends BeanFactory{

    private AnnotationsBeanReader annotationsBeanReader;

    public AnnotationsFactory() {
        super();
        annotationsBeanReader = new AnnotationsBeanReader(this);
    }

    /**
     * Constructor for the case in which the user specifies a configuration class.
     * @param classConfig
     */
    public AnnotationsFactory(String classConfig) {
        super();
        annotationsBeanReader = new AnnotationsBeanReader(this);
        annotationsBeanReader.readBeans(classConfig);
        this.registerConfig(classConfig);
    }

    /**
     * The user may specify more configuration classes later using this method.
     */
    public void registerConfig(String classConfig){
        annotationsBeanReader.readBeans(classConfig);
    }

    @Override
    public Object getBean(String id) {
        try {
            return super.getBean(id);
        } catch (IdNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    @Override
    public void addBean(Bean bean) {
        super.addBean(bean);
    }

    @Override
    public void shutDownHook() {
        super.shutDownHook();
    }

    //----------------------------------------------------------------
    // Standard Setters and Getters section
    //----------------------------------------------------------------

    @Override
    public HashMap<String, Bean> getBeansMap() {
        return super.getBeansMap();
    }

    @Override
    public void setBeansMap(HashMap<String, Bean> beansMap) {
        super.setBeansMap(beansMap);
    }

    public AnnotationsBeanReader getAnnotationsBeanReader() {
        return annotationsBeanReader;
    }

    public void setAnnotationsBeanReader(AnnotationsBeanReader annotationsBeanReader) {
        this.annotationsBeanReader = annotationsBeanReader;
    }
}
