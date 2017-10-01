package com.ci1330.ecci.ucr.ac.cr.ejemplos;

import com.ci1330.ecci.ucr.ac.cr.annotations.*;

@Bean("rose")
@Scope("Prototype")
@ClassAutowire(value = "byType")
public class Rose {

    private String colour;

    @Constructor
    @Parameter(value = "RED", type = "java.lang.String")
    public Rose (String colour) {
        this.colour = colour;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}
