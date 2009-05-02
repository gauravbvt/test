package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Role;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.text.MessageFormat;

/**
 * Organization directory panel
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 9, 2009
 * Time: 11:37:11 AM
 */
public class OrganizationDirectoryPanel extends AbstractReportPanel {
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
        add( new Label( "name",                                                           // NON-NLS
                        MessageFormat.format( "Organization: {0}", organization.toString() ) ) );

        add( new Label( "description", organization.getDescription() ) );                 // NON-NLS

        add( new ListView<Role>( "roles",                                                 // NON-NLS
                                 getQueryService().findRolesIn( organization ) ) {
            @Override
            protected void populateItem( ListItem<Role> item ) {
                Role role = item.getModelObject();
                item.add( new RoleDirectoryPanel( "role",                                 // NON-NLS
                                                  new Model<Role>( role ), organization ) );
            }
        } );
    }
}
