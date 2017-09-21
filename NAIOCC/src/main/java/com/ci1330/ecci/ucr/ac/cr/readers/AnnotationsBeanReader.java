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

    private String id;

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
        }
        if(reflectClass.isAnnotationPresent(Bean.class)){
                this.readBeanProperties(reflectClass);
                this.readBeanConstructor(reflectClass);
                this.readBeanSetter(reflectClass);
                this.beanCreator.addBeanToContainer();
        }
        else {
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
        Annotation beanAnnotation = beanClass.getAnnotation(Bean.class); //Obtengo el beanAnnotation
        Bean bean =(Bean)beanAnnotation; //Lo casteo
        String id = bean.id(); //Obtengo el id
        this.id = id;
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
            if(autowire != AutowireEnum.byName && autowire != AutowireEnum.byType){ //Si no es byName o byType
                try {
                    throw new AnnotationsBeanReaderException("Annotations Reader error: The '@AutowireEnum' in the 'bean' "+ this.id + " was not recognized. It is misspelled.");
                } catch (AnnotationsBeanReaderException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }

        //Busca los metodos
        String initMethod = null;
        String destroyMethod = null;
        int countInit = 0;
        int countDestroy = 0;
        for(Method method : beanClass.getDeclaredMethods()){
            if(method.isAnnotationPresent(Init.class)){
                initMethod = method.getName();
                ++countInit;
                if(countInit>1){
                    try {
                        throw new AnnotationsBeanReaderException("Annotations Reader error: The '@Init' in the 'bean' "+ this.id + " was not recognized. It has more than a definition");
                    } catch (AnnotationsBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
            if(method.isAnnotationPresent(Destroy.class)){
                destroyMethod = method.getName();
                ++countDestroy;
                if(countDestroy>1){
                    try {
                        throw new AnnotationsBeanReaderException("Annotations Reader error: The '@Destroy' in the 'bean' "+ this.id + " was not recognized. It has more than a definition");
                    } catch (AnnotationsBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }
        this.beanCreator.createBean(id, beanClass.getName(), scope, initMethod, destroyMethod,  lazyGeneration, autowire);
        /*System.out.println(id);
        System.out.println(scope);
        System.out.println(initMethod);
        System.out.println(destroyMethod);
        System.out.println(lazyGeneration);
        System.out.println(autowire);*/
    }

    /**
     * Reads the annotations of a constructor, if any.
     * @param beanClass
     */
    private void readBeanConstructor (Class beanClass) {
        int countConstructor = 0;
        for(Constructor constructor: beanClass.getDeclaredConstructors()){
            //System.out.println(constructor.getName());
            if(constructor.isAnnotationPresent(com.ci1330.ecci.ucr.ac.cr.annotations.Constructor.class)){
                ++countConstructor;
                if(countConstructor>1){
                    try {
                        throw new AnnotationsBeanReaderException("Annotations Reader error: The '@Constructor' in the 'bean' "+ this.id + " was not recognized. It has more than a definition");
                    } catch (AnnotationsBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
                if(constructor.isAnnotationPresent(Parameter.class)){
                    for(Annotation annotation: constructor.getDeclaredAnnotations()){
                        if(annotation.annotationType() == Parameter.class){
                            String paramType = ((Parameter)annotation).type();
                            int index = ((Parameter)annotation).index();
                            String value = ((Parameter)annotation).value();
                            String beanRef = ((Parameter)annotation).ref();
                            if ((value.equals("") && !(beanRef.equals(""))) || (!(paramType.equals("")) && beanRef.equals(""))) {
                                this.beanCreator.registerConstructorParameter(paramType, index, value, beanRef);
                                /*System.out.println(paramType);
                                System.out.println(index);
                                System.out.println(value);
                                System.out.println(beanRef);*/
                            }
                            else {
                                try {
                                    throw new AnnotationsBeanReaderException("Annotations Reader error: The '@Parameter' was not recognized in the 'bean' "+ this.id + ". It has 'value' and 'ref', or neither.");
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
            //System.out.println(field.getName());
            if (field.isAnnotationPresent(Attribute.class)) {
                String value = field.getAnnotation(Attribute.class).value(); //Obtengo, casteo y obtengo
                String ref = field.getAnnotation(Attribute.class).ref();
                if ((ref.equals("") && !(value.equals(""))) || (!(ref.equals("")) && value.equals(""))) { //Si solo uno esta
                    this.beanCreator.registerSetter(field.getName(), value, ref);
                    /*System.out.println(field.getName());
                    System.out.println(value);
                    System.out.println(ref);*/
                } else {
                    try {
                        throw new AnnotationsBeanReaderException("Annotations Reader error: The '@Attribute' was not recognized in the 'bean' "+ this.id + ". It has 'value' and 'ref', or neither.");
                    } catch (AnnotationsBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }
    }

}
