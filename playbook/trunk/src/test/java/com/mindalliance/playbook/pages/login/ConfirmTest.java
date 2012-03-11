package com.mindalliance.playbook.pages.login;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.EmailMedium;
import com.mindalliance.playbook.pages.TodoPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * Test emailed confirmation link.
 */
public class ConfirmTest {

    private static final String EMAIL = "someone@example.com";

    @Mock
    private AccountDao accountDao;

    private WicketTester tester;

    private Account account;

    @Before
    public void setup() {
        tester = new WicketTester();
        ApplicationContextMock context = new ApplicationContextMock();
        MockitoAnnotations.initMocks( this );

        account = new Account( "playbook", EMAIL, new Contact( new EmailMedium( "EMAIL", EMAIL ) ) );
        when( accountDao.findByConfirmation( "12345" ) ).thenReturn( account );
        when( accountDao.getDetails( account ) ).thenReturn( new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.singletonList( new SimpleGrantedAuthority( "ROLE_USER" ) );
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public String getUsername() {
                return EMAIL;
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        } );
        
        
        context.putBean( accountDao );
        context.putBean( account );
        
        
        WebApplication application = tester.getApplication();
        application.getComponentInstantiationListeners().add( new SpringComponentInjector( application, context ) );
    }

    @Test
    public void successLink() {
        tester.startPage( Confirm.class, new PageParameters().add( "key", "12345" ) );
        Assert.assertEquals( 200, tester.getLastResponse().getStatus() );
        tester.assertNoErrorMessage();
        tester.assertRenderedPage( TodoPage.class );
        
        Assert.assertTrue( account.isConfirmed() );
        Assert.assertNull( account.getConfirmation() );
        Mockito.verify( accountDao, times( 1 ) ).save( account );
    }

    @Test
    public void noKey() {
        tester.startPage( Confirm.class );
        Assert.assertEquals( 403, tester.getLastResponse().getStatus() );
    }

    @Test
    public void badKey() {
        tester.startPage( Confirm.class, new PageParameters().add( "key", "34567" ) );
        Assert.assertEquals( 403, tester.getLastResponse().getStatus() );
    }
    
}
