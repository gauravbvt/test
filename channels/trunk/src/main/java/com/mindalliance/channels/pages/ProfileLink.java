package com.mindalliance.channels.pages;

import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import com.mindalliance.channels.ResourceSpec;

/**
 * A link to the profile of all resources matching a resource specification
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 14, 2009
 * Time: 4:27:48 PM
 */
public class ProfileLink extends ExternalLink {

    public ProfileLink( String id, IModel<ResourceSpec> model ) {
        this( id, model, null );
    }

    public ProfileLink( String id, final IModel<ResourceSpec> model, IModel<String> s ) {
        super(
                id,
                new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        final ResourceSpec resourceSpec = model.getObject();
                        final String result;
                        result = linkFor( resourceSpec );
                        return result;
                    }
                },
                s
        );
    }

    private static String linkFor( ResourceSpec resourceSpec ) {
        if (resourceSpec.isEmpty()) return "#";
        StringBuilder sb = new StringBuilder();
        sb.append( "resource.html?" );
        if ( !resourceSpec.isAnyActor() ) {
            sb.append( "actor=" );
            sb.append( resourceSpec.getActor().getId() );
            sb.append( "&" );
        }
        if ( !resourceSpec.isAnyRole() ) {
            sb.append( "role=" );
            sb.append( resourceSpec.getRole().getId() );
            sb.append( "&" );
        }
        if ( !resourceSpec.isAnyOrganization() ) {
            sb.append( "organization=" );
            sb.append( resourceSpec.getOrganization().getId() );
            sb.append( "&" );
        }
        if ( !resourceSpec.isAnyJurisdiction() ) {
            sb.append( "jurisdiction=" );
            sb.append( resourceSpec.getJurisdiction().getId() );
        }
        return sb.toString();
    }
}
