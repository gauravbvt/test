package com.mindalliance.channels.dao;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.DuplicateKeyException;
import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.Importer;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.analysis.IssueScanner;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.export.ImportExportFactory;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.model.User;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataRetrievalFailureException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Persistent store for plans.
 */
public class PlanManager implements InitializingBean {

    /**
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( getClass() );
    /**
     * Base for relative user definitions.
     */
    private String base = System.getProperty( "user.home" );

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
     * Definitions parsed from plans properties file.
     * uri => [name, client]
     */
    private Map<String, List<String>> planDefinitions = new HashMap<String, List<String>>();
    /**
     * All the plans, indexed by version uri (uri:version).
     */
    private final Map<String, PlanDao> planIndex =
            Collections.synchronizedMap( new HashMap<String, PlanDao>() );
    /**
     * User participations in plans.
     * plan uri => participation
     */
    private Set<User> users = new HashSet<User>();

    /**
     * For each plan uri, usernames of users who not in sync with its current version.
     * {uri => username}
     */
    private Map<String, List<String>> outOfSyncUsers =
            Collections.synchronizedMap( new HashMap<String, List<String>>() );

    /**
     * The plan importer.
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
     * The default current plan dao -- used only during loading.
     */
    private PlanDao currentDao;
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
    private Resource dataDirectory = new FileSystemResource(
            new File( System.getProperty( "user.dir" ), "channels-data" ) );

    /**
     * Number of commands journaled before a snapshot is taken on next command.
     * Default: 10
     */
    private int snapshotThreshold = 10;
    /**
     * Default uri.
     */
    public static final String DEFAULT_URI = "DEFAULT";
    /**
     * Lowest id for mutable model objects.
     */
    public static final long IMMUTABLE_RANGE = -1000L;
    /**
     * Pre-defined and immutable transmission media.
     */
    private List<TransmissionMedium> builtInMedia = new ArrayList<TransmissionMedium>();

    /**
     * Required for AOP decorations.
     */
    public PlanManager() {
    }

    public PlanManager( ImportExportFactory importExportFactory, IdGenerator idGenerator ) {
        this.importExportFactory = importExportFactory;
        this.idGenerator = idGenerator;
    }

    public void setPlanDefinitionsFileName( String planDefinitionsFileName ) {
        this.planDefinitionsFileName = planDefinitionsFileName;
        plansFile = planDefinitionsFileName == null ? null : new File( base, planDefinitionsFileName );
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
        PlanDao result = planIndex.get( plan.getVersionUri() );
        if ( result == null ) {
            if ( currentDao != null && plan == currentDao.getPlan() ) {
                result = currentDao;
            } else {
                throw new NotFoundException();
            }
        }
        return result;
    }

    private void loadPlanDefinitions() {
        try {
            readPlanDefinitions();
        } catch ( IOException e ) {
            String msg = "Unable to load plan definitions";
            LOG.error( msg, e );
            throw new DataRetrievalFailureException( msg, e );
        }
    }

    // e.g. mindalliance.com/channels/plans/acme=ACME Emergency Communication Plan|ACME Manufacturing, Inc.
    private void readPlanDefinitions() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = findInputStream();
            properties.load( inputStream );
            for ( String uri : properties.stringPropertyNames() ) {
                String values = properties.getProperty( uri );
                StringTokenizer tokens = new StringTokenizer( values, "|" );
                List<String> propVals = new ArrayList<String>();
                while ( tokens.hasMoreTokens() ) {
                    propVals.add( tokens.nextToken() );
                }
                assert propVals.size() == 2;
                planDefinitions.put( uri, propVals );
            }
        } finally {
            if ( inputStream != null )
                inputStream.close();
        }
    }

    private InputStream findInputStream() throws IOException {
        InputStream inputStream;
        if ( plansFile != null && plansFile.exists() ) {
            LOG.debug( "Reading user definitions from {}", plansFile.getAbsolutePath() );
            inputStream = new FileInputStream( plansFile );
        } else if ( defaultPlanDefinitions != null && defaultPlanDefinitions.exists() ) {
            LOG.debug( "Reading default plan definitions from {}", defaultPlanDefinitions.getURI() );
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
    public synchronized void writePlanDefinitions() throws IOException {
        Properties props = new Properties();
        for ( Plan plan : getPlans() ) {
            props.setProperty(
                    plan.getUri(),
                    plan.getName() + "|" + plan.getClient()
            );
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
            for ( PlanDao dao : planIndex.values() ) {
                result.add( dao.getPlan() );
            }
            Collections.sort( result );
            return Collections.unmodifiableList( result );
        }
    }

    /**
     * Return the number of plans manager by this instance.
     *
     * @return the plan count
     */
    public int getPlanCount() {
        return planIndex.size();
    }

    /**
     * Add a plan.
     *
     * @param plan the plan
     */
    public void add( Plan plan ) {
        registerPlanDao( new PlanDao( this, plan ) );
    }

    /**
     * Create a new plan and index it.
     *
     * @return the new plan dao
     * @param queryService a query service
     */
    private PlanDao createPlan( QueryService queryService ) {
        try {
            Plan plan = new Plan();
            plan.setName( "unnamed" );
            plan.setUri( DEFAULT_URI );
            plan.setId( idGenerator.assignId( null, plan ) );
            plan.setVersion( 1 );
            plan.setDevelopment();
            plan.addDefaultPhase( queryService );
            add( plan );
            PlanDao dao = getDao( plan );
            dao.add( plan, plan.getId() );
            return dao;
        } catch ( NotFoundException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Tests if a plan name is already taken.
     *
     * @param name a string
     * @return a boolean
     */
    public boolean isPlanNameTaken( final String name ) {
        return CollectionUtils.find( planIndex.values(), new Predicate() {
            public boolean evaluate( Object object ) {
                PlanDao w = (PlanDao) object;
                return w.getPlan().getName().equals( name );
            }
        } ) != null;
    }

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied
     * (and satisfied BeanFactoryAware and ApplicationContextAware).
     * <p>This method allows the bean instance to perform initialization only
     * possible when all bean properties have been set and to throw an
     * exception in the event of misconfiguration.
     */
    public void afterPropertiesSet() {
        try {
            loadPlanDefinitions();
            LOG.info( "Data will be saved in {}", dataDirectory.getFile().getAbsolutePath() );
        } catch ( IOException e ) {
            LOG.error( "Unable to get reference to data directory", e );
            throw new IllegalStateException( e );
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
     * @param queryService the query service
     * @param plan         the plan
     * @param command      the command
     */
    public void onAfterCommand( QueryService queryService, Plan plan, Command command ) {
        // TODO implement proper listener/callback mechanism
        if ( command.isMemorable() )
            try {
                PlanDao w = getDao( plan );
                Exporter exporter = importExportFactory.createExporter( queryService, plan );
                w.onAfterCommand( command, exporter );
            } catch ( NotFoundException e ) {
                throw new RuntimeException( "Failed to save journal", e );
            }
    }

    /**
     * Remove persistent storage for all plans (for tests).
     *
     * @throws IOException on errors
     */
    public void reset() throws IOException {
        for ( PlanDao dao : planIndex.values() )
            dao.reset();
    }

    /**
     * Replay journaled commands for all plans.
     *
     * @param queryService a query service
     * @param commander    the commander
     */
    public void replayJournals( QueryService queryService, Commander commander ) {
        synchronized ( planIndex ) {
            for ( PlanDao dao : planIndex.values() ) {
                currentDao = dao;
                Plan plan = dao.getPlan();
                try {
                    if ( plan.isDevelopment() ) {
                        commander.replay( dao.getJournal() );
                        LOG.info( "Replayed journal for plan {}", plan );
                        dao.save( importExportFactory.createExporter( queryService, plan ) );
                    }
                } catch ( IOException e ) {
                    LOG.error( MessageFormat.format( "Unable to save plan {0}", dao.getPlan() ), e );
                } catch ( CommandException e ) {
                    LOG.error(
                            MessageFormat.format( "Unable to replay journal for plan {0}", plan ),
                            e );
                } finally {
                    currentDao = null;
                }
            }
        }
    }

    /**
     * Get the plan of the current user, or a default plan if current user does not have a plan.
     *
     * @return a plan
     *         // todo Insecure... Refactor and fix elsewhere.
     */
    public Plan getCurrentPlan() {
        if ( currentDao != null ) return currentDao.getPlan();
        Plan plan = PlanManager.plan();
        if ( plan == null ) {
            plan = getPlans().get( 0 );
        }
        return plan;
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
            if ( user != null )
                return user.getPlan();
            else
                return null;
        }
    }

    /**
     * Load or create defined plans.
     * Create default plan if none defined.
     * Called by DefaultQueryService after properties set.
     *
     * @param queryService the service to use for validation
     */
    public synchronized void validate( QueryService queryService ) {
        List<PlanDao> daos = new ArrayList<PlanDao>();
        for ( String uri : planDefinitions.keySet() ) {
            List<String> propVals = planDefinitions.get( uri );
            int lastVersion = getDevelopmentVersion( uri );
            for ( int version = lastVersion; version > 0; version-- ) {
                Plan plan = new Plan();
                plan.setUri( uri );
                plan.setVersion( version );
                if ( version == lastVersion )
                    plan.setDevelopment();
                else if ( version == lastVersion - 1 )
                    plan.setProduction();
                else
                    plan.setRetired();
                plan.setName( propVals.get( 0 ) );
                plan.setClient( propVals.get( 1 ) );
                PlanDao dao = new PlanDao( this, plan );
                daos.add( dao );
            }
        }
        // Load persisted, defined plans
        for ( PlanDao dao : daos ) {
            if ( dao.isPersisted() ) {
                importPlan( dao, queryService );
            }
        }
        // Create new, defined plans
        for ( PlanDao dao : daos ) {
            if ( !dao.isPersisted() ) {
                Plan plan = dao.getPlan();
                plan.setId( idGenerator.assignId( null, plan ) );
                registerPlanDao( dao );
                dao.add( plan, plan.getId() );
            }
        }
        // Make sure there is at least one active (if bogus) plan
        if ( planIndex.isEmpty() ) {
            daos.add( createPlan( queryService ) );
        }
        // Validate and save each individual plan.
        for ( PlanDao dao : daos ) {
            try {
                currentDao = dao;
                dao.validate( queryService );
            } finally {
                currentDao = null;
            }
        }
        if ( plansFile != null && !plansFile.exists() ) {
            try {
                writePlanDefinitions();
            } catch ( IOException e ) {
                throw new RuntimeException( "Failed to write plan definitions", e );
            }
        }
    }

    private void importPlan( PlanDao dao, QueryService queryService ) {
        Plan plan = dao.getPlan();
        Importer importer = importExportFactory.createImporter( queryService, plan );
        try {
            currentDao = dao;
            dao.getIdGenerator().setLastAssignedId( IMMUTABLE_RANGE, plan );
            defineImmutableEntities( queryService );
            // set last id to start of mutable range
            dao.getIdGenerator().setLastAssignedId( 0, plan );
            dao.load( importer );
            registerPlanDao( dao );
            dao.add( plan, plan.getId() );
        } catch ( IOException e ) {
            String msg = MessageFormat.format(
                    "Unable to import plan {0}", plan.getName() );
            LOG.error( msg, e );
            throw new RuntimeException( msg, e );
        } finally {
            currentDao = null;
        }
    }

    private void defineImmutableEntities( QueryService queryService ) {
        Actor.createImmutables( queryService );
        Event.createImmutables( queryService );
        Organization.createImmutables( queryService );
        Place.createImmutables( queryService );
        Phase.createImmutables( queryService );
        Role.createImmutables( queryService );
        TransmissionMedium.createImmutables( getBuiltInMedia(), queryService );
    }

    private void registerPlanDao( PlanDao dao ) {
        String key = dao.getPlan().getVersionUri();
        if ( planIndex.get( key ) != null ) {
            LOG.error( "Duplicate plan URI " + key );
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
        return (List<Plan>) CollectionUtils.select(
                getPlans(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (Plan) obj ).getUri().equals( uri );
                    }
                }
        );
    }

    /**
     * Register user.
     *
     * @param user a user
     */
    public void addUser( User user ) {
        users.add( user );
    }

    /**
     * Get all users participating in the current plan.
     *
     * @return a list of users
     */
    @SuppressWarnings( "unchecked" )
    public List<User> getParticipants() {
        return (List<User>) CollectionUtils.select(
                new ArrayList<User>( users ),
                PredicateUtils.invokerPredicate( "isParticipant" )
        );
    }

    /**
     * Find participant given user name.
     *
     * @param userName a string
     * @return a user
     */
    public User getParticipant( final String userName ) {
        return (User) CollectionUtils.find(
                getParticipants(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (User) obj ).getUsername().equals( userName );
                    }
                }
        );
    }

    /**
     * Import segment from browsed file.
     *
     * @param upload       a file upload from file borwser dialog
     * @param queryService a query service
     * @return a segment, or null if not successful
     */
    public Segment importSegment( FileUpload upload, QueryService queryService ) {
        Segment imported = null;
        if ( upload != null ) {
            // Import and switch to segment
            LOG.info( "Importing segment" );
            Importer importer = getImportExportFactory().createImporter( queryService, getCurrentPlan() );
            try {
                InputStream inputStream = upload.getInputStream();
                imported = importer.importSegment(
                        inputStream );
                LOG.info( "Imported segment " + imported.getName() );
                this.getDao( getCurrentPlan() )
                        .save( getImportExportFactory()
                                .createExporter( queryService, getCurrentPlan() ) );
            } catch ( Exception e ) {
                // TODO redirect to a proper error screen... user has to know...
                String s = "Import error";
                LOG.error( s, e );
                throw new RuntimeException( s, e );
            }
        }
        return imported;
    }

    /**
     * Get name of directory where all versions of the current plan are  persisted.
     *
     * @return a string
     */
    public String getPlanDirectory() {
        return getPlanDirectory( plan().getUri() );
    }

    /**
     * Get name of directory where the current plan is persisted.
     *
     * @return a string
     */
    public String getPlanVersionDirectory() {
        return getPlanVersionDirectory( plan() );
    }

    private String getPlanVersionDirectory( Plan plan ) {
        return getPlanDirectory( plan.getUri() ) + File.separator + plan.getVersion();
    }

    private String getPlanDirectory( String uri ) {
        try {
            return getDataDirectory().getFile().getAbsolutePath()
                    + File.separator
                    + PlanDao.sanitize( uri );
        } catch ( IOException e ) {
            throw new RuntimeException( "Failed to get plan directory", e );
        }
    }

    private int getDevelopmentVersion( String uri ) {
        int maxVersion = 1;
        File planDir = new File( getPlanDirectory( uri ) );
        File[] subDirs = planDir.listFiles( new FileFilter() {
            public boolean accept( File file ) {
                return file.isDirectory();
            }
        } );
        if ( subDirs != null ) {
            for ( File dir : subDirs ) {
                try {
                    int version = Integer.parseInt( dir.getName() );
                    maxVersion = Math.max( maxVersion, version );
                } catch ( NumberFormatException e ) {
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
     * @param queryService a query service
     * @param analyst      issue analyst
     * @param oldDevPlan   a plan
     */
    private void moveToProduction( Plan oldDevPlan, QueryService queryService, Analyst analyst ) {
        if ( !oldDevPlan.isDevelopment() )
            throw new IllegalStateException( "Plan " + oldDevPlan + " is not a development version" );
        // Stop issue scanning
        analyst.onStop();
        // Create development plan from copy of old dev plan
        Plan newDevPlan = makeNewDevPlan( oldDevPlan, queryService );
        // Substitute current plans of users as appropriate
        for ( User user : users ) {
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
        analyst.onStart();
    }

    /**
     * Find the production version of a plan.
     *
     * @param uri a string
     * @return a plan
     */
    public Plan findProductionPlan( final String uri ) {
        return (Plan) CollectionUtils.find(
                getPlanVersions( uri ),
                PredicateUtils.invokerPredicate( "isProduction" )
        );
    }


    /**
     * Find the development version of a plan.
     *
     * @param uri a string
     * @return a plan
     */
    public Plan findDevelopmentPlan( final String uri ) {
        return (Plan) CollectionUtils.find(
                getPlanVersions( uri ),
                PredicateUtils.invokerPredicate( "isDevelopment" )
        );
    }

    @SuppressWarnings( "unchecked" )
    private List<Plan> getPlanVersions( final String uri ) {
        return (List<Plan>) CollectionUtils.select(
                getPlans(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (Plan) obj ).getUri().equals( uri );
                    }
                }
        );
    }


    private void makeNewVersionDirectory( Plan plan ) throws IOException {
        // Create new persisted dev version
        int version = plan.getVersion();
        String newDevVersionDirName = getPlanDirectory( plan.getUri() )
                + File.separator
                + ( version + 1 );
        File newVersionDir = new File( newDevVersionDirName );
        assert ( !newVersionDir.exists() );
        newVersionDir.mkdir();
        File oldVersionDir = new File( getPlanVersionDirectory( plan ) );
        // Copy files from old to new
        FileUtils.copyFileToDirectory( new File( oldVersionDir, PlanDao.DATA_FILE ), newVersionDir );
        FileUtils.copyFileToDirectory( new File( oldVersionDir, surveysFileName ), newVersionDir );
        FileUtils.copyDirectoryToDirectory( new File( oldVersionDir, uploadsDirName ), newVersionDir );
    }

    private Plan makeNewDevPlan( Plan oldDevPlan, QueryService queryService ) {
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
            PlanDao dao = new PlanDao( this, newDevPlan );
            importPlan( dao, queryService );
            newDevPlan.removeAllProducers();
            dao.save( importExportFactory.createExporter( queryService, newDevPlan ) );
            // TODO - remove initial data_???.xml backup file since it has producers set.
            return newDevPlan;
        } catch ( Exception e ) {
            throw new RuntimeException( "Failed to make new development version of " + oldDevPlan, e );
        }
    }

    /**
     * Add a producer to the plan.
     * Returns whether the plan was put into production as a result.
     *
     * @param producer     user name of planner voting to put plan in production
     * @param plan         a plan
     * @param queryService query service
     * @return a boolean
     */
    public boolean addProducer( String producer, final Plan plan, QueryService queryService ) {
        plan.addProducer( producer );
        final List<String> producers = plan.getProducers();
        boolean unanimous = !CollectionUtils.exists(
                users,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        User user = (User) obj;
                        return user.isPlanner( plan )
                                && !producers.contains( user.getUsername() );
                    }
                }
        );
        if ( unanimous ) {
            Analyst analyst = Channels.instance().getAnalyst();
            moveToProduction( plan, queryService, analyst );
        }
        return unanimous;
    }

    public List<User> getPlanners( String uri ) {
        List<User> planners = new ArrayList<User>();
        for ( User user : users ) {
            if ( user.isPlanner( uri ) ) {
                planners.add( user );
            }
        }
        return planners;
    }

    public void removeProducer( String producer, Plan plan ) {
        plan.removeProducer( producer );
    }

    /**
     * Signal that the plan with given uri was versioned.
     *
     * @param uri the plan's uri
     */
    public synchronized void setResyncRequired( String uri ) {
        outOfSyncUsers.put( uri, allUsernames() );
    }

    @SuppressWarnings( "unchecked" )
    private List<String> allUsernames() {
        return (List<String>) CollectionUtils.collect(
                users,
                TransformerUtils.invokerTransformer( "getUsername" )
        );
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

    @SuppressWarnings("unchecked")
    /**
     * Get All plan names.
     */
    public List<String> getPlanNames() {
        return (List<String>)CollectionUtils.collect(getPlans(), TransformerUtils.invokerTransformer( "getName" ));
    }
}
