package com.mindalliance.sb;

import org.springframework.security.web.util.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 *  Security filter based on client's Accept request header.
 */
public class AcceptMatcher implements RequestMatcher {

    private Set<String> mimeTypes = new HashSet<String>();
    
    @Override
    public boolean matches( HttpServletRequest request ) {
        Enumeration headers = request.getHeaders( "Accept" );
        while ( headers.hasMoreElements() ) {
            String header  = (String) headers.nextElement();
            for ( String mt : mimeTypes )
                if ( header.startsWith( mt ) )
                    return true;
        }
        
        return false;
    }

    public Set<String> getMimeTypes() {
        return Collections.unmodifiableSet( mimeTypes );
    }

    public void setMimeTypes( Set<String> mimeTypes ) {
        this.mimeTypes = mimeTypes;
    }
}
