package com.mindalliance.channels.pages;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.dao.ScenarioDao;
import com.mindalliance.channels.export.Exporter;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.graph.FlowDiagram;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.analysis.ScenarioAnalyst;
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
    private ScenarioDao scenarioDao;

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
        mount( new QueryStringUrlCodingStrategy( "scenario.bin", ExportPage.class ) );
    }

    @Override
    public Class<ScenarioPage> getHomePage() {
        return ScenarioPage.class;
    }

    public ScenarioDao getScenarioDao() {
        return scenarioDao;
    }

    public void setScenarioDao( ScenarioDao scenarioDao ) {
        this.scenarioDao = scenarioDao;
    }

    public FlowDiagram<Node,Flow> getFlowDiagram() {
        return flowDiagram;
    }

    public void setFlowDiagram( FlowDiagram<Node,Flow> flowDiagram ) {
        this.flowDiagram = flowDiagram;
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
}
