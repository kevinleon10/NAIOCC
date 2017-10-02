package com.ci1330.ecci.ucr.ac.cr.exception;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 *
 * Throws an exception if someone tries to recover a bean from {@link com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory}
 * and the id does not exist.
 */
public class IdNotFoundException extends Exception {

    public IdNotFoundException() {
    }
    public IdNotFoundException(String message) {
        super(message);
    }
}
