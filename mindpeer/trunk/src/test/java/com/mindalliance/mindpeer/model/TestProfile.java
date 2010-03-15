// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import com.mindalliance.mindpeer.SecuredAspects;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ...
 */
public class TestProfile {

    private Profile profile;

    private User user;

    /**
     * Create a new TestProfile instance.
     */
    public TestProfile() {
    }

    @Before
    public void init() {
        user = new User();
        profile = user.getProfile();

        SecuredAspects.bypass( true );
    }

    /**
     * Silly tests for coverage.
     */
    @Test
    public void testAccessors() {
        assertEquals( user, profile.getUser() );

        String s = "Some string";
        profile.setName( s );
        assertEquals( s, profile.getName() );

        profile.setUser( null );
        assertNull( profile.getUser() );

        profile.setOrganization( s );
        assertEquals( s, profile.getOrganization() );

        profile.setPhone( s );
        assertEquals( s, profile.getPhone() );

        profile.setFax( s );
        assertEquals( s, profile.getFax() );

        profile.setWebsite( s );
        assertEquals( s, profile.getWebsite() );

        profile.setDesignation( s );
        assertEquals( s, profile.getDesignation() );

        profile.setLocation( s );
        assertEquals( s, profile.getLocation() );

        Set<Tag> tags = new HashSet<Tag>();
        profile.setInterests( tags );
        assertSame( tags, profile.getInterests() );

        byte[] pic = new byte[12];
        profile.setPicture( pic );
        assertSame( pic, profile.getPicture() );
    }

    @Test
    public void testToString() {
        assertEquals( "Profile[null:Unknown]", profile.toString() );

        profile.setId( 1L );
        profile.setName( "bla" );
        assertEquals( "Profile[1:bla]", profile.toString() );
    }

    @Test
    public void testProduct() {
        assertEquals( 0, profile.getProductCount() );
        assertNull( profile.getProduct( null ) );
        assertEquals( 0, profile.getProducts().size() );

        CommentFeed feed = new CommentFeed();
        profile.addProduct( feed );
        assertEquals( 1, profile.getProductCount() );
        assertEquals( profile, feed.getProfile() );
        assertEquals( feed, profile.getProduct( feed.getName() ) );

        List<Product> products = profile.getProducts();
        assertEquals( 1, products.size() );
        assertEquals( feed, products.get( 0 ) );

        profile.removeProduct( feed.getName() );
        assertEquals( 0, profile.getProductCount() );
    }

}
