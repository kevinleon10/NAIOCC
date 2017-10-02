package com.ci1330.ecci.ucr.ac.cr.exception;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 *
 * Indicates an exception when there is a conflict in {@link com.ci1330.ecci.ucr.ac.cr.factory.BeanConstructorModule}
 */
public class BeanConstructorConflictException extends Exception{

    public BeanConstructorConflictException() {
        super();
    }

    public BeanConstructorConflictException(String message) {
        super(message);
    }

}
