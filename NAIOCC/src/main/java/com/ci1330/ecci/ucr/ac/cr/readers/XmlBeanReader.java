package com.ci1330.ecci.ucr.ac.cr.readers;

/**
 * Created by kevinleon10 on 12/09/17.
 */

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import com.ci1330.ecci.ucr.ac.cr.annotations.Autowire;
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
    private String defaultInitMethod; //The init method
    private String defaultDestroyMethod; //The destroy method
    private String currID; //The bean ID

    /**
     * Constructor that inits the annotations reader
     */
    public XmlBeanReader(BeanFactory beanFactory){
        super(beanFactory);
    }

    /**
     * Receives the name of the XML and creates the root
     *
     * @param inputName
     */
    public void readBeans(String inputName) {

        File fXmlFile = new File(inputName);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Document doc = null;
        try {
            doc = dBuilder.parse(fXmlFile);
        } catch (SAXException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        doc.getDocumentElement().normalize();
        Element rootElement = this.readRoot(doc);

        //Get all the beans in the XML
        NodeList nodeList = rootElement.getElementsByTagName("bean");

        //Travel by every bean
        for (int index = 0; index < nodeList.getLength(); index++) {

            Node node = nodeList.item(index);

            //Check if it is an Element
            if ((node.getNodeType() == Node.ELEMENT_NODE)) {

                Element beanElement = (Element) node;
                this.readBeanProperties(beanElement);
                this.readBeanConstructor(beanElement);
                this.readBeanAttributes(beanElement);
                super.beanCreator.addBeanToContainer();

            } else {
                try {
                    throw new XmlBeanReaderException("Xml Reader Error: A 'bean' was not recognized.");
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
     * @return rootElement
     */
    private Element readRoot(Document xmlRootFile) {
        Element rootElement = xmlRootFile.getDocumentElement();
        //Check if there is a correct root
        if (rootElement.getTagName().equals("beans")) {
            //Check if there is an init property in the root
            if (rootElement.hasAttribute("init")) {
                //Check if there is an init method in the root
                if (!rootElement.getAttribute("init").equals("")) {
                    this.defaultInitMethod = rootElement.getAttribute("init");
                }
            }
            //Check if there is a destroy property in the root
            if (rootElement.hasAttribute("destroy")) {
                //Check if there is a destroy method
                if (!rootElement.getAttribute("destroy").equals("")) {
                    this.defaultDestroyMethod = rootElement.getAttribute("destroy");
                }
            }
        } else {
            try {
                throw new XmlBeanReaderException("Xml Reader Error: The root of the XML document is " + rootElement.getTagName() + " instead of 'beans'.");
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

        //Check if the bean has both ID and class
        if (beanElement.hasAttribute("id") && beanElement.hasAttribute("class")) {

            this.currID = beanElement.getAttribute("id");
            String className = beanElement.getAttribute("class");
            String initMethod = null;
            String destroyMethod = null;

            //Check if there is an init property in the current bean
            if (beanElement.hasAttribute("init")) {
                //Check if there is an init method
                if (!beanElement.getAttribute("init").equals("")) {
                    initMethod = beanElement.getAttribute("init");
                }
            } else {
                //If not, put the init method as the default one
                initMethod = this.defaultInitMethod;
            }

            //Check if there is an init property in the current bean
            if (beanElement.hasAttribute("destroy")) {
                //Check if there is a destroy method
                if (!beanElement.getAttribute("destroy").equals("")) {
                    destroyMethod = beanElement.getAttribute("destroy");
                }
            } else {
                //If not, put the destroy method as the default one
                destroyMethod = this.defaultDestroyMethod;
            }

            //-----------------------------------------------------------------------------------

            //Check the scope value
            String scopeString = beanElement.getAttribute("scope");
            if (scopeString.equals("")) {
                scopeString = "singleton";
            }
            scopeString = scopeString.toLowerCase();

            //Check the autowire value
            String autowireString = beanElement.getAttribute("autowire");
            if (autowireString.equals("")) {
                autowireString = "none";
            }
            autowireString = autowireString.toLowerCase();

            //Get the lazy-generation
            String lazyGenString = beanElement.getAttribute("lazy-generation");
            if (lazyGenString.equals("")) {
                lazyGenString = "false";
            }
            lazyGenString = lazyGenString.toLowerCase();

            //-----------------------------------------------------------------------------------

            AutowireEnum autowire = null;
            Scope scope = null;
            boolean lazyGeneration = false;

            //If prototype wasn't specified it is assumed to be Singleton
            switch (scopeString) {
                case "prototype":
                    scope = Scope.Prototype;
                    break;
                case "singleton":
                    scope = Scope.Singleton;
                    break;
                default:
                    try {
                        throw new XmlBeanReaderException("XML Reader Error: The value for scope '" + scopeString + "' was not recognized.");
                    } catch (XmlBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
            }

            //If none of those was specified, it is assumed to be none
            switch (autowireString) {
                case "byname":
                    autowire = AutowireEnum.byName;
                    break;
                case "bytype":
                    autowire = AutowireEnum.byType;
                    break;
                case "constructor":
                    autowire = AutowireEnum.constructor;
                    break;
                case "none":
                    autowire = AutowireEnum.none;
                    break;
                default:
                    try {
                        throw new XmlBeanReaderException("XML Reader Error: The value for autowire '" + autowireString + "' was not recognized.");
                    } catch (XmlBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
            }

            switch (lazyGenString) {
                case "true":
                    lazyGeneration = true;
                    break;
                case "false":
                    lazyGeneration = false;
                    break;
                default:
                    try {
                        throw new XmlBeanReaderException("XML Reader Error: The value for lazy generation '" + lazyGenString + "' was not recognized.");
                    } catch (XmlBeanReaderException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
            }

            this.beanCreator.createBean(this.currID, className, scope, initMethod, destroyMethod, lazyGeneration, autowire);

        } //if (beanElement.hasAttribute("id") && beanElement.hasAttribute("class"))
        else {
            try {
                throw new XmlBeanReaderException("Xml Reader error: ID and Class value for all tags must be entered.");
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
        //Get all the constructor in the current bean
        NodeList constructorList = beanElement.getElementsByTagName("constructor");

        //Check if there is more than a constructor definition
        if (constructorList.getLength() > 1) {
            try {
                throw new XmlBeanReaderException("Xml Reader error: Multiple constructors tags in bean " + this.currID + ".");
            } catch (XmlBeanReaderException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else if (constructorList.getLength() > 0) {

            Element constructorElement = (Element) constructorList.item(0);
            NodeList constructorArgs = constructorElement.getElementsByTagName("param");

            //Travel every param
            for (int index = 0; index < constructorArgs.getLength(); index++) {
                Node parameterNode = constructorArgs.item(index);

                //Check if it is an Element so we can cast it
                if (parameterNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element parameterElement = (Element) parameterNode;

                    final boolean autowireByTypeCombination = parameterElement.hasAttribute("type")  && !(parameterElement.hasAttribute("atomic-autowire"))
                            && !(parameterElement.hasAttribute("ref")) && !(parameterElement.hasAttribute("value"));

                    final boolean autowireByNameCombination = parameterElement.hasAttribute("ref")  && !(parameterElement.hasAttribute("atomic-autowire"))
                            && !(parameterElement.hasAttribute("type")) && !(parameterElement.hasAttribute("value"));

                    final boolean normalCombination = (parameterElement.hasAttribute("type") && parameterElement.hasAttribute("ref") && !(parameterElement.hasAttribute("value")))
                            || (parameterElement.hasAttribute("type") &&  parameterElement.hasAttribute("value") && !(parameterElement.hasAttribute("value")));

                    //Check if there is value or ref
                    //Or if it only has type, in that case, the autowire byType will take action
                    if ( autowireByTypeCombination || autowireByNameCombination || normalCombination ) {

                        int argIndex = -1;
                        try {
                            //Tries to get the index
                            if ( !(parameterElement.getAttribute("index").equals("")) ) {
                                argIndex = Integer.parseInt(parameterElement.getAttribute("index"));
                            } else {
                                throw new XmlBeanReaderException("XML Reader Error: An invalid value was entered in index tag.");
                            }
                        } catch (NumberFormatException | XmlBeanReaderException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }

                        String type = parameterElement.getAttribute("type");
                        if (type.equals("")) {
                            type = null;
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
                            throw new XmlBeanReaderException("Xml Reader error: A 'param' has an invalid tag combination, in bean " + this.currID + ".");
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
        }

    }

    /**
     * Reads an attribute of a bean from the attribute xml node
     *
     * @param beanElement
     */
    private void readBeanAttributes(Element beanElement) {

        NodeList attributeList = beanElement.getElementsByTagName("attribute");
        for (int index = 0; index < attributeList.getLength(); index++) {
            Node attributeNode = attributeList.item(index);

            //Check if it is an Element
            if (attributeNode.getNodeType() == Node.ELEMENT_NODE) {

                Element attributeElement = (Element) attributeNode;

                //Check if there is a value or ref
                if ((attributeElement.hasAttribute("name") && !attributeElement.hasAttribute("value") && !(attributeElement.hasAttribute("ref"))) ||
                        (attributeElement.hasAttribute("name") && attributeElement.hasAttribute("value") && !(attributeElement.hasAttribute("ref"))) ||
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

        //Get all the annotationsClasses in the root
        NodeList annotationsList = beanElement.getElementsByTagName("annotationsClasses");

        //Check if there is more than a annotationClasses definition
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

            //Travel by every class
            for(int index = 0; index < classList.getLength(); ++index){
                Node classNode = classList.item(index);

                //Check if it is an Element
                if (classNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element classElement = (Element) classNode;

                    //If the annotation has a path
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
