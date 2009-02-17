package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Organization;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

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
        setRenderBodyOnly( true );
        init();
    }

    private void init() {
        List<Organization> topOrganizations = findTopOrganizations();
        add( new ListView<Organization>( "organizations", topOrganizations ) {
            @Override
            protected void populateItem( ListItem<Organization> item ) {
                Organization organization = item.getModelObject();
                item.add( new AttributeModifier( "class", true, new Model<String>(
                        organization.getParent() == null
                                ? "top-organization"
                                : "sub-organization" ) ) );
                item.add(
                        new OrganizationDirectoryPanel(
                                "organization",
                                new Model<Organization>( organization ) ) );
            }
        } );

        List<Role> roles = findRolesOutOfOrganization();
        Collections.sort( roles, new Comparator<Role>() {
            /** {@inheritDoc} */
            public int compare( Role o1, Role o2 ) {
                return Collator.getInstance().compare( o1.getName(), o2.getName() );
            }
        } );
        add( new ListView<Role>( "roles", roles ) {
            @Override
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
        Service service = Project.service();
        List<Role> rolesWithoutOrg = new ArrayList<Role>();
        for ( Role role : service.list( Role.class ) ) {
            ResourceSpec roleSpec = ResourceSpec.with( role );
            boolean inOrganization = false;
            Iterator<ResourceSpec> roleSpecs = 
                    service.findAllResourcesNarrowingOrEqualTo( roleSpec ).iterator();
            while ( !inOrganization && roleSpecs.hasNext() ) {
                if ( !roleSpecs.next().isAnyOrganization() ) {
                    inOrganization = true;
                }
            }
            if ( !inOrganization ) {
                rolesWithoutOrg.add( role );
            }
        }
        return rolesWithoutOrg;
    }

}
