// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.definitions;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for typology object.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class TypologyTest {

    private Typology typology;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        typology = new Typology();
    }

    /**
     * Test method for {@link Typology#Typology()}.
     */
    @Test
    public void testTypology() {
        Discipline defaultDiscipline = typology.getDefaultDiscipline();
        assertNotNull( defaultDiscipline );
        Set<Discipline> disciplines = typology.getDisciplines();
        assertEquals( 1, disciplines.size() );
        assertTrue( disciplines.contains( defaultDiscipline ) );
    }

    /**
     * Test method for {@link Typology#Typology(java.lang.String)}.
     */
    @Test
    public void testTypologyString() {
        assertEquals( "Anonymous", typology.getName() );
        String name = "bla";
        typology = new Typology( null, name );
        assertSame( name, typology.getName() );
    }

    /**
     * Test method for {@link Typology#getCategory(Discipline, java.lang.String)}.
     */
    @Test
    public void testGetCategory() {
        Discipline d = typology.getDefaultDiscipline();
        Category category = new Category();
        String name = "bla";
        category.setName( name );

        d.addCategory( category );
        assertSame( category, typology.getCategory( d, name ) );
    }

    /**
     * Test method for {@link Typology#setDefaultDiscipline(Discipline)}.
     */
    @Test
    public void testSetDefaultDiscipline() {
        Discipline d = new Discipline();
        typology.setDefaultDiscipline( d );
        assertSame( d, typology.getDefaultDiscipline() );
    }

    /**
     * Test method for {@link Typology#addDiscipline(Discipline)}.
     */
    @Test
    public void testAddDiscipline() {
        assertEquals( 1, typology.getDisciplines().size() );
        Discipline d = new Discipline( null, "bla" );
        typology.addDiscipline( d );
        assertEquals( 2, typology.getDisciplines().size() );
        typology.addDiscipline( d );
        assertEquals( 2, typology.getDisciplines().size() );
    }
}
