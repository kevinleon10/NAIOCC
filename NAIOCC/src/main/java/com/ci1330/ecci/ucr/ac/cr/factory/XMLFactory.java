package com.ci1330.ecci.ucr.ac.cr.factory;


import com.ci1330.ecci.ucr.ac.cr.bean.Bean;
import com.ci1330.ecci.ucr.ac.cr.exception.IdNotFoundException;
import com.ci1330.ecci.ucr.ac.cr.readers.XmlBeanReader;

import java.util.HashMap;

/**
 * Created by Josue Leon on 13/09/2017
 */
public class XMLFactory extends BeanFactory{

    private XmlBeanReader xmlBeanReader;

    private String xmlFile;

    public XMLFactory(String xmlFile){
        super();
        this.xmlFile = xmlFile;
        this.xmlBeanReader = new XmlBeanReader(this);
        this.registerConfig();
        super.initContainer();
    }

    public void registerConfig(){
        this.xmlBeanReader.readBeans(this.getXmlFile());
    }

    @Override
    public HashMap<String, Bean> getBeansMap() {
        return super.getBeansMap();
    }

    @Override
    public void setBeansMap(HashMap<String, Bean> beansMap) {
        super.setBeansMap(beansMap);
    }

    @Override
    public Object getBean(String id) {
        return super.getBean(id);
    }

    @Override
    public void addBean(Bean bean) {
        super.addBean(bean);
    }

    @Override
    public void shutDownHook() {
        super.shutDownHook();
    }

    //----------------------------------------------------------------
    // Standard Setters and Getters section
    //----------------------------------------------------------------

    public String getXmlFile() {
        return xmlFile;
    }

    public XmlBeanReader getXmlBeanReader() {
        return xmlBeanReader;
    }

    public void setXmlBeanReader(XmlBeanReader xmlBeanReader) {
        this.xmlBeanReader = xmlBeanReader;
    }
}
