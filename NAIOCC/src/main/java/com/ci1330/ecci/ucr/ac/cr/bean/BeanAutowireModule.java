package com.ci1330.ecci.ucr.ac.cr.bean;

import com.ci1330.ecci.ucr.ac.cr.exception.BeanAutowireException;
import com.ci1330.ecci.ucr.ac.cr.exception.BeanTypeConflictException;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class BeanAutowireModule {

    static void autowireBean (Bean bean) {
        switch (bean.getAutowireEnum()) {
            case byName:
                autowireByName(bean);
                break;
            case byType:
                autowireByType(bean);
                break;
            case constructor:
                System.out.println("-----------------------------------------------");
                autowireConstructor(bean);
                break;
            case none:
                break;
            default:
                try {
                    throw new BeanAutowireException("Autowire Module Error: Unexpected value recieved while trying to autowire the bean " + bean.getId());
                } catch (BeanAutowireException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
        }
    }

    private static void autowireByName (Bean bean) {
        Class currInstanceClass = bean.getBeanClass();
        BeanFactory beanFactory = bean.getBeanFactory();
        List<BeanAttribute> registeredAttributes = bean.getBeanAttributeList();

        Method currAttributeSetter;
        String currAttributeName;
        Class currAttributeType;
        for (Field currAttribute: currInstanceClass.getFields()) {
            currAttributeName = currAttribute.getName();
            currAttributeType = currAttribute.getType();

            if(beanFactory.findBean(currAttributeName) != null){
                currAttributeSetter = findSetter(currAttributeName, currAttributeType, bean);
                if (!attributeIsAlreadyRegistered(registeredAttributes, currAttributeName)) {
                    BeanAttribute beanAttribute = new BeanAttribute(currAttributeName, currAttributeType, beanFactory, null, AutowireEnum.none, currAttributeSetter);
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
            if (method.getName().startsWith("set") && method.getName().toLowerCase().contains(attributeName.toLowerCase())) {

                methodParameterTypes = method.getParameterTypes();
                if (method.getParameterCount() == 1 && methodParameterTypes[0].equals(attributeClass)) {
                    return method;
                }

            }

        }

        try {
            throw new BeanAutowireException("Autowire Module Error: The field " + attributeName +" of " + bean.getBeanClass().toString() +
                    " matches with autowiring, but no setter method was found for it.");
        } catch (BeanAutowireException e) {
            e.printStackTrace();
            System.exit(1);
        }
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
        Class currInstanceClass = bean.getBeanClass();
        BeanFactory beanFactory = bean.getBeanFactory();
        List<BeanAttribute> registeredAttributes = bean.getBeanAttributeList();

        Method currAttributeSetter;
        String currAttributeName;
        Class currAttributeClass;

        Bean typeLikeBean = null;

        for (Field currAttribute: currInstanceClass.getFields()) {

            currAttributeClass = currAttribute.getType();
            try {
                typeLikeBean = beanFactory.findBean(currAttributeClass);
            } catch (BeanTypeConflictException e) {
                e.printStackTrace();
            }

            if(typeLikeBean != null){

                currAttributeName = typeLikeBean.getId();
                currAttributeSetter = findSetter(currAttributeName, currAttribute.getType(), bean);

                if (!attributeIsAlreadyRegistered(registeredAttributes, currAttributeName)) {
                    BeanAttribute beanAttribute = new BeanAttribute(currAttributeName, currAttributeClass, beanFactory, null, AutowireEnum.none, currAttributeSetter);
                    bean.appendAttribute(beanAttribute);
                }

            }
        }
    }

    private static void autowireConstructor (Bean bean) {
        System.out.println("se metio a autowire by constructor con bean: " + bean.getId());
        if (bean.getBeanConstructor() == null) { //If the user already defined the constructor explicitly this process is omitted
            System.out.println("se metio en el if de  autowire by constructor con bean: " + bean.getId());
            Constructor[] classConstructors = bean.getBeanClass().getDeclaredConstructors();
            BeanFactory beanFactory = bean.getBeanFactory();

            Parameter[] constructorParameters;
            String[] parameterNames;
            Constructor matchedConstructor = null;
            boolean allParamsMatched, allParamsClassesMatched;
            Paranamer paranamer = new AdaptiveParanamer();

            List<BeanParameter> beanParameterList = new ArrayList<>();
            for (Constructor classConstructor : classConstructors) {
                System.out.println("----------------------------------------------------");
                if (classConstructor.getParameterCount() > 0) {
                    allParamsMatched = true;

                    parameterNames = paranamer.lookupParameterNames(classConstructor);
                    constructorParameters = classConstructor.getParameters();
                    System.out.println("size de paranamers: " + parameterNames.length);
                    for (String  parameter : parameterNames) {
                        if (beanFactory.findBean(parameter) == null) {
                            allParamsMatched = false;
                            break;
                        }
                    }

                    if (allParamsMatched) {
                        if (matchedConstructor == null) {
                            System.out.println("-------- se llamara a checkparametertypes");
                            allParamsClassesMatched = checkParametersTypes(beanFactory, constructorParameters, parameterNames, beanParameterList);

                            if (allParamsClassesMatched) {
                                System.out.println("--------  MATCHEOooooooooooooooooooo");
                                matchedConstructor = classConstructor;
                            } else {
                                try {
                                    throw new BeanAutowireException("Autowire Module Error: parameter types mismatch for autowiring by constructor. Bean: " + bean.getId());
                                } catch (BeanAutowireException e) {
                                    e.printStackTrace();
                                    System.exit(1);
                                }
                            }

                        } else {
                            try {
                                throw new BeanAutowireException("Autowire Module Error: there are multiple constructors that match in their parameters names, in autowiring by constructor. Bean: " + bean.getId());
                            } catch (BeanAutowireException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }
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

    public static void autowireSingleConstructor (BeanConstructor beanConstructor, BeanFactory beanFactory, String beanId) {
        Constructor classConstructor = beanConstructor.getConstructorMethod();

        if (classConstructor.getParameterCount() > 0) {
            Boolean allParamsMatched = true;
            Paranamer paranamer = new AdaptiveParanamer();

            String[] parameterNames = paranamer.lookupParameterNames(classConstructor);
            Parameter[] constructorParameters = classConstructor.getParameters();

            List<BeanParameter> beanParameterList = beanConstructor.getBeanParameterList();

            for (String parameter : parameterNames) {
                if (beanFactory.findBean(parameter) == null) {
                    allParamsMatched = false;
                    break;
                }
            }

            if (!allParamsMatched) {
                try {
                    throw new BeanAutowireException("Autowire Module Error: One or more constructor parameters names does not match with a bean, in autowiring a single constructor. For bean: " + beanId);
                } catch (BeanAutowireException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            } else {
                Boolean allParamsClassesMatched = checkParametersTypes(beanFactory, constructorParameters, parameterNames, beanParameterList);

                if (!allParamsClassesMatched) {
                    try {
                        throw new BeanAutowireException("Autowire Module Error: parameter types mismatch for autowiring by constructor. For bean: " + beanId);
                    } catch (BeanAutowireException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }

        }
    }

    private static boolean checkParametersTypes(BeanFactory beanFactory, Parameter[] constuctorParameters, String[] constructorParameterNames, List<BeanParameter> beanParameterList) {
        boolean allParamsClassesMatched = true;
        int parameterIndex = 0;

        Class currBeanClass;

        for (Parameter constructorParameter : constuctorParameters) {
            currBeanClass = beanFactory.findBean(constructorParameterNames[parameterIndex]).getBeanClass();
            if ( currBeanClass == constructorParameter.getType()) {
                beanParameterList.add(new BeanParameter(constructorParameterNames[parameterIndex], currBeanClass, beanFactory, null, AutowireEnum.none, parameterIndex, constructorParameter.getType().toString()));
            } else {
                allParamsClassesMatched = false;
                break;
            }


            parameterIndex++;
        }

        return allParamsClassesMatched;
    }
}
