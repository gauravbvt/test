package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.Medium;
import com.mindalliance.sb.model.OrgType;
import com.mindalliance.sb.model.Organization;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import static junit.framework.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( locations= "/META-INF/spring/applicationContext-test.xml" )
public class FormatAdapterFactoryTest {

    private FormatAdapterFactory<Organization> factory;

    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private ConversionService conversionService;

    private final String[] orgFields = { 
                          "id", "name", "acronym", "type", "added",
                          "url", "parent", "logo", "disciplines", 
                          "contactInfoes", "incidentSystems", "organizations" };

    @Before
    public void setUp() {       
        factory = new FormatAdapterFactory<Organization>( Organization.class, conversionService, messageSource,
                                                          Organization.entityManager().getEntityManagerFactory().getMetamodel() );
        assertSame( Organization.class, factory.getObjectClass() );
    }

    @Test
    public void testGetPropertyNames() {
        Iterator<String> iterator = factory.getPropertyNames().iterator();
        for ( String e : orgFields ) {
            assertTrue( "Less properties than expected: " + e, iterator.hasNext() );
            assertEquals( e, iterator.next() );
        }

        assertTrue( "More properties returned than expected", !iterator.hasNext() );
    }

    @Test
    public void testGetVisiblePropertyNames() {
        Set<String> propertyNames = factory.getVisiblePropertyNames();
        assertEquals( 5, propertyNames.size() );

        String[] expected = { "id", "name", "acronym", "type", "added" };
        Iterator<String> iterator = propertyNames.iterator();
        for ( String e : expected )
            assertEquals( e, iterator.next() );
    }

    @Test
    public void testGetVisiblePropertyNames2() {
        FormatAdapterFactory<Medium> factory1 = new FormatAdapterFactory<Medium>( Medium.class, conversionService, messageSource,
                                                                                  Medium.entityManager().getEntityManagerFactory().getMetamodel() );

        String[] expected = { "id", "name" };
        Iterator<String> iterator = factory1.getVisiblePropertyNames().iterator();
        for ( String e : expected )
            assertEquals( e, iterator.next() );
        assertFalse( iterator.hasNext() );
    }

    @Test
    public void testGetDisplayName() {
        String name = factory.getDisplayName( "contactInfoes" );
        assertEquals( "Contacts", name );
    }
    
    @Test
    public void testMakeAdapter() {
        Organization org = new Organization();
        org.setId( 1 );
        org.setName( "Bla" );
        org.setType( OrgType.findOrgType( 1 ) );
        org.setAcronym( "Something with a \" in it..." );
        org.setAdded( GregorianCalendar.getInstance() );
        
        FormatAdapter adapter = factory.makeAdapter( org );
        assertNotNull( adapter );
        Iterator<String> iterator = Arrays.asList( orgFields ).iterator();
        for ( FormattedValue value : adapter )
            assertEquals( iterator.next(), value.getFieldName() );
        assertFalse( iterator.hasNext() );

        FormattedValue name = adapter.get( "name" );
        assertNotNull( name );
        assertEquals( "Bla", name.toString() );
        assertEquals( "Name", name.getName() );
        assertEquals( "name", name.getFieldName() );
        assertEquals( "Bla", name.getFieldValue() );
        assertTrue( name.isVisible() );
        assertTrue( name.isQuotable() );
        assertFalse( name.isNull() );
        assertEquals( "\"Bla\"", name.getJavascriptValue() );

        FormattedValue url = adapter.get( "url" );
        assertTrue( url.isNull() );
        assertTrue( url.isQuotable() );
        assertEquals( "", url.getValue() );

        FormattedValue id = adapter.get( "id" );
        assertFalse( id.isNull() );
        assertFalse( id.isQuotable() );
        assertEquals( "1", id.getValue() );
        assertEquals( "1", id.getJavascriptValue() );

        assertTrue( adapter.get( "added" ).isQuotable() );
        assertTrue( adapter.get( "type" ).isQuotable() );
        
        assertEquals( "\"Something with a \\\" in it...\"", adapter.get( "acronym" ).getJavascriptValue() );

    }
    
    @Test( expected = UnsupportedOperationException.class )
    public void testIterator() {
        Organization org = new Organization();
        org.setName( "Bla" );

        FormatAdapter adapter = factory.makeAdapter( org );
        adapter.iterator().remove();
    }
    
    @Test
    public void testDeCamelCase() {
        assertEquals( "Some Unknown Property", factory.getDisplayName( "someUnknownProperty" ) );
    }
    
    /** Just for coverage... */
    @Test
    public void testLocale() {
        assertEquals( Locale.getDefault(), factory.getLocale() );
        
        factory.setLocale( Locale.CANADA_FRENCH );
        assertEquals( Locale.CANADA_FRENCH, factory.getLocale() );

    }
}
