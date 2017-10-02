package com.ci1330.ecci.ucr.ac.cr.exception;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 *
 * Indicates an exception in the {@link com.ci1330.ecci.ucr.ac.cr.bean.BeanAutowireModule}
 */
public class BeanAutowireException extends Exception{

    public BeanAutowireException() {
        super();
    }

    public BeanAutowireException(String message) {
        super(message);
    }
}
