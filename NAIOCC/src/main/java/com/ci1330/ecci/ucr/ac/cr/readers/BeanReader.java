package com.ci1330.ecci.ucr.ac.cr.readers;

import com.ci1330.ecci.ucr.ac.cr.bean.AutowireEnum;
import com.ci1330.ecci.ucr.ac.cr.bean.Scope;
import com.ci1330.ecci.ucr.ac.cr.exception.XmlBeanReaderException;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanCreator;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 13/09/2017
 * The father class for a reader, defines the {@link BeanCreator} to use and the method, readBeans
 */
public class BeanReader{

    /**
     * Object used to create the beans
     */
    protected BeanCreator beanCreator;

    /**
     * General constructor that initializes the creator
     * @param beanFactory the factory that the creator will use
     */
    public BeanReader (BeanFactory beanFactory) {
        this.beanCreator = new BeanCreator(beanFactory);
    }

    /**
     * This constructor receives the bean creator
     * @param beanCreator the creator to use
     */
    public BeanReader (BeanCreator beanCreator) {
        this.beanCreator = beanCreator;
    }

    /**
     * Abstract method, that indicates the name of the input to read
     * @param inputName the name of the configuration container
     */
    public void readBeans (String inputName) {}

    /**
     * Determines which type of {@link AutowireEnum} is entered, if not found, throws an exception and exits.
     * Atomic autowiring, only accepts byName, byType or none.
     * @param atomic_autowireString the String to match with a type of {@link AutowireEnum}
     * @return the respective {@link AutowireEnum}
     */
    protected AutowireEnum determineAtomic_Autowire (String atomic_autowireString) {

        final String byNameString = "byname";
        final String bytypeString = "bytype";
        final String noneString = "none";

        AutowireEnum atomic_autowire = null;

        switch (atomic_autowireString) {
            case byNameString:
                atomic_autowire = AutowireEnum.byName;
                break;
            case bytypeString:
                atomic_autowire = AutowireEnum.byType;
                break;
            case noneString:
                atomic_autowire = AutowireEnum.none;
                break;
            default:
                try {
                    throw new XmlBeanReaderException("XML Reader Error: The value for atomic-autowire '" + atomic_autowireString + "' was not recognized.");
                } catch (XmlBeanReaderException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
        }

        return atomic_autowire;
    }

    /**
     * Determines which type of {@link AutowireEnum} is entered, if not found, throws an exception and exits
     * @param autowireString the String to match with a type of {@link AutowireEnum}
     * @return the respective {@link AutowireEnum}
     */
    protected AutowireEnum determineClass_Autowire (String autowireString) {
        final String byNameString = "byname";
        final String bytypeString = "bytype";
        final String byConstructorString = "constructor";
        final String noneString = "none";

        AutowireEnum autowire = null;

        //If none of those was specified, the system throws an exception
        switch (autowireString) {
            case byNameString:
                autowire = AutowireEnum.byName;
                break;
            case bytypeString:
                autowire = AutowireEnum.byType;
                break;
            case byConstructorString:
                autowire = AutowireEnum.constructor;
                break;
            case noneString:
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

        return autowire;
    }

    /**
     * Determines which type of {@link Scope} is entered, if not found, throws an exception and exits
     * @param scopeString the String to match with a type of {@link Scope}
     * @return the respective {@link Scope}
     */
    protected Scope determineScope (String scopeString) {
        final String singletonString = "singleton";
        final String prototypeString = "prototype";
        Scope scope = null;

        //If prototype wasn't specified, the system throws an exception
        switch (scopeString) {
            case prototypeString:
                scope = Scope.Prototype;
                break;
            case singletonString:
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

        return scope;
    }

    /**
     * Determines which value of lazy generation is entered, if not found, throws an exception and exits
     * @param lazyGenString the String to match with true or false
     * @return a boolean indicating the lazy generation value
     */
    protected Boolean determineLazyGen (String lazyGenString) {
        final String trueString = "true";
        final String falseString = "false";

        Boolean lazyGeneration = false;

        //If none of those was specified, the system throws an exception
        switch (lazyGenString) {
            case trueString:
                lazyGeneration = true;
                break;
            case falseString:
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
        return lazyGeneration;
    }

}