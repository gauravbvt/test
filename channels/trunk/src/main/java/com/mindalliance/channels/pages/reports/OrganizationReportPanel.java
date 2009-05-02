package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 6, 2009
 * Time: 11:46:56 AM
 */
public class OrganizationReportPanel extends AbstractReportPanel {

    /**
     * An organization.
     */
    private Organization organization;

    /**
     * The scenario in context.
     */
    private Scenario scenario;

    public OrganizationReportPanel( String id, IModel<Organization> model, Scenario scenario ) {
        super( id, model );
        setRenderBodyOnly( true );
        organization = model.getObject();
        this.scenario = scenario;

        add( new ListView<Role>( "roles", scenario.findRoles( organization ) ) {          // NON-NLS
            @Override
            protected void populateItem( ListItem<Role> item ) {
                Role role = item.getModelObject();
                item.add( new RoleReportPanel( "role",                                    // NON-NLS
                                   new Model<Role>( role ),
                                   OrganizationReportPanel.this.scenario, organization ) );
            }
        } );
    }
}
