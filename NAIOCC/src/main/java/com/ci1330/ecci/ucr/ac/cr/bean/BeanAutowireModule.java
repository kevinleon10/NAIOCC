package com.ci1330.ecci.ucr.ac.cr.bean;

import com.ci1330.ecci.ucr.ac.cr.exception.BeanAutowireException;
import com.ci1330.ecci.ucr.ac.cr.exception.BeanTypeConflictException;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 *
 * The class is in charge of autowiring (at class level) a bean.
 * It also has the capability to autowire a single constructor.
 */
public class BeanAutowireModule {

    /**
     * Determines which type of autowiring needs to be done
     * @param bean the bean to autowire
     */
    static void autowireBean (Bean bean) {
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
                try {
                    throw new BeanAutowireException("Autowire Module Error: Unexpected value recieved while trying to autowire the bean " + bean.getId());
                } catch (BeanAutowireException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
        }
    }

    /**
     * Iterates all the fields of a class. For every field it searches that the field name matches a beanId
     * in the container, if found, creates a {@link BeanAttribute} for the field.
     * @param bean the bean to use
     */
    private static void autowireByName (Bean bean) {
        Class currInstanceClass = bean.getBeanClass();
        BeanFactory beanFactory = bean.getBeanFactory();
        List<BeanAttribute> registeredAttributes = bean.getBeanAttributeList();

        Method currAttributeSetter;
        String currAttributeName;
        Class currAttributeType;

        //For every field of the class
        for (Field currAttribute: currInstanceClass.getDeclaredFields()) {
            //If the field is private, make it accessible
            if(Modifier.isPrivate(currAttribute.getModifiers())){
                currAttribute.setAccessible(true);
            }

            currAttributeName = currAttribute.getName();
            currAttributeType = currAttribute.getType();

            if(beanFactory.findBean(currAttributeName) != null){
                currAttributeSetter = findSetter(currAttributeName, currAttributeType, bean);

                //If the attribute isn't already registered in the Bean (the user didn't overwrite the autowiring for the
                //attribute), put it in the bean.
                if (!attributeIsAlreadyRegistered(registeredAttributes, currAttributeName)) {
                    BeanAttribute beanAttribute = new BeanAttribute(currAttributeName, currAttributeType, beanFactory, null, AutowireEnum.none, currAttributeSetter);
                    bean.appendAttribute(beanAttribute);
                }
            }
        }
    }

    /**
     * Finds the setter method for an attribute
     * @param attributeName the name of the attribute used
     * @param attributeClass the type of the attribute
     * @param bean used to recover the class of the bean
     * @return the setter method
     */
    private static Method findSetter (String attributeName, Class attributeClass, Bean bean) {
        Method[] beanMethods;
        Class[] methodParameterTypes;

        //Search every method in the bean
        beanMethods = bean.getBeanClass().getDeclaredMethods();
        for (Method method : beanMethods) {
            //If private, make it accessible
            if(Modifier.isPrivate(method.getModifiers())){
                method.setAccessible(true);
            }

            //Check if it has set at the start and contains the name of the attribute
            if (method.getName().startsWith("set") && method.getName().toLowerCase().contains(attributeName.toLowerCase())) {

                methodParameterTypes = method.getParameterTypes();
                //Check the parameters are valid
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

    /**
     * Iterates the list of {@link BeanAttribute} and returns true if the bean reference is already found,
     * and false if not.
     * @param registeredAttributes The list of BeanAttributes
     * @param beanRef the bean reference to search
     * @return the result of the search
     */
    private static boolean attributeIsAlreadyRegistered (List<BeanAttribute> registeredAttributes, String beanRef) {
        for (BeanAttribute registeredAttribute : registeredAttributes) {
            if (registeredAttribute.getBeanRef().equals(beanRef)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Iterates every field in the bean class and tries to search for a bean that match in type with
     * the field. If there are multiple definitions of beans with that type, an exception is thrown.
     * @param bean the bean to autowire
     */
    private static void autowireByType (Bean bean) {

        Class currInstanceClass = bean.getBeanClass();
        BeanFactory beanFactory = bean.getBeanFactory();
        List<BeanAttribute> registeredAttributes = bean.getBeanAttributeList();

        Method currAttributeSetter;
        String currAttributeName;
        Class currAttributeClass;

        Bean typeLikeBean = null;

        //Iterates all the fields
        for (Field currAttribute: currInstanceClass.getDeclaredFields()) {
            //If the field is private, make it accesible
            if(Modifier.isPrivate(currAttribute.getModifiers())){
                currAttribute.setAccessible(true);
            }

            currAttributeClass = currAttribute.getType();

            //If there are multiple beans with that type, exit abnormally.
            try {
                typeLikeBean = beanFactory.findBean(currAttributeClass);
            } catch (BeanTypeConflictException e) {
                e.printStackTrace();
                System.exit(1);
            }

            //If the bean was found
            if(typeLikeBean != null){

                currAttributeName = currAttribute.getName();
                currAttributeSetter = findSetter(currAttributeName, currAttributeClass, bean);

                //And it wasn't already in the container, register it
                if (!attributeIsAlreadyRegistered(registeredAttributes, currAttributeName)) {
                    BeanAttribute beanAttribute = new BeanAttribute(typeLikeBean.getId(), currAttributeClass, beanFactory, null, AutowireEnum.none, currAttributeSetter);
                    bean.appendAttribute(beanAttribute);
                }

            }
        }
    }

    /**
     * Iterates all constructors. For every constructor, searches that its parameters' names, match with a bean's id.
     * If they all match, the constructor is selected. If there is more than one matched constructor, an exception is thrown.
     * @param bean the bean to autowire
     */
    private static void autowireConstructor (Bean bean) {
        if (bean.getBeanConstructor() == null) { //If the user already defined the constructor explicitly this process is omitted
            Constructor[] classConstructors = bean.getBeanClass().getDeclaredConstructors();
            BeanFactory beanFactory = bean.getBeanFactory();

            Parameter[] constructorParameters;
            String[] parameterNames;
            Constructor matchedConstructor = null;
            boolean allParamsMatched, allParamsClassesMatched;
            Paranamer paranamer = new AdaptiveParanamer(); //Utility to recover parameter names

            List<BeanParameter> beanParameterList = new ArrayList<>();
            for (Constructor classConstructor : classConstructors) {
                //If it has parameters
                if (classConstructor.getParameterCount() > 0) {
                    allParamsMatched = true;

                    parameterNames = paranamer.lookupParameterNames(classConstructor);
                    constructorParameters = classConstructor.getParameters();
                    //Look if the names match
                    for (String  parameter : parameterNames) {
                        if (beanFactory.findBean(parameter) == null) {
                            allParamsMatched = false;
                            break;
                        }
                    }

                    //If they all matched
                    if (allParamsMatched) {
                        //And the constructor didn't match already
                        if (matchedConstructor == null) {
                            //Check that the types also match
                            allParamsClassesMatched = checkParametersTypes(beanFactory, constructorParameters, parameterNames, beanParameterList);

                            if (allParamsClassesMatched) {
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
                            //If it did, exit abnormally
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

    /**
     * For a specific constructor searches that its parameters' names, match with a bean's id.
     * If they all match, the BeanParameters are created. If the parameters didn't match, exits abnormally.
     * @param beanConstructor the constructor to match
     * @param beanFactory the factory to use
     * @param beanId the id of the bean (used for throwing the error)
     */
    public static void autowireSingleConstructor (BeanConstructor beanConstructor, BeanFactory beanFactory, String beanId) {
        Constructor classConstructor = beanConstructor.getConstructorMethod();

        //If it has parameters
        if (classConstructor.getParameterCount() > 0) {
            Boolean allParamsMatched = true;
            Paranamer paranamer = new AdaptiveParanamer();

            String[] parameterNames = paranamer.lookupParameterNames(classConstructor);
            Parameter[] constructorParameters = classConstructor.getParameters();

            List<BeanParameter> beanParameterList = beanConstructor.getBeanParameterList();

            //Look if the names match
            for (String parameter : parameterNames) {
                if (beanFactory.findBean(parameter) == null) {
                    allParamsMatched = false;
                    break;
                }
            }

            //If they don't match, exit abnormally.
            if (!allParamsMatched) {
                try {
                    throw new BeanAutowireException("Autowire Module Error: One or more constructor parameters names does not match with a bean, in autowiring a single constructor. For bean: " + beanId);
                } catch (BeanAutowireException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            } else {
                Boolean allParamsClassesMatched = checkParametersTypes(beanFactory, constructorParameters, parameterNames, beanParameterList);

                //If the types didn't match, exit abnormally.
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

    /**
     * Checks that the types of the parameters, match the types of the beans.
     * @param beanFactory the factory to use
     * @param constructorParameters the array of parameters
     * @param constructorParameterNames the array of parameter names
     * @param beanParameterList the list of bean parameters, in which we will start to append if a parameter matches
     * @return true if they all match, false if they don't.
     */
    private static boolean checkParametersTypes(BeanFactory beanFactory, Parameter[] constructorParameters, String[] constructorParameterNames, List<BeanParameter> beanParameterList) {
        boolean allParamsClassesMatched = true;
        int parameterIndex = 0;

        Class currBeanClass;

        //For every parameter
        for (Parameter constructorParameter : constructorParameters) {
            currBeanClass = beanFactory.findBean(constructorParameterNames[parameterIndex]).getBeanClass();
            //If the type of the bean is the same as the type of the parameter
            if ( currBeanClass == constructorParameter.getType()) {
                //Append it
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
