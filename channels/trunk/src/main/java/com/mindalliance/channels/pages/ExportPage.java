package com.mindalliance.channels.pages;

import com.mindalliance.channels.Dao;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.export.Exporter;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.string.StringValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Scenario export stream page.
 */
public class ExportPage extends WebPage {

    /** The log. */
    private static final Logger LOG = LoggerFactory.getLogger( ExportPage.class );

    /** The scenario to export. */
    private Scenario scenario;

    public ExportPage( PageParameters parameters ) {
        super( parameters );

        final Dao dao = getScenarioDao();
        if ( parameters.containsKey( ScenarioPage.SCENARIO_PARM ) )
            try {
                scenario = dao.findScenario(
                        parameters.getLong( ScenarioPage.SCENARIO_PARM ) );

            } catch ( StringValueConversionException ignored ) {
                LOG.warn( "Bad scenario specified. Exporting default scenario.", ignored );
                scenario = dao.getDefaultScenario();
            } catch ( NotFoundException ignored ) {
                LOG.warn( "Unknown scenario specified. Exporting default scenario.", ignored );
                scenario = dao.getDefaultScenario();
            }
        else {
            LOG.warn( "No scenario specified. Exporting default scenario." );
            scenario = dao.getDefaultScenario();
        }
    }

    private Dao getScenarioDao() {
        return ( (Project) getApplication() ).getDao();
    }

    private Exporter getExporter() {
        return ( (Project) getApplication() ).getExporter();
    }

    @Override
    public String getMarkupType() {
        return getExporter().getMimeType();                                                
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
