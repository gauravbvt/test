package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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
public class RoleReportPanel extends AbstractReportPanel {

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
        add( new Label( "org", organization.toString() ) );                               // NON-NLS

        add( new Label( "name", role.getName() ) );                                       // NON-NLS

        String desc = role.getDescription();
        Label descLabel = new Label( "description", desc );                               // NON-NLS
        descLabel.setVisible( desc != null && !desc.isEmpty() );
        add( descLabel );
        List<Actor> actors = getQueryService().findActors(
                organization, role, scenario );
        if ( actors.isEmpty() )
            actors.add( Actor.UNKNOWN );
        add( new ListView<Actor>( "actors", actors ) {                                    // NON-NLS
            @Override
            protected void populateItem( ListItem<Actor> item ) {
                Actor actor = item.getModelObject();
                ResourceSpec spec = new ResourceSpec();
                if ( !organization.equals( Organization.UNKNOWN ) )
                    spec.setOrganization( organization );
                if ( !role.equals( Role.UNKNOWN ) )
                    spec.setRole( role );
                if ( !actor.equals( Actor.UNKNOWN ) )
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
