package com.mindalliance.channels.core.orm.service.impl;

import com.mindalliance.channels.core.orm.service.GenericSqlService;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.wicket.proxy.ILazyInitProxy;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.search.FullTextQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/12
 * Time: 3:55 PM
 */
public abstract class GenericSqlServiceImpl<T, ID extends Serializable> implements GenericSqlService<T, ID> {

    private static final Logger LOG = LoggerFactory.getLogger( GenericSqlServiceImpl.class );

    @Autowired
    private SessionFactory sessionFactory;

    //------------------------------------------------
    @SuppressWarnings( "unchecked" )
    protected GenericSqlServiceImpl() {
    }

    //------------------------------------------------
    @Override
    @Transactional
    public void delete( T entity ) {
        getSession().delete( deproxy( entity ) );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    @Transactional( readOnly = true )
    public List<T> findByExample( T exampleInstance, String... excludeProperty ) {
        Criteria crit = getSession().createCriteria( getPersistentClass() );
        Example example = Example.create( deproxy( exampleInstance ) );
        for ( String exclude : excludeProperty )
            example.excludeProperty( exclude );

        crit.add( example );
        return (List<T>) crit.list();
    }

    @Override
    @Transactional( readOnly = true )
    public List<T> list() {
        return findByCriteria();
    }

    /**
     * Use this inside subclasses as a convenience method.
     *
     * @param criterion criterion for the query
     * @return the results
     */
    @SuppressWarnings( "unchecked" )
    protected List<T> findByCriteria( Criterion... criterion ) {
        Criteria criteria = getSession().createCriteria( getPersistentClass() );
        for ( Criterion crit : criterion )
            criteria.add( crit );

        return (List<T>) criteria.list();
    }

    protected Session getSession() {
        try {
            return sessionFactory.getCurrentSession();
        } catch ( HibernateException e ) {
            LOG.error( "No hibernate session" );
            throw e;
        }
    }

    @Override
    @Transactional
    public T load( ID id ) {
        return load( id, false );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    @Transactional
    public T load( ID id, boolean lock ) {
        return lock ?
                (T) getSession().get( getPersistentClass(), id, LockOptions.UPGRADE ) :
                (T) getSession().get( getPersistentClass(), id );
    }

    @Override
    @Transactional
    public T save( T entity ) {
        getSession().saveOrUpdate( deproxy( entity ) );
        return entity;
    }

    public abstract Class<T> getPersistentClass();

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional
    public Iterator<T> iterator( int first, int count, String property, boolean ascending, FullTextQuery query ) {

        query.setFirstResult( first );
        query.setMaxResults( count );

        if ( property != null )
            query.setSort( new Sort( new SortField( property, SortField.STRING_VAL, !ascending ) ) );

        LOG.debug( "Results: {}", query.getResultSize() );

        return (Iterator<T>) query.list().iterator();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public T deproxy( T object ) {
        if ( object instanceof ILazyInitProxy ) {
            ILazyInitProxy proxy = (ILazyInitProxy) object;
            return (T) proxy.getObjectLocator().locateProxyTarget();
        } else
            return object;
    }

    @Override
    @Transactional
    public void refresh( T object ) {
        getSession().refresh( object );
    }

}
