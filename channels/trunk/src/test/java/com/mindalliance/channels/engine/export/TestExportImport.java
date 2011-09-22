package com.mindalliance.channels.engine.export;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.core.dao.Exporter;
import com.mindalliance.channels.core.dao.ImportExportFactory;
import com.mindalliance.channels.core.dao.Importer;
import com.mindalliance.channels.core.dao.PlanDao;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Segment;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 4:23:11 PM
 */
public class TestExportImport extends AbstractChannelsTest {

    @Autowired
    private ImportExportFactory importExportFactory;

    private List<String> segmentNames;

    private PlanDao planDao;

    @Override
    public void setUp() throws IOException {
        super.setUp();

        segmentNames = new ArrayList<String>();
        for ( Segment segment : queryService.list( Segment.class ) ) {
            segmentNames.add( segment.getName() );
        }
        Plan plan = User.current().getPlan();
        planDao = planManager.getDao( plan );
    }

// TODO fix export/import test
    @Test
    public void testExportImportSegment() throws IOException, NotFoundException {
//        Map<String, String> exported0 = new HashMap<String, String>();
//        Map<String, String> exported1 = new HashMap<String, String>();
//        Map<String, String> exported2 = new HashMap<String, String>();
//        // allow removal of all named segments by creating an empty one
//        queryService.createSegment();
//        // Export all named segments
//        exportAll( exported0 );
//        removeAll();
//        importAll( exported0 );
//        // re-export
//        exportAll( exported1 );
//        // Import in reverse order
//        removeAll();
//        Collections.reverse( segmentNames );
//        importAll( exported0 );
//        // re-export
//        exportAll( exported2 );
//        // Make sure all xml serializations are similar
//        for ( String name : segmentNames ) {
//            // export vs export-import-export
//            compare( exported0.get( name ), exported1.get( name ) );
//            // export vs export-reverse import-export
//            compare( exported1.get( name ), exported2.get( name ) );
//        }
    }

    private void compare( String xml, String otherXml ) throws IOException {
        // for now just check if same number of lines
        assertEquals( "same number of lines", countLines( xml ), countLines( otherXml ) );
    }

    private int countLines( String text ) throws IOException {
        int count = 0;
        BufferedReader reader = new BufferedReader( new StringReader( text ) );
        while ( reader.readLine() != null )
            count++;
        return count;
    }

    private void exportAll( Map<String, String> exported ) throws IOException, NotFoundException {
        for ( String name : segmentNames ) {
            Segment segment = planDao.findSegment( name );
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Exporter exporter = importExportFactory.createExporter( "daemon", planDao );
            exporter.export( segment, out );
            String xml = out.toString();
            // System.out.println( xml );
            exported.put( name, xml );
            assertTrue( xml.length() > 0 );
        }
    }

    private void removeAll() throws NotFoundException {
        for ( String name : segmentNames ) {
            queryService.remove( planDao.findSegment( name ) );
        }
    }

    private void importAll( Map<String, String> exported ) throws IOException {
        for ( String name : segmentNames ) {
            String xml = exported.get( name );
            ByteArrayInputStream in = new ByteArrayInputStream( xml.getBytes() );
            Importer importer = importExportFactory.createImporter( "daemon", planDao );
            Segment segment = importer.importSegment( in );
            assertEquals( name, segment.getName() );
        }
    }
}
