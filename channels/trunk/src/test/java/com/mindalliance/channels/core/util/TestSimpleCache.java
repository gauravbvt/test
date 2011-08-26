package com.mindalliance.channels.core.util;

import junit.framework.TestCase;

import java.util.Date;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 15, 2008
 * Time: 7:46:02 PM
 */
public class TestSimpleCache extends TestCase {

    SimpleCache<String,String> cache;
    Date epoch;

    protected void setUp() {
        epoch = new Date();
        cache = new SimpleCache<String,String>();
    }

    public void testCaching() {
        String value;
        value = cache.get("a", new Date());
        assertTrue(value == null);
        cache.put( "a", "alphabet" );
        value = cache.get("a", epoch);
        assertTrue(value.equals("alphabet"));
        try {
            Thread.sleep( 100 );
        } catch ( InterruptedException e ) {
            fail();
        }
        value = cache.get("a", new Date());
        assertTrue(value == null);
        value = cache.get("a", epoch);
        assertTrue(value == null);
        cache.put( "a", "arid" );
        value = cache.get("a", epoch);
        assertTrue(value.equals("arid"));
        cache.put( "a", "asterisk" );
        value = cache.get("a", epoch);
        assertTrue(value.equals("asterisk"));
    }

}
