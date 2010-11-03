// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.Organization;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.TestExecutionListeners;

import java.util.List;

/**
 * Quick tests for report selector.
 */
@TestExecutionListeners( AbstractChannelsTest.InstallSamplesListener.class )
public class TestSelectorPanel extends AbstractChannelsTest {

    private SelectorPanel panel;

    public TestSelectorPanel() {
        super( "denis", "mindalliance.com/channels/plans/railsec" );
    }

    @Before
    public void init() {
        assertRendered( "report", SOPsReportPage.class );
        panel = (SelectorPanel) tester.getLastRenderedPage().get( "selector" );
    }

    @Test
    public void testAllSegments() {
        List<Segment> segments = panel.getSegments();
        Assert.assertEquals( 3, segments.size() );

        Segment s = segments.get( 0 );
        panel.setSegment( s );

        List<Segment> segs2 = panel.getSegments();
        Assert.assertEquals( 1, segs2.size() );
        Assert.assertSame( s, segs2.get( 0 ) );
    }

    @Test
    public void testAllSegments2() {
        Organization org = panel.getOrganizations().get( 0 );
        panel.setOrganization( org );

        List<Segment> segments = panel.getSegments();
        Assert.assertEquals( 1, segments.size() );
    }
}
