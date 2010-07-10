package com.mindalliance.channels.social;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.dao.User;
import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Planning event service default implementation.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 2, 2010
 * Time: 12:29:07 PM
 */
public class DefaultPlanningEventService implements PlanningEventService {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultPlanningEventService.class );

    private DatabaseFactory databaseFactory;

    private Map<String, PresenceEvent> latestPresences = null;

    public DefaultPlanningEventService() {
        resetLatestPresences();
    }

    private void resetLatestPresences() {
        latestPresences = new HashMap<String, PresenceEvent>();
    }

    public void setDatabaseFactory( DatabaseFactory databaseFactory ) {
        this.databaseFactory = databaseFactory;
    }

    public void commandDone( Command command, Change change ) {
        CommandEvent commandEvent = new CommandEvent( CommandEvent.Type.Done, command, change );
        addPlanningEvent( commandEvent );
    }

    public void commandUndone( Command command ) {
        CommandEvent commandEvent = new CommandEvent( CommandEvent.Type.Undone, command );
        addPlanningEvent( commandEvent );
    }

    public void commandRedone( Command command ) {
        CommandEvent commandEvent = new CommandEvent( CommandEvent.Type.Redone, command );
        addPlanningEvent( commandEvent );
    }

    public void loggedIn( String username ) {
        addPlanningEvent( new PresenceEvent( PresenceEvent.Type.Login, username ) );
    }

    public void loggedOut( String username ) {
        if ( !isLoggedOut( username ) )
            addPlanningEvent( new PresenceEvent( PresenceEvent.Type.Logout, username ) );
    }

    private boolean isLoggedOut( String username ) {
        PresenceEvent presenceEvent = findLatestPresence( username );
        return presenceEvent != null && presenceEvent.isLogout();
    }

    private void addPlanningEvent( PlanningEvent planningEvent ) {
        if ( planningEvent.isPresenceEvent() ) {
            resetLatestPresences();
        }
        ODB odb = null;
        try {
            odb = getOdb();
            odb.store( planningEvent );
            LOG.info( "Planning event: " + planningEvent );
        } catch ( Exception e ) {
            LOG.warn( "Failed to store commandEvent " + planningEvent );
        } finally {
            if ( odb != null && !odb.isClosed() )
                odb.close();
        }
    }

    public Iterator<CommandEvent> getCommandEvents() {
        Iterator<CommandEvent> commandEvents;
        IQuery query = new CriteriaQuery( CommandEvent.class, Where.equal( "planId", getPlanId() ) );
        query.orderByDesc( "date" );
        Objects<CommandEvent> results;
        ODB odb = null;
        try {
            odb = getOdb();
            results = odb.getObjects( query );
            commandEvents = results.iterator();
        } catch ( Exception e ) {
            LOG.warn( "Failed to query for command events", e );
            commandEvents = new ArrayList<CommandEvent>().iterator();
        } finally {
            if ( odb != null && !odb.isClosed() )
                odb.close();
        }
        return commandEvents;
    }

    public PresenceEvent findLatestPresence( String username ) {
        if ( latestPresences.containsKey( username ) ) {
            return latestPresences.get( username );
        } else {
            PresenceEvent presenceEvent = null;
            IQuery query = new CriteriaQuery(
                    PresenceEvent.class,
                    Where.and()
                            .add( Where.equal( "username", username ) )
                            .add( Where.equal( "planId", getPlanId() ) ) );
            query.orderByDesc( "date" );
            Objects<PresenceEvent> results;
            ODB odb = null;
            try {
                odb = getOdb();
                results = odb.getObjects( query );
                if ( results.hasNext() ) {
                    presenceEvent = results.next();
                }
            } catch ( Exception e ) {
                LOG.warn( "Failed to query for latest presence event of " + username, e );
            } finally {
                if ( odb != null && !odb.isClosed() )
                    odb.close();
            }
            latestPresences.put( username, presenceEvent );
            return presenceEvent;
        }
    }

    private ODB getOdb() {
        return databaseFactory.getDatabase();
    }


    private long getPlanId() {
        return User.current().getPlan().getId();
    }

}
