// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.support;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.User;

/**
 * An event-enabled bean, stated or inferred, with a unique id, a
 * name, a description, types and issues.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface Element
    extends Unique, JavaBean {

    /**
     * Return whether a given user has authority over the element.
     * @param user the user
     */
    boolean hasAuthority( User user );
}
