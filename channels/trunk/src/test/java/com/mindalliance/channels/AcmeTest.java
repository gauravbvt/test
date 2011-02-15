// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.components.plan.PlanEditPanel;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test ACME plan.
 */
@TestExecutionListeners( AbstractChannelsTest.InstallSamplesListener.class )
public class AcmeTest extends WalkthroughTest {

    @Autowired
    private Analyst analyst;

    private PlanDao planDao;

    public AcmeTest() {
        super( "guest", "mindalliance.com/channels/plans/acme" );
    }

    @Before
    public void init() {
        planDao = getCommander().getPlanDao();
    }

    @Test
    public void verifyIssues() {
        Map<String,List<Issue>> issueMap = new HashMap<String, List<Issue>>();
        for ( ModelObject modelObject : planDao.list( ModelObject.class ) )
            for ( Issue issue : analyst.listIssues( modelObject, true, false ) ) {
                String kind = issue.getKind();
                List<Issue> kindList = issueMap.get( kind );
                if ( kindList == null ) {
                    kindList = new ArrayList<Issue>();
                    issueMap.put( kind, kindList );
                }
                kindList.add( issue );
            }

        assertEquals( 11, issueMap.size() );
        assertEquals( 1,  issueMap.get( "ActorNotInOneOrganization" ).size() );
        assertEquals( 2,  issueMap.get( "ActorWithoutContactInfo" ).size() );
        assertEquals( 13,  issueMap.get( "CommitmentWithoutRequiredAgreement" ).size() );
        assertEquals( 1,  issueMap.get( "GeonameButNoLocation" ).size() );
        assertEquals( 16, issueMap.get( "OrganizationWithNoAssignmentToCategoryOfTask" ).size() );
//        assertEquals( 1,  issueMap.get( "OrganizationWithoutAssignments" ).size() );
        assertEquals( 1,  issueMap.get( "SinglePointOfFailure" ).size() );
        assertEquals( 1,  issueMap.get( "SegmentNeverEnds" ).size() );
        assertEquals( 4,  issueMap.get( "SegmentWithSameGoal" ).size() );
        assertEquals( 5,  issueMap.get( "UnverifiedPostalCode" ).size() );
        assertEquals( 2,  issueMap.get( "UnconfirmedJob" ).size() );
     }

    @Override
    @Test
    public void testPlan() {
        super.testPlan();
        // Page is rendered for user guest
        FormTester formTester = tester.newFormTester( "big-form" );
        Plan plan = planDao.getPlan();

        // click on "About plan" in Show menu
        tester.clickLink( "big-form:planShowMenu:items:1:menuItem:link" );
        tester.assertComponentOnAjaxResponse( "big-form:plan" );
        tester.assertComponent( "big-form:plan", PlanEditPanel.class );
        assertEquals( plan.getName(), formTester.getTextComponentValue( "plan:mo:aspect:name" ) );
        assertEquals( plan.getDescription(), formTester.getTextComponentValue( "plan:mo:aspect:description" ) );
    }
}
