package com.mindalliance.playbook.pages.login;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.pages.AbstractPageTest;
import com.octo.captcha.service.image.ImageCaptchaService;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.*;

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
        context.putBean( "serverUrl", serverUrl );
        context.putBean( "from", "admin@example.com" );
        context.putBean( accountDao );
        context.putBean( captchaService );
        context.putBean( mailSender );
    }
    
    @Test
    public void plainSubmit() throws MessagingException {
        JavaMailSender jms = new JavaMailSenderImpl();
        MimeMessage msg = jms.createMimeMessage();
        
        when( mailSender.createMimeMessage() ).thenReturn( msg );
        
        
        tester.startPage( Register.class );
        FormTester form = tester.newFormTester( "form" );
        form.setValue( "email", "someone@mind-alliance.com" );
        form.setValue( "password", "bogus" );
        form.submit();
        tester.assertNoErrorMessage();
        tester.assertRenderedPage( Thanks.class );
        
        verify( accountDao ).save( (Account) notNull() );
        verify( mailSender ).send( msg );
        Assert.assertEquals( "someone@mind-alliance.com", msg.getRecipients( RecipientType.TO )[0].toString() );
        
//        verify( mailSender.send( msg ) );
    }
}
