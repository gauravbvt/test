package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Risk;
import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

    /**
     * A part.
     */
    private Part part;

    public PartReportPanel(
            String id, IModel<Part> model, boolean showRole, boolean showingIssues ) {

        super( id, model );
        setRenderBodyOnly( true );
        part = model.getObject();

        init( showRole, showingIssues );
    }

    private void init( boolean showRole, final boolean showingIssues ) {
        List<Risk> riskList = part.getMitigations();

        add( new Label( "task", uppercasedName( part.getTask() ) ),                       // NON-NLS

             new WebMarkupContainer( "role-container" )
                .add( new Label( "role", part.getRoleString() ) )
                    .setVisible( showRole && part.getRole() != null ),

             new Label( "description", part.getDescription() )
                     .setVisible( !part.getDescription().isEmpty() ),

             new Label( "location", getLocation( part.getLocation() ) ),

             new WebMarkupContainer( "mitigates" )
                     .add( new ListView<Risk>( "risks", riskList ) {
                         @Override
                         protected void populateItem( ListItem<Risk> item ) {
                             Risk risk = item.getModelObject();
                             item.add( new Label( "risk", risk.getLabel() ),
                                       new Label( "risk-desc", risk.getDescription() ) );
                         }
                     } ).setVisible( !riskList.isEmpty() ),

             new IssuesReportPanel( "issues", new Model<ModelObject>( part ) )            // NON-NLS
                     .setVisible( showingIssues ),

             new ListView<Flow>( "flows", getSortedFlows( part ) ) {                      // NON-NLS
                @Override
                protected void populateItem( ListItem<Flow> item ) {
                    Flow flow = item.getModelObject();
                    String type = getType( flow, part.equals( flow.getContactedPart() ) );
                    item.add( new AttributeModifier( "class", true, new Model<String>( type ) ) );
                    item.add( new FlowReportPanel( "flow", 
                                                   new Model<Flow>( flow ), part, showingIssues ) );
                }
            } );

        addTimingInfo();
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

             new WebMarkupContainer( "starts" ).setVisible( part.isStartsWithScenario() ),

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

    private static List<Flow> getSortedFlows( Part part ) {
        List<Flow> heads = new ArrayList<Flow>();
        List<Flow> middle = new ArrayList<Flow>();
        List<Flow> tails = new ArrayList<Flow>();

        Iterator<?> i = new IteratorChain( part.requirements(), part.outcomes() );
        while ( i.hasNext() ) {
            Flow flow = (Flow) i.next();
            switch ( part.equals( flow.getSource() ) ?
                        flow.getSignificanceToSource() : flow.getSignificanceToTarget() )
            {
            case Triggers :
                heads.add( flow );
                break;
            case Terminates :
                tails.add( flow );
                break;
            default :
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
