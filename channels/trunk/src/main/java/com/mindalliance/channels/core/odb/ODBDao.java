/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.odb;

import com.mindalliance.channels.core.PersistentObject;
import com.mindalliance.channels.core.PersistentObjectDao;
import org.apache.commons.beanutils.PropertyUtils;
import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.ComposedExpression;
import org.neodatis.odb.core.query.criteria.ICriterion;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Access layer to neodatis ODB.
 */
public class ODBDao implements PersistentObjectDao {

    /**
     * The date attribute.
     */
    private static final String DATE = "date";

    /**
     * The fromUsername attribute.
     */
    private static final String FROM_USERNAME = "fromUsername";

    /**
     * The identifier attribute.
     */
    private static final String ID = "id";

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ODBDao.class );

    /**
     * The planId attribute.
     */
    private static final String PLAN_ID = "planId";

    /**
     * The toUsername attribute.
     */
    private static final String TO_USERNAME = "toUsername";

    /**
     * The dao factory.
     */
    private final ODBDaoFactory factory;

    /**
     * The plan uri.
     */
    private final String planUri;

    /**
     * Ordering specification.
     */
    private enum Ordering {
        /**
         * Results are sorted from lowest to highest.
         */
        Ascendant,
        /**
         * Results are sorted from highest to lowest.
         */
        Descendant
    }

    //-------------------------------
    ODBDao( ODBDaoFactory factory, String planUri ) {
        this.factory = factory;
        this.planUri = planUri;
    }

    //-------------------------------
    @Override
    public <T extends PersistentObject> void delete( Class<T> clazz, String id ) {
        ODB odb = null;
        try {
            synchronized ( factory ) {
                odb = factory.openDatabase( planUri );
                IQuery query = new CriteriaQuery( clazz, Where.equal( ID, id ) );

                Objects<T> objects = odb.getObjects( query );
                Iterator<T> answers = objects.iterator();
                if ( answers.hasNext() )
                    odb.delete( answers.next() );
            }
        } catch ( IOException e ) {
            LOG.warn( "Unable to delete object", e );
        } finally {
            if ( odb != null && !odb.isClosed() )
                odb.close();
        }
    }

    @Override
    public <T extends PersistentObject> T find( Class<T> clazz, String id ) {
        return first( clazz, Where.equal( ID, id ) );
    }

    /**
     * Find the first answer to a query.
     *
     * @param clazz a class of persistent objects
     * @param criterion a criterion
     * @param <T> the specific kind of persistent object
     * @return a persistent object or null
     */
    private <T extends PersistentObject> T first( Class<T> clazz, ICriterion criterion ) {
        return first( clazz, criterion, null, null );
    }

    @Override
    public <T extends PersistentObject> Iterator<T> findAll( Class<T> clazz ) {
        return iterate( clazz, Where.equal( PLAN_ID, planUri ), Ordering.Descendant, DATE );
    }

    /**
     * Iterate over the results of a query.
     *
     * @param clazz a class of persistent objects
     * @param criterion a criterion
     * @param ordering an ordering
     * @param orderedProperty a string
     * @param <T> the specific kind of persistent object
     * @return an iterator on persistent objects
     */
    private <T extends PersistentObject> Iterator<T> iterate(
            Class<T> clazz, ICriterion criterion, Ordering ordering, String orderedProperty ) {
        ODB odb = null;
        try {
            synchronized ( factory ) {
                odb = factory.openDatabase( planUri );

                IQuery query = new CriteriaQuery( clazz, criterion );
                if ( ordering != null ) {
                    if ( ordering == Ordering.Ascendant )
                        query.orderByAsc( orderedProperty );
                    else
                        query.orderByDesc( orderedProperty );
                }

                return odb.<T>getObjects( query ).iterator();
            }
        } catch ( IOException e ) {
            LOG.warn( "Query failed", e );
            return new ArrayList<T>().iterator();
        } finally {
            if ( odb != null && !odb.isClosed() )
                odb.close();
        }
    }

    @Override
    public <T extends PersistentObject> Iterator<T> findAllExceptUser(
            Class<T> clazz, String username, boolean planner, String usersBroadcast, String plannersBroadcast ) {

        ComposedExpression isBroadcast = Where.or();
        isBroadcast.add( Where.equal( TO_USERNAME, usersBroadcast ) );
        if ( planner ) {
            isBroadcast.add( Where.isNull( TO_USERNAME ) ); // legacy for all planners
            isBroadcast.add( Where.equal( TO_USERNAME, plannersBroadcast ) );
        }

        return iterate( clazz,
                        Where.and()
                             .add( Where.equal( PLAN_ID, planUri ) )
                             .add( Where.or().add( Where.equal( TO_USERNAME, username ) ).add( Where.and().add(
                                     isBroadcast ).add( Where.not( Where.equal( FROM_USERNAME, username ) ) ) ) ),

                        Ordering.Descendant, DATE );
    }

    @Override
    public <T extends PersistentObject> Iterator<T> findAllFrom( Class<T> clazz, String username ) {
        return iterate( clazz,
                        Where.and()
                             .add( Where.equal( PLAN_ID, planUri ) )
                             .add( Where.equal( FROM_USERNAME, username ) ),
                        Ordering.Descendant, DATE );
    }

    @Override
    public <T extends PersistentObject> T findLatestFrom( Class<T> clazz, String username, Date startupDate ) {
        return first( clazz,
                      Where.and()
                           .add( Where.equal( "username", username ) )
                           .add( Where.equal( PLAN_ID, planUri ) )
                           .add( Where.ge( DATE, startupDate ) ),

                      Ordering.Descendant, DATE );
    }

    /**
     * Find the first answer to a query.
     *
     * @param clazz a class of persistent objects
     * @param criterion a criterion
     * @param ordering an ordering
     * @param orderedProperty a string
     * @param <T> the specific kind of persistent object
     * @return a persistent object or null
     */
    private <T extends PersistentObject> T first(
            Class<T> clazz, ICriterion criterion, Ordering ordering, String orderedProperty ) {

        Iterator<T> answers = iterate( clazz, criterion, ordering, orderedProperty );
        return answers.hasNext() ? answers.next() : null;
    }

    @Override
    public void store( PersistentObject po ) {
        ODB odb = null;
        try {
            synchronized ( factory ) {
                odb = factory.openDatabase( planUri );
                odb.store( po );
            }
        } catch ( IOException e ) {
            LOG.warn( "Unable to store object", e );
        } finally {
            if ( odb != null && !odb.isClosed() )
                odb.close();
        }
    }

    @Override
    public <T extends PersistentObject> void update( Class<T> clazz, String id, String property, Object value ) {
        ODB odb = null;
        try {
            synchronized ( factory ) {
                odb = factory.openDatabase( planUri );
                IQuery query = new CriteriaQuery( clazz, Where.equal( ID, id ) );
                Objects<T> objects = odb.getObjects( query );
                Iterator<T> answers = objects.iterator();
                if ( answers.hasNext() ) {
                    T object = answers.next();
                    PropertyUtils.setProperty( object, property, value );
                    odb.store( object );
                }
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
