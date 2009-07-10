package com.mindalliance.channels.dao;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.Importer;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.export.ConnectionSpecification;
import com.mindalliance.channels.export.ImportExportFactory;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.query.DefaultQueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persistent store for plans.
 */
public class PlanManager implements InitializingBean {

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    /** All the plans, indexed by id. */
    private final Map<Long, PlanDao> planIndex =
            Collections.synchronizedMap( new HashMap<Long, PlanDao>() );

    /** The plan importer. */
    private ImportExportFactory importExportFactory;

    /** The thing that generates unique ids. */
    private IdGenerator idGenerator;

    /** Where to import initial scenarios from. Don't if null... */
    private Resource importDirectory;

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

    /** Required for AOP decorations. */
    public PlanManager() {
    }

    public PlanManager( ImportExportFactory importExportFactory, IdGenerator idGenerator ) {
        this.importExportFactory = importExportFactory;
        this.idGenerator = idGenerator;
    }

    public Resource getImportDirectory() {
        return importDirectory;
    }

    public void setImportDirectory( Resource importDirectory ) {
        this.importDirectory = importDirectory;
    }

    /**
     * Find a plan given its id.
     * @param id the id
     * @return the plan
     * @throws NotFoundException when there are no plans with this id
     */
    public Plan get( long id ) throws NotFoundException {
        PlanDao result = planIndex.get( id );
        if ( result == null )
            throw new NotFoundException();
        return result.getPlan();
    }

    /**
     * Get the dao wrapper for a plan.
     * @param plan the plan
     * @return the dao wrapper
     * @throws NotFoundException if plan does not exist
     */
    public PlanDao getDao( Plan plan ) throws NotFoundException {
        PlanDao result = planIndex.get( plan.getId() );
        if ( result == null )
            throw new NotFoundException();
        return result;
    }

    /**
     * Get all plans managed by this dao.
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
     * @param plans the plans
     */
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

    /**
     * Return the number of plans manager by this instance.
     * @return the plan count
     */
    public int getPlanCount() {
        return planIndex.size();
    }

    /**
     * Add a plan.
     * @param plan the plan
     */
    public void add( Plan plan ) {
        planIndex.put( plan.getId(), new PlanDao( this, plan ) );
    }

    /**
     * Create a new plan and index it.
     * @return the new plan
     */
    public Plan createPlan() {
        Plan plan = new Plan();
        plan.setName( "UNNAMED" );
        plan.setId( idGenerator.assignId( null ) );

        add( plan );
        try {
            PlanDao dao = getDao( plan );
            dao.add( plan, plan.getId() );
        } catch ( NotFoundException e ) {
            throw new RuntimeException( e );
        }

        return plan;
    }

    /**
     * Tests if a plan name is already taken.
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
            logger.info( "Data will be saved in {}", dataDirectory.getFile().getAbsolutePath() );
        } catch ( IOException e ) {
            logger.error( "Unable to get reference to data directory", e );
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
     * @param queryService the query service
     * @param plan the plan
     * @param command the command
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
     * @throws IOException on errors
     */
    public void reset() throws IOException {
        for ( PlanDao dao : planIndex.values() )
            dao.reset();
    }

    /**
     * Replay journaled commands for all plans.
     * @param queryService the query service
     * @param commander the commander
     */
    public void replayJournals( QueryService queryService, Commander commander ) {
        synchronized ( planIndex ) {
            for ( PlanDao dao : planIndex.values() ) {
                Plan plan = dao.getPlan();
                try {
                    commander.replay( dao.getJournal() );
                    dao.save( importExportFactory.createExporter( queryService, plan ) );
                    logger.info( "Replayed journal for plan {}", plan );
                } catch ( CommandException e ) {
                    logger.error(
                        MessageFormat.format( "Unable to replay journal for plan {0}", plan ),
                        e );
                } catch ( IOException e ) {
                    logger.error( MessageFormat.format( "Unable to save plan {0}", plan ), e );
                }
            }
        }
    }

    /**
     * Get the plan of the current user (or a default plan if current user does not have a plan.
     * @todo Insecure... Refactor and fix elsewhere.
     * @return a plan
     */
    public Plan getCurrentPlan() {
        Plan plan = User.current().getPlan();
        if ( plan == null ) {
            plan = getPlans().get( 0 );
        }
        return plan;
    }

    /**
     * Check all loaded plans.
     * @param service the service to use for the checking
     */
    public synchronized void validate( DefaultQueryService service ) {
        if ( planIndex.isEmpty() )
            createPlan();

        for ( Plan plan : getPlans() )
            try {
                PlanDao dao = getDao( plan );
                Importer importer = importExportFactory.createImporter( service, plan );

                dao.load( importer );

                if ( planIndex.size() == 1 && importDirectory != null
                     && plan.getScenarioCount() == 0 )
                    importScenarios( importer );

                dao.validate( service, importExportFactory.createExporter( service, plan ) );

            } catch ( NotFoundException e ) {
                String msg = MessageFormat.format(
                        "Unable to find dao for plan {0}", plan.getName() );
                logger.error( msg, e );
                throw new RuntimeException( msg, e );

            } catch ( IOException e ) {
                String msg = MessageFormat.format(
                        "Unable to import plan {0}", plan.getName() );
                logger.error( msg, e );
                throw new RuntimeException( msg, e );
            }
    }

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

    private File[] getImportFiles() {
        if ( importDirectory != null ) {
            try {
                File directory = importDirectory.getFile();
                if ( directory.exists() && directory.isDirectory() )
                    return directory.listFiles(
                            new FilenameFilter() {
                                /** {@inheritDoc} */
                                public boolean accept( File dir, String name ) {
                                    return name.endsWith( ".xml" );
                                }
                            } );
            } catch ( IOException e ) {
                logger.warn( "Unable to read import directory. Skipping import", e );
            }
        }

        return new File[0];
    }
}
