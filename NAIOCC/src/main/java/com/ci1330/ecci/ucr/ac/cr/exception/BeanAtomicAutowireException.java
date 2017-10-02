package com.ci1330.ecci.ucr.ac.cr.exception;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 *
 * Indicates an exception in {@link com.ci1330.ecci.ucr.ac.cr.bean.BeanProperty} while trying
 * to atomicly autowire.
 */
public class BeanAtomicAutowireException extends Exception {

    public BeanAtomicAutowireException() {
        super();
    }

    public BeanAtomicAutowireException(String message) {
        super(message);
    }
}
