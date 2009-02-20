package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
        List<Actor> actors = findActors();
        if ( actors.isEmpty() )
            actors.add( Actor.UNKNOWN );
        add( new ListView<Actor>( "actors", actors ) {                                    // NON-NLS
            @Override
            protected void populateItem( ListItem<Actor> item ) {
                item.add( new ActorReportPanel( "actor", item.getModel() ) );             // NON-NLS
            }
        } );
        add( new ListView<Part>( "parts", scenario.findParts( organization, role ) ) {    // NON-NLS
            @Override
            protected void populateItem( ListItem<Part> item ) {
                item.add( new PartReportPanel( "part", item.getModel() ) );               // NON-NLS
            }
        } );
    }

    private List<Actor> findActors() {
        Set<Actor> actors = new HashSet<Actor>();
        boolean noActorRoleFound = false;

        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            boolean sameOrg = Organization.UNKNOWN.equals( organization ) ?
                                part.getOrganization() == null
                              : organization.equals( part.getOrganization() );
            boolean sameRole = Role.UNKNOWN.equals( role ) ?
                                part.getRole() == null
                              : role.equals( part.getRole() );

            if ( sameOrg && sameRole ) {
                if ( part.getActor() != null )
                    actors.add( part.getActor() );
                else
                    noActorRoleFound = true;
            }
        }

        if ( noActorRoleFound )
            return Project.getProject().findActors( organization, role );
        else {
            List<Actor> list = new ArrayList<Actor>( actors );
            Collections.sort( list, new Comparator<Actor>() {
                /** {@inheritDoc} */
                public int compare( Actor o1, Actor o2 ) {
                    return Collator.getInstance().compare( o1.getName(), o2.getName() );
                }
            } );
            return list;
        }
    }
}
