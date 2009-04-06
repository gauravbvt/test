package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Role report panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 5, 2009
 * Time: 8:10:03 PM
 */
public class RoleReportPanel extends Panel {

    /** A role. */
    private Role role;

    /** The scenario. */
    private Scenario scenario;

    /** The organization. */
    private Organization organization;

    public RoleReportPanel(
            String id, IModel<Role> model, Scenario scenario, Organization organization ) {

        super( id, model );
        setRenderBodyOnly( true );
        role = model.getObject();
        this.scenario = scenario;
        this.organization = organization;
        init();
    }

    private void init() {
        add( new Label( "sc-name", scenario.getName() ) );                                // NON-NLS
        add( new Label( "sc-description", scenario.getDescription() ) );                  // NON-NLS
        add( new Label( "org", organization.getName() ) );                                // NON-NLS

        add( new Label( "name", role.getName() ) );                                       // NON-NLS

        String desc = role.getDescription();
        Label descLabel = new Label( "description", desc );                               // NON-NLS
        descLabel.setVisible( desc != null && !desc.isEmpty() );
        add( descLabel );
        List<Actor> actors = Project.getProject().getDqo().findActors( organization, role, scenario );
        if ( actors.isEmpty() )
            actors.add( Actor.UNKNOWN );
        add( new ListView<Actor>( "actors", actors ) {                                    // NON-NLS
            @Override
            protected void populateItem( ListItem<Actor> item ) {
                Actor actor = item.getModelObject();
                ResourceSpec spec = new ResourceSpec();
                if ( actor.equals( Actor.UNKNOWN ) ) {
                    spec.setOrganization( organization );
                    spec.setRole( role );
                } else
                    spec.setActor( actor );
                item.add( new ActorReportPanel( "actor", scenario, spec, false ) );       // NON-NLS
            }
        } );
        add( new ListView<Part>( "parts", scenario.findParts( organization, role ) ) {    // NON-NLS
            @Override
            protected void populateItem( ListItem<Part> item ) {
                item.add( new PartReportPanel( "part", item.getModel() ) );               // NON-NLS
            }
        } );
    }
}
