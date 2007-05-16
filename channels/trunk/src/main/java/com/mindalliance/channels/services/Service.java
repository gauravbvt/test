/*
 * Created on Apr 27, 2007
 */
package com.mindalliance.channels.services;

import java.util.List;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.Element;
import com.mindalliance.channels.data.system.System;

/**
 * A service implements methods to be invoked via commands. Each
 * service implies a context (a JavaBean) for its queries. A marker
 * interface (thus far)
 * 
 * @author jf
 */
public interface Service {

    /**
     * Get the data layer's root
     * 
     * @return
     */
    System getSystem();

    /**
     * Get the top service
     * 
     * @return
     */
    SystemService getSystemService();

    /**
     * Set the top service.
     * 
     * @param systemService
     */
    void setSystemService( SystemService systemService );

    /**
     * Get authenticated user in this thread.
     * 
     * @return
     */
    User getAuthenticatedUser();

    /**
     * Get all users that have authority over an element.
     * 
     * @param element
     * @return
     */
    List<User> getAuthoritativeUsers( Element element );

    /**
     * Whether a user has authority over an element.
     * 
     * @param user
     * @param element
     * @return
     */
    boolean hasAuthority( User user, Element element );

}
