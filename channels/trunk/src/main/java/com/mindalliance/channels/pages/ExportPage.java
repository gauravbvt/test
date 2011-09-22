package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.dao.Exporter;
import com.mindalliance.channels.core.dao.ImportExportFactory;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.PlanServiceFactory;
import com.mindalliance.channels.core.query.QueryService;
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
    private PlanServiceFactory planServiceFactory;

    @SpringBean
    private ImportExportFactory importExportFactory;

    public ExportPage( PageParameters parameters ) {
        super( parameters );

        QueryService queryService = getQueryService();
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
        return planServiceFactory.getService( plan );
    }

    private Exporter getExporter() {
        return importExportFactory.createExporter( "daemon", planManager.getDao( plan ) );
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
