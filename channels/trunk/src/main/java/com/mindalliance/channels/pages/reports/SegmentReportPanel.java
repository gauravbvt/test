package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.components.diagrams.FlowMapDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

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

    public SegmentReportPanel(
            String id, IModel<Segment> model, final Actor actor, final boolean showingIssues ) {
        super( id, model );
        setRenderBodyOnly( true );
        segment = model.getObject();

        addSegmentPage( segment, showingIssues );

        add( new ListView<Organization>(
                "organizations",
                queryService.findAllInvolvedOrganizations( segment ) ) { // NON-NLS

            @Override
            protected void populateItem( ListItem<Organization> item ) {
                Organization organization = item.getModelObject();
                item.add( new AttributeModifier( "class", true, new Model<String>(        // NON-NLS
                        organization.getParent() == null ? "top organization"             // NON-NLS
                                : "sub organization" ) ) );      // NON-NLS
                item.add( new OrganizationReportPanel( "organization",                    // NON-NLS
                        organization, segment, actor, true, showingIssues ) );
            }
        } );
    }

    private void addSegmentPage( Segment s, boolean showIssues ) {
        List<Goal> goalList = s.getGoals();
        add( new Label( "name", s.getName() )
                .add( new AttributeModifier( "name", true,
                new Model<String>( String.valueOf( s.getId() ) ) ) ),

                new Label( "description", getSegmentDesc( s ) ).setRenderBodyOnly( true ),  // NON-NLS

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

                new FlowMapDiagramPanel( "flowMap",                                          // NON-NLS
                        new Model<Segment>( s ),
                        null,
                        //size,
                        new Settings( null, DiagramFactory.LEFT_RIGHT, null, true, false ) ),

                new IssuesReportPanel( "issues", new Model<ModelObject>( s ) )                // NON-NLS
                        .setVisible( showIssues )
        );
        WebMarkupContainer flowMapLink = new WebMarkupContainer( "flow-link" );
        flowMapLink.add( new AttributeModifier( "href", true, new Model<String>( getFlowMapLink(s) ) ) );
        flowMapLink.add( new AttributeModifier( "target", true, new Model<String>( "_" ) ) );
        add( flowMapLink );
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
