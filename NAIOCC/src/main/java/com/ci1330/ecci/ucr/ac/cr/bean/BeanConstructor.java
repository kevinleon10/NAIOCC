package com.ci1330.ecci.ucr.ac.cr.bean;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 15/09/2017
 *
 * Bean Constructor for NAIOCC Container.
 * Contains the Metadata of a Bean's constructor, manages the constructor injection.
 */
public class BeanConstructor {
    private Constructor constructorMethod;
    private List<BeanParameter> beanParameterList;

    /**
     * Constructor of the class, initializes the Parameter list and sets the constructor method value.
     * @param constructorMethod init value for the construction method.
     */
    public BeanConstructor (Constructor constructorMethod) {
        this.constructorMethod = constructorMethod;
        this.beanParameterList = new ArrayList<>();
    }

    /**
     * Creates a new instance of a bean, with constructor injection.
     * @return The injected bean instance.
     */
    public Object newInstance() {
        Object[] parameterInstances = new Object[this.beanParameterList.size()];
        Object beanInstance = null;
        for (BeanParameter currBeanParameter : this.beanParameterList) {
            parameterInstances[currBeanParameter.getIndex()] = currBeanParameter.getInstance();
        }
        try {
            beanInstance = this.constructorMethod.newInstance(parameterInstances);

        } catch (InstantiationException e) {
            System.err.println("Construction Error: There was an exception trying to instantiate a bean with the constructor method for:\n"
                    +  "\t" + this.constructorMethod.toString() + ".");
            e.printStackTrace();
            System.exit(1);
        } catch (IllegalAccessException e) {
            System.err.println("Construction Error: There was an exception trying to access the constructor method for:\n"
                    +  "\t" + this.constructorMethod.toString() + ".");
            e.printStackTrace();
            System.exit(1);
        } catch (InvocationTargetException e) {
            System.err.println("Construction Error: There was an exception trying to invoke the constructor method for:\n"
                    + "\t" + this.constructorMethod.toString() + ".");
            e.printStackTrace();
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Construction Error: There was an exception trying to invoke the constructor method for:\n"
                    + "\t" + this.constructorMethod.toString() + "\n"
                    + " with " + parameterInstances[0].getClass() +".");
            e.printStackTrace();
            System.exit(1);
        }

        return beanInstance;
    }

    /**
     * Appends a bean parameter to the list.
     * @param beanParameter
     */
    public void append(BeanParameter beanParameter){
        this.beanParameterList.add(beanParameter);
    }
    //----------------------------------------------------------------
    // Standard Setters and Getters section
    //----------------------------------------------------------------

    public void setConstructorMethod(Constructor constructorMethod) {
        this.constructorMethod = constructorMethod;
    }

    public Constructor getConstructorMethod() {
        return constructorMethod;
    }

    public void setBeanParameterList(List<BeanParameter> beanParameterList) {
        this.beanParameterList = beanParameterList;
    }

    public List<BeanParameter> getBeanParameterList() {
        return beanParameterList;
    }

}
