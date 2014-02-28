package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.entities.AbstractFilterableTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Plan goals panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/17/13
 * Time: 9:36 PM
 */
public class ModelGoalsPanel extends AbstractUpdatablePanel {

    private static final int PAGE_SIZE = 10;

    private GoalsTable goalsTable;

    public ModelGoalsPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addGoalsTable();
    }

    private void addGoalsTable() {
        goalsTable = new GoalsTable(
                "goalsTable",
                new PropertyModel<List<GoalWrapper>>(this, "goalWrappers")
        );
        addOrReplace( goalsTable );
    }

    public List<GoalWrapper> getGoalWrappers() {
        List<GoalWrapper> wrappers = new ArrayList<GoalWrapper>(  );
        for ( Segment segment : getCollaborationModel().getSegments() ) {
            for ( Goal goal : segment.getGoals() ) {
                wrappers.add( new GoalWrapper( segment, goal ));
            }
        }
        return wrappers;
    }

    public class GoalWrapper implements Serializable {

        private Segment segment;
        private Goal goal;

        public GoalWrapper( Segment segment, Goal goal ) {
            this.goal = goal;
            this.segment = segment;
        }

        public Goal getGoal() {
            return goal;
        }

        public Segment getSegment() {
            return segment;
        }

        public String getGoalLabel() {
            String name = goal.getName();
            if ( !name.isEmpty() ) {
                return name;
            } else {
            return ( goal.isGain()
                    ? "Achieve: "
                    : "Mitigate: " ) + goal.getPartialTitle();
            }
        }

        public String getGoalType() {
            return goal.isGain()
                    ? "Gain"
                    : "Risk mitigation";
        }

        public String getGoalCategory() {
            return goal.getCategory().getLabel( goal.isPositive() );
        }

    }

    private class GoalsTable  extends AbstractFilterableTablePanel {

        private IModel<List<GoalWrapper>> goalsModel;

        private GoalsTable( String id, IModel<List<GoalWrapper>> goalsModel ) {
            super( id, PAGE_SIZE );
            this.goalsModel = goalsModel;
            initTable();
        }

        @SuppressWarnings( "unchecked" )
        private void initTable() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeColumn( "Goal", "goalLabel", null, EMPTY, "goal.description" ));
            columns.add( makeFilterableLinkColumn(
                    "of organization(s)",
                    "goal.organization",
                    "goal.organization.name",
                    EMPTY,
                    GoalsTable.this ));
//            columns.add( makeColumn( "of type", "goalType", EMPTY ));
//            columns.add( makeColumn( "of category","goalCategory", EMPTY ));
            columns.add( makeFilterableLinkColumn(
                    "in segment",
                    "segment",
                    "segment.name",
                    EMPTY,
                    GoalsTable.this ));
            columns.add( makeFilterableLinkColumn(
                    "for phase",
                    "segment.phase",
                    "segment.phase.name",
                    EMPTY,
                    GoalsTable.this ));
            columns.add( makeFilterableLinkColumn(
                    "of event",
                    "segment.event",
                    "segment.event.name",
                    EMPTY,
                    GoalsTable.this ));

            // provider and table
            addOrReplace( new AjaxFallbackDefaultDataTable(
                    "goals",
                    columns,
                    new SortableBeanProvider<GoalWrapper>(
                            getFilteredGoals(),
                            "goalLabel" ),
                    getPageSize() ) );

        }

        @SuppressWarnings( "unchecked" )
        private List<GoalWrapper> getFilteredGoals() {
            return (List<GoalWrapper>) CollectionUtils.select(
                    goalsModel.getObject(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return !isFilteredOut( object );
                        }
                    }
            );
        }

        @Override
        protected void resetTable( AjaxRequestTarget target ) {
            initTable();
            target.add( this );
        }

    }
}
