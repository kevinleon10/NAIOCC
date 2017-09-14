package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.Bean;

import java.util.HashMap;

public class BeanFactory extends BeanContainer {

    public BeanFactory(HashMap<String,Bean> beansMap){
        super(beansMap);
    }

    public HashMap<String, Bean> getBeansMap() {
        return super.getBeansMap();
    }

    public void setBeansMap(HashMap<String, Bean> beansMap) {
        super.setBeansMap(beansMap);
    }

    public Object getBean(String id){
        return super.getBean(id);
    }

    @Override
    public void addBean(Bean bean){
            super.addBean(bean);
    }

    public void shutDownHook(){
        super.shutDownHook();
    }

}
