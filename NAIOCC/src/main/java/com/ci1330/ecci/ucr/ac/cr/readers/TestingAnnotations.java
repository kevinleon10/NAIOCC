package com.ci1330.ecci.ucr.ac.cr.readers;

import com.ci1330.ecci.ucr.ac.cr.annotations.*;

/**
 * Created by lskev on 17-Sep-17.
 */


@Bean(id = "fuck")
@Scope(com.ci1330.ecci.ucr.ac.cr.bean.Scope.Prototype)
@Lazy
@Autowire("byType")
public class TestingAnnotations {
    @Attribute(value = "fuck")
    private String test;

    @Attribute(ref = "kkk")
    public int l;

    @Init
    private void firstMethod(){
        System.out.println("Fuck on");
    }

    @Constructor
    @Parameter(type = "String", value = "fuck")
    public TestingAnnotations(String test){
        this.test = "";
    }

    //@Init
    @Destroy
    public void lastMethod(){
        System.out.println("Fuck off");
    }

    public void fuck(){

    }
}
