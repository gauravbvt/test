// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor;

import java.util.ArrayList;
import java.util.Collection;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.resources.Channel;
import com.mindalliance.channels.data.elements.resources.Organization;
import com.mindalliance.channels.data.elements.resources.Role;
import com.mindalliance.channels.services.SystemService;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ChooserHelper {

    public ChooserHelper() { 
        
    }
    
    public Collection<Role> findRole(SystemService system, User user) {
        Collection<Role> results = new ArrayList<Role>();
        for (int inx = 0 ; inx < 100 ; inx++) {
            Role role = new Role();
            role.setName( "Role " + inx );
            results.add( role );
        }
        return results;
    }
    
    public Collection<Organization> findOrganization(SystemService system, User user) {
        Collection<Organization> results = new ArrayList<Organization>();
        if (system != null) {
            results.addAll( system.getDirectoryService().getOrganizations() );
        }
        
        return results;
    }
    
    public Collection<Channel>findChannel(SystemService system, User user) {
        Collection<Channel> results = new ArrayList<Channel>();
        if (system != null) {
            results.addAll( system.getDirectoryService().getChannels() );
        }
        
        return results;
    }
}
