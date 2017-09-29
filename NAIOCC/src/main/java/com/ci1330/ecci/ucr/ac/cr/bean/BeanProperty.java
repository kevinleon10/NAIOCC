package com.ci1330.ecci.ucr.ac.cr.bean;

import com.ci1330.ecci.ucr.ac.cr.exception.IdNotFoundException;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory;

/**
 * Created by Elias Calderon on 15/09/2017
 *
 * BeanParameter class for NAIOCC Container.
 * Contains the Metadata of a Bean's property. Manages the fetching from the factory, if
 * the property references a Bean.
 */
public abstract class BeanProperty {

    private String beanRef; //The beanId that references a bean
    private Class beanRefType;
    private BeanFactory beanFactory;
    private Object value; //The explicit value, specified by the end-user.

    /**
     * Constructor of the class, initializes the class attributes.
     * @param beanRef init value for the property's beanRef attribute
     * @param beanFactory init value for the property's beanFactory attribute
     * @param value init value for the property's value attribute
     */
    public BeanProperty(String beanRef, Class beanRefType, BeanFactory beanFactory, Object value) {
        this.beanRef = beanRef;
        this.beanRefType = beanRefType;
        this.beanFactory = beanFactory;
        this.value = value;
    }

    /**
     * The bean instance can either be an explicit value, or be fetched from the BeanFactory
     * @return
     */
    public Object getInstance () {
        if (this.value == null) {
            Object tempInstance = this.beanFactory.getBean(this.beanRef);

            if (!tempInstance.getClass().equals(this.beanRefType)) {
                System.err.println("Bean Error: Mismatch in class type defined by the user with the returned class returned by the container");
                System.exit(1);
            }

            return tempInstance;
        } else {
            return this.value;
        }
    }

    //----------------------------------------------------------------
    // Standard Setters and Getters section
    //----------------------------------------------------------------

    public void setBeanRef(String beanRef) {
        this.beanRef = beanRef;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getBeanRef() {
        return this.beanRef;
    }

    public Class getBeanRefType() {
        return this.beanRefType;
    }
}
