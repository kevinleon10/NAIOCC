package com.ci1330.ecci.ucr.ac.cr.ejemplos;

import java.util.Random;

public class Triangle implements Shape{

    private Point pointA;
    private Point pointB;
    private Point pointC;

    private Point triangleCenter;


    public Triangle() {
    }

    public Triangle(Point pointA, Point pointB, Point pointC) {
        this.pointA = pointA;
        this.pointB = pointB;
        this.pointC = pointC;
    }

    public void init() {
        System.out.println("Bean init of triangle");
    }

    public void destroy(){
        System.out.println("Bean destroy of triangle");
    }

    public void setTriangleCenter(Point triangleCenter) {
        this.triangleCenter = triangleCenter;
    }

    public Point getPointA() {
        return pointA;
    }

    public Point getPointB() {
        return pointB;
    }

    public Point getPointC() {
        return pointC;
    }

    public void print(){
    }
    public Point getTriangleCenter() {
        return triangleCenter;
    }
}
