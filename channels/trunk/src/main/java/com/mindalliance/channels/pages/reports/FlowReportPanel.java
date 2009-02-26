package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
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

        List<LocalizedActor> actors = findActors();

        boolean showContact = !partIsSource &&  flow.isAskedFor()
                            || partIsSource && !flow.isAskedFor();

        ChannelsReportPanel channels =
                new ChannelsReportPanel( "channels", new Model<Channelable>( flow ) );
        channels.setVisible( actors.isEmpty() && showContact );
        add( channels );

        WebMarkupContainer actorsDiv = new WebMarkupContainer( "actors-div" );
        ListView<LocalizedActor> actorsList = new ListView<LocalizedActor>( "actors", actors ) {
            @Override
            protected void populateItem( ListItem<LocalizedActor> item ) {
                LocalizedActor localizedActor = item.getModel().getObject();
                Scenario scenario = localizedActor.getScenario();
                item.add( new ActorReportPanel( "actor",
                    part.getScenario().equals( scenario ) ? null : scenario, 
                    localizedActor.getActor() ) );
            }
        };
        actorsList.add( new AttributeModifier( "class", true,
                new Model<String>( flow.isAll() ? "all-actors" : "any-actor" ) ) );
        actorsDiv.add( actorsList );
        actorsDiv.setVisible( !actors.isEmpty() );
        add( actorsDiv );

        add( new IssuesReportPanel( "issues", new Model<ModelObject>( flow ) ) );
    }

    private List<LocalizedActor> findActors() {

        Set<Part> parts = new HashSet<Part>();

        boolean partIsSource = flow.getSource().equals( part );
        Node node = partIsSource ? flow.getTarget() : flow.getSource();
        if ( node.isConnector() ) {
            Iterator<ExternalFlow> xFlows = ( (Connector) node ).externalFlows();
            while ( xFlows.hasNext() )
                parts.add( xFlows.next().getPart() );
        } else {
            Part otherPart = (Part) node;
            if ( otherPart.getActor() == null )
                parts.add( otherPart );
        }

        Set<LocalizedActor> actors = new HashSet<LocalizedActor>();
        for ( Part p : parts ) {
            for ( Actor a : Project.getProject().getService().findAllActors( p.resourceSpec() ) )
                actors.add( new LocalizedActor( a,  p.getScenario() ) );
        }

        List<LocalizedActor> list = new ArrayList<LocalizedActor>( actors );
        Collections.sort( list, new Comparator<LocalizedActor>() {
            public int compare( LocalizedActor o1, LocalizedActor o2 ) {
                return Collator.getInstance().compare(
                        o1.getActor().getName(), o2.getActor().getName() );
            }
        } );
        return list;
    }

    /**
     * An actor from a scenario.
     */
    private static class LocalizedActor {

        /** The actor. */
        private Actor actor;

        /** The scenario. */
        private Scenario scenario;

        private LocalizedActor( Actor actor, Scenario scenario ) {
            this.actor = actor;
            this.scenario = scenario;
        }

        public Actor getActor() {
            return actor;
        }

        public Scenario getScenario() {
            return scenario;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o )
                return true;
            if ( o == null || getClass() != o.getClass() )
                return false;
            LocalizedActor that = (LocalizedActor) o;
            if ( actor != null ? !actor.equals( that.actor ) : that.actor != null )
                return false;
            if ( scenario != null ? !scenario.equals( that.scenario ) : that.scenario != null )
                return false;
            return true;
        }

        @Override
        public int hashCode() {
            int result;
            result = ( actor != null ? actor.hashCode() : 0 );
            result = 31 * result + ( scenario != null ? scenario.hashCode() : 0 );
            return result;
        }
    }
}
