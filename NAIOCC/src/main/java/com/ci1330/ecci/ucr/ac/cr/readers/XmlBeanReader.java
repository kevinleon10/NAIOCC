package com.ci1330.ecci.ucr.ac.cr.readers;

/**
 * Created by kevinleon10 on 12/09/17.
 */

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import com.ci1330.ecci.ucr.ac.cr.bean.Scope;
import com.ci1330.ecci.ucr.ac.cr.exception.XmlBeanReaderException;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanCreator;
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
    private BeanCreator beanCreator;
    private AnnotationsBeanReader annotationsBeanReader;
    private String initMethod;
    private String destroyMethod;

    /**
     * Constructor that inits the annotations reader
     */
    public XmlBeanReader(BeanCreator beanCreator) {
        super();
        this.beanCreator = beanCreator;
        this.initMethod = "";
        this.destroyMethod = "";
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
            } else {
                try {
                    throw new XmlBeanReaderException("Un 'bean' no fue reconocido");
                } catch (XmlBeanReaderException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }

    /**
     * Starts reading the root of the xml
     *
     * @param xmlRootFile
     */
    private Element readRoot(Document xmlRootFile) {
        Element rootElement = xmlRootFile.getDocumentElement();
        if (rootElement.getTagName().equals("beans")) { //Si son beans
            if (rootElement.hasAttribute("annotationsClass")) {
                this.readAnnotationsStatementFound(rootElement);
            }
            if (rootElement.hasAttribute("init")) { //Si tiene init
                this.initMethod = rootElement.getAttribute("init");
            }
            if (rootElement.hasAttribute("destroy")) { //Si tiene destroy
                this.destroyMethod = rootElement.getAttribute("destroy");
            }
        } else {
            try {
                throw new XmlBeanReaderException("Se espera leer 'beans'del XML, no '" + rootElement.getTagName() + "'");
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
            String className = beanElement.getAttribute("class");
            if (beanElement.hasAttribute("init")) {
                this.initMethod = beanElement.getAttribute("init");
            }
            if (beanElement.hasAttribute("destroy")) {
                this.destroyMethod = beanElement.getAttribute("destroy");
            }
            String scopeS = beanElement.getAttribute("scope");
            String autowire = beanElement.getAttribute("autowire");
            if (autowire.equals("")) {
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
                boolean lazyGeneration = lazyGen.equals("true");
                this.beanCreator.createBean(id, className, scope, initMethod, destroyMethod, lazyGeneration, autowire);

                //Se ven los resultados
                /*System.out.println("\nId del Bean: " + id); //Obtengo los atributos del bean
                System.out.println("Class del Bean: " + className);
                System.out.println("Scope del Bean: " + scope);
                System.out.println("Metodo init del Bean: " + initMethod);
                System.out.println("Metodo destroy del Bean: " + destroyMethod);
                System.out.println("Lazy-generation: " + lazyGeneration);
                System.out.println("Autowire del Bean: " + autowire);*/
            } else {
                try {
                    throw new XmlBeanReaderException("El valor de 'autowire', 'lazy-generation' o 'scope' no es reconocido");
                } catch (XmlBeanReaderException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        } else {
            try {
                throw new XmlBeanReaderException("El 'bean' debe poseer 'id' y 'class'");
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
                throw new XmlBeanReaderException("El 'constructor' no fue reconocido, posee más de una definición");
            } catch (XmlBeanReaderException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else {
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
                        int index = Integer.parseInt(element.getAttribute("index"));
                        String value = element.getAttribute("value");
                        String ref = element.getAttribute("ref");
                        this.beanCreator.registerConstructor(type, index, "value", ref);

                        /*System.out.println("Tipo de parametro: " + type); //Obtengo los atributos del bean
                        System.out.println("Index del parametro: " + index);
                        System.out.println("Value del parametro: " + value);
                        System.out.println("Id del Bean referenciado: " + ref);*/
                    } else {
                        try {
                            throw new XmlBeanReaderException("El 'param' debe poseer value o ref, no ambos o ninguno");
                        } catch (XmlBeanReaderException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                        System.out.println();
                    }
                } else {
                    try {
                        throw new XmlBeanReaderException("Un 'param' no fue reconocido");
                    } catch (XmlBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }

            }
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
                    Object value = element.getAttribute("value");
                    String beanRef = element.getAttribute("ref");
                    this.beanCreator.registerSetter(name, value, beanRef);

                    /*System.out.println("Name del attribute: " + name); //Obtengo los atributos del bean
                    System.out.println("Value del attribute: " + value);
                    System.out.println("Id del Bean referenciado: " + beanRef);*/
                } else {
                    try {
                        throw new XmlBeanReaderException("El 'attribute' no fue reconocido, debe poseer nombre y valor o nombre y ref");
                    } catch (XmlBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            } else {
                try {
                    throw new XmlBeanReaderException("Un 'attribute' no fue reconocido");
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
     * @param rootElement
     */
    private void readAnnotationsStatementFound(Element rootElement) {
        String className = rootElement.getAttribute("annotationsClass");
        //System.out.println(className);
        AnnotationsBeanReader annotationsBeanReader = new AnnotationsBeanReader(beanCreator);
        annotationsBeanReader.readBeans(className);
    }

    public static void main(String[] args) {
        //Prueba
        BeanCreator beanCreator = new BeanCreator();
        BeanReader xmlBeanReader = new XmlBeanReader(beanCreator);
        xmlBeanReader.readBeans("example.xml");
        /*AnnotationsBeanReader annotationsBeanReader = new AnnotationsBeanReader(beanCreator);
        annotationsBeanReader.readBeans("com.ci1330.ecci.ucr.ac.cr.readers.TestingAnnotations");*/
    }
}
