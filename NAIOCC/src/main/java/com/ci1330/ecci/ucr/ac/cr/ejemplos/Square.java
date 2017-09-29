package com.ci1330.ecci.ucr.ac.cr.ejemplos;

import java.util.Random;

public class Square implements Shape{

    public Colour c;

    public Square() {

    }

    public Square(Colour c) {
        this.c = c;
    }

    @Override
    public void draw() {
        this.c = new Colour();
    }

    @Override
    public void print() {
        System.out.print("Imprimiendo cuadrado de color: " + this.c.getName());
    }

    public Colour getColour() {
        return c;
    }

    public void setColour(Colour c) {
        this.c = c;
    }
}
