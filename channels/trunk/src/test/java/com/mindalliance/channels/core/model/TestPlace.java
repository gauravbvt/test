// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.core.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * ...
 */
public class TestPlace {

    Place place1;
    Place place2;

    private Place planPlace;

    private Place type1;

    @Before
    public void setUp() {
        place1 = new Place( "Place 1" );
        place1.setActual();
        place1.setId( 1 );

        place2 = new Place( "Place 2" );
        place2.setActual();
        place2.setId( 2 );

        planPlace = new Place( "Model place" );
        planPlace.setId( 3 );
        planPlace.setActual();

        type1 = new Place( "Type 1" );
        type1.setId( 4 );
        type1.setType();

    }

    @Test
    public void testIsCircular1() {
        assertFalse( place1.isInvalid( null ) );

        place1.getMustContain().setPlace( place2 );
        assertFalse( place1.isInvalid( null ) );
        place2.getMustContain().setPlace( place1 );
        assertTrue( place1.isInvalid( null ) );

    }

    @Test
    public void testIsCircular2() {
        assertFalse( place1.isInvalid( null ) );

        place1.getMustContain().setPlace( place2 );
        assertFalse( place1.isInvalid( null ) );

        place1.getMustContain().setModelReferenced( true );
        assertFalse( place1.isInvalid( null ) );
    }

    @Test
    public void testIsCircular3() {
        assertFalse( place1.isInvalid( null ) );

        place1.getMustBeContainedIn().setPlace( place2 );
        assertFalse( place1.isInvalid( null ) );
        place2.getMustBeContainedIn().setPlace( place1 );
        assertTrue( place1.isInvalid( null ) );
    }

    @Test
    public void testIsCircular4() {
        assertFalse( place1.isInvalid( null ) );

        place1.getMustBeContainedIn().setPlace( place2 );
        assertFalse( place1.isInvalid( null ) );
        place1.getMustBeContainedIn().setModelReferenced( true );
        assertFalse( place1.isInvalid( null ) );
    }

    @Test
    public void testIsCircular5() {
        planPlace.getMustContain().setModelReferenced( true );

        place1.getMustContain().setModelReferenced( true );
        assertFalse( place1.isInvalid( planPlace ) );

        place2.getMustBeContainedIn().setModelReferenced( true );
        assertFalse( place2.isInvalid( planPlace ) );

    }

    @Test
    public void testIsAbsolute1() {
        assertTrue( place1.isAbsolute( null ) );

        place1.setWithin( place2 );
        place2.getMustBeContainedIn().setModelReferenced( true );
        assertFalse( place1.isAbsolute( null ) );
    }

    @Test
    public void testIsAbsolute2() {

        place1.addType( type1 );
        assertTrue( place1.isAbsolute( null ) );

        type1.getMustContain().setModelReferenced( true );
        assertFalse( place1.isAbsolute( null ) );
    }

}
