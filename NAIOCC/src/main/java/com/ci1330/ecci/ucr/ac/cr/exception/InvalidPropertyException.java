package com.ci1330.ecci.ucr.ac.cr.exception;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 *
 * Indicates an exception when {@link com.ci1330.ecci.ucr.ac.cr.factory.BeanCreator}
 * receives invalid property information.
 */
public class InvalidPropertyException extends Exception {

    public InvalidPropertyException() {
        super();
    }

    public InvalidPropertyException(String message) {
        super(message);
    }

}
