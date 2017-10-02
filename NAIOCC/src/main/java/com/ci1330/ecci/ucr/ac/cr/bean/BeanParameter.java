package com.ci1330.ecci.ucr.ac.cr.bean;

import com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 15/09/2017
 *
 * BeanParameter class for NAIOCC Container.
 * Contains the Metadata of a Bean's constructor's parameter.
 */
public class BeanParameter extends BeanProperty {

    private int index; //The position of the parameter in the constructor.
    private String explicitTypeName;

    /**
     * Constructor of the class, initializes the class and super-class attributes.
     * @param beanRef init value for the super's beanRef attribute
     * @param beanFactory init value for the super's beanFactory attribute
     * @param value init value for the super's value attribute
     * @param index init value for the parameter's index.
     */
    public BeanParameter(String beanRef, Class beanRefClass, BeanFactory beanFactory, Object value, AutowireEnum atomic_autowire, int index, String explicitTypeName) {
        super(beanRef, beanRefClass, beanFactory, value, atomic_autowire);
        this.index = index;
        this.explicitTypeName = explicitTypeName;
    }

    //----------------------------------------------------------------
    // Standard Setters and Getters section
    //----------------------------------------------------------------

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getExplicitTypeName() {
        return explicitTypeName;
    }

    public void setExplicitTypeName(String explicitTypeName) {
        this.explicitTypeName = explicitTypeName;
    }

}
