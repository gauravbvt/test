package com.mindalliance.channels.pages.entities;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.pages.ProfileLink;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.components.ModelObjectPanel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.LoggerFactory;

/**
 * Organization page
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 13, 2009
 * Time: 4:11:11 PM
 */
public class OrganizationPage extends WebPage {
    /**
     * The organization 'id' parameter in the URL.
     */
    static final String ID_PARM = "id";                                                   // NON-NLS

    public OrganizationPage( PageParameters parameters ) {
        super( parameters );
        try {
            init( parameters );
        } catch ( NotFoundException e ) {
            LoggerFactory.getLogger( getClass() ).error( "Organization not found", e );
        }
    }

    private void init( PageParameters parameters ) throws NotFoundException {
        // setVersioned( false );
        // setStatelessHint( true );
        final Organization organization = findOrganization( parameters );
        assert organization != null;
        add( new Label( "title", new Model<String>( "Actor: " + organization.getName() ) ) );
        add( new Label( "header-title", new PropertyModel<String>( organization, "name" ) ) );
        add ( new ExternalLink("index", "index.html"));
        add( new ModelObjectPanel( "organization-form", new Model<Organization>( organization ) ) );
        add( new ProfileLink( "profile-link",
                        new AbstractReadOnlyModel<ResourceSpec>() {
                            public ResourceSpec getObject() {
                                return ( ResourceSpec.with( organization )) ;
                            }
                        },
                        new AbstractReadOnlyModel<String>() {
                            public String getObject() {
                                return "View profile";
                            }
                        }
                )  );
    }

    private Organization findOrganization( PageParameters parameters ) throws NotFoundException {
        Organization organization = null;
        if ( parameters.containsKey( ID_PARM ) ) {
            final Service service = ( (Project) getApplication() ).getService();
            organization = service.find( Organization.class, parameters.getLong( ID_PARM ) );
        }
        return organization;
    }
}
