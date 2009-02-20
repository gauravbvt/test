package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Flow report panel
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 6, 2009
 * Time: 2:06:55 PM
 */
public class FlowReportPanel extends Panel {
    /**
     * A flow
     */
    private Flow flow;
    /**
     * The part from which perspective the flow is reported on
     */
    private Part part;

    public FlowReportPanel( String id, Model<Flow> model, Part part ) {
        super( id, model );
        setRenderBodyOnly( true );
        flow = model.getObject();
        this.part = part;
        init();
    }

    private void init() {
        boolean partIsSource = flow.getSource() == part;

        Label informationLabel = new Label( "information",
                   partIsSource ? flow.getOutcomeTitle() : flow.getRequirementTitle() );
        informationLabel.add( new AttributeModifier( "class", true, new Model<String>(
                flow.isRequired() ? "required-information" : "information" ) ) );
        add( informationLabel );

        add( new Label( "urgency", flow.getMaxDelay().toString() ) );

        String desc = flow.getDescription();
        Label descLabel = new Label( "description", desc );
        descLabel.setVisible( desc != null && !desc.isEmpty() );
        add( descLabel );

        List<Actor> actors = findRelevantActors();

        boolean showContact =  ( !partIsSource && flow.isAskedFor() )
                            || ( partIsSource && !flow.isAskedFor() );

        ChannelsReportPanel channels =
                new ChannelsReportPanel( "channels", new Model<Channelable>( flow ) );
        channels.setVisible( actors.isEmpty() && showContact );
        add( channels );

        WebMarkupContainer actorsDiv = new WebMarkupContainer( "actors-div" );
        ListView<Actor> actorsList = new ListView<Actor>( "actors", actors ) {
            @Override
            protected void populateItem( ListItem<Actor> item ) {
                item.add( new ActorReportPanel( "actor", item.getModel() ) );
            }
        };
        actorsList.add( new AttributeModifier( "class", true, new Model<String>(
                flow.isAll() ? "all-actors" : "any-actor" ) ) );
        actorsDiv.add( actorsList );
        actorsDiv.setVisible( showContact );
        add( actorsDiv );

        add( new IssuesReportPanel( "issues", new Model<ModelObject>( flow ) ) );
    }

    private List<Actor> findRelevantActors() {
        Set<Actor> actors = new HashSet<Actor>();

        boolean partIsSource = flow.getSource() == part;
        Node node = partIsSource ? flow.getTarget() : flow.getSource();
        if ( node.isConnector() ) {
            Iterator<ExternalFlow> xFlows = ( (Connector) node ).externalFlows();
            while ( xFlows.hasNext() ) {
                ExternalFlow xFlow = xFlows.next();
                Part xPart = xFlow.getPart();
                ResourceSpec spec = xPart.resourceSpec();
                actors.addAll( Project.service().findAllActors( spec ) );
            }
        } else {
            Part otherPart = (Part) node;
            if ( otherPart.getActor() == null )
                actors.addAll( Project.service().findAllActors( otherPart.resourceSpec() ) );
        }

        List<Actor> list = new ArrayList<Actor>( actors );
        Collections.sort( list, new Comparator<Actor>() {
            public int compare( Actor o1, Actor o2 ) {
                return Collator.getInstance().compare( o1.getName(), o2.getName() );
            }
        } );

        return list;
    }
}
