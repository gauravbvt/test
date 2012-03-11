/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Medium;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * The account DAO.
 */
@Repository( "accountDao" )
public class AccountDaoImpl extends GenericHibernateDao<Account,Long> implements AccountDao, UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger( AccountDaoImpl.class );

    @Override
    @SuppressWarnings( "unchecked" )
    public Set<Contact> findAliases( Account account ) {
        Set<Contact> result = new HashSet<Contact>();
        
        for ( Medium key : account.getOwner().getKeyMedia() ) {
            Query query = getSession().createQuery(
                "select m.contact from Medium as m where m.class = :type and m.address = :address"
            )
                .setParameter( "address", key.getAddress().toString() )
                .setParameter( "type", key.getClass().getSimpleName() );
            result.addAll( query.list() );
        }
        
        return result;
    }

    @Override
    public Account findByUserId( String providerId, String userId ) {
        List<Account> accounts = findByCriteria(
            Restrictions.eq( "providerId", providerId ), 
            Restrictions.eq( "userId", userId ) );
        
        return accounts.isEmpty() ? null : accounts.get( 0 );
    }

    @Override
    public Account findByUserKey( String userKey ) {
        StringTokenizer t = new StringTokenizer( userKey, ":|" );
        return findByUserId( t.nextToken(), t.nextToken() );       
    }

    @Override
    public Account findByContact( Contact contact ) {
        for ( Medium medium : contact.getKeyMedia() ) {
            Account account = findByMedium( medium );
            if ( account != null )
                return account;
        }
        return null;
    }
    
    @Override
    public Account findByMedium( Medium medium ) {
        
        List accounts = getSession().createQuery(
            "select m.contact.account from Medium m where"
            + " m.class = :type and m.address=:address and m.contact.main = true" )
            .setParameter( "type", medium.getClass().getSimpleName() )
            .setParameter( "address", medium.getAddress().toString() )
            .setMaxResults( 1 )
            .list();

        return accounts.isEmpty() ? null : (Account) accounts.get( 0 );
    }

    @Override
    public Account findByConfirmation( String key ) {
        List<Account> accounts = findByCriteria( Restrictions.eq( "confirmation", key ) );
        return accounts.isEmpty() ? null : accounts.get( 0 );
    }

    @Override
    public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {
        Account account = findByUserKey( username );
        if ( account == null )
            throw new UsernameNotFoundException( "No user by this name" );

        return getDetails( account );
    }

    /**
     * Return the account of the current user in scope.
     * @param dao the dao to use
     * @return the account of the currently authenticated user or null if anonymous (might cause a run-time error)
     */
    public static Account currentAccount( AccountDao dao ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( authentication != null ) {
            Object obj = authentication.getPrincipal();
            if ( obj instanceof AccountDetails ) {
                Account stale = ( (AccountDetails) obj ).getAccount();
                return dao.load( stale.getId() );
            } 
        }

        // TODO revise this...
        return null;
    }

    @Override
    public Account getCurrentAccount() {
        return currentAccount( this );
    }

    @Override
    public void setCurrentAccount( Account account ) {
        AccountDetails details = new AccountDetails( account );
        SecurityContextHolder.getContext().setAuthentication( 
            new PreAuthenticatedAuthenticationToken( details, null, details.getAuthorities() ) );    
    }

    @Override
    public UserDetails getDetails( Account account ) {
        return new AccountDetails( account );    
    }
    

    /** 
     * Spring security wrapper.
     */
    private static class AccountDetails implements UserDetails {

        private static final long serialVersionUID = -1952757478010843571L;

        private final Account account;

        public AccountDetails( Account account ) {
            this.account = account;
        }

        public Account getAccount() {
            return account;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.singleton( new SimpleGrantedAuthority( "ROLE_USER" ) );
        }

        @Override
        public String getPassword() {
            // Since we're using provider-based authentication, we don't have a password but one is needed
            // for remember-me tokens...
            return account.getPassword() == null ? "x" : account.getPassword();
        }

        @Override
        public String getUsername() {
            return account.getProviderId() + '|' + account.getUserId();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return !account.isDisabled();
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return account.isConfirmed();
        }

        @Override
        public boolean isEnabled() {
            return !account.isDisabled() && account.isConfirmed();
        }
    }
}
