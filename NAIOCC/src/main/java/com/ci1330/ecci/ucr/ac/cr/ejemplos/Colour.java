package com.ci1330.ecci.ucr.ac.cr.ejemplos;

public class Colour {

    public Tone tono;

    public Colour() {
    }

    public Colour(Tone t) {
        this.tono = t;
    }

    public Tone getName() {
        return tono;
    }

    public void setName(Tone t) {
        this.tono = t;
    }

    public void defaultInitMethod(){
        System.out.println("Printing default init method for color");
    }

    public void defaultDestroyMethod(){
        System.out.println("Printing default destroy method for color");
    }
}
