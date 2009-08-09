package com.mindalliance.channels.dao;

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
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.query.DefaultQueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataRetrievalFailureException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
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
     * Last modification date for the plans definitions. Used to monitor external changes to
     * the plans file.
     */
    private long lastModified;
    /**
     * Definitions parsed from plans properties file.
     * uri => [name, client]
     */
    private Map<String, List<String>> planDefinitions = new HashMap<String, List<String>>();
    /**
     * All the plans, indexed by id.
     */
    private final Map<String, PlanDao> planIndex =
            Collections.synchronizedMap( new HashMap<String, PlanDao>() );
    /**
     * User participations in plans.
     * plan uri => participation
     */
    private Set<User> users = new HashSet<User>();

    /**
     * The plan importer.
     */
    private ImportExportFactory importExportFactory;

    /**
     * The thing that generates unique ids.
     */
    private IdGenerator idGenerator;

    /**
     * Where to import initial scenarios from. Don't if null...
     */
    private Resource importDirectory;
    /**
     * The default current plan dao -- used only during loading.
     */
    private PlanDao currentDao;

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

    /**
     * Get the dao wrapper for a plan.
     *
     * @param plan the plan
     * @return the dao wrapper
     * @throws NotFoundException if plan does not exist
     */
    public PlanDao getDao( Plan plan ) throws NotFoundException {
        PlanDao result = planIndex.get( plan.getUri() );
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
            for ( PlanDao dao : planIndex.values() )
                result.add( dao.getPlan() );

            Collections.sort( result );
            return Collections.unmodifiableList( result );
        }
    }

    /**
     * Initialize managed plans.
     *
     * @param plans the plans
     */
/*
    public void setPlans( List<Plan> plans ) {
        synchronized ( planIndex ) {
            planIndex.clear();
            for ( Plan p : plans ) {
                long id = p.getId();
                if ( planIndex.containsKey( id ) )
                    logger.warn( "Duplicate plan id {}. Plan ignored", id );
                else
                    planIndex.put( id, new PlanDao( this, p ) );
            }
        }
        logger.info( "Loaded {} plan(s)", planIndex.size() );
    }
*/

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
     */
    public PlanDao createPlan() {
        try {
            Plan plan = new Plan();
            plan.setName( "UNNAMED" );
            plan.setId( idGenerator.assignId( null ) );
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
                    commander.replay( dao.getJournal() );
                    LOG.info( "Replayed journal for plan {}", plan );
                    dao.save( importExportFactory.createExporter( queryService, dao.getPlan() ) );
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
     * Get the plan of the current user (or a default plan if current user does not have a plan.
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
     * @param service the service to use for validation
     */
    public synchronized void validate( DefaultQueryService service ) {
        List<PlanDao> daos = new ArrayList<PlanDao>();
        for ( String uri : planDefinitions.keySet() ) {
            List<String> propVals = planDefinitions.get( uri );
            Plan plan = new Plan();
            plan.setUri( uri );
            plan.setName( propVals.get( 0 ) );
            plan.setClient( propVals.get( 1 ) );
            PlanDao dao = new PlanDao( this, plan );
            daos.add( dao );
        }
        // Load persisted, defined plans
        for ( PlanDao dao : daos ) {
            if ( dao.isPersisted() ) {
                Plan plan = dao.getPlan();
                Importer importer = importExportFactory.createImporter( service, plan );
                try {
                    currentDao = dao;
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
        }
        // Create new, defined plans
        for ( PlanDao dao : daos ) {
            if ( !dao.isPersisted() ) {
                Plan plan = dao.getPlan();
                plan.setId( idGenerator.assignId( null ) );
                registerPlanDao( dao );
                dao.add( plan, plan.getId() );
            }
        }
        // Make sure there is at least one active (if bogus) plan
        if ( planIndex.isEmpty() ) {
            daos.add( createPlan() );
        }
        // Validate and save each individual plan.
        for ( PlanDao dao : daos ) {
            try {
                currentDao = dao;
                dao.validate( service );
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
        lastModified = plansFile == null ? System.currentTimeMillis() : plansFile.lastModified();
    }

    private void registerPlanDao( PlanDao dao ) {
        String key = dao.getPlan().getUri();
        if ( planIndex.get( key ) != null ) {
            LOG.error( "Duplicate plan URI " + key );
            throw new DuplicateKeyException();
        }
        planIndex.put( dao.getPlan().getUri(), dao );
    }

/*
    private void importScenarios( Importer importer ) {
        Map<String, Long> idMap = new HashMap<String, Long>();
        Map<Connector, List<ConnectionSpecification>> proxyConnectors =
                new HashMap<Connector, List<ConnectionSpecification>>();

        for ( File file : getImportFiles() ) {
            try {
                Map<String, Object> results = importer.loadScenario( new FileInputStream( file ) );

                // Cumulate results
                idMap.putAll( (Map<String, Long>) results.get( "idMap" ) );
                proxyConnectors.putAll(
                        (Map<Connector, List<ConnectionSpecification>>) results.get(
                                "proxyConnectors" ) );

                Scenario scenario = (Scenario) results.get( "scenario" );
                logger.info( MessageFormat.format(
                        "Imported scenario {0} from {1}", scenario.getName(), file.getPath() ) );

            } catch ( IOException e ) {
                logger.warn( MessageFormat.format( "Failed to import {0}", file.getPath() ), e );
            }
        }

        // Reconnect external links
        importer.reconnectExternalFlows( proxyConnectors, false );
    }
*/

/*    private File[] getImportFiles() {
        if ( importDirectory != null ) {
            try {
                File directory = importDirectory.getFile();
                if ( directory.exists() && directory.isDirectory() )
                    return directory.listFiles(
                            new FilenameFilter() {
                                *//** {@inheritDoc} *//*
                                public boolean accept( File dir, String name ) {
                                    return name.endsWith( ".xml" );
                                }
                            } );
            } catch ( IOException e ) {
                logger.warn( "Unable to read import directory. Skipping import", e );
            }
        }

        return new File[0];
    }*/

    /**
     * Find the plan, if any, with a given uri.
     *
     * @param uri a string
     * @return a plan or null
     */
    public Plan getPlanWithUri( final String uri ) {
        return (Plan) CollectionUtils.find(
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
     * Import scenario from browsed file.
     *
     * @param upload       a file upload from file borwser dialog
     * @param queryService a query service
     * @return a scenario, or null if not successful
     */
    public Scenario importScenario( FileUpload upload, QueryService queryService ) {
        Scenario imported = null;
        if ( upload != null ) {
            // Import and switch to scenario
            LOG.info( "Importing scenario" );
            Importer importer = getImportExportFactory().createImporter( queryService, getCurrentPlan() );
            try {
                InputStream inputStream = upload.getInputStream();
                imported = importer.importScenario(
                        inputStream );
                LOG.info( "Imported scenario " + imported.getName() );
                this.getDao( getCurrentPlan() )
                        .save( getImportExportFactory()
                                .createExporter( queryService, getCurrentPlan() ));
            } catch ( Exception e ) {
                // TODO redirect to a proper error screen... user has to know...
                String s = "Import error";
                LOG.error( s, e );
                throw new RuntimeException( s, e );
            }
        }
        return imported;
    }
}
