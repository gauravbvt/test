package com.mindalliance.channels.pages;

import com.mindalliance.channels.FlowDiagram;
import com.mindalliance.channels.dao.ScenarioDao;
import org.apache.wicket.protocol.http.WebApplication;

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
    private FlowDiagram flowDiagram;

    /**
     * Default Constructor.
     */
    public Project() {
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

    public FlowDiagram getFlowDiagram() {
        return flowDiagram;
    }

    public void setFlowDiagram( FlowDiagram flowDiagram ) {
        this.flowDiagram = flowDiagram;
    }
}
