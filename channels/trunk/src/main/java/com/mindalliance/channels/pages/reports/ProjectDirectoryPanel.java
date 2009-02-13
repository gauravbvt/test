package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Organization;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Project directory
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 9, 2009
 * Time: 11:19:13 AM
 */
public class ProjectDirectoryPanel extends Panel {

    public ProjectDirectoryPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        List<Organization> topOrganizations = findTopOrganizations();
        add( new ListView<Organization>( "organizations", topOrganizations ) {
            protected void populateItem( ListItem<Organization> item ) {
                item.add( new OrganizationDirectoryPanel(
                        "organization",
                        new Model<Organization>( item.getModelObject() ) ) );
            }
        } );
        List<Role> roles = findRolesOutOfOrganization();
        Collections.sort( roles, new Comparator<Role>() {
            /** {@inheritDoc} */
            public int compare( Role role1, Role role2 ) {
                return Collator.getInstance().compare( role1.getName(), role2.getName() );
            }
        } );
        add( new ListView<Role>( "roles", roles ) {
            protected void populateItem( ListItem<Role> item ) {
                Role role = item.getModelObject();
                item.add( new RoleDirectoryPanel( "role", new Model<Role>( role ), null ) );
            }
        } );
    }

    private List<Organization> findTopOrganizations() {
        List<Organization> topOrgs = new ArrayList<Organization>();
        for ( Organization organization : Project.service().list( Organization.class ) ) {
            if ( organization.getParent() == null ) {
                topOrgs.add( organization );
            }
        }
        return topOrgs;
    }

    private List<Role> findRolesOutOfOrganization() {
        Set<Role> roles = new HashSet<Role>();
        for ( ResourceSpec resourceSpec : Project.service().findAllResourceSpecs() ) {
            if ( resourceSpec.getOrganization() == null ) {
                Role role = resourceSpec.getRole();
                if ( role != null ) roles.add( role );
            }
        }
        return new ArrayList<Role>( roles );
    }

}
