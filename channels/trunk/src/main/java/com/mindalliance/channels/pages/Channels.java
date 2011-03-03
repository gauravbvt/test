package com.mindalliance.channels.pages;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.LockManager;
import com.mindalliance.channels.dao.ImportExportFactory;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.geo.GeoService;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.playbook.ContactPage;
import com.mindalliance.channels.pages.playbook.VCardPage;
import com.mindalliance.channels.pages.png.DisseminationPage;
import com.mindalliance.channels.pages.png.EntitiesNetworkPage;
import com.mindalliance.channels.pages.png.EntityNetworkPage;
import com.mindalliance.channels.pages.png.FailureImpactsPage;
import com.mindalliance.channels.pages.png.FlowMapPage;
import com.mindalliance.channels.pages.png.HierarchyPage;
import com.mindalliance.channels.pages.png.PlanMapPage;
import com.mindalliance.channels.pages.png.ProceduresPage;
import com.mindalliance.channels.pages.reports.AssignmentReportPage;
import com.mindalliance.channels.pages.reports.CommitmentReportPage;
import com.mindalliance.channels.pages.reports.ProceduresReportPage;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.request.target.coding.IndexedParamUrlCodingStrategy;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;
import org.apache.wicket.settings.IExceptionSettings;
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
     * Expansion id for social panel.
     */
    public static final long SOCIAL_ID = -1;
    /**
     * URI of support community.
     */
    private String supportCommunityUri;

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

        getRequestCycleSettings().setGatherExtendedBrowserInfo( true );

        addComponentInstantiationListener( getInjector() );

        getMarkupSettings().setStripWicketTags( true );

        mount( new QueryStringUrlCodingStrategy( "procedures", ProceduresReportPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "task", AssignmentReportPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "flow", CommitmentReportPage.class ) );

        mount( new IndexedParamUrlCodingStrategy( "vcards", VCardPage.class ) );
        mount( new IndexedParamUrlCodingStrategy( "contacts", ContactPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "plan", PlanPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "admin", AdminPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "nosops.html", NoAccessPage.class ) );

        mount( new IndexedParamUrlCodingStrategy( "uploads", UploadPage.class ) );
        mount( new IndexedParamUrlCodingStrategy( "icons", IconPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "login.html", LoginPage.class ) );

        mount( new QueryStringUrlCodingStrategy( "segment.xml", ExportPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "segment.png", FlowMapPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "plan.png", PlanMapPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "network.png", EntityNetworkPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "entities.png", EntitiesNetworkPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "hierarchy.png", HierarchyPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "geomap.html", GeoMapPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "essential.png", FailureImpactsPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "dissemination.png", DisseminationPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "procedures.png", ProceduresPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "user", UserPage.class ) );

        getApplicationSettings().setInternalErrorPage( ErrorPage.class );
        getExceptionSettings().setUnexpectedExceptionDisplay( IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE );
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
        return plan == null
                ? NoAccessPage.class
                : UserPage.class;
/*
        return user.isAdmin() ? PlanPage.class // was AdminPage.class
                : plan == null ? NoAccessPage.class
                : user.isPlanner( plan.getUri() ) ? PlanPage.class
                : ProceduresReportPage.class;
*/
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
                commander.setPlanDao( planManager.getDao( plan ) );
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
        Plan plan = queryService.getPlan();
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

    public void setSupportCommunityUri( String supportCommunityUri ) {
        this.supportCommunityUri = supportCommunityUri;
    }

    public String getSupportCommunityUri() {
        return supportCommunityUri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final RequestCycle newRequestCycle( final Request request, final Response response ) {
        return new WebRequestCycle( this, (WebRequest) request, (WebResponse) response ) {
            /**
             * {@inheritDoc}
             */
            @Override
            public Page onRuntimeException( final Page cause, final RuntimeException e ) {
                // obviously you can check the instanceof the exception and return the appropriate page if desired
                if ( e instanceof PageExpiredException ) {
                    return new ExpiredPage();
                } else {
                    return new ErrorPage( e );
                }
            }
        };
    }

    /**
     * Get planner support community.
     * @return a string
     */
    public String getPlannerSupportCommunity() {
        return User.current().getPlan()
                            .getPlannerSupportCommunityUri( getSupportCommunityUri() );
    }
}
