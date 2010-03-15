// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.dao.jpa;

import com.mindalliance.mindpeer.dao.Dao;
import com.mindalliance.mindpeer.model.ModelObject;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Generic implementation of common DAO operations.
 * @param <T> the implied model object
 */
public abstract class AbstractDaoImpl<T extends ModelObject> extends JpaDaoSupport
        implements Dao<T> {

    /** The actual class of the managed model objects. */
    private Class<T> domainClass;

    protected AbstractDaoImpl( Class<T> domainClass ) {
        this.domainClass = domainClass;
    }

    /** {@inheritDoc} */
    @Transactional
    public void delete( T object ) {
        LoggerFactory.getLogger( getClass() ).debug( "Deleting {}", object );
        getJpaTemplate().remove( object );
    }

    /** {@inheritDoc} */
    @Transactional
    public T load( Serializable id ) {
        return id == null ? null : getJpaTemplate().find( domainClass, id );
    }

    /** {@inheritDoc} */
    @Transactional
    public T save( T object ) {
        T result = getJpaTemplate().merge( object );
        LoggerFactory.getLogger( getClass() ).debug( "Saved {}", result );
        return result;
    }

    /** {@inheritDoc} */
    @Transactional
    @SuppressWarnings( { "unchecked" } )
    public List<T> findAll() {
        return (List<T>) getJpaTemplate().execute( new JpaCallback() {
            public Object doInJpa( EntityManager em ) {
                String className = domainClass.getSimpleName();
                Query query = em.createQuery( "select e from " + className + " e" );
                return query.getResultList();
            }
        } );
    }

    /**
     * Get a partial iterator on a window of results.
     * @param first the first item to iterate on
     * @param count how many to get
     * @return an iterator
     */
    @Transactional
    @SuppressWarnings( { "unchecked" } )
    public Iterator<T> iterator( final int first, final int count ) {
        return (Iterator<T>) getJpaTemplate().execute( new JpaCallback() {
            public Object doInJpa( EntityManager em ) {
                String className = domainClass.getSimpleName();
                Query query = em.createQuery( "select e from " + className + " e" );
                query.setFirstResult( first );
                query.setMaxResults( count );
                return query.getResultList().iterator();
            }
        } );
    }

    /** {@inheritDoc} */
    @Transactional
    public int countAll() {
        return (Integer) getJpaTemplate().execute( new JpaCallback() {
            public Object doInJpa( EntityManager em ) {
                String className = domainClass.getSimpleName();
                Query query = em.createQuery( "select count (e) from " + className + " e" );
                return ( (Long) query.getSingleResult() ).intValue();
            }
        } );
    }

}

