package com.mindalliance.playbook.pages.login;

import com.mindalliance.playbook.pages.AbstractPageTest;
import com.mindalliance.playbook.services.SocialHub;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Login page test.
 */
public class LoginTest extends AbstractPageTest {
    
    @Mock
    private SocialHub socialHub;

    @Override
    protected Class<? extends WebPage> getTestedClass() {
        return Login.class;
    }

    @Override
    protected void init( ApplicationContextMock context ) {
        MockitoAnnotations.initMocks( this );
        context.putBean( socialHub );
    }
}
