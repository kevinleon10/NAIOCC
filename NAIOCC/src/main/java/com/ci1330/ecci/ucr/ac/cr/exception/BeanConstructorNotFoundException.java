package com.ci1330.ecci.ucr.ac.cr.exception;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 *
 * Indicates an exception when the {@link com.ci1330.ecci.ucr.ac.cr.factory.BeanConstructorModule} can not
 * find a Constructor.
 */
public class BeanConstructorNotFoundException extends Exception {

    public BeanConstructorNotFoundException() {
        super();
    }

    public BeanConstructorNotFoundException(String message) {
        super(message);
    }

}
