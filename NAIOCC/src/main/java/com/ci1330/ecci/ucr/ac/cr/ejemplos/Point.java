package com.ci1330.ecci.ucr.ac.cr.ejemplos;

import java.util.Random;

public class Point {

    private int x;
    private int y;

    public Point() {
        this.x = new Random(100).nextInt();
        this.y = new Random(100).nextInt();
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        System.out.println("Se creo el punto exitosamente.");
    }

    public Point(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
