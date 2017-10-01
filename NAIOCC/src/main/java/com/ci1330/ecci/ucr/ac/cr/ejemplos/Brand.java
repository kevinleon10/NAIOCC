package com.ci1330.ecci.ucr.ac.cr.ejemplos;

public class Brand {

    private String name;

    public Brand() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void defaultInitMethod(){
        System.out.println("Printing default init method for Brand");
    }

    public void defaultDestroyMethod(){
        System.out.println("Printing default destroy method for Brand");
    }
}
