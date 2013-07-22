package com.mindalliance.sb.mvc;

import com.mindalliance.sb.TimeKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Generic controller that allows downloading a list in various formats.
 */
public abstract class AbstractController<T> {

    @Autowired
    private TimeKeeper timeKeeper;

    @RequestMapping( "/data.csv" )
    public void csv( WebRequest request, HttpServletResponse response ) throws Exception {

        long lastModified = Math.max( getLastModified(), timeKeeper.getStartupTime() );

        if ( request.checkNotModified( lastModified ) ) {
            response.setStatus( HttpServletResponse.SC_NOT_MODIFIED );
            response.setDateHeader( "Date", System.currentTimeMillis() );
            response.setDateHeader( "Last-Modified", lastModified );
            response.setHeader( "Expires", null );
        }

        else
            new CsvWriter<T>( getList() ).output( response, getLastPath() + ".csv", lastModified );
    }

    protected abstract List<T> getList();

    protected long getLastModified() {
        return timeKeeper.getStartupTime();
    };

    private String getLastPath() {
        for ( Annotation annotation : getClass().getAnnotations() )
            if ( RequestMapping.class.isAssignableFrom( annotation.annotationType() ) ) {
                String firstPath = ((RequestMapping) annotation).value()[0];
                return firstPath.substring( firstPath.lastIndexOf( '/' ) + 1, firstPath.length() );
            }

        throw new RuntimeException( getClass().getName() + " has no request mapping" );
    }
}
