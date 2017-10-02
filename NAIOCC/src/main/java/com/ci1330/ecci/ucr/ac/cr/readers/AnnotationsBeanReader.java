package com.ci1330.ecci.ucr.ac.cr.readers;

import com.ci1330.ecci.ucr.ac.cr.annotations.*;
import com.ci1330.ecci.ucr.ac.cr.bean.AutowireEnum;
import com.ci1330.ecci.ucr.ac.cr.bean.Stereotype;
import com.ci1330.ecci.ucr.ac.cr.exception.AnnotationsBeanReaderException;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanCreator;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 *
 * The reader is given a String, and then tries to map it with a class
 * and extract the metadata for the BeanCreator
 */
public class AnnotationsBeanReader extends BeanReader {

    private String currID; //The bean ID
    private Stereotype stereotype; //The type of stereotype


    /** Constructor, receives the {@link BeanFactory} that created him
     * @param beanFactory the father {@link BeanFactory}
     */
    public AnnotationsBeanReader(BeanFactory beanFactory) {
        super(beanFactory);
    }

    /**
     * Constructor, receives the {@link BeanCreator} that it'll use
     * @param beanCreator the {@link BeanCreator} to use
     */
    AnnotationsBeanReader(BeanCreator beanCreator) {
        super(beanCreator);
    }

    /**
     * Receives the name of a class and creates the corresponding Class object,
     * and calls a method to read it
     * @param inputName the name of the class
     */
    @Override
    public void readBeans(String inputName) {
        Class reflectClass = null;

        try {
            reflectClass = Class.forName(inputName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        //Check if there is more than a stereotype
        if (reflectClass.isAnnotationPresent(Bean.class) && !reflectClass.isAnnotationPresent(Repository.class)
                && !reflectClass.isAnnotationPresent(Service.class) && !reflectClass.isAnnotationPresent(Controller.class)) {

            this.stereotype = Stereotype.Bean;

        } else if(!reflectClass.isAnnotationPresent(Bean.class) && reflectClass.isAnnotationPresent(Repository.class)
                && !reflectClass.isAnnotationPresent(Service.class) && !reflectClass.isAnnotationPresent(Controller.class)) {

            this.stereotype = Stereotype.Repository;

        } else if(!reflectClass.isAnnotationPresent(Bean.class) && !reflectClass.isAnnotationPresent(Repository.class)
                && reflectClass.isAnnotationPresent(Service.class) && !reflectClass.isAnnotationPresent(Controller.class)){

            this.stereotype = Stereotype.Service;

        } else if(!reflectClass.isAnnotationPresent(Bean.class) && !reflectClass.isAnnotationPresent(Repository.class)
                && !reflectClass.isAnnotationPresent(Service.class) && reflectClass.isAnnotationPresent(Controller.class)){

            this.stereotype = Stereotype.Controller;

        } else {
            try {
                throw new AnnotationsBeanReaderException("Annotations Reader error: The 'class' " + inputName + " does not have the Stereotype Annotation or has more than a Stereotype");
            } catch (AnnotationsBeanReaderException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        //Now read the rest of the metadata
        this.readBeanProperties(reflectClass);
        this.readBeanConstructor(reflectClass);
        this.readBeanSetter(reflectClass);
        this.beanCreator.addBeanToContainer();
    }

    /**
     * Receives the class and starts to read the annotations, if any.
     * @param beanClass the class to search
     */
    private void readBeanProperties (Class beanClass) {

        //Get the bean ID depending of the stereotype
        switch (this.stereotype){
            case Bean:
                Bean bean  = (Bean) beanClass.getDeclaredAnnotation(Bean.class);
                this.currID = bean.value();
                break;
            case Controller:
                Controller controller = (Controller) beanClass.getDeclaredAnnotation(Controller.class);
                this.currID = controller.value();
                break;
            case Repository:
                Repository repository = (Repository) beanClass.getDeclaredAnnotation(Repository.class);
                this.currID = repository.value();
                break;
            default:
                Service service = (Service) beanClass.getDeclaredAnnotation(Service.class);
                this.currID = service.value();
                break;
        }


        //The default scope is singleton
        com.ci1330.ecci.ucr.ac.cr.bean.Scope scope = com.ci1330.ecci.ucr.ac.cr.bean.Scope.Singleton;
        if(beanClass.isAnnotationPresent(Scope.class)){
            Scope scopeAnnotation = (Scope)(beanClass.getAnnotation(Scope.class));
            scope = super.determineScope(scopeAnnotation.value().toLowerCase());
        }

        //The default class-autowire is none
        AutowireEnum autowire = AutowireEnum.none;
        if(beanClass.isAnnotationPresent(ClassAutowire.class)){
            ClassAutowire autowireAnnotation = (ClassAutowire)(beanClass.getAnnotation(ClassAutowire.class));
            autowire = super.determineClass_Autowire(autowireAnnotation.value().toLowerCase());
        }

        //The default lazyGen is false
        boolean lazyGeneration = false;
        if(beanClass.isAnnotationPresent(Lazy.class)){
            lazyGeneration = true;
        }

        //Searches for init and destroy
        String initMethod = null;
        String destroyMethod = null;

        //Travel by every method
        for(Method method : beanClass.getDeclaredMethods()){

            //If there is @Init
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

            //If there is @Destroy
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
     * @param beanClass the class to search
     */
    private void readBeanConstructor (Class beanClass) {
        boolean constructorAlreadyMatched = false;
        for (Constructor constructor : beanClass.getDeclaredConstructors()) {

            //If there is @Constructor
            if (constructor.isAnnotationPresent(com.ci1330.ecci.ucr.ac.cr.annotations.Constructor.class)) {

                if (constructorAlreadyMatched) {
                    try {
                        throw new AnnotationsBeanReaderException("Annotations Reader error: The '@Constructor' in the 'bean' " + this.currID + " was not recognized. The constructor has more than a definition");
                    } catch (AnnotationsBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
                constructorAlreadyMatched = true;

                //If there is @Parameter
                if (constructor.isAnnotationPresent(Parameter.class)) {

                    //Travel by every annotation in the constructor
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

                            final boolean refTypeCombination = paramType != null & beanRef != null && value == null;
                            final boolean valueTypeCombination = paramType != null && value != null && beanRef == null;

                            //Check if the combinations are valid
                            if ( refTypeCombination || valueTypeCombination ) {
                                this.beanCreator.registerConstructorParameter(paramType, index, value, beanRef, AutowireEnum.none);
                            } else {
                                try {
                                    throw new AnnotationsBeanReaderException("Annotations Reader error: The '@Parameter' was not recognized in the 'bean' " + this.currID + ". It has an illegal value, ref and type combination.");
                                } catch (AnnotationsBeanReaderException e) {
                                    e.printStackTrace();
                                    System.exit(1);
                                }
                            }

                        }
                    }

                }
            } // if (constructor.isAnnotationPresent(com.ci1330.ecci.ucr.ac.cr.annotations.Constructor.class))
            else if (constructor.isAnnotationPresent(AtomicAutowire.class)){

                if (constructorAlreadyMatched) {
                    try {
                        throw new AnnotationsBeanReaderException("Annotations Reader error: The '@AtomicAutowire' in the 'bean' " + this.currID + " was not recognized. The constructor has more than a definition");
                    } catch (AnnotationsBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
                constructorAlreadyMatched = true;

                this.beanCreator.explicitConstructorDefinition(constructor);

            }
        }
    }

    /**
     * Reads the annotations of a specific method, if any.
     * @param beanClass the class to search
     */
    private void readBeanSetter (Class beanClass) {
        //Travel by every field
        for (Field field : beanClass.getDeclaredFields()) {

            //If there is @Attribute
            if (field.isAnnotationPresent(Attribute.class)) {

                String value = field.getAnnotation(Attribute.class).value();
                if (value.equals("")) {
                    value = null;
                }

                String ref = field.getAnnotation(Attribute.class).ref();
                if (ref.equals("")) {
                    ref = null;
                }

                //Check if there is value or ref
                if ((ref == null && value != null) || (ref != null && value == null)) {
                    this.beanCreator.registerSetter(field.getName(), value, ref, AutowireEnum.none);
                } else {
                    try {
                        throw new AnnotationsBeanReaderException("Annotations Reader error: The '@Attribute' was not recognized in the 'bean' "+ this.currID + ". It has an illegal combination of value and ref.");
                    } catch (AnnotationsBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
            //The reader will only recognize autowire if an Attribute annotation is not present
            else if (field.isAnnotationPresent(AtomicAutowire.class)) {

                //It is assumed to be the special annotation autowiring
                this.beanCreator.registerSetter(field.getName(), null, null, AutowireEnum.annotation);
            }
        }
    }

}
