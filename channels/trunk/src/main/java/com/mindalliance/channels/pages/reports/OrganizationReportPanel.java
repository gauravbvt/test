package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
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
            String id,
            Organization organization,
            Segment segment,
            final Actor actor,
            final boolean showingIssues ) {

        super( id );
        setRenderBodyOnly( true );
        this.actor = actor;
        this.organization = organization;
        this.segment = segment;
        add( new Label( "org-title", getOrgTitle() ) );
        add( new Label( "name", organization.getName() ) );
        add( new Label( "mission", organization.getMission() ) );
        add( new WebMarkupContainer( "tags-container" )
                .add( new ListView<ModelEntity>( "tags", organization.getTags() ) {
                    protected void populateItem( ListItem<ModelEntity> item ) {
                        ModelEntity tag = item.getModel().getObject();
                        item.add( new Label( "tag", tag.getName() ) );
                    }
                } )
                .setVisible( !organization.getTags().isEmpty() ) );
        add( new DocumentsReportPanel( "documents", new Model<ModelObject>( organization ) ) );
        add( new IssuesReportPanel( "issues", new Model<ModelObject>( organization ) )
                .setVisible( showingIssues )
        );
        add( new ListView<ResourceSpec>(
                "sections",
                getResponsibilities() ) {
            @Override
            protected void populateItem( ListItem<ResourceSpec> item ) {
                item.add( new ResponsibilityReportPanel(
                        "section",
                        item.getModelObject(),
                        actor,
                        OrganizationReportPanel.this.segment,
                        showingIssues ).setRenderBodyOnly( true ) );
            }
        } );
    }

    private String getOrgTitle() {
        return organization.isActual()
                ? "Organization"
                : "Organizations of type";
    }

    private List<ResourceSpec> getResponsibilities() {
        if ( actor != null ) {
            return findActorResponsibilities();
        } else {
            return findAllResponsibilities();
        }
    }

    @SuppressWarnings( "unchecked" )
    private List<ResourceSpec> findActorResponsibilities() {
        List<Assignment> assignments =
                (List<Assignment>) CollectionUtils.select(
                        queryService.findAllAssignments( actor, segment ),
                        new Predicate() {
                            public boolean evaluate( Object object ) {
                                Assignment assignment = (Assignment) object;
                                return organization.equals( assignment.getOrganization() );
                            }
                        } );
        List<ResourceSpec> specs = new ArrayList<ResourceSpec>();
        for ( Assignment assignment : assignments ) {
            specs.add( new ResourceSpec( assignment ) );
        }
        return specs;
    }

    private List<ResourceSpec> findAllResponsibilities() {
        Set<ResourceSpec> specs = new HashSet<ResourceSpec>();
        for ( Part p : queryService.findAllParts(
                segment,
                ResourceSpec.with( organization ),
                false ) ) {
            ResourceSpec spec = p.resourceSpec();
            Organization specOrg = spec.getOrganization();
            // Tweak the matching
            boolean matches = specOrg == null && organization.isUnknown()
                    || specOrg != null && !organization.isWithin( specOrg );
            if ( matches ) {
                spec.setOrganization( organization );
                if ( spec.getRole() == null ) {
                    spec.setRole( Role.UNKNOWN );
                }
                specs.add( spec );
            }
        }
        return new ArrayList<ResourceSpec>( specs );
    }

/*    @SuppressWarnings( "unchecked" )
    private List<ResourceSpec> getSpecs() {
        Set<ResourceSpec> specs = new HashSet<ResourceSpec>();
        for ( Part p : queryService.findAllParts( segment, ResourceSpec.with( organization ), false ) ) {
            final ResourceSpec spec = p.resourceSpec();
            if ( spec.isOrganization() )
                spec.setActor( Actor.UNKNOWN );
            // Find all actors directly employed by  the organization and playing the part
            List<Actor> actorList = (List<Actor>) CollectionUtils.select(
                    queryService.findAllActualActors( spec ),
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            return !queryService.findAllJobs( organization, (Actor) object ).isEmpty();
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
    }*/
}
