package com.mindalliance.channels.pages;

import com.google.code.jqwicket.JQComponentOnBeforeRenderListener;
import com.google.code.jqwicket.JQContributionConfig;
import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.CommanderFactory;
import com.mindalliance.channels.core.community.CommunityServiceFactory;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.dao.ImportExportFactory;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.UserUploadService;
import com.mindalliance.channels.core.query.PlanServiceFactory;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.geo.GeoService;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.pages.png.ChecklistPng;
import com.mindalliance.channels.pages.png.DisseminationPng;
import com.mindalliance.channels.pages.png.EntitiesNetworkPng;
import com.mindalliance.channels.pages.png.EntityNetworkPng;
import com.mindalliance.channels.pages.png.FailureImpactsPng;
import com.mindalliance.channels.pages.png.FlowMapPng;
import com.mindalliance.channels.pages.png.HierarchyPng;
import com.mindalliance.channels.pages.png.IconPng;
import com.mindalliance.channels.pages.png.PlanMapPng;
import com.mindalliance.channels.pages.png.PngReference;
import com.mindalliance.channels.pages.png.ProceduresPng;
import com.mindalliance.channels.pages.png.RequiredNetworkingPng;
import com.mindalliance.channels.pages.png.UploadedReference;
import com.mindalliance.channels.pages.png.UserPhotoPng;
import com.mindalliance.channels.pages.reports.infoNeeds.AllInfoNeedsPage;
import com.mindalliance.channels.pages.reports.infoNeeds.InfoNeedsPage;
import com.mindalliance.channels.pages.reports.protocols.AllChecklistsPage;
import com.mindalliance.channels.pages.reports.protocols.ChecklistsPage;
import com.mindalliance.channels.pages.surveys.RFIsPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
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
 * TODO split into a bona fide service-level object
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
     * Expansion id for guide panel.
     */
    public static final long GUIDE_ID = -2;
    public static final long ALL_EVENTS = -3;
    public static final long ALL_ORGANIZATIONS = -4;
    public static final long ALL_SEGMENTS = -5;
    public static final long ALL_CLASSIFICATIONS = -6;
    public static final long TASK_MOVER = -7;
    public static final long CHECKLISTS_MAP = -8;
    public static final long PLAN_EVALUATION = -9;
    public static final long ALL_ISSUES = -10;
    public static final long PLAN_VERSIONS = -11;
    public static final long PLAN_PARTICIPATION = -12;
    public static final long PLAN_SEARCHING = -13;
    public static final long BIBLIOGRAPHY = -17;  // todo - check reference to -17 in guide
    public static final long ALL_CHECKLISTS = -18;
    public static final long ALL_GOALS = -19;

    public static final long UNKNOWN_FEEDBACK_ID = Long.MIN_VALUE;
    public static final long UNKNOWN_QUESTIONNAIRE_ID = Long.MIN_VALUE + 1;
    public static final Long UNKNOWN_RFI_SURVEY_ID = Long.MIN_VALUE + 2;
    public static final long UNKNOWN_RFI_ID = Long.MIN_VALUE + 3;
    public static final long UNKNOWN_REQUIREMENT_ID =  Long.MIN_VALUE + 4;

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

    private PlanServiceFactory planServiceFactory;

    private ImagingService imagingService;

    private UserUploadService userUploadService;

    private AttachmentManager attachmentManager;
    
    private ChannelsUserDao userDao;

    private CommunityServiceFactory communityServiceFactory;

    private PlanCommunityManager planCommunityManager;

    private Exception exception = null;
    private String serverUrl;

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
/*
        ChannelsUser user = ChannelsUser.current( userDao );
        Plan plan = user.getPlan();
        if ( plan == null ) {
            plan = planManager.getDefaultPlan( user );
            user.setPlan( plan );
        }
        return plan == null
                ? NoAccessPage.class
                : UserPage.class;
*/
        return HomePage.class;
    }

 /*   public LockManager getLockManager( Plan plan ) {
        return commanderFactory.getCommander( plan ).getLockManager();
    }
*/

    /**
     * Set to strip wicket tags from subpanels.
     */
    @Override
    protected void init() {
        super.init();

        getRequestCycleSettings().setGatherExtendedBrowserInfo( true );

        getRequestCycleListeners().add( new AbstractRequestCycleListener() {
            @Override
            public IRequestHandler onException( RequestCycle requestCycle, Exception e ) {
                exception = e;  //todo - unhack
                requestCycle.setResponsePage(
                        e instanceof PageExpiredException
                                ? new ExpiredPage()
                                : new ErrorPage( e ) );
                return requestCycle.getActiveRequestHandler();
            }
        } );

        getApplicationSettings().setInternalErrorPage( ErrorPage.class );
        getExceptionSettings().setUnexpectedExceptionDisplay( IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE );
        getApplicationSettings().setPageExpiredErrorPage( ExpiredPage.class );

        // JQuery Wicket initialization

        /*JQContributionConfig jqContributionConfig = new JQContributionConfig().withDefaultJQueryUi();*/

        JQContributionConfig jqContributionConfig =
                new JQContributionConfig( new JavaScriptResourceReference(Channels.class, "res/jquery-1.7.2.min.js"  ) )
                        .withJQueryUiJs( new JavaScriptResourceReference(Channels.class, "res/jquery-1.8.16-ui.min.js"  ) )
                        .withJQueryUiCss( new CssResourceReference( Channels.class, "res/jquery-1.8.16-ui.css" ) );

        getComponentPreOnBeforeRenderListeners()
                .add( new JQComponentOnBeforeRenderListener( jqContributionConfig ) );

       /* getComponentPreOnBeforeRenderListeners()
                .add( new JQComponentOnBeforeRenderListenerFix( jqContributionConfig ) );   // todo - remove temporary FIX when JQWicket issue #29 fixed*/

        getComponentInstantiationListeners().add( getInjector() );

        getMarkupSettings().setStripWicketTags( true );

        mountPage( "allProtocols", AllChecklistsPage.class );
        mountPage( "allInfoNeeds", AllInfoNeedsPage.class );
        mountPage( "protocols", ChecklistsPage.class );
        mountPage( "infoNeeds", InfoNeedsPage.class );
        mountPage( "plan", PlanPage.class );
        mountPage( "admin", AdminPage.class );
        mountPage( "nosops.html", NoAccessPage.class );
        mountPage( "login.html", LoginPage.class );
        mountPage( "newPasswordRequest.html", NewPasswordPage.class );
        mountPage( "segment.xml", ExportPage.class );
        mountPage( "geomap", GeoMapPage.class );
        mountPage( "home", HomePage.class );
        mountPage( "communities", CommunitiesPage.class );
        mountPage( "community", CommunityPage.class );
        mountPage( "plans", PlansPage.class );
        mountPage( "feedback", FeedbackPage.class );
        mountPage( RFIsPage.SURVEYS, RFIsPage.class );
        mountPage( "requirements", RequirementsPage.class );
        mountPage(  "participation", ParticipationManagerPage.class );
        mountPage( "help", HelpPage.class );

        mountResource( "uploads/${name}", new UploadedReference(  ) );

        mountResource( "users/photos/${name}", new PngReference(
                UserPhotoPng.class,
                getUserDao(),
                getPlanManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
                ) );
        mountResource( "icons/${name}", new PngReference(
                IconPng.class,
                getUserDao(),
                getPlanManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "segment.png", new PngReference(
                FlowMapPng.class,
                getUserDao(),
                getPlanManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "plan.png", new PngReference(
                PlanMapPng.class,
                getUserDao(),
                getPlanManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "network.png", new PngReference(
                EntityNetworkPng.class,
                getUserDao(),
                getPlanManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "entities.png", new PngReference(
                EntitiesNetworkPng.class,
                getUserDao(),
                getPlanManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "hierarchy.png", new PngReference(
                HierarchyPng.class,
                getUserDao(),
                getPlanManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "essential.png", new PngReference(
                FailureImpactsPng.class,
                getUserDao(),
                getPlanManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "dissemination.png", new PngReference(
                DisseminationPng.class,
                getUserDao(),
                getPlanManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "checklist.png", new PngReference(
                ChecklistPng.class,
                getUserDao(),
                getPlanManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "procedures.png", new PngReference(
                ProceduresPng.class,
                getUserDao(),
                getPlanManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "required.png", new PngReference(
                RequiredNetworkingPng.class,
                getUserDao(),
                getPlanManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        userUploadService.cleanUpPhotos();

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

    public CommunityServiceFactory getCommunityServiceFactory() {
        return communityServiceFactory;
    }

    public void setCommunityServiceFactory( CommunityServiceFactory communityServiceFactory ) {
        this.communityServiceFactory = communityServiceFactory;
    }

    public PlanServiceFactory getPlanServiceFactory() {
        return planServiceFactory;
    }

    public void setPlanServiceFactory( PlanServiceFactory planServiceFactory ) {
        this.planServiceFactory = planServiceFactory;
    }

    public ImagingService getImagingService() {
        return imagingService;
    }

    public UserUploadService getUserUploadService() {
        return userUploadService;
    }

    public void setUserUploadService( UserUploadService userUploadService ) {
        this.userUploadService = userUploadService;
    }

    public void setImagingService( ImagingService imagingService ) {
        this.imagingService = imagingService;
    }

    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    public void setAttachmentManager( AttachmentManager attachmentManager ) {
        this.attachmentManager = attachmentManager;
    }

    public void setUserDao( ChannelsUserDao userDao ) {
        this.userDao = userDao;
    }

    public ChannelsUserDao getUserDao() {
        return userDao;
    }

    public PlanCommunityManager getPlanCommunityManager() {
        return planCommunityManager;
    }

    public void setPlanCommunityManager( PlanCommunityManager planCommunityManager ) {
        this.planCommunityManager = planCommunityManager;
    }

    // FOR TESTING ONLY
    public void setDiagramFactory( DiagramFactory dm ) {
        diagramFactory = dm;
    }

    //todo - unhack
    public Exception getExceptionOnce() {
        Exception oneTime = exception;
        exception = null;
        return oneTime;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl( String serverUrl ) {
        this.serverUrl = serverUrl;
    }
}