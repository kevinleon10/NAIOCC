package com.ci1330.ecci.ucr.ac.cr.readers;

import com.ci1330.ecci.ucr.ac.cr.bean.Bean;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanCreator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationsBeanReader extends BeanReader {

    private BeanCreator beanCreator;

    public AnnotationsBeanReader(BeanCreator beanCreator) {
        this.beanCreator = beanCreator;
    }

    /**
     * Recieves the name of a class and creates the corresponding Class object,
     * and calls a method to read it
     * @param inputName
     */
    public void readBeans(String inputName) {
        Class reflectClass = null;
        try {
            reflectClass = Class.forName(inputName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Annotation[] annotations = reflectClass.getAnnotations();
        Annotation[] annotations1 = reflectClass.getDeclaredAnnotations();
        for(int i=0; i<annotations.length; ++i){
            System.out.println(annotations[i].toString());
        }
        for(int i=0; i<annotations1.length; ++i){
            System.out.println(annotations[i].toString());
        }
        if(reflectClass.isAnnotationPresent(com.ci1330.ecci.ucr.ac.cr.readers.Bean.class)){
                System.out.println("Me cago en Josue");
                this.readBeanProperties(com.ci1330.ecci.ucr.ac.cr.readers.Bean.class);
        }
        else {
            System.out.println("La clase " + inputName + " no posee la Annotation @Bean");
        }
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
