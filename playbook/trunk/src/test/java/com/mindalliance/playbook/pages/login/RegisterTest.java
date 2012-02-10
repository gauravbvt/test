package com.mindalliance.playbook.pages.login;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.pages.AbstractPageTest;
import com.octo.captcha.service.image.ImageCaptchaService;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Test the registration page.
 */
public class RegisterTest extends AbstractPageTest {

    private String serverUrl = "http://example.com";

    @Mock
    private AccountDao accountDao;

    @Mock
    private ImageCaptchaService captchaService;

    @Mock
    private JavaMailSender mailSender;

    private String from = "Test";

    @Override
    protected Class<? extends WebPage> getTestedClass() {
        return Register.class;
    }

    @Override
    protected void init( ApplicationContextMock context ) {
        MockitoAnnotations.initMocks( this );

        context.putBean( "serverUrl", serverUrl );
        context.putBean( "from", serverUrl );
        context.putBean( accountDao );
        context.putBean( captchaService );
        context.putBean( mailSender );
    }
}
