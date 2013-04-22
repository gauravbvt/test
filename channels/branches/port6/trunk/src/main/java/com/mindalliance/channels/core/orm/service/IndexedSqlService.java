package com.mindalliance.channels.core.orm.service;

import org.hibernate.Query;
import org.hibernate.search.FullTextQuery;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/12
 * Time: 3:31 PM
 */
public interface IndexedSqlService<T, ID extends Serializable> extends GenericSqlService<T, ID> {

    /**
     * Create a new query on a search string.
     *
     * @param search the search string
     * @return the new query
     */
    FullTextQuery makeQuery( String search );

    /**
     * Create a new global query.
     *
     * @return a query listing all results
     */
    Query makeQuery();

    /**
     * Find by name.
     *
     * @param query a string to query for in indexed fields.
     * @return whatever matches, sorted by relevance. In query is null or empty, will return an empty list.
     */
    List<T> find( String query );
}

