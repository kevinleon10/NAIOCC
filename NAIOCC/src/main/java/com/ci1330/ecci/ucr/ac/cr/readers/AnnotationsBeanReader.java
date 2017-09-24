package com.ci1330.ecci.ucr.ac.cr.readers;

import com.ci1330.ecci.ucr.ac.cr.annotations.*;
import com.ci1330.ecci.ucr.ac.cr.bean.AutowireEnum;
import com.ci1330.ecci.ucr.ac.cr.exception.AnnotationsBeanReaderException;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanCreator;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

public class AnnotationsBeanReader extends BeanReader {

    private String currID;

    public AnnotationsBeanReader(BeanFactory beanFactory) {
        super(beanFactory);
    }

    public AnnotationsBeanReader(BeanCreator beanCreator) {
        super(beanCreator);
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
            System.exit(1);
        }

        if (reflectClass.isAnnotationPresent(Bean.class)) {

            this.readBeanProperties(reflectClass);
            this.readBeanConstructor(reflectClass);
            this.readBeanSetter(reflectClass);
            this.beanCreator.addBeanToContainer();

        } else {
            try {
                throw new AnnotationsBeanReaderException("Annotations Reader error: The 'class' " + inputName + " does not have the annotation '@Bean'");
            } catch (AnnotationsBeanReaderException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * Recieves the class and starts to read the annotations, if any.
     * @param beanClass
     */
    private void readBeanProperties (Class beanClass) {

        Bean bean  = (Bean) beanClass.getAnnotation(Bean.class); //Obtengo el beanAnnotation
        this.currID = bean.id();

        com.ci1330.ecci.ucr.ac.cr.bean.Scope scope = com.ci1330.ecci.ucr.ac.cr.bean.Scope.Singleton; //declaro y defino el scope
        if(beanClass.isAnnotationPresent(Scope.class)){ //si hay lo cambio
            scope = ((Scope)(beanClass.getAnnotation(Scope.class))).value(); //obtengo la anotation, la casteo y obtengo el value
        }

        boolean lazyGeneration = false;
        if(beanClass.isAnnotationPresent(Lazy.class)){
            lazyGeneration = true;
        }

        AutowireEnum autowire = AutowireEnum.none;
        if(beanClass.isAnnotationPresent(Autowire.class)){
            autowire = ((Autowire)(beanClass.getAnnotation(Autowire.class))).value();
        }

        //Searches for init and destroy
        String initMethod = null;
        String destroyMethod = null;

        for(Method method : beanClass.getDeclaredMethods()){

            if(method.isAnnotationPresent(Init.class)){
                if (initMethod == null) {
                    initMethod = method.getName();
                } else {
                    try {
                        throw new AnnotationsBeanReaderException("AnnotationsReader error: The '@Init' in the 'bean' "+ this.currID + " was not recognized. It has more than a definition");
                    } catch (AnnotationsBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }

            if(method.isAnnotationPresent(Destroy.class)){
                if(destroyMethod == null) {
                    destroyMethod = method.getName();
                } else {
                    try {
                        throw new AnnotationsBeanReaderException("AnnotationsReader error: The '@Destroy' in the 'bean' "+ this.currID + " was not recognized. It has more than a definition");
                    } catch (AnnotationsBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }

        }
        this.beanCreator.createBean(this.currID, beanClass.getName(), scope, initMethod, destroyMethod,  lazyGeneration, autowire);
    }

    /**
     * Reads the annotations of a constructor, if any.
     * @param beanClass
     */
    private void readBeanConstructor (Class beanClass) {
        int matchedConstructorCounter = 0;
        for (Constructor constructor : beanClass.getDeclaredConstructors()) {

            if (constructor.isAnnotationPresent(com.ci1330.ecci.ucr.ac.cr.annotations.Constructor.class)) {

                ++matchedConstructorCounter;
                if (matchedConstructorCounter > 1) {
                    try {
                        throw new AnnotationsBeanReaderException("Annotations Reader error: The '@Constructor' in the 'bean' " + this.currID + " was not recognized. It has more than a definition");
                    } catch (AnnotationsBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }

                if (constructor.isAnnotationPresent(Parameter.class)) {

                    for (Annotation annotation : constructor.getDeclaredAnnotations()) {
                        if (annotation.annotationType() == Parameter.class) {

                            String paramType = ((Parameter) annotation).type();
                            if (paramType.equals("")) {
                                paramType = null;
                            }

                            int index = ((Parameter) annotation).index();

                            String value = ((Parameter) annotation).value();
                            if (value.equals("")) {
                                value = null;
                            }

                            String beanRef = ((Parameter) annotation).ref();
                            if (beanRef.equals("")) {
                                beanRef = null;
                            }

                            if ( (value == null && beanRef != null) || (paramType != null && beanRef == null) ) {
                                this.beanCreator.registerConstructorParameter(paramType, index, value, beanRef);
                            } else {
                                try {
                                    throw new AnnotationsBeanReaderException("Annotations Reader error: The '@Parameter' was not recognized in the 'bean' " + this.currID + ". It has 'value' and 'ref', or neither.");
                                } catch (AnnotationsBeanReaderException e) {
                                    e.printStackTrace();
                                    System.exit(1);
                                }
                            }

                        }
                    }

                }
                this.beanCreator.registerConstructor();
            }
        }
    }

    /**
     * Reads the annotations of a specific method, if any.
     * @param beanClass
     */
    private void readBeanSetter (Class beanClass) {
        for (Field field : beanClass.getDeclaredFields()) {

            if (field.isAnnotationPresent(Attribute.class)) {

                String value = field.getAnnotation(Attribute.class).value(); //Obtengo, casteo y obtengo
                if (value.equals("")) {
                    value = null;
                }

                String ref = field.getAnnotation(Attribute.class).ref();
                if (ref.equals("")) {
                    ref = null;
                }

                if ((ref == null && value != null) || (ref != null && value == null)) { //Si solo uno esta
                    this.beanCreator.registerSetter(field.getName(), value, ref);
                } else {
                    try {
                        throw new AnnotationsBeanReaderException("Annotations Reader error: The '@Attribute' was not recognized in the 'bean' "+ this.currID + ". It has 'value' and 'ref', or neither.");
                    } catch (AnnotationsBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }
    }

}
