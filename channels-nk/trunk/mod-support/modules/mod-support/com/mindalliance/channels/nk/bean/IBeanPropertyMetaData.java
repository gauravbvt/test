package com.mindalliance.channels.nk.bean;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 25, 2008
 * Time: 10:03:00 AM
 */
public interface IBeanPropertyMetaData {

    String getPropertyName();
    IBeanPropertyMetaData getParent(); // "Discovered" on activate
    String getLabel();
    boolean isRequired();
    boolean isReadOnly();
    Object getDomain();
    String getHint();
    Map getPresentation();
    boolean isAdvanced();   

}
