package com.mindalliance.channels.core.command;

import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.command.commands.DisconnectFlow;
import com.mindalliance.channels.core.command.commands.RemoveCapability;
import com.mindalliance.channels.core.command.commands.RemoveNeed;
import com.mindalliance.channels.core.dao.Exporter;
import com.mindalliance.channels.core.dao.ImportExportFactory;
import com.mindalliance.channels.core.dao.Journal;
import com.mindalliance.channels.core.dao.JournalCommand;
import com.mindalliance.channels.core.dao.PlanDao;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.dao.UserService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Delay;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.nlp.SemanticMatcher;
import com.mindalliance.channels.engine.query.PlanService;
import com.mindalliance.channels.engine.query.QueryService;
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

    private UserService userService;

    private SemanticMatcher semanticMatcher;

    private AttachmentManager attachmentManager;

    /**
     * Record of when users were most recently active.
     */
    private Map<String, Long> whenLastActive = Collections.synchronizedMap( new HashMap<String, Long>() );

    /**
     * Users who timed out but have yet to be refreshed.
     */
    private Set<String> timedOut = Collections.synchronizedSet( new HashSet<String>() );

    /**
     * Default timeout period  in seconds = 5 minutes
     */
    private int timeout = 300;

    /**
     * A user's copied state of either a part, flow or attachment.
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

    /**
     * Create goal from mapped state.
     *
     * @param map          a map
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
        Organization org = isType
                ? queryService.findOrCreateType( Organization.class, (String) orgSpec.get( 0 ) )
                : queryService.findOrCreate( Organization.class, (String) orgSpec.get( 0 ) );
        goal.setOrganization( org );
        return goal;
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

    public SemanticMatcher getSemanticMatcher() {
        return semanticMatcher;
    }

    public void setSemanticMatcher( SemanticMatcher semanticMatcher ) {
        this.semanticMatcher = semanticMatcher;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService( UserService userService ) {
        this.userService = userService;
    }

    @Override
    public Map<String, Object> getCopy() {
        return copy.get( User.current().getUsername() );
    }

    @Override
    public void setCopy( Map<String, Object> state ) {
        copy.put( User.current().getUsername(), state );
    }

    @Override
    public boolean isPartCopied() {
        Map<String, Object> userCopy = getCopy();
        return userCopy != null && userCopy.get( "partState" ) != null;
    }

    @Override
    public boolean isFlowCopied() {
        Map<String, Object> userCopy = getCopy();
        return userCopy != null && userCopy.get( "isSend" ) != null;
    }

    @Override
    public boolean isAttachmentCopied() {
        Map<String, Object> userCopy = getCopy();
        return userCopy != null && userCopy.get( "url" ) != null && userCopy.get( "type" ) != null;
    }

    @Override
    public void setLockManager( LockManager lockManager ) {
        this.lockManager = lockManager;
    }

    public final QueryService getQueryService() {
        if ( queryService == null )
            queryService = getQueryService( User.current().getPlan() );   // TODO - don't use User.current()
        return queryService;
    }

    private PlanService getQueryService( Plan plan ) {
        return new PlanService( planManager, semanticMatcher, userService, plan, attachmentManager );
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout( int timeout ) {
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

    @Override
    @SuppressWarnings( "unchecked" )
    public <T extends ModelObject> T resolve( Class<T> clazz, Long id ) throws CommandException {
        try {
            return getQueryService().find( clazz, id );
        } catch ( NotFoundException e ) {
            // If replaying a journal, recreate an entity if not found
            if ( replaying && ModelEntity.class.isAssignableFrom( clazz ) ) {
                LOG.warn( "Recreating not found entity " + clazz.getSimpleName() + "[" + id + "] on journal replay" );
                try {
                    return (T) getQueryService().safeFindOrCreate( (Class<? extends ModelEntity>) clazz, "unknown", id );
                } catch ( Exception exc ) {
                    throw new CommandException( "Failed to create missing entity while replaying journal", e );
                }
            } else {
                throw new CommandException( "You need to refresh.", e );
            }
        }
    }


    @Override
    public void resetUserHistory( String userName, boolean all ) {
        synchronized ( getPlan() ) {
            history.resetForUser( userName, all );
        }
    }

    @Override
    public String getUndoTitle() {
        String title = "Undo";
        Memento memento = history.getUndo();
        if ( memento != null ) {
            title = title + " " + memento.getCommand().getName();
        }
        return title;
    }

    @Override
    public String getRedoTitle() {
        String title = "Redo";
        // memento of a command undoing another
        Memento memento = history.getRedo();
        if ( memento != null ) {
            title = title + " " + memento.getCommand().getUndoes( this );
        }
        return title;
    }

    @Override
    public boolean canDo( Command command ) {
        return getPlan().isDevelopment()
                && command.canDo( this )
                && lockManager.isLockableByUser( command.getUserName(), command.getLockingSet() );
    }

    private Change execute( Command command ) throws CommandException {
        Change change;
        String userName = command.getUserName();
        updateUserActive( userName );
        if ( command.isAuthorized() )
            try {
                Collection<Long> grabbedLocks = lockManager.lock( userName, command.getLockingSet() );
                change = command.execute( this );
                if ( change.isNone() )
                    LOG.info( "No change" );

                lockManager.release( userName, grabbedLocks );

            } catch ( LockingException e ) {
                throw new CommandException( e.getMessage(), e );
            }
        else {
            throw new CommandException( "Required locks not acquired" );
        }
        return change;
    }

    @Override
    public boolean canUndo() {
        synchronized ( getPlan() ) {
            if ( getPlan().isDevelopment() ) {
                Memento memento = history.getUndo();
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
    public boolean canRedo() {
        synchronized ( getPlan() ) {
            if ( getPlan().isDevelopment() ) {
                Memento memento = history.getRedo();
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
    public Change doCommand( Command command ) {
        synchronized ( getPlan() ) {
            return executeCommand( command, true );
        }
    }

    @Override
    public Change doUnsafeCommand( Command command ) {
        synchronized ( getPlan() ) {
                return executeCommand( command, false );
        }
    }

    private Change executeCommand( Command command, boolean safe ) {
        try {
            if ( safe && !getPlan().isDevelopment() )
                throw new CommandException( "This version is no longer in development. You need to refresh. " );

            if ( command instanceof MultiCommand )
                LOG.info( "*** START multicommand ***" );

            LOG.info( ( replaying ? "Replaying: " : "Doing: " ) + command.toString() );
            Change change = execute( command );
            if ( command instanceof MultiCommand )
                LOG.info( "*** END multicommand ***" );

            if ( !change.isNone() && !change.isFailed() ) {
                history.recordDone( command );
                afterExecution( command, change );
                if ( !replaying && command.isTop() ) {
                    for ( CommandListener commandListener : commandListeners )
                        commandListener.commandDone( command, change, planUrn() );
                }
            }

            return change;

        } catch ( CommandException e ) {
            LOG.warn( "Command failed " + command, e );
            return Change.failed( e.getMessage() );
        }
    }

    private String planUrn() {
        return getPlan().urn();
    }


    @Override
    public Change undo() {
        synchronized ( getPlan() ) {
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
                    commandListener.commandUndone( undoCommand, planUrn() );
                }
                return change;
            } catch ( CommandException e ) {
                return Change.failed( "Could not undo" );
            }
        }
    }

    @Override
    public Change redo() {
        synchronized ( getPlan() ) {
            // Get memento of undoing command
            Memento memento = history.getRedo();
            if ( memento == null )
                return Change.failed( "Nothing can be redone right now." );

            try {
                // undo the undoing
                Command redoCommand = memento.getCommand().getUndoCommand( this );
                LOG.info( "Redoing: {}", redoCommand.toString() );
                Change change = execute( redoCommand );
                change.setUndoing( true );
                history.recordRedone( memento, redoCommand );
                afterExecution( redoCommand, change );
                for ( CommandListener commandListener : commandListeners )
                    commandListener.commandRedone( redoCommand, planUrn() );

                return change;
            } catch ( CommandException e ) {
                return Change.failed( "Failed to redo" );
            }
        }
    }

    private void afterExecution( Command command, Change change ) {
        if ( !replaying && command.isTop() && !change.isNone() ) {
            LOG.debug( "***After command" );

            // TODO Implement proper observers/listeners
            planManager.onAfterCommand( getPlan(), command );
            analyst.onAfterCommand( getPlan() );
        }
    }

    @Override
    public void reset() {
        replaying = false;
        history.reset();
        lockManager.reset();
    }

    @Override
    public boolean cleanup( Class<? extends ModelObject> clazz, String name ) {
        synchronized ( getPlan() ) {
                return !( name == null || name.trim().isEmpty() )
                && getQueryService().cleanup( clazz, name );
        }
    }

    @Override
    public boolean isLockedByUser( Identifiable identifiable ) {
        return lockManager.isLockedByUser( User.current().getUsername(), identifiable.getId() );
    }

    @Override
    public boolean requestLockOn( Identifiable identifiable ) {
        if ( isTimedOut() || identifiable == null )
            return false;

        String userName = User.current().getUsername();
        updateUserActive( userName );
        return lockManager.requestLock( userName, identifiable.getId() );
    }

    @Override
    public boolean requestLockOn( Long id ) {
        if ( isTimedOut() )
            return false;

        String userName = User.current().getUsername();
        updateUserActive( userName );
        return lockManager.requestLock( userName, id );
    }

    @Override
    public boolean releaseAnyLockOn( Identifiable identifiable ) {
        return lockManager.requestRelease( User.current().getUsername(), identifiable.getId() );
    }

    @Override
    public boolean releaseAnyLockOn( Long id ) {
        return lockManager.requestRelease( User.current().getUsername(), id );
    }

    @Override
    public void releaseAllLocks( String userName ) {
        lockManager.release( userName );
    }

    @Override
    public long getLastModified() {
        return history.getLastModified();
    }

    @Override
    public String getLastModifier() {
        return history.getLastModifier();
    }

    private void updateUserActive( String userName ) {
        synchronized ( getPlan() ) {
                whenLastActive.put( userName, System.currentTimeMillis() );
        }
    }

    @Override
    public void keepAlive( String username, int refreshDelay ) {
        synchronized ( getPlan() ) {
            for ( PresenceListener presenceListener : presenceListeners ) {
                presenceListener.keepAlive( username, planUrn(), refreshDelay );
            }
            processDeaths();
        }
    }

    @Override
    public void updateUserActive() {
        updateUserActive( User.current().getUsername() );
    }

    private void processDeaths() {
        synchronized ( getPlan() ) {
            Set<String> deads = new HashSet<String>();
            for ( PresenceListener presenceListener : presenceListeners )
                deads.addAll( presenceListener.giveMeYourDead( planUrn() ) );

            for ( String userName : deads ) {
                processDeath(  userName );
            }
        }
    }

    private void processDeath( String userName ) {
        LOG.info( "{} is done planning", userName );
        whenLastActive.remove( userName );
        lockManager.release( userName );
    }

    @Override
    public void userLeftPlan( String username ) {
        synchronized ( getPlan() ) {
            for ( PresenceListener presenceListener : presenceListeners )
                presenceListener.killIfAlive( username, planUrn() );
            processDeath( username );
        }
    }

    @Override
    public void processTimeOuts() {
        synchronized ( getPlan() ) {
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
    public  boolean isTimedOut() {
        synchronized ( getPlan() ) {
                return timedOut.contains( User.current().getUsername() );
        }
    }

    @Override
    public void clearTimeOut() {
        synchronized ( getPlan() ) {
                timedOut.remove( User.current().getUsername() );
        }
    }

    @Override
    public boolean isUnlocked( ModelObject mo ) {
        return !lockManager.isLocked( mo.getId() );
    }

    @Override
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

    @Override
    public Plan getPlan() {
        return planDao.getPlan();
    }

    @Override
    public void setResyncRequired() {
        planManager.setResyncRequired( getPlan().getUri() );
    }

    @Override
    public void resynced() {
        planManager.resynced( User.current() );
    }

    @Override
    public boolean isOutOfSync() {
        return planManager.isOutOfSync( User.current(), getPlan().getUri() );
    }

    @Override
    public Exporter getExporter() {
        return importExportFactory.createExporter( planDao );
    }

    public PlanManager getPlanManager() {
        return planManager;
    }

    public void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
    }

    @Override
    public PlanDao getPlanDao() {
        return planDao;
    }

    @Override
    public void setPlanDao( PlanDao planDao ) {
        this.planDao = planDao;
    }

    @Override
    public boolean isLockable( String className ) {
        if ( className == null ) return false;
        try {
            Class clazz = Class.forName( className );
            return Identifiable.class.isAssignableFrom( clazz );
        } catch ( ClassNotFoundException e ) {
            throw new IllegalArgumentException( "Class not found", e );
        }
    }

    @Override
    public void initialize() {
        replayJournal();
        analyst.onStart( getPlan() );
    }

    @Override
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

    @Override
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
        QueryService queryService = getQueryService();
        for ( Map<String, Object> goalMap : goalStates ) {
            Goal goal = fromMap( goalMap, queryService );
            part.addGoal( goal );
        }
//        part.setGoals( new ArrayList<Goal>( (List<Goal>) state.get( "goals" ) ) );
        if ( state.get( "initiatedEvent" ) != null )
            part.setInitiatedEvent( queryService.findOrCreateType( Event.class,
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
            part.setOrganization( queryService.retrieveEntity( Organization.class, state, "organization" ) );
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

    @Override
    public Command makeRemoveFlowCommand( Flow flow ) {
        if ( flow.isSharing() )
            return new DisconnectFlow( flow );
        else if ( flow.isNeed() )
            return new RemoveNeed( flow );
        else if ( flow.isCapability() )
            return new RemoveCapability( flow );

        throw new RuntimeException( "Can't remove unknown kind of flow" );
    }

    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }
    public void setAttachmentManager( AttachmentManager attachmentManager ) {
        this.attachmentManager = attachmentManager;
    }
}
