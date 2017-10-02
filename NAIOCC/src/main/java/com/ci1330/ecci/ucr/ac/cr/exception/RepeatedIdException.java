package com.ci1330.ecci.ucr.ac.cr.exception;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 *
 * Indicates an exception when {@link com.ci1330.ecci.ucr.ac.cr.factory.BeanCreator} receives a repeated bean id.
 */
public class RepeatedIdException extends Exception{

    public RepeatedIdException(){
    }

    public RepeatedIdException(String message){
        super(message);
    }

}
