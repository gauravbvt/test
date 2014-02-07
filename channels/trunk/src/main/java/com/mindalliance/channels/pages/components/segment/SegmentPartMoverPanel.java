/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.commands.MoveParts;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.entities.AbstractFilterableTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Segment part mover panel.
 */
public class SegmentPartMoverPanel extends AbstractUpdatablePanel {

    /**
     * How many parts to show per page in table.
     */
    private static final int PAGE_SIZE = 10;

    /**
     * All/none label.
     */
    private Label allOrNoneLabel;

    /**
     * Select all/none link.
     */
    private AjaxLink<String> allOrNoneLink;

    /**
     * Where to move selected parts.
     */
    private Segment destinationSegment;

    /**
     * Destination segment drop down.
     */
    private DropDownChoice<Segment> destinationSegmentSelector;

    /**
     * Paged, filterable, sortable table of parts to select.
     */
    private MovablePartsTable movablePartsTable;

    /**
     * Move button.
     */
    private AjaxLink<String> moveButton;

    /**
     * Segment model.
     */
    private IModel<Segment> segmentModel;

    /**
     * Selected parts to move.
     */
    private List<Part> selectedParts;

    //-------------------------------
    public SegmentPartMoverPanel(
            String id,
            IModel<Segment> segmentModel,
            Set<Long> expansions ) {
        super( id, segmentModel, expansions );
        this.segmentModel = segmentModel;
        selectedParts = new ArrayList<Part>();
        init();
    }

    private void init() {
        getCommander().getLockManager().requestLock( getUsername(), getSegment().getId() );
        addDestinationSegmentSelector();
        addSelectAllOrNone();
        addMovablePartsTable();
        addMoveButton();
        adjustFields();
    }

    public void releaseLocks() {
        getCommander().getLockManager().requestRelease(  getUsername(), getSegment().getId() );
    }

    private void addDestinationSegmentSelector() {
        destinationSegmentSelector = new DropDownChoice<Segment>( "destinationSegment",
                                                                  new PropertyModel<Segment>( this,
                                                                                              "destinationSegment" ),
                                                                  getCandidateDestinationSegments() );
        destinationSegmentSelector.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields();
                target.add( moveButton );
            }
        } );
        add( destinationSegmentSelector );
    }

    private List<Segment> getCandidateDestinationSegments() {
        List<Segment> otherSegments = new ArrayList<Segment>( getQueryService().list( Segment.class ) );
        otherSegments.remove( getSegment() );
        final Collator collator = Collator.getInstance();
        Collections.sort( otherSegments, new Comparator<Segment>() {
            @Override
            public int compare( Segment s1, Segment s2 ) {
                return collator.compare( s1.getName(), s2.getName() );
            }
        } );
        return otherSegments;
    }

    private void addSelectAllOrNone() {
        allOrNoneLink = new AjaxLink<String>( "selectAllOrNone" ) {
            @Override
            @SuppressWarnings( "unchecked" )
            public void onClick( AjaxRequestTarget target ) {
                selectedParts = isSelectAll() ?
                                (List<Part>) CollectionUtils.collect( movablePartsTable.getFilteredMovableParts(),
                                                                      new Transformer() {
                                                                          @Override
                                                                          public Object transform( Object input ) {
                                                                              return ( (MovablePart) input ).getPart();
                                                                          }
                                                                      } ) :
                                new ArrayList<Part>();
                adjustFields();
                addMovablePartsTable();
                target.add( movablePartsTable );
                target.add( allOrNoneLabel );
                target.add( allOrNoneLink );
                target.add( moveButton );
            }
        };
        add( allOrNoneLink );
        allOrNoneLabel = new Label( "allOrNone", new PropertyModel<String>( this, "allOrNone" ) );
        allOrNoneLabel.setOutputMarkupId( true );
        allOrNoneLink.add( allOrNoneLabel );
    }

    private boolean isSelectAll() {
        int movablePartsSize = movablePartsTable.getFilteredMovableParts().size();
        return movablePartsSize == 0 || movablePartsSize > selectedParts.size();
    }

    private void addMovablePartsTable() {
        movablePartsTable =
                new MovablePartsTable( "parts", new PropertyModel<List<MovablePart>>( this, "movableParts" ) );
        addOrReplace( movablePartsTable );
    }

    private void addMoveButton() {
        moveButton = new AjaxLink<String>( "move", new Model<String>( "Move tasks" ) ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                Change change = moveSelectedParts();
                if ( !change.isNone() ) {
                    update( target, new Change( Change.Type.Recomposed, getSegment() ) );
                    addMovablePartsTable();
                    selectedParts = new ArrayList<Part>();
                    adjustFields();
                    target.add( moveButton );
                    target.add( movablePartsTable );
                    target.add( destinationSegmentSelector );
                }
            }
        };
        moveButton.setOutputMarkupId( true );
        add( moveButton );
    }

    private Change moveSelectedParts() {
        Change change;
        change = getCommander().doCommand( new MoveParts( getUser().getUsername(),
                                                          selectedParts,
                                                          getSegment(),
                                                          destinationSegment ) );
        return change;
    }

    private void adjustFields() {
        moveButton.setEnabled( destinationSegment != null && !selectedParts.isEmpty() );
        makeVisible( allOrNoneLink, !movablePartsTable.getFilteredMovableParts().isEmpty() );
    }

    //-------------------------------
    /**
     * Get content of all/none label.
     *
     * @return a string
     */
    public String getAllOrNone() {
        return isSelectAll() ? "Select all" : "Select none";
    }

    /**
     * Get current segment.
     *
     * @return a segment
     */
    public Segment getSegment() {
        return segmentModel.getObject();
    }

    @Override
    public void update( AjaxRequestTarget target, Object object, String action ) {
        if ( object instanceof MovablePart ) {
            if ( action.equals( "selected" ) ) {
                Part selectedPart = ( (MovablePart) object ).getPart();
                if ( selectedParts.contains( selectedPart ) ) {
                    selectedParts.remove( selectedPart );
                } else {
                    selectedParts.add( selectedPart );
                }
                adjustFields();
                target.add( moveButton );
            }
        }
    }

    //-------------------------------
    public Segment getDestinationSegment() {
        return destinationSegment;
    }

    public void setDestinationSegment( Segment destinationSegment ) {
        this.destinationSegment = destinationSegment;
    }

    /**
     * Get list of movable parts in current segment.
     *
     * @return a list of parts
     */
    public List<MovablePart> getMovableParts() {
        List<MovablePart> movableParts = new ArrayList<MovablePart>();
        Iterator<Part> parts = getSegment().parts();
        Commander commander = getCommander();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            MovablePart movablePart = new MovablePart( part );
            movablePart.setMovable(
                    commander.isLockedByUser( getUsername(), getSegment() )
                            && commander.isLockedByUser( getUsername(), part ) || commander.isUnlocked( part ) );
            movablePart.setSelected( selectedParts.contains( part ) );
            movableParts.add( movablePart );
        }
        return movableParts;
    }

    //===============================
    /**
     * Movable part.
     */
    public class MovablePart implements Serializable {

        private boolean movable;

        private Part part;

        private boolean selected;

    //-------------------------------
        public MovablePart( Part part ) {
            this.part = part;
        }

    //-------------------------------
        /**
         * Get label for a task's goals.
         *
         * @return a string
         */
        public String getGoals() {
            StringBuilder sb = new StringBuilder();
            Iterator<Goal> goals = part.getGoalsAchieved().iterator();
            while ( goals.hasNext() ) {
                sb.append( goals.next().getShortLabel() );
                if ( goals.hasNext() )
                    sb.append( ". " );
            }
            return sb.toString();
        }

    //-------------------------------
        public Part getPart() {
            return part;
        }

        public void setPart( Part part ) {
            this.part = part;
        }

        public boolean isMovable() {
            return movable;
        }

        public void setMovable( boolean movable ) {
            this.movable = movable;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected( boolean selected ) {
            this.selected = selected;
        }
    }

    private class MovablePartsTable extends AbstractFilterableTablePanel {

        private IModel<List<MovablePart>> movablePartsModel;

    //-------------------------------
        private MovablePartsTable( String id, IModel<List<MovablePart>> movablePartsModel ) {
            super( id, PAGE_SIZE );
            this.movablePartsModel = movablePartsModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            if ( isLockedByUserIfNeeded( getSegment().getId() ) )
                columns.add( makeCheckBoxColumn( "", "selected", "movable", SegmentPartMoverPanel.this ) );
            columns.add( makeLinkColumn( "Task", "part", "part.task", EMPTY ) );
            columns.add( makeColumn( "Category", "part.category.label", EMPTY ) );
            columns.add( makeFilterableLinkColumn( "Agent",
                                                   "part.actor",
                                                   "part.actor.name",
                                                   EMPTY,
                                                   MovablePartsTable.this ) );
            columns.add( makeFilterableLinkColumn( "Role",
                                                   "part.role",
                                                   "part.role.name",
                                                   EMPTY,
                                                   MovablePartsTable.this ) );
            columns.add( makeFilterableLinkColumn( "Organization",
                                                   "part.organization",
                                                   "part.organization.name",
                                                   EMPTY,
                                                   MovablePartsTable.this ) );
            columns.add( makeColumn( "Goal", "goals", EMPTY ) );
            addOrReplace( new AjaxFallbackDefaultDataTable( "movableParts",
                                                            columns,
                                                            new SortableBeanProvider<MovablePart>(
                                                                    getFilteredMovableParts(),
                                                                    "part.name" ),
                                                            getPageSize() ) );
        }

    //-------------------------------
        /**
         * Find all employments in the plan that are not filtered out and are within selected name range.
         *
         * @return a list of employments.
         */
        @SuppressWarnings( "unchecked" )
        public List<MovablePart> getFilteredMovableParts() {
            return (List<MovablePart>) CollectionUtils.select( movablePartsModel.getObject(), new Predicate() {
                @Override
                public boolean evaluate( Object obj ) {
                    return !isFilteredOut( obj );
                }
            } );
        }

        @Override
        protected void resetTable( AjaxRequestTarget target ) {
            initialize();
            target.add( this );
        }
    }
}
