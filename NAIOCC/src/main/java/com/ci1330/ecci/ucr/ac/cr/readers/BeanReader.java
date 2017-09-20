package com.ci1330.ecci.ucr.ac.cr.readers;

import com.ci1330.ecci.ucr.ac.cr.factory.BeanCreator;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory;

public abstract class BeanReader{

    /**
     * Object used to create the beans
     */
    protected BeanCreator beanCreator;

    /**
     * General constructor that initializes the creator
     */
    public BeanReader (BeanFactory beanFactory) {
        this.beanCreator = new BeanCreator(beanFactory);
    }

    public BeanReader(BeanCreator beanCreator) {
        this.beanCreator = beanCreator;
    }

    /**
     * Abstract method, that indicates the name of the input to read
     * @param inputName
     */
    public abstract void readBeans (String inputName);

}