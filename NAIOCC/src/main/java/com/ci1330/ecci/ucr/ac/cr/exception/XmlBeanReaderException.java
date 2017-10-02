package com.ci1330.ecci.ucr.ac.cr.exception;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 *
 * Indicates an exception when {@link com.ci1330.ecci.ucr.ac.cr.readers.XmlBeanReader} finds an error
 */
public class XmlBeanReaderException extends Exception {
    public XmlBeanReaderException() {
    }

    public XmlBeanReaderException(String message) {
        super(message);
    }
}
