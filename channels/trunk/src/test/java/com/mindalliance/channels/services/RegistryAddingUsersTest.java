// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.services;

import static org.junit.Assert.*;

import java.beans.PropertyVetoException;

import org.junit.Test;

import com.mindalliance.channels.UserExistsException;
import com.mindalliance.channels.data.user.UserImpl;

/**
 * Tests for the security contracts of the Registry service.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class RegistryAddingUsersTest extends AbstractSecurityTest {
	/**
	 * Adding a new user with logged in user.
	 * @throws PropertyVetoException
	 * @throws UserExistsException
	 */
    @Test
    public void testAddUserByUser() throws PropertyVetoException, UserExistsException {
        UserImpl u = new UserImpl("User1","user1","user1",new String[]{ 
                "ROLE_USER" } );
		registryService.login( "user", "user" ); 
		try { 
			registryService.addUser( u ); 
		} finally { registryService.logout(); }
    	assertFalse(registryService.isAdministrator(u));
    	assertTrue(registryService.isUser(u));
    }
    
	/**
	 * Adding a new administrator with logged in administrator.
	 * @throws PropertyVetoException
	 * @throws UserExistsException
	 */
    @Test
    public void testAddAdminByAdmin() throws PropertyVetoException, UserExistsException {
        UserImpl u = new UserImpl("User2","user2","user2",new String[]{ 
                "ROLE_ADMIN" } );
        registryService.login( "admin", "admin" );
        try {
        	registryService.addAdministrator( u );
        } finally {registryService.logout();}
    	assertTrue(registryService.isAdministrator(u));
    	assertTrue (registryService.isUser(u));
    }
    
    // TODO - Uncomment when @Secured is functional
    
	/**
	 * Adding a new administrator with logged in user - must raise a security exception
	 * @throws PropertyVetoException
	 * @throws UserExistsException
	 */
    /*
   @Test( expected = SecurityException.class )
    public void testAddAdminByUser()  throws PropertyVetoException, UserExistsException {
        UserImpl u = new UserImpl("User3","user3","user3",new String[]{ 
        "ROLE_ADMIN" } );

        registryService.login( "user", "user" );
        try {registryService.addAdministrator( u );} finally {registryService.logout();}
        fail();
   }
   */
	/**
	 * Adding a new user with no logged in user
	 * @throws PropertyVetoException
	 * @throws UserExistsException
	 */
    /*
   @Test( expected = SecurityException.class )
   public void testAddUserByNotLoggedIn() throws PropertyVetoException, UserExistsException {
       UserImpl u = new UserImpl("User4","user4","user4",new String[]{ 
               "ROLE_USER" } );
       registryService.addUser( u );
       fail();
   }
	*/
	/**
	 * Adding a new admin with no logged in user
	 * @throws PropertyVetoException
	 * @throws UserExistsException
	 */
    /*
  @Test( expected = SecurityException.class )
  public void testAddAdminByNotLoggedIn() throws PropertyVetoException, UserExistsException {
      UserImpl u = new UserImpl("User5","user5","user5",new String[]{ 
              "ROLE_ADMIN" } );
      registryService.addAdministrator( u );
      fail();
  }
     */

}
