/*
 * Created on May 1, 2007
 */
package com.mindalliance.channels.data;

import com.mindalliance.channels.data.components.Information;

/**
 * Someone who may know and/or need to know.
 * 
 * @author jf
 */
public interface Knowledgeable extends Element {

    boolean knows( Information information );

    boolean needsToKnow( Information information );

}
