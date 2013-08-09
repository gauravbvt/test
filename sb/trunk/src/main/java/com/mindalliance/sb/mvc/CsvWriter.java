package com.mindalliance.sb.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

/**
 * CSV writer for objects of a class annotated with @JsonPropertyOrder. Only the properties specified, in the order
 * specified, will be generated.
 *
 * @param <T> the class
 * @see com.fasterxml.jackson.annotation.JsonPropertyOrder
 */
public class CsvWriter<T> {

    private final Collection<T> objects;

    private final FormatAdapterFactory<T> formatterFactory;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ", Locale.US );

    private static final Logger LOG = LoggerFactory.getLogger( CsvWriter.class );
    
    public CsvWriter( Collection<? extends T> objects, FormatAdapterFactory<T> formatterFactory ) {
        this.objects = Collections.unmodifiableCollection( objects );
        this.formatterFactory = formatterFactory;
    }

    public void output( HttpServletResponse response, String fileName, long timeInMillis ) throws Exception {

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            output( new PrintWriter( new OutputStreamWriter( buffer, Charset.forName( "UTF8" ) ) ), formatterFactory );

            response.setDateHeader( "Date", System.currentTimeMillis() );
            response.setDateHeader( "Last-Modified", timeInMillis );
            response.setHeader( "Expires", null );
            response.setHeader( "Content-type", "text/csv" );
            response.setHeader( "Content-Disposition", "attachment;filename=" + fileName );
            response.setContentLength( buffer.size() );
            response.getOutputStream().write( buffer.toByteArray() );
            response.setStatus( HttpServletResponse.SC_OK );
            
        } catch ( Exception e ) {
            // Provide a trace in the log for errors reported to the user...
            LOG.error( e.getClass().getSimpleName(), e );
            throw e;
        }
    }

    public void output( PrintWriter out, FormatAdapterFactory<T> formatterFactory ) {
        formatterFactory.outputCsv( out, objects );
    }
}
