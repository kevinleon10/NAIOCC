package com.ci1330.ecci.ucr.ac.cr.readers;

/**
 * Created by kevinleon10 on 12/09/17.
 */

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import com.ci1330.ecci.ucr.ac.cr.bean.Scope;
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
    private BeanCreator beanCreator;
    private AnnotationsBeanReader annotationsBeanReader;
    private String initMethod;
    private String destroyMethod;

    /**
     * Constructor that inits the annotations reader
     */
    public XmlBeanReader(BeanCreator beanCreator) {
        super();
        this.annotationsBeanReader = new AnnotationsBeanReader();
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

            if ((node.getNodeType() == Node.ELEMENT_NODE) && node.getNodeName().equals("bean")) { //Si es un nodo elemento y si es un bean

                Element element = (Element) node;
                this.readBeanProperties(element);
                this.readBeanConstructor(element);
                this.readBeanAttributes(element);
            }
            else{
                System.out.println("Se espera leer un 'bean' del XML, no un " + node.getNodeName());
                System.exit(1);
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
        if (rootElement.getTagName().equals("beans")) { //Si es un bean
            if(rootElement.hasAttribute("init")){ //Si tiene init
                this.initMethod = rootElement.getAttribute("init");
            }
            if(rootElement.hasAttribute("destroy")){ //Si tiene destroy
                this.destroyMethod = rootElement.getAttribute("destroy");
            }
        }
        else {
            System.out.println("Se espera leer 'beans'del XML, no " + rootElement.getTagName());
            System.exit(1);
        }
        return rootElement;
    }

    /**
     * Reads the properties of a bean from the bean xml node
     *
     * @param beanElement
     */
    private void readBeanProperties(Element beanElement) {

        if(beanElement.hasAttribute("id") && beanElement.hasAttribute("class")){
            String id = beanElement.getAttribute("id");
            String className = beanElement.getAttribute("class");
            if(beanElement.hasAttribute("init")){
                this.initMethod = beanElement.getAttribute("init");
            }
            if(beanElement.hasAttribute("destroy")){
                this.destroyMethod = beanElement.getAttribute("destroy");
            }
            String autowire = beanElement.getAttribute("autowire");
            String scope = beanElement.getAttribute("scope");
            String lazyGen = beanElement.getAttribute("lazy-generation");
            if((autowire.equals("byName") || autowire.equals("byType") || autowire.equals("")) &&
                    (lazyGen.equals("true") || lazyGen.equals("false") || lazyGen.equals(""))){ //Reviso si pasan los requisitos
                boolean lazyGeneration = lazyGen.equals("true");
                if(scope.equals("Prototype")){
                    this.beanCreator.createBean(id, className, Scope.Prototype, initMethod, destroyMethod, lazyGeneration, autowire);
                }
                else if(scope.equals("Singleton") || scope.equals("")){
                    this.beanCreator.createBean(id, className, Scope.Singleton, initMethod, destroyMethod, lazyGeneration, autowire);
                }
                else{
                    System.out.println("El 'scope' del Bean debe ser Protoype o Singleton");
                    System.exit(1);
                }

                //Se ven los resultados
                System.out.println("\nId del Bean: " + id); //Obtengo los atributos del bean
                System.out.println("Class del Bean: " + className);
                System.out.println("Scope del Bean: " + scope);
                System.out.println("Metodo init del Bean: " + initMethod);
                System.out.println("Metodo destroy del Bean: " + destroyMethod);
                System.out.println("Lazy-generation: " + lazyGen);
                System.out.println("Autowire del Bean: " + autowire);
            }
            else{
                System.out.println("El valor de autowire o de lazyGen no es reconocido");
                System.exit(1);
            }
        }
        else{
            System.out.println("El Bean debe poseer 'id' y 'class'");
        }
    }

    /**
     * Reads an attribute of a bean from the attribute xml node
     *
     * @param attributeElement
     */
    private void readBeanAttributes(Element attributeElement) {
        NodeList nodeList = attributeElement.getElementsByTagName("attribute");
        for (int index = 0; index < nodeList.getLength(); index++) { //Se itera sobre cada attribute

            Node node = nodeList.item(index); //Se obtiene el nodo actual
            System.out.println("Attributes del Bean:");

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
    private void readBeanConstructor(Element constructorElement) {
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
        BeanReader xmlBeanReader = new XmlBeanReader(beanCreator);
        xmlBeanReader.readBeans("example.xml");
    }
}
