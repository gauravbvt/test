package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

    @SpringBean
    private QueryService queryService;

    public OrganizationReportPanel( String id, Organization organization, Scenario scenario,
                                    boolean showActors ) {
        super( id );
        setRenderBodyOnly( true );
        this.organization = organization;
        this.scenario = scenario;

        if ( showActors )
            add( new ListView<Actor>( "sections", getActors() ) {
                @Override
                protected void populateItem( ListItem<Actor> item ) {
                    item.add( new ActorReportPanel( "section", item.getModelObject(),
                                        OrganizationReportPanel.this.scenario,
                                        OrganizationReportPanel.this.organization ) );
                }
            } );
        else
            add( new ListView<Role>( "sections", scenario.findRoles( organization ) ) {
                @Override
                protected void populateItem( ListItem<Role> item ) {
                    item.add( new RoleReportPanel( "section", item.getModelObject(),
                                       OrganizationReportPanel.this.scenario,
                                       OrganizationReportPanel.this.organization ) );
                }
            } );
    }

    private List<Actor> getActors() {
        Set<Actor> actors = new HashSet<Actor>();
        Iterator<Part> partIterator = scenario.parts();
        while ( partIterator.hasNext() ) {
            Part part = partIterator.next();
            if ( part.isIn( organization ) )
                actors.addAll( queryService.findAllActors( part.resourceSpec() ) );
        }

        List<Actor> result = new ArrayList<Actor>( actors );
        Collections.sort( result );
        return result;
    }
}
