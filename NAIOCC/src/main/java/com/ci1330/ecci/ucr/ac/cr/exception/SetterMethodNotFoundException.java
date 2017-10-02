package com.ci1330.ecci.ucr.ac.cr.exception;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 *
 * Indicates an exception when the {@link com.ci1330.ecci.ucr.ac.cr.factory.BeanCreator} does not find the setter method
 * of an attribute.
 */
public class SetterMethodNotFoundException extends Exception{

    public SetterMethodNotFoundException() {
        super();
    }

    public SetterMethodNotFoundException(String message) {
        super(message);
    }
}
