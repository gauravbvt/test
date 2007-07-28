// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.definitions;

/**
 * Something about which information can be communicated.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 * @composed - - 0..1 Information
 */
public interface Describable {

    /**
     * Get the information template for this object.
     */
    Information getInformationTemplate();

}
