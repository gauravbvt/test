package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.dao.ContactDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Contact;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * Hibernate implementation.
 */
@Repository
public class ContactDaoImpl extends IndexedHibernateDao<Contact, Long> implements ContactDao {

    private static final Logger LOG = LoggerFactory.getLogger( ContactDaoImpl.class );

    @Autowired
    private AccountDao accountDao;

    @SuppressWarnings( "unchecked" )
    @Override
    public List<Contact> findByEmail( Object email ) {

        Query query = getSession().createQuery(
            "select m.contact from Medium as m where m.type='EMAIL' and m.address=:email and m.contact.account=:account" );
        query.setParameter( "email", email );
        query.setParameter( "account", accountDao.getCurrentAccount() );
        query.setMaxResults( getMaxResults() );
        return (List<Contact>) query.list();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public List<Contact> find( String query ) {
        if ( query == null || query.trim().isEmpty() )
            return Collections.emptyList();

        Criteria criteria = getSession().createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "account", accountDao.getCurrentAccount() ) );

        FullTextQuery fullTextQuery = makeQuery( query );
        fullTextQuery.setCriteriaQuery( criteria );
        fullTextQuery.setMaxResults( getMaxResults() );

        return (List<Contact>) fullTextQuery.list();
    }

    @Override
    public List<Contact> findByName( String givenName, String additionalNames, String familyName, String suffixes ) {
        if ( isEmpty( givenName ) && isEmpty( additionalNames ) && isEmpty( familyName ) && isEmpty( suffixes ) )
            return Collections.emptyList();

        Conjunction conjunction = Restrictions.conjunction();
        conjunction.add( Restrictions.eq( "account", accountDao.getCurrentAccount() ) );
        
        if ( !isEmpty( givenName ) )
            conjunction.add( Restrictions.ilike( "givenName", givenName, MatchMode.START ) );

        if ( !isEmpty( additionalNames ) )
            conjunction.add( Restrictions.ilike( "additionalNames", additionalNames, MatchMode.START ) );
        
        if ( !isEmpty( familyName ) )
            conjunction.add( Restrictions.ilike( "familyName", familyName, MatchMode.START ) );
        
        if ( !isEmpty( suffixes ) )
            conjunction.add( Restrictions.ilike( "suffixes", suffixes, MatchMode.START ) );
        
        return findByCriteria( conjunction );
    }

    private static boolean isEmpty( String value ) {
        return value == null || value.isEmpty() || value.trim().isEmpty();
    }

    @Override
    public List<Contact> findByName( Contact contact ) {
        return findByName(
            contact.getGivenName(), contact.getAdditionalNames(), contact.getFamilyName(), contact.getSuffixes() );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public List<Contact> findAliases( Account account ) {
        Query query = getSession().createQuery(
            "select c.contact from OtherMedium as c where c.type = 'EMAIL' and c.address = :email"
        ).setParameter( "email", account.getEmail() );
        return (List<Contact>) query.list();
    }
}
