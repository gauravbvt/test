package com.mindalliance.channels;

import org.apache.wicket.protocol.http.WebApplication;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 */
public class Channels extends WebApplication {

    private ScenarioDao scenarioDao;
    private FlowDiagram flowDiagram;

    /**
     * Constructor
     */
	public Channels()
	{
	}

	@Override
    public Class<ScenarioPage> getHomePage()
	{
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
