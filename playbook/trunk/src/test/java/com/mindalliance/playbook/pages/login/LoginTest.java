package com.mindalliance.playbook.pages.login;

import com.mindalliance.playbook.pages.AbstractPageTest;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.test.ApplicationContextMock;

/**
 * Login page test.
 */
public class LoginTest extends AbstractPageTest {

    @Override
    protected Class<? extends WebPage> getTestedClass() {
        return Login.class;
    }

    @Override
    protected void init( ApplicationContextMock context ) {
    }
}
