package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.Bean;

import java.util.HashMap;
import java.util.List;

public abstract class BeanContainer {

    protected HashMap<String,List<Bean>> beansMap;

    public HashMap<String, List<Bean>> getBeansMap() {
        return beansMap;
    }

    public void setBeansMap(HashMap<String, List<Bean>> beansMap) {
        this.beansMap = beansMap;
    }

    public BeanContainer(HashMap<String, List<Bean>> beansMap) {
        this.beansMap = new HashMap<String,List<Bean>>();
    }

    public void addBean(Bean bean){}

    public Object getBean(String id){
        return new Object();
    }


}
