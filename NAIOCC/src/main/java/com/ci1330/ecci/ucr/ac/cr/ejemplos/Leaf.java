package com.ci1330.ecci.ucr.ac.cr.ejemplos;

import com.ci1330.ecci.ucr.ac.cr.annotations.Bean;
import com.ci1330.ecci.ucr.ac.cr.annotations.ClassAutowire;
import com.ci1330.ecci.ucr.ac.cr.annotations.Lazy;
import com.ci1330.ecci.ucr.ac.cr.annotations.Scope;

@Bean("leaf")
@Scope("Prototype")
@Lazy
@ClassAutowire(value = "byType")
public class Leaf {

    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
