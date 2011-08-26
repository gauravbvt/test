// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.core.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Utility for setting a system property to a servlet context resource path string.
 * This is a kludge for initializing the EhCache diskstore property to a path under
 * /WEB-INF...
 */
public class SystemPropertySetter implements InitializingBean {

    private String property;

    private Resource resource;

    public SystemPropertySetter() {
    }

    /**
     * Get the system property managed by this bean.
     * @return the property name
     */
    public String getProperty() {
        return property;
    }

    /**
     * Set the system property managed by this bean.
     * @param property the property name
     */
    public void setProperty( String property ) {
        this.property = property;
    }

    /**
     * Get the resource assigned to this bean.
     * @return the resource
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Set the resource assigned to this bean.
     * @param resource the resource.
     */
    public void setResource( Resource resource ) {
        this.resource = resource;
    }


    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied.
     */
    public void afterPropertiesSet() throws IOException {
        if ( property == null )
            throw new IllegalArgumentException( "Needs a value for property" );
        if ( resource == null )
            throw new IllegalArgumentException( "Needs a value for resource" );

        String path = resource.getFile().getAbsolutePath();
        LoggerFactory.getLogger( getClass() ).debug( "Setting {} to {}", property, path );
        System.setProperty( property, path );
    }
}
