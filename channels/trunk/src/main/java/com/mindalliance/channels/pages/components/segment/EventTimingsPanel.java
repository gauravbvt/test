package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Event timings panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/11/11
 * Time: 2:46 PM
 */
public class EventTimingsPanel extends AbstractCommandablePanel {

    private static final String ANY = "Any";
    private EventTiming newEventTiming;
    private String eventLevelName;
    private WebMarkupContainer container;
    private WebMarkupContainer eventTimingsContainer;
    private WebMarkupContainer creationContainer;

    public EventTimingsPanel( String id, IModel<? extends Identifiable> iModel ) {
        super( id, iModel );
        init();
    }

    private void init() {
        reset();
        container = new WebMarkupContainer( "container" );
        container.setOutputMarkupId( true );
        container.setVisible( isLockedByUser( getSegment() ) || !getSegment().getContext().isEmpty() );
        add( container );
        addEventTimings();
        addNewEventTiming();
    }

    private void reset() {
        newEventTiming = new EventTiming();
        eventLevelName = null;
    }

    private void addEventTimings() {
        eventTimingsContainer = new WebMarkupContainer( "eventTimingsDiv" );
        eventTimingsContainer.setOutputMarkupId( true );
        container.addOrReplace( eventTimingsContainer );
        makeVisible( eventTimingsContainer, !getSegment().getContext().isEmpty() );
        eventTimingsContainer.add( makeEventTimingsList() );
    }

    private void addNewEventTiming() {
        creationContainer = new WebMarkupContainer( "creationDiv" );
        creationContainer.setOutputMarkupId( true );
        container.addOrReplace( creationContainer );
        addTimingChoice();
        addEventField();
        addLevelField();
        makeVisible( creationContainer, isLockedByUserIfNeeded( getSegment() ) );
    }

    private ListView<EventTiming> makeEventTimingsList() {
        List<EventTiming> context = getSegment().getContext();
        final int count = context.size();
        return new ListView<EventTiming>( "eventTiming", context ) {
            /** {@inheritDoc} */
            protected void populateItem( ListItem<EventTiming> item ) {
                item.setOutputMarkupId( true );
                addTimingText( item );
                addEventLink( item );
                addLevelText( item );
                addDeleteImage( item );
                item.add( new AttributeModifier(
                        "class",
                        new Model<String>( cssClasses( item, count ) ) ) );

            }
        };

    }

    private void addTimingText( ListItem<EventTiming> item ) {
        EventTiming eventTiming = item.getModelObject();
        Label timingText = new Label( "timingText", getTimingLabel( eventTiming.getTiming() ) );
        item.add( timingText );
    }

    private void addEventLink( ListItem<EventTiming> item ) {
        EventTiming eventTiming = item.getModelObject();
        ModelObjectLink eventLink = new ModelObjectLink(
                "eventLink",
                new Model<Event>( eventTiming.getEvent() ),
                new Model<String>( eventTiming.getEvent().getName() ) );
        item.add( eventLink );
    }

    private void addLevelText( ListItem<EventTiming> item ) {
        EventTiming eventTiming = item.getModelObject();
        Label levelText = new Label( "levelText", getLevelLabel( eventTiming ) );
        item.add( levelText );
    }

    private void addDeleteImage( ListItem<EventTiming> item ) {
        final EventTiming eventTiming = item.getModelObject();
        final String oldName = eventTiming.getEvent().getName();
        ConfirmedAjaxFallbackLink deleteLink = new ConfirmedAjaxFallbackLink(
                "delete",
                "Remove from segment context?" ) {
            public void onClick( AjaxRequestTarget target ) {
                doCommand( new UpdatePlanObject( getUsername(), getSegment(),
                        "context",
                        eventTiming,
                        UpdateObject.Action.Remove ) );
                getCommander().cleanup( Event.class, oldName );
                update( target,
                        new Change(
                                Change.Type.Updated,
                                getSegment(),
                                "context"
                        ) );
            }
        };
        deleteLink.setVisible( isLockedByUserIfNeeded( getSegment() ) );
        item.add( deleteLink );
    }


    private void addTimingChoice() {
        final List<Phase.Timing> candidateTimings = getCandidateTimings();
        DropDownChoice<Phase.Timing> timingDropDownChoice = new DropDownChoice<Phase.Timing>(
                "timing",
                new PropertyModel<Phase.Timing>( this, "timing" ),
                candidateTimings,
                new IChoiceRenderer<Phase.Timing>() {
                    public Object getDisplayValue( Phase.Timing timing ) {
                        return timing == null ? "Select" : getTimingLabel( timing );
                    }

                    public String getIdValue( Phase.Timing level, int index ) {
                        return Integer.toString( index );
                    }
                } );
        timingDropDownChoice.add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        if ( isEventTimingComplete() ) {
                            update( target,
                                    new Change(
                                            Change.Type.Updated,
                                            getSegment(),
                                            "context"
                                    ) );
                        }
                    }
                } );
        timingDropDownChoice.setEnabled( isLockedByUserIfNeeded( getSegment() ) );
        creationContainer.add( timingDropDownChoice );
    }

    public boolean isEventTimingComplete() {
        return newEventTiming.getTiming() != null
                && newEventTiming.getEvent() != null
                && eventLevelName != null;
    }


    private void addEventField() {
        final List<String> choices = getQueryService().findAllEntityNames( Event.class );
        AutoCompleteTextField<String> eventField = new AutoCompleteTextField<String>(
                "event",
                new PropertyModel<String>( this, "eventName" ),
                getAutoCompleteSettings() ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( getQueryService().likelyRelated( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();

            }
        };
        eventField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                if ( isEventTimingComplete() ) {
                    update( target,
                            new Change(
                                    Change.Type.Updated,
                                    getSegment(),
                                    "context"
                            ) );
                }
            }
        } );
        eventField.setOutputMarkupId( true );
        eventField.setEnabled( isLockedByUser( getSegment() ) );
        addInputHint( eventField, "The name of an event" );
        creationContainer.add( eventField );
    }

    private void addLevelField() {
        final List<String> choices = getLevelNameChoices();
        DropDownChoice<String> levelDropDownChoice = new DropDownChoice<String>(
                "level",
                new PropertyModel<String>( this, "levelName" ),
                choices );
        levelDropDownChoice.add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        if ( isEventTimingComplete() ) {
                            update( target,
                                    new Change(
                                            Change.Type.Updated,
                                            getSegment(),
                                            "context"
                                    ) );
                        }
                    }
                } );
        levelDropDownChoice.setEnabled( isLockedByUser( getSegment() ) );
        creationContainer.add( levelDropDownChoice );
    }

    private List<String> getLevelNameChoices() {
        List<String> choices = new ArrayList<String>();
        choices.add( ANY );
        for ( Level level : Level.values() ) {
            choices.add( level.name() );
        }
        return choices;
    }

    private List<Phase.Timing> getCandidateTimings() {
        List<Phase.Timing> candidates = new ArrayList<Phase.Timing>();
        candidates.add( Phase.Timing.Concurrent );
        candidates.add( Phase.Timing.PostEvent );
        return candidates;
    }

    private String cssClasses( ListItem<EventTiming> item, int count ) {
        int index = item.getIndex();
        String cssClasses = index % 2 == 0 ? "even" : "odd";
        if ( index == count - 1 ) cssClasses += " last";
        return cssClasses;
    }

    private Segment getSegment() {
        return (Segment) getModel().getObject();
    }

    public static String getTimingLabel( Phase.Timing timing ) {
        return Phase.Timing.Concurrent == timing ? "During" : "After";
    }

    public static String getLevelLabel( EventTiming eventTiming ) {
        Level level = eventTiming.getEventLevel();
        if ( level == null )
            return "";
        else
            return "(" + level.getName().toLowerCase() + ")";
    }

    private void addIfComplete() {
        if ( isEventTimingComplete() ) {
            if ( !getSegment().getContext().contains( newEventTiming ) ) {
                doCommand( new UpdatePlanObject( getUser().getUsername(), getSegment(),
                        "context",
                        newEventTiming,
                        UpdateObject.Action.Add ) );
            }
        }
    }

    public String getEventName() {
        Event event = newEventTiming.getEvent();
        return event == null ? "" : event.getName();
    }

    public void setEventName( String name ) {
        if ( name != null && !name.isEmpty() ) {
            String oldName = getEventName();
            Event event = getQueryService().safeFindOrCreateType( Event.class, name );
            newEventTiming.setEvent( event );
            addIfComplete();
            getCommander().cleanup( Event.class, oldName );
        }
    }

    public Phase.Timing getTiming() {
        return newEventTiming.getTiming();
    }

    public void setTiming( Phase.Timing timing ) {
        newEventTiming.setTiming( timing );
    }

    public String getLevelName() {
        return eventLevelName;
    }

    public void setLevelName( String val ) {
        eventLevelName = val;
        if ( val.equals( ANY ) ) {
            newEventTiming.setEventLevel( null );
        } else {
            newEventTiming.setEventLevel( Level.valueOf( val ) );
        }
        addIfComplete();
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        reset();
        addEventTimings();
        addNewEventTiming();
        target.add( eventTimingsContainer );
        target.add( creationContainer );
        super.updateWith( target, change, updated );
    }


}
