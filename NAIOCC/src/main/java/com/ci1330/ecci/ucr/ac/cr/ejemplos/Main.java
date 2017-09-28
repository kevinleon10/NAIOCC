package com.ci1330.ecci.ucr.ac.cr.ejemplos;

import com.ci1330.ecci.ucr.ac.cr.factory.AnnotationsFactory;
import com.ci1330.ecci.ucr.ac.cr.factory.XMLFactory;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        XMLFactory factory = new XMLFactory("example.xml");

        Triangle t = (Triangle) factory.getBean("triangulo");
        t.print();

        Carro c = (Carro) factory.getBean("carro");
        Square s = (Square) factory.getBean("square");
       //String x = c.getTres().getMarca();
        //System.out.println(x);
        c.print();
        s.print();

        AnnotationsFactory annotationsFactory = new AnnotationsFactory("com.ci1330.ecci.ucr.ac.cr.ejemplos.TestingAnnotations");
        TestingAnnotations testingAnnotations = (TestingAnnotations) annotationsFactory.getBean("fuck");
       // TestingAnnotations testingAnnotations = (TestingAnnotations) factory.getBean("fuck");
        testingAnnotations.fuck();
        factory.shutDownHook();
    }
}
