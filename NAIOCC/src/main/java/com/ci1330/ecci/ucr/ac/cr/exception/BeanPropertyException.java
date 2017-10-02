package com.ci1330.ecci.ucr.ac.cr.exception;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 *
 * Indicates an exception when something goes wrong in {@link com.ci1330.ecci.ucr.ac.cr.bean.BeanProperty}
 */
public class BeanPropertyException extends Exception {

    public BeanPropertyException() {
        super();
    }

    public BeanPropertyException(String message) {
        super(message);
    }

}
