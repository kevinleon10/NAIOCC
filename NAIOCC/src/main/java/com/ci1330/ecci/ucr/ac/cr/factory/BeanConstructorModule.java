package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.Bean;
import com.ci1330.ecci.ucr.ac.cr.bean.BeanConstructor;
import com.ci1330.ecci.ucr.ac.cr.bean.BeanParameter;
import com.ci1330.ecci.ucr.ac.cr.exception.BeanConstructorConflictException;
import com.ci1330.ecci.ucr.ac.cr.exception.BeanConstructorNotFoundException;

import java.lang.reflect.Constructor;

/**
 * Created by Josue Leon on 28/09/2017
 */
public class BeanConstructorModule {

    /**
     * Checks if all parameters have an index assigned. If at least one doesn't, it returns false.
     * @return
     */
    private static boolean checkParametersIndexes(Bean bean){
        boolean allIndexesAssigned = true;
        int paramListIndex = 0;
        BeanParameter beanParameter = null;
        while(paramListIndex < bean.getBeanConstructor().getBeanParameterList().size()
                && allIndexesAssigned){
            beanParameter = bean.getBeanConstructor().getBeanParameterList().get(paramListIndex);
            if(beanParameter.getIndex() == -1){
                allIndexesAssigned = false;
            }
            paramListIndex++;
        }
        return allIndexesAssigned;
    }

    /**
     * Returns an array containing the Class types of each parameter
     * in the bean's constructor.
     * @param bean
     * @return
     */
    private static Class[] obtainParametersClassArray(Bean bean){
        String parameterClass = null;
        String beanParameterType;
        Class param = null;
        Class[] parametersClassArray = new Class[bean.getBeanConstructor().getBeanParameterList().size()];
        for (BeanParameter p : bean.getBeanConstructor().getBeanParameterList()) {
            beanParameterType = p.getExplicitTypeName();
            if(beanParameterType == null){
                beanParameterType = p.getBeanFactory().findBean(p.getBeanRef()).getClass().toString();
            }
            switch (beanParameterType) {
                case "int":
                    param = int.class;
                    break;
                case "byte":
                    param = byte.class;
                    break;
                case "short":
                    param = short.class;
                    break;
                case "long":
                    param = long.class;
                    break;
                case "float":
                    param = float.class;
                    break;
                case "double":
                    param = double.class;
                    break;
                case "boolean":
                    param = boolean.class;
                    break;
                case "char":
                    param = char.class;
                    break;
                default:
                    parameterClass = p.getExplicitTypeName();
                    try {
                        param = Class.forName(parameterClass);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            //System.out.println("agregando parametro al array tipo:" + param);
            parametersClassArray[p.getIndex()] = param;
        }
        return parametersClassArray;
    }

    /**
     * Compares the bean's constructor parameter type with the bean's class constructor parameter
     * type. If they match, it assigns the respective index to the bean's constructor parameter.
     * The switch is needed for primitive types checking and casting.
     * @param beanParameter bean's constructor parameter
     * @param beanClassConstructorParameter bean's class constructor parameter
     * @param paramIndex current index of the bean's class constructor parameter
     * @return True if parameters matched
     */
    public static boolean setBeanParameterIndex(BeanParameter beanParameter, Class beanClassConstructorParameter, int paramIndex){
        boolean parametersMatched = false;
        String beanParameterType = beanParameter.getExplicitTypeName();
        if(beanParameterType == null){
            beanParameterType = beanParameter.getBeanFactory().findBean(beanParameter.getBeanRef()).getClass().toString();
        }
        switch (beanParameterType) {
            case "int":
                //System.out.println("se metio en case int");
                if (beanClassConstructorParameter.toString().equals("int")) {
                    parametersMatched = true;
                    //System.out.println("parametro hizo match con int");
                    beanParameter.setIndex(paramIndex);
                    //System.out.println("indice del parametro tipo: " + p.getExplicitTypeName() + " = " + paramIndex);
                }
                break;
            case "java.lang.Integer":
                //System.out.println("se metio en case int");
                if (beanClassConstructorParameter.toString().equals("int")) {
                    parametersMatched = true;
                    //System.out.println("parametro hizo match con integer");
                    beanParameter.setIndex(paramIndex);
                }
                break;
            case "byte":
                if (beanClassConstructorParameter.toString().equals("byte")) {
                    parametersMatched = true;
                    beanParameter.setIndex(paramIndex);
                }
                break;
            case "java.lang.Byte":
                if (beanClassConstructorParameter.toString().equals("byte")) {
                    parametersMatched = true;
                    beanParameter.setIndex(paramIndex);
                }
                break;
            case "short":
                if (beanClassConstructorParameter.toString().equals("short")) {
                    parametersMatched = true;
                    beanParameter.setIndex(paramIndex);
                }
                break;
            case "java.lang.Short":
                if (beanClassConstructorParameter.toString().equals("short")) {
                    parametersMatched = true;
                    beanParameter.setIndex(paramIndex);
                }
                break;
            case "long":
                if (beanClassConstructorParameter.toString().equals("long")) {
                    parametersMatched = true;
                    beanParameter.setIndex(paramIndex);
                }
                break;
            case "java.lang.Long":
                if (beanClassConstructorParameter.toString().equals("long")) {
                    parametersMatched = true;
                    beanParameter.setIndex(paramIndex);
                }
                break;
            case "float":
                if (beanClassConstructorParameter.toString().equals("float")) {
                    parametersMatched = true;
                    beanParameter.setIndex(paramIndex);
                }
                break;
            case "java.lang.Float":
                if (beanClassConstructorParameter.toString().equals("float")) {
                    parametersMatched = true;
                    beanParameter.setIndex(paramIndex);
                }
                break;
            case "double":
                if (beanClassConstructorParameter.toString().equals("double")) {
                    parametersMatched = true;
                    beanParameter.setIndex(paramIndex);
                }
                break;
            case "java.lang.Double":
                if (beanClassConstructorParameter.toString().equals("double")) {
                    parametersMatched = true;
                    beanParameter.setIndex(paramIndex);
                }
                break;
            case "boolean":
                //System.out.println("se metio en case boolean");
                if (beanClassConstructorParameter.toString().equals("boolean")) {
                    parametersMatched = true;
                    // System.out.println("parametro hizo match con boolean");
                    beanParameter.setIndex(paramIndex);
                    //System.out.println("indice del parametro tipo: " + p.getExplicitTypeName() + " = " + paramIndex);
                }
                break;
            case "java.lang.Boolean":
                // System.out.println("se metio en case boolean");
                if (beanClassConstructorParameter.toString().equals("boolean")) {
                    parametersMatched = true;
                    //System.out.println("parametro hizo match con boolean");
                    beanParameter.setIndex(paramIndex);
                }
                break;
            case "char":
                if (beanClassConstructorParameter.toString().equals("char")) {
                    parametersMatched = true;
                    beanParameter.setIndex(paramIndex);
                }
                break;
            case "java.lang.Character":
                if (beanClassConstructorParameter.toString().equals("char")) {
                    parametersMatched = true;
                    beanParameter.setIndex(paramIndex);
                }
                break;
            default:
                //System.out.println("se metio a default con:" + beanParameter.getExplicitTypeName() + " y de parametro del const: " + beanClassConstructorParameter);
                try {
                    if (Class.forName(beanParameter.getExplicitTypeName()).equals(beanClassConstructorParameter)) {
                        parametersMatched = true;
                        //System.out.println("parametro hizo match con default");
                        beanParameter.setIndex(paramIndex);
                        // System.out.println("indice del parametro tipo: " + beanParameter.getExplicitTypeName() + " = " + paramIndex);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                break;
        }
        return parametersMatched;
    }

    /**
     *Sets the constructor method and parameters to a bean for it to be ready
     * to be autowired and injected. If indexes are not specified, the method checks
     * all constructors of the bean's class to match one.
     */
    public static void registerConstructor(Bean bean) {
        Constructor matchedConstructor = null;

        if(!checkParametersIndexes(bean)) { //Checks if at least one parameter doesn't have an index assigned
            int totalParametersOneType = 0;
            int totalParametersMatched = 0;
            int constructorMatches = 0;
            int paramIndex = 0;
            boolean twoMatchesForOneParam = false;

            Constructor[] classConstructors = bean.getBeanClass().getDeclaredConstructors();
            Class[] classConstructorParameters;

            for (Constructor classConstructor : classConstructors) {        // Iterates through all constructors in the bean's class
                //System.out.println("hciendo loop de un constructor");
                classConstructorParameters = classConstructor.getParameterTypes();

                if (classConstructorParameters.length == bean.getBeanConstructor().getBeanParameterList().size()) { // Checks if the current class constructor has same amount of parameters
                                                                                                                    // than the bean's constructor

                    for (BeanParameter beanParameter : bean.getBeanConstructor().getBeanParameterList()) {      // Iterates through all the declared parameters in the configuration
                        //System.out.println("iterando lista parametros propios:" + p.getExplicitTypeName());
                        for (Class parameter : classConstructorParameters) {                                    // Iterates through all the parameters of the current class constructor
                            //System.out.println("iterando lista parametros constructor:" + p.getExplicitTypeName());
                            //System.out.println("clase del param:" + parameter.toString());
                            if(setBeanParameterIndex(beanParameter, parameter, paramIndex)){ // Compares the parameters and assigns an index to the bean's constructor parameter if they matched
                                totalParametersOneType++;
                                totalParametersMatched++;
                            }
                            paramIndex++;
                        }

                        paramIndex = 0;
                        if (totalParametersOneType > 1) {
                            twoMatchesForOneParam = true;
                        }
                        totalParametersOneType = 0;
                    }

                    if (totalParametersMatched == bean.getBeanConstructor().getBeanParameterList().size() && !twoMatchesForOneParam) {
                        constructorMatches++;
                        matchedConstructor = classConstructor;
                    }
                    totalParametersMatched = 0;
                }
                twoMatchesForOneParam = false;
            }
            if (constructorMatches == 0) {
                try {
                    throw new BeanConstructorNotFoundException("Bean creation error: constructor not found for the specified parameters in bean: " + bean.getId() + ".");
                } catch (BeanConstructorNotFoundException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            if (constructorMatches > 1) {
                try {
                    throw new BeanConstructorConflictException("Bean creation error: there are multiple constructors for the specified parameters in bean: " + bean.getId() +
                            ". Couldn't identify which one is intended to be called (same parameter quantity and types).");
                } catch (BeanConstructorConflictException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
        else{   // All parameters specified in the configuration have indexes assigned.
            try {
                //System.out.println("num parametros:" + this.obtainParametersClassArray().length);
                matchedConstructor = bean.getBeanClass().getConstructor(obtainParametersClassArray(bean));
            } catch (NoSuchMethodException e) {
                System.out.println("Bean creation error: constructor not found for the specified parameters in bean: " + bean.getId() + ".");
                e.printStackTrace();
                System.exit(1);
            }
        }
        bean.getBeanConstructor().setConstructorMethod(matchedConstructor);     // sets the Constructor to the bean, ready to be autowired and injected
    }
}
