/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.dao;

import org.hibernate.search.FullTextQuery;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Expected contract for DAOs.
 * Based on http://community.jboss.org/wiki/GenericDataAccessObjects.
 */
public interface GenericDao<T, ID extends Serializable> {

    @Transactional
    T load( ID id, boolean lock );

    @Transactional
    T load( ID id );

    @Transactional
    List<T> list();

    @Transactional
    T save( T entity );

    @Transactional
    void delete( T entity );

    @Transactional
    Iterator<T> iterator( int first, int count, String property, boolean ascending, FullTextQuery query );

    /**
     * Possibly unwrap a proxy object to get to the actual object managed by Hibernate.
     * @param object may be a proxy
     * @return either the object or the proxied object
     */
    T deproxy( T object );

    /**
     * Refresh an object from the database.
     * @param object the object
     */
    @Transactional    
    void refresh( T object );
}
