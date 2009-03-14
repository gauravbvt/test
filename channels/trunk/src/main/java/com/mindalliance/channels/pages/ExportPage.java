package com.mindalliance.channels.pages;

import com.mindalliance.channels.Service;
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

        final Service service = getService();
        if ( parameters.containsKey( ProjectPage.SCENARIO_PARM ) )
            try {
                scenario = service.find( Scenario.class,
                        parameters.getLong( ProjectPage.SCENARIO_PARM ) );

            } catch ( StringValueConversionException ignored ) {
                LOG.warn( "Bad scenario specified. Exporting default scenario.", ignored );
                scenario = service.getDefaultScenario();
            } catch ( NotFoundException ignored ) {
                LOG.warn( "Unknown scenario specified. Exporting default scenario.", ignored );
                scenario = service.getDefaultScenario();
            }
        else {
            LOG.warn( "No scenario specified. Exporting default scenario." );
            scenario = service.getDefaultScenario();
        }
    }

    private Service getService() {
        return ( (Project) getApplication() ).getService();
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
