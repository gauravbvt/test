package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.ContactInfo;
import com.mindalliance.sb.model.OrgType;
import com.mindalliance.sb.model.Organization;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;

import static junit.framework.Assert.*;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = "/META-INF/spring/applicationContext-test.xml" )
public class CsvWriterTest {

    @Autowired
    private FormatAdapterFactoryService factoryService;

    private static final String HEADER = "Id,Name,Acronym,Type,Added,Url,Parent,Disciplines,Contacts,Incident Systems,Organizations";

    @Test
    public void testEmptyList() throws IOException {
        compareCsvLines( new ArrayList<Organization>(), HEADER );
    }

    @Test
    public void testBlank() throws IOException {
        compareCsvLines( Arrays.asList( new Organization() ), HEADER, ",,,,,,,,,," );
    }

    @Test
    public void testOutput1() throws IOException {
        Organization org1 = new Organization();
        org1.setName( "Bla Inc" );
        org1.setAcronym( "BI" );
        org1.setType( OrgType.findOrgType( 4 ) );
        org1.setUrl( "http://bla.com" );
        org1.persist();

        org1.setContactInfoes( new LinkedHashSet<ContactInfo>( Arrays.asList( 
            org1.addContact( "John Q. Public", "VP", "jqp@bla.com" ),
            org1.addContact( "Jane Doe", "CEO", "jd@bla.com" ) ) ) );
        Calendar calendar = Calendar.getInstance();
        calendar.set( 2013, Calendar.JULY, 2, 14, 30, 27 );
        org1.setAdded( calendar );

        Organization org2 = new Organization();
        org2.setName( "Big \"Bad\" Corp" );
        org2.setAcronym( "BC" );
        org2.setType( OrgType.findOrgType( 3 ) );
        org2.setParent( org1 );
        org2.persist();
        org2.setAdded( calendar );

        compareCsvLines( Arrays.asList( org1, org2 ),
                         HEADER,
                         "4,Bla Inc,BI,Tribal Government,7/02/2013 14:30:27,http://bla.com,,,\"John Q. Public <jqp@bla.com>, Jane Doe <jd@bla.com>\",,",
                         "5,\"Big \"\"Bad\"\" Corp\",BC,Local Government,7/02/2013 14:30:27,,Bla Inc (BI),,,," );
    }

    private void compareCsvLines( List<Organization> list, String... lines ) throws IOException {
        StringWriter stringWriter = new StringWriter();
        FormatAdapterFactory<Organization> formatterFactory = factoryService.getFactory( Organization.class );
        final CsvWriter<Organization> csvWriter = new CsvWriter<Organization>( list, formatterFactory );
        csvWriter.output( new PrintWriter( stringWriter ), formatterFactory );

        BufferedReader reader = new BufferedReader( new StringReader( stringWriter.toString() ) );

        for ( String line : lines ) {
            assertTrue( reader.ready() );
            assertEquals( line, reader.readLine() );
        }
        assertEquals( "More lines returned than expected", -1, reader.read() );
    }
}
