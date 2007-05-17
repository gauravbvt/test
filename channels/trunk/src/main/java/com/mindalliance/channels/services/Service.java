/*
 * Created on Apr 27, 2007
 */
package com.mindalliance.channels.services;

import com.mindalliance.channels.User;

/**
 * A service implements methods to be invoked via commands. Each
 * service implies a context (a JavaBean) for its queries. 
 * 
 * @author jf
 */
public interface Service {

    /**
     * Returns the authenticated user 
     * @return
     */
    public User getAuthenticatedUser();
}
