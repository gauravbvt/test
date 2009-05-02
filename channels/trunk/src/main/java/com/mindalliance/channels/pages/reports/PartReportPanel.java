package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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
public class PartReportPanel extends AbstractReportPanel {

    /**
     * A part.
     */
    private Part part;

    public PartReportPanel( String id, IModel<Part> model ) {
        super( id, model );
        setRenderBodyOnly( true );
        part = model.getObject();
        init();
    }

    private void init() {
        add( new Label( "task", uppercasedName( part.getTask() ) ) );                     // NON-NLS

        String desc = part.getDescription();
        Label descLabel = new Label( "description", desc );                               // NON-NLS
        descLabel.setVisible( desc != null && !desc.isEmpty() );
        add( descLabel );

        add( new Label( "location", part.getLocation() == null ?                          // NON-NLS
                            "Unspecified" : part.getLocation().toString() ) );

        add( new Label( "jurisdiction", part.getJurisdiction() == null ?                  // NON-NLS
                            "Unspecified" : part.getJurisdiction().toString() ) );
        addTimingInfo();

        addFlows( getSortedFlows( part ) );
        add( new IssuesReportPanel( "issues", new Model<ModelObject>( part ) ) );         // NON-NLS
    }

    private void addTimingInfo() {
        WebMarkupContainer completionDiv = new WebMarkupContainer( "delay-div" );         // NON-NLS
        completionDiv.add( new Label( "completion-time",                                  // NON-NLS
                            part.isSelfTerminating() ? part.getCompletionTime().toString() : "" ) );
        completionDiv.setVisible( part.isSelfTerminating() );
        add( completionDiv );

        WebMarkupContainer repeatsDiv = new WebMarkupContainer( "repeats-div" );          // NON-NLS
        repeatsDiv.add( new Label( "repeats-every",                                       // NON-NLS
                                   part.isRepeating() ? part.getRepeatsEvery().toString() : "" ) );
        repeatsDiv.setVisible( part.isRepeating() );
        add( repeatsDiv );

        WebMarkupContainer starts = new WebMarkupContainer( "starts" );                   // NON-NLS
        starts.setVisible( part.isStartsWithScenario() );
        add( starts );

        WebMarkupContainer terminates = new WebMarkupContainer( "terminates" );           // NON-NLS
        terminates.setVisible( part.isTerminatesScenario() );
        add( terminates );

        Scenario initiatedScenario = part.getInitiatedScenario();
        WebMarkupContainer starting = new WebMarkupContainer( "starting" );               // NON-NLS
        String name = initiatedScenario == null ? "" : initiatedScenario.getName();
        starting.add( new Label( "started-scenario", name ) );                            // NON-NLS
        starting.setVisible( initiatedScenario != null );
        add( starting );
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
            switch ( flow.getSource().equals( part ) ?
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

    private void addFlows( final List<Flow> flows ) {

        add( new ListView<Flow>( "flows", flows ) {                                       // NON-NLS
            @Override
            protected void populateItem( ListItem<Flow> item ) {
                Flow flow = item.getModelObject();

                boolean incoming = part.equals( flow.getContactedPart() );
                String type = incoming ? "receive" : "send";                              // NON-NLS
                type += flow.isAskedFor() ? "-answer" : "-notification";                  // NON-NLS

                Flow.Significance s = part.equals( flow.getSource() ) ?
                        flow.getSignificanceToSource() : flow.getSignificanceToTarget();
                if ( s.equals( Flow.Significance.Triggers ) )
                    type += " trigger" ;                                                  // NON-NLS
                if ( s.equals( Flow.Significance.Terminates ) )
                    type += " terminate" ;                                               // NON-NLS

                item.add( new AttributeModifier(
                        "class", true, new Model<String>( type ) ) );                     // NON-NLS

                item.add( new FlowReportPanel( "flow", new Model<Flow>( flow ), part ) ); // NON-NLS
            }
        } );
    }
}
