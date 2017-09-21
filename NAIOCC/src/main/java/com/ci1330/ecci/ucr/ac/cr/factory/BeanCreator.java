package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.*;
import com.ci1330.ecci.ucr.ac.cr.exception.BeanConstructorConflictException;
import com.ci1330.ecci.ucr.ac.cr.exception.BeanConstructorNotFoundException;
import com.ci1330.ecci.ucr.ac.cr.exception.RepeatedIdException;
import com.ci1330.ecci.ucr.ac.cr.exception.SetterMethodNotFoundException;

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
    private List<BeanParameter> constructorParams;

    /**
     *
     * @param beanFactory
     */
    public BeanCreator(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        constructorParams = new ArrayList<BeanParameter>();
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
                if(initMethodName != null && method.getName().contains(destroyMethodName)){
                    if(method.getParameterCount() == 0 ){
                        destroyMethod = method;
                    }
                }
            }
            bean.setInitMethod(initMethod);
            bean.setDestroyMethod(destroyMethod);
            bean.setLazyGen(lazyGen);
            bean.setAutowireEnum(autowireEnum);
        }catch (RepeatedIdException r){
            r.printStackTrace();
            System.exit(1);
        }
    }

    /**
     *
     * @param attributeName
     * @param value
     * @param beanRef
     */
    public void registerSetter(String attributeName, Object value, String beanRef){

        Method setterMethod = null;
        Method[] beanMethods = this.bean.getBeanClass().getMethods();

        for(Method method: beanMethods){
            if(method.getName().startsWith("set") && method.getName().contains(attributeName)){
                Class[] methodParameterType = method.getParameterTypes();
                if(method.getParameterCount() == 1 && methodParameterType.getClass().equals(value.getClass())){
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
        BeanAttribute beanAttribute = new BeanAttribute(beanRef, this.beanFactory, value, setterMethod);
        bean.appendAttribute(beanAttribute);
    }

    /**
     *
     * @param paramType
     * @param index
     * @param value
     * @param beanRef
     */
    public void registerConstructorParameter(String paramType, int index, Object value, String beanRef){
        BeanParameter beanConstructorParam = new BeanParameter(beanRef, this.beanFactory, value, index, paramType);
        this.constructorParams.add(beanConstructorParam);
    }

    /**
     * Checks if all parameters have an index assigned. If at least one doesn't, it returns false.
     * @return
     */
    private boolean checkParametersIndexes(){
        boolean allIndexesAssigned = true;
        int paramListIndex = 0;
        BeanParameter beanParameter = null;
        while(paramListIndex < this.constructorParams.size() && allIndexesAssigned){
            beanParameter = this.constructorParams.get(paramListIndex);
            if(beanParameter.getIndex() == -1){
                allIndexesAssigned = false;
            }
            paramListIndex++;
        }
        return allIndexesAssigned;
    }

    private Class[] obtainParametersClassArray(){
        //int parametersClassArrayIndex = 0;
        String parameterClass = null;
        Class param = null;
        Class[] parametersClassArray = new Class[this.constructorParams.size()];
        for (BeanParameter p : this.constructorParams) {
            switch (p.getType()) {
                case "int":
                    param = int.class;
                    break;
                case "byte":
                    param = byte.class;
                    break;
                case "short":
                    param = short.class;
                    break;
                case "long":
                    param = long.class;
                    break;
                case "float":
                    param = float.class;
                    break;
                case "double":
                    param = double.class;
                    break;
                case "boolean":
                    param = boolean.class;
                    break;
                case "char":
                    param = char.class;
                    break;
                default:
                    parameterClass = p.getType();
                    try {
                        param = Class.forName(parameterClass);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
                parametersClassArray[p.getIndex()] = param;
        }
        return parametersClassArray;
    }

    /**
     *
     */
    public void registerConstructor() {
        Constructor matchedConstructor = null;
        if(!this.checkParametersIndexes()) {
            int totalParametersOneType = 0;
            int totalParametersMatched = 0;
            int constructorMatches = 0;
            int paramIndex = 0;
            boolean twoMatchesForOneParam = false;
            Constructor[] beanConstructors = this.bean.getBeanClass().getDeclaredConstructors();
            Class[] beanConstructorParameters;
            for (Constructor beanConstructor : beanConstructors) {
                beanConstructorParameters = beanConstructor.getParameterTypes();
                if (beanConstructorParameters.length == this.constructorParams.size()) {
                    for (BeanParameter p : this.constructorParams) {
                        for (Class parameter : beanConstructorParameters) {
                            paramIndex++;
                            switch (parameter.toString()) {
                                case "int":
                                    if (p.getType().equals("int") | p.getType().equals("java.lang.Integer")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "byte":
                                    if (p.getType().equals("byte") | p.getType().equals("java.lang.Byte")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "short":
                                    if (p.getType().equals("short") | p.getType().equals("java.lang.Short")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "long":
                                    if (p.getType().equals("long") | p.getType().equals("java.lang.Long")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "float":
                                    if (p.getType().equals("float") | p.getType().equals("java.lang.Float")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "double":
                                    if (p.getType().equals("double") | p.getType().equals("java.lang.Double")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "boolean":
                                    if (p.getType().equals("boolean") | p.getType().equals("java.lang.Boolean")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "char":
                                    if (p.getType().equals("char") | p.getType().equals("java.lang.Char")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                default:
                                    try {
                                        if (Class.forName(p.getType()).equals(parameter)) {
                                            totalParametersOneType++;
                                            totalParametersMatched++;
                                            p.setIndex(paramIndex);
                                        }
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }
                        }
                        paramIndex = 0;
                        if (totalParametersOneType > 1) {
                            twoMatchesForOneParam = true;
                        }
                        totalParametersOneType = 0;
                    }
                    if (totalParametersMatched == this.constructorParams.size() && !twoMatchesForOneParam) {
                        constructorMatches++;
                        matchedConstructor = beanConstructor;
                    }
                    totalParametersMatched = 0;
                }
                twoMatchesForOneParam = false;
            }
            if (constructorMatches == 0) {
                try {
                    throw new BeanConstructorNotFoundException("Bean creation error: constructor not found for the specified parameters in bean: " + this.bean.getId() + ".");
                } catch (BeanConstructorNotFoundException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            if (constructorMatches > 1) {
                try {
                    throw new BeanConstructorConflictException("Bean creation error: there are multiple constructors for the specified parameters in bean: " + this.bean.getId() +
                            ". Couldn't identify which one is intended to be called (same parameter quantity and types).");
                } catch (BeanConstructorConflictException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
        else{
            try {
                matchedConstructor = this.bean.getBeanClass().getConstructor(this.obtainParametersClassArray());
            } catch (NoSuchMethodException e) {
                System.out.println("Bean creation error: constructor not found for the specified parameters in bean: " + this.bean.getId() + ".");
                e.printStackTrace();
                System.exit(1);
            }
        }
        constructorClass = new BeanConstructor(matchedConstructor);
        constructorClass.setBeanParameterList(constructorParams);
        this.bean.setBeanConstructor(constructorClass);
    }

    /**
     *
     */
    public void addBeanToContainer(){
        this.beanFactory.addBean(this.bean);
        bean = null;
        attributeClass = null;
        constructorClass = null;
        this.constructorParams = new ArrayList<>();
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

    public List<BeanParameter> getConstructorParams() {
        return constructorParams;
    }

    public void setConstructorParams(List<BeanParameter> constructorParams) {
        this.constructorParams = constructorParams;
    }
}