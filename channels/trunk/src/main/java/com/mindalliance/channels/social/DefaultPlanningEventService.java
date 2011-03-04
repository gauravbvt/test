package com.mindalliance.channels.social;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.odb.ODBAccessor;
import com.mindalliance.channels.odb.ODBTransactionFactory;
import org.neodatis.odb.core.query.criteria.Where;

import java.util.Date;
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

    private ODBTransactionFactory databaseFactory;

    private Map<String, PresenceEvent> latestPresences = null;
    private Map<String, Date> whenLastChanged;
    private Map<String, Date> startupDate;

    public DefaultPlanningEventService( ) {
        startupDate = new HashMap<String, Date>();
        whenLastChanged = new HashMap<String, Date>();
        resetLatestPresences();
    }

    public Map<String, PresenceEvent> getLatestPresences() {
        return latestPresences;
    }

    public void setLatestPresences( Map<String, PresenceEvent> latestPresences ) {
        this.latestPresences = latestPresences;
    }

    private void resetLatestPresences() {
        latestPresences = new HashMap<String, PresenceEvent>();
    }

    public void setDatabaseFactory( ODBTransactionFactory databaseFactory ) {
        this.databaseFactory = databaseFactory;
    }

    public void commandDone( Command command, Change change, Plan plan ) {
        CommandEvent commandEvent = new CommandEvent( CommandEvent.Type.Done, command, change, plan.getId() );
        addPlanningEvent( commandEvent, plan );
    }

    public void commandUndone( Command command, Plan plan ) {
        CommandEvent commandEvent = new CommandEvent( CommandEvent.Type.Undone, command, plan.getId() );
        addPlanningEvent( commandEvent, plan );
    }

    public void commandRedone( Command command, Plan plan ) {
        CommandEvent commandEvent = new CommandEvent( CommandEvent.Type.Redone, command, plan.getId() );
        addPlanningEvent( commandEvent, plan );
    }

    public void loggedIn( String username, Plan plan ) {
        addPlanningEvent( new PresenceEvent( PresenceEvent.Type.Login, username, plan.getId() ), plan );
    }

    public void loggedOut( String username, Plan plan ) {
        if ( !isLoggedOut( username, plan ) )
            addPlanningEvent( new PresenceEvent( PresenceEvent.Type.Logout, username, plan.getId() ), plan );
    }

    private boolean isLoggedOut( String username, Plan plan ) {
        PresenceEvent presenceEvent = findLatestPresence( username, plan );
        return presenceEvent != null && presenceEvent.isLogout();
    }

    private void addPlanningEvent( PlanningEvent planningEvent, Plan plan ) {
        markStarted( plan );
        if ( planningEvent.isPresenceEvent() ) {
            resetLatestPresences();
        }
        getOdb( plan ).store( planningEvent );
        whenLastChanged.put( plan.getUri(), new Date() );
    }

    public Iterator<CommandEvent> getCommandEvents( Plan plan ) {
        markStarted( plan );
        return getOdb( plan ).iterate(
                CommandEvent.class,
                Where.equal( "planId", plan.getId() ),
                ODBAccessor.Ordering.Descendant,
                "date" );
    }

    public PresenceEvent findLatestPresence( String username, Plan plan ) {
        markStarted( plan );
        PresenceEvent latestPresence;
        if ( latestPresences.containsKey( username ) ) {
            latestPresence = latestPresences.get( username );
        } else {
            latestPresence = getOdb( plan ).first(
                    PresenceEvent.class,
                    Where.and()
                            .add( Where.equal( "username", username ) )
                            .add( Where.equal( "planId", plan.getId() ) )
                            .add( Where.ge( "date", getStartupDate( plan.getUri() ) )),
                    ODBAccessor.Ordering.Descendant,
                    "date" );
            latestPresences.put( username, latestPresence );
        }
        return latestPresence;
    }

    private Date getStartupDate( String uri ) {
        Date startup = startupDate.get( uri );
        if ( startup == null ) {
            startupDate.put( uri, new Date() );
        }
        return startupDate.get( uri );
    }

    private ODBAccessor getOdb( Plan plan ) {
        return databaseFactory.getODBAccessor( plan.getUri() );
    }


    public Date getWhenLastChanged( Plan plan ) {
        return whenLastChanged.get( plan.getUri() );
    }

    private void markStarted( Plan plan ) {
        getStartupDate( plan.getUri() );
    }
}
