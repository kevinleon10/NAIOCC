package com.ci1330.ecci.ucr.ac.cr.ejemplos;

import com.ci1330.ecci.ucr.ac.cr.factory.XMLFactory;
import javafx.beans.property.ObjectProperty;

public class main {
    public static void main(String[] args) {
        XMLFactory factory = new XMLFactory("example.xml");
        Triangle t = (Triangle) factory.getBean("triangulo");
        t.print();
        factory.shutDownHook();
    }
}
