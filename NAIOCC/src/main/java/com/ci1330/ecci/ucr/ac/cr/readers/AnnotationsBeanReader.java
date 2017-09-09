package com.ci1330.ecci.ucr.ac.cr.readers;

import java.lang.reflect.Method;

public class AnnotationsBeanReader extends BeanReader {

    /**
     * Recieves the name of a class and creates the corresponding Class object,
     * and calls a method to read it
     * @param inputName
     */
    public void readBeans(String inputName) {

    }

    /**
     * Recieves the class and starts to read the annotations, if any.
     * @param beanClass
     */
    private void readBeanProperties (Class beanClass) {

    }

    /**
     * Reads the annotations of a specific method, if any.
     * @param beanSetterMethod
     */
    private void readBeanSetter (Method beanSetterMethod) {

    }

    /**
     * Reads the annotations of a constructor, if any.
     * @param beanConsMethod
     */
    private void readBeanConstructor (Method beanConsMethod) {

    }
}
