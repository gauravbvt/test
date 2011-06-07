// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages.reports.responders;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.AbstractChannelsTest.InstallSamplesListener;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.dao.UserInfo;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.query.PlanService;
import org.junit.Test;
import org.springframework.test.context.TestExecutionListeners;

import javax.servlet.http.HttpServletResponse;

/** ... */

@TestExecutionListeners ( InstallSamplesListener.class )
public class TestParticipantPage extends AbstractChannelsTest {

    @Test
    public void testPlanner() {
        login( "denis" );
        assertRendered( "participants", AllParticipants.class );
    }

    @Test
    public void testNormal() {
        associate( "guest", "mindalliance.com/channels/plans/railsec", "Joseph Neumann" );
        assertRendered( "participants", ParticipantPage.class );
    }

    @Test
    public void testBadUri() {
        associate( "guest", "mindalliance.com/channels/plans/railsec", "Joseph Neumann" );
        assertErrorRendering( "participants?plan=mindalliance.com/channels/plans/waitasec&v=1",
                              HttpServletResponse.SC_NOT_FOUND );
    }

    @Test
    public void testBadValue() {
        associate( "guest", "mindalliance.com/channels/plans/railsec", "Joseph Neumann" );
        assertErrorRendering( "participants?plan=mindalliance.com/channels/plans/railsec&v=abc",
                              HttpServletResponse.SC_NOT_FOUND );
    }

    @Test
    public void testBadValue2() {
        associate( "guest", "mindalliance.com/channels/plans/railsec", "Joseph Neumann" );
        assertErrorRendering( "participants?plan=mindalliance.com/channels/plans/railsec&v=1&agent=booboo",
                              HttpServletResponse.SC_NOT_FOUND );
    }

    @Test
    public void testNoParticipation() {
        associate( "guest", "mindalliance.com/channels/plans/railsec", "Joseph Neumann" );
        assertErrorRendering( "participants?plan=mindalliance.com/channels/plans/acme&v=1&agent=234",
                              HttpServletResponse.SC_NOT_FOUND );
    }



    private void associate( String userName, String planUri, String agentName ) {
        login( "denis" );
        User user = userService.getUserNamed( userName );
        user.setUserInfo( new UserInfo( userName, "bla,Test,bla,[" + planUri + "|ROLE_USER]" ) );

        Plan devPlan = planManager.findDevelopmentPlan( planUri );
        PlanService service = new PlanService( planManager, null, userService, devPlan );

        planManager.productize( devPlan );
        Participation participation = new Participation( userName );
        Actor actor = service.findActualEntity( Actor.class, agentName );
        participation.setActor( actor );
        participation.setActual();
        service.add( participation );

        setPlanUri( planUri );

        logout();
        login( userName );
    }

}
