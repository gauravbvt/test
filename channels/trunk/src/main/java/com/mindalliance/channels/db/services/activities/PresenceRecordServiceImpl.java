package com.mindalliance.channels.db.services.activities;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.db.data.activities.PresenceRecord;
import com.mindalliance.channels.db.data.activities.QPresenceRecord;
import com.mindalliance.channels.db.repositories.PresenceRecordRepository;
import com.mindalliance.channels.db.services.AbstractDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/18/13
 * Time: 5:33 PM
 */
@Component
public class PresenceRecordServiceImpl
        extends AbstractDataService<PresenceRecord>
        implements PresenceRecordService {

    @Autowired
    private PresenceRecordRepository repository;

    private Map<String, Map<String, PresenceRecord>> latestPresences = new HashMap<String, Map<String, PresenceRecord>>();
    private Date startupDate = new Date();
    private Set<String> deaths = new HashSet<String>();
    /**
     * Times after which users are considered "dead", unless incremented by "keepAlive" signals.
     */
    private Map<String, Map<String, Long>> userLives = Collections.synchronizedMap( new HashMap<String, Map<String, Long>>() );
    ;

    @Override
    public void save( PresenceRecord presenceRecord ) {
        repository.save( presenceRecord );
    }

    @Override
    public PresenceRecord load( String uid ) {
        return repository.findOne( uid );
    }

    @Override
    public void recordPresence( String username, PlanCommunity planCommunity ) {
        resetLatestPresences( planCommunity.getUri() );
        save( new PresenceRecord( PresenceRecord.Type.Active, username, planCommunity ) );
    }

    @Override
    public void recordAbsence( String username, PlanCommunity planCommunity ) {
        resetLatestPresences( planCommunity.getUri() );
        save( new PresenceRecord( PresenceRecord.Type.Inactive, username, planCommunity ) );
    }


    @Override
    public PresenceRecord findLatestPresence( String username, String communityUri ) {
        PresenceRecord latestPresence;
        if ( isLatestPresenceCached( username, communityUri ) ) {
            latestPresence = getLatestCachedPresence( communityUri, username );
        } else {
            latestPresence = fetchLatestPresence( communityUri, username );
            cacheLatestPresence( communityUri, username, latestPresence );
        }
        return latestPresence;
    }

    private PresenceRecord fetchLatestPresence( String communityUri, String username ) {
        QPresenceRecord qPresenceRecord = QPresenceRecord.presenceRecord;
        List<PresenceRecord> presenceRecords = toList(
                repository.findAll(
                        qPresenceRecord.classLabel.eq( PresenceRecord.class.getSimpleName() )
                                .and( qPresenceRecord.communityUri.eq( communityUri ) )
                                .and( qPresenceRecord.username.eq( username ) ),
                        new PageRequest( 0, 1, org.springframework.data.domain.Sort.Direction.DESC, "created" ) )
        );
        return presenceRecords.isEmpty()
                ? null
                : presenceRecords.get( 0 );
    }

    @Override
    public boolean isAlive( String username, String communityUri ) {
        long now = System.currentTimeMillis();
        Map<String, Long> lives = getUserLives( communityUri );
        return lives.containsKey( username ) && now <= lives.get( username );
    }

    @Override
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


    @Override
    public void keepAlive( String username, PlanCommunity planCommunity, int refreshDelay ) {
        if ( !getUserLives( planCommunity.getUri() ).containsKey( username ) ) {
            // Record first sign of life
            recordPresence( username, planCommunity );
        }
        Map<String, Long> lives = getUserLives( planCommunity.getUri() );
        lives.put( username, System.currentTimeMillis() + ( refreshDelay * 2L * 1000L ) );
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
