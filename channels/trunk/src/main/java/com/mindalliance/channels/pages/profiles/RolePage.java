package com.mindalliance.channels.pages.profiles;

import com.mindalliance.channels.Dao;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.components.DirectoryPanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.pages.components.ModelObjectPanel;
import com.mindalliance.channels.pages.components.PlaybookPanel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.slf4j.LoggerFactory;

/**
 * Role profile page
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

    static final int PAGE_SIZE = 10;

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
        Role role = findRole( parameters );
        assert role != null;
        add( new Label( "title", new Model<String>( "Role: " + role.getName() ) ) );
        IssuesPanel issuesPanel = new IssuesPanel( "issues", new Model<ModelObject>( role ) );
        add( issuesPanel );
        ModelObjectPanel roleForm = new ModelObjectPanel( "role-form", new Model<Role>( role ) );
        add( roleForm );
        add( new PlaybookPanel( "playbook", new Model<Role>( role ) ) );
        add( new DirectoryPanel( "directory", new Model<Role>( role ) ) );
    }

    private Role findRole( PageParameters parameters ) throws NotFoundException {
        Role role = null;
        if ( parameters.containsKey( ID_PARM ) ) {
            Dao dao = Project.getProject().getDao();
            role = dao.findRole( parameters.getLong( ID_PARM ) );
        }
        return role;
    }
}
