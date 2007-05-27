// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.components;

import com.mindalliance.channels.data.elements.Element;
import com.mindalliance.channels.data.reference.Information;

/**
 * Someone who may know and/or need to know.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface Knowledgeable extends Element {

    /**
     * Tells if one knows about an information.
     * @param information the information.
     */
    boolean knows( Information information );

    /**
     * Tells if one needs to know about an information.
     * @param information the information.
     */
    boolean needsToKnow( Information information );
}
