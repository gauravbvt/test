// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.definitions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.mindalliance.channels.data.definitions.Category.Taxonomy;

import static org.junit.Assert.* ;

/**
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class TypologyFactoryTest {

    private TypologyFactory factory;
    private File file; 

    @Before
    public void setUp() {
        factory = new TypologyFactory();
        file = new File( "/tmp/typology.yaml" );
    }
    
    @Test
    public void testSave() throws IOException {
        FileOutputStream stream = new FileOutputStream( file );
        factory.save( createTypology(), stream );
        stream.close();
    }
    
    @Test
    public void testLoad() throws IOException {
        FileInputStream stream = new FileInputStream( file );
        factory = new TypologyFactory( stream );
        Typology t = factory.getTypology();
        stream.close();
        verifyTypology( t );
    }
    
    @Test
    public void testLoad_2() throws IOException {
        FileInputStream stream = new FileInputStream( file );
        Typology t = factory.load( stream );
        stream.close();
        verifyTypology( t );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetCategory_1() throws IOException {
        FileInputStream stream = new FileInputStream( file );
        factory = new TypologyFactory( stream );
        stream.close();

        factory.getCategorySet( "Health", "Bla" );
    }

    @Test
    public void testGetCategory_2() throws IOException {
        FileInputStream stream = new FileInputStream( file );
        factory = new TypologyFactory( stream );
        Typology t = factory.getTypology();
        stream.close();

        CategorySet categorySet = factory.getCategorySet( "Health", "Plague" );
        assertNotNull( categorySet );
        assertEquals( 1, categorySet.getCategories().size() );
        assertTrue( categorySet.getCategories().contains( t.getDiscipline( "Health" ).getCategory( "Plague" ) ) );
        
        
        //file.delete();
    }

    private Typology createTypology() {
        Typology t = new Typology( null, "test" );
        Discipline d1 = new Discipline( null, "Law Enforcement" );
        Discipline d2 = new Discipline( null, "Health" );

        t.addDiscipline( d1 );
        t.addDiscipline( d2 );

        Category a = new Category( "Accident", Taxonomy.Event );
        d1.addCategory( a );
        d1.addCategory( new Category( "Train derailment", Taxonomy.Event, a ) );
        d1.addCategory( new Category( "Plane crash", Taxonomy.Event, a ) );

        Discipline common = t.getDefaultDiscipline();
        Category l1 = new Category( "State", Taxonomy.Location );
        common.addCategory( l1 );
        Category l2 = new Category( "County", Taxonomy.Location, l1 );
        common.addCategory( l2 );
        Category l3 = new Category( "Municipality", Taxonomy.Location, l2 );
        common.addCategory( l3 );

        d2.addCategory( new Category( "Plague", Taxonomy.Event ) );
        return t;
    }

    private void verifyTypology( Typology t ) {
        assertNotNull( t );
        assertEquals( "test", t.getName() );
        
        Set<Discipline> disciplines = t.getDisciplines();
        assertEquals( 3, disciplines.size() );
        Discipline d1 = t.getDiscipline( "Law Enforcement" );
        assertNotNull( d1 );
        assertSame( d1.getParents().iterator().next(), t.getDefaultDiscipline() );
        Discipline d2 = t.getDiscipline( "Health" );
        assertNotNull( d2 );
        assertSame( d2.getParents().iterator().next(), t.getDefaultDiscipline() );

        assertEquals( 3, t.getDefaultDiscipline().getCategories().size() );
        NamedObject a = d1.getCategory( "Accident" );
        assertNotNull( a );
        Category c2 = d1.getCategory( "Train derailment" );
        assertNotNull( c2 );
        assertTrue( c2.getImplications().contains( a ) );
        assertNotNull( d1.getCategory( "Plane crash" ) );
        assertNotNull( d1.getCategory( "State" ) );
        assertEquals( 3, d1.getCategories().size() );
        assertEquals( 6, d1.getAllCategories( Taxonomy.Any ).size() );
        assertNotNull( d2.getCategory( "Plague" ) );
        assertNotNull( d2.getCategory( "State" ) );
        assertEquals( 1, d2.getCategories().size() );        
        assertEquals( 4, d2.getAllCategories( Taxonomy.Any ).size() );
    }
}
