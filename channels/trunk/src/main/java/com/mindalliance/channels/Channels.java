package com.mindalliance.channels;

import com.mindalliance.channels.export.ImportExportFactory;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.pages.AdminPage;
import com.mindalliance.channels.pages.ErrorPage;
import com.mindalliance.channels.pages.ExpiredPage;
import com.mindalliance.channels.pages.ExportPage;
import com.mindalliance.channels.pages.GeoMapPage;
import com.mindalliance.channels.pages.LoginPage;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.UploadPage;
import com.mindalliance.channels.pages.playbook.ContactPage;
import com.mindalliance.channels.pages.playbook.TaskPlaybook;
import com.mindalliance.channels.pages.playbook.VCardPage;
import com.mindalliance.channels.pages.png.EntityNetworkPage;
import com.mindalliance.channels.pages.png.FlowMapPage;
import com.mindalliance.channels.pages.png.HierarchyPage;
import com.mindalliance.channels.pages.png.PlanMapPage;
import com.mindalliance.channels.pages.reports.PlanReportPage;
import org.acegisecurity.Authentication;
import org.acegisecurity.concurrent.SessionIdentifierAware;
import org.acegisecurity.event.authentication.AbstractAuthenticationEvent;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.IndexedParamUrlCodingStrategy;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Application object for Channels.
 * Initialized in /WEB-INF/applicationContext.xml.
 *
 * @TODO split into a bonified service-level object
 */
public final class Channels extends WebApplication implements ApplicationListener {

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
     * Scenario importer.
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

    /**
     * One commander per plan
     */
    private Map<Plan, Commander> commanders = new HashMap<Plan, Commander>();
    /**
     * One lock manager per plan
     */
    private Map<Plan, LockManager> lockManagers = new HashMap<Plan, LockManager>();

    /**
     * Default Constructor.
     */
    public Channels() {
    }

    private Map<Plan, Commander> getCommanders() {
        return commanders;
    }

    private Map<Plan, LockManager> getLockManagers() {
        return lockManagers;
    }// TODO - get rid of this

    /**
     * Set to strip wicket tags from subpanels.
     */
    @Override
    protected void init() {
        super.init();

        addComponentInstantiationListener( new SpringComponentInjector( this ) );

        getMarkupSettings().setStripWicketTags( true );

        String[] parameterNames = { "0", "1" };
        mount( new MixedParamUrlCodingStrategy( "report", PlanReportPage.class, parameterNames ) );
        
        mount( new IndexedParamUrlCodingStrategy( "playbooks", TaskPlaybook.class ) );
        mount( new IndexedParamUrlCodingStrategy( "vcards", VCardPage.class ) );
        mount( new IndexedParamUrlCodingStrategy( "contacts", ContactPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "plan", PlanPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "admin", AdminPage.class ) );

        mount( new IndexedParamUrlCodingStrategy( "uploads", UploadPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "login.html", LoginPage.class ) );

        mount( new QueryStringUrlCodingStrategy( "scenario.xml", ExportPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "scenario.png", FlowMapPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "plan.png", PlanMapPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "network.png", EntityNetworkPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "hierarchy.png", HierarchyPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "geomap.html", GeoMapPage.class ) );

        getApplicationSettings().setInternalErrorPage( ErrorPage.class );
        getApplicationSettings().setPageExpiredErrorPage( ExpiredPage.class );

    }

    @Override
    protected void onDestroy() {
        LOG.info( "Goodbye!" );
        queryService.onDestroy();
        analyst.onDestroy();
    }

    /**
     *
     */
    @Override
    public Class<? extends WebPage> getHomePage() {
        User user = User.current();
        return user.isAdmin() ? AdminPage.class
             : user.isPlanner() ? PlanPage.class : PlanReportPage.class ;
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
            return (DiagramFactory) getApplicationContext().getBean( "diagramFactory" );
        }
    }

    private ApplicationContext getApplicationContext() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext( getServletContext() );
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
     * Get the active plan's commander
     *
     * @return a commander
     */
    public Commander getCommander() {
        Plan plan = queryService.getCurrentPlan();
        return instance().getCommander( plan );
    }

    /**
     * Get the active plan's commander
     *
     * @param plan a plan
     * @return a commander
     */
    public Commander getCommander( Plan plan ) {
        Map<Plan, Commander> commanders = getCommanders();
        synchronized ( commanders ) {
            Commander commander = commanders.get( plan );
            if ( commander == null ) {
                commander = (Commander) getApplicationContext().getBean( "commander" );
                commander.setLockManager( Channels.instance().getLockManager() );
                commanders.put( plan, commander );
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
        return instance().getLockManager( plan );
    }

    /**
     * Get the active plan's lock manager
     *
     * @param plan a plan
     * @return a lock manager
     */
    public LockManager getLockManager( Plan plan ) {
        Map<Plan, LockManager> lockManagers = getLockManagers();
        synchronized ( lockManagers ) {
            LockManager lockManager = lockManagers.get( plan );
            if ( lockManager == null ) {
                lockManager = (LockManager) getApplicationContext().getBean( "lockManager" );
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
}
