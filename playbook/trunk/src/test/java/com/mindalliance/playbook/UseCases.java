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
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Integration test for interactions between 3 users (Bob, John and Jane).
 * Bob knows John and Jane. John and Jane only know Bob.
 * 
 * Note: tests in this class need to be run together in sequence. They run alphabetically...
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
@TransactionConfiguration( defaultRollback = false )
@Transactional
public class UseCases {
    
    @Autowired
    private AccountDao accountDao;
    
    @Autowired
    private PlayDao playDao;
    
    @Autowired
    private StepDao stepDao;

    private WicketTester tester;

    @Autowired
    private ApplicationContext context;

    private static final Logger LOG = LoggerFactory.getLogger( UseCases.class );

    /**
     * Create users Bob, John and Jane.
     * Bob knows about John and Jane. 
     * John and Jane both know Bob.
     */
    @Before
    public void setup() {
        if ( accountDao.findByUserId( "pb", "A" ) == null ) {
            Contact bob  = new Contact( new EmailMedium( "work", "bob@example.com" ) );
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
     * <ul><li>Bob creates a new play</li> 
     *     <li>... add a new step</li>
     * </ul>
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

        // Bob adds a step
        FormTester form = tester.newFormTester( "form" );
        form.setValue( "newStep", "Discuss something" );
        // Clicks on the "Save" button
        form.submit();
        assertRendered( EditPlay.class );        
    }

    @Test
    /**
     * <ul><li>Bob change the new step to "Send"</li> 
     *     <li>... selects John as a contact</li> 
     *     <li>... selects email as medium</li>
     *     <li>... verifies message list is as should be</li> 
     *     <li>... asks for confirmation without permission to forward</li> 
     *     <li>... checks in messages to see entry in outgoing section</li> 
     *     <li>John checks in messages and sees incoming message</li> 
     *     <li>... clicks on message</li> 
     *     <li>... confirms and creates a new step in a new play</li> 
     *     <li>... verifies messages are empty</li> 
     *     <li>Bob verifies messages are empty</li> 
     * </ul>
     */
    public void useCase01b() {
        LOG.debug( "Running use-case 01b" );
        accountDao.setCurrentAccount( accountDao.findByUserId( "pb", "A" ) );

        // Bob navigate to new play
        tester.startPage( PlaysPage.class );
        assertRendered( PlaysPage.class );        
        tester.clickLink( "playbook.plays:0:editlink" );
        assertRendered( EditPlay.class );
        
        // Empty save, for coverage...
        tester.newFormTester( "form" ).submit();
        assertRendered( PlaysPage.class );
        tester.clickLink( "playbook.plays:0:editlink" );
        assertRendered( EditPlay.class );

        // Clicks on the new step
        tester.clickLink( "form:stepDiv:steps:0:step:link" );
        assertRendered( EditStep.class );

        LOG.debug( "Bob created step" );
    }
    
    /**
     * Bob deletes his play.
     * TODO figure out the transaction problem
     */
    /*
    @Test
    public void useCase02b() {
        accountDao.setCurrentAccount( accountDao.findByUserId( "pb", "A" ) );

        tester.startPage( PlaysPage.class );
        assertRendered( PlaysPage.class );
        tester.clickLink( "playbook.plays:0:editlink" );
        assertRendered( EditPlay.class );

        tester.clickLink( "form:deletePlay" );
        assertRendered( PlaysPage.class );

        Account bob = accountDao.findByUserId( "pb", "A" );
        List<Play> plays = bob.getPlaybook().getPlays();
        assertEquals( 0, plays.size() );
    }
    */
    
    /**
     * Check that John can access Bob's play.
     */
    @Test
    public void verifyAccess() {
        LOG.debug( "Checking access" );
        Account bob = accountDao.findByUserId( "pb", "A" );
        List<Play> plays = bob.getPlaybook().getPlays();
        assertEquals( 1, plays.size() );
        long id = plays.get( 0 ).getId();
        assertEquals( 1L, id );        

        accountDao.setCurrentAccount( accountDao.findByUserId( "pb", "B" ) );
        tester.startPage( EditPlay.class, new PageParameters().add( "id", id ) );
        assertEquals( 403, tester.getLastResponse().getStatus() );
    }

    private void assertRendered( Class<? extends Page> aClass ) {
        assertEquals( HttpServletResponse.SC_OK, tester.getLastResponse().getStatus() );
        tester.assertNoErrorMessage();
        tester.assertRenderedPage( aClass );
    }
}
