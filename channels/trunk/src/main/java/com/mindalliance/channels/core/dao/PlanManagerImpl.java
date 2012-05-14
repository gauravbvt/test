/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.dao;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.dao.PlanDefinition.Version;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Persistent store for plans.
 */
public class PlanManagerImpl implements PlanManager {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanManagerImpl.class );

    /** Manager for plan manager event listeners. */
    private final Listeners listeners = new Listeners();

    /** Plan persistence manager. */
    private final DefinitionManager definitionManager;
    /**
     * All the plans, indexed by version uri (uri:version).
     */
    private final Map<Version, PlanDao> daoIndex =
            Collections.synchronizedMap(
                    new HashMap<Version, PlanDao>() );

    /**
     * For each plan uri, usernames of users who not in sync with its current version.
     * {uri => username}
     */
    private final Map<String, List<String>> outOfSyncUsers =
            Collections.synchronizedMap( new HashMap<String, List<String>>() );

    /**
     * Pre-defined and immutable transmission media.
     */
    private List<TransmissionMedium> builtInMedia = new ArrayList<TransmissionMedium>();

    private ChannelsUserDao userDao;

    private ImportExportFactory importExportFactory;
    /**
     * Name of the default support community.
     */
    private String defaultSupportCommunity;
    /**
     * URI of the default community calendar host.
     */
    private String defaultCommunityCalendarHost;
    /**
     * Name of the default community calendar.
     */
    private String defaultCommunityCalendar;
    /**
     * Name of the default community calendar private ticket.
     */
    private String defaultCommunityCalendarPrivateTicket;

    /**
     * Required for AOP decorations.
     */
    public PlanManagerImpl() {
        this( null );
    }

    public PlanManagerImpl( DefinitionManager definitionManager ) {
        this.definitionManager = definitionManager;
    }

    @Override
    public void addListener( PlanListener listener ) {
        listeners.addListener( listener );
    }

    @Override
    public void removeListener( PlanListener listener ) {
        listeners.removeListener( listener );
    }

    public ChannelsUserDao getUserDao() {
        return userDao;
    }

    public void setUserDao( ChannelsUserDao userDao ) {
        this.userDao = userDao;
    }

    public ImportExportFactory getImportExportFactory() {
        return importExportFactory;
    }

    public void setImportExportFactory( ImportExportFactory importExportFactory ) {
        this.importExportFactory = importExportFactory;
    }

    @Override
    public List<TransmissionMedium> getBuiltInMedia() {
        return builtInMedia;
    }

    public void setBuiltInMedia( List<TransmissionMedium> builtInMedia ) {
        this.builtInMedia = builtInMedia;
    }

    @Override
    public String getDefaultSupportCommunity() {
        return defaultSupportCommunity;
    }

    @Override
    public void setDefaultSupportCommunity( String defaultSupportCommunity ) {
        this.defaultSupportCommunity = defaultSupportCommunity;
    }

    @Override
    public String getDefaultCommunityCalendar() {
        return defaultCommunityCalendar;
    }

    @Override
    public void setDefaultCommunityCalendar( String defaultCommunityCalendar ) {
        this.defaultCommunityCalendar = defaultCommunityCalendar;
    }

    @Override
    public String getDefaultCommunityCalendarHost() {
        return defaultCommunityCalendarHost;
    }

    @Override
    public void setDefaultCommunityCalendarHost( String defaultCommunityCalendarHost ) {
        this.defaultCommunityCalendarHost = defaultCommunityCalendarHost;
    }

    @Override
    public String getDefaultCommunityCalendarPrivateTicket() {
        return defaultCommunityCalendarPrivateTicket;
    }

    @Override
    public void setDefaultCommunityCalendarPrivateTicket( String defaultCommunityCalendarPrivateTicket ) {
        this.defaultCommunityCalendarPrivateTicket = defaultCommunityCalendarPrivateTicket;
    }

    @Override
    public Version getVersion( Plan plan ) {
        return definitionManager.get( plan.getUri(), plan.isDevelopment() );
    }

    @Override
    public PlanDao getDao( Plan plan )  {
        return getDao( plan.getUri(), plan.isDevelopment() );
    }

    @Override
    public PlanDao getDao( String uri, boolean development ) {
        synchronized ( daoIndex ) {
            Version version = definitionManager.get( uri, development );
            if ( version == null )
                return null;

            PlanDao result = daoIndex.get( version );
            if ( result == null ) {
                result = createDao( version );
                daoIndex.put( version, result );
            }

            return result;
        }
    }

    private PlanDao createDao( Version version ) {
        try {
            PlanDao dao = new PlanDao( version );
            dao.setUserDetailsService( userDao );
            dao.setIdGenerator( definitionManager.getIdGenerator() );
            dao.resetPlan();
            dao.defineImmutableEntities( builtInMedia );
            if ( importExportFactory != null )
                dao.load( importExportFactory.createImporter( "daemon", dao ) );
            else
                dao.validate();
            listeners.fireLoaded( dao );
            return dao;

        } catch ( IOException e ) {
            LOG.error( "Unable to load plan " + version, e );
            return null;
        }
    }

    @Override
    public DefinitionManager getDefinitionManager() {
        return definitionManager;
    }

    @Override
    public List<Plan> getPlans() {
        List<Plan> result = new ArrayList<Plan>( daoIndex.size() );
        synchronized ( definitionManager ) {
            for ( PlanDefinition definition : definitionManager ) {
                String uri = definition.getUri();
                PlanDao devDao = getDao( uri, true );
                if ( devDao != null )
                    result.add( devDao.getPlan() );

                PlanDao prodDao = getDao( uri, false );
                if ( prodDao != null )
                    result.add( prodDao.getPlan() );
            }
        }

        Collections.sort( result );
        return Collections.unmodifiableList( result );
    }

    /**
     * Callback after a command was executed.
     *
     * @param plan         the plan
     * @param command      the command
     */
    public void onAfterCommand( Plan plan, JournalCommand command ) {
        // TODO implement proper listener/callback mechanism
        if ( command != null && command.isMemorable() )
            try {
                PlanDao dao = getDao( plan );
                Exporter exporter = importExportFactory.createExporter( "daemon", dao );
                synchronized ( dao ) {
                    Journal journal = dao.getJournal();
                    if ( command.forcesSnapshot()
                         || journal.size() >= getDefinitionManager().getSnapshotThreshold()
                         || plan.isProduction() )
                        dao.save( exporter );
                    else {
                        journal.addCommand( command );
                        dao.saveJournal( exporter );
                    }
                }

            } catch ( IOException e ) {
                throw new RuntimeException( "Failed to save journal", e );
            }
    }

    @Override
    public void save( Plan plan ) {
        try {
            PlanDao dao = getDao( plan );
            dao.save( importExportFactory.createExporter( "daemon", dao ) );

        } catch ( IOException e ) {
            throw new RuntimeException( "Failed to save journal", e );
        }
    }

    @Override
    public synchronized void assignPlans() {

        // Assign default plan to users
        if ( userDao != null )
            for ( ChannelsUser user : userDao.getUsers() ) {
                Plan plan = user.getPlan();
                if ( plan == null )
                    user.setPlan( getDefaultPlan( user ) );
                else {
                    String uri = plan.getUri();
                    if ( plan.isRetired() ) {
                        // User was connected to an old production plan
                        user.setPlan( findProductionPlan( uri ) );

                    } else if ( plan.isProduction() && user.isPlanner( uri ) )
                        // Plan was put in production
                        user.setPlan( findDevelopmentPlan( uri ) );
                }
            }

    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Plan> getPlansWithUri( final String uri ) {
        return (List<Plan>) CollectionUtils.select( getPlans(), new Predicate() {
            public boolean evaluate( Object object ) {
                return ( (Plan) object ).getUri().equals( uri );
            }
        } );
    }

    @Override
    public Plan getPlan( String uri, int version ) {

        PlanDefinition definition = definitionManager.get( uri );
        if ( definition != null ) {
            Version v = definition.get( version );
            if ( v != null )
                return getDao( uri, v.isDevelopment() ).getPlan();
        }

        return null;
    }

    @Override
    public Segment importSegment( String userName, Plan plan, InputStream inputStream ) {
        // Import and switch to segment
        LOG.debug( "Importing segment" );
        try {
            PlanDao dao = getDao( plan );

            Importer importer = importExportFactory.createImporter( userName, dao );
            Segment imported = importer.importSegment( inputStream );

            dao.save( importExportFactory.createExporter( userName, dao ) );
            LOG.info( "Imported segment {}", imported.getName() );
            return imported;

        } catch ( IOException e ) {
            // TODO redirect to a proper error screen... user has to know...
            String s = "Import error";
            LOG.error( s, e );
            throw new RuntimeException( s, e );
        }
    }

    @Override
    public void delete( Plan plan ) {
        String uri = plan.getUri();

        for ( ChannelsUser user : userDao.getUsers() )
            user.getUserInfo().clearAuthority( uri );

        assignPlans();

        synchronized ( daoIndex ) {
            Set<Entry<Version,PlanDao>> entries =
                    new HashSet<Entry<Version, PlanDao>>( daoIndex.entrySet() );
            for ( Entry<Version, PlanDao> entry : entries ) {
                Version version = entry.getKey();
                PlanDao dao = entry.getValue();
                if ( uri.equals( version.getPlanDefinition().getUri() ) ) {
                    listeners.fireAboutToUnload( dao );
                    daoIndex.remove( version );
                }
            }
        }

        definitionManager.delete( uri );
        LOG.info( "Deleted {}", plan );
    }

    @Override
    public void productize( Plan oldDevPlan ) {

        // Stop issue scanning
        listeners.fireAboutToProductize( oldDevPlan );

        // Make sure journal is flushed
        save( oldDevPlan );

        // Mark loaded production version of plan retired
        Plan oldProductionPlan = findProductionPlan( oldDevPlan.getUri() );
        if ( oldProductionPlan != null ) {
            oldProductionPlan.setRetired();
            oldProductionPlan.setWhenVersioned( new Date() );
            //daoIndex.remove( oldProductionPlan.getUri() );
        }

        // Create development plan from copy of old dev plan
        Plan newDevPlan = makeNewDevPlan( oldDevPlan );

        // Mark loaded development version of plan as production
        oldDevPlan.setProduction();
        oldDevPlan.setWhenVersioned( new Date() );

        assignPlans();

        // Restart issue scanning
        listeners.fireCreated( newDevPlan );
        listeners.fireProductized( oldDevPlan );

        LOG.info( "Productized {}", oldDevPlan );
    }

    @Override
    public Plan findProductionPlan( String uri ) {
        PlanDao planDao = getDao( uri, false );
        return planDao == null ? null : planDao.getPlan();
    }

    @Override
    public Plan findDevelopmentPlan( String uri ) {
        PlanDao planDao = getDao( uri, true );
        return planDao == null ? null : planDao.getPlan();
    }

    private Plan makeNewDevPlan( Plan oldDevPlan ) {
        try {
            // Create new persisted dev version
            Version oldVersion = getVersion( oldDevPlan );
            oldVersion.getPlanDefinition().productize();
            PlanDao newDao = getDao( oldDevPlan.getUri(), true );

            // TODO - remove initial data_???.xml backup file since it has producers set.
            return newDao.getPlan();

        } catch ( IOException e ) {
            throw new RuntimeException(
                    "Failed to make new development version of " + oldDevPlan, e );
        }
    }

    @Override
    public boolean addProducer( String producer, Plan plan ) {
        if ( !plan.isDevelopment() )
            throw new IllegalStateException(
                    "Plan " + plan + " is not a development version" );

        plan.addProducer( producer );

        return revalidateProducers( plan );
    }

    @Override
    public boolean revalidateProducers( Plan plan ) {
        List<String> producers = plan.getProducers();
        for ( ChannelsUser user : userDao.getUsers() )
            if ( user.isPlanner( plan.getUri() ) && !producers.contains( user.getUsername() ) )
                return false;
// TODO reenable production voting
//        productize( plan );
        return true;
    }

    @Override
    public synchronized void setResyncRequired( String uri ) {
        outOfSyncUsers.put( uri, userDao.getUsernames() );
    }

    @Override
    public synchronized void resynced( String userName ) {
        for ( Entry<String, List<String>> stringListEntry : outOfSyncUsers.entrySet() ) {
            List<String> usernames = stringListEntry.getValue();
            if ( usernames != null )
                usernames.remove( userName );
        }
    }

    @Override
    public synchronized boolean isOutOfSync( String userName, String uri ) {
        List<String> usernames = outOfSyncUsers.get( uri );
        return usernames != null && usernames.contains( userName );
    }

    @Override
    public List<Plan> getPlannablePlans( ChannelsUser user ) {
        List<Plan> planList = getPlans();
        List<Plan> result = new ArrayList<Plan>( planList.size() );

        for ( Plan p : planList )
            if ( user.isPlanner( p.getUri() ) )
                result.add( p );

        return result;
    }

    @Override
    public List<Plan> getReadablePlans( ChannelsUser user ) {
        List<Plan> planList = getPlans();
        List<Plan> result = new ArrayList<Plan>( planList.size() );

        for ( Plan object : planList )
            if ( user.isParticipant( object.getUri() ) )
                result.add( object );

        return result;
    }

    @Override
    public Plan getDefaultPlan( ChannelsUser user ) {
        for ( PlanDefinition planDefinition : definitionManager ) {
            String uri = planDefinition.getUri();
            if ( user.isPlanner( uri ) )
                return getDao( uri, true ).getPlan();
        }

        for ( PlanDefinition planDefinition : definitionManager ) {
            String uri = planDefinition.getUri();
            PlanDao dao = getDao( uri, false );
            if ( dao != null && user.isParticipant( uri ) )
                return dao.getPlan();
        }

        LOG.warn( "No default plan for user {}", user.getUsername() );
        return null;
    }

    @Override
    @Secured( "ROLE_ADMIN" )
    public void setAuthorities( ChannelsUser user, String role, String uri ) {
        synchronized ( user ) {
            user.getUserInfo().setAuthorities( role, uri, getPlans() );
            user.setPlan( uri != null ? getDefaultPlan( user ) : null );
        }
    }

    @Override
    public File getVersionDirectory( Plan plan ) {
        Version version = getVersion( plan );
        return version.getVersionDirectory();
    }

    @Override
    public List<String> getPlanUris() {
        List<String> uris = new ArrayList<String>(  );
        for ( Plan plan : getPlans() ) {
            uris.add(  plan.getUri() );
        }
        return uris;
    }

    @Override
    public void commandDone( Commander commander, Command command, Change change ) {
        if ( !commander.isReplaying() && command.isTop() && !change.isNone() )
            onAfterCommand( commander.getPlan(), command );
    }

    @Override
    public void commandUndone( Commander commander, Command command, Change change ) {
        commandDone( commander, command, change );
    }

    @Override
    public void commandRedone( Commander commander, Command command, Change change ) {
        commandDone( commander, command, change );
    }

    @Override
    public void started( Commander commander ) {
    }

    @Override
    public void clearCache() {
        // clearing done via aspect
    }



    /**
     * Listener event management.
     */
    private static final class Listeners {

        /** Whoever cares about plan manager events. */
        private final List<PlanListener> planListeners =
                Collections.synchronizedList( new ArrayList<PlanListener>() );

        private Listeners() {
        }

        public void addListener( PlanListener planListener ) {
            planListeners.add( planListener );
        }

        public void removeListener( PlanListener planListener ) {
            planListeners.remove( planListener );
        }

        public void fireAboutToProductize( Plan plan ) {
            synchronized ( planListeners ) {
                for ( PlanListener planListener : planListeners )
                    planListener.aboutToProductize( plan );
            }
        }

        public void fireAboutToUnload( PlanDao planDao ) {
            synchronized ( planListeners ) {
                for ( PlanListener planListener : planListeners )
                    planListener.aboutToUnload( planDao );
            }
        }

        public void fireProductized( Plan plan ) {
            synchronized ( planListeners ) {
                for ( PlanListener planListener : planListeners )
                    planListener.productized( plan );
            }
        }

        public void fireCreated( Plan plan ) {
            synchronized ( planListeners ) {
                for ( PlanListener planListener : planListeners )
                    planListener.created( plan );
            }
        }

        public void fireLoaded( PlanDao planDao ) {
            synchronized ( planListeners ) {
                for ( PlanListener planListener : planListeners )
                    planListener.loaded( planDao );
            }
        }
    }
}
