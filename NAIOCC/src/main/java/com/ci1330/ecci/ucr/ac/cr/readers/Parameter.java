package com.ci1330.ecci.ucr.ac.cr.readers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lskev on 17-Sep-17.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface Parameter {
    String type() default "String";
    String value() default "";
    String ref() default "";
    int index() default 0;
}
