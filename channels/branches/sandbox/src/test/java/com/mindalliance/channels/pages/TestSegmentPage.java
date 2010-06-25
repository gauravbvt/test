package com.mindalliance.channels.pages;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Iterator;

/**
 * Simple test using the WicketTester.
 */
@SuppressWarnings( {"HardCodedStringLiteral"} )
public class TestSegmentPage extends AbstractChannelsTest {

    @Autowired
    private PlanManager planManager;

    private Segment segment;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        segment = queryService.getDefaultSegment();
    }

    @Test
    public void testParms() {
        tester.startPage( PlanPage.class );
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        PageParameters parms = new PageParameters();
        parms.put( PlanPage.SEGMENT_PARM, Long.toString( segment.getId() ) );

        tester.startPage( PlanPage.class, parms );
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        parms.put( PlanPage.SEGMENT_PARM, "-1" );
        tester.startPage( PlanPage.class, parms );
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        parms.put( PlanPage.SEGMENT_PARM, "bla" );
        tester.startPage( PlanPage.class, parms );
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        parms.put( PlanPage.SEGMENT_PARM, Long.toString( segment.getId() ) );
        parms.put( PlanPage.PART_PARM, "-1" );
        tester.startPage( PlanPage.class, parms );
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        parms.put( PlanPage.PART_PARM, "bla" );
        tester.startPage( PlanPage.class, parms );
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        parms.put( PlanPage.PART_PARM, Long.toString( segment.getDefaultPart().getId() ) );
        tester.startPage( PlanPage.class, parms );
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        parms.put( PlanPage.EXPAND_PARM, "bla" );
        tester.startPage( PlanPage.class, parms );
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        parms.add( PlanPage.EXPAND_PARM, "burp" );
        tester.startPage( PlanPage.class, parms );
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();
    }

    /**
     * Test all nodes pages in default segment.
     */
    @Test
    public void testNodes() {
        Iterator<Part> parts = segment.parts();
        while ( parts.hasNext() ) {
            tester.startPage( new PlanPage( segment, parts.next() ) );
            tester.assertRenderedPage( PlanPage.class );
            tester.assertNoErrorMessage();
        }
    }

/*
    @Test
    public void testNewSegment() throws NotFoundException {
        tester.startPage( new PlanPage( segment ) );
        long size = PlanManager.plan().getSegmentCount();
        tester.clickLink( "big-form:sc-new" );
        assertEquals( size + 1, PlanManager.plan().getSegmentCount() );

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        tester.startPage( new PlanPage( segment ) );
        tester.clickLink( "big-form:sc-new" );
        assertEquals( size + 2, PlanManager.plan().getSegmentCount() );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();
    }
*/

    /**
     * Test submit with part modifications.
     *
     * @throws com.mindalliance.channels.dao.NotFoundException on error
     */
    @Test
    public void testEmptySubmit() throws NotFoundException, IOException {
        Part part = segment.getDefaultPart();

        tester.startPage( new PlanPage( segment, part ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        FormTester ft = tester.newFormTester( "big-form" );
        ft.submit();

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();
    }

    /**
     * Test submit with part modifications.
     *
     * @throws NotFoundException on error
     */
/*
    @Test
    public void testDescriptionSubmit1() throws NotFoundException, IOException {
        Part part = segment.getDefaultPart();
        part.setDescription( "" );
        assertEquals( "", part.getDescription() );

        tester.startPage( new PlanPage( segment, part ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        FormTester ft = tester.newFormTester( "big-form" );
        String desc = "New value";
        ft.setValue( "description", desc );
        ft.submit();

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        assertEquals( desc, part.getDescription() );
    }
*/

    /**
     * Test submit with part modifications.
     *
     * @throws NotFoundException on error
     */
/*
    @Test
    public void testDescriptionSubmit2() throws NotFoundException, IOException {
        Part part = segment.getDefaultPart();
        part.setDescription( "something" );

        tester.startPage( new PlanPage( segment, part ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        FormTester ft = tester.newFormTester( "big-form" );
        String desc = "";
        ft.setValue( "description", desc );
        ft.submit();

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        assertEquals( desc, part.getDescription() );
    }
*/

/*
    @Test
    public void testDeleteSegment() throws IOException, NotFoundException {
        assertEquals( 2, PlanManager.plan().getSegmentCount() );
        Segment sc2 = queryService.createSegment();
        sc2.setName( "Test" );
        assertEquals( 3, PlanManager.plan().getSegmentCount() );

        tester.startPage( new PlanPage( segment ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();
        assertEquals( 3, PlanManager.plan().getSegmentCount() );

        FormTester ft = tester.newFormTester( "big-form" );
        ft.setValue( "sc-del", "true" );

        ft.submit();
        try {
            Memory dao = planManager.getDao( PlanManager.plan() );
            assertNull( dao.find( Segment.class, segment.getId() ) );
            fail();
        } catch ( NotFoundException ignored ) {
        }
        assertEquals( 3, PlanManager.plan().getSegmentCount() );
        // the setFiles() imports/creates a new segment...

    }
*/

/*
    @Test
    public void testGetParameters1() {
        Part part = segment.getDefaultPart();
        PageParameters parms = PlanPage.getParameters( segment, part );

        assertEquals( segment.getId(), (long) parms.getAsLong( "segment" ) );
        assertEquals( part.getId(), (long) parms.getAsLong( "part" ) );
    }

    @Test
    public void testGetParameters2() {
        Part part = segment.getDefaultPart();

        Set<Long> expand = new HashSet<Long>( Arrays.asList( 1L, 2L ) );
        PageParameters parms = PlanPage.getParameters( segment, part, expand );

        assertEquals( segment.getId(), (long) parms.getAsLong( "segment" ) );
        assertEquals( part.getId(), (long) parms.getAsLong( "part" ) );

        Set<String> results = new HashSet<String>(
                Arrays.asList( parms.getStringArray( "expand" ) ) );

        assertEquals( 2, results.size() );
        assertTrue( results.contains( "1" ) );
        assertTrue( results.contains( "2" ) );
    }
*/
}
