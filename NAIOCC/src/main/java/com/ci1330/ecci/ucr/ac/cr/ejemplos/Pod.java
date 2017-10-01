package com.ci1330.ecci.ucr.ac.cr.ejemplos;


import com.ci1330.ecci.ucr.ac.cr.annotations.*;

@Bean("podBean")
@Scope("Prototype")
@Lazy
public class Pod {

    @Attribute(ref="rose")
    private Rose rose;

    @AtomicAutowire
    private Tulip tulip;

    @AtomicAutowire
    private Leaf leaf;

    @Attribute(value = "3")
    private int noFlowers;

    private Point paintSpot;

    @AtomicAutowire
    public Pod (Point pointB) {
        this.paintSpot = pointB;
    }

    @Init
    public void initPot () {
        System.out.println("The pot has been created!");
    }

    @Destroy
    public void destroyPot () {
        System.out.println("The pot has been created!");
    }

    public Rose getRose() {
        return rose;
    }

    public void setRose(Rose rose) {
        this.rose = rose;
    }

    public Tulip getTulip() {
        return tulip;
    }

    public Leaf getLeaf() {
        return leaf;
    }

    public void setLeaf(Leaf leaf) {
        this.leaf = leaf;
    }

    public void setTulip(Tulip tulip) {
        this.tulip = tulip;
    }

    public int getNoFlowers() {
        return noFlowers;
    }

    public void setNoFlowers(int noFlowers) {
        this.noFlowers = noFlowers;
    }

    public Point getPaintSpot() {
        return paintSpot;
    }

    public void setPaintSpot(Point paintSpot) {
        this.paintSpot = paintSpot;
    }
}
