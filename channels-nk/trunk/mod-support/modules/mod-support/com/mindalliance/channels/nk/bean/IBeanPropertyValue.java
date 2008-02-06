package com.mindalliance.channels.nk.bean;

import groovy.lang.Closure;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 25, 2008
 * Time: 1:43:04 PM
 */
public interface IBeanPropertyValue {

    // Visitor - accept invitation to action given property's name and xpath to parent property
    void accept(Map args, Closure action);

    IPersistentBean getContextBean();

    Object getMetadata();

    String getSchemaType();

}
