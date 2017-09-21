package com.ci1330.ecci.ucr.ac.cr.ejemplos;

import com.ci1330.ecci.ucr.ac.cr.annotations.*;
import com.ci1330.ecci.ucr.ac.cr.bean.AutowireEnum;

/**
 * Created by lskev on 17-Sep-17.
 */


@Bean(id = "fuck")
@Scope(com.ci1330.ecci.ucr.ac.cr.bean.Scope.Prototype)
@Lazy
public class TestingAnnotations {
    @Attribute(value = "sarkis es puto")
    private String test;

    @Attribute(value = "12")
    public int l;

    @Init
    private void firstMethod(){
        System.out.println("Example Annotaions");
    }

    @Constructor
    @Parameter(type = "String")
    public TestingAnnotations(String test){
        this.test = "";
    }

    //@Init
    @Destroy
    public void lastMethod(){
        System.out.println("Fuck off");
    }

    public void fuck(){
        System.out.println("El valor de test es: " + test);
        System.out.println("El valor de value es: " + l);
    }
}
