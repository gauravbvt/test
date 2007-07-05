// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.components;

/**
 * A contactable resource that controls access to itself.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface Accessible {

    /**
     * Tell if this object can be contacted by a resource.
     * @param contactable the resource
     */
    boolean hasAccess( Contactable contactable );

}
