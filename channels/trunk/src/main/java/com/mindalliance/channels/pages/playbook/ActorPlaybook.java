package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.query.QueryService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import java.net.URI;
import java.net.URISyntaxException;
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

    public static final String DEFAULT_PIC = "images/actor.png";

    //----------------------------------------------
    public ActorPlaybook( PageParameters parameters ) {
        super( parameters );

        init( getQueryService(), getActor() );
    }

    private void init( final QueryService service, final Actor actor ) {
        String name = actor.getName();

        add(
                new Label( "title", "Playbook: " + name ),
                new Label( "header", name ).setRenderBodyOnly( true ),
                new Label( "ess", name.endsWith( "s" ) ? "" : "s" ).setRenderBodyOnly( true ),
                new Label( "planName", service.getPlan().getName() ),
                new BookmarkablePageLink<TaskPlaybook>( "top", TaskPlaybook.class ),

                new ListView<EventParts>( "events", classifyParts( service, actor ) ) {
                    @Override
                    protected void populateItem( ListItem<EventParts> item ) {
                        EventParts eventPart = item.getModelObject();
                        item.add(
                                new Label( "event-name", eventPart.getEventName() )
                                        .setRenderBodyOnly( true ),

                                newPartList( actor, eventPart.getParts() ) );
                    }
                },

                new ListView<FlowSet>( "sources",
                        sortFlows( findInitialFlows( service, actor ) ) ) {
                    @Override
                    protected void populateItem( ListItem<FlowSet> item ) {
                        FlowSet flowSet = item.getModelObject();
                        item.add(
                                new Label( "spec", flowSet.getSourceString() ).setRenderBodyOnly( true ),
                                newFlowList( actor, flowSet.getSynonymSets() ),
                                createPicture( "pic", flowSet.getActor(), "../", DEFAULT_PIC ) );
                    }
                } );
    }

    /**
     * Create a "pic" image component.
     *
     * @param id             the wicket id
     * @param object         the model object associated with an icon
     * @param prefix         url fixup to relative links to upload directory.
     * @param defaultPicture the picture to show if none is attached to the object. Hide picture if
     *                       null
     * @return a wicket component
     */
    public static Component createPicture(
            String id, ModelObject object, String prefix, String defaultPicture ) {

        String name = object == null ? "" : object.getName();
        String url;
        if ( object == null )
            url = defaultPicture;
        else {
            String s = object.getImageUrl();
            if ( s != null )
                try {
                    URI u = new URI( s );
                    if ( u.isAbsolute() )
                        url = u.toString();
                    else
                        url = prefix + u.toString();
                } catch ( URISyntaxException ignored ) {
                    url = defaultPicture;
                }
            else
                url = defaultPicture;
        }
        return new WebMarkupContainer( id )
                .add( new AttributeModifier( "src", new Model<String>( url ) ),
                        new AttributeModifier( "alt", new Model<String>( name ) ) )
                .setVisible( url != null );
    }

    private static List<EventParts> classifyParts( QueryService service, Actor actor ) {
        Map<Event, EventParts> rawEvents = new HashMap<Event, EventParts>();
        // ResourceSpec spec = ResourceSpec.with( actor );
        for ( Segment segment : service.list( Segment.class ) ) {
            for ( Part part :
                    service.getAssignments().with( segment ).with( actor ).getParts() )
                if ( part.getSegment().equals( segment ) && part.isAutoStarted() ) {
                    Event event = segment.getEvent();
                    EventParts parts = rawEvents.get( event );
                    if ( parts == null ) {
                        parts = new EventParts( segment, event );
                        rawEvents.put( event, parts );
                    }
                    parts.add( part );
                }
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

                item.add( new BookmarkablePageLink<ActorPlaybook>( "part", TaskPlaybook.class )
                        .setParameter( ACTOR_PARM, actor.getId() )
                        .setParameter( PART_PARM, part.getId() )
                        .add( new Label( "task-name", part.getTask() )
                        .setRenderBodyOnly( true ) ) );
            }
        };
    }

    private static Component newFlowList( final Actor actor, List<SynonymFlowSet> synonymFlows ) {

        return new ListView<SynonymFlowSet>( "flows", synonymFlows ) {
            @Override
            protected void populateItem( ListItem<SynonymFlowSet> item ) {
                SynonymFlowSet set = item.getModelObject();

                Component flowName = new Label( "flow-name", set.getLabel() )
                        .setRenderBodyOnly( true );

                if ( set.isMultipart() )
                    item.add( new WebMarkupContainer( "flow" )
                            .add( flowName )
                            .setRenderBodyOnly( true ) );

                else
                    item.add( new BookmarkablePageLink<ActorPlaybook>( "flow", TaskPlaybook.class )
                            .setParameter( ACTOR_PARM, actor.getId() )
                            .setParameter( PART_PARM, set.getTargetId() )
                            .add( flowName ) );

                List<Part> partList = set.getParts();
                item.add( new WebMarkupContainer( "parts-list" )
                        .add( newPartList( actor, partList ) )
                        .setVisible( partList.size() > 1 ),

                        new Label( "cause", set.getCauseString() ).setRenderBodyOnly( true ) );
            }
        };
    }

    private static Set<Flow> findInitialFlows( QueryService service, Actor actor ) {
        Set<Flow> flows = new HashSet<Flow>();
        for ( Part part : service.getAssignments().with( actor ).getParts() ) {
            for ( Iterator<Flow> iterator = part.flows(); iterator.hasNext(); ) {
                Flow flow = iterator.next();
                if ( part.equals( flow.getTarget() ) && flow.isTriggeringToTarget() )
                    flows.add( flow );
            }
        }
        return flows;
    }

    //=======================================================
    /**
     * Parts that are started by an external event.
     */
    private static final class EventParts implements Comparable<EventParts> {

        /**
         * The segment of the event.
         */
        private Segment segment;

        /**
         * The initiating event.
         */
        private final Event event;

        /**
         * The associated parts.
         */
        private final Set<Part> parts = new HashSet<Part>();

        private EventParts( Segment segment, Event event ) {
            this.segment = segment;
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
         *
         * @param o the object to be compared.
         * @return a negative integer, zero, or a positive integer as this object
         *         is less than, equal to, or greater than the specified object.
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
            return segment.getPhaseEventTitle();
        }
    }
}
