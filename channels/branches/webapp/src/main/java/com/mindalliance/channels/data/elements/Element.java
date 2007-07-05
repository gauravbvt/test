// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.Named;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.Assertable;
import com.mindalliance.channels.data.Described;
import com.mindalliance.channels.data.Unique;
import com.mindalliance.channels.data.reference.TypeSet;
import com.mindalliance.channels.data.reference.Typed;

/**
 * An event-enabled bean, stated or inferred, with a unique id, a
 * name, a description, types and issues.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface Element
    extends Unique, Typed, Named, Described, Assertable, JavaBean {

    /**
     * Get the issues attached to the element.
     */
    List<Issue> getIssues();

    /**
     * Get the domains to which this element belongs given the types
     * it is categorized with.
     */
    TypeSet getDomains();

    /**
     * Return whether a given user has authority over the element.
     * @param user the user
     */
    boolean hasAuthority( User user );
}
