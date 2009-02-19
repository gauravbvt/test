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

import java.text.Collator;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
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
        add( new Label( "name", MessageFormat.format(                                     // NON-NLS
                "Role: {0}", role.getName() ) ) );

        String desc = role.getDescription();
        Label descLabel = new Label( "description", desc );                               // NON-NLS
        descLabel.setVisible( desc != null && !desc.isEmpty() );
        add( descLabel );
        add( new ListView<Actor>( "actors", findActors( organization, role ) ) {          // NON-NLS
            @Override
            protected void populateItem( ListItem<Actor> item ) {
                item.add( new ActorReportPanel( "actor", item.getModel() ) );             // NON-NLS
            }
        } );
        add( new ListView<Part>( "parts", scenario.findParts( organization, role ) ) {                                       // NON-NLS
            @Override
            protected void populateItem( ListItem<Part> item ) {
                item.add( new PartReportPanel( "part", item.getModel() ) );               // NON-NLS
            }
        } );
    }

    private static List<Actor> findActors( Organization organization, Role role ) {
        ResourceSpec resourceSpec = ResourceSpec.with( role );
        resourceSpec.setOrganization( organization );
        // Find all actors in role for organization
        List<Actor> actors = Project.service().findAllActors( resourceSpec );
        Collections.sort( actors, new Comparator<Actor>() {
            /** {@inheritDoc} */
            public int compare( Actor o1, Actor o2 ) {
                return Collator.getInstance().compare( o1.getName(), o2.getName() );
            }
        } );
        if ( actors.isEmpty() )
            actors.add( new Actor( "(unknown)" ) );
        return actors;
    }
}
