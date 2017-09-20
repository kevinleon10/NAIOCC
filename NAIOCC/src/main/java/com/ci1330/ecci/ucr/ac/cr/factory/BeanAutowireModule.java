package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.Bean;
import com.ci1330.ecci.ucr.ac.cr.bean.BeanAttribute;
import com.ci1330.ecci.ucr.ac.cr.exception.BeanTypeConflictException;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class BeanAutowireModule {

    public void autowireBean (Bean bean) {
        switch (bean.getAutowireEnum()) {
            case byName:
                this.autowireByName(bean);
                break;
            case byType:
                this.autowireByType(bean);
                break;
            case constructor:
                this.autowireConstructor(bean);
                break;
            default:
                System.err.println("Autowire error: Unexpected value.");
                System.exit(1);
        }
    }

    private void autowireByName (Bean bean) {
        Class currInstanceClass = bean.getClass();
        BeanFactory beanFactory = bean.getBeanFactory();
        List<BeanAttribute> registeredAttributes = bean.getBeanAttributeList();

        Method currAttributeSetter;
        String currAttributeName;

        for (Field currAttribute: currInstanceClass.getFields()) {
            currAttributeName = currAttribute.getName();

            if(beanFactory.findBean(currAttributeName) != null){

                currAttributeSetter = this.findSetter(currAttributeName, currAttribute.getClass(), bean);

                if (!this.attributeIsAlreadyRegistered(registeredAttributes, currAttributeName)) {
                    BeanAttribute beanAttribute = new BeanAttribute(currAttributeName, beanFactory, null, currAttributeSetter);
                    bean.appendAttribute(beanAttribute);
                }

            }
        }
    }

    private Method findSetter (String attributeName, Class attributeClass, Bean bean) {
        Method[] beanMethods;
        Class[] methodParameterTypes;

        beanMethods = bean.getBeanClass().getMethods();

        for (Method method : beanMethods) {

            if (method.getName().startsWith("set") && method.getName().contains(attributeName)) {

                methodParameterTypes = method.getParameterTypes();
                if (method.getParameterCount() == 1 && methodParameterTypes.getClass().equals(attributeClass)) {

                    return method;

                }
            }

        }

        System.err.println("Autowire Error: The field " + attributeName +" of " + bean.getBeanClass().toString() +
                " matches with autowiring, but no setter method was found for it.");
        System.exit(1);
        return null; //Keep the compiler happy
    }

    private boolean attributeIsAlreadyRegistered (List<BeanAttribute> registeredAttributes, String beanRef) {
        for (BeanAttribute registeredAttribute : registeredAttributes) {
            if (registeredAttribute.getBeanRef().equals(beanRef)) {
                return true;
            }
        }
        return false;
    }

    private void autowireByType (Bean bean) {
        Class currInstanceClass = bean.getClass();
        BeanFactory beanFactory = bean.getBeanFactory();
        List<BeanAttribute> registeredAttributes = bean.getBeanAttributeList();

        Method currAttributeSetter;
        String currAttributeName;
        Class currAttributeClass;
        Bean typeLikeBean = null;

        for (Field currAttribute: currInstanceClass.getFields()) {
            currAttributeClass = currAttribute.getClass();
            try {
                typeLikeBean = beanFactory.findBean(currAttributeClass);
            } catch (BeanTypeConflictException e) {
                e.printStackTrace();
            }

            if(typeLikeBean != null){

                currAttributeName = typeLikeBean.getId();
                currAttributeSetter = this.findSetter(currAttributeName, currAttribute.getClass(), bean);

                if (!this.attributeIsAlreadyRegistered(registeredAttributes, currAttributeName)) {
                    BeanAttribute beanAttribute = new BeanAttribute(currAttributeName, beanFactory, null, currAttributeSetter);
                    bean.appendAttribute(beanAttribute);
                }

            }
        }
    }

    private void autowireConstructor (Bean bean) {

    }

}
