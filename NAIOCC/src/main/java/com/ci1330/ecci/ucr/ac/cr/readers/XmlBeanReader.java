package com.ci1330.ecci.ucr.ac.cr.readers;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import com.ci1330.ecci.ucr.ac.cr.factory.BeanCreator;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.beans.beancontext.BeanContext;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlBeanReader extends BeanReader {

    /**
     * The annotations reader is used if in the xml file, a read annotations
     * statement is found.
     */
    private AnnotationsBeanReader annotationsBeanReader;

    /**
     * Constructor that inits the annotations reader
     */
    public XmlBeanReader(String inputName, BeanCreator beanCreator) {
        super();
        this.annotationsBeanReader = new AnnotationsBeanReader();
        this.readBeans(inputName, beanCreator);
    }

    /**
     * Receives the name of the XML and creates the root
     *
     * @param inputName
     */
    public void readBeans(String inputName, BeanCreator beanCreator) {

        File fXmlFile = new File(inputName); //Crea un archivo
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance(); //Crea un doc factory, para crear el builder

        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder(); //Crea un doc builder para construir el doc
        } catch (ParserConfigurationException e) {
            //Buscar por qué ocurre este error
            e.printStackTrace();
            System.exit(1);
        }

        Document doc = null;
        try {
            doc = dBuilder.parse(fXmlFile); //Se parsea el doc creado
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
        doc.getDocumentElement().normalize(); //Se normaliza

        //Se llama a read root
        this.readRoot(doc);

        //Se crea una lista con los beans del xml
        NodeList nodeList = doc.getElementsByTagName("bean");

        for (int index = 0; index < nodeList.getLength(); index++) { //Se itera sobre cada bean

            Node node = nodeList.item(index); //Se obtiene el nodo actual

            if ((node.getNodeType() == Node.ELEMENT_NODE) && node.getNodeName() == "bean") { //Si es un nodo elemento y si es un bean

                Element element = (Element) node;
                this.readBeanProperties(element, beanCreator);
                this.readBeanConstructor(element, beanCreator);
                this.readBeanAttributes(element, beanCreator);
            }
        }
    }

    /**
     * Starts reading the root of the xml
     *
     * @param xmlRootFile
     */
    private void readRoot(Document xmlRootFile) {
        //System.out.println("Elemento raíz :" + xmlRootFile.getDocumentElement().getNodeName());
    }

    /**
     * Reads the properties of a bean from the bean xml node
     *
     * @param beanElement
     */
    private void readBeanProperties(Element beanElement, BeanCreator beanCreator) {

        String id = beanElement.getAttribute("id");
        String className = beanElement.getAttribute("class");
        String scope = beanElement.getAttribute("scope");
        String initMethod = beanElement.getAttribute("init");
        String destroyMethod = beanElement.getAttribute("destroy");
        String lazyGen = beanElement.getAttribute("lazy-generation");
        String autowire = beanElement.getAttribute("autowire");
        if((scope.equalsIgnoreCase("Singleton") || scope.equalsIgnoreCase("Prototype")) &&
                (lazyGen.equalsIgnoreCase("true") || lazyGen.equalsIgnoreCase("false")) &&
                (autowire.equalsIgnoreCase("byName") || autowire.equalsIgnoreCase("byType")))
        System.out.println("\nId del Bean: " + id); //Obtengo los atributos del bean
        System.out.println("Class del Bean: " + className);
        System.out.println("Scope del Bean: " + scope);
        System.out.println("Metodo init del Bean: " + initMethod);
        System.out.println("Metodo destroy del Bean: " + destroyMethod);
        System.out.println("Lazy-generation: " + lazyGen);
        System.out.println("Autowire del Bean: " + autowire);
    }

    /**
     * Reads an attribute of a bean from the attribute xml node
     *
     * @param attributeElement
     */
    private void readBeanAttributes(Element attributeElement, BeanCreator beanCreator) {
        NodeList nodeList = attributeElement.getElementsByTagName("attribute");
        for (int index = 0; index < nodeList.getLength(); index++) { //Se itera sobre cada attribute

            Node node = nodeList.item(index); //Se obtiene el nodo actual
            System.out.println("Attributes del Bean:");
            //System.out.println("\nElemento actual :" +  node.getNodeName());

            if (node.getNodeType() == Node.ELEMENT_NODE) { //Si es un nodo elemento

                Element element = (Element) node;
                System.out.println("Name del attribute: " + element.getAttribute("name")); //Obtengo los atributos del bean
                System.out.println("Value del attribute: " + element.getAttribute("value"));
                System.out.println("Id del Bean referenciado: " + element.getAttribute("ref"));
            }
        }
    }

    /**
     * Reads the constructor of a bean from the constructor xml node
     *
     * @param constructorElement
     */
    private void readBeanConstructor(Element constructorElement, BeanCreator beanCreator) {
        Node node = constructorElement.getElementsByTagName("param").item(0);
        System.out.println("Parametros del constructor:");
        //System.out.println("\nElemento actual :" +  node.getNodeName());

        if (node.getNodeType() == Node.ELEMENT_NODE) { //Si es un nodo elemento

            Element element = (Element) node; //Se convierte el nodo en un elemento
            System.out.println("Tipo de parametro: " + element.getAttribute("type")); //Obtengo los atributos del bean
            System.out.println("Index del parametro: " + element.getAttribute("index"));
            System.out.println("Value del parametro: " + element.getAttribute("value"));
            System.out.println("Id del Bean referenciado: " + element.getAttribute("ref"));
        }
    }

    /**
     * The method tells the annotationsBeanReader to read a specific class.
     *
     * @param statementNode
     */
    private void readAnnotationsStatementFound(Node statementNode) {

    }

    public static void main(String[] args) {
        //Prueba
        BeanCreator beanCreator = new BeanCreator();
        BeanReader xmlBeanReader = new XmlBeanReader("example.xml", beanCreator);
    }
}
