package com.ci1330.ecci.ucr.ac.cr.readers;

import com.ci1330.ecci.ucr.ac.cr.bean.Bean;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanCreator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
        if(reflectClass.isAnnotationPresent(com.ci1330.ecci.ucr.ac.cr.readers.Bean.class)){
                this.readBeanProperties(reflectClass);
                this.readBeanSetter(reflectClass);
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
        Annotation beanAnnotation = beanClass.getAnnotation(com.ci1330.ecci.ucr.ac.cr.readers.Bean.class); //Obtengo el beanAnnotation
        com.ci1330.ecci.ucr.ac.cr.readers.Bean bean =(com.ci1330.ecci.ucr.ac.cr.readers.Bean)beanAnnotation; //Lo casteo
        String id = bean.id(); //Obtengo el id
        com.ci1330.ecci.ucr.ac.cr.bean.Scope scope = com.ci1330.ecci.ucr.ac.cr.bean.Scope.Singleton; //declaro y defino el scope
        if(beanClass.isAnnotationPresent(Scope.class)){ //si hay lo cambio
            scope = ((Scope)(beanClass.getAnnotation(Scope.class))).value(); //obtengo la anotation, la casteo y obtengo el value
        }
        boolean lazyGeneration = false;
        if(beanClass.isAnnotationPresent(Lazy.class)){
            lazyGeneration = true;
        }
        String autowire = "none";
        if(beanClass.isAnnotationPresent(Autowire.class)){
            autowire = ((Autowire)(beanClass.getAnnotation(Autowire.class))).value();
            if(!(autowire.equals("byName")) && !(autowire.equals("byType"))){ //Si no es byName o byType
                System.out.println("El Autowire no fue reconocido, está mal escrito");
                System.exit(1);
            }
        }

        //Busca los metodos
        String initMethod = "";
        String destroyMethod = "";
        int countInit = 0;
        int countDestroy = 0;
        for(Method method : beanClass.getDeclaredMethods()){
            if(method.isAnnotationPresent(Init.class)){
                initMethod = method.getName();
                ++countInit;
                if(countInit>1){
                    System.out.println("El Init no fue reconocido, posee más de una definición");
                    System.exit(1);
                }
            }
            if(method.isAnnotationPresent(Destroy.class)){
                destroyMethod = method.getName();
                ++countDestroy;
                if(countDestroy>1){
                    System.out.println("El Destroy no fue reconocido, posee más de una definición");
                    System.exit(1);
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
     * Reads the annotations of a specific method, if any.
     * @param beanClass
     */
    private void readBeanSetter (Class beanClass) {
        for(Field field : beanClass.getDeclaredFields()){
            System.out.println(field.getName());
            if(field.isAnnotationPresent(Attribute.class)){
                Object value = ((Attribute)(beanClass.getAnnotation(Attribute.class))).value(); //Obtengo, casteo y obtengo
                String ref = ((Attribute)(beanClass.getAnnotation(Attribute.class))).ref();
                if((ref.equals("") && !(value.equals(""))) || (!(ref.equals("")) && value.equals(""))){ //Si solo uno esta
                    this.beanCreator.registerSetter(field.getName(), value, ref);
                }
                else{
                    System.out.println("El Attribute no fue reconocido, debe haber solo uno, no ambos o ninguno");
                    System.exit(1);
                }
            }
        }
    }

    /**
     * Reads the annotations of a constructor, if any.
     * @param beanConsMethod
     */
    private void readBeanConstructor (Method beanConsMethod) {

    }

}
