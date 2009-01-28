package com.mindalliance.channels.pages;

import com.mindalliance.channels.Dao;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.export.Exporter;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.graph.FlowDiagram;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.pages.entities.ActorPage;
import com.mindalliance.channels.pages.entities.OrganizationPage;
import com.mindalliance.channels.pages.entities.RolePage;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.value.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Application object for Channels.
 * Initialized in /WEB-INF/applicationContext.xml.
 */
public final class Project extends WebApplication {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( Project.class );
    /**
     * The 'expand' parameter in the URL.
     */
    public static final String EXPAND_PARM = "expand";                               // NON-NLS

    /**
     * The project's unique identifier
     */
    private String uri;

    /**
     * The manipulator of scenarios.
     */
    private Dao dao;
    /**
     * The builder of graphs (as data)
     */
    private GraphBuilder graphBuilder;

    /**
     * The creator of nifty diagrams.
     */
    private FlowDiagram flowDiagram;

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
     * Default Constructor.
     */
    public Project() {
    }

    /**
     * Set to strip wicket tags from subpanels.
     */
    @Override
    protected void init() {
        super.init();

        getMarkupSettings().setStripWicketTags( true );
//        getRequestCycleSettings().setRenderStrategy( IRequestCycleSettings.REDIRECT_TO_RENDER );
        mount( new QueryStringUrlCodingStrategy( "index.html", IndexPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "node.html", ScenarioPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "scenario.xml", ExportPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "scenario.png", FlowPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "role.html", RolePage.class ) );
        mount( new QueryStringUrlCodingStrategy( "actor.html", ActorPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "organization.html", OrganizationPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "resource.html", ProfilePage.class ) );
    }

    @Override
    public Class<IndexPage> getHomePage() {
        return IndexPage.class;
    }

    /**
     * @return the user name of the current user (or "Anonymous").
     */
    public static String getUserName() {
        String result = "Anonymous";
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( authentication != null ) {
            final Object obj = authentication.getPrincipal();
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

    public Dao getDao() {
        return dao;
    }

    public void setDao( Dao dao ) {
        this.dao = dao;
    }

    public FlowDiagram getFlowDiagram() {
        return flowDiagram;
    }

    public void setFlowDiagram( FlowDiagram fd ) {
        flowDiagram = fd;
    }

    public GraphBuilder getGraphBuilder() {
        return graphBuilder;
    }

    public void setGraphBuilder( GraphBuilder graphBuilder ) {
        this.graphBuilder = graphBuilder;
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

    /**
     * Gets the project's graph builder
     *
     * @return a GraphBuilder
     */
    public static GraphBuilder graphBuilder() {
        Project project = (Project) WebApplication.get();
        return project.getGraphBuilder();
    }

    public static Project getProject() {
        return (Project) get();
    }

    /**
     * Get current project's dao
     *
     * @return a Dao
     */
    public static Dao dao() {
        return getProject().getDao();
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
     * Get project's attachment manager
     *
     * @return an attachment manager
     */
    public static AttachmentManager attachmentManager() {
        return getProject().getAttachmentManager();
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

        /**
         * {@inheritDoc}
         */
        @Override
        protected void appendParameters( AppendingStringBuffer url, Map parameters ) {
            super.appendParameters( url, parameters );
            url.append( extension );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ValueMap decodeParameters( String urlFragment, Map urlParameters ) {
            return super.decodeParameters( trimmed( urlFragment ), urlParameters );
        }
    }

    /**
     * Find expansions in page parameters
     *
     * @param parameters page parameters
     * @return set of ids
     */
    public static Set<Long> findExpansions( PageParameters parameters ) {
        if ( parameters == null ) return new HashSet<Long>();
        final Set<Long> result = new HashSet<Long>( parameters.size() );
        if ( parameters.containsKey( Project.EXPAND_PARM ) ) {
            final List<String> stringList =
                    Arrays.asList( parameters.getStringArray( Project.EXPAND_PARM ) );
            for ( String id : stringList )
                try {
                    result.add( Long.valueOf( id ) );
                } catch ( NumberFormatException ignored ) {
                    LOG.warn( MessageFormat.format( "Invalid expansion parameter: {0}", id ) );
                }
        }

        return result;
    }

/*    public static PageParameters findPageParameters( Component component ) {
        WebPage page = component.findParent( WebPage.class );
        return ( page == null ) ? new PageParameters() : page.getPageParameters();
    }*/


}
