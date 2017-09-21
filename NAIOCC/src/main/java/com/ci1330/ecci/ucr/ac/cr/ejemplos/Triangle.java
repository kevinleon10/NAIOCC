package com.ci1330.ecci.ucr.ac.cr.ejemplos;

import java.util.Random;

public class Triangle implements Shape{

    private Point a;
    private Point b;
    private Point c;

    public Triangle(Point a, Point b, Point c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public void draw() {
        a = new Point();
        b = new Point();
        c = new Point();
    }

    @Override
    public void print() {
            System.out.print("Imprimiendo triangulo.");
    }

    public Point getA() {
        return a;
    }

    public void setA(Point a) {
        this.a = a;
    }

    public Point getB() {
        return b;
    }

    public void setB(Point b) {
        this.b = b;
    }

    public Point getC() {
        return c;
    }

    public void setC(Point c) {
        this.c = c;
    }

}
