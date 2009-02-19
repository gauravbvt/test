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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;
import java.util.Comparator;
import java.text.Collator;

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
        add( new Label( "name", "Organization: " + organization.toString() ) );
        add( new Label( "description", organization.getDescription() ) );

        List<Role> roles = findRolesInOrganization( organization );
        add( new ListView<Role>( "roles", roles ) {
            @Override
            protected void populateItem( ListItem<Role> item ) {
                Role role = item.getModelObject();
                item.add( new RoleDirectoryPanel( "role", new Model<Role>( role ), organization ) );
            }
        } );
    }

    private static List<Role> findRolesInOrganization( Organization organization ) {
        Set<Role> rolesInOrganization = new HashSet<Role>();
        List<ResourceSpec> allResourceSpecs = Project.service().findAllResourceSpecs();
        for ( ResourceSpec resourceSpec : allResourceSpecs ) {
            if ( resourceSpec.getOrganization() == organization ) {
                Role role = resourceSpec.getRole();
                if ( role != null ) rolesInOrganization.add( role );
            }
        }
        List<Role> list = new ArrayList<Role>( rolesInOrganization );
        Collections.sort( list, new Comparator<Role>() {
            /** {@inheritDoc} */
            public int compare( Role o1, Role o2 ) {
                return Collator.getInstance().compare( o1.getName(), o2.getName() );
            }
        } );
        return list;
    }
}
