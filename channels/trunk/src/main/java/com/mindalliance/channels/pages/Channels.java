package com.mindalliance.channels.pages;

import com.google.code.jqwicket.JQComponentOnBeforeRenderListener;
import com.google.code.jqwicket.JQContributionConfig;
import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.CommanderFactory;
import com.mindalliance.channels.core.command.LockManager;
import com.mindalliance.channels.core.dao.ImportExportFactory;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanServiceFactory;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.geo.GeoService;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.pages.playbook.ContactPage;
import com.mindalliance.channels.pages.playbook.VCardPage;
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
import com.mindalliance.channels.pages.procedures.AssignmentReportPage;
import com.mindalliance.channels.pages.procedures.CommitmentReportPage;
import com.mindalliance.channels.pages.procedures.ProcedureMapPage;
import com.mindalliance.channels.pages.procedures.ProceduresReportPage;
import com.mindalliance.channels.pages.reports.guidelines.AllGuidelinesPage;
import com.mindalliance.channels.pages.reports.guidelines.GuidelinesPage;
import com.mindalliance.channels.pages.reports.infoNeeds.AllInfoNeedsPage;
import com.mindalliance.channels.pages.reports.infoNeeds.InfoNeedsPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
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
 * TODO split into a bonified service-level object
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

    public static final long UNKNOWN_FEEDBACK_ID = Long.MIN_VALUE;
    public static final long UNKNOWN_QUESTIONNAIRE_ID = Long.MIN_VALUE + 1;
    public static final Long UNKNOWN_RFI_SURVEY_ID = Long.MIN_VALUE + 2;
    public static final long UNKNOWN_RFI_ID = Long.MIN_VALUE + 3;

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

    private AttachmentManager attachmentManager;
    
    private ChannelsUserDao userDao;

    private Exception exception = null;

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
        ChannelsUser user = ChannelsUser.current( userDao );
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
        //getComponentPreOnBeforeRenderListeners().add( new JQComponentOnBeforeRenderListener() );
        getComponentPreOnBeforeRenderListeners().add( new JQComponentOnBeforeRenderListener(
                new JQContributionConfig().withDefaultJQueryUi() ) );

        getComponentInstantiationListeners().add( getInjector() );

        getMarkupSettings().setStripWicketTags( true );

        mountPage( "procedures", ProceduresReportPage.class );
        mountPage( "allGuidelines", AllGuidelinesPage.class );
        mountPage( "allInfoNeeds", AllInfoNeedsPage.class );
        mountPage( "guidelines", GuidelinesPage.class );
        mountPage( "infoNeeds", InfoNeedsPage.class );
        mountPage( "mapped", ProcedureMapPage.class );
        mountPage( "task", AssignmentReportPage.class );
        mountPage( "flow", CommitmentReportPage.class );
        mountPage( "vcards", VCardPage.class );
        mountPage( "contacts", ContactPage.class );
        mountPage( "plan", PlanPage.class );
        mountPage( "admin", AdminPage.class );
        mountPage( "nosops.html", NoAccessPage.class );
        mountPage( "login.html", LoginPage.class );
        mountPage( "newPasswordRequest.html", NewPasswordPage.class );
        mountPage( "segment.xml", ExportPage.class );
        mountPage( "geomap", GeoMapPage.class );
        mountPage( "home", UserPage.class );

        mountResource( "uploads/${name}", new PngReference( UploadedImage.class ) );
        mountResource( "icons/${name}", new PngReference( IconPng.class ) );
        mountResource( "segment.png", new PngReference( FlowMapPng.class ) );
        mountResource( "plan.png", new PngReference( PlanMapPng.class ) );
        mountResource( "network.png", new PngReference( EntityNetworkPng.class ) );
        mountResource( "entities.png", new PngReference( EntitiesNetworkPng.class ) );
        mountResource( "hierarchy.png", new PngReference( HierarchyPng.class ) );
        mountResource( "essential.png", new PngReference( FailureImpactsPng.class ) );
        mountResource( "dissemination.png", new PngReference( DisseminationPng.class ) );
        mountResource( "procedures.png", new PngReference( ProceduresPng.class ) );
        mountResource( "required.png", new PngReference( RequiredNetworkingPng.class ) );

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

    public PlanServiceFactory getPlanServiceFactory() {
        return planServiceFactory;
    }

    public void setPlanServiceFactory( PlanServiceFactory planServiceFactory ) {
        this.planServiceFactory = planServiceFactory;
    }

    public ImagingService getImagingService() {
        return imagingService;
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
}
