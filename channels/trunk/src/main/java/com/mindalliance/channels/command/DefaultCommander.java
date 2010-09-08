package com.mindalliance.channels.command;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.dao.Exporter;
import com.mindalliance.channels.dao.ImportExportFactory;
import com.mindalliance.channels.dao.Journal;
import com.mindalliance.channels.dao.JournalCommand;
import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.Delay;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.social.CommandListener;
import com.mindalliance.channels.social.PresenceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 3, 2009
 * Time: 1:47:58 PM
 */
public class DefaultCommander implements Commander {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultCommander.class );

    /**
     * Lock manager.
     */
    private LockManager lockManager;

    /**
     * Done and undone history.
     */
    private History history = new History();

    /**
     * Query service.
     */
    private QueryService queryService;

    /**
     * Plan analyst.
     */
    private Analyst analyst;

    /**
     * Whether in reloading mode, i.e. replaying journaled commands.
     */
    private boolean replaying;

    /**
     * The planDao (and therefore, plan) used by this commander.
     */
    private PlanDao planDao;

    private PlanManager planManager;

    private ImportExportFactory importExportFactory;

    /**
     * Record of when users were most recently active.
     */
    private Map<String, Long> whenLastActive = Collections.synchronizedMap( new HashMap<String, Long>() );

    /**
     * Times after which users are considered "dead", unless incremented by "keepAlive" signals.
     */
    private Map<String, Long> userLives = Collections.synchronizedMap( new HashMap<String, Long>() );

    /**
     * Users who timed out but have yet to be refreshed.
     */
    private Set<String> timedOut = Collections.synchronizedSet( new HashSet<String>() );

    /**
     * Default timeout period  in seconds = 5 minutes
     */
    private int timeout = 300;

    /**
     * A user's copied state of eiher a part, flow or attachment.
     */
    private Map<String, Map<String, Object>> copy = Collections.synchronizedMap(
            new HashMap<String, Map<String, Object>>() );

    /**
     * When timeouts were last checked.
     */
    private long whenLastCheckedForTimeouts = System.currentTimeMillis();
    /**
     * Presence listeners.
     */
    private List<PresenceListener> presenceListeners = new ArrayList<PresenceListener>();
    /**
     * Command listeners.
     */
    private List<CommandListener> commandListeners = new ArrayList<CommandListener>();

    //===============================================
    public DefaultCommander() {
    }

    public void setPresenceListeners( List<PresenceListener> presenceListeners ) {
        this.presenceListeners = presenceListeners;
    }

    public void setCommandListeners( List<CommandListener> commandListeners ) {
        this.commandListeners = commandListeners;
    }

    /**
     * Add a presence listener.
     *
     * @param presenceListener a presence listener
     */
    public void addPresenceListener( PresenceListener presenceListener ) {
        presenceListeners.add( presenceListener );
    }

    public void setAnalyst( Analyst analyst ) {
        this.analyst = analyst;
    }

    public Analyst getAnalyst() {
        return analyst;
    }

    public Map<String, Object> getCopy() {
        return copy.get( User.current().getUsername() );
    }

    public void setCopy( Map<String, Object> state ) {
        copy.put( User.current().getUsername(), state );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPartCopied() {
        Map<String, Object> userCopy = getCopy();
        return userCopy != null && userCopy.get( "partState" ) != null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFlowCopied() {
        Map<String, Object> userCopy = getCopy();
        return userCopy != null && userCopy.get( "isSend" ) != null;
    }

    public boolean isAttachmentCopied() {
        Map<String, Object> userCopy = getCopy();
        return userCopy != null && userCopy.get( "url" ) != null && userCopy.get( "type" ) != null;
    }

    public void setLockManager( LockManager lockManager ) {
        this.lockManager = lockManager;
    }

    public void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
    }

    public QueryService getQueryService() {
        return queryService;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout( int timeout ) {
        this.timeout = timeout;
    }

    public boolean isReplaying() {
        return replaying;
    }

    public void setReplaying( boolean replaying ) {
        this.replaying = replaying;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> T resolve( Class<T> clazz, Long id ) throws CommandException {
        try {
            return queryService.find( clazz, id );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void resetUserHistory( String userName, boolean all ) {
        history.resetForUser( userName, all );
    }

    /**
     * {@inheritDoc}
     */
    public String getUndoTitle() {
        String title = "Undo";
        Memento memento = history.getUndo();
        if ( memento != null ) {
            title = title + " " + memento.getCommand().getName();
        }
        return title;
    }

    /**
     * {@inheritDoc}
     */
    public String getRedoTitle() {
        String title = "Redo";
        // memento of a command undoing another
        Memento memento = history.getRedo();
        if ( memento != null ) {
            title = title + " " + memento.getCommand().getUndoes( this );
        }
        return title;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canDo( Command command ) {
        return getPlan().isDevelopment()
                && command.canDo( this )
                && lockManager.canGrabLocksOn( command.getLockingSet() );
    }

    private Change execute( Command command ) throws CommandException {
        Change change;
        if ( command.isAuthorized() ) {
            try {
                Collection<Lock> grabbedLocks = lockManager.grabLocksOn( command.getLockingSet() );
                change = command.execute( this );
                if ( change.isNone() ) LOG.info( "No change" );
                lockManager.releaseLocks( grabbedLocks );
            } catch ( LockingException e ) {
                throw new CommandException( e.getMessage(), e );
            }
        } else {
            throw new CommandException( "You are not authorized." );
        }
        updateUserActive( command.getUserName() );
        return change;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized boolean canUndo() {
        if ( getPlan().isDevelopment() ) {
            Memento memento = history.getUndo();
            if ( memento != null ) {
                Command command = memento.getCommand();
                if ( command.isUndoable() ) {
                    try {
                        return command.noLockRequired() || canDo( command.getUndoCommand( this ) );
                    } catch ( CommandException e ) {
                        LOG.debug( "Unable to test undoability", e );
                    }
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized boolean canRedo() {
        if ( getPlan().isDevelopment() ) {
            Memento memento = history.getRedo();
            if ( memento != null ) {
                Command command = memento.getCommand();
                if ( command.isUndoable() ) {
                    try {
                        return command.noLockRequired() || canDo( command.getUndoCommand( this ) );
                    } catch ( CommandException e ) {
                        LOG.debug( "Unable to test redoability", e );
                    }
                }
            }
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public synchronized Change doCommand( Command command ) {
        try {
            if ( !getPlan().isDevelopment() )
                throw new CommandException(
                        "This version is no longer in development. You need to refresh. " );
            if ( command instanceof MultiCommand ) LOG.info( "*** START multicommand ***" );
            LOG.info( ( isReplaying() ? "Replaying: " : "Doing: " ) + command.toString() );
            Change change = execute( command );
            if ( command instanceof MultiCommand ) LOG.info( "*** END multicommand ***" );
            if ( !change.isNone() && !change.isFailed() ) {
                history.recordDone( command );
                afterExecution( command, change );
                if ( !isReplaying() && command.isTop() ) {
                    for ( CommandListener commandListener : commandListeners ) {
                        commandListener.commandDone( command, change );
                    }
                }
            }
            return change;
        } catch ( CommandException e ) {
            LOG.warn( "Command failed " + command, e );
            return Change.failed( e.getMessage() );
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Change undo() {
        Memento memento = history.getUndo();
        if ( memento == null ) {
            return Change.failed( "Nothing can be undone right now." );
        }
        try {
            Command undoCommand = memento.getCommand().getUndoCommand( this );
            if ( undoCommand instanceof MultiCommand ) {
                LOG.info( "*** START multicommand ***" );
            }
            LOG.info( "Undoing: " + undoCommand.toString() );
            Change change = execute( undoCommand );
            if ( undoCommand instanceof MultiCommand )
                LOG.info( "*** END multicommand ***" );

            change.setUndoing( true );
            history.recordUndone( memento, undoCommand );
            afterExecution( undoCommand, change );
            for ( CommandListener commandListener : commandListeners ) {
                commandListener.commandUndone( undoCommand );
            }
            return change;
        } catch ( CommandException e ) {
            return Change.failed( "Could not undo" );
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Change redo() {
        // Get memento of undoing command
        Memento memento = history.getRedo();
        if ( memento == null ) {
            return Change.failed( "Nothing can be redone right now." );
        }
        try {
            // undo the undoing
            Command redoCommand = memento.getCommand().getUndoCommand( this );
            LOG.info( "Redoing: {}", redoCommand.toString() );
            Change change = execute( redoCommand );
            change.setUndoing( true );
            history.recordRedone( memento, redoCommand );
            afterExecution( redoCommand, change );
            for ( CommandListener commandListener : commandListeners ) {
                commandListener.commandRedone( redoCommand );
            }
            return change;
        } catch ( CommandException e ) {
            return Change.failed( "Failed to redo" );
        }
    }

    private void afterExecution( Command command, Change change ) {
        if ( !isReplaying() && command.isTop() && !change.isNone() ) {
            LOG.debug( "***After command" );

            // TODO Implement proper observers/listeners
            planManager.onAfterCommand( getPlan(), command );
            analyst.onAfterCommand( getPlan() );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void reset() {
        replaying = false;
        history.reset();
        lockManager.reset();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized boolean cleanup( Class<? extends ModelObject> clazz, String name ) {
        if ( name == null || name.trim().isEmpty() )
            return false;

        ModelObject mo = planDao.find( clazz, name.trim() );
        if ( mo == null || mo.isUnknown() || !mo.isUndefined()
                || queryService.isReferenced( mo ) || mo.isImmutable() )
            return false;

        LOG.info( "Removing unused " + mo.getClass().getSimpleName() + ' ' + mo );
        queryService.remove( mo );
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLockedByUser( Identifiable identifiable ) {
        return lockManager.isLockedByUser( identifiable );
    }

    /**
     * {@inheritDoc}
     */
    public boolean requestLockOn( Identifiable identifiable ) {
        if ( isTimedOut() || identifiable == null ) return false;
        updateUserActive( User.current().getUsername() );
        return lockManager.requestLockOn( identifiable );
    }

    /**
     * {@inheritDoc}
     */
    public boolean requestLockOn( Long id ) {
        if ( isTimedOut() ) return false;
        updateUserActive( User.current().getUsername() );
        return lockManager.requestLockOn( id );
    }

    /**
     * {@inheritDoc}
     */
    public boolean releaseAnyLockOn( Identifiable identifiable ) {
        return lockManager.releaseAnyLockOn( identifiable );
    }

    /**
     * {@inheritDoc}
     */
    public boolean releaseAnyLockOn( Long id ) {
        return lockManager.releaseAnyLockOn( id );
    }

    /**
     * {@inheritDoc}
     */
    public void releaseAllLocks( String userName ) {
        lockManager.releaseAllLocks( userName );
    }

    /**
     * {@inheritDoc}
     */
    public long getLastModified() {
        return history.getLastModified();
    }

    /**
     * {@inheritDoc}
     */
    public String getLastModifier() {
        return history.getLastModifier();
    }

    private synchronized void updateUserActive( String userName ) {
        whenLastActive.put( userName, System.currentTimeMillis() );
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void keepAlive( String userName, int refreshDelay ) {
        if ( !userLives.containsKey( userName ) ) {
            LOG.info( "{} is planning", userName );
        }
        userLives.put( userName, System.currentTimeMillis() + refreshDelay * 2 * 1000 );
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void processDeaths() {
        List<String> deads = new ArrayList<String>();

        long now = System.currentTimeMillis();
        for ( String userName : userLives.keySet() )
            if ( now > userLives.get( userName ) ) {
                loggedOut( userName );
                deads.add( userName );
                LOG.info( "{} is done planning", userName );
                lockManager.releaseAllLocks( userName );
            }

        for ( String userName : deads )
            userLives.remove( userName );
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void loggedOut( String username ) {
        for ( PresenceListener presenceListener : presenceListeners ) {
            presenceListener.loggedOut( username );
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void loggedIn( String username ) {
        for ( PresenceListener presenceListener : presenceListeners ) {
            presenceListener.loggedIn( username );
        }
    }


    /**
     * {@inheritDoc}
     */
    public synchronized void processTimeOuts() {
        long now = System.currentTimeMillis();
        long timeoutMillis = timeout * 1000L;
        if ( timeoutMillis < now - whenLastCheckedForTimeouts ) {
            for ( String userName : whenLastActive.keySet() ) {
                long time = whenLastActive.get( userName );
                if ( timeoutMillis < now - time && lockManager.releaseAllLocks( userName ) )
                    timedOut.add( userName );
            }

            for ( String userName : timedOut )
                whenLastActive.remove( userName );

            whenLastCheckedForTimeouts = now;
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized boolean isTimedOut() {
        return timedOut.contains( User.current().getUsername() );
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void clearTimeOut() {
        timedOut.remove( User.current().getUsername() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUnlocked( ModelObject mo ) {
        return lockManager.getLock( mo.getId() ) == null;
    }

    /**
     * {@inheritDoc}
     */
    public void replay( Journal journal ) throws CommandException {
        setReplaying( true );
        if ( !journal.isEmpty() )
            for ( JournalCommand command : journal.getCommands() ) {
                Change change = doCommand( (Command) command );
                if ( change.isFailed() ) {
                    throw new CommandException( "Command failed" );
                }
            }
        journal.reset();
        reset();
    }

    public Plan getPlan() {
        return planDao.getPlan();
    }

    /**
     * {@inheritDoc}
     */
    public void setResyncRequired() {
        planManager.setResyncRequired( getPlan().getUri() );
    }

    /**
     * {@inheritDoc}
     */
    public void resynced() {
        planManager.resynced( User.current() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isOutOfSync() {
        return planManager.isOutOfSync( User.current(), getPlan().getUri() );
    }

    /**
     * {@inheritDoc}
     */
    public Exporter getExporter() {
        return importExportFactory.createExporter( planDao );
    }

    public PlanManager getPlanManager() {
        return planManager;
    }

    public void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
    }

    public PlanDao getPlanDao() {
        return planDao;
    }

    public void setPlanDao( PlanDao planDao ) {
        this.planDao = planDao;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLockable( String className ) {
        if ( className == null ) return false;
        try {
            Class clazz = Class.forName( className );
            return Identifiable.class.isAssignableFrom( clazz ) &&
                    !Plan.class.isAssignableFrom( clazz ) &&
                    !Segment.class.isAssignableFrom( clazz );
        } catch ( ClassNotFoundException e ) {
            throw new IllegalArgumentException( "Class not found", e );
        }
    }

    public void initialize() {
        replayJournal();
        queryService.getAttachmentManager().removeUnattached( planDao );
        analyst.onStart( planDao.getPlan() );
    }

    public ImportExportFactory getImportExportFactory() {
        return importExportFactory;
    }

    public void setImportExportFactory( ImportExportFactory importExportFactory ) {
        this.importExportFactory = importExportFactory;
    }

    /**
     * Replay journaled commands for current plan.
     */
    public void replayJournal() {
        Plan plan = planDao.getPlan();

        try {
            if ( plan.isDevelopment() ) {
                replay( planDao.getJournal() );
                LOG.info( "Replayed journal for plan {}", plan );

                planDao.save( importExportFactory.createExporter( planDao ) );
            }

        } catch ( IOException e ) {
            LOG.error( MessageFormat.format( "Unable to save plan {0}", plan ), e );
        } catch ( CommandException e ) {
            LOG.error( MessageFormat.format( "Unable to replay journal for plan {0}", plan ), e );
        }
    }

    /*
        List<Map<String, Object>> goalMaps = new ArrayList<Map<String, Object>>();
        for ( Goal goal : getGoals() ) {
            goalMaps.add( goal.toMap() );
        }
     */

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public void initPartFrom( Part part, Map<String, Object> state ) {
        part.setDescription( (String) state.get( "description" ) );
        part.setTask( (String) state.get( "task" ) );
        part.setRepeating( (Boolean) state.get( "repeating" ) );
        part.setSelfTerminating( (Boolean) state.get( "selfTerminating" ) );
        part.setTerminatesEventPhase( (Boolean) state.get( "terminatesEventPhase" ) );
        part.setStartsWithSegment( (Boolean) state.get( "startsWithSegment" ) );
        part.setRepeatsEvery( (Delay) state.get( "repeatsEvery" ) );
        part.setCompletionTime( (Delay) state.get( "completionTime" ) );
        part.setAttachments( new ArrayList<Attachment>( (List<Attachment>) state.get( "attachments" ) ) );
        List<Map<String, Object>> goalStates = (List<Map<String, Object>>) state.get( "goals" );
        for ( Map<String, Object> goalMap : goalStates ) {
            Goal goal = Goal.fromMap( goalMap, getQueryService() );
            part.addGoal( goal );
        }
//        part.setGoals( new ArrayList<Goal>( (List<Goal>) state.get( "goals" ) ) );
        if ( state.get( "initiatedEvent" ) != null )
            part.setInitiatedEvent( queryService.findOrCreateType(
                    Event.class,
                    (String) state.get( "initiatedEvent" ) ) );
        else
            part.setInitiatedEvent( null );
        if ( state.get( "actor" ) != null )
            part.setActor( queryService.retrieveEntity( Actor.class, state, "actor" ) );
        else
            part.setActor( null );
        if ( state.get( "role" ) != null )
            part.setRole( queryService.retrieveEntity( Role.class, state, "role" ) );
        else
            part.setRole( null );
        if ( state.get( "organization" ) != null )
            part.setOrganization( queryService.retrieveEntity(
                    Organization.class, state, "organization" ) );
        else
            part.setOrganization( null );
        if ( state.get( "jurisdiction" ) != null )
            part.setJurisdiction( queryService.retrieveEntity( Place.class, state, "jurisdiction" ) );
        else
            part.setJurisdiction( null );
        if ( state.get( "location" ) != null )
            part.setLocation( queryService.retrieveEntity( Place.class, state, "location" ) );
        else
            part.setLocation( null );
    }
}
