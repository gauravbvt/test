package com.mindalliance.channels.odb;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.ICriterion;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Access layer to neodatis ODB.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 10, 2010
 * Time: 1:18:31 PM
 */
public class ODBAccessor {

    public enum Ordering {
        Ascendant,
        Descendant
    }

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ODBAccessor.class );
    private final ODBTransactionFactory odbTxFactory;

    public ODBAccessor( ODBTransactionFactory odbTxFactory ) {
        this.odbTxFactory = odbTxFactory;
    }

    /**
     * Store a persistent object.
     *
     * @param po a persistent object.
     */
    public void store( PersistentObject po ) {
        synchronized ( odbTxFactory ) {
            ODB odb = null;
            try {
                odb = odbTxFactory.openDatabase();
                odb.store( po );
            } finally {
                if ( odb != null && !odb.isClosed() )
                    odb.close();
            }
        }
    }

    /**
     * Iterate over the results of a query.
     *
     * @param clazz     a class of persistent objects
     * @param criterion a criterion
     * @return an iterator on persistent objects
     */
    public <T extends PersistentObject> Iterator<T> iterate(
            Class<T> clazz,
            ICriterion criterion
    ) {
        return iterate( clazz, criterion, null, null );
    }

    /**
     * Iterate over the results of a query.
     *
     * @param clazz           a class of persistent objects
     * @param criterion       a criterion
     * @param ordering        an ordering
     * @param orderedProperty a string
     * @return an iterator on persistent objects
     */
    public <T extends PersistentObject> Iterator<T> iterate(
            Class<T> clazz,
            ICriterion criterion,
            Ordering ordering,
            String orderedProperty
    ) {
        synchronized ( odbTxFactory ) {
            ODB odb = null;
            try {
                odb = odbTxFactory.openDatabase();
                Iterator<T> answers;
                IQuery query = new CriteriaQuery(
                        clazz,
                        criterion );
                if ( ordering != null ) {
                    if ( ordering == Ordering.Ascendant ) {
                        query.orderByAsc( orderedProperty );
                    } else {
                        query.orderByDesc( orderedProperty );
                    }
                }
                Objects<T> objects;
                try {
                    objects = odb.getObjects( query );
                    answers = objects.iterator();
                } catch ( Exception e ) {
                    LOG.warn( "Query failed", e );
                    answers = new ArrayList<T>().iterator();
                }
                return answers;
            } finally {
                if ( odb != null && !odb.isClosed() )
                    odb.close();
            }
        }
    }

    /**
     * Find the first answer to a query
     *
     * @param clazz     a class of persistent objects
     * @param criterion a criterion
     * @return a persistent object
     */
    public <T extends PersistentObject> T first(
            Class<T> clazz,
            ICriterion criterion ) {
        return first( clazz, criterion, null, null );
    }

    /**
     * Find the first answer to a query
     *
     * @param clazz           a class of persistent objects
     * @param criterion       a criterion
     * @param ordering        an ordering
     * @param orderedProperty a string
     * @return a persistent object
     */
    public <T extends PersistentObject> T first(
            Class<T> clazz,
            ICriterion criterion,
            Ordering ordering,
            String orderedProperty ) {
        Iterator<T> answers = iterate( clazz, criterion, ordering, orderedProperty );
        if ( answers.hasNext() ) {
            return answers.next();
        } else {
            return null;
        }
    }
}
