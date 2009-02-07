package com.mindalliance.channels.pages;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.pages.reports.ProjectReportPage;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 7, 2009
 * Time: 2:54:01 PM
 */
public class TestProjectReportPage extends AbstractChannelsTest {

    public void testPage() {
        tester.startPage( ProjectReportPage.class );
        tester.assertRenderedPage( ProjectReportPage.class );
        tester.assertNoErrorMessage();
    }


}
