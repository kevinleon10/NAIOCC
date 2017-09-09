package com.ci1330.ecci.ucr.ac.cr.factory;

import com.ci1330.ecci.ucr.ac.cr.bean.Bean;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kevinleon10 on 09/09/17.
 */
public abstract class BeanContainer {
    protected HashMap<String,List<Bean>> beansMap;
    public Object getBean(String id){
        return new Object();
    }
}
