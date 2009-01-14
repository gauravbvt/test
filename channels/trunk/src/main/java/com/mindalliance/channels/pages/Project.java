package com.mindalliance.channels.pages;

import com.mindalliance.channels.Dao;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Player;
import com.mindalliance.channels.Resourceable;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.analysis.ScenarioAnalyst;
import com.mindalliance.channels.analysis.Issue;
import com.mindalliance.channels.analysis.profiling.Play;
import com.mindalliance.channels.analysis.profiling.Resource;
import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.export.Exporter;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.graph.FlowDiagram;
import com.mindalliance.channels.graph.GraphBuilder;
import com.mindalliance.channels.pages.profiles.ActorPage;
import com.mindalliance.channels.pages.profiles.OrganizationPage;
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

    public static GraphBuilder graphBuilder() {
        Project project = (Project) WebApplication.get();
        return project.getGraphBuilder();
    }

    public static Project getProject() {
        return (Project) get();
    }

    /**
     * Find all resources implied by resourceable
     *
     * @param resourceable a resourceable
     * @return a list of resources
     */
    public List<Resource> findAllResourcesFor( Resourceable resourceable ) {
        Set<Resource> set = new HashSet<Resource>();
        Iterator<Scenario> scenarios = getDao().scenarios();
        while ( scenarios.hasNext() ) {
            Scenario scenario = scenarios.next();
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( part.involves( resourceable ) ) {
                    Resource resource = new Resource( part );
                    // Find all channels used to communicate with this part
                    Iterator<Flow> flows = scenario.flows();
                    while ( flows.hasNext() ) {
                        Flow flow = flows.next();
                        if ( flow.getChannel() != null && !flow.getChannel().isEmpty() ) {
                            if ( flow.getTarget() == part && !flow.isAskedFor() ) {
                                resource.addChannel( flow.getChannel() );
                            }
                            if ( flow.getSource() == part && flow.isAskedFor() ) {
                                resource.addChannel( flow.getChannel() );
                            }
                        }
                    }
                    set.add( resource );
                }
            }
        }
        List<Resource> list = new ArrayList<Resource>();
        list.addAll( set );
        return list;
    }

    /**
     * Find all plays for the player
     *
     * @param player a player
     * @return a list of plays
     */
    public List<Play> findAllPlaysFor( Player player ) {
        List<Play> list = new ArrayList<Play>();
        Iterator<Scenario> scenarios = getDao().scenarios();
        while ( scenarios.hasNext() ) {
            Scenario scenario = scenarios.next();
            Iterator<Flow> flows = scenario.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                if ( Play.hasPlay( flow ) ) {
                    if ( flow.getSource().isPart() && ( (Part) flow.getSource() ).involves( player ) ) {
                        // role sends
                        Play play = new Play( (Part) flow.getSource(), flow, true );
                        list.add( play );
                    }
                    if ( flow.getTarget().isPart() && ( (Part) flow.getTarget() ).involves( player ) ) {
                        // role receives
                        Play play = new Play( (Part) flow.getTarget(), flow, false );
                        list.add( play );
                    }
                }
            }
        }
        return list;
    }

    /**
     * Find all issues related to a model object
     *
     * @param modelObject a model object
     * @return a list of issues
     */
    public List<Issue> findAllIssuesFor( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Iterator<Issue> iterator = getScenarioAnalyst().findIssues( modelObject, true );
        while ( iterator.hasNext() ) issues.add( iterator.next() );
        if ( modelObject instanceof Player ) {
            issues.addAll( findAllIssuesForPlayer( (Player) modelObject ) );
        }
        // TODO  -- other cases
        return issues;
    }

    /**
     * Find the issues on parts and flows for all plays of a player
     *
     * @param player a player
     * @return a list of issues
     */
    private List<Issue> findAllIssuesForPlayer( Player player ) {
        ScenarioAnalyst analyst = getScenarioAnalyst();
        List<Issue> issues = new ArrayList<Issue>();
        List<Play> plays = findAllPlaysFor( player );
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
