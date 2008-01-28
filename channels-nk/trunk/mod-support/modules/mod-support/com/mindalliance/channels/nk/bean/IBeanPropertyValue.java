package com.mindalliance.channels.nk.bean;

import groovy.lang.Closure;

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 25, 2008
 * Time: 1:43:04 PM
 */
public interface IBeanPropertyValue {

    // Visitor - accept invitation to action
      void accept(Closure action);

    IPersistentBean getContextBean();

}
