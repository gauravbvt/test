package com.mindalliance.channels.core;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.CommunityServiceFactory;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/8/13
 * Time: 11:45 AM
 */
public class ModelObjectReference implements Serializable {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ModelObjectReference.class );

    public static final String SEPARATOR = "/";


    private String className;
    private String contextUri;
    private long id;

    public ModelObjectReference fromString( String s ) {
        String[] tokens =s.split( SEPARATOR );
        if ( tokens.length == 3 ) {
            ModelObjectReference moRef = new ModelObjectReference(  );
            moRef.setContextUri( tokens[0] );
            moRef.setClassName( tokens[1] );
            try {
            moRef.setId( Long.parseLong( tokens[2] ) );
            } catch ( NumberFormatException e ) {
                throw new IllegalArgumentException( "Invalid model object reference: " + s );
            }
            return moRef;
        } else {
            throw new IllegalArgumentException( "Invalid model object reference: " + s );
        }
    }

    ModelObject deref( PlanCommunityManager planCommunityManager,
                       CommunityServiceFactory communityServiceFactory ) {
        ModelObject mo = null;
        try {
            Class moClass = this.getClass().getClassLoader().loadClass( className );
            if ( !ModelObject.class.isAssignableFrom( moClass ) )
                throw new NotFoundException();
            PlanCommunity planCommunity = planCommunityManager.getPlanCommunity( contextUri );
            if ( planCommunity == null )
                throw new NotFoundException();
            CommunityService communityService = communityServiceFactory.getService( planCommunity );
            mo = communityService.find( moClass, id );
        } catch ( ClassNotFoundException e ) {
            LOG.warn( "Model object class not found: " + className );
        } catch ( NotFoundException e ) {
            LOG.warn( "Model object not found: " + this );
        }
        return mo;
    }

    public ModelObjectReference() {
    }

    public ModelObjectReference( ModelObject mo ) {
        className = mo.getClass().getName();
        contextUri = mo.getContextUri();
        id = mo.getId();
    }

    public String getClassName() {
        return className == null ? "" : className;
    }

    public void setClassName( String className ) {
        this.className = className;
    }

    public String getContextUri() {
        return contextUri == null ? "" : contextUri;
    }

    public void setContextUri( String contextUri ) {
        this.contextUri = contextUri;
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( getContextUri() )
                .append( SEPARATOR )
                .append( getClassName() )
                .append( SEPARATOR )
                .append( getId() );
        return sb.toString();
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof ModelObjectReference ) {
            ModelObjectReference other = (ModelObjectReference) obj;
            return getContextUri().equals( other.getContextUri() )
                    && getClassName().equals( other.getClassName() )
                    && getId() == other.getId();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + getContextUri().hashCode();
        hash = hash * 31 + getClassName().hashCode();
        hash = hash * 31 + Long.toString( getId() ).hashCode();
        return hash;
    }
}
