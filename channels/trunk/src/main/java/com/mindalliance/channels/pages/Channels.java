package com.mindalliance.channels.pages;

import com.google.code.jqwicket.JQComponentOnBeforeRenderListenerFix;
import com.google.code.jqwicket.JQContributionConfig;
import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.CommanderFactory;
import com.mindalliance.channels.core.community.CommunityServiceFactory;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.dao.ImportExportFactory;
import com.mindalliance.channels.core.dao.ModelManager;
import com.mindalliance.channels.core.dao.user.UserUploadService;
import com.mindalliance.channels.core.query.ModelServiceFactory;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.geo.GeoService;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.pages.png.ChecklistPng;
import com.mindalliance.channels.pages.png.CommandChainsPng;
import com.mindalliance.channels.pages.png.DisseminationPng;
import com.mindalliance.channels.pages.png.EntitiesNetworkPng;
import com.mindalliance.channels.pages.png.EntityNetworkPng;
import com.mindalliance.channels.pages.png.FailureImpactsPng;
import com.mindalliance.channels.pages.png.FlowMapPng;
import com.mindalliance.channels.pages.png.HierarchyPng;
import com.mindalliance.channels.pages.png.IconPng;
import com.mindalliance.channels.pages.png.ModelMapPng;
import com.mindalliance.channels.pages.png.PngReference;
import com.mindalliance.channels.pages.png.ProceduresPng;
import com.mindalliance.channels.pages.png.RequiredNetworkingPng;
import com.mindalliance.channels.pages.png.UploadedReference;
import com.mindalliance.channels.pages.png.UserPhotoPng;
import com.mindalliance.channels.pages.reports.issues.IssuesPage;
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
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;

/**
 * Application object for Channels.
 * Initialized in /WEB-INF/applicationContext.xml.
 * <p/>
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
    public static final long ALL_INVOLVEMENTS = -4;
    public static final long ALL_SEGMENTS = -5;
    public static final long ALL_CLASSIFICATIONS = -6;
    public static final long TASK_MOVER = -7;
    public static final long CHECKLISTS_MAP = -8;
    public static final long MODEL_EVALUATION = -9;
    public static final long ALL_ISSUES = -10;
    public static final long MODEL_VERSIONS = -11;
    public static final long PLAN_PARTICIPATION = -12; // obsolete
    public static final long MODEL_SEARCHING = -13;
    public static final long BIBLIOGRAPHY = -17;  // todo - check reference to -17 in guide
    public static final long ALL_CHECKLISTS = -18;
    public static final long ALL_GOALS = -19;
    public static final long GALLERY_ID = -20;

    public static final long UNKNOWN_FEEDBACK_ID = Long.MIN_VALUE;
    public static final long UNKNOWN_QUESTIONNAIRE_ID = Long.MIN_VALUE + 1;
    public static final Long UNKNOWN_RFI_SURVEY_ID = Long.MIN_VALUE + 2;
    public static final long UNKNOWN_RFI_ID = Long.MIN_VALUE + 3;
    public static final long UNKNOWN_REQUIREMENT_ID = Long.MIN_VALUE + 4;

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

    private ModelManager modelManager;

    private ModelServiceFactory modelServiceFactory;

    private ImagingService imagingService;

    private UserUploadService userUploadService;

    private AttachmentManager attachmentManager;

    private UserRecordService userDao;

    private CommunityServiceFactory communityServiceFactory;

    private PlanCommunityManager planCommunityManager;

    private ParticipationManager participationManager;

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
                new JQContributionConfig( new JavaScriptResourceReference( Channels.class, "res/jquery-1.7.2.min.js" ) )
                        .withJQueryUiJs( new JavaScriptResourceReference( Channels.class, "res/jquery-1.8.16-ui.min.js" ) )
                        .withJQueryUiCss( new CssResourceReference( Channels.class, "res/jquery-1.8.16-ui.css" ) );

        /*
        getComponentPreOnBeforeRenderListeners()
                .add( new JQComponentOnBeforeRenderListener( jqContributionConfig ) );
       */

        getComponentPreOnBeforeRenderListeners()
                .add( new JQComponentOnBeforeRenderListenerFix( jqContributionConfig ) );   // todo - remove temporary FIX when JQWicket issue #29 fixed

        getComponentInstantiationListeners().add( getInjector() );

        getMarkupSettings().setStripWicketTags( true );

        mountPage( "allChecklists", AllChecklistsPage.class );
        mountPage( "checklists", ChecklistsPage.class );
        mountPage( "model", ModelPage.class );
        mountPage( "admin", SettingsPage.class );
        mountPage( "nosops.html", NoAccessPage.class );
        mountPage( "login.html", LoginPage.class );
        mountPage( "newPasswordRequest.html", NewPasswordPage.class );
        mountPage( "segment.xml", ExportPage.class );
        mountPage( "geomap", GeoMapPage.class );
        mountPage( "home", HomePage.class );
        mountPage( "communities", CollaborationCommunitiesPage.class );
        mountPage( CollaborationCommunityPage.COMMUNITY, CollaborationCommunityPage.class );
        mountPage( "models", ModelsPage.class );
        mountPage( "feedback", FeedbackPage.class );
        mountPage( RFIsPage.SURVEYS, RFIsPage.class );
        mountPage( "requirements", RequirementsPage.class );
        mountPage( CollaborationCommunityPage.PARTICIPATION, CommunityParticipationPage.class );
        mountPage( "issues", IssuesPage.class );
        mountPage( "help", HelpPage.class );

        mountResource( "uploads/${name}", new UploadedReference() );

        mountResource( "users/photos/${name}", new PngReference(
                UserPhotoPng.class,
                getUserDao(),
                getModelManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "icons/${name}", new PngReference(
                IconPng.class,
                getUserDao(),
                getModelManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "segment.png", new PngReference(
                FlowMapPng.class,
                getUserDao(),
                getModelManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "model.png", new PngReference(
                ModelMapPng.class,
                getUserDao(),
                getModelManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "network.png", new PngReference(
                EntityNetworkPng.class,
                getUserDao(),
                getModelManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "entities.png", new PngReference(
                EntitiesNetworkPng.class,
                getUserDao(),
                getModelManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "hierarchy.png", new PngReference(
                HierarchyPng.class,
                getUserDao(),
                getModelManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "command_chains.png", new PngReference(
                CommandChainsPng.class,
                getUserDao(),
                getModelManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "essential.png", new PngReference(
                FailureImpactsPng.class,
                getUserDao(),
                getModelManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "dissemination.png", new PngReference(
                DisseminationPng.class,
                getUserDao(),
                getModelManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "checklist.png", new PngReference(
                ChecklistPng.class,
                getUserDao(),
                getModelManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "procedures.png", new PngReference(
                ProceduresPng.class,
                getUserDao(),
                getModelManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "required.png", new PngReference(
                RequiredNetworkingPng.class,
                getUserDao(),
                getModelManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );
        mountResource( "segment.png", new PngReference(
                FlowMapPng.class,
                getUserDao(),
                getModelManager(),
                getCommunityServiceFactory(),
                getPlanCommunityManager()
        ) );

        userUploadService.cleanUpPhotos();
        participationManager.registerAllFixedOrganizations();
    }

    @Override
    public void onApplicationEvent( ApplicationEvent event ) {
         if ( event instanceof AuthenticationSuccessEvent ) {
            AuthenticationSuccessEvent ae = (AuthenticationSuccessEvent) event;
            LOG.info( "login user={}", ae.getAuthentication().getPrincipal() );
         } else if ( event instanceof AuthenticationFailureBadCredentialsEvent ) {
             AuthenticationFailureBadCredentialsEvent ae = (AuthenticationFailureBadCredentialsEvent) event;
             Object principal = ( (Authentication) ae.getSource() ).getPrincipal();
             if ( userDao.getUserRecord( principal.toString() ) == null )
                 LOG.info( "Unknown user {}", principal );
             else
                 LOG.info( "Invalid password for user {}", principal );
         } else if ( event instanceof HttpSessionDestroyedEvent )
             LOG.info( "end of session" );
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

    public ModelManager getModelManager() {
        return modelManager;
    }

    public void setModelManager( ModelManager modelManager ) {
        this.modelManager = modelManager;
    }

    public CommunityServiceFactory getCommunityServiceFactory() {
        return communityServiceFactory;
    }

    public void setCommunityServiceFactory( CommunityServiceFactory communityServiceFactory ) {
        this.communityServiceFactory = communityServiceFactory;
    }

    public ModelServiceFactory getModelServiceFactory() {
        return modelServiceFactory;
    }

    public void setModelServiceFactory( ModelServiceFactory modelServiceFactory ) {
        this.modelServiceFactory = modelServiceFactory;
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

    public void setUserDao( UserRecordService userDao ) {
        this.userDao = userDao;
    }

    public UserRecordService getUserDao() {
        return userDao;
    }

    public PlanCommunityManager getPlanCommunityManager() {
        return planCommunityManager;
    }

    public void setPlanCommunityManager( PlanCommunityManager planCommunityManager ) {
        this.planCommunityManager = planCommunityManager;
    }

    public ParticipationManager getParticipationManager() {
        return participationManager;
    }

    public void setParticipationManager( ParticipationManager participationManager ) {
        this.participationManager = participationManager;
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
