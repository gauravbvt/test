package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.IndexedDao;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.Search;
import org.hibernate.search.annotations.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A dao that allows text search on its contents.
 */
public abstract class IndexedHibernateDao<T, ID extends Serializable> extends GenericHibernateDao<T, ID>
    implements IndexedDao<T,ID>, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger( IndexedHibernateDao.class );

    protected static final Version LUCENE_VERSION = Version.LUCENE_35;

    /**
     * Indexed search fields annotated with @Field.
     */
    private String[] fields;

    private int maxResults = 20;

    /**
     * Specify how to parse and match string queries.
     * @return an analyzer
     */
    protected abstract Analyzer getAnalyzer();
    
    @Override
    public FullTextQuery makeQuery() {
        FullTextSession session = Search.getFullTextSession( getSession() );
        return session.createFullTextQuery( new MatchAllDocsQuery(), getPersistentClass() );
    }

    /**
     * Create a new query on a search string.
     *
     * @param search the search string
     * @return the new query
     */
    @Override
    public FullTextQuery makeQuery( String search ) {
        if ( search != null )
            try {
                FullTextSession session = Search.getFullTextSession( getSession() );
                return session.createFullTextQuery( parse( search ), getPersistentClass() );
 
            } catch ( ParseException e ) {
                LOG.error( "Unable to create query", e );
            }

        return makeQuery();
    }
    
    private void index( Class<T> aClass ) throws InterruptedException {
        LOG.info( "Indexing {}", aClass.getSimpleName() );
        Session session = getSessionFactory().openSession();
        try {
            FullTextSession fullTextSession = Search.getFullTextSession( session );
            MassIndexer indexer = fullTextSession.createIndexer( aClass );
            indexer.startAndWait();
 
        } finally {
            session.close();
        }
        LOG.debug( "Done indexing {}", aClass.getSimpleName() );
    }

    
    protected Query parse( String search ) throws ParseException {
        MultiFieldQueryParser parser = new MultiFieldQueryParser( LUCENE_VERSION, fields, getAnalyzer() );
        return parser.parse( search );
    }

    @Override
    public void afterPropertiesSet() throws InterruptedException {
        Class<T> aClass = getPersistentClass();
        index( aClass );

        List<String> fieldNames = new ArrayList<String>();
        addFields( aClass, fieldNames, "" );

        fields = fieldNames.toArray( new String[fieldNames.size()] );
    }

    protected void addFields( Class<?> aClass, Collection<String> fieldNames, String prefix ) {
        for ( Method method : aClass.getMethods() ) {
            PropertyDescriptor propertyForMethod = BeanUtils.findPropertyForMethod( method );
            if ( method.getAnnotation( Field.class ) != null ) {
                fieldNames.add( prefix + propertyForMethod.getName() );
            }
            // TODO follow indexed embedded
//            if ( method.getAnnotation( IndexedEmbedded.class ) != null )
//                addFields( method.getReturnType(), fieldNames, prefix + propertyForMethod.getName() + "." );
        }
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public List<T> find( String query ) {
        if ( query == null || query.trim().isEmpty() )
            return Collections.emptyList();
        
        FullTextQuery fullTextQuery = makeQuery( query );
        fullTextQuery.setMaxResults( getMaxResults() );

        return (List<T>) fullTextQuery.list();        
    }

    /**
     * Get the maximum contacts to retrieve.
     *
     * @return the maximum contacts to retrieve
     */
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * Set the number of items to retrieve in fuzzy queries.
     *
     * @param maxResults how may contacts to retrieve.
     * @see #find(String)
     */
    public void setMaxResults( int maxResults ) {
        this.maxResults = maxResults;
    }
}
