package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.Bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BeanFactory extends BeanContainer implements Cloneable{


    public BeanFactory(HashMap<String,List<Bean>> beansMap){
        super(beansMap);
    }

    public HashMap<String, List<Bean>> getBeansMap() {
        return super.getBeansMap(); }

    public void setBeansMap(HashMap<String, List<Bean>> beansMap) {
        super.setBeansMap(beansMap); }

    public Object getBean(String id){
        //si es singleton devuelve el unico, si es prototype crea una nueva instancia
        // inyecta las dependencias
        if(super.getBeansMap().get(id).get(0).getScope().equals("Singleton")){
            //inicializar si no se ha inicializado el unico
            return  super.getBeansMap().get(id).get(0);
        }
        else{
            Bean newBean = (Bean)super.getBeansMap().get(id).get(0).clone();
            super.getBeansMap().get(id).add(newBean);
            //inyectar dependencias
            return super.getBeansMap().get(id).get(super.getBeansMap().get(id).size() - 1); //devuelve el que acaba de agregar ya inyectado
        }
    }

    public void addBean(Bean bean){
            List beanList = new ArrayList<Bean>();
            beanList.add(bean);
            super.getBeansMap().put(bean.getId(), beanList);
    }

    public void shutDownHook(){

    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
