package com.ci1330.ecci.ucr.ac.cr.bean;

import com.ci1330.ecci.ucr.ac.cr.exception.BeanConstructorConflictException;
import com.ci1330.ecci.ucr.ac.cr.exception.BeanTypeConflictException;
import com.ci1330.ecci.ucr.ac.cr.exception.IdNotFoundException;
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
        Class currInstanceClass = bean.getBeanClass();
        BeanFactory beanFactory = bean.getBeanFactory();
        List<BeanAttribute> registeredAttributes = bean.getBeanAttributeList();

        Method currAttributeSetter;
        String currAttributeName;
        Class currAttributeType;
       // System.out.println("autowireByName de: " +  bean.getId());
       // System.out.println("obteniendo lista de fields de: " + currInstanceClass.toString() + " tamano: " + currInstanceClass.getFields().length);
        for (Field currAttribute: currInstanceClass.getFields()) {
            currAttributeName = currAttribute.getName();
            currAttributeType = currAttribute.getType();

            //System.out.println("chequeando si atributo: " + currAttributeName + " existe en el mapa");
            if(beanFactory.findBean(currAttributeName) != null){
                currAttributeSetter = findSetter(currAttributeName, currAttributeType, bean);
               // System.out.println("chequeando si atributo: " + currAttributeName + " ya fue registrado");
                if (!attributeIsAlreadyRegistered(registeredAttributes, currAttributeName)) {
                    BeanAttribute beanAttribute = new BeanAttribute(currAttributeName, currAttributeType, beanFactory, null, currAttributeSetter);
                    bean.appendAttribute(beanAttribute);
                   // System.out.println("se agrego el atributo ya autowireado de: " + bean.getId() + " : " + beanAttribute.getBeanRef());
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
                System.out.println("buscando setter, num params: " + methodParameterTypes.length + "tipo de clase: " + methodParameterTypes[0]);
                System.out.println("attributeClass: " + attributeClass.toString());
                if (method.getParameterCount() == 1 && methodParameterTypes[0].equals(attributeClass)) {

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
                    BeanAttribute beanAttribute = new BeanAttribute(currAttributeName, currAttributeClass, beanFactory, null, currAttributeSetter);
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
            String[] parameterNames = null;
            Constructor matchedConstructor = null;
            boolean allParamsMatched, allParamsClassesMatched;
            Paranamer paranamer = new AdaptiveParanamer();

            List<BeanParameter> beanParameterList = new ArrayList<>();
            for (Constructor classConstructor : classConstructors) {
                if (classConstructor.getParameterCount() > 0) {
                    allParamsMatched = true;

                    parameterNames = paranamer.lookupParameterNames(classConstructor);
                    constuctorParameters = classConstructor.getParameters();

                    for (String  parameter : parameterNames) {
                        if (beanFactory.findBean(parameter) == null) {
                            allParamsMatched = false;
                            break;
                        }
                    }

                    if (allParamsMatched) {
                        if (matchedConstructor == null) {
                            allParamsClassesMatched = checkParametersTypes(bean, constuctorParameters, parameterNames, beanParameterList);

                            if (allParamsClassesMatched) {
                                matchedConstructor = classConstructor;
                            } else {
                                try {
                                    throw new BeanConstructorConflictException("Bean creation error: parameter types mismatch for autowiring by constructor. Bean: " + bean.getId());
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
            }

            if (matchedConstructor != null) {
                BeanConstructor beanConstructor = new BeanConstructor(matchedConstructor);
                beanConstructor.setBeanParameterList(beanParameterList);
                bean.setBeanConstructor(beanConstructor);
            }
        }

    }
    private static boolean checkParametersTypes(Bean bean, Parameter[] constuctorParameters, String[] constructorParameterNames, List<BeanParameter> beanParameterList) {
        boolean allParamsClassesMatched = true;
        int parameterIndex = 0;
        BeanFactory beanFactory = bean.getBeanFactory();

        Class currBeanClass;

        for (Parameter constructorParameter : constuctorParameters) {
            currBeanClass = beanFactory.getBean(constructorParameterNames[parameterIndex]).getClass();
            if ( currBeanClass == constructorParameter.getType()) {
                beanParameterList.add(new BeanParameter(constructorParameterNames[parameterIndex], currBeanClass, beanFactory, null, parameterIndex, constructorParameter.getType().toString()));
            } else {
                allParamsClassesMatched = false;
                break;
            }


            parameterIndex++;
        }

        return allParamsClassesMatched;
    }
}
