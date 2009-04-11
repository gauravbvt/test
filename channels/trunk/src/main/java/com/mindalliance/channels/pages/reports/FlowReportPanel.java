package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Medium;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Flow report panel.
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
        boolean showContacts = !partIsSource &&  flow.isAskedFor()
                             || partIsSource && !flow.isAskedFor();

        addFlowPropertyFields( partIsSource );

        List<Channel> channels = flow.getChannels();
        Set<Medium> unicasts = getUnicasts( flow );
        Collection<Channel> broadcasts = getBroadcasts( channels );
        List<LocalizedActor> actors = findActors();

        ResourceSpec spec = new ResourceSpec( flow.getContactedPart() );
        Component channelsPanel = new ChannelsReportPanel( "channels", spec, unicasts, broadcasts );
        channelsPanel.setVisible( showContacts && !channels.isEmpty() && actors.isEmpty() );
        add( channelsPanel );

        WebMarkupContainer actorsDiv = createContacts( actors, unicasts, broadcasts );
        actorsDiv.setVisible( showContacts && !actors.isEmpty() );
        add( actorsDiv );

        add( new IssuesReportPanel( "issues", new Model<ModelObject>( flow ) ) );
    }

    private static Collection<Channel> getBroadcasts( List<Channel> channels ) {
        Set<Channel> broadcasts = new HashSet<Channel>();
        for ( Channel c : channels )
            if ( c.isBroadcast() )
                broadcasts.add( c );
        return broadcasts;
    }

    private static Set<Medium> getUnicasts( Flow flow ) {
        Set<Medium> result = EnumSet.noneOf( Medium.class );

        for ( Channel c : flow.getChannels() ) {
            Medium medium = c.getMedium();
            if ( medium.isUnicast() )
                result.add( medium );
        }
        return result;
    }

    private void addFlowPropertyFields( boolean partIsSource ) {
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
    }

    private WebMarkupContainer createContacts(
            final List<LocalizedActor> actors, final Set<Medium> unicasts,
            final Collection<Channel> broadcasts ) {

        ListView<LocalizedActor> actorsList = new ListView<LocalizedActor>( "actors", actors ) {
            @Override
            protected void populateItem( ListItem<LocalizedActor> item ) {
                LocalizedActor localizedActor = item.getModel().getObject();
                Part p = localizedActor.getPart();
                Actor actor = localizedActor.getActor();
                ResourceSpec spec = new ResourceSpec( p );
                if ( !Actor.UNKNOWN.equals( actor ) )
                    spec.setActor( actor );

                Scenario scenario = p.getScenario().equals( part.getScenario() ) ? null
                                                                                 : p.getScenario();
                item.add( new ActorReportPanel(
                        "actor", scenario, spec, true, unicasts, broadcasts ) );
            }
        };
        actorsList.add( new AttributeModifier( "class", true,
                new Model<String>( flow.isAll() ? "all-actors" : "any-actor" ) ) );

        WebMarkupContainer result = new WebMarkupContainer( "actors-div" );
        result.add( actorsList );
        return result;
    }

    private List<LocalizedActor> findActors() {

        Set<Part> parts = new HashSet<Part>();

        boolean partIsSource = flow.getSource().equals( part );
        Node node = partIsSource ? flow.getTarget() : flow.getSource();
        if ( node.isConnector() ) {
            Iterator<ExternalFlow> xFlows = ( (Connector) node ).externalFlows();
            while ( xFlows.hasNext() )
                parts.add( xFlows.next().getContactedPart() );
        } else {
            Part otherPart = flow.getContactedPart();
            if ( otherPart != null )
                parts.add( otherPart );
        }

        Set<LocalizedActor> localizedActors = new HashSet<LocalizedActor>();
        for ( Part p : parts ) {
            ResourceSpec spec = p.resourceSpec();
            List<Actor> actors = Project.getProject().getDqo().findAllActors( spec );
            for ( Actor a : actors )
                localizedActors.add( new LocalizedActor( a, p ) );
        }

        List<LocalizedActor> list = new ArrayList<LocalizedActor>( localizedActors );
        Collections.sort( list, new Comparator<LocalizedActor>() {
            public int compare( LocalizedActor o1, LocalizedActor o2 ) {
                return Collator.getInstance().compare(
                        o1.getActor().getName(), o2.getActor().getName() );
            }
        } );
        return list;
    }

    /**
     * An actor from a part in a scenario.
     */
    private static class LocalizedActor implements Serializable {

        /** The actor. */
        private Actor actor;

        /** The part. */
        private Part part;

        private LocalizedActor( Actor actor, Part part ) {
            this.actor = actor;
            this.part = part;
        }

        public Actor getActor() {
            return actor;
        }

        public Part getPart() {
            return part;
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj )
                return true;
            if ( obj == null || getClass() != obj.getClass() )
                return false;
            LocalizedActor that = (LocalizedActor) obj;
            if ( actor != null ? !actor.equals( that.actor ) : that.actor != null )
                return false;
            if ( part != null ? !part.equals( that.part ) : that.part != null )
                return false;
            return true;
        }

        @Override
        public int hashCode() {
            int result = actor != null ? actor.hashCode() : 0;
            result = 31 * result + ( part != null ? part.hashCode() : 0 );
            return result;
        }
    }
}
