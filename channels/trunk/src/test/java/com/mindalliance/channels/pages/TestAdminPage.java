// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.dao.PlanDefinition;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Plan;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestExecutionListeners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * ...
 */
@SuppressWarnings( "unchecked" )
@TestExecutionListeners( AbstractChannelsTest.InstallSamplesListener.class )
public class TestAdminPage extends AbstractChannelsTest {

    private static final String DEMO = "mindalliance.com/channels/plans/demo";

    private static final String ACME = "mindalliance.com/channels/plans/acme";

    public TestAdminPage() {
        super( "denis", DEMO );
    }

    @Test
    public void testPlanSelection() {
        assertRendered( "admin", AdminPage.class );
        AdminPage adminPage = (AdminPage) tester.getLastRenderedPage();

        FormTester form = tester.newFormTester( "users" );
        DropDownChoice<Plan> dropDown = (DropDownChoice<Plan>) adminPage.get( "users:plan-sel" );

        Plan plan = dropDown.getModelObject();
        assertEquals( DEMO, plan.getUri() );
        assertEquals( plan, User.current().getPlan() );

        form.select( "plan-sel", 0 );
        tester.executeAjaxEvent( "users:plan-sel", "onchange" );
        tester.assertRenderedPage( AdminPage.class );
        Plan acme = planManager.getPlansWithUri( ACME ).get( 0 );
        assertEquals( acme, User.current().getPlan() );
    }

    @Test
    public void testPlanSupportField() {
        assertRendered( "admin", AdminPage.class );
        AdminPage adminPage = (AdminPage) tester.getLastRenderedPage();

        Plan plan = adminPage.getPlan();
        assertEquals( "", plan.getPlannerSupportCommunity() );

        FormTester form = tester.newFormTester( "users" );
        form.setValue( "plannerSupportCommunity", "AA" );
        form.submit();

        assertEquals( "AA", plan.getPlannerSupportCommunity() );
    }

    @Test
    public void testUserCreation() {
        assertRendered( "admin", AdminPage.class );
        try {
            User u = (User) userService.loadUserByUsername( "aaaa" );
            userService.deleteUser( u );
            fail( "side-effect from previous run" );
        } catch ( UsernameNotFoundException ignored ) {
            // OK
        }

        FormTester form = tester.newFormTester( "users" );
        form.setValue( "new", "aaaa" );
        form.submit();
        tester.assertNoErrorMessage();

        User aaaa = (User) userService.loadUserByUsername( "aaaa" );
        assertNotNull( aaaa );
        assertEquals( "aaaa", aaaa.getUsername() );
        assertFalse( aaaa.isEnabled() );

        form = tester.newFormTester( "users" );
        form.select( "item:0:group", 0 );
        form.setValue( "item:0:group:email", "aaaa@example.com" );
        form.setValue( "item:0:group:fullName", "Aaron Aardvark" );
        form.setValue( "item:0:group:password", "test" );
        form.submit();
        tester.assertNoErrorMessage();
        assertTrue( aaaa.isAdmin() );
        assertEquals( "aaaa@example.com", aaaa.getEmail() );
        assertEquals( "Aaron Aardvark", aaaa.getFullName() );
        assertEquals( "098f6bcd4621d373cade4e832627b4f6", aaaa.getPassword() );

        form = tester.newFormTester( "users" );
        form.select( "item:0:group", 2 );
        form.submit();
        tester.assertNoErrorMessage();
        assertFalse( aaaa.isAdmin() );
        assertFalse( aaaa.isPlanner( null ) );
        assertTrue( aaaa.isParticipant( null ) );

        form = tester.newFormTester( "users" );
        form.select( "item:0:group", 3 );
        form.submit();
        tester.assertNoErrorMessage();
        assertFalse( aaaa.isEnabled() );

        form = tester.newFormTester( "users" );
        form.select( "item:0:group", 4 );
        form.submit();
        tester.assertNoErrorMessage();
        assertTrue( aaaa.isEnabled() );
        assertFalse( aaaa.isPlanner( null ) );
        assertTrue( aaaa.isPlanner( User.current().getPlan().getUri() ) );

        form = tester.newFormTester( "users" );
        form.select( "item:0:group", 5 );
        form.submit();
        tester.assertNoErrorMessage();
        assertTrue( aaaa.isParticipant( User.current().getPlan().getUri() ) );

        form = tester.newFormTester( "users" );
        form.select( "item:0:group", 1 );
        form.submit();
        tester.assertNoErrorMessage();
        assertFalse( aaaa.isAdmin() );
        assertTrue( aaaa.isPlanner( null ) );

        form = tester.newFormTester( "users" );
        form.select( "item:0:group", 6 );
        form.submit();
        tester.assertNoErrorMessage();
        assertFalse( aaaa.isPlanner( DEMO ) );
        assertTrue( aaaa.isPlanner( ACME ) );

        form = tester.newFormTester( "users" );
        form.setValue( "new", "aaaa" );
        form.submit();
        tester.assertErrorMessages( new String[]{ "User aaaa already exists" } );

    }

    @Test
    public void testDeleteUser() {
        // Note:  side-effect from testUserCreation()
        User aaaa = (User) userService.loadUserByUsername( "aaaa" );
        assertNotNull( aaaa );

        try {
            userService.loadUserByUsername( "aaaa2" );
            fail( "side-effect from previous run" );
        } catch ( UsernameNotFoundException ignored ) {
            userService.createUser( "aaaa2" );
        }

        assertRendered( "admin", AdminPage.class );
        FormTester form = tester.newFormTester( "users" );

        form.setValue( "item:0:group:delete", true );
        form.setValue( "item:1:group:delete", true );
        form.submit();

        tester.assertNoErrorMessage();
        try {
            userService.loadUserByUsername( "aaaa" );
            fail();
        } catch ( UsernameNotFoundException ignored ) {
            // ok
            assertFalse( aaaa.isEnabled() );
        }
        try {
            userService.loadUserByUsername( "aaaa2" );
            fail();
        } catch ( UsernameNotFoundException ignored ) {
            // ok
        }
    }

    @Test
    public void testCreatePlan() {
        assertRendered( "admin", AdminPage.class );

        FormTester form = tester.newFormTester( "users" );
        form.setValue( "newPlanUri", DEMO );
        form.submit();
        tester.assertRenderedPage( AdminPage.class );
        tester.assertErrorMessages( new String[]{ "A plan with that uri already exists" } );

        String uri = "brand.spanking.new.plan";
        String client = "Bob Customer";

        form = tester.newFormTester( "users" );
        form.setValue( "newPlanUri", uri );
        form.setValue( "newPlanClient", client );
        form.submit();
        tester.assertRenderedPage( AdminPage.class );
        tester.assertNoErrorMessage();

        PlanDao dao = planManager.getDao( uri, true );
        Plan plan = dao.getPlan();

        assertEquals( client, plan.getClient() );
        assertEquals( uri, plan.getUri() );
        assertNotNull( plan.getDefaultEvent() );
        assertNotNull( plan.getDefaultPhase() );
        assertNotNull( plan.getDefaultSegment() );
    }
    @Test
    public void testProductize() {
        User guest = userService.getUserNamed( "guest" );
        planManager.setAuthorities( guest, "ROLE_USER", null  );

        assertRendered( "admin", AdminPage.class );

        FormTester form = tester.newFormTester( "users" );
        form.select( "plan-sel", 0 );
        tester.executeAjaxEvent( "users:plan-sel", "onchange" );
        AdminPage page = (AdminPage) tester.getLastRenderedPage();
        assertEquals( ACME, page.getPlan().getUri() );

        PlanDefinition planDefinition = planManager.getDefinitionManager().get( ACME );

        assertNull( planDefinition.getProductionVersion() );
        tester.executeAjaxEvent( "users:productize", "onclick" );

        assertRendered( "admin", AdminPage.class );
        assertEquals( 2, planManager.getPlansWithUri( ACME ).size() );
        assertTrue( planDefinition.getPlanDirectory().exists() );

        Plan newDev = planManager.findDevelopmentPlan( ACME );
        assertNotNull( newDev );
        assertEquals( 2, newDev.getVersion() );

        Plan newProd = planManager.findProductionPlan( ACME );
        assertNotNull( newProd );
        assertSame( newProd, guest.getPlan() );
        assertEquals( 1, newProd.getVersion() );

        // Productize again
        tester.executeAjaxEvent( "users:productize", "onclick" );

        Plan newestProd = planManager.findProductionPlan( ACME );
        assertNotNull( newestProd );
        assertSame( newestProd, guest.getPlan() );
        assertEquals( 2, newestProd.getVersion() );

        assertTrue( newProd.isRetired() );
        assertEquals( 3, planManager.findDevelopmentPlan( ACME ).getVersion() );

    }


    @Test
    public void testDeletePlan() {
        assertRendered( "admin", AdminPage.class );
        FormTester form = tester.newFormTester( "users" );
        form.select( "plan-sel", 1 );
        tester.executeAjaxEvent( "users:plan-sel", "onchange" );
        AdminPage page = (AdminPage) tester.getLastRenderedPage();
        assertEquals( ACME, page.getPlan().getUri() );

        PlanDefinition planDefinition = planManager.getDefinitionManager().get( ACME );

        tester.executeAjaxEvent( "users:deletePlan", "onclick" );

        assertRendered( "admin", AdminPage.class );
        assertEquals( 0, planManager.getPlansWithUri( ACME ).size() );
        assertFalse( planDefinition.getPlanDirectory().exists() );
    }

}
