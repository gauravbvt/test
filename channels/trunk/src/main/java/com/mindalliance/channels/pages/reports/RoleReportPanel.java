package com.mindalliance.channels.pages.reports;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.IModel;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.util.SemMatch;
import com.mindalliance.channels.pages.Project;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ArrayList;
import java.text.Collator;

/**
 * Role report panel
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 5, 2009
 * Time: 8:10:03 PM
 */
public class RoleReportPanel extends Panel {
    /**
     * A role
     */
    private Role role;
    private Scenario scenario;
    private Organization organization;

    public RoleReportPanel( String id, IModel<Role> model, Scenario scenario, Organization organization ) {
        super( id, model );
        role = model.getObject();
        this.scenario = scenario;
        this.organization = organization;
        init();
    }

    private void init() {
        add (new Label("name", role.getName()) );
        add (new Label("description", role.getDescription()) );
        ResourceSpec resourceSpec = ResourceSpec.with(role);
        resourceSpec.setOrganization( organization );
        // Find all actors in role for organization
        List<Actor> actors = Project.service().findAllActors( resourceSpec );
        Collections.sort( actors, new Comparator<Actor>() {
            /** {@inheritDoc} */
            public int compare( Actor actor1, Actor actor2 ) {
                return Collator.getInstance().compare( actor1.getName(), actor2.getName() );
            }
        } );
        add( new ListView<Actor>("actors", actors) {
            protected void populateItem( ListItem<Actor> item ) {
                item.add( new ActorReportPanel("actor", item.getModel()));
            }
        } );
        List<Part> parts = findPartsForRole( role, scenario );
        add( new ListView<Part>("parts", parts) {
            protected void populateItem( ListItem<Part> item ) {
                 item.add (new PartReportPanel("part", item.getModel()));
            }
        });
    }

    private List<Part> findPartsForRole( Role role, Scenario scenario ) {
        List<Part> partsForRole = new ArrayList<Part>();
        Iterator<Part> parts = scenario.parts();
        while( parts.hasNext()) {
            Part part = parts.next();
            if (part.getRole() != null && SemMatch.sameAs( part.getRole(), role ))
                partsForRole.add( part );
        }
        return partsForRole;
    }

}
