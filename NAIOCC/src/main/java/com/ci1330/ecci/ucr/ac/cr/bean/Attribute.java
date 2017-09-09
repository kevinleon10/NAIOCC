package com.ci1330.ecci.ucr.ac.cr.bean;

import java.lang.reflect.Method;

/**
 * Created by kevinleon10 on 09/09/17.
 */
public class Attribute {
    private Bean bean;
    private Object value;
    private String beanRef;
    private Method setterMethod;

    public Bean getBean() {
        return bean;
    }

    public void setBean(Bean bean) {
        this.bean = bean;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getBeanRef() {
        return beanRef;
    }

    public void setBeanRef(String beanRef) {
        this.beanRef = beanRef;
    }

    public Method getSetterMethod() {
        return setterMethod;
    }

    public void setSetterMethod(Method setterMethod) {
        this.setterMethod = setterMethod;
    }
}
