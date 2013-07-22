package com.mindalliance.sb.mvc;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * CSV writer for objects of a class annotated with @JsonPropertyOrder. Only the properties specified, in the order
 * specified, will be generated.
 *
 * @param <T> the class
 * @see com.fasterxml.jackson.annotation.JsonPropertyOrder
 */
public class CsvWriter<T> {

    private final Class<T> clazz;

    private final Collection<T> objects;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ", Locale.US );

    private static final Logger LOG = LoggerFactory.getLogger( CsvWriter.class );

    public CsvWriter( Collection<T> objects ) {
        clazz = (Class<T>) ( objects.isEmpty() ? Object.class : objects.iterator().next().getClass() );
        this.objects = objects;
    }

    public void output( HttpServletResponse response, String fileName, long timeInMillis ) throws Exception {

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            output( new PrintWriter( new OutputStreamWriter( buffer, Charset.forName( "UTF8" ) ) ) );

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

    public void output( PrintWriter out )
        throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        Set<String> propertyNames = getOrdering();

        // Output headers
        boolean first = true;
        for ( String propertyName : propertyNames ) {
            if ( !first )
                out.print( ',' );
            first = false;
            out.print( propertyName );
        }
        out.println();

        // Output rows
        for ( T object : objects ) {
            first = true;
            for ( String propertyName : propertyNames ) {
                if ( !first )
                    out.print( ',' );
                first = false;

                PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor( clazz, propertyName );
                Method readMethod = propertyDescriptor.getReadMethod();
                Object value = readMethod.invoke( object );

                output( out, value );
            }
            out.println();
        }
        out.flush();
    }

    private static void output( PrintWriter out, Object object ) {
        if ( object != null ) {
            Class<?> objectClass = object.getClass();

            if ( Calendar.class.isAssignableFrom( objectClass ) )
                out.print( DATE_FORMAT.format( ( (Calendar) object ).getTime() ) );
            else if ( isQuotable( objectClass ) )
                outputQuoted( out, String.valueOf( object ) );
            else
                out.print( object );
        }
    }

    public static boolean isQuotable( Class<?> aClass ) {
        return String.class.isAssignableFrom( aClass ) || !BeanUtils.isSimpleProperty( aClass );
    }

    private static void outputQuoted( PrintWriter out, CharSequence chars ) {
        // TODO make this more efficient
        out.print( '"' );
        for ( int i = 0; i < chars.length(); i++ ) {
            char c = chars.charAt( i );
            if ( c == '"' )
                out.print( "\"\"" );
            else
                out.print( c );
        }
        out.print( '"' );
    }

    public Set<String> getOrdering() {
        Set<String> result = new LinkedHashSet<String>();

        for ( Annotation annotation : clazz.getAnnotations() )
            if ( JsonPropertyOrder.class.isAssignableFrom( annotation.annotationType() ) )
                Collections.addAll( result, ( (JsonPropertyOrder) annotation ).value() );

        return result;
    }
}
