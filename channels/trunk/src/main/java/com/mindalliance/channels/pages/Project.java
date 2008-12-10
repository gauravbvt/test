package com.mindalliance.channels.pages;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.analysis.ScenarioAnalyst;
import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.export.Exporter;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.graph.FlowDiagram;
import com.mindalliance.channels.graph.GraphBuilder;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;

/**
 * Application object for Channels.
 * Initialized in /WEB-INF/applicationContext.xml.
 */
public final class Project extends WebApplication {

    /**
     * The manipulator of scenarios.
     */
    private Dao dao;

    private GraphBuilder graphBuilder;

    /**
     * The creator of nifty diagrams.
     */
    private FlowDiagram<Node,Flow> flowDiagram;

    /**
     * The official manager of attachements.
     */
    private AttachmentManager attachmentManager;

    /** Scenario importer. */
    private Importer importer;

    /** Scenario exporter. */
    private Exporter exporter;

    /** Scenario analyst */
    private ScenarioAnalyst scenarioAnalyst;

    /**
     * Default Constructor.
     */
    public Project() {
    }

    /** Set to strip wicket tags from subpanels. */
    @Override
    protected void init() {
        super.init();

        getMarkupSettings().setStripWicketTags( true );
//        getRequestCycleSettings().setRenderStrategy( IRequestCycleSettings.REDIRECT_TO_RENDER );
        mount( new QueryStringUrlCodingStrategy( "node.html", ScenarioPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "scenario.bin", ExportPage.class ) );
        mount( new QueryStringUrlCodingStrategy( "scenario.png", FlowPage.class ) );
    }

    @Override
    public Class<ScenarioPage> getHomePage() {
        return ScenarioPage.class;
    }

    public Dao getDao() {
        return dao;
    }

    public void setDao( Dao dao ) {
        this.dao = dao;
    }

    public FlowDiagram<Node,Flow> getFlowDiagram() {
        return flowDiagram;
    }

    public void setFlowDiagram( FlowDiagram<Node, Flow> fd ) {
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
        Project project = (Project)WebApplication.get();
        return project.getGraphBuilder();
    }
}
