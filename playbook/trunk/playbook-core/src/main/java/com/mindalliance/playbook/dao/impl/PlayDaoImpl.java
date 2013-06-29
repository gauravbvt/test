package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Step;
import org.apache.lucene.analysis.Analyzer;
import org.apache.solr.analysis.ClassicFilterFactory;
import org.apache.solr.analysis.ClassicTokenizerFactory;
import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.PorterStemFilterFactory;
import org.apache.solr.analysis.TokenFilterFactory;
import org.apache.solr.analysis.TokenizerChain;
import org.apache.solr.schema.IndexSchema;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Hibernate implementation.
 */
@Repository
public class PlayDaoImpl extends IndexedHibernateDao<Play,Long> implements PlayDao {

    @Autowired
    private StepDao stepDao;
    
    @Autowired
    private AccountDao accountDao;

    private final Analyzer analyzer;

    public PlayDaoImpl() {
        Map<String,String> args = Collections.singletonMap(
            IndexSchema.LUCENE_MATCH_VERSION_PARAM, LUCENE_VERSION.toString() );

        ClassicTokenizerFactory tokenizer = new ClassicTokenizerFactory();
        tokenizer.init( args );

        LowerCaseFilterFactory f1 = new LowerCaseFilterFactory();
        f1.init( args );

        PorterStemFilterFactory f3 = new PorterStemFilterFactory();
        f3.init( args );

        analyzer = new TokenizerChain(
            tokenizer,
            new TokenFilterFactory[]{
                new ClassicFilterFactory(),
                f1,
                f3
            }
        );
    }

    @Override
    public void delete( Play entity ) {
        for ( Step step : entity.getSteps() )
            stepDao.delete( step );

        super.delete( entity );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public List<Play> find( String query ) {
        Criteria criteria = getSession().createCriteria( getPersistentClass() );
        criteria.createAlias( "playbook", "p" );
        criteria.add( Restrictions.eq( "p.account", accountDao.getCurrentAccount() ) );

        if ( query == null || query.trim().isEmpty() )
            return criteria.list();
        
        else {
            FullTextQuery fullTextQuery = makeQuery( query );
            fullTextQuery.setCriteriaQuery( criteria );
            fullTextQuery.setMaxResults( getMaxResults() );

            return (List<Play>) fullTextQuery.list();
        }
    }

    @Override
    protected Analyzer getAnalyzer() {
        return analyzer;
    }

    @Override
    protected void addFields( Class<?> aClass, Collection<String> fieldNames, String prefix ) {
        fieldNames.addAll( Arrays.asList( "title", "description", "tags.name" ) );
    }
}
