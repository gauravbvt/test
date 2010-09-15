package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
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
import java.util.List;

/**
 * Part report panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 5, 2009
 * Time: 9:35:15 PM
 */
public class PartReportPanel extends Panel {

    @SpringBean
    private QueryService queryService;

    /**
     * A part.
     */
    private Part part;
    private Organization organization;

    public PartReportPanel(
            String id,
            IModel<Part> model,
            Organization organization,
            boolean showingIssues ) {

        super( id, model );
        this.organization = organization;
        setRenderBodyOnly( true );
        part = model.getObject();

        init( showingIssues );
    }

    private void init( final boolean showingIssues ) {
        List<Goal> goalList = part.getGoals();
        add( new Label( "task", uppercasedName( part.getTaskWithCategory() ) ),
                new Label( "description", part.getDescription() ).setVisible( !part.getDescription().isEmpty() ),
                new WebMarkupContainer( "as-team" ).setVisible( part.isAsTeam() ),
                new Label( "location", getLocation( part.getLocation() ) ),

                new WebMarkupContainer( "achieves" )
                        .add( new ListView<Goal>( "goals", goalList ) {
                            @Override
                            protected void populateItem( ListItem<Goal> item ) {
                                Goal goal = item.getModelObject();
                                item.add( new Label( "goal", goal.getFullLabel() ),
                                        new Label( "risk-desc", goal.getDescription() ) );
                            }
                        } ).setVisible( !goalList.isEmpty() ),
                 new DocumentsReportPanel( "documents", new Model<ModelObject>( part ) ),
                new IssuesReportPanel( "issues", new Model<ModelObject>( part ) )
                        .setVisible( showingIssues ) );

        WebMarkupContainer receivesContainer = new WebMarkupContainer( "receives-container" );
        List<Flow> receives = getSortedFlows( part, getReceives() );
        receivesContainer.add( new ListView<Flow>( "receives", receives ) {

            @Override
            protected void populateItem( ListItem<Flow> item ) {
                Flow flow = item.getModelObject();
                String type = getType( flow, part.equals( flow.getContactedPart() ) );
                item.add( new AttributeModifier( "class", true, new Model<String>( type ) ) );
                item.add( new FlowReportPanel(
                        "flow",
                        new Model<Flow>( flow ),
                        part,
                        organization,
                        showingIssues ) );
            }
        } );
        receivesContainer.setVisible( !receives.isEmpty() );
        add( receivesContainer );
        WebMarkupContainer sendsContainer = new WebMarkupContainer( "sends-container" );
        List<Flow> sends = getSortedFlows( part, getSends() );
        sendsContainer.add( new ListView<Flow>( "sends", sends ) {

            @Override
            protected void populateItem( ListItem<Flow> item ) {
                Flow flow = item.getModelObject();
                String type = getType( flow, part.equals( flow.getContactedPart() ) );
                item.add( new AttributeModifier( "class", true, new Model<String>( type ) ) );
                item.add( new FlowReportPanel(
                        "flow",
                        new Model<Flow>( flow ),
                        part,
                        organization,
                        showingIssues ) );
            }
        } );
        sendsContainer.setVisible( !sends.isEmpty() );
        add( sendsContainer );

        addTimingInfo();
    }

    @SuppressWarnings( "unchecked" )
    private List<Flow> getReceives() {
        return (List<Flow>) CollectionUtils.select(
                IteratorUtils.toList( part.receives() ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (Flow) object ).isSharing();
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    private List<Flow> getSends() {
        return (List<Flow>) CollectionUtils.select(
                IteratorUtils.toList( part.sends() ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (Flow) object ).isSharing();
                    }
                }
        );
    }

    private static String getLocation( Place place ) {
        return place == null ? "Unspecified" : place.toString();
    }

    private void addTimingInfo() {
        String completion = part.isSelfTerminating() ? part.getCompletionTime().toString() : "";
        String repetition = part.isRepeating() ? part.getRepeatsEvery().toString() : "";

        String eventName = getEventName( part.getInitiatedEvent() );
        add( new WebMarkupContainer( "delay-div" )
                .add( new Label( "completion-time", completion ) )
                .setVisible( part.isSelfTerminating() ),

                new WebMarkupContainer( "repeats-div" )
                        .add( new Label( "repeats-every", repetition ) )
                        .setVisible( part.isRepeating() ),

                new WebMarkupContainer( "starts" ).setVisible( part.isStartsWithSegment() ),

                new WebMarkupContainer( "terminates" ).setVisible( part.isTerminatesEventPhase() ),

                new WebMarkupContainer( "starting" )
                        .add( new Label( "started-event", eventName ) )
                        .setVisible( !eventName.isEmpty() ) );
    }

    private static String getEventName( Event initiatedEvent ) {
        return initiatedEvent == null ? "" : initiatedEvent.getName();
    }

    private static String uppercasedName( String name ) {
        return name.length() > 0 ? name.substring( 0, 1 ).toUpperCase() + name.substring( 1 )
                : name;
    }

    private static List<Flow> getSortedFlows( Part part, List<Flow> flows ) {
        List<Flow> heads = new ArrayList<Flow>();
        List<Flow> middle = new ArrayList<Flow>();
        List<Flow> tails = new ArrayList<Flow>();

        for ( Flow flow : flows ) {
            switch ( part.equals( flow.getSource() ) ?
                    flow.getSignificanceToSource() : flow.getSignificanceToTarget() ) {
                case Triggers:
                    heads.add( flow );
                    break;
                case Terminates:
                    tails.add( flow );
                    break;
                default:
                    middle.add( flow );
            }
        }

        Collections.sort( heads );
        Collections.sort( middle );
        Collections.sort( tails );

        heads.addAll( middle );
        heads.addAll( tails );
        return heads;
    }

    private String getType( Flow flow, boolean incoming ) {
        StringBuilder type = new StringBuilder( incoming ? "receive" : "send" );          // NON-NLS
        type.append( flow.isAskedFor() ? "-answer" : "-notification" );                   // NON-NLS

        Flow.Significance s = part.getSignificance( flow );
        if ( s.equals( Flow.Significance.Triggers ) )
            type.append( " trigger" );                                                    // NON-NLS
        if ( s.equals( Flow.Significance.Terminates ) )
            type.append( " terminate" );                                                  // NON-NLS

        return type.toString();
    }
}
