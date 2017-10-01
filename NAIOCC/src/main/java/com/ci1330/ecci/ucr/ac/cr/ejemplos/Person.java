package com.ci1330.ecci.ucr.ac.cr.ejemplos;

public class Person {
    private String name;
    private int identification;
    private String nationality;
    private boolean civilStatus;

    private Triangle triangle;
    private Bicycle bicycle;

    public Person(int identification, String nationality, boolean civilStatus) {
        this.name = name;
        this.identification = identification;
        this.nationality = nationality;
        this.civilStatus = civilStatus;
    }

    public void defaultInitMethod(){
        System.out.println("Printing default init method for test.com.ci1330.ecci.ucr.ac.cr.Person");
    }

    public void defaultDestroyMethod(){
        System.out.println("Printing default destroy method for test.com.ci1330.ecci.ucr.ac.cr.Person");
    }

    public void setTriangle(Triangle triangle) {
        this.triangle = triangle;
    }

    public void setBicycle(Bicycle bicycle) {
        this.bicycle = bicycle;
    }

    public void print(){
        System.out.println("Person's information: \n " + "Name: " + name + "\n Nationatily: " + nationality + "\n Civil Status: " + civilStatus
                + "\n Owner of a triangle with points: ");
        triangle.getPointA().print();
        triangle.getPointB().print();
        triangle.getPointC().print();
        System.out.println("\n Owner of a bycicle with: " + bicycle.getFirstWheel().getRim().getBrand().getName() + "wheels.");
    }

    public void setName(String name) {
        this.name = name;
    }
}
