// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels;

import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.AdminPage;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.playbook.MainPage;
import com.mindalliance.channels.pages.reports.SOPsReportPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.List;

/**
 * Basic test that follows every links to make sure there are no display errors.
 * Does not validate contents or add/change data.
 */
public abstract class WalkthroughTest extends AbstractChannelsTest {

    protected WalkthroughTest( String userName, String planUri ) {
        super( userName, planUri );
    }

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
        assertRendered( "report", SOPsReportPage.class );
    }

    @SuppressWarnings( { "unchecked" } )
    @Test
    public void testAdmin() {
        assertRendered( "admin", AdminPage.class );

        Label label = (Label) tester.getComponentFromLastRenderedPage( "user" );
        assertEquals( "guest", label.getDefaultModelObjectAsString() );

        DropDownChoice<Plan> dropdown = (DropDownChoice<Plan>) tester.getComponentFromLastRenderedPage( "plan-sel" );
        User user = User.current();

        assertEquals( user.getPlan(), dropdown.getModelObject() );

        List<? extends Plan> plans = dropdown.getChoices();
        if ( plans.size() > 1 ) {
            // Only 1 plan in empty test...
            Plan nextPlan = plans.get( 1 );
            dropdown.setModelObject( nextPlan );
            assertEquals( user.getPlan(), nextPlan );

//            tester.executeAjaxEvent( "plan-sel", "onchange" );
        }
    }
}
