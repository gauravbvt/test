package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.text.MessageFormat;
import java.util.List;

/**
 * A role directory for an organization
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 9, 2009
 * Time: 11:52:19 AM
 */
public class RoleDirectoryPanel extends Panel {

    /**
     * A role.
     */
    private Role role;

    /**
     * An organization.
     * Can be null
     */
    private Organization organization;

    public RoleDirectoryPanel( String id, IModel<Role> model, Organization organization ) {
        super( id, model );
        setRenderBodyOnly( true );
        this.organization = organization;
        role = model.getObject();
        init();
    }

    private void init() {
        add( new Label( "name", MessageFormat.format(                                     // NON-NLS
                "Role: {0}", role.getName() ) ) );

        String desc = role.getDescription();
        Label descLabel = new Label( "description", desc );                               // NON-NLS
        if ( desc == null || desc.isEmpty() )
            descLabel.setVisible( false );
        add( descLabel );

        // Find all actors in role for organization
        List<Actor> actors = Project.getProject().findActors( organization, role );
        if ( actors.isEmpty() )
            actors.add( new Actor( "(unknown)" ) );
        add( new ListView<Actor>( "actors", actors ) {                                    // NON-NLS
            @Override
            protected void populateItem( ListItem<Actor> item ) {
                item.add( new ActorReportPanel( "actor", item.getModel() ) );             // NON-NLS
            }
        } );
    }
}
