/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.services.base;

import java.util.Arrays;
import java.util.Set;

import org.acegisecurity.Authentication;
import org.acegisecurity.annotation.Secured;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContextImpl;
import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.ProviderManager;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.dao.DaoAuthenticationProvider;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.springframework.dao.DataAccessException;

import com.mindalliance.channels.User;
import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.data.system.Registry;
import com.mindalliance.channels.data.system.System;
import com.mindalliance.channels.services.ChannelsService;
import com.mindalliance.channels.services.RegistryService;

public class RegistryServiceImpl extends AbstractService implements
		RegistryService {
	
	public RegistryServiceImpl(ChannelsService channelsService, System system) {
		super(channelsService, system);
	}
	/**
	 * Logs a user in.
	 * @param user
	 * @param password
	 */
	@Secured("ROLE_RUN_AS_SYSTEM")
    public void login( String user, String password ) {

        DaoAuthenticationProvider daoAuthenticationProvider =
            new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(this);

        ProviderManager providerManager = new ProviderManager();
        providerManager.setProviders(
                Arrays.asList( new AuthenticationProvider[]{
                    daoAuthenticationProvider
                } ) );

        // Create and store the Acegi SecureContext into the ContextHolder.
        SecurityContextImpl secureContext = new SecurityContextImpl();
        secureContext.setAuthentication(
                providerManager.doAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                user, password ) ) );
        SecurityContextHolder.setContext( secureContext );
    }

	/**
	 * Logs current user out.
	 *
	 */
	public void logout() {
		SecurityContextHolder.clearContext();
	}

    /**
     * Initialize all users
     * @param users
     * @throws UserExistsException 
     */
    @Secured("ROLE_RUN_AS_SYSTEM")
    public void setUsers(Set<User> users) throws UserExistsException {
    	getRegistry().setUsers(users);
    }
    
    /**
     * Initialize all admins
     * @param users
     */
    @Secured("ROLE_RUN_AS_SYSTEM")
    public void setAdministrators(Set<User> users) throws UserExistsException {
    	getRegistry().setAdministrators(users);
    }
    


	/**
	 * @return the registry
	 */
	private Registry getRegistry() {
		return system.getRegistry();
	}

	@Secured( { "ROLE_ADMIN" } )
	public void addAdministrator(User user) throws UserExistsException {
		getRegistry().addAdministrator(user);
	}

	public void addUser(User user) throws UserExistsException {
		getRegistry().addUser(user);
	}

	public Set<User> getAdministrators() {
		return getRegistry().getAdministrators();
	}

	public Set<User> getUsers() {
		return getRegistry().getUsers();
	}

	public boolean isAdministrator(User user) {
		return getRegistry().isAdministrator(user);
	}

	public boolean isUser(User user) {
		return getRegistry().isUser(user);
	}

	@Secured( { "ROLE_ADMIN" } )
	public void removeAdministrator(User user) {
		getRegistry().removeAdministrator(user);
	}

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		return getRegistry().loadUserByUsername(username);
	}



}
