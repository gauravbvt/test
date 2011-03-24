package com.mindalliance.channels.social;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.odb.ODBAccessor;
import com.mindalliance.channels.odb.ODBTransactionFactory;
import org.neodatis.odb.core.query.criteria.Where;

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

    private ODBTransactionFactory databaseFactory;

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

    private void resetLatestPresences( Plan plan ) {
        latestPresences.put( plan.getUri(), new HashMap<String, PresenceEvent>() );
    }

    public void setDatabaseFactory( ODBTransactionFactory databaseFactory ) {
        this.databaseFactory = databaseFactory;
    }

    public void commandDone( Command command, Change change, Plan plan ) {
        CommandEvent commandEvent = new CommandEvent( CommandEvent.Type.Done, command, change, plan.getUri() );
        addPlanningEvent( commandEvent, plan );
    }

    public void commandUndone( Command command, Plan plan ) {
        CommandEvent commandEvent = new CommandEvent( CommandEvent.Type.Undone, command, plan.getUri() );
        addPlanningEvent( commandEvent, plan );
    }

    public void commandRedone( Command command, Plan plan ) {
        CommandEvent commandEvent = new CommandEvent( CommandEvent.Type.Redone, command, plan.getUri() );
        addPlanningEvent( commandEvent, plan );
    }

    public synchronized void inactive( String username, Plan plan ) {
        if ( isActive( username, plan ) ) {
            recordAbsence( username, plan );
            kill( username, plan );
        }
    }

    @Override
    public synchronized void keepAlive( String username, Plan plan, int refreshDelay ) {
        if ( !isActive( username, plan ) ) {
            recordPresence( username, plan );
        }
        Map<String, Long> lives = getUserLives( plan );
        lives.put( username, System.currentTimeMillis() + refreshDelay * 2 * 1000 );
    }

    public synchronized boolean isActive( String username, Plan plan ) {
        Map<String, Long> lives = getUserLives( plan );
        boolean alive = false;
        if ( lives.containsKey( username ) ) {
                if ( System.currentTimeMillis() <= lives.get( username ) ) {
                    alive = true;
                } else {
                    recordAbsence( username, plan );
                    kill( username, plan );
                }
        }
        return alive;
    }

    @Override
    public synchronized List<String> giveMeYourDead( Plan plan ) {
        List<String> deathRoll = new ArrayList<String>( deaths );
        deaths = new HashSet<String>();
        return deathRoll;
    }

    public synchronized PresenceEvent findLatestPresence( String username, Plan plan ) {
        PresenceEvent latestPresence;
        if ( isLatestPresenceCached( plan, username ) ) {
            latestPresence = getLatestCachedPresence( plan, username );
        } else {
            latestPresence = getOdb( plan ).first(
                    PresenceEvent.class,
                    Where.and()
                            .add( Where.equal( "username", username ) )
                            .add( Where.equal( "planId", plan.getUri() ) )
                            .add( Where.ge( "date", startupDate ) ),
                    ODBAccessor.Ordering.Descendant,
                    "date" );
            cacheLatestPresence( plan, username, latestPresence );
        }
        return latestPresence;
    }

    private void kill( String username, Plan plan ) {
        getUserLives( plan ).remove( username );
        deaths.add( username );
    }

    private void recordPresence( String username, Plan plan ) {
        resetLatestPresences( plan );
        // removePlanningEvents( PresenceEvent.Type.Active, username, plan );
        addPlanningEvent( new PresenceEvent( PresenceEvent.Type.Active, username, plan.getUri() ), plan );
    }

    private void recordAbsence( String username, Plan plan ) {
        resetLatestPresences( plan );
        // removePlanningEvents( PresenceEvent.Type.Active, username, plan );
        addPlanningEvent( new PresenceEvent( PresenceEvent.Type.Active, username, plan.getUri() ), plan );
    }

    private Map<String, Long> getUserLives( Plan plan ) {
        Map<String, Long> planLives = userLives.get( plan.getUri() );
        if ( planLives == null ) {
            planLives = new HashMap<String, Long>();
            userLives.put( plan.getUri(), planLives );
        }
        return planLives;
    }

    private void addPlanningEvent( PlanningEvent planningEvent, Plan plan ) {
        getOdb( plan ).store( planningEvent );
        whenLastChanged.put( plan.getUri(), new Date() );
    }

    public Iterator<CommandEvent> getCommandEvents( Plan plan ) {
        return getOdb( plan ).iterate(
                CommandEvent.class,
                Where.equal( "planId", plan.getUri() ),
                ODBAccessor.Ordering.Descendant,
                "date" );
    }

    private boolean isLatestPresenceCached( Plan plan, String username ) {
        return getLatestPresenceCache( plan ).get( username ) != null;
    }

    private void cacheLatestPresence( Plan plan, String username, PresenceEvent latestPresence ) {
        getLatestPresenceCache( plan ).put( username, latestPresence );
    }

    private PresenceEvent getLatestCachedPresence( Plan plan, String username ) {
        return getLatestPresenceCache( plan ).get( username );
    }

    private Map<String, PresenceEvent> getLatestPresenceCache( Plan plan ) {
        Map<String, PresenceEvent> cache = latestPresences.get( plan.getUri() );
        if ( cache == null ) {
            cache = new HashMap<String, PresenceEvent>();
            latestPresences.put( plan.getUri(), cache );
        }
        return cache;
    }


    private ODBAccessor getOdb( Plan plan ) {
        return databaseFactory.getODBAccessor( plan.getUri() );
    }


    public Date getWhenLastChanged( Plan plan ) {
        return whenLastChanged.get( plan.getUri() );
    }

}
