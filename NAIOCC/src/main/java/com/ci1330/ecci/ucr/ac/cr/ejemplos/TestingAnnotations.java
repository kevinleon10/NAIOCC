package com.ci1330.ecci.ucr.ac.cr.ejemplos;

import com.ci1330.ecci.ucr.ac.cr.annotations.*;
import com.ci1330.ecci.ucr.ac.cr.bean.AutowireEnum;

/**
 * Created by lskev on 17-Sep-17.
 */


@Repository("fuck")
@Scope(com.ci1330.ecci.ucr.ac.cr.bean.Scope.Prototype)
@Lazy
public class TestingAnnotations {
    @Attribute("sarkis es puto")
    private String test;

    @Attribute("12")
    public int l;

    @Init
    public void firstMethod(){
        System.out.println("Example Annotaions");
    }

    @Constructor
    @Parameter(type = "java.lang.String", value = "fuck")
    public TestingAnnotations(String test){
        this.test = test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public void setL(int l) {
        this.l = l;
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
