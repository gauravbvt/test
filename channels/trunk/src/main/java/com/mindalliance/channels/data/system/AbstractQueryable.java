// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.system;

import java.util.Iterator;
import java.util.Map;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.Element;
import com.mindalliance.channels.data.Queryable;
import com.mindalliance.channels.data.support.Query;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * Holds queryable, top-level javabeans (i.e. not contained in
 * others).
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class AbstractQueryable extends AbstractJavaBean
    implements Queryable {

    private System system;

    /**
     * Default constructor.
     */
    public AbstractQueryable() {
    }

    /**
     * Default constructor.
     * @param system the system
     */
    protected AbstractQueryable( System system ) {
        this.system = system;
    }

    /**
     * Iterate over elements matching the query.
     * @param query the query
     * @param bindings the bindings
     */
    public Iterator<Element> findAll( Query query, Map bindings ) {
        // TODO
        return null;
    }

    /**
     * Iterate over elements matching the query.
     * @param query the query
     * @param bindings the bindings
     */
    public Element findOne( Query query, Map bindings ) {
        // TODO
        return null;
    }

    /**
     * Returns the authenticated user.
     */
    public User getAuthenticatedUser() {
        User user = null;
        SecurityContext context = SecurityContextHolder.getContext();
        if ( context != null ) {
            Authentication authentication = context.getAuthentication();
            if ( authentication != null )
                user = (User) authentication.getPrincipal();
        }
        return user;
    }

    /**
     * Return the system.
     */
    public final System getSystem() {
        return system;
    }

    /**
     * Set the system attached to this object.
     * @param system the system
     */
    void setSystem( System system ) {
        this.system = system;
    }
}
