package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.*;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
        constructorParams = new ArrayList<>();
    }

    /**
     *
     * @param id
     * @param beanClass
     * @param scope
     * @param initMethod
     * @param destroyMethod
     * @param lazyGen
     * @param autowire
     * @throws RepeatedIdException
     */
    public void createBean(String id, String beanClass, Scope scope, String initMethod, String destroyMethod, boolean lazyGen, Autowire autowire) throws RepeatedIdException{
        try {
            if(this.beanFactory.containsBean(id)){
                throw new RepeatedIdException("Exception error: Bean id " + id + " is repeated.");
            }
            bean = new Bean();
            bean.setId(id);
            try {
                bean.setBeanClass(Class.forName(beanClass));    // AGREGAR CASOS DE TIPOS PRIMITIVOS
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            bean.setScope(scope);
            bean.setInitMethod(initMethod);
            bean.setDestroyMethod(destroyMethod);
            bean.setLazyGen(lazyGen);
            bean.setAutowire(autowire);
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
        try {
            Method[] beanMethods = this.bean.getBeanClass.getMethods();

            for(Method method: beanMethods){
                if(method.getName().startsWith("set") && method.getName().contains(attributeName)){
                    Class[] methodParameterType = method.getParameterTypes();
                    if(method.getParameterCount() == 1 && methodParameterType.getClass().equals(value.getClass())){
                        setterMethod = method;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Creation error: Bean class not found for id:" + this.bean.getId());
            e.printStackTrace();
            System.exit(1);
        }
        if(setterMethod == null){
            throw new SetterMethodNotFoundException("Creation error: Bean attribute's setter method not found for attribute: " + attributeName + ".");
            System.exit(1);
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
        BeanParameter beanConstructorParam = new BeanParameter(beanRef, this.beanFactory, value, paramType, index);
        constructorParams.add(beanConstructorParam);
    }

    /**
     *
     */
    public void registerConstructor() {
        // CHEQUEAR SI YA TIENEN INDICES TODOS LOS PARAMETROS DE LA LISTA DEL BEAN, SI NO SE CORRE EL METODO
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
                        try {
                            if (Class.forName(p.getType()).equals(parameter)) {             // PONER CASOS PARA TIPOS PRIMITIVOS
                                totalParametersOneType++;
                                totalParametersMatched++;
                                p.setIndex(paramIndex);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    paramIndex = 0;
                    if(totalParametersOneType > 1) {
                        twoMatchesForOneParam = true;
                    }
                    totalParametersOneType = 0;
                }
                if(totalParametersMatched == this.constructorParams.size() && !twoMatchesForOneParam){
                    constructorMatches++;
                }
                totalParametersMatched = 0;
            }
            twoMatchesForOneParam = false;
        }
        if(constructorMatches == 0){
            System.exit(1); // EXCEPCION DE QUE NO HAYA UN CONSTRUCTOR PARA ESOS PARAMETROS
        }
        if(constructorMatches > 1){
            System.exit(1); // EXCEPCION DE QUE HAY DOS O MASCONSTRUCTORES CON LOS MISMOS TIPOS Y CANTIDAD
        }
        constructorClass = new BeanConstructor();
        constructorClass.setParameterList(constructorParams);
    }

    /**
     *
     */
    public void addBeanToContainer(){
        this.beanFactory.addBean(this.bean);
        bean = null;
        attributeClass = null;
        constructorClass = null;
        constructorParams = null;
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