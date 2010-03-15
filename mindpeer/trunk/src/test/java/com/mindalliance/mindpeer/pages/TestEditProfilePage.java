// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.IntegrationTest;
import com.mindalliance.mindpeer.dao.TagDao;
import com.mindalliance.mindpeer.model.Profile;
import com.mindalliance.mindpeer.model.User;
import org.apache.wicket.util.tester.FormTester;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * ...
 */
public class TestEditProfilePage extends IntegrationTest {

    private FormTester formTester;

    @Autowired
    private TagDao tagDao;

    @Override
    @Before
    public void init() {
        super.init();
    }

    @After
    public void done() {
        logout();
    }

    @Test
    @Transactional
    @Rollback
    public void testSubmit() {
        login( "guest", "" );
        tester.startPage( EditProfilePage.class );
        tester.assertRenderedPage( EditProfilePage.class );
        formTester = tester.newFormTester( "profile" );

        formTester.setValue( "profile.name", "Test User" );
        formTester.setValue( "profile.organization", "a1" );
        formTester.setValue( "profile.location", "a2" );
        formTester.setValue( "profile.interests", "a3" );
        formTester.setValue( "profile.description", "a4" );
        formTester.setValue( "email", "a5" );
        formTester.setValue( "profile.phone", "a6" );
        formTester.setValue( "profile.fax", "a7" );
        formTester.setValue( "profile.website", "a8" );

        formTester.submit();
        tester.assertNoErrorMessage();

        User u = userDao.findByName( "guest" );
        Profile profile = u.getProfile();
        assertEquals( "Test User", profile.getName() );
        assertEquals( "a1", profile.getOrganization() );
        assertEquals( "a2", profile.getLocation() );
        assertTrue( profile.getInterests().contains( tagDao.get( "a3" ) ) );
        assertEquals( "a4", profile.getDescription() );
        assertEquals( "a5", u.getEmail() );
        assertEquals( "a6", profile.getPhone() );
        assertEquals( "a7", profile.getFax() );
        assertEquals( "a8", profile.getWebsite() );

    }

}
