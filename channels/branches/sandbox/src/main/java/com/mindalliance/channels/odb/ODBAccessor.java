package com.mindalliance.channels.odb;

import org.apache.commons.beanutils.PropertyUtils;
import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.ICriterion;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

    /** Logger. */
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
        ODB odb = null;
        try {
            synchronized ( odbTxFactory ) {
                odb = odbTxFactory.openDatabase();
                odb.store( po );
            }
        } catch ( IOException e ) {
            LOG.warn( "Unable to store object", e );
        } finally {
            if ( odb != null && !odb.isClosed() )
                odb.close();
        }
    }

    /**
     * Iterate over the results of a query.
     *
     * @param clazz a class of persistent objects
     * @param criterion a criterion
     * @return an iterator on persistent objects
     */
    public <T extends PersistentObject> Iterator<T> iterate( Class<T> clazz, ICriterion criterion ) {
        return iterate( clazz, criterion, null, null );
    }

    /**
     * Iterate over the results of a query.
     *
     * @param clazz a class of persistent objects
     * @param criterion a criterion
     * @param ordering an ordering
     * @param orderedProperty a string
     * @return an iterator on persistent objects
     */
    public <T extends PersistentObject> Iterator<T> iterate(
        Class<T> clazz, ICriterion criterion, Ordering ordering, String orderedProperty ) {
        synchronized ( odbTxFactory ) {
            ODB odb = null;
            try {
                odb = odbTxFactory.openDatabase();

                IQuery query = new CriteriaQuery( clazz, criterion );
                if ( ordering != null ) {
                    if ( ordering == Ordering.Ascendant )
                        query.orderByAsc( orderedProperty );
                    else
                        query.orderByDesc( orderedProperty );
                }

                return odb.<T>getObjects( query ).iterator();

            } catch ( IOException e ) {
                LOG.warn( "Query failed", e );
                return new ArrayList<T>().iterator();
            } finally {
                if ( odb != null && !odb.isClosed() )
                    odb.close();
            }
        }
    }

    /**
     * Find the first answer to a query
     *
     * @param clazz a class of persistent objects
     * @param criterion a criterion
     * @return a persistent object or null
     */
    public <T extends PersistentObject> T first( Class<T> clazz, ICriterion criterion ) {
        return first( clazz, criterion, null, null );
    }

    /**
     * Retrieve persistent object given its unique id.
     *
     * @param clazz a class of persistent objects
     * @param id a string
     * @return a persistent object or null
     */
    public <T extends PersistentObject> T fromId( Class<T> clazz, String id ) {
        return first( clazz, Where.equal( "id", id ) );
    }

    /**
     * Find the first answer to a query
     *
     * @param clazz a class of persistent objects
     * @param criterion a criterion
     * @param ordering an ordering
     * @param orderedProperty a string
     * @return a persistent object or null
     */
    public <T extends PersistentObject> T first(
        Class<T> clazz, ICriterion criterion, Ordering ordering, String orderedProperty ) {

        Iterator<T> answers = iterate( clazz, criterion, ordering, orderedProperty );
        return answers.hasNext() ? answers.next() : null;
    }

    /**
     * Delete an object from the database.
     *
     * @param clazz a class of persistent objects
     * @param id a string
     */
    public <T extends PersistentObject> void delete( Class<T> clazz, String id ) {
        synchronized ( odbTxFactory ) {
            ODB odb = null;
            try {
                odb = odbTxFactory.openDatabase();
                IQuery query = new CriteriaQuery(
                    clazz, Where.equal( "id", id ) );

                Objects<T> objects = odb.getObjects( query );
                Iterator<T> answers = objects.iterator();
                if ( answers.hasNext() )
                    odb.delete( answers.next() );

            } catch ( IOException e ) {
                LOG.warn( "Unable to delete object", e );
            } finally {
                if ( odb != null && !odb.isClosed() )
                    odb.close();
            }
        }
    }

    public <T extends PersistentObject> void update( Class<T> clazz, String id, String property, Object value ) {
        synchronized ( odbTxFactory ) {
            ODB odb = null;
            try {
                odb = odbTxFactory.openDatabase();
                IQuery query = new CriteriaQuery(
                    clazz, Where.equal( "id", id ) );
                Objects<T> objects = odb.getObjects( query );
                Iterator<T> answers = objects.iterator();
                if ( answers.hasNext() ) {
                    T object = answers.next();
                    PropertyUtils.setProperty( object, property, value );
                    odb.store( object );
                }
            } catch ( Exception e ) {
                LOG.error( "Update failed", e );
                throw new RuntimeException( e );
            } finally {
                if ( odb != null && !odb.isClosed() )
                    odb.close();
            }
        }
    }
}
