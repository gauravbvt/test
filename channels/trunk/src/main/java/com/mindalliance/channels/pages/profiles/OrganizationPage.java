package com.mindalliance.channels.pages.profiles;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.Model;
import org.slf4j.LoggerFactory;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.pages.components.ModelObjectPanel;
import com.mindalliance.channels.pages.components.PlaybookPanel;
import com.mindalliance.channels.pages.components.DirectoryPanel;
import com.mindalliance.channels.pages.Project;

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
        Organization organization = findOrganization( parameters );
        assert organization != null;
        add( new Label( "title", new Model<String>( "Actor: " + organization.getName() ) ) );
        add( new IssuesPanel<Organization>( "issues", new Model<Organization>( organization ) ) );
        add( new ModelObjectPanel( "organization-form", new Model<Organization>( organization ) ) );
        add( new PlaybookPanel( "playbook", new Model<Organization>( organization ) ) );
        add( new DirectoryPanel( "directory", new Model<Organization>( organization ) ) );
    }

    private Organization findOrganization( PageParameters parameters ) throws NotFoundException {
        Organization organization = null;
        if ( parameters.containsKey( ID_PARM ) ) {
            Dao dao = Project.getProject().getDao();
            organization = dao.findOrganization( parameters.getLong( ID_PARM ) );
        }
        return organization;
    }
}
