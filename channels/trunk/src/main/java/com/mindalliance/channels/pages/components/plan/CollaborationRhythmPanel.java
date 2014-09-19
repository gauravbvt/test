package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.time.TimeUnit;
import com.mindalliance.channels.core.model.time.Tranche;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/6/14
 * Time: 2:59 PM
 */
public class CollaborationRhythmPanel extends AbstractUpdatablePanel {

    private static String ANYPHASE = "ALL PHASES";
    private static String ANYEVENT = "ALL EVENTS";

    private TimeUnit timeUnit = TimeUnit.Day;
    private Phase phase = Phase.UNKNOWN;
    private Event event = Event.UNKNOWN;
    private boolean showAssigned = false;
    private WebMarkupContainer rhythmTable;
    private List<Part> repeatingParts;
    private WebMarkupContainer noRepeatingTasks;
    // Caching
    private List<Part> filteredRepeatingParts;
    private List<Segment> allSegments;
    private List<Tranche> allTranches;
    private WebMarkupContainer filtersContainer;


    public CollaborationRhythmPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addFilters();
        addNoRepeatingTasks();
        addRhythmTable();
    }

    private void addFilters() {
        filtersContainer = new WebMarkupContainer( "filters" );
        filtersContainer.setOutputMarkupId( true );
        addOrReplace( filtersContainer );
        addTimeUnitChoice();
        addPhasesChoice();
        addEventsChoice();
        addShowAssigned();
    }

    private void addNoRepeatingTasks() {
        noRepeatingTasks = new WebMarkupContainer( "noRepeatingTasks" );
        noRepeatingTasks.setOutputMarkupId( true );
        noRepeatingTasks.add( new Label( "noRepeating", makeNoRepeatingTaskMessage() ) );
        makeVisible( noRepeatingTasks, findFilteredRepeatingParts().isEmpty() );
        addOrReplace( noRepeatingTasks );
    }

    private String makeNoRepeatingTaskMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append( "There are no " )
                .append( timeUnit.asAdverb() )
                .append( " repeating tasks" );
        if ( !phase.isUnknown() ) {
            sb.append( " in the phase - " )
                    .append( phase.getLabel() );
        }
        if ( !event.isUnknown() ) {
            sb.append( phase.isUnknown() ? " for " : ", of " )
                    .append( "the event - " )
                    .append( event.getLabel() );
        }
        return sb.toString();
    }

    private void addTimeUnitChoice() {
        ListView<TimeUnit> timeUnitListView = new ListView<TimeUnit>(
                "timeUnits",
                new PropertyModel<List<TimeUnit>>( this, "timeUnits" ) ) {
            @Override
            protected void populateItem( ListItem<TimeUnit> item ) {
                final TimeUnit tu = item.getModelObject();
                AjaxLink<String> timeUnitLink = new AjaxLink<String>( "timeUnitLink" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        setTimeUnit( tu );
                        addFilters();
                        target.add( filtersContainer );
                        refreshPanel( target );
                    }
                };
                if ( tu.equals( getTimeUnit() ) ) {
                    timeUnitLink.add( new AttributeModifier( "class", "active" ) );
                }
                timeUnitLink.add( new Label( "timeUnit", tu.asAdverb() ) );
                item.add( timeUnitLink );
            }
        };
        filtersContainer.add( timeUnitListView );
    }

    private void refreshPanel( AjaxRequestTarget target ) {
        filteredRepeatingParts = null;
        allSegments = null;
        allTranches = null;
        addNoRepeatingTasks();
        addRhythmTable();
        target.add( noRepeatingTasks );
        target.add( rhythmTable );
    }

    private void addPhasesChoice() {
        DropDownChoice<Phase> phaseChoice = new DropDownChoice<Phase>(
                "phases",
                new PropertyModel<Phase>( this, "phase" ),
                new PropertyModel<List<Phase>>( this, "phases" ),
                new ChoiceRenderer<Phase>() {
                    @Override
                    public Object getDisplayValue( Phase p ) {
                        return p.isUnknown()
                                ? ANYPHASE
                                : p.getLabel();
                    }
                }
        );
        phaseChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                refreshPanel( target );
            }
        } );
        filtersContainer.add( phaseChoice );
    }

    private void addEventsChoice() {
        DropDownChoice<Event> eventChoice = new DropDownChoice<Event>(
                "events",
                new PropertyModel<Event>( this, "event" ),
                new PropertyModel<List<Event>>( this, "events" ),
                new ChoiceRenderer<Event>() {
                    @Override
                    public Object getDisplayValue( Event e ) {
                        return e.isUnknown()
                                ? ANYEVENT
                                : e.getLabel();
                    }
                }
        );
        eventChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                refreshPanel( target );
            }
        } );
        filtersContainer.add( eventChoice );
    }


    private void addShowAssigned() {
        AjaxCheckBox showAgentsCheckBox = new AjaxCheckBox(
                "showAssigned",
                new PropertyModel<Boolean>( this, "showAssigned" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                refreshPanel( target );
            }
        };
        filtersContainer.add( showAgentsCheckBox );
    }

    private void addRhythmTable() {
        rhythmTable = new WebMarkupContainer( "rhythmTable" );
        rhythmTable.setOutputMarkupId( true );
        makeVisible( rhythmTable, !findFilteredRepeatingParts().isEmpty() );
        addOrReplace( rhythmTable );
        addRhythmTableHeader();
        addRhythmTableBody();
    }

    private void addRhythmTableHeader() {
        rhythmTable.add( new Label( "subTimeUnit", TimeUnit.getSubUnitOf( timeUnit ).toString().toLowerCase() ) );
        ListView<Tranche> tranchesListView = new ListView<Tranche>(
                "tranches",
                findAllTranches()
        ) {
            @Override
            protected void populateItem( ListItem<Tranche> item ) {
                item.add( new Label( "tranche", item.getModelObject().getLabel() ) );
            }
        };
        rhythmTable.add( tranchesListView );
    }

    private void addRhythmTableBody() {
        ListView<Segment> segmentRowsListView = new ListView<Segment>(
                "segmentRows",
                findAllSegments()
        ) {
            @Override
            protected void populateItem( ListItem<Segment> item ) {
                item.add( new Label( "segment", item.getModelObject().getLabel() ) );
                addAnyTimeTasks( item );
                addTrancheTasks( item );
            }
        };
        rhythmTable.add( segmentRowsListView );
    }

    private void addAnyTimeTasks( ListItem<Segment> segmentRowItem ) {
        Segment segment = segmentRowItem.getModelObject();
        List<Part> anytimeTasks = findAnytimePartsIn( segment );
        WebMarkupContainer anytimeTasksContainer = new WebMarkupContainer( "anytimeTasksContainer" );
        if ( !anytimeTasks.isEmpty() ) {
            anytimeTasksContainer.add( new AttributeModifier( "class", "active" ) );
        }
        segmentRowItem.add( anytimeTasksContainer );
        ListView<Part> anytimePartsListView = new ListView<Part>(
                "anytimeTasks",
                anytimeTasks
        ) {
            @Override
            protected void populateItem( ListItem<Part> item ) {
                Part part = item.getModelObject();
                item.add( new ModelObjectLink(
                        "taskLink",
                        item.getModel(),
                        new Model<String>( textFor( part ) ) ) );
                item.add( makeAssignedList( part ) );
                addTipTitle( item, part.getName() );
            }
        };
        anytimeTasksContainer.add( anytimePartsListView );
    }

    private Component makeAssignedList( Part part ) {
        WebMarkupContainer assignedContainer = new WebMarkupContainer( "assigned" );
        List<Assignment> assignments = isShowAssigned()
                ? getQueryService().findAllAssignments( part, false )
                : new ArrayList<Assignment>();
        assignedContainer.setVisible( !assignments.isEmpty() );
        ListView<Assignment> assignedListView = new ListView<Assignment>(
                "assignedActors",
                assignments ) {
            @Override
            protected void populateItem( ListItem<Assignment> item ) {
                Actor actor = item.getModelObject().getActor();
                ModelObjectLink actorLink = new ModelObjectLink(
                        "actorLink",
                        new Model<Actor>( actor ),
                        new Model<String>( actor.getLabel() ) );
                item.add( actorLink );
            }
        };
        assignedContainer.add( assignedListView );
        return assignedContainer;
    }

    private String textFor( Part part ) {
        StringBuilder sb = new StringBuilder();
        sb.append( part.getTaskLabel() );
        int skip = part.getCycle().getSkip();
        if ( skip > 1 ) {
            sb.append( " (every " );
            sb.append( skip == 2 ? "other" : skip )
                    .append( " " )
                    .append( timeUnit.name().toLowerCase() )
                    .append( skip == 2 ? "" : "s" );
            sb.append( ")" );
        }
        return sb.toString();
    }

    private void addTrancheTasks( ListItem<Segment> segmentRowItem ) {
        final Segment segment = segmentRowItem.getModelObject();
        List<Tranche> tranches = findAllTranches();
        ListView<Tranche> tranchesListView = new ListView<Tranche>(
                "segmentTranches",
                tranches
        ) {
            @Override
            protected void populateItem( ListItem<Tranche> item ) {
                addSegmentTrancheTasks( segment, item );
            }
        };
        segmentRowItem.add( tranchesListView );
    }

    private void addSegmentTrancheTasks( Segment segment, ListItem<Tranche> item ) {
        List<Part> segmentTrancheParts = findPartsInSegmentAndTranche( segment, item.getModelObject() );
        WebMarkupContainer trancheTasksContainer = new WebMarkupContainer( "trancheTasksContainer" );
        if ( !segmentTrancheParts.isEmpty() ) {
            trancheTasksContainer.add( new AttributeModifier( "class", "active" ) );
        }
        ListView<Part> segmentTranchePartsListView = new ListView<Part>(
                "trancheTasks",
                segmentTrancheParts
        ) {
            @Override
            protected void populateItem( ListItem<Part> item ) {
                Part part = item.getModelObject();
                item.add( new ModelObjectLink(
                        "taskLink",
                        item.getModel(),
                        new Model<String>( textFor( item.getModelObject() ) ) ) );
                item.add( makeAssignedList( part ) );
                addTipTitle( item, item.getModelObject().getName() );
            }
        };
        trancheTasksContainer.add( segmentTranchePartsListView );
        item.add( trancheTasksContainer );
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit( TimeUnit timeUnit ) {
        this.timeUnit = timeUnit;
    }

    public List<TimeUnit> getTimeUnits() {
        List<TimeUnit> timeUnits = new ArrayList<TimeUnit>( Arrays.asList( TimeUnit.values() ) );
        timeUnits.remove( TimeUnit.Second );
        timeUnits.remove( TimeUnit.Minute );
        return timeUnits;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase( Phase phase ) {
        this.phase = phase;
    }

    public List<Phase> getPhases() {
        List<Phase> results = new ArrayList<Phase>();
        List<Phase> phases = new ArrayList<Phase>( getQueryService().listActualEntities( Phase.class, true ) );
        Collections.sort( phases, new Comparator<Phase>() {
            @Override
            public int compare( Phase phase1, Phase phase2 ) {
                return phase1.getLabel().compareTo( phase2.getLabel() );
            }
        } );
        results.add( Phase.UNKNOWN );
        results.addAll( phases );
        return results;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent( Event event ) {
        this.event = event;
    }

    public List<Event> getEvents() {
        List<Event> results = new ArrayList<Event>();
        List<Event> events = new ArrayList<Event>( getQueryService().listTypeEntities( Event.class, true ) );
        Collections.sort( events );
        results.add( Event.UNKNOWN );
        results.addAll( events );
        return results;
    }

    public boolean isShowAssigned() {
        return showAssigned;
    }

    public void setShowAssigned( boolean showAssigned ) {
        this.showAssigned = showAssigned;
    }

    @SuppressWarnings("unchecked")
    private List<Part> findRepeatingParts() {
        if ( repeatingParts == null ) {
            repeatingParts = (List<Part>) CollectionUtils.select(
                    getCommunityService().list( Part.class ),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (Part) object ).isRepeating();
                        }
                    }
            );
        }
        return repeatingParts;
    }

    @SuppressWarnings("unchecked")
    private List<Part> findFilteredRepeatingParts() {
        if ( filteredRepeatingParts == null ) {
            filteredRepeatingParts = (List<Part>) CollectionUtils.select(
                    findRepeatingParts(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return matchesFilters( (Part) object );
                        }
                    }
            );
        }
        return filteredRepeatingParts;
    }

    private boolean matchesFilters( Part part ) {
        return ( part.getCycle().getTimeUnit().equals( timeUnit ) )
                && ( event.isUnknown() || part.getSegment().getEvent().narrowsOrEquals( event ) )
                && ( phase.isUnknown() || part.getSegment().getPhase().narrowsOrEquals( phase ) );
    }

    private List<Tranche> findAllTranches() {
        if ( allTranches == null ) {
            Set<Tranche> tranches = new HashSet<Tranche>();
            for ( Part repeatingPart : findFilteredRepeatingParts() ) {
                tranches.addAll( repeatingPart.getCycle().getTranches() );
            }
            allTranches = new ArrayList<Tranche>( tranches );
            Collections.sort( allTranches );
        }
        return allTranches;
    }

    private List<Segment> findAllSegments() {
        if ( allSegments == null ) {
            Set<Segment> segments = new HashSet<Segment>();
            for ( Part repeatingPart : findFilteredRepeatingParts() ) {
                segments.add( repeatingPart.getSegment() );
            }
            allSegments = new ArrayList<Segment>( segments );
            Collections.sort( allSegments );
        }
        return allSegments;
    }

    @SuppressWarnings("unchecked")
    private List<Part> findPartsIn( final Segment segment ) {
        return (List<Part>) CollectionUtils.select(
                findFilteredRepeatingParts(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Part part = (Part) object;
                        return part.getSegment().equals( segment );
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    private List<Part> findAnytimePartsIn( final Segment segment ) {
        return (List<Part>) CollectionUtils.select(
                findPartsIn( segment ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Part part = (Part) object;
                        return part.getCycle().getTranches().isEmpty();
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    private List<Part> findPartsInSegmentAndTranche( Segment segment, final Tranche tranche ) {
        return (List<Part>) CollectionUtils.select(
                findPartsIn( segment ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Part part = (Part) object;
                        return part.getCycle().getTranches().contains( tranche );
                    }
                }
        );
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        super.refresh( target, change, aspect );
        refreshPanel( target );
    }


}