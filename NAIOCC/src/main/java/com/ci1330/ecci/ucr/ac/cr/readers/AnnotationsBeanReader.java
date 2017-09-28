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

public class AnnotationsBeanReader extends BeanReader {

    private String currID; //The bean ID
    private Stereotype stereotype;

    public AnnotationsBeanReader(BeanFactory beanFactory) {
        super(beanFactory);
    }

    public AnnotationsBeanReader(BeanCreator beanCreator) {
        super(beanCreator);
    }

    /**
     * Receives the name of a class and creates the corresponding Class object,
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

        this.readBeanProperties(reflectClass);
        this.readBeanConstructor(reflectClass);
        this.readBeanSetter(reflectClass);
        this.beanCreator.addBeanToContainer();
    }

    /**
     * Receives the class and starts to read the annotations, if any.
     * @param beanClass
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
            scope = ((Scope)(beanClass.getAnnotation(Scope.class))).value();
        }

        //The default lazyGen is false
        boolean lazyGeneration = false;
        if(beanClass.isAnnotationPresent(Lazy.class)){
            lazyGeneration = true;
        }

        //Searches for fields with autowiring
        this.searchForAutowiring(beanClass);

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
        this.beanCreator.createBean(this.currID, beanClass.getName(), scope, initMethod, destroyMethod,  lazyGeneration, AutowireEnum.none);
    }

    private void searchForAutowiring(Class beanClass) {

        Field[] classFields = beanClass.getDeclaredFields();

        for (Field classField : classFields) {

            if (classField.isAnnotationPresent(Autowire.class)) {

                if (!classField.isAnnotationPresent(Qualifier.class)) {

                }

            }

        }

    }

    /**
     * Reads the annotations of a constructor, if any.
     * @param beanClass
     */
    private void readBeanConstructor (Class beanClass) {
        int matchedConstructorCounter = 0;
        for (Constructor constructor : beanClass.getDeclaredConstructors()) {

            //If there is @Constructor
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

                            //Check if there is value or ref
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
