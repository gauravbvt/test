package com.mindalliance.channels;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.pages.ExportPage;
import com.mindalliance.channels.pages.IndexPage;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.png.EntityNetworkPage;
import com.mindalliance.channels.pages.png.FlowMapPage;
import com.mindalliance.channels.pages.png.PlanMapPage;
import com.mindalliance.channels.pages.reports.PlanReportPage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.value.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Application object for Channels.
 * Initialized in /WEB-INF/applicationContext.xml.
 *
 * @TODO split into a bonified service-level object
 */
public final class Channels extends WebApplication {

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
     * The official manager of attachements.
     */
    private AttachmentManager attachmentManager;

    /**
     * Scenario importer.
     */
    private Importer importer;

    /**
     * Scenario exporter.
     */
    private Exporter exporter;

    /**
     * Analyst
     */
    private Analyst analyst;

    /**
     * One commander per plan
     */
    private Map<Plan, Commander> commanders = new HashMap<Plan, Commander>();
    /**
     * One lock manager per plan
     */
    private Map<Plan, LockManager> lockManagers = new HashMap<Plan, LockManager>();
    /**
     * Plans.
     */
    private List<Plan> plans = new ArrayList<Plan>();
    /**
     * Force this plan as the current one.
     */
    private Plan currentPlan;


    // TODO -- move to Plan

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

    public List<Plan> getPlans() {
        return plans;
    }

    /**
     * Plans set from application context only.
     * @param contextPlans a list of plans.
     */
    public void setPlans( List<Plan> contextPlans ) {
        plans = contextPlans;
    }

    /**
     * Set to strip wicket tags from subpanels.
     */
    @Override
    protected void init() {
        super.init();

        addComponentInstantiationListener( new SpringComponentInjector( this ) );

        getMarkupSettings().setStripWicketTags( true );
//        getRequestCycleSettings().setRenderStrategy( IRequestCycleSettings.REDIRECT_TO_RENDER );
        mount( new QueryStringUrlCodingStrategy( "index.html", IndexPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "report.html", PlanReportPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "node.html", PlanPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "scenario.xml", ExportPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "scenario.png", FlowMapPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "plan.png", PlanMapPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "network.png", EntityNetworkPage.class ) );

        queryService.initialize();
    }

    protected void onDestroy() {
        LOG.info( "Goodbye!" );
        queryService.onDestroy();
    }

    @Override
    public Class<PlanPage> getHomePage() {
        return PlanPage.class;
    }

    public QueryService getQueryService() {
        return queryService;
    }

    public void setQueryService( QueryService queryService ) {
        queryService.setChannels( this );
        queryService.getDao().setChannels( this );
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
        dm.setChannels( this );
        diagramFactory = dm;
    }

    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    public void setAttachmentManager( AttachmentManager attachmentManager ) {
        this.attachmentManager = attachmentManager;
    }

    public Exporter getExporter() {
        return exporter;
    }

    public void setExporter( Exporter exporter ) {
        exporter.setChannels( this );
        this.exporter = exporter;
    }

    public Importer getImporter() {
        return importer;
    }

    public void setImporter( Importer importer ) {
        importer.setChannels( this );
        this.importer = importer;
    }

    public Analyst getAnalyst() {
        return analyst;
    }

    public void setAnalyst( Analyst analyst ) {
        analyst.setChannels( this );
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
        Plan plan = getPlan();
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
        Plan plan = getPlan();
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

    /**
     * Get the active plan.
     *
     * @return a plan
     */
    public static Plan getPlan() {
        if ( instance().currentPlan != null ) {
            return instance().currentPlan;
        } else {
            // TODO - temporary - change to session-scoped plan set at login
            return instance().plans.get( 0 );
        }
    }

    /**
     * Enter plan loading mode.
     *
     * @param plan a plan
     */
    public void beginUsingPlan( Plan plan ) {
        // TODO - temporarily modify session-scoped plan
        assert ( User.current().isAnonymous() );
        currentPlan = plan;
    }

    /**
     * Exit plan loading mode.
     */
    public void endUsingPlan() {
        // TODO - reset session-scope plan
        assert ( User.current().isAnonymous() );
        assert ( currentPlan != null );
        currentPlan = null;
    }

    /**
     * Tests if a plan name is already taken.
     * @param name a string
     * @return a boolean
     */
    public boolean isPlanNameTaken( final String name ) {
        return CollectionUtils.find( plans, new Predicate() {
            public boolean evaluate( Object obj ) {
                return ( (Plan) obj ).getName().equals( name );
            }
        } ) != null;
    }


    //=========================================================================
    /**
     * URL coding strategy
     */
    private static final class CodingStrategy extends MixedParamUrlCodingStrategy {

        /**
         * The suffix for the link.
         */
        private String extension;

        private CodingStrategy(
                String mountPath, Class<?> bookmarkablePageClass, String[] parameterNames,
                String extension ) {
            super( mountPath, bookmarkablePageClass, parameterNames );
            this.extension = extension;
        }

        /**
         * Test if a path matches...
         *
         * @param path the path to match
         * @return true if matches, false otherwise
         */
        @Override
        public boolean matches( String path ) {
            return path.endsWith( extension )
                    && super.matches( trimmed( path ) );
        }

        private String trimmed( String path ) {
            return path.substring( 0, path.length() - extension.length() );
        }

        @Override
        protected void appendParameters( AppendingStringBuffer url, Map parameters ) {
            super.appendParameters( url, parameters );
            url.append( extension );
        }

        @Override
        protected ValueMap decodeParameters( String urlFragment, Map urlParameters ) {
            return super.decodeParameters( trimmed( urlFragment ), urlParameters );
        }


    }

}
