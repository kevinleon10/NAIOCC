package com.ci1330.ecci.ucr.ac.cr.ejemplos;

import com.ci1330.ecci.ucr.ac.cr.factory.XMLFactory;

public class Main {

    public static void main(String[] args) {
        XMLFactory factory = new XMLFactory("example.xml");

        Triangle t = (Triangle) factory.getBean("triangulo");
        t.print();
        /*
        TestingAnnotations testingAnnotations = (TestingAnnotations) factory.getBean("fuck");
        testingAnnotations.fuck();
        factory.shutDownHook();*/
    }
}
