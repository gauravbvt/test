package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.ConfirmationReqDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.EmailMedium;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Playbook;
import com.mindalliance.playbook.model.Send;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Collections;

/**
 * Test the message pane.
 */
public class MessagesPageTest extends AbstractPageTest {
    
    @Mock
    private Account account;

    @Mock
    private ConfirmationReqDao reqDao;

    @Mock
    private StepDao stepDao;

    @Override
    protected Class<? extends WebPage> getTestedClass() {
        return MessagesPage.class;
    }

    @Override
    protected void init( ApplicationContextMock context ) {
        Mockito.when( account.getUserId() ).thenReturn( "someone@example.com" );
        
        context.putBean( account );
        context.putBean( reqDao );
        context.putBean( stepDao );        
    }

    @Override
    public void render() {
        super.render();

        Component component = tester.getComponentFromLastRenderedPage( "empty" );
        Assert.assertTrue( component.isVisible() );
    }

    @Test
    public void unconfirmedList() {
        Collaboration step = new Send( new Play( new Playbook( account, new Contact(
            new EmailMedium( "EMAIL", account.getUserId() ) ) ), "Test play" ) );
        Mockito.when( stepDao.getUnconfirmed() ).thenReturn( Collections.singletonList( step ) );


        tester.startPage( getTestedClass() );
        Assert.assertEquals( 200, tester.getLastResponse().getStatus() );
        tester.assertNoErrorMessage();
        tester.assertRenderedPage( getTestedClass() );
        Mockito.verify( stepDao ).getUnconfirmed();
    }

    @Test
    public void incomingList() {
        Collaboration step = new Send( new Play( new Playbook( account, new Contact(
            new EmailMedium( "EMAIL", account.getUserId() ) ) ), "Test play" ) );
        ConfirmationReq req = new ConfirmationReq( step );
        Mockito.when( reqDao.getIncomingRequests() ).thenReturn( Collections.singletonList( req ) );


        tester.startPage( getTestedClass() );
        Assert.assertEquals( 200, tester.getLastResponse().getStatus() );
        tester.assertNoErrorMessage();
        tester.assertRenderedPage( getTestedClass() );
        Mockito.verify( stepDao ).getUnconfirmed();
    }
}
