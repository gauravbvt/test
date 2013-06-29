package com.mindalliance.playbook;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.EmailMedium;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.pages.EditPlay;
import com.mindalliance.playbook.pages.EditStep;
import com.mindalliance.playbook.pages.PlaysPage;
import junit.framework.Assert;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Integration test for interactions between 3 users (Bob, John and Jane). Bob knows John and Jane. John and Jane only
 * know Bob.
 * <p/>
 * Note: tests in this class need to be run together in sequence. They run alphabetically...
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
public class UseCases {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private PlayDao playDao;

    @Autowired
    private StepDao stepDao;

    @Autowired
    private SessionFactory sessionFactory;

    private WicketTester tester;

    @Autowired
    private ApplicationContext context;

    private static final Logger LOG = LoggerFactory.getLogger( UseCases.class );

    @Before
    public void openSession() {
        if ( !TransactionSynchronizationManager.hasResource( sessionFactory ) )
            try {
                LOG.debug( "Opening session" );
                Session session = SessionFactoryUtils.openSession( sessionFactory );
                session.setFlushMode( FlushMode.MANUAL );
                TransactionSynchronizationManager.bindResource( sessionFactory, new SessionHolder( session ) );
            } catch ( HibernateException ex ) {
                throw new DataAccessResourceFailureException( "Could not open Hibernate Session", ex );
            }
    }

    /**
     * Create users Bob, John and Jane. Bob knows about John and Jane. John and Jane both know Bob.
     */
    @Before
    public void setup() {
        if ( accountDao.findByUserId( "pb", "A" ) == null ) {
            Contact bob = new Contact( new EmailMedium( "work", "bob@example.com" ) );
            Contact john = new Contact( new EmailMedium( "work", "john@example.com" ) );
            Contact jane = new Contact( new EmailMedium( "work", "jane@example.com" ) );

            Account account1 = new Account( "pb", "A", bob );
            account1.addContact( new Contact( john ) );
            account1.addContact( new Contact( jane ) );
            accountDao.save( account1 );

            Account account2 = new Account( "pb", "B", john );
            account2.addContact( new Contact( bob ) );
            accountDao.save( account2 );

            Account account3 = new Account( "pb", "C", jane );
            account3.addContact( new Contact( bob ) );
            accountDao.save( account3 );
        }

        tester = new WicketTester();
        WebApplication application = tester.getApplication();
        application.getComponentInstantiationListeners().add( new SpringComponentInjector( application, context ) );
    }

    /**
     * <ul><li>Bob creates a new play</li> <li>... add a new step</li> </ul>
     */
    @Test
    public void useCase01a() {
        LOG.debug( "Running use-case 01a" );
        accountDao.setCurrentAccount( accountDao.findByUserId( "pb", "A" ) );

        // Bob creates a new play
        tester.startPage( PlaysPage.class );
        assertRendered( PlaysPage.class );
        tester.clickLink( "addPlay" );
        assertRendered( EditPlay.class );

        List<Play> plays = playDao.find( "" );
        assertEquals( 1, plays.size() );
        Play play = plays.get( 0 );
        long id = play.getId();

        // Fill-in all fields and check that onblur actually saves them
        FormTester form = tester.newFormTester( "form" );
        form.setValue( "title", "The first play" );
        tester.executeAjaxEvent( "form:title", "onblur" );
        Play play1 = playDao.load( id );
        Assert.assertEquals( "The first play", play1.getTitle() );

        FormTester form2 = tester.newFormTester( "form" );
        form2.setValue( "description", "Some description" );
        tester.executeAjaxEvent( "form:description", "onblur" );
        Play play2 = playDao.load( id );
        Assert.assertEquals( "Some description", play2.getDescription() );

        FormTester form3 = tester.newFormTester( "form" );
        form3.setValue( "tagString", "b, c, c, a" );
        tester.executeAjaxEvent( "form:tagString", "onblur" );
        Play play3 = playDao.load( id );
        Assert.assertEquals( 3, play3.getTags().size() );
        Assert.assertEquals( "a, b, c", play3.getTagString() );
    }

    @Test
    public void useCase01b() {
        LOG.debug( "Running use-case 01b" );
        accountDao.setCurrentAccount( accountDao.findByUserId( "pb", "A" ) );

        // Bob navigate to new play
        tester.startPage( PlaysPage.class );
        assertRendered( PlaysPage.class );

        FormTester form = tester.newFormTester( "form" );
        form.setValue( "search", "First" );

        tester.executeAjaxEvent( "form:search", "onchange" );

        tester.clickLink( "list:filteredPlays:0:editlink" );
        assertRendered( EditPlay.class );

        // Clicks on the add step
        tester.clickLink( "form:addStep" );
        assertRendered( EditStep.class );

        LOG.debug( "Bob created step" );
    }

    /**
     * <ul><li>Bob change the new step to "Send"</li> <li>... selects John as a contact</li> <li>... selects email as
     * medium</li> <li>... verifies message list is as should be</li> <li>... asks for confirmation without permission
     * to forward</li> <li>... checks in messages to see entry in outgoing section</li> <li>John checks in messages and
     * sees incoming message</li> <li>... clicks on message</li> <li>... confirms and creates a new step in a new
     * play</li> <li>... verifies messages are empty</li> <li>Bob verifies messages are empty</li> </ul>
     */
    @Test
    public void useCase01c() {
        LOG.debug( "Running use-case 01c" );
        accountDao.setCurrentAccount( accountDao.findByUserId( "pb", "A" ) );

        // Bob navigate to new play
        tester.startPage( PlaysPage.class );
        assertRendered( PlaysPage.class );
        tester.clickLink( "list:filteredPlays:0:editlink" );
        assertRendered( EditPlay.class );

        // Clicks on the new step
        tester.clickLink( "form:addStep" );
        assertRendered( EditStep.class );

        LOG.debug( "Bob created step" );
    }

    /**
     * Check that John can't access Bob's play.
     */
    @Test
    @Transactional
    public void useCase01d() {
        LOG.debug( "Checking access" );
        accountDao.setCurrentAccount( accountDao.findByUserId( "pb", "A" ) );
        List<Play> plays = playDao.find( "" );
        assertEquals( 1, plays.size() );
        long id = plays.get( 0 ).getId();
        assertEquals( 1L, id );

        accountDao.setCurrentAccount( accountDao.findByUserId( "pb", "B" ) );
        tester.startPage( EditPlay.class, new PageParameters().add( "id", id ) );
        assertEquals( 403, tester.getLastResponse().getStatus() );
    }


    /**
     * Bob deletes his play.
     */
    @Test
    public void useCase02b() {
        accountDao.setCurrentAccount( accountDao.findByUserId( "pb", "A" ) );

        tester.startPage( PlaysPage.class );
        assertRendered( PlaysPage.class );
        tester.clickLink( "list:filteredPlays:0:editlink" );
        assertRendered( EditPlay.class );

        tester.clickLink( "form:deletePlay" );
        assertRendered( PlaysPage.class );

        Account bob = accountDao.findByUserId( "pb", "A" );
        List<Play> plays = bob.getPlaybook().getPlays();
        assertEquals( 0, plays.size() );
    }

    private void assertRendered( Class<? extends Page> aClass ) {
        assertEquals( HttpServletResponse.SC_OK, tester.getLastResponse().getStatus() );
        tester.assertNoErrorMessage();
        tester.assertRenderedPage( aClass );
    }

    /**
     * Not really a test... Just to close the session properly after tests...
     */
    @Test
    public void xCloseSession() {
        LOG.debug( "Closing session in OpenSessionInViewFilter" );
        SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource( sessionFactory );
        SessionFactoryUtils.closeSession( sessionHolder.getSession() );
    }
}
