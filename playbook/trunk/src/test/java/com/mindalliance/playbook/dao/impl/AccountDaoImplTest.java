package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.EmailMedium;
import com.mindalliance.playbook.model.PhoneMedium;
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
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Make sure account finder method work as expected.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
public class AccountDaoImplTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private AccountDao accountDao;

    private static final Logger LOG = LoggerFactory.getLogger( AccountDaoImplTest.class );

    @Before
    public void init() {
        if ( !TransactionSynchronizationManager.hasResource( sessionFactory ) )
            try {
                SecurityContextHolder.getContext().setAuthentication( null );
                
                Session session = SessionFactoryUtils.openSession( sessionFactory );
                session.setFlushMode( FlushMode.MANUAL );
                TransactionSynchronizationManager.bindResource( sessionFactory, new SessionHolder( session ) );

                addAccounts();
            } catch ( HibernateException ex ) {
                throw new DataAccessResourceFailureException( "Could not open Hibernate Session", ex );
            }
    }
    
    public void addAccounts() {
        LOG.debug( "Adding accounts" );
        Contact bob = new Contact( new EmailMedium( "work", "bob@example.com" ) );
        bob.setGivenName( "Robert" );
        bob.setFamilyName( "Robertson" );
        bob.addMedium( new PhoneMedium( "home", "555-1212" ) );
        Account entity = new Account( "pb", "bob", bob );
        entity.setConfirmed( true );
        accountDao.save( entity );

        Contact joe = new Contact( new PhoneMedium( "home", "555-2344" ) );
        joe.setGivenName( "Joseph" );
        joe.setFamilyName( "McJoseph" );
        joe.setNickname( "Joe" );
        joe.addMedium( new EmailMedium( null, "joe@example.com" ) );

        Account entity1 = new Account( "pb", "joe", joe );
        entity1.setConfirmed( true );
        accountDao.save( entity1 );

        Account joeAccount = accountDao.findByUserId( "pb", "joe" );
        joeAccount.addContact( new Contact( new EmailMedium( "work", "bob@example.com" ) ) );
        joeAccount.setConfirmation( "1234" );
        accountDao.save( joeAccount );
    }

    @Test
    public void testFindAliases() throws Exception {
        Account bob = accountDao.findByUserKey( "pb|bob" );
        assertTrue( bob.getOwner().isMain() );
        Set<Contact> aliases = accountDao.findAliases( bob );
        assertEquals( 2, aliases.size() );
    }

    @Test
    public void testFindByContact() throws Exception {
        Account byContact = accountDao.findByContact( new Contact( new EmailMedium( null, "bob@example.com" ) ) );
        assertNotNull( byContact );
        assertEquals( "pb:bob", byContact.getUserKey() );
        assertNull( accountDao.findByContact( new Contact( new EmailMedium( null, "rob@example.com" ) ) ) );
    }

    @Test
    public void testFindByMedium() throws Exception {
        Account a = accountDao.findByMedium( new EmailMedium( null, "bob@example.com" ) );
        assertNotNull( a );
        assertEquals( "bob", a.getUserId() );
        
        Account a2 = accountDao.findByMedium( new EmailMedium( null, "rob@example.com" ) );
        assertNull( a2 );
    }

    @Test
    public void testCurrentAccount() {
        Account account = accountDao.getCurrentAccount();
        assertNull( account );
    }
    
    @Test
    public void testFindByConfirmation() throws Exception {
        Account account = accountDao.findByConfirmation( "1234" );
        assertEquals( "pb:joe", account.getUserKey() );

        Account account2 = accountDao.findByConfirmation( "12345" );
        assertNull( account2 );
    }

    @Test
    public void testLoadUserByUsername() throws Exception {
        UserDetailsService a = (UserDetailsService) accountDao;
        UserDetails userDetails = a.loadUserByUsername( "pb|bob" );
        
        assertEquals( "pb|bob", userDetails.getUsername() );
        assertEquals( "x", userDetails.getPassword() );
        assertTrue( userDetails.isAccountNonExpired() );
        assertTrue( userDetails.isAccountNonLocked() );
        assertTrue( userDetails.isCredentialsNonExpired() );
        assertTrue( userDetails.isEnabled() );

        
        try {
            UserDetails bla = a.loadUserByUsername( "bla" );
            fail();
        } catch ( UsernameNotFoundException ignored ) {
            // success

        }
    }
}
