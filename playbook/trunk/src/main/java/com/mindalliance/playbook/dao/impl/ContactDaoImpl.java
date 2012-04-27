package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.dao.ContactDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Medium;
import com.mindalliance.playbook.model.Medium.MediumType;
import com.mindalliance.playbook.model.Receive;
import org.apache.lucene.analysis.Analyzer;
import org.apache.solr.analysis.ClassicFilterFactory;
import org.apache.solr.analysis.ClassicTokenizerFactory;
import org.apache.solr.analysis.DoubleMetaphoneFilterFactory;
import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.TokenFilterFactory;
import org.apache.solr.analysis.TokenizerChain;
import org.apache.solr.schema.IndexSchema;
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
import java.util.Map;

/**
 * Hibernate implementation.
 */
@Repository
public class ContactDaoImpl extends IndexedHibernateDao<Contact, Long> implements ContactDao {

    private static final Logger LOG = LoggerFactory.getLogger( ContactDaoImpl.class );

    @Autowired
    private AccountDao accountDao;

    private Analyzer analyzer;
    
    public ContactDaoImpl() {
        Map<String,String> args = Collections.singletonMap(
            IndexSchema.LUCENE_MATCH_VERSION_PARAM,
            LUCENE_VERSION.toString() );

        ClassicTokenizerFactory tokenizer = new ClassicTokenizerFactory();
        tokenizer.init( args );

        LowerCaseFilterFactory lowerCaseFilterFactory = new LowerCaseFilterFactory();
        lowerCaseFilterFactory.init( args );

        DoubleMetaphoneFilterFactory doubleMetaphoneFilterFactory = new DoubleMetaphoneFilterFactory();
        doubleMetaphoneFilterFactory.init( Collections.singletonMap( "inject", "true" ) );
        
        analyzer = new TokenizerChain(
            tokenizer,
            new TokenFilterFactory[]{
                new ClassicFilterFactory(), 
                lowerCaseFilterFactory, 
                doubleMetaphoneFilterFactory
            }
        );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Contact> findByMedium( Medium medium ) {
        if ( medium.getMediumType() == MediumType.ADDRESS )
            return Collections.emptyList();

        Query query = getSession().createQuery(
            "select m.contact from Medium as m where  m.contact.account=:account" 
            + " and m.class=:class and m.address=:address" );       
        
        query.setParameter( "class", medium.getClass().getSimpleName() );
        query.setParameter( "address", medium.getAddress().toString() );
        query.setParameter( "account", accountDao.getCurrentAccount() );
        query.setMaxResults( getMaxResults() );
        
        return (List<Contact>) query.list();
    }

    @Override
    protected Analyzer getAnalyzer() {
        return analyzer;
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

    private Contact privatize( Contact localContact, Contact foreignContact, Collaboration collaboration ) {
        Account account = localContact.getAccount();
        localContact.merge( new Contact( foreignContact ) );
        if ( collaboration instanceof Receive )
            localContact.addMedium( collaboration.getUsing() );
        else
            account.getOwner().addMedium( collaboration.getUsing() );        
        
        return save( localContact );
    }

    /**
     * Make sure contact is present in current account. 
     * If not, copy information relevant to a collaboration.
     *
     * @param foreignContact the foreign contact
     * @param collaboration the collaboration
     * @return a private contact, possibly new
     */
    @Override
    public Contact privatize( Contact foreignContact, Collaboration collaboration ) {
        Account account = accountDao.getCurrentAccount();
        
        for ( Medium medium : foreignContact.getKeyMedia() )
            for ( Contact myContact : findByMedium( medium ) )
                if ( myContact.isMergeableWith( foreignContact ) )
                    return privatize( myContact, foreignContact, collaboration );

        for ( Contact myContact : findByName( foreignContact ) )
            if ( myContact.isMergeableWith( foreignContact ) )
                return privatize( myContact, foreignContact, collaboration );

        return privatize( account.addContact( new Contact() ), foreignContact, collaboration );
    }
}
