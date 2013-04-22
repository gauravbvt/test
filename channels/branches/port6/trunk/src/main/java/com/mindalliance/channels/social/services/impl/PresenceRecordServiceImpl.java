package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.social.model.PresenceRecord;
import com.mindalliance.channels.social.services.PresenceRecordService;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/3/12
 * Time: 9:26 AM
 */
@Repository
public class PresenceRecordServiceImpl
        extends GenericSqlServiceImpl<PresenceRecord, Long>
        implements PresenceRecordService {

    private Map<String, Map<String, PresenceRecord>> latestPresences = new HashMap<String, Map<String, PresenceRecord>>();
    private Date startupDate  = new Date();
    private Set<String> deaths = new HashSet<String>();
    /**
     * Times after which users are considered "dead", unless incremented by "keepAlive" signals.
     */
    private Map<String,Map<String, Long>> userLives = Collections.synchronizedMap( new HashMap<String, Map<String, Long>>() );;


    @Override
    @Transactional( readOnly = true)
    public PresenceRecord findLatestPresence( String username, String communityUri ) {
        PresenceRecord latestPresence;
        if ( isLatestPresenceCached(  username, communityUri ) ) {
            latestPresence = getLatestCachedPresence( communityUri, username );
        } else {
            latestPresence = fetchLatestPresence( communityUri, username );
            cacheLatestPresence( communityUri, username, latestPresence );
        }
        return latestPresence;
    }

    private PresenceRecord fetchLatestPresence( String uri, String username ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", uri ) );
        criteria.add( Restrictions.eq( "username", username ) );
        criteria.addOrder( Order.desc( "created" ) );
        List<PresenceRecord> results = (List<PresenceRecord>)criteria.list( );
        return results.isEmpty() ? null : results.get( 0 );
    }

    @Override
    public boolean isAlive( String username, String uri ) {
        long now = System.currentTimeMillis();
        Map<String, Long> lives = getUserLives( uri );
        return lives.containsKey( username ) && now <= lives.get( username );
    }

    /** Presence listener */

    @Override
   @Transactional
    public void killIfAlive( String username, PlanCommunity planCommunity ) {
        if ( isAlive( username, planCommunity.getUri() ) )
            kill( username, planCommunity  );
    }

    private void kill( String username, PlanCommunity planCommunity ) {
        recordAbsence( username, planCommunity  );
        getUserLives( planCommunity.getUri()  ).remove( username );
        deaths.add( username );
    }

    @Override
    @Transactional
    public void keepAlive( String username, PlanCommunity planCommunity, int refreshDelay ) {
        if ( !getUserLives( planCommunity.getUri() ).containsKey( username ) ) {
            // Record first sign of life
            recordPresence( username, planCommunity );
        }
        Map<String, Long> lives = getUserLives( planCommunity.getUri() );
        lives.put( username, System.currentTimeMillis() + ( refreshDelay * 2L * 1000L ) );
    }

    @Override
    @Transactional
    public List<String> giveMeYourDead( PlanCommunity planCommunity ) {
        discoverDeadUsers( planCommunity );
        List<String> deathRoll = new ArrayList<String>( deaths );
        deaths = new HashSet<String>();
        return deathRoll;
    }

    private void discoverDeadUsers( PlanCommunity planCommunity ) {
        List<String> discovered = new ArrayList<String>(  );
        for ( String username : getUserLives(  planCommunity.getUri()  ).keySet() ) {
            if ( !isAlive(  username, planCommunity.getUri() ) ) {
                discovered.add( username );
            }
        }
        for ( String deadUser : discovered ) {
            kill( deadUser, planCommunity );
        }
    }

    @Transactional
    public void recordPresence( String username, PlanCommunity planCommunity ) {
        resetLatestPresences( planCommunity.getUri() );
        save( new PresenceRecord( PresenceRecord.Type.Active, username, planCommunity ) );
    }

    @Transactional
    public void recordAbsence( String username, PlanCommunity planCommunity ) {
        resetLatestPresences( planCommunity.getUri() );
        save( new PresenceRecord( PresenceRecord.Type.Inactive, username, planCommunity ) );
    }

    private Map<String, Long> getUserLives( String urn ) {
        Map<String, Long> planLives = userLives.get( urn );
        if ( planLives == null ) {
            planLives = new HashMap<String, Long>();
            userLives.put( urn, planLives );
        }
        return planLives;
    }

    private boolean isLatestPresenceCached( String urn, String username ) {
        return getLatestPresenceCache( urn ).get( username ) != null;
    }

    private void cacheLatestPresence( String urn, String username, PresenceRecord latestPresence ) {
        getLatestPresenceCache( urn ).put( username, latestPresence );
    }

    private PresenceRecord getLatestCachedPresence( String urn, String username ) {
        return getLatestPresenceCache( urn ).get( username );
    }

    private Map<String, PresenceRecord> getLatestPresenceCache( String urn ) {
        Map<String, PresenceRecord> cache = latestPresences.get( urn );
        if ( cache == null ) {
            cache = new HashMap<String, PresenceRecord>();
            latestPresences.put( urn, cache );
        }
        return cache;
    }

    private void resetLatestPresences( String urn ) {
        latestPresences.put( urn, new HashMap<String, PresenceRecord>() );
    }

}
