package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.Bean;
import com.ci1330.ecci.ucr.ac.cr.readers.AnnotationsBeanReader;

import java.util.HashMap;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 *
 * AnnotationsFactory class which inherits from BeanFactory
 * and registers the Annotations classes from which the configuration
 * must be read and tells the reader to parse it.
 */
public class AnnotationsFactory extends BeanFactory{

    private AnnotationsBeanReader annotationsBeanReader;    // Instance of the annotations reader

    /**
     * Constructor of the class, it initializes the super-class attributes and
     * also the annotations bean reader.
     */
    public AnnotationsFactory() {
        super();
        annotationsBeanReader = new AnnotationsBeanReader(this);
    }

    /**
     * Constructor of the class, it initializes the super-class attributes and
     * also the annotations bean reader. It receives the path of a class which
     * holds annotations configurations for the reader to parse it.
     * @param classConfig the name of the class to use
     */
    public AnnotationsFactory(String classConfig) {
        super();
        annotationsBeanReader = new AnnotationsBeanReader(this);
        this.registerConfig(classConfig);
    }

    /**
     * Allows the user to register more configurations
     * classes later, indicating their path.
     * @param classConfig the name of the class to use
     */
    public void registerConfig(String classConfig){
        annotationsBeanReader.readBeans(classConfig);
        super.initContainer();
    }

    /**
     * Return a bean instance from the super class.
     * @param id the beanId
     * @return the bean instance
     */
    @Override
    public Object getBean(String id) {
        return super.getBean(id);
    }

    /**
     * Adds a bean to the container
     * @param bean the {@link Bean} class
     */
    @Override
    public void addBean(Bean bean) {
        super.addBean(bean);
    }

    /**
     * Calls the super method for shutDownHook
     */
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
