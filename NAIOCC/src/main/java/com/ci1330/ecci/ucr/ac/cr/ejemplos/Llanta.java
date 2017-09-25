package com.ci1330.ecci.ucr.ac.cr.ejemplos;

public class Llanta {

    private String marca;

    public Llanta() {
    }

    public Llanta(String marca) {
        this.marca = marca;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void defaultInitMethod(){
        System.out.println("Printing default init method for llanta");
    }

    public void defaultDestroyMethod(){
        System.out.println("Printing default destroy method for llanta");
    }
}
