package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.User;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

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

    //----------------------------------------------
    public ActorPlaybook( PageParameters parameters ) {
        super( parameters );

        init( getQueryService(), getUser(), getActor() );
    }

    private void init( QueryService service, User user, final Actor actor ) {
        String name = actor.getName();

        add(
            new Label( "title", name ),
            new Label( "header", name ).setRenderBodyOnly( true ),

            new BookmarkablePageLink<TaskPlaybook>( "top", TaskPlaybook.class ),
            new Label( "user", user.getUsername() ).setRenderBodyOnly( true ),

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
                        createPicture( flowSet.getActor() ) );
                }
            } );
    }

    private static Component createPicture( Actor actor ) {
        String name = actor == null ? "" : actor.getName();
        String url = actor == null ? "images/actor.png" : actor.getImageUrl();

        return new WebMarkupContainer( "pic" )
                .add( new AttributeModifier( "src", new Model<String>( "../" + url ) ),
                      new AttributeModifier( "alt", new Model<String>( name ) ) )
                .setVisible( url != null );
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
