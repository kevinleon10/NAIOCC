package com.ci1330.ecci.ucr.ac.cr.ejemplos;

import java.util.Random;

public class Square implements Shape{

    private Point a;
    private Point b;
    private Point c;
    private Point d;

    public Square() {

    }

    public Square(Point a, Point b, Point c, Point d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public void draw() {
        a = new Point();
        b = new Point();
        c = new Point();
        d = new Point();
    }

    @Override
    public void print() {
        System.out.print("Imprimiendo cuadrado.");
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

    public Point getD() {
        return d;
    }

    public void setD(Point d) {
        this.d = d;
    }

}
