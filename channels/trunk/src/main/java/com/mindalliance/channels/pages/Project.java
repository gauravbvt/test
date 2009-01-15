package com.mindalliance.channels.pages;

import com.mindalliance.channels.Dao;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.analysis.Issue;
import com.mindalliance.channels.analysis.ScenarioAnalyst;
import com.mindalliance.channels.analysis.profiling.Play;
import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.export.Exporter;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.graph.FlowDiagram;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.pages.profiles.ActorPage;
import com.mindalliance.channels.pages.profiles.OrganizationPage;
import com.mindalliance.channels.pages.profiles.ResourceSpecPage;
import com.mindalliance.channels.pages.profiles.RolePage;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
        mount( new QueryStringUrlCodingStrategy( "node.html", ScenarioPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "scenario.xml", ExportPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "scenario.png", FlowPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "role.html", RolePage.class ) );
        mount( new QueryStringUrlCodingStrategy( "actor.html", ActorPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "organization.html", OrganizationPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "resource.html", ResourceSpecPage.class ) );
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

    /**
     * Find all resources that equal or narrow given resource
     *
     * @param resource a resource
     * @return a list of implied resources
     */
    public List<ResourceSpec> findAllResourcesNarrowingOrEqualTo( ResourceSpec resource ) {
        return getDao().findAllResourcesNarrowingOrEqualTo( resource );
    }

    /**
     * Find all plays for the resource
     *
     * @param resource a resource
     * @return a list of plays
     */
    public List<Play> findAllPlays( ResourceSpec resource ) {
        return getDao().findAllPlays( resource );
    }

    /**
     * Find all issues related to any of the components of a resource
     *
     * @param resource a resource
     * @return a list of issues
     */
    public List<Issue> findAllIssuesFor( ResourceSpec resource ) {
        ScenarioAnalyst analyst = getScenarioAnalyst();
        List<Issue> issues = new ArrayList<Issue>();
        if ( !resource.isAnyActor() ) {
            issues.addAll( analyst.listIssues( resource.getActor(), true ) );
        }
        if ( !resource.isAnyOrganization() ) {
            issues.addAll( analyst.listIssues( resource.getOrganization(), true ) );
        }
        if ( !resource.isAnyRole() ) {
            issues.addAll( analyst.listIssues( resource.getRole(), true ) );
        }
        if ( !resource.isAnyJurisdiction() ) {
            issues.addAll( analyst.listIssues( resource.getJurisdiction(), true ) );
        }
        issues.addAll( findAllIssuesInPlays( resource ) );
        return issues;
    }

    /**
     * Find the issues on parts and flows for all plays of a resource
     *
     * @param resource a resource
     * @return a list of issues
     */
    private List<Issue> findAllIssuesInPlays( ResourceSpec resource ) {
        ScenarioAnalyst analyst = getScenarioAnalyst();
        List<Issue> issues = new ArrayList<Issue>();
        List<Play> plays = findAllPlays( resource );
        Set<Part> parts = new HashSet<Part>();
        for ( Play play : plays ) {
            parts.add( play.getPart() );
            Iterator<Issue> iterator = analyst.findIssues( play.getFlow(), true );
            while ( iterator.hasNext() ) issues.add( iterator.next() );
        }
        for ( Part part : parts ) {
            Iterator<Issue> iterator = analyst.findIssues( part, true );
            while ( iterator.hasNext() ) issues.add( iterator.next() );
        }
        return issues;
    }
}
