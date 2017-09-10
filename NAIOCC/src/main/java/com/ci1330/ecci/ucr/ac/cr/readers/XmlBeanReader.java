package com.ci1330.ecci.ucr.ac.cr.readers;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class XmlBeanReader extends BeanReader {

    /**
     * The annotations reader is used if in the xml file, a read annotations
     * statement is found.
     */
    private AnnotationsBeanReader annotationsBeanReader;

    /**
     * Constructor that inits the annotations reader
     */
    public XmlBeanReader () {
        super();
        this.annotationsBeanReader = new AnnotationsBeanReader();
    }

    /**
     * Receives the name of the XML and creates the root
     * @param inputName
     */
    public void readBeans(String inputName) {

        File fXmlFile = new File(inputName); //No me acuerdo cómo era esto
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            //Buscar por qué ocurre este error
            e.printStackTrace();
            System.exit(1);
        }

        Document doc = null;
        try {
            doc = dBuilder.parse(fXmlFile);
        } catch (SAXException e) {
            //Buscar por qué ocurre este error
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            //Buscar por qué ocurre este error
            e.printStackTrace();
            System.exit(1);
        }

        //Normalize document
        doc.getDocumentElement().normalize();

        //Tiene que llamar a readRoot
    }

    /**
     * Starts reading the root of the xml
     * @param xmlRootFile
     */
    private void readRoot (Document xmlRootFile) {

    }

    /**
     * Reads the properties of a bean from the bean xml node
     * @param beanNode
     */
    private void readBeanProperties (Node beanNode) {

    }

    /**
     * Reads an attribute of a bean from the attribute xml node
     * @param attributeNode
     */
    private void readBeanAttribute (Node attributeNode) {

    }

    /**
     * Reads the constructor of a bean from the constructor xml node
     * @param constructorNode
     */
    private void readBeanConstructor (Node constructorNode) {

    }

    /**
     * The method tells the annotationsBeanReader to read a specific class.
     * @param statementNode
     */
    private void readAnnotationsStatementFound (Node statementNode) {

    }

}
