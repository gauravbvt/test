package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Task goals panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 11, 2009
 * Time: 6:24:53 AM
 */
public class PartGoalsPanel extends AbstractCommandablePanel {

    public PartGoalsPanel(
            String id,
            IModel<? extends Identifiable> model,
            Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        List<GoalWrapper> wrappers = getWrappedGoals();
        ListView<GoalWrapper> mitigationList = new ListView<GoalWrapper>(
                "goals",
                wrappers
        ) {
            protected void populateItem( ListItem<GoalWrapper> item ) {
                item.setOutputMarkupId( true );
                item.add( new GoalPanel(
                        "goal",
                        item ) );
                addConfirmedCell( item );
            }
        };
        add( mitigationList );
    }

    private void addConfirmedCell( ListItem<GoalWrapper> item ) {
        final GoalWrapper wrapper = item.getModelObject();
        final CheckBox confirmedCheckBox = new CheckBox(
                "confirmed",
                new PropertyModel<Boolean>( wrapper, "confirmed" ) );
        makeVisible( confirmedCheckBox, wrapper.isConfirmed() );
        confirmedCheckBox.setEnabled( isLockedByUser( getPart() ) );
        item.addOrReplace( confirmedCheckBox );
        confirmedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target,
                        new Change( Change.Type.Updated, getPart(), "goals"
                        ) );
            }
        } );
    }

    private List<GoalWrapper> getWrappedGoals() {
        List<GoalWrapper> wrappers = new ArrayList<GoalWrapper>();
        List<Goal> goals = getPart().getGoals();
        // Task goals
        for ( Goal goal : goals ) {
            wrappers.add( new GoalWrapper( goal, true ) );
        }
        GoalWrapper creationWrapper = new GoalWrapper( null, false );
        creationWrapper.setMarkedForCreation( true );
        wrappers.add( creationWrapper );
        return wrappers;
    }

    /**
     * Get edited part.
     *
     * @return a part
     */
    public Part getPart() {
        return (Part) getModel().getObject();
    }

    /**
     * Mitigation wrapper.
     */
    public class GoalWrapper implements Serializable {
        /**
         * Achieved goal. Can be null.
         */
        private Goal goal;
        /**
         * Whether goal is confirmed.
         */
        private boolean confirmed;
        /**
         * Whether being created.
         */
        private boolean markedForCreation;

        public GoalWrapper( Goal goal, boolean confirmed ) {
            this.goal = goal;
            this.confirmed = confirmed;
            markedForCreation = false;
        }

        public boolean isMarkedForCreation() {
            return markedForCreation;
        }

        public void setMarkedForCreation( boolean markedForCreation ) {
            this.markedForCreation = markedForCreation;
        }

        public Goal getGoal() {
            return goal;
        }

        public void setGoal( Goal goal ) {
            this.goal = goal;
            setConfirmed( true );
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public void setConfirmed( boolean confirmed ) {
            this.confirmed = confirmed;
            assert goal != null;
            if ( confirmed ) {
                if ( !getPart().getGoals().contains( goal ) ) {
                    doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(),
                            "goals",
                            goal,
                            UpdateObject.Action.AddUnique ) );
                }
            } else {
                if ( getPart().getGoals().contains( goal ) ) {
                    doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(),
                            "goals",
                            goal,
                            UpdateObject.Action.Remove ) );
                }
            }
        }

        public boolean canBeConfirmed() {
            return goal != null;
        }
    }

    /**
     * Panel showing goal as label or a choice of goals to be achieved.
     */
    public class GoalPanel extends Panel {
        /**
         * Item in list view this panel is a component of.
         */
        private ListItem<GoalWrapper> item;

        public GoalPanel( String id, ListItem<GoalWrapper> item ) {
            super( id );
            this.item = item;
            init();
        }

        private void init() {
            DropDownChoice<Goal> goalChoice = new DropDownChoice<Goal>(
                    "goalChoice",
                    new PropertyModel<Goal>( getWrapper(), "goal" ),
                    getCandidateMitigations(),
                    new IChoiceRenderer<Goal>() {
                        public Object getDisplayValue( Goal goal ) {
                            return goal.toString();
                        }

                        public String getIdValue( Goal goal, int index ) {
                            return Integer.toString( index );
                        }
                    }
            );
            goalChoice.add(
                    new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            update( target,
                                    new Change( Change.Type.Updated, getPart(), "goals"
                                    ) );
                        }
                    } );
            add( goalChoice );
            goalChoice.setVisible( getWrapper().isMarkedForCreation()
                    && !getCandidateMitigations().isEmpty() && isLockedByUser( getPart() ) );
            Goal goal = getWrapper().getGoal();
            Label goalLabel = new Label(
                    "goalLabel",
                    new Model<String>( goal != null ? goal.getLabel() : "" ) );
            add( goalLabel );
            goalLabel.setVisible( !getWrapper().isMarkedForCreation() );
        }

        private List<Goal> getCandidateMitigations() {
            List<Goal> candidates = new ArrayList<Goal>();
            List<Goal> goals = getPart().getGoals();
            for ( Goal goal : getPart().getSegment().getGoals() ) {
                if ( !goals.contains( goal ) ) candidates.add( goal );
            }
            return candidates;
        }

        private GoalWrapper getWrapper() {
            return item.getModelObject();
        }
    }

}
