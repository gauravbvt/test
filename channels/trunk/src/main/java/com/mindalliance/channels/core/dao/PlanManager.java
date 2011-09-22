/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.dao;

import com.mindalliance.channels.core.PersistentObjectDaoFactory;
import com.mindalliance.channels.core.dao.PlanDefinition.Version;
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
public class PlanManager {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanManager.class );

    /** Manager for plan manager event listeners. */
    private final Listeners listeners = new Listeners();

    /** Plan persistence manager. */
    private final DefinitionManager definitionManager;
    /**
     * Persistent object database factory.
     */
    private PersistentObjectDaoFactory databaseFactory;
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

    private UserDao userDao;

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
    public PlanManager() {
        this( null );
    }

    public PlanManager( DefinitionManager definitionManager ) {
        this.definitionManager = definitionManager;
    }

    /**
     * Add a listener for plan manager events.
     * @param listener a listener
     */
    public void addListener( PlanListener listener ) {
        listeners.addListener( listener );
    }

    /**
     * Remove a listener for plan manager events.
     * @param listener a listener
     */
    public void removeListener( PlanListener listener ) {
        listeners.removeListener( listener );
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao( UserDao userDao ) {
        this.userDao = userDao;
    }

    public PersistentObjectDaoFactory getDatabaseFactory() {
        return databaseFactory;
    }

    public void setDatabaseFactory( PersistentObjectDaoFactory databaseFactory ) {
        this.databaseFactory = databaseFactory;
    }

    public ImportExportFactory getImportExportFactory() {
        return importExportFactory;
    }

    public void setImportExportFactory( ImportExportFactory importExportFactory ) {
        this.importExportFactory = importExportFactory;
    }

    public List<TransmissionMedium> getBuiltInMedia() {
        return builtInMedia;
    }

    public void setBuiltInMedia( List<TransmissionMedium> builtInMedia ) {
        this.builtInMedia = builtInMedia;
    }

    public String getDefaultSupportCommunity() {
        return defaultSupportCommunity;
    }

    public void setDefaultSupportCommunity( String defaultSupportCommunity ) {
        this.defaultSupportCommunity = defaultSupportCommunity;
    }

    public String getDefaultCommunityCalendar() {
        return defaultCommunityCalendar;
    }

    public void setDefaultCommunityCalendar( String defaultCommunityCalendar ) {
        this.defaultCommunityCalendar = defaultCommunityCalendar;
    }

    public String getDefaultCommunityCalendarHost() {
        return defaultCommunityCalendarHost;
    }

    public void setDefaultCommunityCalendarHost( String defaultCommunityCalendarHost ) {
        this.defaultCommunityCalendarHost = defaultCommunityCalendarHost;
    }

    public String getDefaultCommunityCalendarPrivateTicket() {
        return defaultCommunityCalendarPrivateTicket;
    }

    public void setDefaultCommunityCalendarPrivateTicket( String defaultCommunityCalendarPrivateTicket ) {
        this.defaultCommunityCalendarPrivateTicket = defaultCommunityCalendarPrivateTicket;
    }

    /**
     * Get version information from a plan.
     * @param plan the plan
     * @return the version information
     */
    public Version getVersion( Plan plan ) {
        return definitionManager.get( plan.getUri(), plan.isDevelopment() );
    }

    /**
     * Get the dao wrapper for a plan.
     *
     * @param plan the plan
     * @return the dao wrapper
     */
    public PlanDao getDao( Plan plan )  {
        return getDao( plan.getUri(), plan.isDevelopment() );
    }

    /**
     * Get the dao wrapper for a plan uri. Load plan if necessary.
     *
     * @param uri the plan's uri
     * @param development true for development version; false for production version
     * @return the dao wrapper or null if not found
     */
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

            createPersistentObjectDB( dao.getPlan() );
            listeners.fireLoaded( dao );
            return dao;

        } catch ( IOException e ) {
            LOG.error( "Unable to load plan " + version, e );
            return null;
        }
    }

    private void createPersistentObjectDB( Plan plan ) {
        String planUri = plan.getUri();
        databaseFactory.check( plan.getUrn() );
    }

    public DefinitionManager getDefinitionManager() {
        return definitionManager;
    }

    /**
     * Get all loaded plans managed, development and production.
     * @return an unmodifiable list
     */
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

    /**
     * Force a full save with journal flush.
     * @param plan the plan to flush
     */
    public void save( Plan plan ) {
        try {
            PlanDao dao = getDao( plan );
            dao.save( importExportFactory.createExporter( "daemon", dao ) );

        } catch ( IOException e ) {
            throw new RuntimeException( "Failed to save journal", e );
        }
    }

    /**
     * Assign default plans to users.
     */
    public synchronized void assignPlans() {

        // Assign default plan to users
        if ( userDao != null )
            for ( User user : userDao.getUsers() ) {
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

    /**
     * Find the plans, if any, with a given uri.
     *
     * @param uri a string
     * @return a list of plans
     */
    @SuppressWarnings( "unchecked" )
    public List<Plan> getPlansWithUri( final String uri ) {
        return (List<Plan>) CollectionUtils.select( getPlans(), new Predicate() {
            public boolean evaluate( Object object ) {
                return ( (Plan) object ).getUri().equals( uri );
            }
        } );
    }

    /**
     * Get a specific plan.
     * @param uri the plan uri
     * @param version the version
     * @return the plan or null if not found
     */
    public Plan getPlan( String uri, int version ) {

        PlanDefinition definition = definitionManager.get( uri );
        if ( definition != null ) {
            Version v = definition.get( version );
            if ( v != null )
                return getDao( uri, v.isDevelopment() ).getPlan();
        }

        return null;
    }

    /**
     * Import segment from browsed file.
     *
     *
     * @param userName who is doing the import
     * @param plan the plan to import into
     * @param inputStream where the segment lies
     * @return a segment, or null if not successful
     */
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

    /**
     * Remove all traces of a plan.
     * @param plan the soon to be ex-plan
     */
    public void delete( Plan plan ) {
        String uri = plan.getUri();

        for ( User user : userDao.getUsers() )
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

    /**
     * Make a plan in development version and make it a production version.
     * Retire previous production version of the plan if any, and
     * Create a new development version from a copy of the prior development version.
     * Substitute the new development plan as the current plan for each user where applicable.
     * Substitute the new production plan as the current plan for each user where applicable.
     *
     * @param oldDevPlan   a plan
     */
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

    /**
     * Find the production version of a plan.
     *
     * @param uri a string
     * @return a plan
     */
    public Plan findProductionPlan( String uri ) {
        PlanDao planDao = getDao( uri, false );
        return planDao == null ? null : planDao.getPlan();
    }

    /**
     * Find the development version of a plan.
     *
     * @param uri a string
     * @return a plan
     */
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

    /**
     * Add a producer to the plan.
     * Returns whether the plan was put into production as a result.
     *
     * @param producer     user name of planner voting to put plan in production
     * @param plan         a plan
     * @return a boolean
     */
    public boolean addProducer( String producer, Plan plan ) {
        if ( !plan.isDevelopment() )
            throw new IllegalStateException(
                    "Plan " + plan + " is not a development version" );

        plan.addProducer( producer );

        return revalidateProducers( plan );
    }

    /**
     * Check if all producers of a plan want to productize.
     * Productize the plan if so.
     * @param plan the plan
     * @return true if the plan was productized as a result
     */
    public boolean revalidateProducers( Plan plan ) {
        List<String> producers = plan.getProducers();
        for ( User user : userDao.getUsers() )
            if ( user.isPlanner( plan.getUri() ) && !producers.contains( user.getUsername() ) )
                return false;
// TODO reenable production voting
//        productize( plan );
        return true;
    }

    /**
     * Signal that the plan with given uri was versioned.
     *
     * @param uri the plan's uri
     */
    public synchronized void setResyncRequired( String uri ) {
        outOfSyncUsers.put( uri, userDao.getUsernames() );
    }

    /**
     * Signal that a user is now in sync with all plans versions.
     *
     * @param userName the user name
     */
    public synchronized void resynced( String userName ) {
        for ( Entry<String, List<String>> stringListEntry : outOfSyncUsers.entrySet() ) {
            List<String> usernames = stringListEntry.getValue();
            if ( usernames != null )
                usernames.remove( userName );
        }
    }

    /**
     * Whether a user is out of sync with a plan of given uri.
     *
     * @param userName the user name
     * @param uri  a string
     * @return a boolean
     */
    public synchronized boolean isOutOfSync( String userName, String uri ) {
        List<String> usernames = outOfSyncUsers.get( uri );
        return usernames != null && usernames.contains( userName );
    }

    /**
     * Return the list of plans the user has at least planner privileges to.
     *
     * @param user the user of concern
     * @return a list of plans
     */
    public List<Plan> getPlannablePlans( User user ) {
        List<Plan> planList = getPlans();
        List<Plan> result = new ArrayList<Plan>( planList.size() );

        for ( Plan p : planList )
            if ( user.isPlanner( p.getUri() ) )
                result.add( p );

        return result;
    }

    /**
     * Return the list of plans the user has at least user privileges to.
     *
     * @param user the user
     * @return a list of plans
     */
    public List<Plan> getReadablePlans( User user ) {
        List<Plan> planList = getPlans();
        List<Plan> result = new ArrayList<Plan>( planList.size() );

        for ( Plan object : planList )
            if ( user.isParticipant( object.getUri() ) )
                result.add( object );

        return result;
    }

    /**
     * Get a plan the uer can edit, else one the user can read, else the default plan.
     * Also loads said plan.
     * @param user a user
     * @return a plan, or null if none
     */
    public Plan getDefaultPlan( User user ) {
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

    /**
     * Set the permissions for a user.
     *
     * @param user the user
     * @param role either ROLE_ADMIN, ROLE_PLANNER, ROLE_USER or null for none
     * @param uri either a plan uri or null for all.
     */
    @Secured( "ROLE_ADMIN" )
    public void setAuthorities( User user, String role, String uri ) {
        synchronized ( user ) {
            user.getUserInfo().setAuthorities( role, uri, getPlans() );
            user.setPlan( uri != null ? getDefaultPlan( user ) : null );
        }
    }

    public File getVersionDirectory( Plan plan ) {
        Version version = getVersion( plan );
        return version.getVersionDirectory();
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
