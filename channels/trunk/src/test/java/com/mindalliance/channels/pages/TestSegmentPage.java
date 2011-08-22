package com.mindalliance.channels.pages;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import org.apache.wicket.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;

/**
 * Simple test using the WicketTester.
 */
@SuppressWarnings( { "HardCodedStringLiteral" } )
public class TestSegmentPage extends AbstractChannelsTest {

    private Segment segment;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        segment = queryService.getDefaultSegment();
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
     * @throws NotFoundException on error
     * @throws IOException on error
     */
    @Test
    public void testEmptySubmit() throws NotFoundException, IOException {
        Part part = segment.getDefaultPart();

        tester.startPage( new PlanPage( segment, part ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        FormTester ft = tester.newFormTester( "indicator:big-form" );
        ft.submit();

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
}
