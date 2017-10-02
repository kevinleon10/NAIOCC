package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.*;
import com.ci1330.ecci.ucr.ac.cr.exception.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.ci1330.ecci.ucr.ac.cr.bean.Bean;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 *
 * Module in charge of receiving each bean's metadata
 * from the reader and creating the bean, with all
 * the properties it needs for it to be instantiated
 * later.
 */
public class BeanCreator {

    // Classes needed to create the bean
    private Bean bean;
    private BeanFactory beanFactory;
    private BeanAttribute attributeClass;
    private BeanConstructor beanConstructorTemp;

    /**
     * Constructor of the class which receives the beanFactory and
     * assigns it for later use.
     * @param beanFactory the factory to add beans
     */
    public BeanCreator(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * Method which receives the basic IoC properties for the bean
     * and creates it.
     * @param id the bean's ID
     * @param beanClass the bean's class
     * @param scope the bean's scope
     * @param initMethodName the bean's init method name
     * @param destroyMethodName the bean's destroy method name
     * @param lazyGen the bean's lazy generation value
     * @param autowireEnum the bean's autowire mode
     */
    public void createBean(String id, String beanClass, Scope scope, String initMethodName, String destroyMethodName, boolean lazyGen, AutowireEnum autowireEnum) {
        try {
            if (this.beanFactory.containsBean(id)) {
                throw new RepeatedIdException("Creation error: Bean id " + id + " is repeated.");
            }
        } catch (RepeatedIdException r) {
            r.printStackTrace();
            System.exit(1);
        }

        bean = new Bean(this.beanFactory);
        bean.setId(id);
        try {
            bean.setBeanClass(Class.forName(beanClass));        // Sets the beans type
        } catch (ClassNotFoundException e) {
            System.err.println("Creation error: bean class not found for bean: " + id + ".");
            e.printStackTrace();
            System.exit(1);
        }
        bean.setBeanScope(scope);
        Method initMethod = null;
        Method destroyMethod = null;
        Method[] beanMethods = this.bean.getBeanClass().getDeclaredMethods();
        for (Method method : beanMethods) {
            if(Modifier.isPrivate(method.getModifiers())){
                method.setAccessible(true);
            }
            if (initMethodName != null && method.getName().contains(initMethodName)) {      //Finds the initialization and destruction methods for the bean
                if (method.getParameterCount() == 0) {
                    initMethod = method;
                }
            }
            if (destroyMethodName != null && method.getName().contains(destroyMethodName)) {
                if (method.getParameterCount() == 0) {
                    destroyMethod = method;
                }
            }
        }
        bean.setInitMethod(initMethod);
        bean.setDestroyMethod(destroyMethod);
        bean.setLazyGen(lazyGen);
        bean.setAutowireEnum(autowireEnum);
        this.beanConstructorTemp = new BeanConstructor(null);   // Creates a temporary constructor to receive the parameters of the bean
    }

    /**
     * Method that returns an object after
     * casting the string value to its real type.
     * @param stringValue a string that contains the value
     * @return object with respective type
     */
    private Object obtainValueType(String stringValue) {
        boolean parsed = false;
        Object value = null;
        try {
            value = Integer.valueOf(stringValue);       // It tries to cast the string to the stated types and if not proceeds to the next one
            parsed = true;
        } catch (NumberFormatException e) {
            //No es un int.
        }
        if (!parsed) {
            try {
                value = Byte.valueOf(stringValue);
                parsed = true;
            } catch (NumberFormatException e) {
                //No es un byte.
            }
        }
        if (!parsed) {
            try {
                value = Short.valueOf(stringValue);
                parsed = true;
            } catch (NumberFormatException e) {
                //No es un byte.
            }
        }
        if (!parsed) {
            try {
                value = Long.valueOf(stringValue);
                parsed = true;
            } catch (NumberFormatException e) {
                //No es un byte.
            }
        }
        if (!parsed) {
            try {
                value = Float.valueOf(stringValue);
                parsed = true;
            } catch (NumberFormatException e) {
                //No es un byte.
            }
        }
        if (!parsed) {
            try {
                value = Double.valueOf(stringValue);
                parsed = true;
            } catch (NumberFormatException e) {
                //No es un byte.
            }
        }
        if (!parsed) {
            if ((stringValue.toLowerCase()).equals("true")) {
                value = true;
                parsed = true;
            } else if ((stringValue.toLowerCase()).equals("false")) {
                value = false;
                parsed = true;
            }
        }
        if (stringValue.length() == 1 && !parsed) {
            try {
                value = stringValue.charAt(0);
                parsed = true;
            } catch (Exception e) {
                //No es un char.
            }
        }
        if (!parsed) {
            value = stringValue;
        }
        return value;
    }

    /**
     *
     * Method to register an attribute of the bean and find
     * its setter method to be used later when injecting
     * the bean's dependencies
     * @param attributeName the name of the attribute to register
     * @param stringValue a string with the attribute's value
     * @param beanRef a string with the attribute's bean reference
     * @param atomic_autowire the atomic autowiring mode
     */
    public void registerSetter(String attributeName, String stringValue, String beanRef, AutowireEnum atomic_autowire){
        try {
            if (this.beanFactory.containsBean(beanRef)) {
                throw new RepeatedIdException("Creation error: Bean attribute with reference to: " + beanRef + " is repeated.");
            }
        } catch (RepeatedIdException r) {
            r.printStackTrace();
            System.exit(1);
        }

        Object value = null;
        if(stringValue != null){
            value = this.obtainValueType(stringValue);
        }

        Method setterMethod = null;
        Method[] beanMethods = this.bean.getBeanClass().getDeclaredMethods();
        Class beanRefType = null;

        for(Method method: beanMethods){
            if(Modifier.isPrivate(method.getModifiers())){
                method.setAccessible(true);
            }

            // Checks if the method is the respective setter for this attribute
            if(method.getName().startsWith("set") && method.getName().toLowerCase().contains(attributeName.toLowerCase())){
                if(method.getParameterCount() == 1){
                    setterMethod = method;
                }
            }
        }

        if(setterMethod == null){
            try {
                throw new SetterMethodNotFoundException("Creation error: Bean attribute's setter method not found for attribute: " + attributeName + ".");
            } catch (SetterMethodNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        //If the value is null, the user is using beans, so search for the type that the attribute should have
        if(value == null) {
            Field[] beanFields = this.bean.getBeanClass().getDeclaredFields();
            for(Field field: beanFields){
                if(Modifier.isPrivate(field.getModifiers())){
                    field.setAccessible(true);
                }
                if(field.getName().equals(attributeName)) {
                    beanRefType = field.getType();
                }
            }
        }

        //If the user specified autowire byName at atomic level, the beanRef is the same as the attributeName
        if (beanRef == null && atomic_autowire == AutowireEnum.byName) {
            beanRef = attributeName;
        }
        BeanAttribute beanAttribute = new BeanAttribute(beanRef, beanRefType, this.beanFactory, value, atomic_autowire, setterMethod);
        bean.appendAttribute(beanAttribute);
    }

    /**
     * Method which registers a parameter of the bean's constructor
     * @param paramType the name of the parameter's type
     * @param stringValue a string with the parameter's value
     * @param beanRef a string with the parameter's bean reference
     * @param atomic_autowire the atomic autowiring mode
     */
    public void registerConstructorParameter(String paramType, int index, String stringValue, String beanRef, AutowireEnum atomic_autowire){
        Object value = null;
        if(stringValue != null){
            value = this.obtainValueType(stringValue);
        }
        if(value == null && beanRef == null && paramType == null){
            try {
                throw new InvalidPropertyException("Bean creation error: parameter's type, reference or value is invalid for a declared bean parameter.");
            } catch (InvalidPropertyException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        Class beanRefClass = null;

        //If the value is null, the user is using beans, so search for the type that the parameter should have
        //But because this is a constructor parameter, only search for it if we have at least the type
        if (value == null && paramType != null) {
            try {
                beanRefClass = Class.forName(paramType);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        BeanParameter beanConstructorParam = new BeanParameter(beanRef, beanRefClass, this.beanFactory, value, atomic_autowire, index, paramType);
        this.beanConstructorTemp.append(beanConstructorParam);

    }

    /**
     * There is a special case in which an AtomicAutowire annotation is found above a constructor
     * In this case, the constructor is already known, but the parameters need to be set later.
     * So the Reader sends the constructor explicitly, and it is added to the current bean.
     *
     * The method addBeanToContainer won't interfere in this definition, because if the user didn't
     * specify another constructor elsewhere, the beanConstructorTemp won't be added to the current bean,
     * leaving the explicit definition untouched.
     */
    public void explicitConstructorDefinition (Constructor constructorMethod) {
        this.bean.setBeanConstructor(new BeanConstructor(constructorMethod));
    }

    /**
     * Adds the bean to the container and resets all its attributes
     * for the creator to be ready to read another bean's data
     */
    public void addBeanToContainer(){
        //If there were no parameters specified for the constructor, it is assumed the user didn't
        //indicate to use constructor injection
        if (this.beanConstructorTemp.getBeanParameterList().size() > 0) {
            this.bean.setBeanConstructor(this.beanConstructorTemp);
        }
        this.beanFactory.addBean(this.bean);
        bean = null;
        attributeClass = null;
        beanConstructorTemp = null;
    }

    //----------------------------------------------------------------
    // Standard Setters and Getters section
    //----------------------------------------------------------------

    public Bean getBean() {
        return bean;
    }

    public void setBean(Bean bean) {
        this.bean = bean;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public BeanAttribute getAttributeClass() {
        return attributeClass;
    }

    public void setAttributeClass(BeanAttribute attributeClass) {
        this.attributeClass = attributeClass;
    }

}