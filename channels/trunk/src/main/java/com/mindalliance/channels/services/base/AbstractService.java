/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.services.base;

import java.util.List;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.Element;
import com.mindalliance.channels.data.system.System;
import com.mindalliance.channels.services.ChannelsService;
import com.mindalliance.channels.services.Service;

/**
 * A service with access to the System data model.
 * @author jf
 *
 */
abstract public class AbstractService implements Service {
	
	protected ChannelsService channelsService;
	
	protected AbstractService() {}
	
	protected AbstractService(ChannelsService channelsService) {
		this.channelsService = channelsService;
	}
	
	
	/* (non-Javadoc)
	 * @see com.mindalliance.channels.services.Service#hasAuthority(com.mindalliance.channels.User, com.mindalliance.channels.data.elements.Element)
	 */
	public boolean hasAuthority(User user, Element element) {
		return false;
	}

	public List<User> getAuthoritativeUsers(Element element) {
		return getSystem().findAuthoritativeUsers(element);
	}

	
	/**
	 * Get authenticated user in current thread or null if none.
	 * @return
	 */
	public User getAuthenticatedUser() {
        User user = null ;
        SecurityContext context = SecurityContextHolder.getContext();
        if ( context != null ) {
            Authentication authentication = context.getAuthentication();
            if ( authentication != null )
                user = (User) authentication.getPrincipal();
        }
        return user;
	}

	/**
	 * @return the channelsService
	 */
	public ChannelsService getChannelsService() {
		return channelsService;
	}
	
	public System getSystem() {
		return getChannelsService().getSystem();
	}


	/**
	 * @param channelsService the channelsService to set
	 */
	public void setChannelsService(ChannelsService channelsService) {
		this.channelsService = channelsService;
	}

}
