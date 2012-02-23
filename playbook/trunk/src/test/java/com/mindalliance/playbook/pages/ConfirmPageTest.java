package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.ConfirmationReqDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Playbook;
import com.mindalliance.playbook.model.Send;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.junit.Assert;
import org.mockito.Mock;

import java.util.Date;

/**
 * Test confirmation requests.
 */
public class ConfirmPageTest extends AbstractPageTest {

    private static final String EMAIL = "someone@example.com";

    @Mock
    private StepDao stepDao;

    @Mock
    private ConfirmationReqDao dao;

    private Collaboration collaboration;

    @Override
    protected Class<? extends WebPage> getTestedClass() {
        return ConfirmPage.class;
    }

    @Override
    protected void init( ApplicationContextMock context ) {
        Account account = new Account( "someone@example.com", new Date() );
        Play play = new Play( new Playbook( account ), "Test play" );
        collaboration = new Send( play );
        Contact with = new Contact( account, EMAIL );
        with.setGivenName( "Bob" );
        collaboration.setWith( with );
        
        context.putBean( stepDao );
        context.putBean( dao );
    }

    @Override
    public void render() {
        tester.startPage( new ConfirmPage( collaboration ) );
        Assert.assertEquals( 200, tester.getLastResponse().getStatus() );
        tester.assertNoErrorMessage();
        tester.assertRenderedPage( ConfirmPage.class );

    }
}
