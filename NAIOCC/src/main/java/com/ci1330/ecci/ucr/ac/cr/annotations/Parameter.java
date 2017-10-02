package com.ci1330.ecci.ucr.ac.cr.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 17/09/2017
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface Parameter {
    String type();
    int index() default -1;
    String value() default "";
    String ref() default "";
}
