package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Comparator;

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
            add( new ListView<ResourceSpec>( "sections", getSpecs() ) {
                @Override
                protected void populateItem( ListItem<ResourceSpec> item ) {
                    item.add( new ActorReportPanel( "section",
                                                    OrganizationReportPanel.this.scenario,
                                                    item.getModelObject() ) );
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

    private List<ResourceSpec> getSpecs() {
        Set<Actor> actors = new HashSet<Actor>();
        Set<ResourceSpec> specs = new HashSet<ResourceSpec>();

        for ( Part p : queryService.findAllParts( scenario, ResourceSpec.with( organization ) ) ) {
            ResourceSpec spec = p.resourceSpec();
            if ( spec.isOrganization() )
                spec.setActor( Actor.UNKNOWN );
            List<Actor> a = queryService.findAllActors( spec );
            if ( a.isEmpty() )
                specs.add( spec );
            else
                actors.addAll( a );
        }

        List<ResourceSpec> result = new ArrayList<ResourceSpec>( specs );
        for ( Actor a : actors ) {
            ResourceSpec spec = new ResourceSpec();
            spec.setActor( a );
            spec.setOrganization( organization );
            result.add( spec );
        }
        Collections.sort( result, new Comparator<ResourceSpec>() {
            public int compare( ResourceSpec o1, ResourceSpec o2 ) {
                return o1.getReportTitle().compareTo( o2.getReportTitle() );
            }
        } );
        return result;
    }
}
