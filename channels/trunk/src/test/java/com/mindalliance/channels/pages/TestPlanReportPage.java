package com.mindalliance.channels.pages;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.pages.reports.PlanReportPage;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 7, 2009
 * Time: 2:54:01 PM
 */
public class TestPlanReportPage extends AbstractChannelsTest {

    public void testPage() {
        tester.startPage( PlanReportPage.class );
        tester.assertRenderedPage( PlanReportPage.class );
        tester.assertNoErrorMessage();
    }


}
