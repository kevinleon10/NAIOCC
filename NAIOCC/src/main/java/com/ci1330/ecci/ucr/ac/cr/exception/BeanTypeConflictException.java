package com.ci1330.ecci.ucr.ac.cr.exception;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 *
 * Indicates an exception when trying to invoke findBean method of {@link com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory}
 * and there are two beans of the same type.
 */
public class BeanTypeConflictException extends Exception {

    public BeanTypeConflictException() {
        super();
    }

    public BeanTypeConflictException(String message) {
        super(message);
    }
}
