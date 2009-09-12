package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Part;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

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

    /** The query service. */
    @SpringBean
    private QueryService queryService;

    public RoleReportPanel(
            String id, final Role role, final Scenario scenario, final Organization organization,
            final boolean showingIssues ) {

        super( id );
        setRenderBodyOnly( true );
        
        add( new Label( "sc-name", scenario.getName() ),                                  // NON-NLS
             new Label( "org", organization.toString() ),                                 // NON-NLS
             new Label( "name", role.getName() ),                                         // NON-NLS
             new Label( "description", role.getDescription() )
                     .setVisible( !role.getDescription().isEmpty() ),

             new ListView<Part>( "parts", scenario.findParts( organization, role ) ) {    // NON-NLS
                    @Override
                    protected void populateItem( ListItem<Part> item ) {
                        item.add( new PartReportPanel( "part",                            // NON-NLS
                                                       item.getModel(), false, showingIssues ) );
                    }
                },

             new ListView<Actor>( "actors", getActors( role, scenario, organization ) ) { // NON-NLS
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
                        item.add( new ActorBannerPanel( "actor", scenario, spec, false ) );
                    }
                } );
    }

    private List<Actor> getActors( Role role, Scenario scenario, Organization organization ) {
        List<Actor> actors = queryService.findActors( organization, role, scenario );
        if ( actors.isEmpty() )
            actors.add( Actor.UNKNOWN );
        return actors;
    }
}
