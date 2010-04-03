package com.mindalliance.channels.command;

import com.mindalliance.channels.AbstractService;
import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.dao.Exporter;
import com.mindalliance.channels.LockManager;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.commands.CreateEntityIfNew;
import com.mindalliance.channels.dao.Journal;
import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.ImportExportFactory;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.dao.FileUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 3, 2009
 * Time: 1:47:58 PM
 */
public class DefaultCommander extends AbstractService implements Commander {

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

    /** The planDao (and therefore, plan) used by this commander. */
    private PlanDao planDao;

    private PlanManager planManager;

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

    //===============================================
    public DefaultCommander() {
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
    public synchronized Change doCommand( Command command ) throws CommandException {
        if ( !getPlan().isDevelopment() )
            throw new CommandException(
                    "This version is no longer in development. You need to refresh. " );
        if ( command instanceof MultiCommand ) LOG.info( "*** START multicommand ***" );
        LOG.info( ( isReplaying() ? "Replaying: " : "Doing: " ) + command.toString() );
        Change change = execute( command );
        if ( command instanceof MultiCommand ) LOG.info( "*** END multicommand ***" );
        history.recordDone( command );
        afterExecution( command, change );
        return change;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Change undo() throws CommandException {
        Memento memento = history.getUndo();
        if ( memento == null )
            throw new CommandException( "Nothing can be undone right now." );

        Command undoCommand = memento.getCommand().getUndoCommand( this );
        if ( undoCommand instanceof MultiCommand )
            LOG.info( "*** START multicommand ***" );
        LOG.info( "Undoing: " + undoCommand.toString() );
        Change change = execute( undoCommand );
        if ( undoCommand instanceof MultiCommand )
            LOG.info( "*** END multicommand ***" );

        change.setUndoing( true );
        history.recordUndone( memento, undoCommand );
        afterExecution( undoCommand, change );
        return change;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Change redo() throws CommandException {
        // Get memento of undoing command
        Memento memento = history.getRedo();
        if ( memento == null )
            throw new CommandException( "Nothing can be redone right now." );

        // undo the undoing
        Command redoCommand = memento.getCommand().getUndoCommand( this );
        LOG.info( "Redoing: {}", redoCommand.toString() );
        Change change = execute( redoCommand );
        change.setUndoing( true );
        history.recordRedone( memento, redoCommand );
        afterExecution( redoCommand, change );
        return change;
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
        if ( isTimedOut() ) return false;
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

    private void updateUserActive( String userName ) {
        whenLastActive.put( userName, System.currentTimeMillis() );
    }

    /**
     * {@inheritDoc}
     */
    public void keepAlive( String userName, int refreshDelay ) {
        if ( !userLives.containsKey( userName ) )
            LOG.info( "{} is planning", userName );
        userLives.put( userName, System.currentTimeMillis() + refreshDelay * 2 * 1000 );
    }

    /**
     * {@inheritDoc}
     */
    public void processDeaths() {
        List<String> deads = new ArrayList<String>();

        long now = System.currentTimeMillis();
        for ( String userName : userLives.keySet() )
            if ( now > userLives.get( userName ) ) {
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
            for ( Command command : journal.getCommands() )
                doCommand( command );
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
        ImportExportFactory factory = planManager.getImportExportFactory();
        return factory.createExporter( planDao );
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

    public void initialize() {
        this.replayJournal();
        queryService.getAttachmentManager().removeUnattached( queryService, getPlan() );
        analyst.onStart();
    }

    public void createRequisiteModelObjects( FileUserDetailsService userDetailsService )
            throws CommandException {

        // Make sure that there is one participation per user
        for ( String username : userDetailsService.getAllPlanUsernames() ) {
            if ( planManager.getParticipant( getPlan(), username ) != null )
                doCommand( new CreateEntityIfNew(
                        Participation.class, username, ModelEntity.Kind.Actual ) );
        }
    }

    /**
     * Replay journaled commands for current plan.
     */
    public void replayJournal() {
        Plan plan = getPlan();
        try {
            if ( plan.isDevelopment() ) {
                replay( planDao.getJournal() );
                LOG.info( "Replayed journal for plan {}", plan );
                planDao.save( planManager.getImportExportFactory().createExporter( planDao ) );
                createRequisiteModelObjects( planManager.getUserDetailsService() );
            }
        } catch ( IOException e ) {
            LOG.error( MessageFormat.format( "Unable to save plan {0}", plan ), e );
        } catch ( CommandException e ) {
            LOG.error( MessageFormat.format( "Unable to replay journal for plan {0}", plan ), e );
        }
    }
}
