package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.WicketApplication;
import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.User;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test dynamic web pages.
 */
public class TestWicketPages {

    protected WicketTester tester;

    @Before
    public void setup() {
        final ApplicationContextMock acm = new ApplicationContextMock();
        UserDao userDao = Mockito.mock( UserDao.class );
        User user = Mockito.mock( User.class );
        acm.putBean( "userDao", userDao );
        acm.putBean( "user", user );

        tester = new WicketTester( new WicketApplication() {
            /* (non-Javadoc)
                * @see com.mindalliance.mindpeer.WicketApplication#getGuiceInjector()
                */
            @Override
            protected SpringComponentInjector getSpringInjector() {
                return new SpringComponentInjector( this, acm );
            }
        } );
    }

    @Test
    public void testStartPage() {
        tester.startPage( PublicHomePage.class );
    }

    @Test
    public void testUserPage() {
        tester.startPage( UserHomePage.class );
    }

    @Test
    public void testRegisterPage() {
        tester.startPage( RegisterPage.class );
    }
}
