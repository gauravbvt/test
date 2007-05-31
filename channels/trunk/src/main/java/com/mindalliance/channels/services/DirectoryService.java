// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.services;

import java.util.List;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.data.elements.resources.Organization;

/**
 * The directory service.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public interface DirectoryService extends Service, JavaBean {

    /**
     * Return the organizations knows to this service.
     */
    List<Organization> getOrganizations();

    /**
     * Add an organization to the list.
     * @param organization the organization
     */
    void addOrganization( Organization organization );

    /**
     * Remove an organization from the list.
     * @param organization the organization
     */
    void removeOrganization( Organization organization );

}
