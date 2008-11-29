package com.mindalliance.channels.pages;

import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.dao.ScenarioDao;
import com.mindalliance.channels.export.Exporter;
import com.mindalliance.channels.Scenario;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.wicket.PageParameters;
import org.apache.wicket.util.string.StringValueConversionException;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebPage;

import java.io.IOException;

/**
 * Scenario export stream page.
 */
public class ExportPage extends WebPage {

    /** The log. */
    private static final Log LOG = LogFactory.getLog( ExportPage.class );

    /** The scenario to export. */
    private Scenario scenario;

    public ExportPage( PageParameters parameters ) {
        super( parameters );

        final ScenarioDao scenarioDao = getScenarioDao();
        if ( parameters.containsKey( ScenarioPage.SCENARIO_PARM ) )
            try {
                scenario = scenarioDao.findScenario(
                        parameters.getLong( ScenarioPage.SCENARIO_PARM ) );

            } catch ( StringValueConversionException ignored ) {
                LOG.warn( "Bad scenario specified. Exporting default scenario.", ignored );
                scenario = scenarioDao.getDefaultScenario();
            } catch ( NotFoundException ignored ) {
                LOG.warn( "Unknown scenario specified. Exporting default scenario.", ignored );
                scenario = scenarioDao.getDefaultScenario();
            }
        else {
            LOG.warn( "No scenario specified. Exporting default scenario." );
            scenario = scenarioDao.getDefaultScenario();
        }
    }

    private ScenarioDao getScenarioDao() {
        return ( (Project) getApplication() ).getScenarioDao();
    }

    private Exporter getExporter() {
        return ( (Project) getApplication() ).getExporter();
    }

    @Override
    public String getMarkupType() {
        return "application/octet-stream";                                                // NON-NLS
    }

    /**
     * Generate and return the bytes for the scenario.
     * @param markupStream the markup stream (ignored)
     */
    @Override
    protected void onRender( MarkupStream markupStream ) {
        try {
            getExporter().exportScenario( scenario, getResponse().getOutputStream() );
        } catch ( IOException e ) {
            LOG.error( "Export error", e );
        }
    }
}
