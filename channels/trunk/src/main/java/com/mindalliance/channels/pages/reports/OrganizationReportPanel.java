package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Scenario;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.text.MessageFormat;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 6, 2009
 * Time: 11:46:56 AM
 */
public class OrganizationReportPanel extends Panel {

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
        init();
    }

    private void init() {
        add( new Label( "name", MessageFormat.format(                                     // NON-NLS
                "Organization: {0}", organization.toString() ) ) );
        String desc = organization.getDescription();
        Label descLabel = new Label( "description", desc );                               // NON-NLS
        descLabel.setVisible( desc != null && !desc.isEmpty() );
        add( descLabel );
        add( new ListView<Role>( "roles",                                                 // NON-NLS
                                 scenario.findRoles( organization ) ) {
            @Override
            protected void populateItem( ListItem<Role> item ) {
                Role role = item.getModelObject();
                item.add( new RoleReportPanel( "role",                                    // NON-NLS
                                               new Model<Role>( role ), scenario, organization ) );
            }
        } );
    }
}
