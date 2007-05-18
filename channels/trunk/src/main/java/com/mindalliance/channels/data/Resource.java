/*
 * Created on Apr 25, 2007
 */
package com.mindalliance.channels.data;

import com.mindalliance.channels.data.elements.scenario.Circumstance;

/**
 * Something that may or may not be operational by default or in a
 * given circumstance.
 * 
 * @author jf
 */
public interface Resource extends Element, Describable {

    /**
     * @param circumstance
     * @return whether the resource is operational in a given
     *         circumstance.
     */
    boolean isOperationalIn( Circumstance circumstance );

    /**
     * @return whether the resource is operation by default.
     */
    boolean isOperational();

}
