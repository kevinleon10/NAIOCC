package com.ci1330.ecci.ucr.ac.cr.bean;

import com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory;

/**
 * Created by Elias Calderon on 15/09/2017
 *
 * BeanParameter class for NAIOCC Container.
 * Contains the Metadata of a Bean's constructor's parameter.
 */
public class BeanParameter extends BeanProperty {

    private int index; //The position of the parameter in the constructor.
    private String type;

    /**
     * Constructor of the class, initializes the class and super-class attributes.
     * @param beanRef init value for the super's beanRef attribute
     * @param beanFactory init value for the super's beanFactory attribute
     * @param value init value for the super's value attribute
     * @param index init value for the parameter's index.
     */
    public BeanParameter(String beanRef, BeanFactory beanFactory, Object value, int index, String type) {
        super(beanRef, beanFactory, value);
        this.index = index;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
