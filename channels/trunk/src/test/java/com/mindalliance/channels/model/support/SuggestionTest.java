// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContextImpl;
import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.ProviderManager;
import org.acegisecurity.providers.TestingAuthenticationProvider;
import org.acegisecurity.providers.TestingAuthenticationToken;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.dao.DaoAuthenticationProvider;
import org.acegisecurity.userdetails.UserDetailsService;
import org.junit.Before;
import org.junit.Test;

import com.mindalliance.channels.impl.UserImpl;
import com.mindalliance.channels.impl.UserManager;
import com.mindalliance.channels.model.TestModelObject;

/**
 * Test suite for Suggestions.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class SuggestionTest {

    private static final String TEST_VALUE = "test value";
    private Suggestion<String> suggestion;
    private PropertyDescriptor property;
    
    private UserImpl user1;
    private UserImpl user2;
    private UserManager userDetails;

    /**
     * Setup before each test.
     * @throws IntrospectionException never...
     */
    @Before
    public void setUp() throws IntrospectionException {

        this.user1 = new UserImpl( "joe", "bla", new GrantedAuthority[]{
            new GrantedAuthorityImpl( "ROLE_USER" ),
        } );
        this.user2 = new UserImpl( "bob", "blabla", new GrantedAuthority[]{
            new GrantedAuthorityImpl( "ROLE_USER" ),
        } );
        this.userDetails = new UserManager();
        this.userDetails.addUser( this.user1 );
        userDetails.addUser( this.user2 );

        login( "bob", "blabla" );

        this.property = new PropertyDescriptor( "name", TestModelObject.class );
        this.suggestion = new Suggestion<String>( this.property, TEST_VALUE );

    }

    private void login( String user, String password ) {

        DaoAuthenticationProvider daoAuthenticationProvider =
            new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService( this.userDetails );

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
     * Test method for {@link Suggestion#hashCode()}.
     */
    @Test
    public final void testHashCode() {
        assertEquals( TEST_VALUE.hashCode(), this.suggestion.hashCode() );
    }

    /**
     * Test method for {@link Suggestion#getProperty()}.
     */
    @Test
    public final void testGetProperty() {
        assertSame( property, this.suggestion.getProperty() );
    }

    /**
     * Test method for {@link Suggestion#getValue()}.
     */
    @Test
    public final void testGetValue() {
        assertSame( TEST_VALUE, this.suggestion.getValue() );
    }

    /**
     * Needed for compatibility when running inside eclipse...
     * @return a compatibility adapter
     */
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter( SuggestionTest.class );
    }
}
