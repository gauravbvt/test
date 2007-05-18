// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static com.mindalliance.channels.ui.IconManager.Mode.*;

/**
 * Tests for IconManager.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class IconManagerTest {

    private IconManager im ;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        Map<Class, String> icons = new HashMap<Class,String>();
        icons.put( Object.class, "object.png" );
        icons.put( Map.class, "map.png" );
        icons.put( Collection.class, "collection.png" );
        
        im = new IconManager();
        im.setIcons( icons );
        im.setBigPrefix( "bg_" );
        im.setSmallPrefix( "sm_" );
    }

    /**
     * Test method for {@link IconManager#getSmallIcon(java.lang.Object)}.
     */
    @Test
    public void testGetSmallIconObject_1() {
        im.setNullIcon( "null.png" );
        assertEquals( "sm_null.png", im.getSmallIcon( null ) );
    }

    /**
     * Test method for {@link IconManager#getSmallIcon(java.lang.Object)}.
     */
    @Test
    public void testGetSmallIconObject_2() {
        assertEquals( "sm_object.png", im.getSmallIcon( im ) );
        assertEquals( "sm_object.png", im.getSmallIcon( "Object" ) );
        assertEquals( "sm_collection.png", im.getSmallIcon( new ArrayList() ) );
        assertEquals( "sm_map.png", im.getSmallIcon( new HashMap() ) );
        
        im.getIcons().remove( Object.class );
        im.setDefaultIcon( "default.png" );
        assertEquals( "sm_default.png", im.getSmallIcon( "bla" ) );
    }

    /**
     * Test method for {@link IconManager#getBigIcon(java.lang.Object)}.
     */
    @Test
    public void testGetBigIconObject_1() {
        im.setNullIcon( "null.png" );
        assertEquals( "bg_null.png", im.getBigIcon( null ) );
    }

    /**
     * Test method for {@link IconManager#getSmallIcon(java.lang.Object)}.
     */
    @Test
    public void testGetBigIconObject_2() {
        assertEquals( "bg_object.png", im.getBigIcon( im ) );
        assertEquals( "bg_object.png", im.getBigIcon( "Object" ) );
        assertEquals( "bg_collection.png", im.getBigIcon( new ArrayList() ) );
        assertEquals( "bg_map.png", im.getBigIcon( new HashMap() ) );

        im.getIcons().remove( Object.class );
        im.setDefaultIcon( "default.png" );
        assertEquals( "bg_default.png", im.getBigIcon( "bla" ) );
    }

    /**
     * Test method for {@link IconManager#setSmallPrefix(java.lang.String)}.
     */
    @Test
    public void testSetPrefix() {
        assertEquals( "sm_", im.getSmallPrefix() );
        String icon = im.getSmallIcon( Object.class, Normal );
        assertEquals( "sm_object.png", icon );

        im.setSmallPrefix( "prefix/" );
        assertEquals( "prefix/object.png", im.getSmallIcon( Object.class, Normal ) );

        im.setBigPrefix( "bprefix/" );
        assertEquals( "bprefix/object.png", im.getBigIcon( Object.class, Normal ) );
    }

    /**
     * Test method for {@link IconManager#getNullIcon()}.
     */
    @Test
    public void testGetNullIcon() {
        assertNull( im.getNullIcon() );
        String string = "default";
        im.setDefaultIcon( string );
        assertSame( string, im.getNullIcon() );
        
        string = "nullIcon" ;
        im.setNullIcon( string );
        assertSame( string, im.getNullIcon() );        
    }
}
