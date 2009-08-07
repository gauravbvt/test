package com.mindalliance.channels.pages;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.export.ImportExportFactory;
import com.mindalliance.channels.model.Scenario;
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

        final QueryService queryService = getQueryService();
        if ( parameters.containsKey( PlanPage.SCENARIO_PARM ) )
            try {
                scenario = queryService.find( Scenario.class,
                        parameters.getLong( PlanPage.SCENARIO_PARM ) );

            } catch ( StringValueConversionException ignored ) {
                LOG.warn( "Bad scenario specified. Exporting default scenario.", ignored );
                scenario = queryService.getDefaultScenario();
            } catch ( NotFoundException ignored ) {
                LOG.warn( "Unknown scenario specified. Exporting default scenario.", ignored );
                scenario = queryService.getDefaultScenario();
            }
        else {
            LOG.warn( "No scenario specified. Exporting default scenario." );
            scenario = queryService.getDefaultScenario();
        }
    }

    private QueryService getQueryService() {
        return ( (Channels) getApplication() ).getQueryService();
    }

    private Exporter getExporter() {
        Channels channels = (Channels) getApplication();
        ImportExportFactory exportFactory = channels.getImportExportFactory();
        return exportFactory.createExporter(
                channels.getQueryService(),
                channels.getQueryService().getCurrentPlan() );
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
            getExporter().export( scenario, getResponse().getOutputStream() );
        } catch ( IOException e ) {
            LOG.error( "Export error", e );
        }
    }
}
