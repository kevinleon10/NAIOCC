package com.ci1330.ecci.ucr.ac.cr.readers;

/**
 * Created by lskev on 17-Sep-17.
 */


@Bean(id = "beanId")
@Scope
@Lazy
@Autowire(value = "byType")
public class TestingAnnotations {
    @Attribute(value = "FUCK", ref = "beanId")
    private String test;

    @Init
    public void firstMethod(){
        System.out.println("Fuck on");
    }

    @Constructor
    @Parameter
    public TestingAnnotations(String test){
        this.test = "";
    }

    @Destroy
    public void lastMethod(){
        System.out.println("Fuck off");
    }

    public void fuck(){

    }
}
