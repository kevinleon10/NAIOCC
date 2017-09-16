package com.ci1330.ecci.ucr.ac.cr.factory;


import com.ci1330.ecci.ucr.ac.cr.bean.Bean;
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
        xmlBeanReader = new XmlBeanReader(super.getBeanCreator());
        this.registerConfig();
        super.initContainer();
    }

    @Override
    public void registerConfig(){
        this.xmlBeanReader.readConfig(this.getXmlFile());
    }

    public String getXmlFile() {
        return xmlFile;
    }

    public void setXmlFile(String xmlFile) {
        this.xmlFile = xmlFile;
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

    public XmlBeanReader getXmlBeanReader() {
        return xmlBeanReader;
    }

    public void setXmlBeanReader(XmlBeanReader xmlBeanReader) {
        this.xmlBeanReader = xmlBeanReader;
    }
}
