package com.mindalliance.channels.social;

import com.mindalliance.channels.core.PersistentObjectDao;
import com.mindalliance.channels.core.PersistentObjectDaoFactory;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.model.Plan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Planning event service default implementation.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 2, 2010
 * Time: 12:29:07 PM
 */
public class DefaultPlanningEventService implements PlanningEventService {

    private PersistentObjectDaoFactory databaseFactory;

    private Map<String, Map<String, PresenceEvent>> latestPresences;
    private Map<String, Date> whenLastChanged;
    private Date startupDate;
    private Set<String> deaths = new HashSet<String>();
    /**
     * Times after which users are considered "dead", unless incremented by "keepAlive" signals.
     */
    private Map<String,Map<String, Long>> userLives;


    public DefaultPlanningEventService() {
        userLives = Collections.synchronizedMap( new HashMap<String, Map<String,Long>>() );
        startupDate = new Date();
        whenLastChanged = new HashMap<String, Date>();
        latestPresences = new HashMap<String, Map<String, PresenceEvent>>();
    }

    private void resetLatestPresences( String urn ) {
        latestPresences.put( urn, new HashMap<String, PresenceEvent>() );
    }

    public void setDatabaseFactory( PersistentObjectDaoFactory databaseFactory ) {
        this.databaseFactory = databaseFactory;
    }

    @Override
    public void commandDone( Commander commander, Command command, Change change ) {
        Plan plan = commander.getPlan();
        CommandEvent commandEvent = new CommandEvent( CommandEvent.Type.Done, command, change, plan.getUrn() );
        addPlanningEvent( commandEvent, plan.getUrn() );
    }

    @Override
    public void commandUndone( Commander commander, Command command, Change change ) {
        Plan plan = commander.getPlan();
        CommandEvent commandEvent = new CommandEvent( CommandEvent.Type.Undone, command, plan.getUrn() );
        addPlanningEvent( commandEvent, plan.getUrn() );
    }

    @Override
    public void commandRedone( Commander commander, Command command, Change change ) {
        Plan plan = commander.getPlan();
        CommandEvent commandEvent = new CommandEvent( CommandEvent.Type.Redone, command, plan.getUrn() );
        addPlanningEvent( commandEvent, plan.getUrn() );
    }

    @Override
    public synchronized void killIfAlive( String username, String urn ) {
        if ( isAlive( username, urn ) )
            kill( username, urn );
    }

    private void kill( String username, String urn ) {
        recordAbsence( username, urn );
        getUserLives( urn ).remove( username );
        deaths.add( username );
    }

    @Override
    public synchronized void keepAlive( String username, String urn, int refreshDelay ) {
        if ( !getUserLives( urn ).containsKey( username ) ) {
            // Record first sign of life
            recordPresence( username, urn );
        }
        Map<String, Long> lives = getUserLives( urn );
        lives.put( username, System.currentTimeMillis() + ( refreshDelay * 2 * 1000 ) );
    }

    @Override
    public synchronized boolean isAlive( String username, String urn ) {
        long now = System.currentTimeMillis();
        Map<String, Long> lives = getUserLives( urn );
        return lives.containsKey( username ) && now <= lives.get( username );
    }

    @Override
    public synchronized List<String> giveMeYourDead( String urn ) {
        discoverDeadUsers( urn );
        List<String> deathRoll = new ArrayList<String>( deaths );
        deaths = new HashSet<String>();
        return deathRoll;
    }

    @Override
    public synchronized PresenceEvent findLatestPresence( String username, String urn ) {
        PresenceEvent latestPresence;
        if ( isLatestPresenceCached( urn, username ) ) {
            latestPresence = getLatestCachedPresence( urn, username );
        } else {
            latestPresence = getOdb( urn ).findLatestFrom( PresenceEvent.class, username, startupDate );
            cacheLatestPresence( urn, username, latestPresence );
        }
        return latestPresence;
    }

    private void discoverDeadUsers( String urn ) {
        List<String> discovered = new ArrayList<String>(  );
        for ( String username : getUserLives(  urn  ).keySet() ) {
            if ( !isAlive(  username, urn ) ) {
               discovered.add( username );
            }
        }
        for ( String deadUser : discovered ) {
             kill( deadUser, urn );
        }
    }

    private void recordPresence( String username, String urn ) {
        resetLatestPresences( urn );
        // removePlanningEvents( PresenceEvent.Type.Active, username, plan );
        addPlanningEvent( new PresenceEvent( PresenceEvent.Type.Active, username, urn ), urn );
    }

    private void recordAbsence( String username, String urn ) {
        resetLatestPresences( urn );
        // removePlanningEvents( PresenceEvent.Type.Active, username, plan );
        addPlanningEvent( new PresenceEvent( PresenceEvent.Type.Inactive , username, urn ), urn );
    }

    private Map<String, Long> getUserLives( String urn ) {
        Map<String, Long> planLives = userLives.get( urn );
        if ( planLives == null ) {
            planLives = new HashMap<String, Long>();
            userLives.put( urn, planLives );
        }
        return planLives;
    }

    private void addPlanningEvent( PlanningEvent planningEvent, String urn ) {
        getOdb( urn ).store( planningEvent );
        whenLastChanged.put( urn, new Date() );
    }

    @Override
    public Iterator<CommandEvent> getCommandEvents( String urn ) {
        return getOdb( urn ).findAll( CommandEvent.class );
    }

    private boolean isLatestPresenceCached( String urn, String username ) {
        return getLatestPresenceCache( urn ).get( username ) != null;
    }

    private void cacheLatestPresence( String urn, String username, PresenceEvent latestPresence ) {
        getLatestPresenceCache( urn ).put( username, latestPresence );
    }

    private PresenceEvent getLatestCachedPresence( String urn, String username ) {
        return getLatestPresenceCache( urn ).get( username );
    }

    private Map<String, PresenceEvent> getLatestPresenceCache( String urn ) {
        Map<String, PresenceEvent> cache = latestPresences.get( urn );
        if ( cache == null ) {
            cache = new HashMap<String, PresenceEvent>();
            latestPresences.put( urn, cache );
        }
        return cache;
    }


    private PersistentObjectDao getOdb( String urn ) {
        return databaseFactory.getDao( urn );
    }


    @Override
    public Date getWhenLastChanged( String urn ) {
        return whenLastChanged.get( urn );
    }

}
