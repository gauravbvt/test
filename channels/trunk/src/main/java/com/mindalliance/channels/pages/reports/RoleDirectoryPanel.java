package com.mindalliance.channels.pages.reports;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.IModel;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.pages.Project;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.text.Collator;

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
     * A role
     */
    private Role role;
    /**
     * An organization
     * Can be null
     */
    private Organization organization;

    public RoleDirectoryPanel( String id, IModel<Role> model, Organization organization ) {
        super( id, model );
        this.organization = organization;
        role = model.getObject();
        init();
    }

    private void init() {
        add( new Label( "name", role.getName() ) );
        add( new Label( "description", role.getDescription() ) );
        ResourceSpec resourceSpec = ResourceSpec.with( role );
        resourceSpec.setOrganization( organization );
        // Find all actors in role for organization
        List<Actor> actors = Project.service().findAllActors( resourceSpec );
        Collections.sort( actors, new Comparator<Actor>() {
            /** {@inheritDoc} */
            public int compare( Actor actor1, Actor actor2 ) {
                return Collator.getInstance().compare( actor1.getName(), actor2.getName() );
            }
        } );
        add( new ListView<Actor>( "actors", actors ) {
            protected void populateItem( ListItem<Actor> item ) {
                item.add( new ActorReportPanel( "actor", item.getModel() ) );
            }
        } );
    }
}
