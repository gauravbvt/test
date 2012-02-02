package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.model.Account;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Simple test using the WicketTester
 */
public class TodoPageTest extends AbstractPageTest {

    @Mock
    private Account account;

    @Override
    protected Class<? extends WebPage> getTestedClass() {
        return TodoPage.class;
    }

    @Override
    protected void init( ApplicationContextMock context ) {
        MockitoAnnotations.initMocks( this );
        Mockito.when( account.getEmail() ).thenReturn( "someone@somewhere.com" );

        context.putBean( "account", account );
    }
}
