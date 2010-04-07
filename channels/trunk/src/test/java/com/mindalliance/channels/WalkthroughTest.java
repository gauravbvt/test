// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels;

import com.mindalliance.channels.pages.AdminPage;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.playbook.MainPage;
import com.mindalliance.channels.pages.reports.PlanReportPage;
import org.apache.wicket.markup.html.basic.Label;
import org.junit.Assert;
import org.junit.Test;

/**
 * Basic test that follows every links to make sure there are no display errors.
 * Does not validate contents or add/change data.
 */
public abstract class WalkthroughTest extends AbstractChannelsTest {

    protected WalkthroughTest( String userName, String planUri ) {
        super( userName, planUri );
    }

//    public void testPages() {
//        testPlan();
//        testPlaybook();
//        testReport();
//        testAdmin();
//    }

    @Test
    public void testPlan() {
        assertRendered( "plan", PlanPage.class );
    }

    @Test
    public void testPlaybook() {
        assertRendered( "playbooks", MainPage.class );
    }

    @Test
    public void testReport() {
        assertRendered( "report", PlanReportPage.class );
    }

    @Test
    public void testAdmin() {
        assertRendered( "admin", AdminPage.class );

        Label label = (Label) tester.getComponentFromLastRenderedPage( "user" );
        Assert.assertEquals( "guest", label.getDefaultModelObjectAsString() );
    }
}
