package com.ci1330.ecci.ucr.ac.cr.bean;

import com.ci1330.ecci.ucr.ac.cr.factory.BeanConstructorModule;
import com.ci1330.ecci.ucr.ac.cr.factory.BeanFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Elias Calderon, Josue Leon, Kevin Leon
 * Date: 11/09/2017
 *
 * Bean class for NAIOCC Container.
 * Contains the Metadata of a Bean, manages the creation and destruction,
 * manages all the instances (if prototype), and the autowiring.
 */
public class Bean {

    private String id; //Uniquely identifies the bean
    private Class beanClass; //Used for different Java Reflection methods.
    private boolean lazyGen; //Flag used to indicate if the bean is lazy generated
    private AutowireEnum autowireEnum; //Indicates the type of autowiring the Bean uses.
    private Scope beanScope; //Indicates the scope of the Bean.

    private BeanFactory beanFactory;

    private Method initMethod; //Initialization method, called after the injection of dependencies.
    private Method destroyMethod; //Destroy method, called when the container is going to be destroyed.

    private BeanConstructor beanConstructor; //Class used for constructor-injections
    private List<BeanAttribute> beanAttributeList; //List of classes that are used for setter-injection

    /*The stack is used for keeping track of the different instances of a bean.
    The top bean instance is considered as the current one.*/
    private Stack<Object> beanInstanceStack;

    /**
     * Constructor of the class, initializes the Instances Stack and the BeanAttribute List.
     */
    public Bean (BeanFactory beanFactory) {
        this.beanInstanceStack = new Stack<>();
        this.beanAttributeList = new ArrayList<>();
        this.beanFactory = beanFactory;
    }

    /**
     * Initializes an instance of a bean, and appends the new instance to end of
     * the beanInstanceStack.
     */
    public void createNewInstance() {
        if (this.beanScope == Scope.Singleton && this.beanInstanceStack.size() > 0) {
            System.err.println("Invalid initialization: The Singleton Bean has already been initialized.");
            System.exit(1);
        }

        Object currInstance = this.newInstance();
        this.beanInstanceStack.push(currInstance);
    }

    /**
     * Autowires all the properties of the bean
     */
    public void autowire () {
        //Autowire by constructor or, Atomic-autowire all parameters and register the constructor
        if (this.beanConstructor != null) {
            List<BeanParameter> beanParameterList = this.beanConstructor.getBeanParameterList();
            if (beanParameterList.size() > 0) {
                //If the parameter list has parameters, they are autowired (if necessary) and the constructor is registered
                for (BeanParameter beanParameter : beanParameterList) {
                    beanParameter.autowireProperty();
                }
                BeanConstructorModule.registerConstructor(this);

            } else {
                //If there are no paramters, but the constructor isn't null, it's because the user indicated
                //autowire by constructor to a single constructor
                BeanAutowireModule.autowireSingleConstructor(this.beanConstructor, this.beanFactory, this.id);
            }
        }
        //Atomic-autowire all attributes
        for (BeanAttribute beanAttribute : this.beanAttributeList) {
            beanAttribute.autowireProperty();
        }

        //Class autowiring
        BeanAutowireModule.autowireBean(this);
    }

    /**
     * Checks if all the properties of the bean are correct
     */
    public void checkBeanProperties() {
        for (BeanAttribute beanAttribute : this.beanAttributeList) {
            beanAttribute.checkProperty();
        }

        if (this.beanConstructor != null) {
            for (BeanParameter beanParameter : this.beanConstructor.getBeanParameterList()) {
                beanParameter.checkProperty();
            }
        }
    }

    /**
     * Creates an instance, by injecting the constructor, if any.
     * If there is no specified constructor, it uses the default one.
     * @return The new bean instance
     */
    private Object newInstance() {
        Object currInstance = null;
        if (this.beanConstructor == null) {
            try {
                currInstance = this.beanClass.newInstance();

            } catch (InstantiationException e) {
                System.err.println("Instantiation Error: There was an exception trying to instantiate the bean " + this.beanClass.toString() + ".");
                e.printStackTrace();
                System.exit(1);
            } catch (IllegalAccessException e) {
                System.err.println("Instantiation Error: There was an exception trying to access the instance bean " + this.beanClass.toString() + ".");
                e.printStackTrace();
                System.exit(1);
            }
        }
        else {
            currInstance = this.beanConstructor.newInstance();
        }

        return currInstance;
    }

    /**
     * Make all the setter-injections by iterating the attribute list.
     * It pops the top of the stack, makes all the injections, and then
     * it pushes back to the stack.
     */
    public void injectDependencies () {
        Object currInstance = this.getInstance();
        for (BeanAttribute currBeanAttribute : this.beanAttributeList) {
            currBeanAttribute.injectDependency(currInstance);
        }
    }

    /**
     * Calls the initialization method for the current bean instance, if any.
     */
    public void initialize () {
        if (this.initMethod != null) {
            Object currInstance = this.getInstance();
            try {

                this.initMethod.invoke(currInstance);
            } catch (IllegalAccessException e) {
                System.err.println("Initialize Error: There was an exception trying to access the init method.");
                e.printStackTrace();
                System.exit(1);
            } catch (InvocationTargetException e) {
                System.err.println("Initialize Error: There was an exception trying to invoke the init method.");
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * Calls the destruction method for all the beans instances, if any, and leaves
     * the stack empty.
     */
    public void destroyAllInstances() {
        Object currInstance;
        while (!this.beanInstanceStack.empty()) {
            currInstance = this.beanInstanceStack.pop();
            if (this.destroyMethod != null) {
                try {

                    this.destroyMethod.invoke(currInstance);

                } catch (IllegalAccessException e) {
                    System.err.println("Destruction Error: There was an exception trying to access the destroyAllInstances method.");
                    e.printStackTrace();
                    System.exit(1);
                } catch (InvocationTargetException e) {
                    System.err.println("Destruction Error: There was an exception trying to invoke the destroyAllInstances method.");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }

    /**
     * Peeks the top of the stack.
     * @return Returns the current bean.
     */
    public Object getInstance () {
        if (this.beanInstanceStack.empty()) {
            return null;
        } else {
            return this.beanInstanceStack.peek();
        }
    }

    /**
     * Appends an attribute to the end of the attribute list.
     * @param beanAttributeToAppend bean attribte to apend
     */
    public void appendAttribute (BeanAttribute beanAttributeToAppend) {
        this.beanAttributeList.add(beanAttributeToAppend);
    }


    //----------------------------------------------------------------
    // Standard Setters and Getters section
    //----------------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public Class getBeanClass () {
        return beanClass;
    }

    public boolean isLazyGen() {
        return lazyGen;
    }

    public void setLazyGen(boolean lazyGen) {
        this.lazyGen = lazyGen;
    }

    public AutowireEnum getAutowireEnum() {
        return autowireEnum;
    }

    public void setAutowireEnum(AutowireEnum autowireEnum) {
        this.autowireEnum = autowireEnum;
    }

    public void setBeanScope(Scope beanScope) {
        this.beanScope = beanScope;
    }

    public Scope getBeanScope() {
        return beanScope;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setInitMethod(Method initMethod) {
        this.initMethod = initMethod;
    }

    public void setDestroyMethod(Method destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    public void setBeanConstructor(BeanConstructor beanConstructor) {
        this.beanConstructor = beanConstructor;
    }

    public List<BeanAttribute> getBeanAttributeList () {
        return beanAttributeList;
    }

    public BeanConstructor getBeanConstructor() {
        return beanConstructor;
    }
}
