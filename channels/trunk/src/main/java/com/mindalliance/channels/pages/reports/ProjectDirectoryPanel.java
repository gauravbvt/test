package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Organization;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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
        List<Organization> orgs = findOrganizations();
        add( new ListView<Organization>( "organizations", orgs ) {                        // NON-NLS
            @Override
            protected void populateItem( ListItem<Organization> item ) {
                Organization organization = item.getModelObject();
                item.add( new AttributeModifier( "class", true, new Model<String>(        // NON-NLS
                        organization.getParent() == null
                                ? "top-organization"                                      // NON-NLS
                                : "sub-organization" ) ) );                               // NON-NLS
                item.add(
                        new OrganizationDirectoryPanel(
                                "organization",                                           // NON-NLS
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
        WebMarkupContainer wmc = new WebMarkupContainer( "no-org" );                      // NON-NLS
        wmc.add( new ListView<Role>( "roles", roles ) {                                   // NON-NLS
            @Override
            protected void populateItem( ListItem<Role> item ) {
                Role role = item.getModelObject();
                item.add( new RoleDirectoryPanel( "role",                                 // NON-NLS
                                                  new Model<Role>( role ), null ) );
            }
        } );
        wmc.setVisible( !roles.isEmpty() );
        add( wmc );
    }

    private static List<Organization> findOrganizations() {
        List<Organization> orgs = new ArrayList<Organization>(
                new HashSet<Organization>( Project.service().list( Organization.class ) ) );

        Collections.sort( orgs, new Comparator<Organization>() {
            /** {@inheritDoc} */
            public int compare( Organization o1, Organization o2 ) {
                return Collator.getInstance().compare( o1.toString(), o2.toString() );
            }
        } );

        return orgs;
    }

    private static List<Role> findRolesOutOfOrganization() {
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
