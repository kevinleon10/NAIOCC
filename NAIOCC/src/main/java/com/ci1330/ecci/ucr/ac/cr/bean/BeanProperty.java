package com.ci1330.ecci.ucr.ac.cr.bean;

import com.ci1330.ecci.ucr.ac.cr.exception.BeanAtomicAutowireException;
import com.ci1330.ecci.ucr.ac.cr.exception.BeanPropertyException;
import com.ci1330.ecci.ucr.ac.cr.exception.BeanTypeConflictException;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
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
    private AutowireEnum atomic_autowire; //Specifies the atomic autowiring for the property

    /**
     * Constructor of the class, initializes the class attributes.
     * @param beanRef init value for the property's beanRef attribute
     * @param beanFactory init value for the property's beanFactory attribute
     * @param value init value for the property's value attribute
     */
    BeanProperty(String beanRef, Class beanRefType, BeanFactory beanFactory, Object value, AutowireEnum atomic_autowire) {
        this.beanRef = beanRef;
        this.beanRefType = beanRefType;
        this.beanFactory = beanFactory;
        this.value = value;
        this.atomic_autowire = atomic_autowire;
    }

    /**
     * The bean instance can either be an explicit value, or be fetched from the BeanFactory
     * @return instance of the value or instance sent by the container for the reference
     */
    Object getInstance () {
        if (this.value == null) {
            return this.beanFactory.getBean(this.beanRef);
        } else {
            return this.value;
        }
    }

    /**
     * According to the value of /atomic_autowire, autowires the property
     */
    void autowireProperty () {
        switch (this.atomic_autowire) {
            case byName:
                //This case is mostly for parameter autowiring, in which the type is known until the container
                //is fully created
                this.autowireByName();
                break;
            case byType:
                //This case is for both parameters and attributes.
                //It searches the container for a bean that matches with its type, if found stores its ID.
                this.autowireByType();
                break;
            case annotation:
                //This case is exclusive for attributes that were autowired using an annotation.
                //It first tries to autowire byType, if it fails, tries to autowire byName
                this.autowireByAnnotation();
                break;
        }
    }

    /**
     * Searches for a bean with the name of beanRef, if not found, throws an exception
     * If found, puts beanRefType (if null) to the type of the recovered bean
     */
    private void autowireByName () {
        if (this.beanFactory.findBean(this.beanRef) == null) {
            try {
                throw new BeanAtomicAutowireException("Bean Atomic-Autowire Error: At atomic-autowiring byName no bean was found for the beanId " + this.beanRef);
            } catch (BeanAtomicAutowireException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else if (this.beanRefType == null) {
            //If the beanRefType was null, put it as the type of the recovered bean
            this.beanRefType = this.beanFactory.findBean(this.beanRef).getBeanClass();
        }
    }

    /**
     * Searches for a bean with the type of beanRefType, if not found, throws an exception
     * If found, stores the recovered bean's ID
     */
    private void autowireByType () {
        try {
            if (this.beanFactory.findBean(this.beanRefType) == null) {

                try {
                    throw new BeanAtomicAutowireException("Bean Atomic-Autowire Error: At atomic-autowiring byType no bean was found for the type " + this.beanRefType);
                } catch (BeanAtomicAutowireException e) {
                    e.printStackTrace();
                    System.exit(1);
                }

            } else {
                //If a bean exists, store the property's ID
                this.beanRef = this.beanFactory.findBean(this.beanRefType).getId();
            }
        } catch (BeanTypeConflictException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Special autowire for annotation, first tries to do autowire byType, if it fails, it does autowire byName
     */
    private void autowireByAnnotation () {
        try {
            //Sees if there exists a bean with that type (autowire byType)
            if (this.beanFactory.findBean(this.beanRefType) == null) {

                //Sees if there exists a bean with that reference (autowire byName)
                if (this.beanFactory.findBean(this.beanRef) == null) {
                    try {
                        throw new BeanAtomicAutowireException("Bean Atomic-Autowire Error: At atomic-autowiring byName no bean was found for the beanId " + this.beanRef);
                    } catch (BeanAtomicAutowireException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
                //We don't have to do anything if the beanRef is valid, because the type is already assign
                //And the checkProperty method will check that everything matches.

            } else {
                this.beanRef = this.beanFactory.findBean(this.beanRefType).getId();
            }
        } catch (BeanTypeConflictException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Checks if the metadata of a bean is correct, if not, throws an exception.
     */
    public void checkProperty() {
        if (value == null) {
            boolean thereIsProblem = true;
            //If the reference is not null and exists a reference for it in the container
            if (this.beanRef != null && this.beanFactory.findBean(this.beanRef) != null) {

                //If the type is not null and the bean returned by the factory matches with the declared type
                if (this.beanRefType != null && this.beanRefType == this.beanFactory.findBean(this.beanRef).getBeanClass()) {

                    thereIsProblem = false;
                }

            }

            if (thereIsProblem) {
                try {
                    throw new BeanPropertyException("Bean Property Error: There was an unexpected exception with the reference and type of the property " + this.beanRef);
                } catch (BeanPropertyException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
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

    public Object getValue() {
        return value;
    }
}
