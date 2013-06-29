package com.mindalliance.playbook.dao;

import org.hibernate.Query;
import org.hibernate.search.FullTextQuery;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * A searchable dao.
 */
public interface IndexedDao<T, ID extends Serializable> extends GenericDao<T, ID> {

    /**
     * Create a new query on a search string.
     * 
     * @param search the search string
     * @return the new query
     */
    @Transactional( propagation = Propagation.MANDATORY )
    FullTextQuery makeQuery( String search );
    
    /**
     * Create a new global query.
     * 
     * @return a query listing all results
     */
    @Transactional( propagation = Propagation.MANDATORY )
    Query makeQuery();

    /**
     * Find by name.
     * 
     * @param query a string to query for in indexed fields.
     * @return whatever matches, sorted by relevance. In query is null or empty, will return an empty list.
     */
    @Transactional
    List<T> find( String query );
}
