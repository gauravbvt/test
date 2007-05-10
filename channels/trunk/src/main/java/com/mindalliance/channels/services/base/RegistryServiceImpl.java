/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.services.base;

import java.util.List;
import java.util.Set;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;

import com.mindalliance.channels.User;
import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.data.Element;
import com.mindalliance.channels.data.system.Registry;
import com.mindalliance.channels.services.RegistryService;

public class RegistryServiceImpl extends AbstractService implements
		RegistryService {
	
	/**
	 * @return the registry
	 */
	private Registry getRegistry() {
		return getSystem().getRegistry();
	}

	public void addAdministrator(User user) throws UserExistsException {
	}

	public void addUser(User user) throws UserExistsException {
	}

	public Set<User> getAdministrators() {
		return null;
	}

	public Set<User> getUsers() {
		return null;
	}

	public boolean isAdministrator(User user) {
		return false;
	}

	public boolean isUser(User user) {
		return false;
	}

	public void removeAdministrator(User user) {
	}


}
