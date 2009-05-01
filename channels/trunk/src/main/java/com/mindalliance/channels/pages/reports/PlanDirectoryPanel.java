package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.Channels;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Plan directory
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 9, 2009
 * Time: 11:19:13 AM
 */
public class PlanDirectoryPanel extends Panel {

    public PlanDirectoryPanel( String id ) {
        super( id );
        setRenderBodyOnly( true );
        init();
    }

    private void init() {
        List<Organization> orgs = Channels.instance().getQueryService().findOrganizations();
        add( new ListView<Organization>( "organizations", orgs ) {                        // NON-NLS
            @Override
            protected void populateItem( ListItem<Organization> item ) {
                Organization organization = item.getModelObject();
                item.add( new AttributeModifier( "class", true, new Model<String>(        // NON-NLS
                        organization.getParent() == null
                                ? "top organization"                                      // NON-NLS
                                : "sub organization" ) ) );                               // NON-NLS
                item.add(
                        new OrganizationDirectoryPanel(
                                "organization",                                           // NON-NLS
                                new Model<Organization>( organization ) ) );
            }
        } );
    }

    private static List<Role> findRolesOutOfOrganization() {
        QueryService queryService = Channels.queryService();
        List<Role> rolesWithoutOrg = new ArrayList<Role>();
        for ( Role role : queryService.list( Role.class ) ) {
            ResourceSpec roleSpec = ResourceSpec.with( role );
            boolean inOrganization = false;
            Iterator<ResourceSpec> roleSpecs =
                    queryService.findAllResourcesNarrowingOrEqualTo( roleSpec ).iterator();
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
