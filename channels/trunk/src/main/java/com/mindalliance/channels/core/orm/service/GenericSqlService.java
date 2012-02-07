package com.mindalliance.channels.core.orm.service;

import org.hibernate.search.FullTextQuery;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/12
 * Time: 3:27 PM
 */
public interface GenericSqlService<T, ID extends Serializable> {

    T load( ID id, boolean lock );

    T load( ID id );

    List<T> list();

    List<T> findByExample( T exampleInstance, String... excludeProperty );

    T save( T entity );

    void delete( T entity );

    Iterator<T> iterator( int first, int count, String property, boolean ascending, FullTextQuery query );

    /**
     * Possibly unwrap a proxy object to get to the actual object managed by Hibernate.
     *
     * @param object may be a proxy
     * @return either the object or the proxied object
     */
    T deproxy( T object );

    /**
     * Refresh an object from the database.
     *
     * @param object the object
     */
    @Transactional( readOnly = true )
    void refresh( T object );
}

