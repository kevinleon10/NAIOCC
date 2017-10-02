package com.ci1330.ecci.ucr.ac.cr.readers;

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

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 * The reader is given a String, and then tries to map it with a XML file
 * and extract the metadata for the BeanCreator
 */
public class XmlBeanReader extends BeanReader {

    /**
     * The annotations reader is used if in the xml file, a read annotations
     * statement is found.
     */
    private String defaultInitMethod; //The init method
    private String defaultDestroyMethod; //The destroy method
    private String currID; //The bean ID

    //Init and destroy method tags
    private final String initTag = "init";
    private final String destroyTag = "destroy";

    //Bean properties tags
    private final String idTag = "id";
    private final String classTag = "class";
    private final String scopeTag = "scope";
    private final String autowireTag = "autowire";
    private final String lazyGenerationTag = "lazy-generation";

    //Constructor tags
    private final String constructorTag = "constructor";
    private final String paramTag = "param";
    private final String typeTag = "type";
    private final String indexTag = "index";

    //Constructor tags
    private final String nameTag = "name";

    //Constructor and Attribute tags
    private final String valueTag = "value";
    private final String beanRefTag = "ref";
    private final String atomic_autowireTag = "atomic-autowire";


    /**
     * Constructor, receives the {@link BeanFactory} that created him
     * @param beanFactory the father {@link BeanFactory}
     */
    public XmlBeanReader(BeanFactory beanFactory){
        super(beanFactory);
    }

    /**
     * Receives the name of the XML and creates the root
     * @param inputName the name of the XML file
     */
    @Override
    public void readBeans(String inputName) {

        final String beanTag = "bean";

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
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        doc.getDocumentElement().normalize();
        Element rootElement = this.readRoot(doc);

        //Get all the beans in the XML
        NodeList nodeList = rootElement.getElementsByTagName(beanTag);

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
     * @param xmlRootFile the root of the file
     * @return rootElement
     */
    private Element readRoot(Document xmlRootFile) {
        final String beansTag = "beans";

        Element rootElement = xmlRootFile.getDocumentElement();
        //Check if there is a correct root
        if (rootElement.getTagName().equals(beansTag)) {
            //Check if there is an init property in the root
            if (rootElement.hasAttribute(this.initTag)) {
                //Check if there is an init method in the root
                if (!rootElement.getAttribute(this.initTag).equals("")) {
                    this.defaultInitMethod = rootElement.getAttribute(this.initTag);
                }
            }
            //Check if there is a destroy property in the root
            if (rootElement.hasAttribute(this.destroyTag)) {
                //Check if there is a destroy method
                if (!rootElement.getAttribute(this.destroyTag).equals("")) {
                    this.defaultDestroyMethod = rootElement.getAttribute(this.destroyTag);
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
     * Reads the properties of a bean from the bean xml node, any invalid combination or value, throws an exception
     * and exits the program.
     * @param beanElement the XML element of a bean
     */
    private void readBeanProperties(Element beanElement) {

        final String defaultScope = "singleton";
        final String defaultAutowire = "none";
        final String defaultLazyGen = "false";

        //Check if the bean has both ID and class
        if (beanElement.hasAttribute(this.idTag) && beanElement.hasAttribute(this.classTag)) {

            this.currID = beanElement.getAttribute(this.idTag);
            String className = beanElement.getAttribute(this.classTag);
            String initMethod = null;
            String destroyMethod = null;

            //Check if there is an init property in the current bean
            if (beanElement.hasAttribute(this.initTag)) {
                //Check if there is an init method
                if (!beanElement.getAttribute(this.initTag).equals("")) {
                    initMethod = beanElement.getAttribute(this.initTag);
                }
            } else {
                //If not, put the init method as the default one
                initMethod = this.defaultInitMethod;
            }

            //Check if there is an init property in the current bean
            if (beanElement.hasAttribute(this.destroyTag)) {
                //Check if there is a destroy method
                if (!beanElement.getAttribute(this.destroyTag).equals("")) {
                    destroyMethod = beanElement.getAttribute(this.destroyTag);
                }
            } else {
                //If not, put the destroy method as the default one
                destroyMethod = this.defaultDestroyMethod;
            }

            //-----------------------------------------------------------------------------------

            //Check the scope value
            String scopeString = beanElement.getAttribute(this.scopeTag).toLowerCase();
            if (scopeString.equals("")) {
                scopeString = defaultScope;
            }

            //Check the autowire value
            String autowireString = beanElement.getAttribute(this.autowireTag).toLowerCase();
            if (autowireString.equals("")) {
                autowireString = defaultAutowire;
            }

            //Get the lazy-generation
            String lazyGenString = beanElement.getAttribute(this.lazyGenerationTag).toLowerCase();
            if (lazyGenString.equals("")) {
                lazyGenString = defaultLazyGen;
            }

            //-----------------------------------------------------------------------------------

            AutowireEnum autowire = super.determineClass_Autowire(autowireString);
            Scope scope = super.determineScope(scopeString);
            boolean lazyGeneration = super.determineLazyGen(lazyGenString);

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
     * Reads the constructor of a bean from the constructor xml node, any invalid combination or value, throws an exception
     * and exits the program.
     *
     * @param beanElement the XML Element for a bean
     */
    private void readBeanConstructor(Element beanElement) {

        //Get all the constructor in the current bean
        NodeList constructorList = beanElement.getElementsByTagName(this.constructorTag);

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
            NodeList constructorArgs = constructorElement.getElementsByTagName(this.paramTag);

            //Travel every param
            for (int index = 0; index < constructorArgs.getLength(); index++) {
                Node parameterNode = constructorArgs.item(index);

                //Check if it is an Element so we can cast it
                if (parameterNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element parameterElement = (Element) parameterNode;

                    //Combination of only type and atomic-autowire tag
                    final boolean autowireByTypeCombination = parameterElement.hasAttribute(this.typeTag)  && parameterElement.hasAttribute(this.atomic_autowireTag)
                            && !(parameterElement.hasAttribute(this.beanRefTag)) && !(parameterElement.hasAttribute(this.valueTag));

                    //Combination of only beanRef and atomic-autowire tag
                    final boolean autowireByNameCombination = parameterElement.hasAttribute(this.beanRefTag)  && parameterElement.hasAttribute(this.atomic_autowireTag)
                            && !(parameterElement.hasAttribute(this.typeTag)) && !(parameterElement.hasAttribute(this.valueTag));

                    //Combination of only type and beanRef
                    final boolean typeRefCombination = parameterElement.hasAttribute(this.typeTag) && parameterElement.hasAttribute(this.beanRefTag)
                                                        && !(parameterElement.hasAttribute(this.valueTag)) && !(parameterElement.hasAttribute(this.atomic_autowireTag));

                    //Combination of only type and value
                    final boolean typeValueCombination = parameterElement.hasAttribute(this.typeTag) &&  parameterElement.hasAttribute(this.valueTag)
                            && !(parameterElement.hasAttribute(this.beanRefTag)) && !(parameterElement.hasAttribute(this.atomic_autowireTag));

                    //Check if any combination matches
                    if ( autowireByTypeCombination || autowireByNameCombination || typeRefCombination || typeValueCombination ) {

                        int argIndex = -1;
                        try {
                            //Tries to get the index if it exists
                            if ( parameterElement.hasAttribute(this.indexTag)) {
                                if (!parameterElement.getAttribute(this.indexTag).equals("")) {
                                    argIndex = Integer.parseInt(parameterElement.getAttribute(this.indexTag));
                                } else {
                                    throw new XmlBeanReaderException("XML Reader Error: An invalid value was entered in index tag.");
                                }
                            }

                        } catch (NumberFormatException | XmlBeanReaderException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }

                        //If nothing was specified put it to null
                        String type = parameterElement.getAttribute(this.typeTag);
                        if (type.equals("")) {
                            type = null;
                        }

                        //If nothing was specified put it to null
                        String value = parameterElement.getAttribute(this.valueTag);
                        if (value.equals("")) {
                            value = null;
                        }

                        //If nothing was specified put it to null
                        String ref = parameterElement.getAttribute(this.beanRefTag);
                        if (ref.equals("")) {
                            ref = null;
                        }

                        //If nothing was specified put it to none
                        String atomic_autowireString = parameterElement.getAttribute(this.atomic_autowireTag).toLowerCase();
                        if (atomic_autowireString.equals("")) {
                            atomic_autowireString = "none";
                        }
                        AutowireEnum atomic_autowire = super.determineAtomic_Autowire(atomic_autowireString);

                        this.beanCreator.registerConstructorParameter(type, argIndex, value, ref, atomic_autowire);

                    } else {
                        try {
                            throw new XmlBeanReaderException("Xml Reader error: A 'param' has an invalid tag combination, in bean " + this.currID + ".");
                        } catch (XmlBeanReaderException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
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
     * Reads an attribute of a bean from the attribute xml node, any invalid combination or value, throws an exception
     * and exits the program.
     *
     * @param beanElement the XML Element for a bean.
     */
    private void readBeanAttributes(Element beanElement) {

        NodeList attributeList = beanElement.getElementsByTagName("attribute");
        for (int index = 0; index < attributeList.getLength(); index++) {
            Node attributeNode = attributeList.item(index);

            //Check if it is an Element
            if (attributeNode.getNodeType() == Node.ELEMENT_NODE) {

                Element attributeElement = (Element) attributeNode;

                //Combination of only name and Value tag
                final boolean nameValueCombination = attributeElement.hasAttribute(this.nameTag) && attributeElement.hasAttribute(this.valueTag)
                        && !(attributeElement.hasAttribute(this.beanRefTag));

                //Combination of only name and Ref tag
                final boolean nameRefCombination = attributeElement.hasAttribute(this.nameTag) && attributeElement.hasAttribute(this.beanRefTag)
                        && !(attributeElement.hasAttribute(this.valueTag));

                //Combination of only name and autowire tag
                final boolean atomicAutowireCombination = attributeElement.hasAttribute(this.nameTag) && attributeElement.hasAttribute(this.atomic_autowireTag)
                        && !(attributeElement.hasAttribute(this.beanRefTag)) && !(attributeElement.hasAttribute(this.valueTag));

                //Check if any combination matches
                if ( nameValueCombination || nameRefCombination || atomicAutowireCombination ) {

                    //If the name is empty, throw an exception
                    String name = attributeElement.getAttribute(this.nameTag);
                    if (name.equals("")) {
                        try {
                            throw new XmlBeanReaderException("Xml Reader error: An 'attribute' has a null name in bean "+this.currID + ".");
                        } catch (XmlBeanReaderException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                    }

                    //If the value is empty put it to null
                    String value = attributeElement.getAttribute(this.valueTag);
                    if (value.equals("")) {
                        value = null;
                    }

                    //If the value is empty put it to null
                    String beanRef = attributeElement.getAttribute(this.beanRefTag);
                    if (beanRef.equals("")) {
                        beanRef = null;
                    }

                    //If nothing was specified, put it to none
                    String atomic_autowireString = attributeElement.getAttribute(this.atomic_autowireTag).toLowerCase();
                    if (atomic_autowireString.equals("")) {
                        atomic_autowireString = "none";
                    }
                    AutowireEnum atomic_autowire = super.determineAtomic_Autowire(atomic_autowireString);

                    this.beanCreator.registerSetter(name, value, beanRef, atomic_autowire);

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
     * The method tells the annotationsBeanReader to read a specific class. If it has more than one tag, exits abnormally.
     *
     * @param beanElement the XML Element for a bean
     */
    private void readAnnotationsStatement(Element beanElement) {

        final String annotationClassesTag = "annotationsClasses";
        final String pathTag = "path";

        //Get all the annotationsClasses in the root
        NodeList annotationsList = beanElement.getElementsByTagName(annotationClassesTag);

        //Check if there is more than a annotationClasses definition
        if (annotationsList.getLength() > 1) {
            try {
                throw new XmlBeanReaderException("Xml Reader error: 'annotationClasses' has more than one definition.");
            } catch (XmlBeanReaderException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else if(annotationsList.getLength() > 0){

            //Create a new Annotations reader with the same creator of this factory.
            AnnotationsBeanReader annotationsBeanReader = new AnnotationsBeanReader(super.beanCreator);
            Element annotationsElement = (Element) annotationsList.item(0);
            NodeList classList = annotationsElement.getElementsByTagName(this.classTag);

            //Travel by every class
            for(int index = 0; index < classList.getLength(); ++index){
                Node classNode = classList.item(index);

                //Check if it is an Element
                if (classNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element classElement = (Element) classNode;

                    //If the annotation has a path
                    if (!(classElement.getAttribute(pathTag).equals(""))) {
                        annotationsBeanReader.readBeans(classElement.getAttribute(pathTag));

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
