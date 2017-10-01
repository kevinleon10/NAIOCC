package com.ci1330.ecci.ucr.ac.cr.ejemplos;

import com.ci1330.ecci.ucr.ac.cr.factory.AnnotationsFactory;
import com.ci1330.ecci.ucr.ac.cr.factory.XMLFactory;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        XMLFactory factory = new XMLFactory("example.xml");
        Person p = (Person) factory.getBean("person");
        p.print();

        AnnotationsFactory annotationsFactory = new AnnotationsFactory("com.ci1330.ecci.ucr.ac.cr.ejemplos.TestingAnnotations");
        TestingAnnotations testingAnnotations = (TestingAnnotations) annotationsFactory.getBean("fuck");
        // test.com.ci1330.ecci.ucr.ac.cr.TestingAnnotations testingAnnotations = (test.com.ci1330.ecci.ucr.ac.cr.TestingAnnotations) factory.getBean("fuck");
        testingAnnotations.fuck();
        factory.shutDownHook();
    }
}
