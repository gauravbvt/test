package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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
     * The segment in context.
     */
    private Segment segment;

    @SpringBean
    private QueryService queryService;

    /**
     * The actor to single out or null to show all actors.
     */
    private Actor actor;

    public OrganizationReportPanel(
            String id, Organization organization, Segment segment, Actor actor,
            boolean showActors, final boolean showingIssues ) {

        super( id );
        setRenderBodyOnly( true );
        this.actor = actor;
        this.organization = organization;
        this.segment = segment;

        if ( showActors )
            add( new ListView<ResourceSpec>(
                    "sections",
                    getSpecs() ) {
                @Override
                protected void populateItem( ListItem<ResourceSpec> item ) {
                    ResourceSpec resourceSpec = item.getModelObject();
                    item.add( new ActorReportPanel( "section",
                            OrganizationReportPanel.this.segment,
                            resourceSpec, showingIssues )
                            .setRenderBodyOnly( true ) );
                }
            } );
        else
            add( new ListView<Role>(
                    "sections",
                    segment.findRoles( organization ) ) {
                @Override
                protected void populateItem( ListItem<Role> item ) {
                    item.add( new RoleReportPanel( "section", item.getModelObject(),
                            OrganizationReportPanel.this.segment,
                            OrganizationReportPanel.this.organization, showingIssues )
                            .setRenderBodyOnly( true ) );
                }
            } );
    }

    @SuppressWarnings( "unchecked" )
    private List<ResourceSpec> getSpecs() {
        Set<ResourceSpec> specs = new HashSet<ResourceSpec>();

        for ( Part p : queryService.findAllParts( segment, ResourceSpec.with( organization ), false ) ) {
            final ResourceSpec spec = p.resourceSpec();
            if ( spec.isOrganization() )
                spec.setActor( Actor.UNKNOWN );
            // Find all actors directly employed by  the organization and playing the part
            List<Actor> actorList = (List<Actor>)CollectionUtils.select(
                    queryService.findAllActualActors( spec ),
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            return !queryService.findAllJobs(  organization, (Actor)object ).isEmpty();
                        }
                    }
                    );
            if ( actor == null ) {
                if ( actorList.isEmpty() )
                    specs.add( spec );
                else
                    for ( Actor a1 : actorList ) {
                        ResourceSpec rs = new ResourceSpec( spec );
                        rs.setActor( a1 );
                        rs.setOrganization( organization );
                        specs.add( rs );
                    }

            } else if ( actorList.contains( actor ) ) {
                ResourceSpec rs = new ResourceSpec( spec );
                rs.setActor( actor );
                rs.setOrganization( organization );
                specs.add( rs );
            }
        }

        List<ResourceSpec> result = new ArrayList<ResourceSpec>( removeNarrowed( specs ) );
        Collections.sort( result, new Comparator<ResourceSpec>() {
            public int compare( ResourceSpec o1, ResourceSpec o2 ) {
                return o1.toString().compareTo( o2.toString() );
            }
        } );
        return result;
    }

    @SuppressWarnings( "unchecked" )
    private Collection<ResourceSpec> removeNarrowed( final Collection<ResourceSpec> specs ) {
        return CollectionUtils.select(
                specs,
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        final ResourceSpec spec = (ResourceSpec) object;
                        return !CollectionUtils.exists(
                                specs,
                                new Predicate() {
                                    public boolean evaluate( Object object ) {
                                        ResourceSpec otherSpec = (ResourceSpec) object;
                                        return spec != otherSpec
                                                && otherSpec.compatibleWith( spec );
                                    }
                                }
                        );
                    }
                }
        );
    }
}
