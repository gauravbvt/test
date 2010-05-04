package com.mindalliance.channels.pages;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.LockManager;
import com.mindalliance.channels.dao.ImportExportFactory;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.geo.GeoService;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.playbook.ContactPage;
import com.mindalliance.channels.pages.playbook.PlaybookPage;
import com.mindalliance.channels.pages.playbook.TaskPlaybook;
import com.mindalliance.channels.pages.playbook.VCardPage;
import com.mindalliance.channels.pages.png.EntitiesNetworkPage;
import com.mindalliance.channels.pages.png.EntityNetworkPage;
import com.mindalliance.channels.pages.png.EssentialFlowMapPage;
import com.mindalliance.channels.pages.png.FlowMapPage;
import com.mindalliance.channels.pages.png.HierarchyPage;
import com.mindalliance.channels.pages.png.PlanMapPage;
import com.mindalliance.channels.pages.reports.SOPsReportPage;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.IndexedParamUrlCodingStrategy;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionIdentifierAware;

import java.util.HashMap;
import java.util.Map;

/**
 * Application object for Channels.
 * Initialized in /WEB-INF/applicationContext.xml.
 *
 * @TODO split into a bonified service-level object
 */
public class Channels extends WebApplication
        implements ApplicationListener, ApplicationContextAware {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( Channels.class );

    /**
     * The underlying service.
     */
    private QueryService queryService;

    /**
     * A diagram factory  - for testing only
     */
    private DiagramFactory diagramFactory;

    /**
     * Segment importer.
     */
    private ImportExportFactory importExportFactory;

    /**
     * Analyst
     */
    private Analyst analyst;

    /**
     * GeoService
     */
    private GeoService geoService;

    private PlanManager planManager;

    /**
     * One commander per plan.
     */
    private final Map<Plan, Commander> commanders = new HashMap<Plan, Commander>();

    /**
     * One lock manager per plan.
     */
    private final Map<Plan, LockManager> lockManagers = new HashMap<Plan, LockManager>();

    private SpringComponentInjector injector;

    private ApplicationContext applicationContext;

    /**
     * Default Constructor.
     */
    public Channels() {
    }


    /**
     * Set to strip wicket tags from subpanels.
     */
    @Override
    protected void init() {
        super.init();

        addComponentInstantiationListener( getInjector() );

        getMarkupSettings().setStripWicketTags( true );

        String[] parameterNames = { PlaybookPage.ACTOR_PARM, PlaybookPage.PART_PARM };
        mount( new MixedParamUrlCodingStrategy( "report", SOPsReportPage.class, parameterNames ) );

        mount( new IndexedParamUrlCodingStrategy( "playbooks", TaskPlaybook.class ) );
        mount( new IndexedParamUrlCodingStrategy( "vcards", VCardPage.class ) );
        mount( new IndexedParamUrlCodingStrategy( "contacts", ContactPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "plan", PlanPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "admin", AdminPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "nosops.html", NoAccessPage.class ) );

        mount( new IndexedParamUrlCodingStrategy( "uploads", UploadPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "login.html", LoginPage.class ) );

        mount( new QueryStringUrlCodingStrategy( "segment.xml", ExportPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "segment.png", FlowMapPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "plan.png", PlanMapPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "network.png", EntityNetworkPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "entities.png", EntitiesNetworkPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "hierarchy.png", HierarchyPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "geomap.html", GeoMapPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "essential.png", EssentialFlowMapPage.class ) );

        getApplicationSettings().setInternalErrorPage( ErrorPage.class );
        getApplicationSettings().setPageExpiredErrorPage( ExpiredPage.class );

    }

    public SpringComponentInjector getInjector() {
        if ( injector == null )
            injector = new SpringComponentInjector( this );
        return injector;
    }

    public void setInjector( SpringComponentInjector injector ) {
        this.injector = injector;
    }

    @Override
    protected void onDestroy() {
        LOG.info( "Goodbye!" );
        queryService.onDestroy();
        analyst.onDestroy();
    }

    /**
     * Get the home page for the current user.
     */
    @Override
    public Class<? extends WebPage> getHomePage() {
        User user = User.current();
        Plan plan = user.getPlan();
        if ( plan == null ) {
            plan = planManager.getDefaultPlan( user );
            user.setPlan( plan );
        }
        return user.isAdmin()                  ? AdminPage.class
             : plan == null                    ? NoAccessPage.class
             : user.isPlanner( plan.getUri() ) ? PlanPage.class
                                               : SOPsReportPage.class ;
    }

    public QueryService getQueryService() {
        return queryService;
    }

    public void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
    }

    public DiagramFactory getDiagramFactory() {
        if ( diagramFactory != null ) {
            // When testing only
            return diagramFactory;
        } else {
            // Get a prototype bean
            return (DiagramFactory) applicationContext.getBean( "diagramFactory" );
        }
    }

    public void setApplicationContext( ApplicationContext applicationContext ) {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // FOR TESTING ONLY
    public void setDiagramFactory( DiagramFactory dm ) {
        diagramFactory = dm;
    }

    public ImportExportFactory getImportExportFactory() {
        return importExportFactory;
    }

    public void setImportExportFactory( ImportExportFactory importExportFactory ) {
        this.importExportFactory = importExportFactory;
    }

    public Analyst getAnalyst() {
        return analyst;
    }

    public void setAnalyst( Analyst analyst ) {
        this.analyst = analyst;
    }

    public static Channels instance() {
        return (Channels) get();
    }

    /**
     * Get the given plan's commander
     *
     * @param plan a plan
     * @return a commander
     */
    public Commander getCommander( Plan plan ) {
        assert plan != null;
        synchronized ( commanders ) {
            Commander commander = commanders.get( plan );
            if ( commander == null ) {
                commander = (Commander) applicationContext.getBean( "commander" );
                commander.setLockManager( getLockManager( plan ) );
                try {
                    commander.setPlanDao( planManager.getDao( plan ) );
                } catch ( NotFoundException e ) {
                    // Program error if this happens...
                    throw new RuntimeException( e );
                }
                commanders.put( plan, commander );
                commander.initialize();
            }
            return commander;
        }
    }

    /**
     * Get the active plan's lock manager
     *
     * @return a lock manager
     */
    public LockManager getLockManager() {
        Plan plan = queryService.getCurrentPlan();
        return getLockManager( plan );
    }

    /**
     * Get the active plan's lock manager
     *
     * @param plan a plan
     * @return a lock manager
     */
    public LockManager getLockManager( Plan plan ) {
        synchronized ( lockManagers ) {
            LockManager lockManager = lockManagers.get( plan );
            if ( lockManager == null ) {
                lockManager = (LockManager) applicationContext.getBean( "lockManager" );
                lockManagers.put( plan, lockManager );
            }
            return lockManager;
        }
    }

    public void onApplicationEvent( ApplicationEvent event ) {
        if ( LOG.isDebugEnabled() && event instanceof AbstractAuthenticationEvent ) {
            Authentication auth = ( (AbstractAuthenticationEvent) event ).getAuthentication();
            String name = auth.getName();
            String session = ( (SessionIdentifierAware) auth.getDetails() ).getSessionId();
            LOG.debug( event.getClass().getSimpleName() + ": " + name
                       + ";session=" + session );
        }

    }

    public GeoService getGeoService() {
        return geoService;
    }

    public void setGeoService( GeoService geoService ) {
        this.geoService = geoService;
    }

    public PlanManager getPlanManager() {
        return planManager;
    }

    public void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
    }
}
