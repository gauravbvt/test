package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Phase details panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 22, 2009
 * Time: 9:27:31 AM
 */
public class PhaseDetailsPanel extends EntityDetailsPanel {

    /**
     * Web markup container.
     */
    private WebMarkupContainer moDetailsDiv;

    /**
     * Maximum number of rows in phase segment table.
     */
    private static final int MAX_ROWS = 20;


    public PhaseDetailsPanel( String id, PropertyModel<ModelEntity> entityModel, Set<Long> expansions ) {
        super( id, entityModel, expansions );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void addSpecifics( final WebMarkupContainer moDetailsDiv ) {
        this.moDetailsDiv = moDetailsDiv;
        addTimingChoice();
        addSegmentsTable();
    }


    private void addTimingChoice() {
        DropDownChoice<Phase.Timing> timingChoices = new DropDownChoice<Phase.Timing>(
                "phaseChoices",
                new PropertyModel<Phase.Timing>( this, "timing" ),
                Arrays.asList( Phase.Timing.values() ),
                new ChoiceRenderer<Phase.Timing>() {
                    public Object getDisplayValue( Phase.Timing timing ) {
                        return timing.getLabel();
                    }
                }
        );
        timingChoices.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPhase() ) );
            }
        } );
        timingChoices.setEnabled( isLockedByUser( getPhase() ) );
        moDetailsDiv.add( timingChoices );
    }

    private void addSegmentsTable() {
        PhaseSegmentTable phaseSegmentTable = new PhaseSegmentTable(
                "phaseSegmentTable",
                new PropertyModel<List<Segment>>( this, "segments"),
                MAX_ROWS
        );
        moDetailsDiv.add( phaseSegmentTable );
    }

    /**
     * Get all segments in phase.
     * @return a list of segments
     */
    public List<Segment> getSegments() {
        return getQueryService().findAllSegmentsForPhase( getPhase() );
    }

    /**
     * Get phase event timing.
     * @return an event timing
     */
    public Phase.Timing getTiming() {
        return getPhase().getTiming();
    }

    /**
     * Set phase event timing.
     * @param timing an event timing
     */
    public void setTiming( Phase.Timing timing ) {
        doCommand( new UpdatePlanObject( getUser().getUsername(), getPhase(), "timing", timing ) );
    }

    private Phase getPhase() {
        return (Phase) getEntity();
    }

    /**
     * Table with segments in phase.
     */
    public class PhaseSegmentTable extends AbstractTablePanel<Segment> {
        /**
         * Phase segments model.
         */
        private IModel<List<Segment>> segmentsModel;

        public PhaseSegmentTable( String id, PropertyModel<List<Segment>> segmentsModel, int pageSize ) {
            super( id, null, pageSize, null );
            this.segmentsModel = segmentsModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeLinkColumn(
                    "Plan segment",
                    "",
                    "name",
                    EMPTY ) );
            columns.add( makeLinkColumn(
                    "for event",
                    "event",
                    "event.name",
                    EMPTY ) );
            // provider and table
            add( new AjaxFallbackDefaultDataTable(
                    "phaseSegments",
                    columns,
                    new SortableBeanProvider<Segment>(
                            segmentsModel.getObject(),
                            "name" ),
                    getPageSize() ) );
        }
    }

}
