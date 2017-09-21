package com.ci1330.ecci.ucr.ac.cr.bean;

import com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Elias Calderon on 14/09/2017
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
    public BeanAttribute(String beanRef, BeanFactory beanFactory, Object value, Method setterMethod) {
        super(beanRef, beanFactory, value);
        this.setterMethod = setterMethod;
    }

    /**
     * Receives an object, an injects a dependency to the object.
     * The dependency is fetched by using the super.getInstance method
     * @param objectToInject
     * @return The object already injected
     */
    public Object injectDependency(Object objectToInject) {
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

        return objectToInject;
    }

    //----------------------------------------------------------------
    // Standard Setters and Getters section
    //----------------------------------------------------------------

    public void setSetterMethod(Method setterMethod) {
        this.setterMethod = setterMethod;
    }

}
