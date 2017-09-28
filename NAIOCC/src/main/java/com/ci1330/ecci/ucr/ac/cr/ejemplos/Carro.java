package com.ci1330.ecci.ucr.ac.cr.ejemplos;

public class Carro {

/*    private Llanta uno;
    private Llanta dos;
    private Llanta tres;
    private Llanta cuatro;
    private Persona dueno;*/

    public Llanta uno;
    public Llanta dos;
    public Llanta tres;
    public Llanta cuatro;
    public Persona dueno;

    public Carro(){}

    public Carro(Llanta uno, Llanta dos, Llanta tres, Llanta cuatro) {
        this.uno = uno;
        this.dos = dos;
        this.tres = tres;
        this.cuatro = cuatro;
    }

    public Llanta getUno() {
        return uno;
    }

    public void setUno(Llanta uno) {
        System.out.println("se metio en el set de llanta 1");
        this.uno = uno;
    }

    public Llanta getDos() {
        return dos;
    }

    public void setDos(Llanta dos) {
        this.dos = dos;
    }

    public Llanta getTres() {
        return tres;
    }

    public void setTres(Llanta tres) {
        this.tres = tres;
    }

    public Llanta getCuatro() {
        return cuatro;
    }

    public void setCuatro(Llanta cuatro) {
        this.cuatro = cuatro;
    }

    public void print() {
        System.out.println("Marcas de las llantas:" + this.getUno().toString());
    }


        public void defaultInitMethod(){
            System.out.println("Printing default init method for carro");
        }

        public void defaultDestroyMethod(){
            System.out.println("Printing default destroy method for carro");
        }

    public Persona getDueno() {
        return dueno;
    }

    public void setDueno(Persona dueno) {
        this.dueno = dueno;
    }
}
