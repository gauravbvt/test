/*
 * Created on Apr 30, 2007
 */
package com.mindalliance.channels.services.base;

import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.Set;

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
import com.mindalliance.channels.data.user.UserImpl;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.services.RegistryService;

public class RegistryServiceImpl extends AbstractService implements
        RegistryService {

    public RegistryServiceImpl( SystemService systemService ) {
        super( systemService );
    }

    /**
     * Logs a user in.
     * 
     * @param user
     * @param password
     */
    @Secured( "ROLE_RUN_AS_SYSTEM")
    public void login( String user, String password ) {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService( this );

        ProviderManager providerManager = new ProviderManager();
        providerManager.setProviders( Arrays.asList( new AuthenticationProvider[] { daoAuthenticationProvider } ) );

        // Create and store the Acegi SecureContext into the
        // ContextHolder.
        SecurityContextImpl secureContext = new SecurityContextImpl();
        secureContext.setAuthentication( providerManager.doAuthentication( new UsernamePasswordAuthenticationToken(
                user, password ) ) );
        SecurityContextHolder.setContext( secureContext );
    }

    /**
     * Logs current user out.
     */
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.services.RegistryService#registerAdministrator(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public User registerAdministrator( String name, String username,
            String password ) throws UserExistsException {
        UserImpl user = new UserImpl( name, username, password, new String[] {
            "ROLE_USER", "ROLE_ADMIN" } );
        getRegistry().addAdministrator( user );
        return user;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.services.RegistryService#registerUser(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public User registerUser( String name, String username, String password )
            throws UserExistsException {
        UserImpl user = new UserImpl( name, username, password,
                new String[] { "ROLE_USER" } );
        getRegistry().addUser( user );
        return user;
    }

    /**
     * @return the registry
     */
    private Registry getRegistry() {
        return getSystem().getRegistry();
    }

    @Secured( { "ROLE_ADMIN" })
    public void makeAdministrator( User user ) throws UserExistsException {
        getRegistry().addAdministrator( user );
    }

    public Set<User> getAdministrators() {
        return getRegistry().getAdministrators();
    }

    public Set<User> getUsers() {
        return getRegistry().getUsers();
    }

    public boolean isAdministrator( User user ) {
        return getRegistry().isAdministrator( user );
    }

    public boolean isUserNameTaken( String username ) {
        return getRegistry().isUserNameTaken( username );
    }

    @Secured( { "ROLE_ADMIN" })
    public void removeAdministrator( User user ) {
        getRegistry().removeAdministrator( user );
    }

    public UserDetails loadUserByUsername( String username )
            throws UsernameNotFoundException, DataAccessException {
        return getRegistry().loadUserByUsername( username );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.services.RegistryService#isUserRegistered(com.mindalliance.channels.User)
     */
    public boolean isUserRegistered( User user ) {
        return getRegistry().isUser( user );
    }

}
