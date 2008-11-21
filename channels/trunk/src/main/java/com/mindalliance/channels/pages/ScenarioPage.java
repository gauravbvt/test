package com.mindalliance.channels.pages;

import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.dao.ScenarioDao;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Scenario;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;

import java.util.Collections;
import java.util.List;

/**
 * The scenario editor page.
 */
public final class ScenarioPage extends WebPage {

    /** Class logger. */
    private static final Log Logger = LogFactory.getLog( ScenarioPage.class );

    private static final String SCENARIO_PARM = "scenario";

    public ScenarioPage() {
        init( getScenarioDao().getDefaultScenario() );
    }

    public ScenarioPage( PageParameters parameters ) {
        final ScenarioDao scenarioDao = getScenarioDao();
        Scenario scenario = scenarioDao.getDefaultScenario();

        if ( parameters.containsKey( SCENARIO_PARM ) )
            try {
                scenario = scenarioDao.findScenario( parameters.getLong( SCENARIO_PARM ) );
            } catch ( NotFoundException ignored ) {
                    Logger.warn( "Unknown scenario in parameter. Using default." );
            }

        init( scenario );
    }

    private void init( Scenario scenario ) {
        init( scenario, scenario.nodes().next() );
    }

    private void init( Scenario scenario, Node selected ) {
        final List<String> expanded = Collections.emptyList();
        init( scenario, selected, expanded );
    }

    private void init( Scenario scenario, Node selected, List<String> expanded ) {
    }

    /**
     * Get the scenario manager from project via the application context.
     * @return the scenario DAO
     */
    private ScenarioDao getScenarioDao() {
        return ( (Project) getApplication() ).getScenarioDao();
    }
}
