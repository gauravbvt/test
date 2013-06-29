package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.AckDao;
import com.mindalliance.playbook.dao.ConfirmationReqDao;
import com.mindalliance.playbook.dao.ContactDao;
import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.EmailMedium;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Send;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Confirmation acknowledgement page.
 */
public class AckPageTest extends AbstractPageTest {

    @Mock
    private PlayDao playDao;

    @Mock
    private AckDao ackDao;

    @Mock
    private StepDao stepDao;

    private Account account;

    @Mock
    private ContactDao contactDao;

    @Mock
    private ConfirmationReqDao reqDao;

    private ConfirmationReq req;

    @Override
    protected Class<? extends WebPage> getTestedClass() {
        return AckPage.class;
    }

    @Override
    protected void init( ApplicationContextMock context ) {
        account = new Account(
            "playbook",
            "someone@example.com",
            new Contact( new EmailMedium( "EMAIL", "someone@example.com" ) ) );
        Send collaboration = new Send( new Play( account.getPlaybook(), "Test play" ) );
        req = new ConfirmationReq( collaboration );

        context.putBean( playDao );
        context.putBean( ackDao );
        context.putBean( stepDao );
        context.putBean( account );
        context.putBean( reqDao );
        context.putBean( contactDao );
    }

    @Override
    @Test
    public void render() {
        tester.startPage( new AckPage( req ) );
        Assert.assertEquals( 200, tester.getLastResponse().getStatus() );
        tester.assertNoErrorMessage();
        tester.assertRenderedPage( AckPage.class );
    }
}
