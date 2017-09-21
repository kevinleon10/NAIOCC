package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.*;
import com.ci1330.ecci.ucr.ac.cr.exception.*;

import java.io.CharConversionException;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.text.ParseException;
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

    private Object obtainValueType(String stringValue){
        boolean parsed = false;
        Object value = null;
        System.out.println(stringValue);
        if(parsed == false) {
            try {
                value = Integer.valueOf(stringValue);
                parsed = true;
               // System.out.print("es un int");
            } catch (NumberFormatException e) {
                //No es un int.
                //System.out.print("excepcion int");
            }
        }
        if(parsed = false) {
            try {
                value = Byte.valueOf(stringValue);
                //System.out.print("es un byte");
            } catch (NumberFormatException e) {
                //No es un byte.
               // System.out.print("excepcion byte");
            }
        }
        if(parsed = false) {
            try {
                value = Short.valueOf(stringValue);
                //System.out.print("es un short");
            } catch (NumberFormatException e) {
                //No es un byte.
               // System.out.print("excepcion short");
            }
        }
        if(parsed = false) {
            try {
                value = Long.valueOf(stringValue);
                //System.out.print("es un long");
            } catch (NumberFormatException e) {
                //No es un byte.
               // System.out.print("excepcion long");
            }
        }
        if(parsed = false) {
            try {
                value = Float.valueOf(stringValue);
                //System.out.print("es un float");
            } catch (NumberFormatException e) {
                //No es un byte.
                //System.out.print("excepcion float");
            }
        }
        if(parsed = false) {
            try {
                value = Double.valueOf(stringValue);
                //System.out.print("es un double");
            } catch (NumberFormatException e) {
                //No es un byte.
                //System.out.print("excepcion double");
            }
        }
        if(parsed = false) {
            try {
                value = Boolean.valueOf(stringValue);
                //System.out.print("es un boolean");
            } catch (NumberFormatException e) {
                //No es un byte.
                //System.out.print("excepcion boolean");
            }
        }
        if(stringValue.length() > 1 && parsed == false) {
            try {
                value = stringValue.charAt(0);
            } catch (Exception e) {
                //No es un char.
            }
        }
        if(parsed = false) {
            try {
                value = stringValue;
            }catch(NumberFormatException e){
            }
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

        for(Method method: beanMethods){
            //System.out.println( method.getName().toLowerCase() + " contiene:??? " + attributeName.toLowerCase());
            if(method.getName().startsWith("set") && method.getName().toLowerCase().contains(attributeName.toLowerCase())){
                Class[] methodParameterType = method.getParameterTypes();
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

        if(value == null && beanRef == null){
            try {
                throw new InvalidPropertyException("Bean creation error: attribute's type or reference is invalid.");
            } catch (InvalidPropertyException e) {
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
            //System.out.println("agregando parametro al array tipo:" + param);
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
                //System.out.println("hciendo loop de un constructor");
                beanConstructorParameters = beanConstructor.getParameterTypes();
                if (beanConstructorParameters.length == this.constructorParams.size()) {
                    for (BeanParameter p : this.constructorParams) {
                        //System.out.println("iterando lista parametros propios:" + p.getType());
                        for (Class parameter : beanConstructorParameters) {
                           // System.out.println("iterando lista parametros constructor:" + p.getType());
                            //System.out.println("clase del param:" + parameter.toString());
                            switch (p.getType()) {
                                case "int":
                                   // System.out.println("se metio en case int");
                                    if (parameter.toString().equals("int")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        //System.out.println("parametro hizo match con int");
                                        p.setIndex(paramIndex);
                                        System.out.println("indice del parametro tipo: " + p.getType() + " = " + paramIndex);
                                    }
                                    break;
                                case "java.lang.Integer":
                                    //System.out.println("se metio en case int");
                                    if (parameter.toString().equals("int")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                       // System.out.println("parametro hizo match con int");
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "byte":
                                    if (parameter.toString().equals("byte")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "java.lang.Byte":
                                    if (parameter.toString().equals("byte")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "short":
                                    if (parameter.toString().equals("short")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "java.lang.Short":
                                    if (parameter.toString().equals("short")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "long":
                                    if (parameter.toString().equals("long")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "java.lang.Long":
                                    if (parameter.toString().equals("long")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "float":
                                    if (parameter.toString().equals("float")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "java.lang.Float":
                                    if (parameter.toString().equals("float")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "double":
                                    if (parameter.toString().equals("double")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "java.lang.Double":
                                    if (parameter.toString().equals("double")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "boolean":
                                    //System.out.println("se metio en case boolean");
                                    if (parameter.toString().equals("boolean")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                       // System.out.println("parametro hizo match con boolean");
                                        p.setIndex(paramIndex);
                                        System.out.println("indice del parametro tipo: " + p.getType() + " = " + paramIndex);
                                    }
                                    break;
                                case "java.lang.Boolean":
                                   // System.out.println("se metio en case boolean");
                                    if (parameter.toString().equals("boolean")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        //System.out.println("parametro hizo match con boolean");
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "char":
                                    if (parameter.toString().equals("char")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                case "java.lang.Character":
                                    if (parameter.toString().equals("char")) {
                                        totalParametersOneType++;
                                        totalParametersMatched++;
                                        p.setIndex(paramIndex);
                                    }
                                    break;
                                default:
                                    //System.out.println("se metio a default con:" + p.getType() + " y de parametro del const: " + parameter);
                                    try {
                                        if (Class.forName(p.getType()).equals(parameter)) {
                                            totalParametersOneType++;
                                            totalParametersMatched++;
                                           // System.out.println("parametro hizo match con default");
                                            p.setIndex(paramIndex);
                                            System.out.println("indice del parametro tipo: " + p.getType() + " = " + paramIndex);
                                        }
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }
                            paramIndex++;
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
                //System.out.println("num parametros:" + this.obtainParametersClassArray().length);
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