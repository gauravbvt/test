package com.mindalliance.channels.pages;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.dao.Exporter;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.ImportExportFactory;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.Plan;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Segment export stream page.
 */
public class ExportPage extends WebPage {

    /** The log. */
    private static final Logger LOG = LoggerFactory.getLogger( ExportPage.class );

    /** The segment to export. */
    private Segment segment;

    /** The plan manager. */
    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private Plan plan;

    @SpringBean
    private ImportExportFactory importExportFactory;

    public ExportPage( PageParameters parameters ) {
        super( parameters );

        final QueryService queryService = getQueryService();
        if ( parameters.containsKey( PlanPage.SEGMENT_PARM ) )
            try {
                segment = queryService.find( Segment.class,
                        parameters.getLong( PlanPage.SEGMENT_PARM ) );

            } catch ( StringValueConversionException ignored ) {
                LOG.warn( "Bad segment specified. Exporting default segment.", ignored );
                segment = queryService.getDefaultSegment();
            } catch ( NotFoundException ignored ) {
                LOG.warn( "Unknown segment specified. Exporting default segment.", ignored );
                segment = queryService.getDefaultSegment();
            }
        else {
            LOG.warn( "No segment specified. Exporting default segment." );
            segment = queryService.getDefaultSegment();
        }
    }

    private QueryService getQueryService() {
        return ( (Channels) getApplication() ).getQueryService();
    }

    private Exporter getExporter() {
        try {
            return importExportFactory.createExporter( planManager.getDao( plan ) );

        } catch ( NotFoundException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public String getMarkupType() {
        return getExporter().getMimeType();
    }

    /**
     * Generate and return the bytes for the segment.
     * @param markupStream the markup stream (ignored)
     */
    @Override
    protected void onRender( MarkupStream markupStream ) {
        try {
            getExporter().export( segment, getResponse().getOutputStream() );
        } catch ( IOException e ) {
            LOG.error( "Export error", e );
        }
    }
}
