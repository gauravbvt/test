/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.GenericDao;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.search.FullTextQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.List;

abstract class GenericHibernateDao<T, ID extends Serializable> implements GenericDao<T, ID> {

    private static final Logger LOG = LoggerFactory.getLogger( GenericHibernateDao.class );

    private final Class<T> persistentClass;

    @Autowired
    private SessionFactory sessionFactory;

    //------------------------------------------------
    @SuppressWarnings( "unchecked" )
    protected GenericHibernateDao() {
        persistentClass = (Class<T>) ( (ParameterizedType) getClass().getGenericSuperclass() ).getActualTypeArguments()[0];
    }

    //------------------------------------------------
    @Override
    public void delete( T entity ) {
        getSession().delete( deproxy( entity ) );
    }

    @Override
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
        Criteria criteria = getSession().createCriteria( persistentClass );
        for ( Criterion crit : criterion )
            criteria.add( crit );

        return (List<T>) criteria.list();
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public T load( ID id ) {
        return load( id, false );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public T load( ID id, boolean lock ) {
        return lock ?
               (T) getSession().get( persistentClass, id, LockOptions.UPGRADE ) :
               (T) getSession().get( persistentClass, id );
    }

    @Override
    public T save( T entity ) {
        LOG.debug( "Saving {}", getPersistentClass().getSimpleName() );
        getSession().saveOrUpdate( deproxy( entity ) );
        return entity;
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    @SuppressWarnings( "unchecked" )
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
        // TODO check this
//        if ( object instanceof ILazyInitProxy ) {
//            ILazyInitProxy proxy = (ILazyInitProxy) object;
//            return (T) proxy.getObjectLocator().locateProxyTarget();           
//        }
//        else
            return object;
    }

    @Override
    public void refresh( T object ) {
       getSession().refresh( object );       
    }
}
