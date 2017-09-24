package com.ci1330.ecci.ucr.ac.cr.bean;

import com.ci1330.ecci.ucr.ac.cr.exception.BeanConstructorConflictException;
import com.ci1330.ecci.ucr.ac.cr.exception.BeanTypeConflictException;
import com.ci1330.ecci.ucr.ac.cr.exception.IdNotFoundException;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class BeanAutowireModule {

    public static void autowireBean (Bean bean) {
        switch (bean.getAutowireEnum()) {
            case byName:
                autowireByName(bean);
                break;
            case byType:
                autowireByType(bean);
                break;
            case constructor:
                autowireConstructor(bean);
                break;
            case none:
                break;
            default:
                System.err.println("Autowire error: Unexpected value.");
                System.exit(1);
        }
    }

    private static void autowireByName (Bean bean) {
        Class currInstanceClass = bean.getClass();
        BeanFactory beanFactory = bean.getBeanFactory();
        List<BeanAttribute> registeredAttributes = bean.getBeanAttributeList();

        Method currAttributeSetter;
        String currAttributeName;

        for (Field currAttribute: currInstanceClass.getFields()) {
            currAttributeName = currAttribute.getName();

            if(beanFactory.findBean(currAttributeName) != null){

                currAttributeSetter = findSetter(currAttributeName, currAttribute.getClass(), bean);

                if (!attributeIsAlreadyRegistered(registeredAttributes, currAttributeName)) {
                    BeanAttribute beanAttribute = new BeanAttribute(currAttributeName, beanFactory, null, currAttributeSetter);
                    bean.appendAttribute(beanAttribute);
                }

            }
        }
    }

    private static Method findSetter (String attributeName, Class attributeClass, Bean bean) {
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

    private static boolean attributeIsAlreadyRegistered (List<BeanAttribute> registeredAttributes, String beanRef) {
        for (BeanAttribute registeredAttribute : registeredAttributes) {
            if (registeredAttribute.getBeanRef().equals(beanRef)) {
                return true;
            }
        }
        return false;
    }

    private static void autowireByType (Bean bean) {
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
                currAttributeSetter = findSetter(currAttributeName, currAttribute.getClass(), bean);

                if (!attributeIsAlreadyRegistered(registeredAttributes, currAttributeName)) {
                    BeanAttribute beanAttribute = new BeanAttribute(currAttributeName, beanFactory, null, currAttributeSetter);
                    bean.appendAttribute(beanAttribute);
                }

            }
        }
    }

    private static void autowireConstructor (Bean bean) {
        if (bean.getBeanConstructor() == null) { //If the user already defined the constructor explicitly this process is omitted
            Constructor[] classConstructors = bean.getBeanClass().getDeclaredConstructors();
            BeanFactory beanFactory = bean.getBeanFactory();

            Parameter[] constuctorParameters = null;
            Constructor matchedConstructor = null;
            boolean allParamsMatched, allParamsClassesMatched;

            List<BeanParameter> beanParameterList = new ArrayList<>();
            for (Constructor classConstructor : classConstructors) {
                constuctorParameters = classConstructor.getParameters();
                allParamsMatched = true;

                for (Parameter constructorParameter : constuctorParameters) {
                    if (beanFactory.findBean(constructorParameter.getName()) == null) {
                        allParamsMatched = false;
                        break;
                    }
                }

                if (allParamsMatched) {

                    if (matchedConstructor == null) {
                        allParamsClassesMatched = checkParametersTypes(bean, constuctorParameters, beanParameterList);

                        if (allParamsClassesMatched) {
                            matchedConstructor = classConstructor;
                        } else {
                            try {
                                throw new BeanConstructorConflictException("Bean creation error: there are multiple constructors that match in their parameters types, in autowiring by constructor. Bean: " + bean.getId());
                            } catch (BeanConstructorConflictException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }
                        }

                    } else {
                        try {
                            throw new BeanConstructorConflictException("Bean creation error: there are multiple constructors that match in their parameters names, in autowiring by constructor. Bean: " + bean.getId());
                        } catch (BeanConstructorConflictException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                    }

                }

            }

            if (matchedConstructor != null) {
                BeanConstructor beanConstructor = new BeanConstructor(matchedConstructor);
                beanConstructor.setBeanParameterList(beanParameterList);
                bean.setBeanConstructor(beanConstructor);
            }
        }

    }
    private static boolean checkParametersTypes(Bean bean, Parameter[] constuctorParameters, List<BeanParameter> beanParameterList) {
        boolean allParamsClassesMatched = true;
        int parameterIndex = 0;
        BeanFactory beanFactory = bean.getBeanFactory();

        for (Parameter constructorParameter : constuctorParameters) {
            if (beanFactory.getBean(constructorParameter.getName()).getClass() == constructorParameter.getClass()) {
                beanParameterList.add(new BeanParameter(constructorParameter.getName(), beanFactory, null, parameterIndex, constructorParameter.getClass().toString()));
            } else {
                allParamsClassesMatched = false;
                break;
            }


            parameterIndex++;
        }

        return allParamsClassesMatched;
    }
}
