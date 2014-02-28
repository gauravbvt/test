/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command;

import com.mindalliance.channels.core.ChannelsLockable;
import com.mindalliance.channels.core.ModelObjectContext;
import com.mindalliance.channels.core.command.commands.DisconnectFlow;
import com.mindalliance.channels.core.command.commands.RemoveCapability;
import com.mindalliance.channels.core.command.commands.RemoveNeed;
import com.mindalliance.channels.core.community.CommunityDao;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.AbstractModelObjectDao;
import com.mindalliance.channels.core.dao.Exporter;
import com.mindalliance.channels.core.dao.ImportExportFactory;
import com.mindalliance.channels.core.dao.Journal;
import com.mindalliance.channels.core.dao.JournalCommand;
import com.mindalliance.channels.core.dao.ModelDao;
import com.mindalliance.channels.core.dao.ModelManager;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.query.ModelService;
import com.mindalliance.channels.core.query.QueryService;
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
 * Commander implementation.
 */
public class DefaultCommander implements Commander {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultCommander.class );

    /**
     * Command listeners.
     */
    private List<CommandListener> commandListeners = new ArrayList<CommandListener>();

    /**
     * A user's copied state of either a part, flow or attachment.
     */
    private Map<String, Map<String, Object>> copy =
            Collections.synchronizedMap( new HashMap<String, Map<String, Object>>() );

    /**
     * Done and undone history.
     */
    private History history = new History();

    private ImportExportFactory importExportFactory;

    /**
     * Lock manager.
     */
    private LockManager lockManager;

    private ModelManager modelManager;

    /**
     * Presence listeners.
     */
    private List<PresenceListener> presenceListeners = new ArrayList<PresenceListener>();

    /**
     * Whether in reloading mode, i.e. replaying journaled commands.
     */
    private boolean replaying;

    /**
     * Users who timed out but have yet to be refreshed.
     */
    private Set<String> timedOut = Collections.synchronizedSet( new HashSet<String>() );

    /**
     * Default timeout period  in seconds = 5 minutes.
     */
    private int timeout = 300;

    /**
     * Record of when users were most recently active.
     */
    private Map<String, Long> whenLastActive = Collections.synchronizedMap( new HashMap<String, Long>() );

    /**
     * When timeouts were last checked.
     */
    private long whenLastCheckedForTimeouts = System.currentTimeMillis();

    private CommunityService communityService;

    //-------------------------------
    public DefaultCommander() {
    }

    public PlanCommunity getPlanCommunity() {
        return communityService.getPlanCommunity();
    }

    public void setCommunityService( CommunityService communityService ) {
        this.communityService = communityService;
    }

    @Override
    public ModelService getPlanService() {
        return communityService.getModelService();
    }

    //-------------------------------
    /**
     * Add a presence listener.
     *
     * @param presenceListener a presence listener
     */
    public void addPresenceListener( PresenceListener presenceListener ) {
        presenceListeners.add( presenceListener );
    }

    private boolean isCommandExecutionAllowed() { // Use this instead of  getPlan().isDevelopment()
        return !getPlanCommunity().isModelCommunity() || getPlan().isDevelopment();
    }

    @Override
    public boolean canDo( Command command ) {
        return isCommandExecutionAllowed() && command.canDo( this )
               && lockManager.isLockableByUser( command.getUserName(), command.getLockingSet() );
    }

    @Override
    public boolean canRedo( String userName ) {
        synchronized ( getDao() ) {
            if ( isCommandExecutionAllowed() ) {
                Memento memento = history.getRedo( userName );
                if ( memento != null ) {
                    Command command = memento.getCommand();
                    if ( command.isUndoable() ) {
                        try {
                            return command.noLockRequired() || canDo( command.getUndoCommand( this ) );
                        } catch ( CommandException e ) {
                            LOG.debug( "Unable to test redo-ability", e );
                        }
                    }
                }
            }
            return false;
        }
    }

    @Override
    public boolean canUndo( String userName ) {
        synchronized ( getDao() ) {
            if ( isCommandExecutionAllowed() ) {
                Memento memento = history.getUndo( userName );
                if ( memento != null ) {
                    Command command = memento.getCommand();
                    if ( command.isUndoable() ) {
                        try {
                            return command.noLockRequired() || canDo( command.getUndoCommand( this ) );
                        } catch ( CommandException e ) {
                            LOG.debug( "Unable to test undo-ability", e );
                        }
                    }
                }
            }
            return false;
        }
    }

    @Override
    public boolean cleanup( Class<? extends ModelObject> clazz, String name ) {
        synchronized ( getDao() ) {
            return !( name == null || name.trim().isEmpty() ) && getDao().cleanup( clazz, name );
        }
    }

    @Override
    public void clearTimeOut( String userName ) {
        synchronized ( getDao() ) {
            timedOut.remove( userName );
        }
    }

    @Override
    public Change doCommand( Command command ) {
        synchronized ( getDao() ) {
            return executeCommand( command, true );
        }
    }

    private Change executeCommand( Command command, boolean safe ) {
        try {
            if ( safe && !isCommandExecutionAllowed() )
                throw new CommandException( "This version has been put into production. You need to go to the current development version." );

            if ( command instanceof MultiCommand )
                LOG.info( "*** START multicommand ***" );

            LOG.info( ( replaying ? "Replaying: " : "Doing: " ) + command.toString() );
            Change change = execute( command );
            if ( command instanceof MultiCommand )
                LOG.info( "*** END multicommand ***" );

            if ( !change.isNone() && !change.isFailed() ) {
                history.recordDone( command );
                if ( !replaying && command.isTop() )
                    for ( CommandListener commandListener : commandListeners )
                            commandListener.commandDone( this, command, change );
            }

            return change;
        } catch ( CommandException e ) {
            LOG.warn( "Command failed " + command );
            return Change.failed( e.getMessage() );
        }
    }

    private Change execute( Command command ) throws CommandException {
        String userName = command.getUserName();
        updateUserActive( userName );
        if ( command.isAuthorized() )
            try {
                Collection<Long> grabbedLocks = lockManager.lock( userName, command.getLockingSet() );
                Change change = command.execute( this );
                if ( change.isNone() )
                    LOG.info( "No change" );

                lockManager.release( userName, grabbedLocks );
                return change;
            } catch ( LockingException e ) {
                throw new CommandException( e.getMessage(), e );
            }

        throw new CommandException( "Required locks not acquired" );
    }

    @Override
    public Change doUnsafeCommand( Command command ) {
        synchronized ( getDao() ) {
            return executeCommand( command, false );
        }
    }

    @Override
    public Map<String, Object> getCopy( String userName ) {
        return copy.get( userName );
    }

    @Override
    public Exporter getExporter( String userName ) {
        return importExportFactory.createExporter( userName, getDao() );
    }

    @Override
    public long getLastModified() {
        return history.getLastModified();
    }

    @Override
    public String getLastModifier() {
        return history.getLastModifier();
    }

    public CollaborationModel getPlan() {
        return communityService.getPlan();
    }

    @Override
    public ModelDao getPlanDao() {
        return (ModelDao)getDao();
    }

    @Override
    public CommunityDao getCommunityDao() {
        return (CommunityDao)getDao();
    }

    @Override
    public CommunityService getCommunityService() {
        return communityService;
    }

    @Override
    public String getRedoTitle( String userName ) {
        // memento of a command undoing another
        Memento memento = history.getRedo( userName );
        String title = "Redo";
        if ( memento != null )
            title = title + ' ' + memento.getCommand().getUndoes( this );
        return title;
    }

    @Override
    public String getUndoTitle( String userName ) {
        String title = "Undo";
        Memento memento = history.getUndo( userName );
        if ( memento != null )
            title = title + ' ' + memento.getCommand().getName();
        return title;
    }

    /*
        List<Map<String, Object>> goalMaps = new ArrayList<Map<String, Object>>();
        for ( Goal goal : getGoals() ) {
            goalMaps.add( goal.toMap() );
        }
     */

    /**
     * Create goal from mapped state.
     *
     * @param map a map
     * @param queryService a query service
     * @return a goal
     */
    private static Goal fromMap( Map<String, Object> map, QueryService queryService ) {
        Goal goal = new Goal();
        goal.setCategory( Goal.Category.valueOf( (String) map.get( "category" ) ) );
        goal.setDescription( (String) map.get( "description" ) );
        goal.setLevel( Level.valueOf( (String) map.get( "level" ) ) );
        goal.setPositive( (Boolean) map.get( "positive" ) );
        goal.setEndsWithSegment( (Boolean) map.get( "ends" ) );
        List orgSpec = (List) map.get( "organization" );
        boolean isType = (Boolean) orgSpec.get( 1 );
        Organization org = isType ?
                           queryService.findOrCreateType( Organization.class, (String) orgSpec.get( 0 ) ) :
                           queryService.findOrCreate( Organization.class, (String) orgSpec.get( 0 ) );
        goal.setOrganization( org );
        return goal;
    }

    @Override
    public void initialize() {
        replayJournal();

        for ( CommandListener commandListener : commandListeners )
            commandListener.started( this );
    }

    /**
     * Replay journaled commands for current plan.
     */
    private void replayJournal() {
        ModelObjectContext modelObjectContext = getDao().getModelObjectContext();
        try {
            if ( isCommandExecutionAllowed() ) {
                replay( getDao().getJournal() );
                LOG.info( "Replayed journal for {}", modelObjectContext );

                getDao().save( importExportFactory.createExporter( "daemon", getDao() ) );
            }
        } catch ( IOException e ) {
            LOG.error( MessageFormat.format( "Unable to replay journal for {0}", modelObjectContext ), e );
        } catch ( CommandException e ) {
            LOG.error( MessageFormat.format( "Unable to replay journal for {0}", modelObjectContext ), e );
        }
    }

    @Override
    public boolean isAttachmentCopied( String userName ) {
        Map<String, Object> userCopy = getCopy( userName );
        return userCopy != null && userCopy.get( "url" ) != null && userCopy.get( "type" ) != null;
    }

    @Override
    public boolean isFlowCopied( String userName ) {
        Map<String, Object> userCopy = getCopy( userName );
        return userCopy != null && userCopy.get( "isSend" ) != null;
    }

    @Override
    public boolean isLockable( String className ) {
        if ( className == null )
            return false;
        try {
            Class clazz = Class.forName( className );
            return ChannelsLockable.class.isAssignableFrom( clazz );
        } catch ( ClassNotFoundException e ) {
            throw new IllegalArgumentException( "Class not found", e );
        }
    }

    @Override
    public boolean isLockedByUser( String userName, Identifiable identifiable ) {
        return lockManager.isLockedByUser( userName, identifiable.getId() );
    }

    @Override
    public boolean isPlanOutOfSync( String userName ) {
        return modelManager.isOutOfSync( userName, getPlan().getUri() );
    }

    @Override
    public boolean isPartCopied( String userName ) {
        Map<String, Object> userCopy = getCopy( userName );
        return userCopy != null && userCopy.get( "partState" ) != null;
    }

    @Override
    public boolean isTimedOut( String userName ) {
        synchronized ( getDao() ) {
            return timedOut.contains( userName );
        }
    }

    @Override
    public boolean isUnlocked( ModelObject mo ) {
        return !lockManager.isLocked( mo.getId() );
    }

    @Override
    public void keepAlive( String userName, int refreshDelay ) {
        final PlanCommunity planCommunity = getPlanCommunity();
        synchronized ( getDao() ) {
            for ( PresenceListener presenceListener : presenceListeners )
                presenceListener.keepAlive( userName, planCommunity, refreshDelay );
            processDeaths();
        }
    }

    private void processDeaths() {
        PlanCommunity planCommunity = getPlanCommunity();
        synchronized ( getDao() ) {
            Set<String> deads = new HashSet<String>();
            for ( PresenceListener presenceListener : presenceListeners )
                deads.addAll( presenceListener.giveMeYourDead( planCommunity ) );

            for ( String userName : deads )
                processDeath( userName );
        }
    }

    private void processDeath( String userName ) {
        LOG.info( "{} is done modeling", userName );
        whenLastActive.remove( userName );
        lockManager.release( userName );
    }

    @Override
    public Command makeRemoveFlowCommand( String userName, Flow flow ) {
        if ( flow.isSharing() )
            return new DisconnectFlow( userName, flow );
        else if ( flow.isNeed() )
            return new RemoveNeed( userName, flow );
        else if ( flow.isCapability() )
            return new RemoveCapability( userName, flow );

        throw new RuntimeException( "Can't remove unknown kind of flow" );
    }

    @Override
    public void processTimeOuts() {
        synchronized ( getDao() ) {
            long now = System.currentTimeMillis();
            long timeoutMillis = timeout * 1000L;
            if ( timeoutMillis < now - whenLastCheckedForTimeouts ) {
                for ( String userName : whenLastActive.keySet() ) {
                    long time = whenLastActive.get( userName );
                    if ( timeoutMillis < now - time && lockManager.release( userName ) )
                        timedOut.add( userName );
                }

                for ( String userName : timedOut )
                    whenLastActive.remove( userName );

                whenLastCheckedForTimeouts = now;
            }
        }
    }

    @Override
    public Change redo( String userName ) {
        synchronized ( getDao() ) {
            // Get memento of undoing command
            Memento memento = history.getRedo( userName );
            if ( memento == null )
                return Change.failed( "Nothing can be redone right now." );

            try {
                // undo the undoing
                Command redoCommand = memento.getCommand().getUndoCommand( this );
                LOG.info( "Redoing: {}", redoCommand.toString() );
                Change change = execute( redoCommand );
                change.setUndoing( true );
                history.recordRedone( memento, redoCommand );
                for ( CommandListener commandListener : commandListeners )
                    commandListener.commandRedone( this, redoCommand, change );

                return change;

            } catch ( CommandException e ) {
                return Change.failed( "Failed to redo" );
            }
        }
    }

    @Override
    public void releaseAllLocks( String userName ) {
        lockManager.release( userName );
    }

    @Override
    public boolean releaseAnyLockOn( String userName, Identifiable identifiable ) {
        return lockManager.requestRelease( userName, identifiable.getId() );
    }

    @Override
    public boolean releaseAnyLockOn( String userName, Long id ) {
        return lockManager.requestRelease( userName, id );
    }

    @Override
    public void replay( Journal journal ) throws CommandException {
        setReplaying( true );
        if ( !journal.isEmpty() )
            for ( JournalCommand command : journal.getCommands() ) {
                Change change = doCommand( (Command) command );
                if ( change.isFailed() )
                    throw new CommandException( "Command failed" );
            }
        journal.reset();
        reset();
    }

    @Override
    public boolean requestLockOn( String userName, Identifiable identifiable ) {
        if ( isTimedOut( userName ) || identifiable == null )
            return false;

        updateUserActive( userName );
        return lockManager.requestLock( userName, identifiable.getId() );
    }

    @Override
    public boolean requestLockOn( String userName, Long id ) {
        if ( isTimedOut( userName ) )
            return false;

        updateUserActive( userName );
        return lockManager.requestLock( userName, id );
    }

    @Override
    public void reset() {
        replaying = false;
        history.reset();
        lockManager.reset();
    }

    @Override
    public void resetUserHistory( String userName, boolean all ) {
        synchronized ( getDao() ) {
            history.resetForUser( userName, all );
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T extends ModelObject> T resolve( Class<T> clazz, Long id ) throws CommandException {
        try {
            return getCommunityService().find( clazz, id );
        } catch ( NotFoundException e ) {
            // If replaying a journal, recreate an entity if not found
            if ( replaying && ModelEntity.class.isAssignableFrom( clazz ) ) {
                LOG.warn( "Recreating not found entity " + clazz.getSimpleName() + "[" + id + "] on journal replay" );
                try {
                    return (T) getCommunityService().safeFindOrCreate( (Class<? extends ModelEntity>) clazz,
                                                                   "unknown",
                                                                   id );
                } catch ( Exception exc ) {
                    throw new CommandException( "Failed to create missing entity while replaying journal", e );
                }
            } else {
                throw new CommandException( "You need to refresh.", e );
            }
        }
    }

    @Override
    public void resynced( String userName ) {
        modelManager.resynced( userName );
    }

    @Override
    public void setCopy( String userName, Map<String, Object> state ) {
        copy.put( userName, state );
    }

    @Override
    public void setPlanResyncRequired() {
        modelManager.setResyncRequired( getPlan().getUri() );
    }

    @Override
    public Change undo( String userName ) {
        synchronized ( getDao() ) {
            Memento memento = history.getUndo( userName );
            if ( memento == null )
                return Change.failed( "Nothing can be undone right now." );

            try {
                Command undoCommand = memento.getCommand().getUndoCommand( this );
                if ( undoCommand instanceof MultiCommand )
                    LOG.info( "*** START multicommand ***" );
                LOG.info( "Undoing: " + undoCommand.toString() );
                Change change = execute( undoCommand );
                if ( undoCommand instanceof MultiCommand )
                    LOG.info( "*** END multicommand ***" );

                change.setUndoing( true );
                history.recordUndone( memento, undoCommand );
                for ( CommandListener commandListener : commandListeners )
                    commandListener.commandUndone( this, undoCommand, change );
                return change;
            } catch ( CommandException e ) {
                return Change.failed( "Could not undo" );
            }
        }
    }

    @Override
    public void updateUserActive( String userName ) {
        synchronized ( getDao() ) {
            whenLastActive.put( userName, System.currentTimeMillis() );
        }
    }

    @Override
    public void userLeftCommunity( String username ) {
        PlanCommunity planCommunity = getPlanCommunity();
        synchronized ( planCommunity ) {
            for ( PresenceListener presenceListener : presenceListeners )
                presenceListener.killIfAlive( username, planCommunity );
            processDeath( username );
        }
    }

    @Override
    public ImportExportFactory getImportExportFactory() {
        return importExportFactory;
    }

    void setImportExportFactory( ImportExportFactory importExportFactory ) {
        this.importExportFactory = importExportFactory;
    }

    @Override
    public AbstractModelObjectDao getDao() {
        return !getPlanCommunity().isModelCommunity()
                ? communityService.getDao()
                : communityService.getModelService().getDao();
    }


    public ModelManager getModelManager() {
        return modelManager;
    }

    void setModelManager( ModelManager modelManager ) {
        this.modelManager = modelManager;
    }

    @Override
    public QueryService getQueryService() {
        return communityService.getModelService();
    }

    public int getTimeout() {
        return timeout;
    }

    void setTimeout( int timeout ) {
        this.timeout = timeout;
    }

    @Override
    public boolean isReplaying() {
        return replaying;
    }

    @Override
    public void setReplaying( boolean replaying ) {
        this.replaying = replaying;
    }

    void setCommandListeners( List<CommandListener> commandListeners ) {
        this.commandListeners = commandListeners;
    }

    /**
     * Sets the commander's lock manager.
     *
     * @param lockManager a lock manager
     */
     void setLockManager( LockManager lockManager ) {
        this.lockManager = lockManager;
    }

    @Override
    public LockManager getLockManager() {
        return lockManager;
    }

    public void setPresenceListeners( List<PresenceListener> presenceListeners ) {
        this.presenceListeners = presenceListeners;
    }
}
