package com.mindalliance.channels.pages.entities;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.pages.ProfileLink;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.components.EntityPanel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.LoggerFactory;

/**
 * Role page.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 7, 2009
 * Time: 12:50:01 PM
 */
public class RolePage extends WebPage {

    /**
     * The role 'id' parameter in the URL.
     */
    static final String ID_PARM = "id";                                                   // NON-NLS

    public RolePage( PageParameters parameters ) {
        super( parameters );
        try {
            init( parameters );
        } catch ( NotFoundException e ) {
            LoggerFactory.getLogger( getClass() ).error( "Role not found", e );
        }
    }

    private void init( PageParameters parameters ) throws NotFoundException {
        // setVersioned( false );
        // setStatelessHint( true );
        final Role role = findRole( parameters );
        assert role != null;
        add( new Label( "title", new Model<String>( "Role: " + role.getName() ) ) );
        add( new Label( "header-title", new PropertyModel<String>( role, "name" ) ) );
        add ( new ExternalLink("index", "index.html"));
        add( new EntityPanel( "role-form", new Model<Role>( role ) ) );
        add( new ProfileLink( "profile-link",
                        new AbstractReadOnlyModel<ResourceSpec>() {
                            public ResourceSpec getObject() {
                                return ( ResourceSpec.with( role )) ;
                            }
                        },
                        new AbstractReadOnlyModel<String>() {
                            public String getObject() {
                                return "View profile";
                            }
                        }
                )  );
    }

    private Role findRole( PageParameters parameters ) throws NotFoundException {
        Role role = null;
        if ( parameters.containsKey( ID_PARM ) ) {
            role = getService().find( Role.class, parameters.getLong( ID_PARM ) );
        }
        return role;
    }

    private Service getService() {
        return ( (Project) getApplication() ).getService();
    }


}
