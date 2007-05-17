/*
 * Created on Apr 28, 2007
 */
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
 * @author jf
 */
abstract public class AbstractQueryable extends AbstractJavaBean implements
        Queryable {
    
    protected System system;
    
    public AbstractQueryable() {}
    
    protected AbstractQueryable(System system) {
        this.system = system;
    }

    public Iterator<Element> findAll( Query query, Map bindings ) {
        return null;
    }

    public Element findOne( Query query, Map bindings ) {
        return null;
    }
    
    /**
     * Returns the authenticated user 
     * @return
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
    
    public System getSystem() {
        return system;
    }



}
