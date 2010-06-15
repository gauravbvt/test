package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.commands.MoveParts;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.entities.AbstractFilterableTablePanel;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Segment part mover panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 10, 2010
 * Time: 4:46:15 PM
 */
public class SegmentPartMoverPanel extends AbstractUpdatablePanel implements Updatable {

    @SpringBean
    private QueryService queryService;

    /**
     * Segment model.
     */
    private IModel<Segment> segmentModel;
    private Segment destinationSegment;
    private AjaxFallbackLink<String> moveButton;
    private MovablePartsTable movablePartsTable;
    private List<Part> selectedParts;
    private DropDownChoice<Segment> destinationSegmentSelector;
    private static final int PAGE_SIZE = 10;

    public SegmentPartMoverPanel( String id, PropertyModel<Segment> segmentModel, Set<Long> expansions ) {
        super( id, segmentModel, expansions );
        this.segmentModel = segmentModel;
        selectedParts = new ArrayList<Part>();
        init();
    }

    private void init() {
        addDestinationSegmentSelector();
        addMovablePartsTable();
        addMoveButton();
        adjustFields();
    }

    private void addDestinationSegmentSelector() {
        destinationSegmentSelector = new DropDownChoice<Segment>(
                "destinationSegment",
                new PropertyModel<Segment>( this, "destinationSegment" ),
                getCandidateDestinationSegments() );
        destinationSegmentSelector.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields();
                target.addComponent( moveButton );
            }
        } );
        add( destinationSegmentSelector );
    }

    private List<Segment> getCandidateDestinationSegments() {
        List<Segment> otherSegments = new ArrayList<Segment>( queryService.list( Segment.class ) );
        otherSegments.remove( getSegment() );
        return otherSegments;
    }

    private void addMovablePartsTable() {
        movablePartsTable = new MovablePartsTable(
                "parts",
                new PropertyModel<List<MovablePart>>( this, "movableParts" ) );
        addOrReplace( movablePartsTable );
    }

    private void addMoveButton() {
        moveButton = new AjaxFallbackLink<String>( "move", new PropertyModel<String>( this, "moveButtonValue" ) ) {
            public void onClick( AjaxRequestTarget target ) {
                Change change = moveSelectedParts();
                if ( !change.isNone() ) {
                    update( target, new Change( Change.Type.Recomposed, getSegment() ) );
                    addMovablePartsTable();
                    selectedParts = new ArrayList<Part>();
                    adjustFields();
                    target.addComponent( moveButton );
                    target.addComponent( movablePartsTable );
                    target.addComponent( destinationSegmentSelector );
                }
            }
        };
        moveButton.setOutputMarkupId( true );
        add( moveButton );
    }

    public String getMoveButtonValue() {
        return "Move"
                + ( destinationSegment != null
                ? " to " + destinationSegment.getName()
                : "" );
    }

    private Change moveSelectedParts() {
        Change change;
        try {
            change = getCommander().doCommand( new MoveParts(
                    selectedParts,
                    getSegment(),
                    destinationSegment ) );
        } catch ( CommandException e ) {
            change = new Change( Change.Type.None );
            change.setScript( "alert(\"Failed to move tasks\")" );
        }
        return change;
    }

    public List<MovablePart> getMovableParts() {
        List<MovablePart> movableParts = new ArrayList<MovablePart>();
        Iterator<Part> parts = getSegment().parts();
        Commander commander = getCommander();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            MovablePart movablePart = new MovablePart( part );
            movablePart.setMovable( commander.isLockedByUser( part ) || commander.isUnlocked( part ) );
            movablePart.setSelected( selectedParts.contains( part ) );
            movableParts.add( movablePart );
        }
        return movableParts;
    }

    private void adjustFields() {
        moveButton.setEnabled( destinationSegment != null && !selectedParts.isEmpty() );
    }

    public Segment getDestinationSegment() {
        return destinationSegment;
    }

    public void setDestinationSegment( Segment destinationSegment ) {
        this.destinationSegment = destinationSegment;
    }

    public Segment getSegment() {
        return segmentModel.getObject();
    }

    public void update( AjaxRequestTarget target, Object object, String action ) {
        if ( object instanceof MovablePart ) {
            if ( action.equals( "selected" ) ) {
                Part selectedPart = ( (MovablePart) object ).getPart();
                if ( selectedParts.contains( selectedPart) ) {
                    selectedParts.remove( selectedPart );
                } else {
                    selectedParts.add( selectedPart );
                }
                adjustFields();
                target.addComponent( moveButton );
            }
        }
    }

    /**
     * Movable part.
     */
    public class MovablePart implements Serializable {

        private Part part;
        private boolean selected;
        private boolean movable;

        public MovablePart( Part part ) {
            this.part = part;
        }

        public boolean isMovable() {
            return movable;
        }

        public void setMovable( boolean movable ) {
            this.movable = movable;
        }

        public Part getPart() {
            return part;
        }

        public void setPart( Part part ) {
            this.part = part;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected( boolean selected ) {
            this.selected = selected;
        }

        public String getGoals() {
            StringBuilder sb = new StringBuilder();
            Iterator<Goal> goals = part.getGoalsAchieved().iterator();
            while ( goals.hasNext() ) {
                sb.append( goals.next().getShortLabel() );
                if ( goals.hasNext() ) sb.append( ". " );
            }
            return sb.toString();
        }
    }

    private class MovablePartsTable extends AbstractFilterableTablePanel {

        private IModel<List<MovablePart>> movablePartsModel;

        private MovablePartsTable( String id, IModel<List<MovablePart>> movablePartsModel ) {
            super( id, PAGE_SIZE );
            this.movablePartsModel = movablePartsModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeCheckBoxColumn(
                    "",
                    "selected",
                    "movable",
                    SegmentPartMoverPanel.this
            ) );
            columns.add( makeLinkColumn(
                    "Task",
                    "part",
                    "part.task",
                    EMPTY
            ) );
            columns.add( makeFilterableLinkColumn(
                    "Agent",
                    "part.actor",
                    "part.actor.name",
                    EMPTY,
                    MovablePartsTable.this
            ) );
            columns.add( makeFilterableLinkColumn(
                    "Role",
                    "part.role",
                    "part.role.name",
                    EMPTY,
                    MovablePartsTable.this
            ) );
            columns.add( makeFilterableLinkColumn(
                    "Organization",
                    "part.organization",
                    "part.organization.name",
                    EMPTY,
                    MovablePartsTable.this
            ) );
            columns.add( makeColumn(
                    "Goal",
                    "goals",
                    EMPTY
            ) );
            addOrReplace( new AjaxFallbackDefaultDataTable(
                    "movableParts",
                    columns,
                    new SortableBeanProvider<MovablePart>(
                            getFilteredMovableParts(),
                            "part.name" ),
                    getPageSize() ) );
        }

        /**
         * Find all employments in the plan that are not filtered out and are within selected name range.
         *
         * @return a list of employments.
         */
        @SuppressWarnings( "unchecked" )
        public List<MovablePart> getFilteredMovableParts() {
            return (List<MovablePart>) CollectionUtils.select(
                    movablePartsModel.getObject(),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return !isFilteredOut( obj );
                        }
                    }
            );
        }


        /**
         * {@inheritDoc}
         */
        protected void resetTable( AjaxRequestTarget target ) {
            initialize();
            target.addComponent( this );
        }
    }
}
