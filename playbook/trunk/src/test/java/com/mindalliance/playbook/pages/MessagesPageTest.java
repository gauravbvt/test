package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.ConfirmationReqDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Account;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.junit.Assert;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

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
        MockitoAnnotations.initMocks( this );

        Mockito.when( account.getEmail() ).thenReturn( "someone@example.com" );
        
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
}
