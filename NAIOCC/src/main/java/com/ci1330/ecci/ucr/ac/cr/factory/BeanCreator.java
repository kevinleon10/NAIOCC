package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.*;

import java.util.List;

/**
 * Created by kevinleon10 on 09/09/17.
 */
public class BeanCreator {
    private Bean bean;
    private Attribute attributeClass;
    private Constructor constructorClass;
    private List<Parameter> constructorParams;

    public void createBean(String id, String classBean, Scope scope, String initMethod, String destroyMethod, boolean lazyGen, String autowire){
    }
    public void registerSetter(String attributeName, Object value, String beanREf){
    }
    public void registerConstructor(String paramType, int index, Object value, String beanRef){
    }
}
