package com.ci1330.ecci.ucr.ac.cr.ejemplos;

import com.ci1330.ecci.ucr.ac.cr.annotations.AtomicAutowire;
import com.ci1330.ecci.ucr.ac.cr.annotations.Bean;
import com.ci1330.ecci.ucr.ac.cr.annotations.ClassAutowire;

@Bean("tulipBean")
@ClassAutowire(value = "constructor")
public class Tulip {

    private Point pointA;

    @AtomicAutowire
    private Pod pod;

    public Tulip (Point pointA ) {
        this.pointA = pointA;
    }

    public Point getPointA() {
        return pointA;
    }

    public void setPointA(Point pointA) {
        this.pointA = pointA;
    }

    public Pod getPod() {
        return pod;
    }

    public void setPod(Pod pod) {
        this.pod = pod;
    }
}
