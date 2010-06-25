// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Plan;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.util.tester.FormTester;
import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestExecutionListeners;

/**
 * ...
 */
@SuppressWarnings( { "unchecked" } )
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
        tester.assertComponentOnAjaxResponse( "users" );
        Plan acme = planManager.getPlansWithUri( ACME ).get( 0 );
        assertEquals( acme, User.current().getPlan() );
    }

    @Test
    public void testUserCreation() {
        assertRendered( "admin", AdminPage.class );
        try {
            User u = (User) userService.loadUserByUsername( "aaaa" );
            // fail( "side-effect from previous run" );
            userService.deleteUser( u );
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
    }

    @Test
    public void testDelete() {
        // Note:  side-effect from testUserCreation()
        User aaaa = (User) userService.loadUserByUsername( "aaaa" );
        assertNotNull( aaaa );

        try {
            userService.loadUserByUsername( "aaaa2" );
            // fail( "side-effect from previous run" );
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


}
