package com.mindalliance.channels.nk.bean;

import java.util.Map;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 21, 2008
 * Time: 1:41:37 PM
 */
public interface IBean extends Serializable, Cloneable {

    Map getBeanProperties();

    boolean isComponent();

    boolean isPersistent();

    Object deepCopy();

    void initialize();
    
}
