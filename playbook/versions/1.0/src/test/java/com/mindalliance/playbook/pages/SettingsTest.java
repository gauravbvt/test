package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Playbook;
import com.mindalliance.playbook.services.ContactMerger;
import com.mindalliance.playbook.services.SocialHub;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.*;

/**
 * Settings page test.
 */
public class SettingsTest extends AbstractPageTest {
    
    @Mock
    private Account account;
    
    @Mock
    private AccountDao accountDao;
    
    @Mock
    private ContactMerger contactMerger;
    
    @Mock
    private SocialHub socialHub;

    @Mock
    private Playbook playbook;

    @Mock
    private Contact contact;

    @Override
    protected Class<? extends WebPage> getTestedClass() {
        return Settings.class;
    }

    @Override
    protected void init( ApplicationContextMock context ) {
        when( account.getPlaybook() ).thenReturn( playbook );
        when( playbook.getMe() ).thenReturn( contact );
        
        context.putBean( account );
        context.putBean( accountDao );
        context.putBean( contactMerger ); 
        context.putBean( socialHub );
    }
    
    @Test
    public void emptySubmit() throws IOException {
        tester.startPage( Settings.class );
        FormTester form = tester.newFormTester( "form" );
        form.submit();
        
        verify( accountDao ).save( (Account) notNull() );
        verify( contactMerger, never() ).importVCards( (InputStream) any() );
        
        tester.assertRenderedPage( Settings.class );
    }

    @Test
    public void viewByTags() {
        tester.startPage( Settings.class );
        FormTester form = tester.newFormTester( "form" );

        form.setValue( "viewByTags", true );
        form.submit();

        verify( account ).setViewByTags( true );
        tester.assertRenderedPage( Settings.class );
    }

    @Test
    public void showInactive() {
        tester.startPage( Settings.class );
        FormTester form = tester.newFormTester( "form" );

        form.setValue( "showInactive", true );
        form.submit();

        verify( account ).setShowInactive( true );
        tester.assertRenderedPage( Settings.class );
    }
}
