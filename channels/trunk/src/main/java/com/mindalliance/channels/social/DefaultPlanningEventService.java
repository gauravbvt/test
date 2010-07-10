package com.mindalliance.channels.social;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.odb.ODBAccessor;
import com.mindalliance.channels.odb.ODBTransactionFactory;
import org.neodatis.odb.core.query.criteria.Where;

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

    public DefaultPlanningEventService() {
        resetLatestPresences();
    }

    private void resetLatestPresences() {
        latestPresences = new HashMap<String, PresenceEvent>();
    }

    public void setDatabaseFactory( ODBTransactionFactory databaseFactory ) {
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
        getOdb().store( planningEvent );
    }

    public Iterator<CommandEvent> getCommandEvents() {
        return getOdb().iterate(
                CommandEvent.class,
                Where.equal( "planId", getPlanId() ),
                ODBAccessor.Ordering.Descendant,
                "date" );
    }

    public PresenceEvent findLatestPresence( String username ) {
        if ( latestPresences.containsKey( username ) ) {
            return latestPresences.get( username );
        } else {
            return getOdb().first(
                    PresenceEvent.class,
                    Where.and()
                            .add( Where.equal( "username", username ) )
                            .add( Where.equal( "planId", getPlanId() ) ),
                    ODBAccessor.Ordering.Descendant,
                    "date" );
        }
    }

    private ODBAccessor getOdb() {
        return databaseFactory.getODBAccessor();
    }


    private long getPlanId() {
        return User.current().getPlan().getId();
    }

}
