package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.CommanderFactory;
import com.mindalliance.channels.core.command.LockManager;
import com.mindalliance.channels.core.dao.ImportExportFactory;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.geo.GeoService;
import com.mindalliance.channels.graph.DiagramFactory;
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
import com.mindalliance.channels.pages.procedures.AssignmentReportPage;
import com.mindalliance.channels.pages.procedures.CommitmentReportPage;
import com.mindalliance.channels.pages.procedures.ProcedureMapPage;
import com.mindalliance.channels.pages.procedures.ProceduresReportPage;
import com.mindalliance.channels.pages.reports.guidelines.GuidelinesPage;
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
     * Expansion id for social panel.
     */
    public static final long SOCIAL_ID = -1;

    /**
     * Analyst.
     */
    private Analyst analyst;

    private ApplicationContext applicationContext;

    private CommanderFactory commanderFactory;

    /**
     * A diagram factory  - for testing only.
     */
    private DiagramFactory diagramFactory;

    /**
     * GeoService.
     */
    private GeoService geoService;

    /**
     * Segment importer.
     */
    private ImportExportFactory importExportFactory;

    private SpringComponentInjector injector;

    private PlanManager planManager;

    //-------------------------------
    /**
     * Default Constructor.
     */
    public Channels() {
    }

    //-------------------------------
    public DiagramFactory getDiagramFactory() {
        if ( diagramFactory != null ) {
            // When testing only
            return diagramFactory;
        } else {
            // Get a prototype bean
            return (DiagramFactory) applicationContext.getBean( "diagramFactory" );
        }
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
    }
    
    /**
     * Get the active plan's lock manager.
     *
     * @param plan a plan
     * @return a lock manager
     */
    public LockManager getLockManager( Plan plan ) {
        return commanderFactory.getCommander( plan ).getLockManager();
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
        mount( new QueryStringUrlCodingStrategy( "participants", GuidelinesPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "mapped", ProcedureMapPage.class ) );
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
        mount( new QueryStringUrlCodingStrategy( "newPasswordRequest.html", NewPasswordPage.class ) );

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
        mount( new QueryStringUrlCodingStrategy( "home", UserPage.class ) );

        getApplicationSettings().setInternalErrorPage( ErrorPage.class );
        getExceptionSettings().setUnexpectedExceptionDisplay( IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE );
        getApplicationSettings().setPageExpiredErrorPage( ExpiredPage.class );

    }

    @Override
    public final RequestCycle newRequestCycle( final Request request, final Response response ) {
        return new WebRequestCycle( this, (WebRequest) request, (WebResponse) response ) {
            @Override
            public Page onRuntimeException( Page cause, RuntimeException e ) {
                // obviously you can check the instanceof the exception and return the appropriate page if desired
                return e instanceof PageExpiredException ? new ExpiredPage() : new ErrorPage( e );
            }
        };
    }

    @Override
    public void onApplicationEvent( ApplicationEvent event ) {
        if ( LOG.isDebugEnabled() && event instanceof AbstractAuthenticationEvent ) {
            Authentication auth = ( (AbstractAuthenticationEvent) event ).getAuthentication();
            String name = auth.getName();
            String session = ( (SessionIdentifierAware) auth.getDetails() ).getSessionId();
            LOG.debug( event.getClass().getSimpleName() + ": " + name
                    + ";session=" + session );
        }

    }

    @Override
    protected void onDestroy() {
        LOG.info( "Goodbye!" );
        analyst.onDestroy();
    }

    //-------------------------------
    public Analyst getAnalyst() {
        return analyst;
    }

    public void setAnalyst( Analyst analyst ) {
        this.analyst = analyst;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext( ApplicationContext applicationContext ) {
        this.applicationContext = applicationContext;
    }

    public CommanderFactory getCommanderFactory() {
        return commanderFactory;
    }

    public void setCommanderFactory( CommanderFactory commanderFactory ) {
        this.commanderFactory = commanderFactory;
    }

    public GeoService getGeoService() {
        return geoService;
    }

    public void setGeoService( GeoService geoService ) {
        this.geoService = geoService;
    }

    public ImportExportFactory getImportExportFactory() {
        return importExportFactory;
    }

    public void setImportExportFactory( ImportExportFactory importExportFactory ) {
        this.importExportFactory = importExportFactory;
    }

    public SpringComponentInjector getInjector() {
        if ( injector == null )
            injector = new SpringComponentInjector( this );
        return injector;
    }

    public void setInjector( SpringComponentInjector injector ) {
        this.injector = injector;
    }

    public PlanManager getPlanManager() {
        return planManager;
    }

    public void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
    }

    // FOR TESTING ONLY
    public void setDiagramFactory( DiagramFactory dm ) {
        diagramFactory = dm;
    }

}
