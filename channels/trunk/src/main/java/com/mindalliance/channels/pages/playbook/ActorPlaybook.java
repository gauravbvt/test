package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.User;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An actor's playbook.
 */
public class ActorPlaybook extends PlaybookPage {

    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger( ActorPlaybook.class );

    //----------------------------------------------
    public ActorPlaybook( PageParameters parameters ) {
        super( parameters );

        init( getQueryService(), getUser(), getActor() );
    }

    private void init( QueryService service, User user, final Actor actor ) {
        String name = actor.getName();
        add( new Label( "title", name ) );

        Label header = new Label( "header", name );
        header.setRenderBodyOnly( true );
        add( header );

        add( new BookmarkablePageLink<TaskPlaybook>( "top", TaskPlaybook.class ) );

        Label userField = new Label( "user", user.getUsername() );
        userField.setRenderBodyOnly( true );
        add( userField );

        add( new ListView<EventParts>( "events", classifyParts( service, actor ) ) {
            @Override
            protected void populateItem( ListItem<EventParts> item ) {
                EventParts eventPart = item.getModelObject();
                Label eventName = new Label( "event-name", eventPart.getEventName() );
                eventName.setRenderBodyOnly( true );
                item.add( eventName );
                item.add( newPartList( actor, eventPart.getParts() ) );
            }
        } );

        add( new ListView<IncomingFlows>( "sources", classifyFlows( service, actor ) ) {
            @Override
            protected void populateItem( ListItem<IncomingFlows> item ) {
                IncomingFlows incomingFlows = item.getModelObject();
                Label specName = new Label( "spec",
                    incomingFlows.isFrom( actor ) ? "self" : incomingFlows.getSourceString() );
                specName.setRenderBodyOnly( true );
                item.add( specName );
                item.add( newFlowList( actor, incomingFlows.getFlowSpecs() ) );
            }
        } );
    }

    private static List<EventParts> classifyParts( QueryService service, Actor actor ) {
        Map<Event, EventParts> rawEvents = new HashMap<Event, EventParts>();
        ResourceSpec spec = ResourceSpec.with( actor );
        for ( Scenario scenario : service.list( Scenario.class ) )
            for ( Part part : service.findAllParts( scenario, spec ) )
                if ( part.isStartsWithScenario() ) {
                    Event event = scenario.getEvent();
                    EventParts parts = rawEvents.get( event );
                    if ( parts == null ) {
                        parts = new EventParts( event );
                        rawEvents.put( event, parts );
                    }
                    parts.add( part );
                }

        List<EventParts> events = new ArrayList<EventParts>( rawEvents.values() );
        Collections.sort( events );
        return events;
    }

    private static Component newPartList( final Actor actor, final List<Part> parts ) {
        return new ListView<Part>( "parts", parts ) {
            @Override
            protected void populateItem( ListItem<Part> item ) {
                Part part = item.getModelObject();

                BookmarkablePageLink<ActorPlaybook> pageLink =
                        new BookmarkablePageLink<ActorPlaybook>( "part", TaskPlaybook.class );
                pageLink.setParameter( ACTOR_PARM, actor.getId() );
                pageLink.setParameter( PART_PARM, part.getId() );

                Label partName = new Label( "task-name", part.getTask() );
                partName.setRenderBodyOnly( true );
                pageLink.add( partName );

                item.add( pageLink );
            }
        };
    }

    private static Component newFlowList( final Actor actor, final List<FlowSpec> flows ) {
        return new ListView<FlowSpec>( "flows", flows ) {
            @Override
            protected void populateItem( ListItem<FlowSpec> item ) {
                FlowSpec spec = item.getModelObject();

                Label flowName = new Label( "flow-name", spec.getLabel() );
                flowName.setRenderBodyOnly( true );

                if ( spec.isMultipart() ) {
                    WebMarkupContainer markupContainer = new WebMarkupContainer( "flow" );
                    markupContainer.add( flowName );
                    markupContainer.setRenderBodyOnly( true );
                    item.add( markupContainer );

                } else {
                    BookmarkablePageLink<ActorPlaybook> pageLink =
                            new BookmarkablePageLink<ActorPlaybook>( "flow", TaskPlaybook.class );
                    pageLink.setParameter( ACTOR_PARM, actor.getId() );
                    pageLink.setParameter( PART_PARM, spec.getTargetId() );
                    pageLink.add( flowName );
                    item.add( pageLink );
                }

                WebMarkupContainer partsContainer = new WebMarkupContainer( "parts-list" );
                List<Part> partList = spec.getParts();
                partsContainer.add( newPartList( actor, partList ) );
                partsContainer.setVisible( partList.size() > 1 );
                item.add( partsContainer );

                Label cause = new Label( "cause", spec.getCauseString() );
                cause.setRenderBodyOnly( true );
                item.add( cause );
            }
        };
    }

    /**
     * Classify incoming flows that start tasks according to source.
     * Resolve sources to single actors if appropriate.
     * @param service the service to use
     * @param actor the target actor
     * @return flows, sorted by resource specs.
     */
    private static List<IncomingFlows> classifyFlows( QueryService service, Actor actor ) {
        Map<ResourceSpec, IncomingFlows> sources = new HashMap<ResourceSpec, IncomingFlows>();

        for ( Flow flow : findInitialFlows( service, actor ) ) {
            Node source = flow.getSource();
            if ( source instanceof Part ) {
                ResourceSpec partSpec = ( (Part) source ).resourceSpec();
                List<Actor> actors = service.findAllActors( partSpec );
                ResourceSpec spec = actors.size() == 1 ? ResourceSpec.with( actors.get( 0 ) )
                                                       : partSpec;

                IncomingFlows incomingFlows = sources.get( spec );
                if ( incomingFlows == null ) {
                    incomingFlows = new IncomingFlows( spec );
                    sources.put( spec, incomingFlows );
                }

                incomingFlows.add( flow );
            } else
                LOG.warn( "Not treating {}", source.getClass() );
        }

        List<IncomingFlows> result = new ArrayList<IncomingFlows>( sources.values() );
        Collections.sort( result );
        return result;
    }

    private static Set<Flow> findInitialFlows( QueryService service, Actor actor ) {
        ResourceSpec spec = ResourceSpec.with( actor );

        Set<Flow> flows = new HashSet<Flow>();
        for ( Part part : service.findAllParts( null, spec ) )
            for ( Iterator<Flow> iterator = part.flows(); iterator.hasNext(); ) {
                Flow flow = iterator.next();
                if ( part.equals( flow.getTarget() ) && flow.isTriggeringToTarget() )
                    flows.add( flow );
            }

        return flows;
    }

    //=======================================================
    /**
     *  Incoming flows from the same source, regardless of scenarios.
     */
    private static final class IncomingFlows implements Comparable<IncomingFlows> {

        /** The source specification. */
        private final ResourceSpec source;

        /** Initiating flows from the source. */
        private final Map<String,FlowSpec> flowSpecs = new HashMap<String,FlowSpec>();

        private IncomingFlows( ResourceSpec source ) {
            this.source = source;
        }

        public ResourceSpec getSource() {
            return source;
        }

        @Override
        public String toString() {
            return source.toString();
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj )
                return true;

            return obj != null && getClass() == obj.getClass()
                   && source.equals( ( (IncomingFlows) obj ).getSource() );
        }

        @Override
        public int hashCode() {
            return source.hashCode();
        }

        /**
         * Compares this object with the specified object for order.  Returns a
         * negative integer, zero, or a positive integer as this object is less
         * than, equal to, or greater than the specified object.
         *
         * @param  o the object to be compared.
         * @return a negative integer, zero, or a positive integer as this object
         *         is less than, equal to, or greater than the specified object.
         */
        public int compareTo( IncomingFlows o ) {
            return source.compareTo( o.getSource() );
        }

        public void add( Flow flow ) {
            String key = flow.getName();
            FlowSpec spec = flowSpecs.get( key );
            if ( spec == null )
                flowSpecs.put( key, new FlowSpec( flow ) );
            else
                spec.add( flow );
        }

        public List<FlowSpec> getFlowSpecs() {
            List<FlowSpec> result = new ArrayList<FlowSpec>( flowSpecs.values() );
            Collections.sort( result );
            return result;
        }

        private String getSourceString() {
            return source.toString();
        }

        public boolean isFrom( Actor actor ) {
            return source.isActor() && actor.equals( source.getActor() );
        }
    }

    //=======================================================
    /**
     * Specification for a flow section.
     */
    private static final class FlowSpec implements Comparable<FlowSpec> {

        /** The flows contributing to this spec. */
        private final Set<Flow> flows = new HashSet<Flow>();

        private FlowSpec( Flow flow ) {
            flows.add( flow );
        }

        public void add( Flow flow ) {
            flows.add( flow );
        }

        public String getLabel() {
            return getSingleFlow().getName();
        }

        private Flow getSingleFlow() {
            return flows.iterator().next();
        }

        public String getCauseString() {
            StringBuilder result = new StringBuilder();
            result.append( "because of " );

            Iterator<Event> it = getCauses().iterator();
            do {
                result.append( it.next().getName().toLowerCase() );
                if ( it.hasNext() )
                    result.append( " or " );
            } while ( it.hasNext() );

            return result.toString();
        }

        private Set<Event> getCauses() {
            Set<Event> eventCauses = new HashSet<Event>();
            for ( Flow flow : flows )
                eventCauses.add( flow.getSource().getScenario().getEvent() );

            return eventCauses;
        }

        /**
         * Get the actual parts associated with this spec.
         * @return a list of parts
         */
        public List<Part> getParts() {
            Set<Part> parts = new HashSet<Part>();
            for ( Flow flow : flows )
                parts.add( (Part) flow.getTarget() );

            List<Part> result = new ArrayList<Part>( parts );
            Collections.sort( result );
            return result;
        }

        public boolean isMultipart() {
            Set<Node> parts = new HashSet<Node>();
            for ( Flow flow : flows )
                parts.add( flow.getTarget() );
            return parts.size() > 1;
        }

        /**
         * Compares this object with the specified object for order.  Returns a
         * negative integer, zero, or a positive integer as this object is less
         * than, equal to, or greater than the specified object.
         *
         * @param  o the object to be compared.
         * @return a negative integer, zero, or a positive integer as this object
         *         is less than, equal to, or greater than the specified object.
         */
        public int compareTo( FlowSpec o ) {
            return getLabel().compareTo( o.getLabel() );
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj )
                return true;

            if ( obj != null && getClass() == obj.getClass() && obj instanceof FlowSpec )
                return getLabel().equals( ( (FlowSpec) obj ).getLabel() );

            return false;
        }

        @Override
        public int hashCode() {
            return getLabel().hashCode();
        }

        public long getTargetId() {
            return getSingleFlow().getTarget().getId();
        }
    }

    //=======================================================
    /**
     * Parts that are started by an external event.
     */
    private static final class EventParts implements Comparable<EventParts> {

        /** The initiating event. */
        private final Event event;

        /** The associated parts. */
        private final Set<Part> parts = new HashSet<Part>();

        private EventParts( Event event ) {
            this.event = event;
        }

        public Event getEvent() {
            return event;
        }

        public List<Part> getParts() {
            List<Part> result = new ArrayList<Part>( parts );
            Collections.sort( result );
            return result;
        }

        /**
         * Compares this object with the specified object for order.  Returns a
         * negative integer, zero, or a positive integer as this object is less
         * than, equal to, or greater than the specified object.
         * @param   o the object to be compared.
         * @return a negative integer, zero, or a positive integer as this object
         * is less than, equal to, or greater than the specified object.
         */
        public int compareTo( EventParts o ) {
            return event.compareTo( o.getEvent() );
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj )
                return true;

            if ( obj == null || getClass() != obj.getClass() )
                return false;

            return event.equals( ( (EventParts) obj ).getEvent() );
        }

        @Override
        public int hashCode() {
            return event.hashCode();
        }

        public void add( Part part ) {
            parts.add( part );
        }

        private String getEventName() {
            String name = event.getName();
            return name.toLowerCase();
        }
    }
}
