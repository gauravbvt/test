package com.mindalliance.channels.pages;

import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.LockManager;
import com.mindalliance.channels.export.Exporter;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.pages.reports.ProjectReportPage;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.value.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.Map;

/**
 * Application object for Channels.
 * Initialized in /WEB-INF/applicationContext.xml.
 *
 * @TODO split into a bonified service-level object
 */
public final class Project extends WebApplication implements Identifiable {                                   // NON-NLS

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( Project.class );
    /**
     * Constant id for singleton project.
     */
    private static long PROJECT_ID = -1;
    /**
     * The project's unique identifier
     */
    private String uri;

    /**
     * The underlying service.
     */
    private DataQueryObject dqo;

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
     * Commander
     */
    private Commander commander;
    /**
     * Lock manager
     */
    private LockManager lockManager;

    private String projectName = "Untitled";

    private String client = "Unnamed";

    private String description = "";

    /**
     * Default Constructor.
     */
    public Project() {
    }

    public long getId() {
        return PROJECT_ID;
    }

    /**
     * Set to strip wicket tags from subpanels.
     */
    @Override
    protected void init() {
        super.init();

// TODO enable @Javabean in wicket components
//        addComponentInstantiationListener( new SpringComponentInjector( this ) );

        getMarkupSettings().setStripWicketTags( true );
//        getRequestCycleSettings().setRenderStrategy( IRequestCycleSettings.REDIRECT_TO_RENDER );
        mount( new QueryStringUrlCodingStrategy( "index.html", IndexPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "report.html", ProjectReportPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "node.html", ProjectPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "scenario.xml", ExportPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "scenario.png", FlowMapPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "plan.png", PlanMapPage.class ) );
        /*
        mount( new QueryStringUrlCodingStrategy( "role.html", RolePage.class ) );
        mount( new QueryStringUrlCodingStrategy( "actor.html", ActorPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "organization.html", OrganizationPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "resource.html", ProfilePage.class ) );
        */

        dqo.initialize();
    }

    protected void onDestroy() {
        LOG.info( "Goodbye!" );
        dqo.onDestroy();
    }

    @Override
    public Class<ProjectPage> getHomePage() {
        return ProjectPage.class;
    }

    /**
     * @return the user name of the current user (or "Anonymous").
     */
    public static String getUserName() {
        String result = "Anonymous";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( authentication != null ) {
            Object obj = authentication.getPrincipal();
            result = obj instanceof UserDetails ? ( (UserDetails) obj ).getUsername()
                    : obj.toString();
        }

        return result;
    }

    public String getUri() {
        return uri;
    }

    public void setUri( String uri ) {
        this.uri = uri;
    }

    public DataQueryObject getDqo() {
        return dqo;
    }

    public void setDqo( DataQueryObject dqo ) {
        this.dqo = dqo;
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
        this.exporter = exporter;
    }

    public Importer getImporter() {
        return importer;
    }

    public void setImporter( Importer importer ) {
        this.importer = importer;
    }

    public Analyst getAnalyst() {
        return analyst;
    }

    public void setAnalyst( Analyst analyst ) {
        this.analyst = analyst;
    }

    public Commander getCommander() {
        return commander;
    }

    public void setCommander( Commander commander ) {
        this.commander = commander;
    }

    public LockManager getLockManager() {
        return lockManager;
    }

    public void setLockManager( LockManager lockManager ) {
        this.lockManager = lockManager;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName( String name ) {
        projectName = name;
    }

    public String getClient() {
        return client;
    }

    public void setClient( String client ) {
        this.client = client;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public static Project getProject() {
        return (Project) get();
    }

    /**
     * Get current project's dao
     *
     * @return a Dao
     */
    public static DataQueryObject dqo() {
        return getProject().getDqo();
    }

    /**
     * Get current projects' analyst
     *
     * @return an analyst
     */
    public static Analyst analyst() {
        return getProject().getAnalyst();
    }

    /**
     * Get current project's diagram maker.
     *
     * @return a DiagramFactory
     */
    public static DiagramFactory diagramFactory() {
        return getProject().getDiagramFactory();
    }

    /**
     * Get project's attachment manager
     *
     * @return an attachment manager
     */
    public static AttachmentManager attachmentManager() {
        return getProject().getAttachmentManager();
    }

    /**
     * Get the project's commander
     *
     * @return a commander
     */
    public static Commander commander() {
        return getProject().getCommander();
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
