package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.dao.Exporter;
import com.mindalliance.channels.core.dao.ImportExportFactory;
import com.mindalliance.channels.core.dao.ModelManager;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.ModelServiceFactory;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ResponseOutputStream;
import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
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
    private ModelManager modelManager;

    @SpringBean
    private CollaborationModel collaborationModel;

    @SpringBean
    private ModelServiceFactory modelServiceFactory;

    @SpringBean
    private ImportExportFactory importExportFactory;

    public ExportPage( PageParameters parameters ) {
        super( parameters );

        QueryService queryService = getQueryService();
        if ( parameters.getNamedKeys().contains( ModelPage.SEGMENT_PARM ) )
            try {
                segment = queryService.find( Segment.class,
                        parameters.get( ModelPage.SEGMENT_PARM ).toLong() );

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
        return modelServiceFactory.getService( collaborationModel );
    }

    private Exporter getExporter() {
        return importExportFactory.createExporter( "daemon", modelManager.getDao( collaborationModel ) );
    }

    @Override
    public MarkupType getMarkupType() {
        return getExporter().getMimeType();
    }

    /**
     * Generate and return the bytes for the segment.
     */
    @Override
    protected void onRender(  ) {
        try {
            getExporter().export( segment, new ResponseOutputStream( getResponse() ) );
        } catch ( IOException e ) {
            LOG.error( "Export error", e );
        }
    }
}
