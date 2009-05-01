package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Medium;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.Channels;
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
        Collection<Channel> broadcasts = getBroadcasts( flow );
        List<LocalizedActor> actors = findActors( flow, broadcasts, unicasts );

        ResourceSpec spec = new ResourceSpec( flow.getContactedPart() );
        Component channelsPanel = new ChannelsReportPanel(
                "channels", spec, unicasts, broadcasts );
        channelsPanel.setVisible( showContacts && !channels.isEmpty() && actors.isEmpty() );
        add( channelsPanel );

        WebMarkupContainer actorsDiv = createContacts( actors );
        actorsDiv.setVisible( showContacts && !actors.isEmpty() );
        add( actorsDiv );

        add( new IssuesReportPanel( "issues", new Model<ModelObject>( flow ) ) );
    }

    private static Collection<Channel> getBroadcasts( Flow flow ) {
        Set<Channel> broadcasts = new HashSet<Channel>();
        for ( Channel c : flow.getEffectiveChannels() )
            if ( c.isBroadcast() )
                broadcasts.add( c );
        return broadcasts;
    }

    private static Set<Medium> getUnicasts( Flow flow ) {
        Set<Medium> result = EnumSet.noneOf( Medium.class );

        for ( Channel c : flow.getEffectiveChannels() ) {
            Medium medium = c.getMedium();
            if ( medium.isUnicast() )
                result.add( medium );
        }
        return result;
    }

    private void addFlowPropertyFields( boolean partIsSource ) {
        String classes = flow.isRequired() ? "required-information" : "information";      // NON-NLS

        String title = partIsSource ? flow.getOutcomeTitle() : flow.getRequirementTitle();
        Label informationLabel = new Label( "information", title );                       // NON-NLS
        informationLabel.add(
                new AttributeModifier( "class", true, new Model<String>( classes ) ) );   // NON-NLS
        add( informationLabel );

        add( new Label( "urgency", flow.getMaxDelay().toString() ) );                     // NON-NLS

        String desc = flow.getDescription();
        Label descLabel = new Label( "description", desc );                               // NON-NLS
        descLabel.setVisible( desc != null && !desc.isEmpty() );
        add( descLabel );
    }

    private WebMarkupContainer createContacts( final List<LocalizedActor> actors ) {

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
                        "actor", scenario, spec, true,
                        localizedActor.getUnicasts(), localizedActor.getBroadcasts() ) );
            }
        };
        actorsList.add( new AttributeModifier( "class", true,
                new Model<String>( flow.isAll() ? "all-actors" : "any-actor" ) ) );

        WebMarkupContainer result = new WebMarkupContainer( "actors-div" );
        result.add( actorsList );
        return result;
    }

    private static List<LocalizedActor> findActors(
            Flow flow, Collection<Channel> broadcasts, Set<Medium> unicasts ) {

        Set<LocalizedActor> localizedActors = new HashSet<LocalizedActor>();

        Part part = flow.getContactedPart();
        if ( part == null ) {
            Connector connector = (Connector) flow.getContactedNode();

            for ( Iterator<ExternalFlow> flows = connector.externalFlows(); flows.hasNext(); ) {
                ExternalFlow f = flows.next();
                Collection<Channel> b = getBroadcasts( f );
                Set<Medium> u = getUnicasts( f );
                localizedActors.addAll( findActors( f, b, u ) );
            }
        } else
            for ( Actor a : Channels.instance().getQueryService().findAllActors( part.resourceSpec() ) )
                localizedActors.add( new LocalizedActor( a, part, unicasts, broadcasts ) );

        List<LocalizedActor> result = new ArrayList<LocalizedActor>( localizedActors );
        Collections.sort( result, new Comparator<LocalizedActor>() {
            public int compare( LocalizedActor o1, LocalizedActor o2 ) {
                return Collator.getInstance().compare(
                        o1.getActor().getName(), o2.getActor().getName() );
            }
        } );
        return result;
    }

    /**
     * An actor from a part in a scenario.
     */
    private static class LocalizedActor implements Serializable {

        /** Unicast media used to contact the actor. */
        private Set<Medium> unicasts;

        /** Broadcast channels used to contact the actor. */
        private Collection<Channel> broadcasts;

        /** The actor. */
        private Actor actor;

        /** The part. */
        private Part part;

        private LocalizedActor( Actor actor, Part part,
                                Set<Medium> unicasts, Collection<Channel> broadcasts ) {
            this.actor = actor;
            this.part = part;
            this.unicasts = unicasts;
            this.broadcasts = broadcasts;
        }

        public Actor getActor() {
            return actor;
        }

        public Part getPart() {
            return part;
        }

        public Collection<Channel> getBroadcasts() {
            return broadcasts;
        }

        public Set<Medium> getUnicasts() {
            return unicasts;
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
