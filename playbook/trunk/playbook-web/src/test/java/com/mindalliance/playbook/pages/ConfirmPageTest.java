package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.ConfirmationReqDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.EmailMedium;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Send;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.junit.Assert;
import org.mockito.Mock;

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
        final EmailMedium email = new EmailMedium( "EMAIL", "someone@example.com" );
        Account account = new Account( "playbook", "someone@example.com", new Contact( email ) );
        Play play = new Play( account.getPlaybook(), "Test play" );
        collaboration = new Send( play );
        Contact with = new Contact( new EmailMedium( "EMAIL", EMAIL ) );
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
