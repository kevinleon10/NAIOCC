package com.ci1330.ecci.ucr.ac.cr.ejemplos;

public class Tone {

    public String tonalidad;

    public Tone(){

    }

    public Tone(String fortaleza) {
        this.tonalidad= fortaleza;
    }

    public String getTonalidad() {
        return tonalidad;
    }

    public void setTonalidad(String fortaleza) {
        this.tonalidad = fortaleza;
    }

    public void defaultInitMethod(){
        System.out.println("Printing default init method for tone");
    }

    public void defaultDestroyMethod(){
        System.out.println("Printing default destroy method for tone");
    }
}
