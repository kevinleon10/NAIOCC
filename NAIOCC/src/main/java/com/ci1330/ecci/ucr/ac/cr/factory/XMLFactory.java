package com.ci1330.ecci.ucr.ac.cr.factory;


import com.ci1330.ecci.ucr.ac.cr.bean.Bean;
import com.ci1330.ecci.ucr.ac.cr.readers.XmlBeanReader;

import java.util.HashMap;

/**
 * @Author Elias Calderon, Josue Leon, Kevin Leon
 * @Date 13/09/2017
 *
 * XMLFactory class which inherits from BeanFactory
 * and registers the XML file from which the configuration
 * must be read and tells the reader to parse it.
 */
public class XMLFactory extends BeanFactory{

    private XmlBeanReader xmlBeanReader;    // Instance of the XML configuration reader

    private String xmlFile;     //Path of the XML file which holds the configuration

    /**
     * Constructor of the class, it initializes the super-class attributes and
     * also the XML bean reader and the file.
     * @param xmlFile
     */
    public XMLFactory(String xmlFile){
        super();
        this.xmlFile = xmlFile;
        this.xmlBeanReader = new XmlBeanReader(this);
        this.registerConfig();
        super.initContainer();
    }

    //Registers the XML configuration file so that the reader starts parsing
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
