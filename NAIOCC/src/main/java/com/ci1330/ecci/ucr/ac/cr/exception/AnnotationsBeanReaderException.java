package com.ci1330.ecci.ucr.ac.cr.exception;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 *
 * Indicates an exception in {@link com.ci1330.ecci.ucr.ac.cr.readers.AnnotationsBeanReader}
 */
public class AnnotationsBeanReaderException extends Exception {
    public AnnotationsBeanReaderException() {
    }

    public AnnotationsBeanReaderException(String message) {
        super(message);
    }
}
