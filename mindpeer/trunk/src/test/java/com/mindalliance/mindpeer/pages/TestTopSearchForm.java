// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.IntegrationTest;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;

/**
 * ...
 */
public class TestTopSearchForm extends IntegrationTest {

    @Test
    @Transactional
    @Rollback
    public void testForm() throws URISyntaxException {
        login( "guest", "" );
        try {
            assertRendered( "focus", FocusPage.class );
            FormTester formTester = tester.newFormTester( "search" );

            formTester.setValue( "q", "foo" );
            formTester.submit();

            tester.assertRenderedPage( SearchResultPage.class );
            Assert.assertEquals( "foo",
                    tester.getLastRenderedPage().getPageParameters().getString( "q" ) );

        } finally {
            logout();
        }
    }

}
