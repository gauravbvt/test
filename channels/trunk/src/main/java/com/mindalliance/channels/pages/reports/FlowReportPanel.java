package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.TransmissionMedium;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

    @SpringBean
    private QueryService queryService;

    /**
     * A flow
     */
    private Flow flow;

    /**
     * The part from which perspective the flow is reported on
     */
    private Part part;

    public FlowReportPanel( String id, Model<Flow> model, Part part, boolean showingIssues ) {
        super( id, model );
        setRenderBodyOnly( true );
        flow = model.getObject();
        this.part = part;
        init( showingIssues, part.equals( flow.getSource() ) );
    }

    private void init( boolean showingIssues, boolean isSource ) {
        boolean hasDescription = !flow.getDescription().isEmpty();
        boolean showContacts = !isSource &&  flow.isAskedFor()
                             || isSource && !flow.isAskedFor();

        Set<TransmissionMedium> unicasts = flow.getUnicasts();
        Collection<Channel> broadcasts = flow.getBroadcasts();
        final List<LocalizedActor> actors = findActors( flow, broadcasts, unicasts, queryService );

        add( new Label( "information",
                        isSource ? flow.getOutcomeTitle() : flow.getRequirementTitle() )
                .add( new AttributeModifier( "class", true,
                        new Model<String>( flow.isRequired() ? "required-information"
                                                             : "information" ) ) ),
             newUrgency(),
             new Label( "start-stop", getSignificance() ),
             new WebMarkupContainer( "eoi-lead" ).setVisible( hasDescription ),
             new WebMarkupContainer( "eoi" )
                     .add( new Label( "eoi-item", flow.getDescription() ) )
                     .setVisible( hasDescription ),

             new IssuesReportPanel( "issues", new Model<ModelObject>( flow ) )
                .setVisible( showingIssues ),

             new ChannelsBannerPanel( "channels",
                            new ResourceSpec( flow.getContactedPart() ), unicasts, broadcasts )
                .setVisible( showContacts && !flow.getChannels().isEmpty() && actors.isEmpty() ),

             new WebMarkupContainer( "actors-div" )
                .add( new ListView<LocalizedActor>( "actors", actors ) {
                    @Override
                    protected void populateItem( ListItem<LocalizedActor> item ) {
                        LocalizedActor localizedActor = item.getModel().getObject();
                        item.add( new ActorBannerPanel( "actor",
                                localizedActor.getOtherSegment( part.getSegment() ),
                                localizedActor.getActorSpec(),
                                true,
                                localizedActor.getUnicasts(),
                                localizedActor.getBroadcasts() ) );
                    }
                }
                    .add( new AttributeModifier( "class", true,
                         new Model<String>( flow.isAll() ? "all-actors" : "any-actor" ) ) ) )
                    .setVisible( showContacts && !actors.isEmpty() ) );

    }

    private Label newUrgency() {
        Label result = new Label( "urgency", flow.getMaxDelay().toString() );
        if ( flow.isCritical() )
            result.add( new AttributeModifier( "class", true, new Model<String>( "urgent" ) ) );
        return result;
    }

    private String getSignificance() {
        Flow.Significance significance = part.getSignificance( flow );
        return significance.equals( Flow.Significance.Triggers )   ? "Starts this task."
             : significance.equals( Flow.Significance.Terminates ) ? "Ends this task."
                                                                   : "";
    }

    private static List<LocalizedActor> findActors(
            Flow flow,
            Collection<Channel> broadcasts,
            Set<TransmissionMedium> unicasts,
            QueryService queryService ) {

        Set<LocalizedActor> localizedActors = new HashSet<LocalizedActor>();

        Part part = flow.getContactedPart();
        if ( part == null ) {
            Connector connector = (Connector) flow.getContactedNode();

            for ( Iterator<ExternalFlow> flows = connector.externalFlows(); flows.hasNext(); ) {
                ExternalFlow f = flows.next();
                Collection<Channel> b = f.getBroadcasts();
                Set<TransmissionMedium> u = f.getUnicasts();
                localizedActors.addAll( findActors( f, b, u, queryService ) );
            }
        } else {
            ResourceSpec spec = part.resourceSpec();
            if ( spec.isOrganization() )
                spec.setActor( Actor.UNKNOWN );
            for ( Actor a : queryService.findAllActualActors( spec ) )
                localizedActors.add( new LocalizedActor( a, part, unicasts, broadcasts ) );
        }

        List<LocalizedActor> result = new ArrayList<LocalizedActor>( localizedActors );
        Collections.sort( result );
        return result;
    }

    /**
     * An actor from a part in a segment.
     */
    public static final class LocalizedActor implements Serializable, Comparable<LocalizedActor> {

        /** Unicast media used to contact the actor. */
        private Set<TransmissionMedium> unicasts;

        /** Broadcast channels used to contact the actor. */
        private Collection<Channel> broadcasts;

        /** The actor. */
        private final Actor actor;

        /** The part. */
        private final Part part;

        private LocalizedActor( Actor actor, Part part,
                                Set<TransmissionMedium> unicasts, Collection<Channel> broadcasts ) {
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

        public Set<TransmissionMedium> getUnicasts() {
            return unicasts;
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj )
                return true;
            else if ( obj == null || getClass() != obj.getClass() ) {
                return false;
            } else {
                LocalizedActor other = (LocalizedActor) obj;
                return actor.equals( other.getActor() ) && part.equals( other.getPart() );
            }
        }

        @Override
        public int hashCode() {
            int result = actor != null ? actor.hashCode() : 0;
            result = 31 * result + ( part != null ? part.hashCode() : 0 );
            return result;
        }

        /**
         * Compares this object with the specified object for order.
         * @param   o the object to be compared.
         * @return a negative integer, zero, or a positive integer as this object
         *         is less than, equal to, or greater than the specified object.
         */
        public int compareTo( LocalizedActor o ) {
            return actor.compareTo( o.getActor() );
        }

        public ResourceSpec getActorSpec() {
            ResourceSpec spec = new ResourceSpec( part );
            if ( !actor.equals( Actor.UNKNOWN ) )
                spec.setActor( actor );
            return spec;
        }

        private Segment getOtherSegment( Segment segment ) {
            Segment s = part.getSegment();
            return segment.equals( s ) ? null : s;
        }
    }
}
