package com.ci1330.ecci.ucr.ac.cr.ejemplos;

public class Point {
    private int x;
    private int y;
    private int z;

    public Point() {}

    public Point(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void defaultInitMethod(){
        System.out.println("Printing default init method for test.com.ci1330.ecci.ucr.ac.cr.Point");
    }

    public void defaultDestroyMethod(){
        System.out.println("Printing default destroy method for test.com.ci1330.ecci.ucr.ac.cr.Point");
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

    public int getZ() { return z; }

    public void setZ(int z) {
        this.z = z;
    }

    public void print() {
        System.out.println("\n (" + x + "," + y + ")");
    }
}
