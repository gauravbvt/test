package com.mindalliance.channels.pages;

import com.mindalliance.channels.Dao;
import com.mindalliance.channels.analysis.ScenarioAnalyst;
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
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.value.ValueMap;

import java.util.Map;

/**
 * Application object for Channels.
 * Initialized in /WEB-INF/applicationContext.xml.
 */
public final class Project extends WebApplication {

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
     * Scenario analyst
     */
    private ScenarioAnalyst scenarioAnalyst;

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
    public Class<ScenarioPage> getHomePage() {
        return ScenarioPage.class;
    }

    /**
     * @return the user name of the current user (or empty string for anonymous.
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

    public ScenarioAnalyst getScenarioAnalyst() {
        return scenarioAnalyst;
    }

    public void setScenarioAnalyst( ScenarioAnalyst scenarioAnalyst ) {
        this.scenarioAnalyst = scenarioAnalyst;
    }

    /**
     * Gets the project's graph builder
     * @return a GraphBuilder
     */
    public static GraphBuilder graphBuilder() {
        Project project = (Project) WebApplication.get();
        return project.getGraphBuilder();
    }

    public static Project getProject() {
        return (Project) get();
    }

    //=========================================================================
    private static class CodingStrategy extends MixedParamUrlCodingStrategy {

        /** The suffix for the link. */
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

        /** {@inheritDoc} */
        @Override
        protected void appendParameters( AppendingStringBuffer url, Map parameters ) {
            super.appendParameters( url, parameters );
            url.append( extension );
        }

        /** {@inheritDoc} */
        @Override
        protected ValueMap decodeParameters( String urlFragment, Map urlParameters ) {
            return super.decodeParameters( trimmed( urlFragment ), urlParameters );
        }
    }
}
