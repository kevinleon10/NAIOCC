package com.ci1330.ecci.ucr.ac.cr.bean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by kevinleon10 on 09/09/17.
 */
public class Bean {

    private String id;
    private Class classBean;
    private boolean lazyGen;
    private String autowire;
    private Scope beanScope;

    private Method initMethod;
    private Method destroyMethod;

    private Constructor beanConstructor;
    private ArrayList<Attribute> beanAttributeList;
    private Stack<Object> beanInstanceStack;

    public Bean () {
        this.beanInstanceStack = new Stack<Object>();
        this.beanAttributeList = new ArrayList<Attribute>();
    }

    /**
     * Initializes an instance of a bean, and appends the new instance to end of
     * the beanInstanceStack.
     */
    public void initializeNewBean () {
        if (this.beanScope == Scope.Singleton && this.beanInstanceStack.size() > 0) {
            System.err.println("Unvalid initialization: The Singleton Bean has already been initialized.");
            System.exit(1);
        }

        Object currInstance = this.createNewBeanInstance();
        this.beanInstanceStack.push(currInstance);
    }

    /**
     * Creates an instance, by injecting the constructor, if any.
     * If there is no specified constructor, it uses the default one.
     * @return The new bean instance
     */
    public Object createNewBeanInstance() {
        Object currInstance = null;
        if (this.beanConstructor == null) {
            try {
                currInstance = classBean.newInstance();
            } catch (InstantiationException e) {
                System.err.println("Instantiation Error: There was an exception trying to instantiate the bean.");
                e.printStackTrace();
                System.exit(1);
            } catch (IllegalAccessException e) {
                System.err.println("Instantiation Error: There was an exception trying to access the instance bean.");
                e.printStackTrace();
                System.exit(1);
            }
        }
        else {
            //currInstance = beanConstructor.inject(currInstance);
        }

        return currInstance;
    }

    public void injectDependencies () {
        for (Attribute currAttribute : beanAttributeList) {
            //currAttribute = currAttribute.inject()
        }
    }

    /*public Object getInstance () {
        this.beanInstanceStack.get()
    }*/

    //----------------------------------------------------------------
    // Standard Setters and Getters section
    //----------------------------------------------------------------
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClassBean(Class classBean) {
        this.classBean = classBean;
    }

    public void setInitMethod(Method initMethod) {
        this.initMethod = initMethod;
    } {}

    public void setDestroyMethod(Method destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    public boolean isLazyGen() {
        return lazyGen;
    }

    public void setLazyGen(boolean lazyGen) {
        this.lazyGen = lazyGen;
    }

    public void setAutowire(String autowire) {
        this.autowire = autowire;
    }

    public void setBeanScope(Scope beanScope) {
        this.beanScope = beanScope;
    }

    public void setBeanConstructor(Constructor beanConstructor) {
        this.beanConstructor = beanConstructor;
    }

   /* public void setBeanAttributeList(List<Attribute> beanAttributeList) {
        this.beanAttributeList = beanAttributeList;
    }*/

}
