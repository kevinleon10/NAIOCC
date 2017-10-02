package com.ci1330.ecci.ucr.ac.cr.factory;


import com.ci1330.ecci.ucr.ac.cr.bean.Bean;
import com.ci1330.ecci.ucr.ac.cr.readers.XmlBeanReader;

import java.util.HashMap;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
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
     * @param xmlFile the name of the file
     */
    public XMLFactory(String xmlFile){
        super();
        this.xmlFile = xmlFile;
        this.xmlBeanReader = new XmlBeanReader(this);
        this.registerConfig();
        super.initContainer();
    }

    //Tells the reader to start parsing
    private void registerConfig(){
        this.xmlBeanReader.readBeans(this.getXmlFile());
    }


    /**
     * Return a bean instance from the super class.
     * @param id the beanId
     * @return the bean instance
     */
    @Override
    public Object getBean(String id) {
        return super.getBean(id);
    }

    /**
     * Adds a bean to the container
     * @param bean the {@link Bean} class
     */
    @Override
    public void addBean(Bean bean) {
        super.addBean(bean);
    }

    /**
     * Calls the super method for shutDownHook
     */
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

    @Override
    public HashMap<String, Bean> getBeansMap() {
        return super.getBeansMap();
    }

    @Override
    public void setBeansMap(HashMap<String, Bean> beansMap) {
        super.setBeansMap(beansMap);
    }
}
