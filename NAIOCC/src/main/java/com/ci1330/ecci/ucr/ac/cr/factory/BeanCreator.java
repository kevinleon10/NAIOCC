package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josue Leon on 13/09/2017
 */
public class BeanCreator {

    private Bean bean;
    private BeanFactory beanFactory;
    private Attribute attributeClass;
    private Constructor constructorClass;
    private List<Parameter> constructorParams;

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
     * @param classBean
     * @param scope
     * @param initMethod
     * @param destroyMethod
     * @param lazyGen
     * @param autowire
     * @throws RepeatedIdException
     */
    public void createBean(String id, String classBean, Scope scope, String initMethod, String destroyMethod, boolean lazyGen, Autowire autowire) throws RepeatedIdException{
        try {
            if(this.beanFactory.getBeansMap().containsKey(id)){
                throw new RepeatedIdException("Exception error: Bean id " + id + " is repeated.");
            }
            bean = new Bean();
            bean.setId(id);
            bean.setClassBean(classBean);
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

        Method setterMethod;

        try {
            Class reflectionClass = Class.forName(this.bean.getClassBean());
            Method[] beanMethods = reflectionClass.getMethods();

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

        Attribute beanAttribute = new Attribute(beanRef, this.beanFactory, value, setterMethod);
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
        Parameter beanConstructorParam = new Parameter(beanRef, this.beanFactory, value, paramType, index);
        constructorParams.add(beanConstructorParam);
    }

    /**
     *
     */
    public void registerConstructor(){
        constructorClass = new Constructor();
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

    public Attribute getAttributeClass() {
        return attributeClass;
    }

    public void setAttributeClass(Attribute attributeClass) {
        this.attributeClass = attributeClass;
    }

    public Constructor getConstructorClass() {
        return constructorClass;
    }

    public void setConstructorClass(Constructor constructorClass) {
        this.constructorClass = constructorClass;
    }

    public List<Parameter> getConstructorParams() {
        return constructorParams;
    }

    public void setConstructorParams(List<Parameter> constructorParams) {
        this.constructorParams = constructorParams;
    }
}