package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.*;
import com.ci1330.ecci.ucr.ac.cr.exception.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josue Leon on 13/09/2017
 */
public class BeanCreator {

    private Bean bean;
    private BeanFactory beanFactory;
    private BeanAttribute attributeClass;
    private BeanConstructor constructorClass;

    /**
     *
     * @param beanFactory
     */
    public BeanCreator(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     *
     * @param id
     * @param beanClass
     * @param scope
     * @param initMethodName
     * @param destroyMethodName
     * @param lazyGen
     * @param autowireEnum
     * @throws RepeatedIdException
     */
    public void createBean(String id, String beanClass, Scope scope, String initMethodName, String destroyMethodName, boolean lazyGen, AutowireEnum autowireEnum){
        try {
            if(this.beanFactory.containsBean(id)){
                throw new RepeatedIdException("Creation error: Bean id " + id + " is repeated.");
            }
            bean = new Bean(this.beanFactory);
            bean.setId(id);
            try {
                bean.setBeanClass(Class.forName(beanClass));
            } catch (ClassNotFoundException e) {
                System.out.println("Creation error: bean class not found for bean: " + id + ".");
                e.printStackTrace();
                System.exit(1);
            }
            bean.setBeanScope(scope);
            Method initMethod = null;
            Method destroyMethod = null;
            Method[] beanMethods = this.bean.getBeanClass().getMethods();
            for(Method method: beanMethods){
                if(initMethodName != null && method.getName().contains(initMethodName)){
                    if(method.getParameterCount() == 0 ){
                        initMethod = method;
                    }
                }
                if(destroyMethodName != null && method.getName().contains(destroyMethodName)){
                    if(method.getParameterCount() == 0 ){
                        destroyMethod = method;
                    }
                }
            }
            bean.setInitMethod(initMethod);
            bean.setDestroyMethod(destroyMethod);
            bean.setLazyGen(lazyGen);
            bean.setAutowireEnum(autowireEnum);
            bean.setBeanConstructor(new BeanConstructor(null));
        }catch (RepeatedIdException r){
            r.printStackTrace();
            System.exit(1);
        }
    }

    private Object obtainValueType(String stringValue) {
        boolean parsed = false;
        Object value = null;
        System.out.println(stringValue);
        try {
            value = Integer.valueOf(stringValue);
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
     * @param attributeName
     * @param stringValue
     * @param beanRef
     */
    public void registerSetter(String attributeName, String stringValue, String beanRef){
        Object value = null;
        if(stringValue != null){
            value = this.obtainValueType(stringValue);
        }

        Method setterMethod = null;
        Method[] beanMethods = this.bean.getBeanClass().getMethods();
        Class beanRefType = null;

        for(Method method: beanMethods){
            //System.out.println( method.getName().toLowerCase() + " contiene:??? " + attributeName.toLowerCase());
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

        //If the value is null, the user is using beans, so search for the type that the bean should have
        if(value == null) {
            Field[] beanFields = this.bean.getBeanClass().getDeclaredFields();
            for(Field field: beanFields){
                if(field.getName().equals(attributeName)) {
                    beanRefType = field.getType();
                }
            }
        }

        BeanAttribute beanAttribute = new BeanAttribute(beanRef, beanRefType, this.beanFactory, value, setterMethod);
        bean.appendAttribute(beanAttribute);
    }

    /**
     *
     * @param paramType
     * @param index
     * @param stringValue
     * @param beanRef
     */
    public void registerConstructorParameter(String paramType, int index, String stringValue, String beanRef){
        Object value = null;
        if(stringValue != null){
            value = this.obtainValueType(stringValue);
        }
        if(value == null && beanRef == null){
            try {
                throw new InvalidPropertyException("Bean creation error: parameter's type or reference is invalid for: " + paramType + ".");
            } catch (InvalidPropertyException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        Class beanRefClass = null;

        //If the value is null, the user is using beans, so search for the type that the bean should have
        //But because this is a constructor parameter, only search for it if we have at least the type
        if (value == null && paramType != null) {
            try {
                beanRefClass = Class.forName(paramType);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        BeanParameter beanConstructorParam = new BeanParameter(beanRef, beanRefClass, this.beanFactory, value, index, paramType);
        bean.getBeanConstructor().append(beanConstructorParam);

    }

    /**
     *
     */
    public void addBeanToContainer(){
        this.beanFactory.addBean(this.bean);
        bean = null;
        attributeClass = null;
        constructorClass = null;
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

    public BeanConstructor getConstructorClass() {
        return constructorClass;
    }

    public void setConstructorClass(BeanConstructor constructorClass) {
        this.constructorClass = constructorClass;
    }

}