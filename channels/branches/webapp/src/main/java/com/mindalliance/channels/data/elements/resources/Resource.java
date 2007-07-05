// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements.resources;

import com.mindalliance.channels.data.elements.Element;
import com.mindalliance.channels.data.elements.scenario.Circumstance;
import com.mindalliance.channels.data.reference.Describable;

/**
 * Something that may or may not be operational by default or in a
 * given circumstance.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface Resource extends Element, Describable {

    /**
     * Return whether the resource is operational in a given
     * circumstance.
     * @param circumstance the circumstance
     */
    boolean isOperationalIn( Circumstance circumstance );

    /**
     * Return whether the resource is operation by default.
     */
    boolean isOperational();

}
