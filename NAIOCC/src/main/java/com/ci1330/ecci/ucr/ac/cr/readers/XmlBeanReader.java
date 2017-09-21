package com.ci1330.ecci.ucr.ac.cr.readers;

/**
 * Created by kevinleon10 on 12/09/17.
 */

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import com.ci1330.ecci.ucr.ac.cr.bean.AutowireEnum;
import com.ci1330.ecci.ucr.ac.cr.bean.Scope;
import com.ci1330.ecci.ucr.ac.cr.exception.XmlBeanReaderException;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanCreator;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory;
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
    private BeanFactory beanFactory;
    private String initMethod;
    private String destroyMethod;
    private String id;

    /**
     * Constructor that inits the annotations reader
     */
    public XmlBeanReader(BeanFactory beanFactory){
        super(beanFactory);
        this.beanFactory = beanFactory;
        this.initMethod = null;
        this.destroyMethod = null;
    }

    /**
     * Receives the name of the XML and creates the root
     *
     * @param inputName
     */
    public void readBeans(String inputName) {

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

        //Se llama a read root para obtener la raiz
        Element rootElement = this.readRoot(doc);

        //Se crea una lista con los beans de la raiz
        NodeList nodeList = rootElement.getElementsByTagName("bean");

        for (int index = 0; index < nodeList.getLength(); index++) { //Se itera sobre cada bean

            Node node = nodeList.item(index); //Se obtiene el nodo actual

            if ((node.getNodeType() == Node.ELEMENT_NODE)) { //Si es un nodo elemento

                Element beanElement = (Element) node;
                this.readBeanProperties(beanElement);
                this.readBeanConstructor(beanElement);
                this.readBeanAttributes(beanElement);
                super.beanCreator.addBeanToContainer();
            } else {
                try {
                    throw new XmlBeanReaderException("Xml Reader error: A 'bean' was not recognized.");
                } catch (XmlBeanReaderException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
        this.readAnnotationsStatementFound(rootElement);
    }

    /**
     * Starts reading the root of the xml
     *
     * @param xmlRootFile
     */
    private Element readRoot(Document xmlRootFile) {
        Element rootElement = xmlRootFile.getDocumentElement();
        if (rootElement.getTagName().equals("beans")) { //Si son beans
            if (rootElement.hasAttribute("init")) { //Si tiene init
                this.initMethod = rootElement.getAttribute("init");
            }
            if (rootElement.hasAttribute("destroy")) { //Si tiene destroy
                this.destroyMethod = rootElement.getAttribute("destroy");
            }
        } else {
            try {
                throw new XmlBeanReaderException("Xml Reader error: The root of the XML document is " + rootElement.getTagName() + " instead of 'beans'.");
            } catch (XmlBeanReaderException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return rootElement;
    }

    /**
     * Reads the properties of a bean from the bean xml node
     *
     * @param beanElement
     */
    private void readBeanProperties(Element beanElement) {

        if (beanElement.hasAttribute("id") && beanElement.hasAttribute("class")) {
            String id = beanElement.getAttribute("id");
            this.id = id;
            String className = beanElement.getAttribute("class");
            if (beanElement.hasAttribute("init")) {
                this.initMethod = beanElement.getAttribute("init");
            }
            if (beanElement.hasAttribute("destroy")) {
                this.destroyMethod = beanElement.getAttribute("destroy");
            }
            String scopeS = beanElement.getAttribute("scope");
            AutowireEnum autowireEnum = AutowireEnum.none;;
            String autowire = beanElement.getAttribute("autowire");
            if(autowire.equals("")){
                autowire = "none";
            }
            String lazyGen = beanElement.getAttribute("lazy-generation");
            if ((autowire.equals("byName") || autowire.equals("byType") || autowire.equals("none")) &&
                    (lazyGen.equals("true") || lazyGen.equals("false") || lazyGen.equals("")) &&
                    (scopeS.equals("Singleton") || scopeS.equals("Prototype") || scopeS.equals(""))) { //Reviso si pasan los requisitos
                Scope scope = Scope.Singleton;
                if (beanElement.getAttribute("scope").equals("Prototype")) {
                    scope = Scope.Prototype;
                }
                if (autowire.equals("byName")){
                    autowireEnum = AutowireEnum.byName;
                }
                else if(autowire.equals("byType")){
                    autowireEnum = AutowireEnum.byType;
                }
                boolean lazyGeneration = lazyGen.equals("true");
                this.beanCreator.createBean(id, className, scope, initMethod, destroyMethod, lazyGeneration, autowireEnum);

                //Se ven los resultados
                /*System.out.println("\nId del Bean: " + id); //Obtengo los atributos del bean
                System.out.println("Class del Bean: " + className);
                System.out.println("Scope del Bean: " + scope);
                System.out.println("Metodo init del Bean: " + initMethod);
                System.out.println("Metodo destroy del Bean: " + destroyMethod);
                System.out.println("Lazy-generation: " + lazyGeneration);
                System.out.println("AutowireEnum del Bean: " + autowire);*/
            } else {
                try {
                    throw new XmlBeanReaderException("Xml Reader error: 'scope', 'lazy-generation' or 'autowire' were not recognized in the 'bean' "+this.id+". It is misspelled.");
                } catch (XmlBeanReaderException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        } else {
            try {
                throw new XmlBeanReaderException("Xml Reader error: A 'bean' does not have an 'id', 'class' or both.");
            } catch (XmlBeanReaderException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * Reads the constructor of a bean from the constructor xml node
     *
     * @param beanElement
     */
    private void readBeanConstructor(Element beanElement) {
        NodeList nodeList = beanElement.getElementsByTagName("constructor");
        if (nodeList.getLength() > 1) {
            try {
                throw new XmlBeanReaderException("Xml Reader error: The 'constructor' was not recognized in the 'bean' "+this.id+". It has more than a definition.");
            } catch (XmlBeanReaderException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else if (nodeList.getLength() != 0){
            Element constructorElement = (Element) nodeList.item(0);
            nodeList = constructorElement.getElementsByTagName("param");
            for (int i = 0; i < nodeList.getLength(); i++) { //Se itera sobre cada attribute
                Node node = nodeList.item(i); //Se obtiene el nodo actual
                //System.out.println("Parametros del constructor:");

                if (node.getNodeType() == Node.ELEMENT_NODE) { //Si es un nodo elemento
                    Element element = (Element) node; //Se convierte el nodo en un elemento
                    if ((element.hasAttribute("value") && !(element.hasAttribute("ref"))) ||
                            (element.hasAttribute("ref") && !(element.hasAttribute("value")))) {
                        String type = element.getAttribute("type");
                        int index = -1;
                        try{
                            index = Integer.parseInt(element.getAttribute("index"));
                        }catch(NumberFormatException e){
                            //nulo
                        }
                        String value = element.getAttribute("value");
                        if (value.equals("")) {
                            value = null;
                        }
                        String ref = element.getAttribute("ref");
                        this.beanCreator.registerConstructorParameter(type,index,value,ref);

                        /*System.out.println("Tipo de parametro: " + type); //Obtengo los atributos del bean
                        System.out.println("Index del parametro: " + index);
                        System.out.println("Value del parametro: " + value);
                        System.out.println("Id del Bean referenciado: " + ref);*/
                    } else {
                        try {
                            throw new XmlBeanReaderException("Xml Reader error: A 'param' was not recognized in the 'bean' "+ this.id + ". It has 'value' and 'ref', or neither.");
                        } catch (XmlBeanReaderException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                        System.out.println();
                    }
                } else {
                    try {
                        throw new XmlBeanReaderException("Xml Reader error: A 'param' was not recognized in the 'bean' "+this.id+".");
                    } catch (XmlBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }

            }
            this.beanCreator.registerConstructor();
        }

    }

    /**
     * Reads an attribute of a bean from the attribute xml node
     *
     * @param beanElement
     */
    private void readBeanAttributes(Element beanElement) {
        NodeList nodeList = beanElement.getElementsByTagName("attribute");
        for (int index = 0; index < nodeList.getLength(); index++) { //Se itera sobre cada attribute

            Node node = nodeList.item(index); //Se obtiene el nodo actual
            //System.out.println("Attributes del Bean:");

            if (node.getNodeType() == Node.ELEMENT_NODE) { //Si es un nodo elemento
                Element element = (Element) node;
                if ((element.hasAttribute("name") && element.hasAttribute("value") && !(element.hasAttribute("ref"))) ||
                        (element.hasAttribute("name") && element.hasAttribute("ref") && !(element.hasAttribute("value")))) {
                    String name = element.getAttribute("name");
                    String value = element.getAttribute("value");
                    if (value.equals("")) {
                        value = null;
                    }
                    String beanRef = element.getAttribute("ref");
                    this.beanCreator.registerSetter(name, value, beanRef);

                    /*System.out.println("Name del attribute: " + name); //Obtengo los atributos del bean
                    System.out.println("Value del attribute: " + value);
                    System.out.println("Id del Bean referenciado: " + beanRef);*/
                } else {
                    try {
                        throw new XmlBeanReaderException("Xml Reader error: An 'attribute' was not recognized in the 'bean' "+this.id + ". It must has 'name' and 'value' or 'name' and 'ref'.");
                    } catch (XmlBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            } else {
                try {
                    throw new XmlBeanReaderException("Xml Reader error: An 'attribute' was not recognized in the 'bean' " + this.id +".");
                } catch (XmlBeanReaderException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }

    /**
     * The method tells the annotationsBeanReader to read a specific class.
     *
     * @param beanElement
     */
    private void readAnnotationsStatementFound(Element beanElement) {
        AnnotationsBeanReader annotationsBeanReader = new AnnotationsBeanReader(super.beanCreator);
        NodeList nodeList = beanElement.getElementsByTagName("annotationsClasses");
        if (nodeList.getLength() > 1) {
            try {
                throw new XmlBeanReaderException("Xml Reader error: The 'annotationClasses' was not recognized. It has more than a definition.");
            } catch (XmlBeanReaderException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else if(nodeList.getLength() != 0){
            Element annotationsElement = (Element) nodeList.item(0);
            nodeList = annotationsElement.getElementsByTagName("class");
            for(int i=0; i<nodeList.getLength(); ++i){
                Node node = nodeList.item(i); //Se obtiene el nodo actual

                if (node.getNodeType() == Node.ELEMENT_NODE) { //Si es un nodo elemento
                    Element element = (Element) node; //Se convierte el nodo en un elemento
                    if (!(element.getAttribute("path").equals(""))) {
                        annotationsBeanReader.readBeans(element.getAttribute("path"));
                    } else {
                        try {
                            throw new XmlBeanReaderException("Xml Reader error: A class in 'annotationClasses' does not have a 'path'");
                        } catch (XmlBeanReaderException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                        System.out.println();
                    }
                } else {
                    try {
                        throw new XmlBeanReaderException("Xml Reader error: A 'class' in 'annotationClasses' was not recognized");
                    } catch (XmlBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }
        /*String className = beanElement.getAttribute("annotationsClass");
        //System.out.println(className);
        AnnotationsBeanReader annotationsBeanReader = new AnnotationsBeanReader(super.getBeanFactory(), super.getBeanCreator());
        annotationsBeanReader.readBeans(className);*/
    }

/*    public static void main(String[] args) {
        //Prueba
        BeanCreator beanCreator = new BeanCreator();
        BeanReader xmlBeanReader = new XmlBeanReader(beanCreator);
        xmlBeanReader.readBeans("example.xml");
        *//*AnnotationsBeanReader annotationsBeanReader = new AnnotationsBeanReader(beanCreator);
        annotationsBeanReader.readBeans("com.ci1330.ecci.ucr.ac.cr.readers.TestingAnnotations");*//*
    }*/
}
