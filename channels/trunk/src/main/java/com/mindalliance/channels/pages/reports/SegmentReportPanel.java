package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.components.diagrams.FlowMapDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Segment report panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 5, 2009
 * Time: 7:18:56 PM
 */
public class SegmentReportPanel extends Panel {

    /**
     * A segment
     */
    private Segment segment;

    @SpringBean
    private QueryService queryService;

    private Organization organization;
    private boolean showingDiagrams;

    public SegmentReportPanel(
            String id,
            IModel<Segment> model,
            final Organization organization,
            final Actor actor,
            final boolean showingIssues,
            final boolean showingDiagrams) {
        super( id, model );
        this.organization = organization;
        this.showingDiagrams = showingDiagrams;
        setRenderBodyOnly( true );
        segment = model.getObject();

        addSegmentPage( segment, showingIssues );
        List<Organization> involvedOrgs = findAllInvolvedOrganizations( segment, actor );
        add( new Label(
                "no-procedure",
                "No information sharing procedures" ).setVisible( involvedOrgs.isEmpty() ) );        
        add( new ListView<Organization>(
                "organizations",
                involvedOrgs ) {

            @Override
            protected void populateItem( ListItem<Organization> item ) {
                Organization org = item.getModelObject();
                item.add( new AttributeModifier(
                        "class",
                        true,
                        new Model<String>(
                                org.getParent() == null
                                        ? "top-organization"
                                        : "sub-organization" ) ) );
                item.add( new OrganizationReportPanel(
                        "organization",
                        org,
                        organization == null,
                        segment,
                        actor,
                        showingIssues ) );
            }
        } );
    }

    private List<Organization> findAllInvolvedOrganizations( Segment segment, Actor actor ) {
        if ( organization != null ) {
            List<Organization> orgs = new ArrayList<Organization>();
            orgs.add( organization );
            return orgs;
        } else {
            if ( actor == null ) {
                return findAllInvolvedOrganizations( segment );
            } else {
                return findAllAssignedOrganizations( segment, actor );
            }
        }
    }

    private List<Organization> findAllAssignedOrganizations( Segment segment, Actor actor ) {
        Set<Organization> organizations = new HashSet<Organization>();
        for ( Assignment assignment : queryService.findAllAssignments( actor, segment ) ) {
            organizations.add( assignment.getOrganization() );
        }
        return new ArrayList<Organization>( organizations );
    }

    private List<Organization> findAllInvolvedOrganizations( Segment segment ) {
        Set<Organization> actualOrganizations = new HashSet<Organization>();
        Set<Organization> organizationTypes = new HashSet<Organization>();
        Iterator<Part> parts = segment.parts();
        boolean hasUnknown = false;
        while ( parts.hasNext() ) {
            Part part = parts.next();
            Organization organization = part.getOrganization();
            if ( organization != null ) {
                if ( organization.isActual() ) {
                    actualOrganizations.add( organization );
                } else {
                    actualOrganizations.addAll( queryService.findAllActualEntitiesMatching(
                            Organization.class,
                            organization ) );
                    organizationTypes.add( organization );
                }
            } else {
                hasUnknown = true;
            }
        }
        // Filter out non-leaf,, non-actualized types.
        List<Organization> leafTypes = new ArrayList<Organization>();
        for ( final Organization type : organizationTypes ) {
            boolean actualized = CollectionUtils.exists(
                    actualOrganizations,
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            Organization actualOrg = (Organization) object;
                            return actualOrg.getAllTags().contains( type );
                        }
                    }
            );
            if ( !actualized ) {
                boolean narrowed = CollectionUtils.exists(
                        organizationTypes,
                        new Predicate() {
                            public boolean evaluate( Object object ) {
                                Organization otherType = (Organization) object;
                                return !type.equals( otherType ) && otherType.narrowsOrEquals( type );
                            }
                        }
                );
                if ( !narrowed )
                    leafTypes.add( type );
            }
        }
        // Assemble results
        List<Organization> results = new ArrayList<Organization>();
        List<Organization> actuals = new ArrayList<Organization>( actualOrganizations );
        Collections.sort( actuals );
        if ( hasUnknown )
            results.add( Organization.UNKNOWN );
        results.addAll( actuals );
        Collections.sort( leafTypes );
        results.addAll( leafTypes );
        return results;
    }


    private void addSegmentPage( Segment s, boolean showIssues ) {
        List<Goal> goalList = s.getGoals();
        add( new Label( "name", s.getName() )
                .add( new AttributeModifier( "name", true,
                new Model<String>( String.valueOf( s.getId() ) ) ) ),

                new Label( "description", getSegmentDesc( s ) ).setRenderBodyOnly( true ),

                new Label( "event", s.getPhaseEventTitle() ).setVisible( s.getEvent() != null ),

                new WebMarkupContainer( "goal-lead" )
                        .setRenderBodyOnly( true )
                        .setVisible( !goalList.isEmpty() ),

                new WebMarkupContainer( "goal-section" )
                        .add( new ListView<Goal>( "goals", goalList ) {
                            @Override
                            protected void populateItem( ListItem<Goal> item ) {
                                Goal goal = item.getModelObject();
                                item.add( new Label( "goal", goal.getLabel() )
                                        .setRenderBodyOnly( true ),
                                        new Label( "goal-desc", goal.getDescription() )
                                                .setRenderBodyOnly( true ) );
                            }
                        } ).setVisible( !goalList.isEmpty() ),


                new DocumentsReportPanel( "documents", new Model<ModelObject>( s ) ),
                new IssuesReportPanel( "issues", new Model<ModelObject>( s ) )
                        .setVisible( showIssues )
        );
        addFlowDiagram( s );
        WebMarkupContainer flowMapLink = new WebMarkupContainer( "flow-link" );
        flowMapLink.setVisible( User.current().isPlanner() );
        flowMapLink.add( new AttributeModifier( "href", true, new Model<String>( getFlowMapLink( s ) ) ) );
        flowMapLink.add( new AttributeModifier( "target", true, new Model<String>( "_blank" ) ) );
        flowMapLink.setVisible( showingDiagrams );
        add( flowMapLink );
    }


    private void addFlowDiagram( Segment s ) {
        if ( User.current().isPlanner() && showingDiagrams ) {
            add( new FlowMapDiagramPanel( "flowMap",
                    new Model<Segment>( s ),
                    null,
                    //size,
                    new Settings( null, DiagramFactory.LEFT_RIGHT, null, true, false ) ) );
        } else {
            add( new Label( "flowMap", "" ) );
        }
    }

    private String getFlowMapLink( Segment segment ) {
        return "/segment.png?segment=" + segment.getId() + "&node=" + "NONE";
    }

    private static String getSegmentDesc( Segment s ) {
        String desc = s.getDescription();
        return desc.isEmpty() || !desc.endsWith( "." ) ?
                desc + "." : desc;
    }
}
