package com.ci1330.ecci.ucr.ac.cr.ejemplos;

public class Persona {

    private String nombre;
    private int cedula;
    private String nacionalidad;
    private boolean estadoCivil;

    public Persona() {
    }

    public Persona(int cedula, String nacionalidad, boolean sexo) {
        this.cedula = cedula;
        this.nacionalidad = nacionalidad;
        this.estadoCivil = estadoCivil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCedula() {
        return cedula;
    }

    public void setCedula(int cedula) {
        this.cedula = cedula;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public boolean isEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(boolean estadoCivil) {
        this.estadoCivil = estadoCivil;
    }
}
