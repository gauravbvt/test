package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Organization;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Organization directory panel
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 9, 2009
 * Time: 11:37:11 AM
 */
public class OrganizationDirectoryPanel extends Panel {
    /**
     * An organization
     */
    private Organization organization;

    public OrganizationDirectoryPanel( String id, IModel<Organization> model ) {
        super( id, model );
        organization = model.getObject();
        setRenderBodyOnly( true );
        init();
    }

    private void init() {
        add( new Label( "name", organization.getName() ) );
        add( new Label( "description", organization.getDescription() ) );
        Label parentage = new Label( "parentage", organization.parentage() );
        if ( organization.getParent() == null )
            parentage.setVisible( false );
        add( parentage );
        List<Role> roles = findRolesInOrganization();
        Collections.sort( roles, new Comparator<Role>() {
            /** {@inheritDoc} */
            public int compare( Role role1, Role role2 ) {
                return Collator.getInstance().compare( role1.getName(), role2.getName() );
            }
        } );
        add( new ListView<Role>( "roles", roles ) {
            @Override
            protected void populateItem( ListItem<Role> item ) {
                Role role = item.getModelObject();
                item.add( new RoleDirectoryPanel( "role", new Model<Role>( role ), organization ) );
            }
        } );

        List<Organization> subOrganizations = findSubOrganizations();
        add( new ListView<Organization>( "sub-organizations", subOrganizations ) {
            @Override
            protected void populateItem( ListItem<Organization> item ) {
                Organization subOrganization = item.getModelObject();
                item.add( new OrganizationDirectoryPanel(
                        "sub-organization",
                        new Model<Organization>( subOrganization ) ) );
            }
        } );
    }

    private List<Organization> findSubOrganizations() {
        List<Organization> subOrgs = new ArrayList<Organization>();
        for ( Organization organization : Project.service().list( Organization.class ) ) {
            if ( organization.getParent() == this.organization ) {
                subOrgs.add( organization );
            }
        }
        return subOrgs;
    }

    private List<Role> findRolesInOrganization() {
        Set<Role> rolesInOrganization = new HashSet<Role>();
        List<ResourceSpec> allResourceSpecs = Project.service().findAllResourceSpecs();
        for ( ResourceSpec resourceSpec : allResourceSpecs ) {
            if ( resourceSpec.getOrganization() == organization ) {
                Role role = resourceSpec.getRole();
                if ( role != null ) rolesInOrganization.add( role );
            }
        }
        return new ArrayList<Role>( rolesInOrganization );
    }
}
