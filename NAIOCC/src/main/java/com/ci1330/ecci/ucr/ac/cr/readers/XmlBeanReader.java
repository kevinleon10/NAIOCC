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
    private String defaultInitMethod;
    private String defaultDestroyMethod;
    private String currID;

    /**
     * Constructor that inits the annotations reader
     */
    public XmlBeanReader(BeanFactory beanFactory){
        super(beanFactory);
        this.beanFactory = beanFactory;
        this.defaultInitMethod = null;
        this.defaultDestroyMethod = null;
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
            e.printStackTrace();
            System.exit(1);
        }

        Document doc = null;
        try {
            doc = dBuilder.parse(fXmlFile); //Se parsea el doc creado
        } catch (SAXException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        //Normalize document
        doc.getDocumentElement().normalize();

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
        this.readAnnotationsStatement(rootElement);
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
                this.defaultInitMethod = rootElement.getAttribute("init");
            }
            if (rootElement.hasAttribute("destroy")) { //Si tiene destroy
                this.defaultDestroyMethod = rootElement.getAttribute("destroy");
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

            this.currID = beanElement.getAttribute("id");
            String className = beanElement.getAttribute("class");
            String initMethod, destroyMethod;

            if (beanElement.hasAttribute("init")) {
                initMethod = beanElement.getAttribute("init");
            } else {
                initMethod = this.defaultInitMethod;
            }

            if (beanElement.hasAttribute("destroy")) {
                destroyMethod = beanElement.getAttribute("destroy");
            } else {
                destroyMethod = this.defaultDestroyMethod;
            }



            AutowireEnum autowire;
            Scope scope;

            String scopeString = beanElement.getAttribute("scope");
            if(scopeString.equals("")){
                scopeString = "Singleton";
            }

            String autowireString = beanElement.getAttribute("autowire");
            if(autowireString.equals("")){
                autowireString = "none";
            }

            String lazyGen = beanElement.getAttribute("lazy-generation");
            if ( (autowireString.equals("byName") || autowireString.equals("byType") || autowireString.equals("none") || autowireString.equals("constructor")) &&
                    (lazyGen.equals("true") || lazyGen.equals("false") || lazyGen.equals("")) &&
                    (scopeString.equals("Singleton") || scopeString.equals("Prototype")) ) { //Reviso si pasan los requisitos

                scope = Scope.Singleton;
                autowire = AutowireEnum.none;

                //If prototype wasn't specified it is assumed to be Singleton
                if (scopeString.equals("Prototype")) {
                    scope = Scope.Prototype;
                }

                //If none of those was specified, it is assumed to be none
                if (autowireString.equals("byName")){
                    autowire = AutowireEnum.byName;
                } else if(autowireString.equals("byType")){
                    autowire = AutowireEnum.byType;
                } else if(autowireString.equals("constructor")){
                    autowire = AutowireEnum.constructor;
                }

                boolean lazyGeneration = lazyGen.equals("true");

                this.beanCreator.createBean(this.currID, className, scope, initMethod, destroyMethod, lazyGeneration, autowire);

            } else {
                try {
                    throw new XmlBeanReaderException("Xml Reader error: 'scope', 'lazy-generation' or 'autowire' were not recognized in the 'bean' "+this.currID +". It is misspelled.");
                } catch (XmlBeanReaderException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        } else {
            try {
                throw new XmlBeanReaderException("Xml Reader error: A 'bean' does not have tags for 'id', 'class' or both.");
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
        NodeList constructorList = beanElement.getElementsByTagName("constructor");
        if (constructorList.getLength() > 1) {
            try {
                throw new XmlBeanReaderException("Xml Reader error: Multiple constructors tags in bean " + this.currID + ".");
            } catch (XmlBeanReaderException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else if (constructorList.getLength() != 0) {

            Element constructorElement = (Element) constructorList.item(0);
            NodeList constructorArgs = constructorElement.getElementsByTagName("param");

            for (int index = 0; index < constructorArgs.getLength(); index++) { //Se itera sobre cada attribute
                Node parameterNode = constructorArgs.item(index); //Se obtiene el nodo actual

                if (parameterNode.getNodeType() == Node.ELEMENT_NODE) { //Si es un nodo elemento
                    Element parameterElement = (Element) parameterNode; //Se convierte el nodo en un elemento

                    if ((parameterElement.hasAttribute("value") && !(parameterElement.hasAttribute("ref"))) ||
                            (parameterElement.hasAttribute("ref") && !(parameterElement.hasAttribute("value")))) {

                        String type = parameterElement.getAttribute("type");

                        int argIndex = -1;
                        try {
                            if ( !(parameterElement.getAttribute("index").equals("")) ) {
                                argIndex = Integer.parseInt(parameterElement.getAttribute("index"));
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }

                        String value = parameterElement.getAttribute("value");
                        if (value.equals("")) {
                            value = null;
                        }

                        String ref = parameterElement.getAttribute("ref");
                        if (ref.equals("")) {
                            ref = null;
                        }

                        this.beanCreator.registerConstructorParameter(type, argIndex, value, ref);

                    } else {
                        try {
                            throw new XmlBeanReaderException("Xml Reader error: A 'param' has both ref and value, or neither of them, in bean " + this.currID + ".");
                        } catch (XmlBeanReaderException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                        System.out.println();
                    }
                } else {
                    try {
                        throw new XmlBeanReaderException("Xml Reader error: A 'param' was not recognized in the 'bean' " + this.currID + ".");
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

        NodeList attributeList = beanElement.getElementsByTagName("attribute");
        for (int index = 0; index < attributeList.getLength(); index++) { //Se itera sobre cada attribute
            Node attributeNode = attributeList.item(index); //Se obtiene el nodo actual

            if (attributeNode.getNodeType() == Node.ELEMENT_NODE) { //Si es un nodo elemento

                Element attributeElement = (Element) attributeNode;
                if ((attributeElement.hasAttribute("name") && attributeElement.hasAttribute("value") && !(attributeElement.hasAttribute("ref"))) ||
                        (attributeElement.hasAttribute("name") && attributeElement.hasAttribute("ref") && !(attributeElement.hasAttribute("value")))) {

                    String name = attributeElement.getAttribute("name");
                    if (name.equals("")) {
                        try {
                            throw new XmlBeanReaderException("Xml Reader error: An 'attribute' has a null name in bean "+this.currID + ".");
                        } catch (XmlBeanReaderException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                    }

                    String value = attributeElement.getAttribute("value");
                    if (value.equals("")) {
                        value = null;
                    }

                    String beanRef = attributeElement.getAttribute("ref");
                    if (beanRef.equals("")) {
                        beanRef = null;
                    }

                    this.beanCreator.registerSetter(name, value, beanRef);

                } else {
                    try {
                        throw new XmlBeanReaderException("Xml Reader error: The 'attribute' must have 'name' and 'value' or 'name' and 'ref' in bean "+this.currID + ".");
                    } catch (XmlBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }

            } else {
                try {
                    throw new XmlBeanReaderException("Xml Reader error: An 'attribute' was not recognized in the 'bean' " + this.currID +".");
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
    private void readAnnotationsStatement(Element beanElement) {

        NodeList annotationsList = beanElement.getElementsByTagName("annotationsClasses");

        if (annotationsList.getLength() > 1) {
            try {
                throw new XmlBeanReaderException("Xml Reader error: 'annotationClasses' has more than one definition.");
            } catch (XmlBeanReaderException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else if(annotationsList.getLength() != 0){

            AnnotationsBeanReader annotationsBeanReader = new AnnotationsBeanReader(super.beanCreator);
            Element annotationsElement = (Element) annotationsList.item(0);
            NodeList classList = annotationsElement.getElementsByTagName("class");

            for(int index = 0; index < classList.getLength(); ++index){
                Node classNode = classList.item(index); //Se obtiene el nodo actual

                if (classNode.getNodeType() == Node.ELEMENT_NODE) { //Si es un nodo elemento
                    Element classElement = (Element) classNode; //Se convierte el nodo en un elemento

                    if (!(classElement.getAttribute("path").equals(""))) {

                        annotationsBeanReader.readBeans(classElement.getAttribute("path"));

                    } else {

                        try {
                            throw new XmlBeanReaderException("Xml Reader error: A class in 'annotationClasses' doesn't have a 'path'");
                        } catch (XmlBeanReaderException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }

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
    }

}
