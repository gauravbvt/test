package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.model.Account;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

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
        when( account.getUserId() ).thenReturn( "someone@somewhere.com" );

        context.putBean( account );
    }
}
