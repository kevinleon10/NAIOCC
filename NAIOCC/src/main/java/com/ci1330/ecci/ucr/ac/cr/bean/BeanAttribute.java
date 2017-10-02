package com.ci1330.ecci.ucr.ac.cr.bean;

import com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 14/09/2017
 *
 * BeanAttribute class for NAIOCC Container.
 * Contains the Metadata of an attribute and manages the setter injection of a
 * dependency for a Bean.
 */
public class BeanAttribute extends BeanProperty {

    private Method setterMethod; //Used for invoking the respective class setter

    /**
     * Constructor of the class, initializes the class and super-class attributes.
     * @param beanRef init value for the super's beanRef attribute
     * @param beanFactory init value for the super's beanFactory attribute
     * @param value init value for the super's value attribute
     * @param setterMethod init value for the bean's setter method
     */
    public BeanAttribute(String beanRef, Class beanRefClass, BeanFactory beanFactory, Object value, AutowireEnum atomic_autowire, Method setterMethod) {
        super(beanRef, beanRefClass, beanFactory, value, atomic_autowire);
        this.setterMethod = setterMethod;
    }

    /**
     * Receives an object, an injects a dependency to the object.
     * The dependency is fetched by using the super.getInstance method
     * @param objectToInject The bean instance without injections
     */
    void injectDependency(Object objectToInject) {
        Object dependency = super.getInstance();
        try {
            this.setterMethod.invoke(objectToInject, dependency);

        } catch (IllegalAccessException e) {
            System.err.println("Setter Error: There was an exception trying to access the setter method for:\n"
                    + "\t" + this.setterMethod.toString() + ".");
            e.printStackTrace();
            System.exit(1);
        } catch (InvocationTargetException e) {
            System.err.println("Setter Error: There was an exception trying to invoke the setter method for:\n"
                    + "\t" + this.setterMethod.toString() + ".");
            e.printStackTrace();
            System.exit(1);
        }

    }

    //----------------------------------------------------------------
    // Standard Setters and Getters section
    //----------------------------------------------------------------

    public void setSetterMethod(Method setterMethod) {
        this.setterMethod = setterMethod;
    }

}
