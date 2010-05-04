package com.mindalliance.channels.dao;

import com.mindalliance.channels.analysis.IssueScanner;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.TransmissionMedium;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.annotation.Secured;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Persistent store for plans.
 */
public class PlanManager {

    /**
     * Default uri.
     */
    private static final String DEFAULT_URI = "DEFAULT";

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanManager.class );

    /**
     * Base for relative user definitions.
     */
    private String base = System.getProperty( "user.home" );

    private Listeners listeners = new Listeners();

    /**
     * Name of the plans property file.
     */
    private String planDefinitionsFileName;

    /**
     * Plans property file.
     */
    private Resource defaultPlanDefinitions;

    /**
     * Plans properties file.
     */
    private File plansFile;

    /**
     * All the plans, indexed by version uri (uri:version).
     */
    private final Map<String, PlanDao> planIndex =
            Collections.synchronizedMap( new HashMap<String, PlanDao>() );

    /**
     * For each plan uri, usernames of users who not in sync with its current version.
     * {uri => username}
     */
    private final Map<String, List<String>> outOfSyncUsers =
            Collections.synchronizedMap( new HashMap<String, List<String>>() );

    /**
     * The plan importer/exporter factory.
     */
    private ImportExportFactory importExportFactory;

    /**
     * The thing that generates unique ids.
     */
    private IdGenerator idGenerator;

    /**
     * Where to import initial segments from. Don't if null...
     */
    private Resource importDirectory;

    /**
     * Surveys file name.
     */
    private String surveysFileName;

    /**
     * Uploads dir name.
     */
    private String uploadsDirName;

    /**
     * The location of the persisted data.
     * Default: ./channels-data
     */
    private Resource dataDirectory =
            new FileSystemResource( new File( System.getProperty( "java.io.tmpdir" ),
                                              "channels-data" ) );

    /**
     * Number of commands journaled before a snapshot is taken on next command.
     * Default: 10
     */
    private int snapshotThreshold = 10;

    /**
     * Pre-defined and immutable transmission media.
     */
    private List<TransmissionMedium> builtInMedia = new ArrayList<TransmissionMedium>();

    private UserService userService;

    //------------------------------------------
    /**
     * Required for AOP decorations.
     */
    public PlanManager() {
    }

    public PlanManager( ImportExportFactory importExportFactory, IdGenerator idGenerator ) {
        this.importExportFactory = importExportFactory;
        this.idGenerator = idGenerator;
    }

    public void addListener( Listener listener ) {
        listeners.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.removeListener( listener );
    }

    public void setPlanDefinitionsFileName( String fileName ) {
        planDefinitionsFileName = fileName;
        plansFile = fileName == null ? null : new File( base, fileName );
    }

    public String getPlanDefinitionsFileName() {
        return planDefinitionsFileName;
    }

    public void setDefaultPlanDefinitions( Resource defaultPlanDefinitions ) {
        this.defaultPlanDefinitions = defaultPlanDefinitions;
    }

    public Resource getImportDirectory() {
        return importDirectory;
    }

    public void setImportDirectory( Resource importDirectory ) {
        this.importDirectory = importDirectory;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService( UserService userService ) {
        this.userService = userService;
    }

    public synchronized String getBase() {
        return base;
    }

    public synchronized void setBase( String base ) {
        this.base = base;
    }

    public void setSurveysFileName( String surveysFileName ) {
        this.surveysFileName = surveysFileName;
    }

    public void setUploadsDirName( String uploadsDirName ) {
        this.uploadsDirName = uploadsDirName;
    }

    public List<TransmissionMedium> getBuiltInMedia() {
        return builtInMedia;
    }

    public void setBuiltInMedia( List<TransmissionMedium> builtInMedia ) {
        this.builtInMedia = builtInMedia;
    }

    /**
     * Get the dao wrapper for a plan.
     *
     * @param plan the plan
     * @return the dao wrapper
     * @throws NotFoundException if plan does not exist
     */
    public PlanDao getDao( Plan plan ) throws NotFoundException {
        return getDao( plan.getVersionUri() );
    }

    /**
     * Get the dao wrapper for a plan.
     *
     * @param uri the plan's uri
     * @return the dao wrapper
     * @throws NotFoundException if plan does not exist
     */
    public PlanDao getDao( String uri ) throws NotFoundException {
        PlanDao result = planIndex.get( uri );
        if ( result == null )
            throw new NotFoundException();
        return result;
    }

    private Map<String, List<String>> getPlanDefinitions() {
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        InputStream inputStream = null;
        try {
            inputStream = findInputStream();
            Properties properties = new Properties();
            properties.load( inputStream );
            for ( String uri : properties.stringPropertyNames() ) {
                String values = properties.getProperty( uri );
                List<String> propVals = new ArrayList<String>();
                StringTokenizer tokens = new StringTokenizer( values, "|" );
                while ( tokens.hasMoreTokens() )
                    propVals.add( tokens.nextToken() );

                assert propVals.size() == 2;
                result.put( uri, propVals );
            }

        } catch ( IOException e ) {
            LOG.error( "Unable to read plan properties", e );

        } finally {
            if ( inputStream != null )
                try {
                    inputStream.close();
                } catch ( IOException e ) {
                    LOG.error( "Unable to close plan properties file", e );
                }
        }

        return result;
    }

    private InputStream findInputStream() throws IOException {
        InputStream inputStream;
        if ( plansFile != null && plansFile.exists() ) {
            LOG.debug( "Reading plan definitions from {}", plansFile.getAbsolutePath() );
            inputStream = new FileInputStream( plansFile );
        } else if ( defaultPlanDefinitions != null && defaultPlanDefinitions.exists() ) {
            LOG.debug( "Reading default plan definitions from {}",
                       defaultPlanDefinitions.getURI() );
            inputStream = defaultPlanDefinitions.getInputStream();
        } else {
            LOG.warn( "No user readable plan definitions" );
            inputStream = new ByteArrayInputStream( new byte[0] );
        }
        return inputStream;
    }

    /**
     * Write the user definition in memory to the disk storage.
     *
     * @throws IOException on write errors
     */
    private synchronized void writePlanDefinitions() throws IOException {
        Properties props = new Properties();
        for ( Plan plan : getPlans() ) {
            props.setProperty( plan.getUri(), plan.getName() + '|' + plan.getClient() );
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream( plansFile );
            props.store( stream, " Active plans" );
        } finally {
            if ( stream != null )
                stream.close();
        }
        LOG.debug( "Wrote plan definitions to {}", plansFile.getAbsolutePath() );
    }

    /**
     * Get all plans managed.
     *
     * @return an unmodifiable list
     */
    public List<Plan> getPlans() {
        synchronized ( planIndex ) {
            List<Plan> result = new ArrayList<Plan>( planIndex.size() );
            for ( PlanDao dao : planIndex.values() )
                result.add( dao.getPlan() );

            Collections.sort( result );
            return Collections.unmodifiableList( result );
        }
    }

    /**
     * Get all plan names.
     * @return names of all plans
     */
    public List<String> getPlanNames() {
        List<Plan> planList = getPlans();
        List<String> answer = new ArrayList<String>( planList.size() );
        for ( Plan plan : planList )
            answer.add( plan.getName() );

        return answer;
    }

    /**
     * Tests if a plan name is already taken.
     *
     * @param name a string
     * @return a boolean
     */
    public boolean isPlanNameTaken( String name ) {
        synchronized ( planIndex ) {
            for ( PlanDao object : planIndex.values() )
                if ( name.equals( object.getPlan().getName() ) )
                    return true;

            return false;
        }
    }

    public ImportExportFactory getImportExportFactory() {
        return importExportFactory;
    }

    public void setImportExportFactory( ImportExportFactory importExportFactory ) {
        this.importExportFactory = importExportFactory;
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator( IdGenerator idGenerator ) {
        this.idGenerator = idGenerator;
    }

    public Resource getDataDirectory() {
        File baseDirectory;
        try {
            baseDirectory = dataDirectory.getFile();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        if ( !baseDirectory.exists() )
            baseDirectory.mkdir();
        return dataDirectory;
    }

    public void setDataDirectory( Resource dataDirectory ) {
        this.dataDirectory = dataDirectory;
    }

    public int getSnapshotThreshold() {
        return snapshotThreshold;
    }

    public void setSnapshotThreshold( int snapshotThreshold ) {
        this.snapshotThreshold = snapshotThreshold;
    }

    /**
     * Callback after a command was executed.
     *
     * @param plan         the plan
     * @param command      the command
     */
    public void onAfterCommand( Plan plan, Command command ) {
        // TODO implement proper listener/callback mechanism
        if ( command.isMemorable() )
            try {
                PlanDao dao = getDao( plan );
                Exporter exporter = importExportFactory.createExporter( dao );
                synchronized ( dao ) {
                    Journal journal = dao.getJournal();
                    if ( command.forcesSnapshot() || journal.size() >= dao.getSnapshotThreshold() )
                        dao.save( exporter );
                    else {
                        journal.addCommand( command );
                        dao.saveJournal( exporter );
                    }
                }

            } catch ( IOException e ) {
                throw new RuntimeException( "Failed to save journal", e );
            } catch ( NotFoundException e ) {
                throw new RuntimeException( "Failed to save journal", e );
            }
    }

    /**
     * Get current plan from current thread.
     *
     * @return a plan
     */
    public static Plan plan() {
        if ( Thread.currentThread() instanceof IssueScanner.Daemon ) {
            return ( (IssueScanner.Daemon) Thread.currentThread() ).getPlan();
        } else {
            User user = User.current();
            return user == null ? null : user.getPlan();
        }
    }

    /**
     * Create a new plan and index it.
     * @return the new plan dao
     */
    public PlanDao createPlan() {
        PlanDao dao = createDao( createPlan( DEFAULT_URI, 1, Plan.Status.DEVELOPMENT ) );
        dao.validate();
        registerPlanDao( dao );
        listeners.fireCreated( dao.getPlan() );
        return dao;
    }

    private Plan createPlan( String uri, int version, Plan.Status status ) {
        Plan plan = new Plan();

        plan.setId( idGenerator.assignId( null, plan ) );
        plan.setName( "unnamed" );
        plan.setUri( uri );
        plan.setVersion( version );
        plan.setStatus( status );

        return plan;
    }

    private PlanDao createDao( Plan plan ) {
        try {
            PlanDao dao = new PlanDao( plan, getDataDirectory().getFile(), idGenerator );
            dao.setSnapshotThreshold( snapshotThreshold );
            dao.setUserDetailsService( userService );
            dao.defineImmutableEntities( builtInMedia );
            dao.add( plan, plan.getId() );

            return dao;
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Load or create defined plans.
     * Create default plan if none defined.
     * Called by DefaultQueryService after properties set.
     */
    public synchronized void validate() {
        createDataDir();


        // Load persisted, defined plans
        for ( PlanDao dao : getDaos() ) {
            if ( dao.isPersisted() )
                dao.importPlan( importExportFactory, getBuiltInMedia() );

            registerPlanDao( dao );
            dao.validate();
            listeners.fireCreated( dao.getPlan() );
        }

        if ( plansFile != null && !plansFile.exists() ) {
            // Save plan description file, if none exists
            try {
                writePlanDefinitions();
            } catch ( IOException e ) {
                throw new RuntimeException( "Failed to write plan definitions", e );
            }
        }

        // Assign default plan to users
        if ( userService != null )
            for ( User user : userService.getUsers() )
                user.setPlan( getDefaultPlan( user ) );

    }

    /**
     * Create DAOs implied by the definition file.
     * @return list of versioned daos, not loaded or imported.
     */
    private List<PlanDao> getDaos() {
        List<PlanDao> daos = new ArrayList<PlanDao>();

        for ( Map.Entry<String, List<String>> entry : getPlanDefinitions().entrySet() ) {
            String uri = entry.getKey();
            List<String> properties = entry.getValue();
            int lastVersion = getDevelopmentVersion( uri );

            for ( int version = lastVersion; version > 0; version-- ) {
                Plan plan = createPlan( uri, version,
                                        version == lastVersion     ? Plan.Status.DEVELOPMENT
                                      : version == lastVersion - 1 ? Plan.Status.PRODUCTION
                                                                   : Plan.Status.RETIRED );
                plan.setName( properties.get( 0 ) );
                plan.setClient( properties.get( 1 ) );
                daos.add( createDao( plan ) );
            }
        }

        // Make sure there is at least one active (if bogus) plan
        if ( daos.isEmpty() )
            daos.add( createDao( createPlan( DEFAULT_URI, 1, Plan.Status.DEVELOPMENT ) ) );

        return daos;
    }

    private void createDataDir() {
        try {
            String path = dataDirectory.getFile().getAbsolutePath();
            new File( path ).mkdirs();
            LOG.info( "Data will be saved in {}", path );
        } catch ( IOException e ) {
            LOG.error( "Unable to get reference to data directory", e );
            throw new IllegalStateException( e );
        }
    }

    private void registerPlanDao( PlanDao dao ) {
        String key = dao.getPlan().getVersionUri();
        if ( planIndex.get( key ) != null ) {
            LOG.error( "Duplicate plan URI {}", key );
            throw new DuplicateKeyException();
        }
        planIndex.put( key, dao );
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
            public boolean evaluate( Object obj ) {
                return ( (Plan) obj ).getUri().equals( uri );
            }
        } );
    }

    /**
     * Import segment from browsed file.
     *
     * @param plan
     * @param inputStream where the segment lies
     * @return a segment, or null if not successful
     */
    public Segment importSegment( Plan plan, InputStream inputStream ) {
        Segment imported = null;

        // Import and switch to segment
        LOG.debug( "Importing segment" );
        try {
            PlanDao planDao = getDao( plan );
            Importer importer = importExportFactory.createImporter( planDao );
            imported = importer.importSegment( inputStream );
            LOG.info( "Imported segment {}", imported.getName() );

            planDao.save( importExportFactory.createExporter( planDao ) );

        } catch ( Exception e ) {
            // TODO redirect to a proper error screen... user has to know...
            String s = "Import error";
            LOG.error( s, e );
            throw new RuntimeException( s, e );
        }

        return imported;
    }

    public String getPlanVersionDirectory( Plan plan ) {
        return getPlanDirectory( plan.getUri() ) + File.separator + plan.getVersion();
    }

    private String getPlanDirectory( String uri ) {
        try {
            return getDataDirectory().getFile().getAbsolutePath() + File.separator
                   + PlanDao.sanitize( uri );
        } catch ( IOException e ) {
            throw new RuntimeException( "Failed to get plan directory", e );
        }
    }

    private int getDevelopmentVersion( String uri ) {
        int maxVersion = 1;
        File planDir = new File( getPlanDirectory( uri ) );
        File[] subDirs = planDir.listFiles( new FileFilter() {
            public boolean accept( File pathname ) {
                return pathname.isDirectory();
            }
        } );
        if ( subDirs != null ) {
            for ( File dir : subDirs ) {
                try {
                    int version = Integer.parseInt( dir.getName() );
                    maxVersion = Math.max( maxVersion, version );
                } catch ( NumberFormatException ignored ) {
                    // Do nothing
                }
            }
        }
        // else version dirs to be created
        return maxVersion;
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
    public void moveToProduction( Plan oldDevPlan ) {
        if ( !oldDevPlan.isDevelopment() )
            throw new IllegalStateException(
                    "Plan " + oldDevPlan + " is not a development version" );

        // Stop issue scanning
        listeners.fireAboutToProductize( oldDevPlan );

        // Create development plan from copy of old dev plan
        Plan newDevPlan = makeNewDevPlan( oldDevPlan );
        // Substitute current plans of users as appropriate
        for ( User user : userService.getUsers() ) {
            Plan userPlan = user.getPlan();
            if ( userPlan != null && userPlan.getUri().equals( oldDevPlan.getUri() ) ) {
                if ( userPlan.isDevelopment() ) {
                    user.setPlan( newDevPlan );
                } else {
                    if ( userPlan.isProduction() ) {
                        user.setPlan( oldDevPlan );
                    }
                }
            }
        }
        // Mark loaded production version of plan retired
        Plan oldProductionPlan = findProductionPlan( oldDevPlan.getUri() );
        if ( oldProductionPlan != null ) {
            oldProductionPlan.setRetired();
            oldProductionPlan.setWhenVersioned( new Date() );
            planIndex.remove( oldProductionPlan.getVersionUri() );
        }
        // Mark loaded development version of plan as production
        oldDevPlan.setProduction();
        oldDevPlan.setWhenVersioned( new Date() );

        // Restart issue scanning
        listeners.fireCreated( newDevPlan );
    }

    /**
     * Find the production version of a plan.
     *
     * @param uri a string
     * @return a plan
     */
    public Plan findProductionPlan( String uri ) {
        return (Plan) CollectionUtils.find( getPlanVersions( uri ),
                                            PredicateUtils.invokerPredicate( "isProduction" ) );
    }

    /**
     * Find the development version of a plan.
     *
     * @param uri a string
     * @return a plan
     */
    public Plan findDevelopmentPlan( String uri ) {
        return (Plan) CollectionUtils.find( getPlanVersions( uri ),
                                            PredicateUtils.invokerPredicate( "isDevelopment" ) );
    }

    @SuppressWarnings( "unchecked" )
    private List<Plan> getPlanVersions( final String uri ) {
        return (List<Plan>) CollectionUtils.select( getPlans(), new Predicate() {
            public boolean evaluate( Object object ) {
                return ( (Plan) object ).getUri().equals( uri );
            }
        } );
    }

    private void makeNewVersionDirectory( Plan plan ) throws IOException {
        // Create new persisted dev version
        int version = plan.getVersion();
        String newDevVersionDirName =
                getPlanDirectory( plan.getUri() ) + File.separator + ( version + 1 );
        File newVersionDir = new File( newDevVersionDirName );
        assert !newVersionDir.exists();
        newVersionDir.mkdir();
        File oldVersionDir = new File( getPlanVersionDirectory( plan ) );
        // Copy files from old to new
        FileUtils.copyFileToDirectory( new File( oldVersionDir, PlanDao.DATA_FILE ),
                                       newVersionDir );
        FileUtils.copyFileToDirectory( new File( oldVersionDir, surveysFileName ), newVersionDir );
        FileUtils.copyDirectoryToDirectory( new File( oldVersionDir, uploadsDirName ),
                                            newVersionDir );
    }

    private Plan makeNewDevPlan( Plan oldDevPlan ) {
        try {
            // Create new persisted dev version
            makeNewVersionDirectory( oldDevPlan );
            Plan newDevPlan = new Plan();
            newDevPlan.setUri( oldDevPlan.getUri() );
            newDevPlan.setVersion( oldDevPlan.getVersion() + 1 );
            newDevPlan.setDevelopment();
            newDevPlan.setWhenVersioned( new Date() );
            newDevPlan.setName( oldDevPlan.getName() );
            newDevPlan.setClient( oldDevPlan.getClient() );
            PlanDao dao = createDao( newDevPlan );
            dao.importPlan( importExportFactory, getBuiltInMedia() );
            registerPlanDao( dao );
            newDevPlan.removeAllProducers();
            dao.save( importExportFactory.createExporter( dao ) );
            // TODO - remove initial data_???.xml backup file since it has producers set.
            return newDevPlan;

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
    public boolean addProducer( String producer, final Plan plan ) {
        plan.addProducer( producer );
        final List<String> producers = plan.getProducers();
        boolean unanimous = !CollectionUtils.exists( userService.getUsers(), new Predicate() {
            public boolean evaluate( Object object ) {
                User user = (User) object;
                return user.isPlanner( plan.getUri() ) && !producers.contains( user.getUsername() );
            }
        } );
        if ( unanimous ) {
            moveToProduction( plan );
        }
        return unanimous;
    }

    /**
     * Signal that the plan with given uri was versioned.
     *
     * @param uri the plan's uri
     */
    public synchronized void setResyncRequired( String uri ) {
        outOfSyncUsers.put( uri, userService.getUsernames() );
    }

    /**
     * Signal that a user is now in sync with all plans versions.
     *
     * @param user a user
     */
    public synchronized void resynced( User user ) {
        for ( String uri : outOfSyncUsers.keySet() ) {
            List<String> usernames = outOfSyncUsers.get( uri );
            if ( usernames != null )
                usernames.remove( user.getUsername() );
        }
    }

    /**
     * Whether a user is out of sync with a plan of given uri.
     *
     * @param user a user
     * @param uri  a string
     * @return a boolean
     */
    public synchronized boolean isOutOfSync( User user, String uri ) {
        List<String> usernames = outOfSyncUsers.get( uri );
        return usernames != null && usernames.contains( user.getUsername() );
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
     * @param user a user
     * @return a plan, or null if none
     */
    public Plan getDefaultPlan( User user ) {
        List<Plan> planList = getPlans();
        for ( Plan plan : planList )
            if ( plan.isDevelopment() && user.isPlanner( plan.getUri() ) )
                return plan;

        for ( Plan plan : planList )
            if ( plan.isProduction() && user.isParticipant( plan.getUri() ) )
                return plan;

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
        user.getUserInfo().setAuthorities( role, uri, getPlans() );
        user.setPlan( uri != null ? getDefaultPlan( user ) : null );
        try {
            userService.save();
        } catch ( IOException e ) {
            LOG.error( "Unable to save user definitions", e );
        }
    }

    //=============================================================
    /**
     * A listener to important plan management business.
     */
    public interface Listener {

        /**
         * A plan is about to be put in production.
         * @param devPlan the development plan
         */
        void aboutToProductize( Plan devPlan );

        /**
         * A new plan was put in production.
         * @param plan the new plan
         */
        void productized( Plan plan );

        /**
         * A new development plan was created.
         * @param devPlan the new plan.
         */
        void created( Plan devPlan );
    }

    //=============================================================
    /**
     * Listener event management.
     */
    private static final class Listeners {

        /** Whoever cares about plan manager events. */
        private final List<Listener> listeners =
                Collections.synchronizedList( new ArrayList<Listener>() );

        private Listeners() {
        }

        public void addListener( Listener listener ) {
            listeners.add( listener );
        }

        public void removeListener( Listener listener ) {
            listeners.remove( listener );
        }

        public void fireAboutToProductize( Plan plan ) {
            synchronized ( listeners ) {
                for ( Listener listener : listeners )
                    listener.aboutToProductize( plan );
            }
        }

        public void fireProductized( Plan plan ) {
            synchronized ( listeners ) {
                for ( Listener listener : listeners )
                    listener.productized( plan );
            }
        }

        public void fireCreated( Plan plan ) {
            synchronized ( listeners ) {
                for ( Listener listener : listeners )
                    listener.created( plan );
            }
        }
    }
}
