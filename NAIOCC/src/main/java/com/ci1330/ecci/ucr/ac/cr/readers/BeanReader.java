package com.ci1330.ecci.ucr.ac.cr.readers;

import com.ci1330.ecci.ucr.ac.cr.factory.BeanCreator;

public abstract class BeanReader {

    /**
     * Object used to create the beans
     */
    private BeanCreator beanCreator;

    /**
     * General constructor that initializes the creator
     */
    public BeanReader () {
        this.beanCreator = new BeanCreator();
    }

    /**
     * Abstract method, that indicates the name of the input to read
     * @param inputName
     */
    public abstract void readBeans (String inputName);

}