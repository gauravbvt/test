package com.mindalliance.channels.pages;

import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import com.mindalliance.channels.analysis.profiling.Resource;

/**
 * A link to a resource's profile
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 14, 2009
 * Time: 4:27:48 PM
 */
public class ResourceProfileLink extends ExternalLink {

    public ResourceProfileLink( String id, IModel<Resource> model ) {
        this( id, model, null );
    }

    public ResourceProfileLink( String id, final IModel<Resource> model, IModel<String> s ) {
        super(
                id,
                new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        final Resource resource = model.getObject();
                        final String result;
                        result = linkFor( resource );
                        return result;
                    }
                },
                s
        );
    }

    private static String linkFor( Resource resource ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "resource.html?" );
        if ( !resource.isAnyActor() ) {
            sb.append( "actor=" );
            sb.append( resource.getActor().getId() );
            sb.append( "&" );
        }
        if ( !resource.isAnyRole() ) {
            sb.append( "role=" );
            sb.append( resource.getRole().getId() );
            sb.append( "&" );
        }
        if ( !resource.isAnyOrganization() ) {
            sb.append( "organization=" );
            sb.append( resource.getOrganization().getId() );
            sb.append( "&" );
        }
        if ( !resource.isAnyJurisdiction() ) {
            sb.append( "jurisdiction=" );
            sb.append( resource.getJurisdiction().getId() );
        }
        return sb.toString();
    }
}
