package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.RemoveGoal;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import com.mindalliance.channels.pages.components.entities.EntityReferencePanel;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Goal list panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 8, 2009
 * Time: 3:02:51 PM
 */
public class GoalListPanel extends AbstractCommandablePanel {
    /**
     * Maximum number of rows in goal-seeking task table before paging.
     */
    private static final int MAX_TASK_ROWS = 5;
    /**
     * Collator.
     */
    private static Collator collator = Collator.getInstance();
    /**
     * Risk.
     */
    private final static String RISK = "Mitigate risk of";
    /**
     * Gain.
     */
    private final static String GAIN = "Achieve";
    /**
     * Risk or gain.
     */
    private static String[] INTENTS = {RISK, GAIN};
    /**
     * Risks container.
     */
    private WebMarkupContainer goalsContainer;
    /**
     * More... container.
     */
    private WebMarkupContainer moreContainer;
    /**
     * Goals for which achiever tasks are shown.
     */
    private GoalWrapper selectedGoal;

    public GoalListPanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
        init();
    }

    private void init() {
        goalsContainer = new WebMarkupContainer( "goalsDiv" );
        goalsContainer.setOutputMarkupId( true );
        add( goalsContainer );
        goalsContainer.add( makeGoalsTable() );
        moreContainer = new WebMarkupContainer( "moreDiv" );
        moreContainer.setOutputMarkupId( true );
        add( moreContainer );
        initLabel();
        addDescriptionField();
        moreContainer.add( makeTasksTable() );
        makeVisible( moreContainer, false );
    }

    private void initLabel() {
        Label goalLabel = new Label( "goalLabel", new PropertyModel<String>( this, "goalLabel" ) );
        goalLabel.setOutputMarkupId( true );
        moreContainer.addOrReplace( goalLabel );
    }


    private ListView<GoalWrapper> makeGoalsTable() {
        List<GoalWrapper> riskWrappers = getWrappedGoals();
        return new ListView<GoalWrapper>( "goal", riskWrappers ) {
            /** {@inheritDoc} */
            protected void populateItem( ListItem<GoalWrapper> item ) {
                item.setOutputMarkupId( true );
                addKindCell( item );
                addLevelCell( item );
                addCategoryCell( item );
                addOrganizationCell( item );
                addEndsWithSegment( item );
                addDeleteImage( item );
                addShowMoreCell( item );
            }
        };
    }

    private void addKindCell( final ListItem<GoalWrapper> item ) {
        final GoalWrapper wrapper = item.getModelObject();
        final List<String> candidateKinds = Arrays.asList( INTENTS );
        DropDownChoice<String> kindDropDownChoice = new DropDownChoice<String>(
               "kind",
                new PropertyModel<String>( wrapper, "kind" ),
                candidateKinds
        );
        kindDropDownChoice.add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        if ( !wrapper.isUndergoingCreation() ) {
                            update( target, new Change( Change.Type.Updated, getSegment(), "goals" ) );
                        }
                        addCategoryCell( item );
                        target.addComponent( item );
                    }
                } );
        kindDropDownChoice.setEnabled( getPlan().isDevelopment() );
        item.add( kindDropDownChoice );
    }

    private void addLevelCell( final ListItem<GoalWrapper> item ) {
        final GoalWrapper wrapper = item.getModelObject();
        final List<Level> candidateLevels = getCandidateLevels();
        DropDownChoice<Level> levelDropDownChoice = new DropDownChoice<Level>(
                "level",
                new PropertyModel<Level>( wrapper, "level" ),
                candidateLevels,
                new IChoiceRenderer<Level>() {
                    public Object getDisplayValue( Level level ) {
                        return level == null ? "Select a level" : wrapper.getLevelLabel( level );
                    }

                    public String getIdValue( Level level, int index ) {
                        return Integer.toString( index );
                    }
                } );
        levelDropDownChoice.add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        if ( !wrapper.isUndergoingCreation() ) {
                            update( target, new Change( Change.Type.Updated, getSegment(), "goals" ) );
                        }
                    }
                } );
        levelDropDownChoice.setEnabled( getPlan().isDevelopment() );
        item.add( levelDropDownChoice );
    }

    private void addCategoryCell( final ListItem<GoalWrapper> item ) {
        final GoalWrapper wrapper = item.getModelObject();
        final List<Goal.Category> candidateTypes = getCandidateCategories( wrapper );
        DropDownChoice<Goal.Category> categoryChoices = new DropDownChoice<Goal.Category>(
                "category",
                new PropertyModel<Goal.Category>( wrapper, "category" ),
                candidateTypes,
                new IChoiceRenderer<Goal.Category>() {
                    public Object getDisplayValue( Goal.Category category ) {
                        return category == null
                                ? "Select a category"
                                : wrapper.categoryLabel( category );
                    }

                    public String getIdValue( Goal.Category type, int index ) {
                        return Integer.toString( index );
                    }
                } );
        categoryChoices.add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        if ( !wrapper.isUndergoingCreation() ) {
                            update( target, new Change( Change.Type.Updated, getSegment(), "goals" ) );
                        }

                    }
                } );
        categoryChoices.setEnabled( getPlan().isDevelopment() );
        categoryChoices.setOutputMarkupId( true );
        item.addOrReplace( categoryChoices );
    }

    private void addOrganizationCell( final ListItem<GoalWrapper> item ) {
        final GoalWrapper wrapper = item.getModelObject();
        final List<String> choices = getQueryService().findAllEntityNames( Organization.class );
        EntityReferencePanel<Organization> orgRefField = new EntityReferencePanel<Organization>(
                "organization",
                new Model<GoalWrapper>( wrapper ),
                choices,
                "organization",
                Organization.class);
        orgRefField.enable( getPlan().isDevelopment() );
        item.add( orgRefField );
    }

    private void addEndsWithSegment( ListItem<GoalWrapper> item ) {
        final GoalWrapper wrapper = item.getModelObject();
        CheckBox endsWithSegmentCheckBox = new CheckBox(
                "endsWithSegment",
                new PropertyModel<Boolean>(wrapper, "endsWithSegment"));
        endsWithSegmentCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        if ( !wrapper.isUndergoingCreation() ) {
                            update( target, new Change( Change.Type.Updated, getSegment(), "goals" ) );
                        }
                    }
                });
        endsWithSegmentCheckBox.setEnabled( getPlan().isDevelopment() );
        item.add( endsWithSegmentCheckBox );
    }

    private void addDeleteImage( ListItem<GoalWrapper> item ) {
        final GoalWrapper wrapper = item.getModelObject();
        ConfirmedAjaxFallbackLink deleteLink = new ConfirmedAjaxFallbackLink(
                "delete",
                "Delete goal?" ) {
            public void onClick( AjaxRequestTarget target ) {
                wrapper.deleteGoal();
                update( target,
                        new Change(
                                Change.Type.Updated,
                                getSegment(),
                                "goals"
                        ) );
            }
        };
        makeVisible( deleteLink, getPlan().isDevelopment() && wrapper.isComplete() );
        item.addOrReplace( deleteLink );
    }

    private void addShowMoreCell( ListItem<GoalWrapper> item ) {
        final GoalWrapper wrapper = item.getModel().getObject();
        AjaxFallbackLink<String> moreLink = new AjaxFallbackLink<String>(
                "more-link",
                new Model<String>( "More..." ) ) {
            public void onClick( AjaxRequestTarget target ) {
                selectedGoal = wrapper;
                moreContainer.addOrReplace( makeTasksTable() );
                makeVisible( moreContainer, selectedGoal != null );
                target.addComponent( moreContainer );
            }
        };
        makeVisible( moreLink, !wrapper.isMarkedForCreation() );
        item.add( moreLink );
    }

    private void addDescriptionField() {
        TextArea<String> descriptionField = new TextArea<String>(
                "description",
                new PropertyModel<String>( this, "description" ) );
        descriptionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
            }
        } );
        descriptionField.setOutputMarkupId( true );
        descriptionField.setEnabled( getPlan().isDevelopment() );
        moreContainer.add( descriptionField );
    }

    private Component makeTasksTable() {
        if ( selectedGoal == null ) {
            return new Label( "tasks", new Model<String>( "No goal selected" ) );
        } else {
            return new TasksTable(
                    "tasks",
                    new Model<Segment>( getSegment() ),
                    MAX_TASK_ROWS,
                    selectedGoal.getGoal()
            );
        }
    }

    private List<Level> getCandidateLevels() {
        return Arrays.asList( Level.values() );
    }

    private List<Goal.Category> getCandidateCategories( final GoalWrapper wrapper ) {
        List<Goal.Category> categories = Arrays.asList(Goal.Category.values() );
        Collections.sort( categories, new Comparator<Goal.Category>() {
            public int compare( Goal.Category r1, Goal.Category r2 ) {
                return collator.compare( wrapper.categoryLabel( r1 ), wrapper.categoryLabel( r2 ) );
            }
        } );
        return categories;
    }

    private List<GoalWrapper> getWrappedGoals() {
        List<GoalWrapper> wrappers = new ArrayList<GoalWrapper>();
        for ( Goal goal : getSegment().getGoals() ) {
            wrappers.add( new GoalWrapper( goal, true ) );
        }
        Collections.sort( wrappers, new Comparator<GoalWrapper>() {
            public int compare( GoalWrapper r1, GoalWrapper r2 ) {
                return collator.compare( r1.getLevel().getLabel(), r2.getLevel().getLabel() );
            }
        } );
        GoalWrapper creationWrapper = new GoalWrapper( new Goal(), false );
        creationWrapper.setMarkedForCreation( true );
        wrappers.add( creationWrapper );
        return wrappers;
    }

    /**
     * Get a label for the selected risk, if any.
     *
     * @return a string
     */
    public String getGoalLabel() {
        return selectedGoal != null
                ? selectedGoal.getGoal().getLabel()
                : "no goal is selected";
    }

    /**
     * Get selected risk's description.
     *
     * @return a string
     */
    public String getDescription() {
        return selectedGoal != null
                ? selectedGoal.getDescription()
                : "";
    }

    /**
     * Set selected risk's description.
     *
     * @param value a string
     */
    public void setDescription( String value ) {
        if ( selectedGoal != null ) {
            selectedGoal.setDescription( value != null ? value : "" );
        }
    }

    private Segment getSegment() {
        return (Segment) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        goalsContainer.addOrReplace( makeGoalsTable() );
        initLabel();
        moreContainer.addOrReplace( makeTasksTable() );
        makeVisible( moreContainer, selectedGoal != null );
        target.addComponent( goalsContainer );
        target.addComponent( moreContainer );
        super.updateWith( target, change, updated );
    }

    public class GoalWrapper implements Identifiable {
        /**
         * Goal.
         */
        private Goal goal;
        /**
         * Whether to be created.
         */
        private boolean markedForCreation;
        /**
         * Whether confirmed.
         */
        private boolean confirmed;

        protected GoalWrapper( Goal goal, boolean confirmed ) {
            this.goal = goal;
            markedForCreation = false;
            this.confirmed = confirmed;
        }

        public Goal getGoal() {
            return goal;
        }

        public void setGoal( Goal goal ) {
            this.goal = goal;
        }

        public boolean isMarkedForCreation() {
            return markedForCreation;
        }

        public void setMarkedForCreation( boolean markedForCreation ) {
            this.markedForCreation = markedForCreation;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        /**
         * Can risk be deleted?
         *
         * @return a boolean
         */
        public boolean isComplete() {
            return goal.getCategory() != null && goal.getLevel() != null && goal.getOrganization() != null;
        }

        public void deleteGoal() {
            selectedGoal = null;
            if ( getSegment().getGoals().contains( goal ) ) {
                doCommand( new RemoveGoal( getSegment(), goal ) );
            }
        }

        public void addIfComplete() {
            assert markedForCreation;
            if ( goal.getCategory() != null && goal.getLevel() != null && goal.getOrganization() != null ) {
                if ( !getSegment().getGoals().contains( goal ) ) {
                    doCommand( new UpdatePlanObject(
                            getSegment(),
                            "goals",
                            goal,
                            UpdateObject.Action.Add
                    ) );
                }
            }
        }

        public String getKind() {
            return goal.isGain()
                    ? GAIN
                    : RISK;
        }

        public void setKind( String val ) {
            boolean value = val.equals( GAIN );
            if ( markedForCreation ) {
                goal.setPositive( value );
            } else {
                if ( value != goal.isPositive() ) {
                    int index = getSegment().getGoals().indexOf( goal );
                    if ( index >= 0 ) {
                        doCommand( new UpdatePlanObject(
                                getSegment(),
                                "goals[" + index + "].positive",
                                value,
                                UpdateObject.Action.Set
                        ) );
                    }
                }
            }
        }

        public Level getLevel() {
            return goal.getLevel();
        }

        public void setLevel( Level value ) {
            if ( markedForCreation ) {
                goal.setLevel( value );
                addIfComplete();
            } else {
                if ( value != goal.getLevel() ) {
                    int index = getSegment().getGoals().indexOf( goal );
                    if ( index >= 0 ) {
                        doCommand( new UpdatePlanObject(
                                getSegment(),
                                "goals[" + index + "].level",
                                value,
                                UpdateObject.Action.Set
                        ) );
                    }
                }
            }
        }

        public Goal.Category getCategory() {
            return goal.getCategory();
        }

        public void setCategory( Goal.Category value ) {
            if ( markedForCreation ) {
                goal.setCategory( value );
                addIfComplete();
            } else {
                if ( value != goal.getCategory() ) {
                    int index = getSegment().getGoals().indexOf( goal );
                    if ( index >= 0 ) {
                        doCommand( new UpdatePlanObject(
                                getSegment(),
                                "goals[" + index + "].category",
                                value,
                                UpdateObject.Action.Set
                        ) );
                    }
                }
            }
        }

        public Organization getOrganization() {
            return  goal.getOrganization();
        }

        public void setOrganization( Organization org ) {
            String oldName = getOrganizationName();
            if ( markedForCreation ) {
                goal.setOrganization( org );
                addIfComplete();
            } else {
                int index = getSegment().getGoals().indexOf( goal );
                if ( index >= 0 ) {
                    doCommand( new UpdatePlanObject(
                            getSegment(),
                            "goals[" + index + "].organization",
                            org,
                            UpdateObject.Action.Set
                    ) );
                }
            }
            getCommander().cleanup( Organization.class, oldName );
        }

        public boolean isEndsWithSegment() {
            return goal.isEndsWithSegment();
        }

        public void setEndsWithSegment( boolean val ) {
            if ( markedForCreation ) {
                goal.setEndsWithSegment( val );
                addIfComplete();
            } else {
                int index = getSegment().getGoals().indexOf( goal );
                if ( index >= 0 ) {
                    doCommand( new UpdatePlanObject(
                            getSegment(),
                            "goals[" + index + "].endsWithSegment",
                            val,
                            UpdateObject.Action.Set
                    ) );
                }
            }
        }


       public String getOrganizationName() {
            Organization org = goal.getOrganization();
            return org != null ? org.getName() : "";
        }

        /** {@inheritDoc} */
        public long getId() {
            return 0;
        }

        /** {@inheritDoc} */
        public String getDescription() {
            return goal.getDescription();
        }

        /** {@inheritDoc} */
        public String getName() {
            return goal.toString();
        }

        /** {@inheritDoc} */
        public String getTypeName() {
            return "Goal wrapper";
        }

        public void setDescription( String value ) {
            String oldValue = goal.getDescription();
            if ( !oldValue.equals( value ) ) {
                if ( markedForCreation ) {
                    goal.setDescription( value );
                } else {
                    int index = getSegment().getGoals().indexOf( goal );
                    if ( index >= 0 ) {
                        doCommand( new UpdatePlanObject(
                                getSegment(),
                                "goals[" + index + "].description",
                                value,
                                UpdateObject.Action.Set
                        ) );
                    }
                }
            }
        }

        private boolean isUndergoingCreation() {
            return isMarkedForCreation() && !isComplete();
        }

        public String getLevelLabel( Level level ) {
            return goal.isPositive() ? level.name() : level.getNegativeLabel();
        }

        public String categoryLabel( Goal.Category category ) {
            return category.getLabel( goal.isPositive() ) ;
        }
    }

    /**
     * Achiever tasks table panel.
     */
    public class TasksTable extends AbstractTablePanel<Goal> {
        /**
         * The goal tasks are achieving.
         */
        private Goal goal;
        /**
         * Segment.
         */
        private Segment segment;

        public TasksTable(
                String id,
                IModel<? extends Identifiable> segmentModel,
                int pageSize,
                Goal goal ) {
            super( id, segmentModel, pageSize, null );
            segment = (Segment) segmentModel.getObject();
            this.goal = goal;
            initTable();
        }

        @SuppressWarnings( "unchecked" )
        private void initTable() {
            List<Achiever> achievers = new ArrayList<Achiever>();
            for ( Part part : getQueryService().findAchievers( segment, goal ) ) {
                achievers.add( new Achiever( part, goal ) );
            }
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            columns.add( makeColumn( "Purpose", "kind", EMPTY ) );
            columns.add( makeLinkColumn( "Task", "part", "part.task", EMPTY ) );
            columns.add( makeLinkColumn( "Segment", "part.segment", "part.segment.name", EMPTY ) );
            columns.add( makeLinkColumn( "Agent", "part.actor", "part.actor.name", EMPTY ) );
            columns.add( makeLinkColumn( "Role", "part.role", "part.role.name", EMPTY ) );
            columns.add( makeLinkColumn( "Organization", "part.organization", "part.organization.name", EMPTY ) );
            add( new AjaxFallbackDefaultDataTable(
                    "achievers",
                    columns,
                    new SortableBeanProvider<Achiever>( achievers, "part.task" ),
                    getPageSize() ) );
        }
    }

    /**
     * Part as goal achiever.
     */
    public class Achiever implements Serializable {
        /**
         * Part.
         */
        private Part part;
        /**
         * Goal.
         */
        private Goal goal;

        public Achiever( Part part, Goal goal ) {
            this.part = part;
            this.goal = goal;
        }

        public Part getPart() {
            return part;
        }

        public Goal getGoal() {
            return goal;
        }

        /**
         * Get kind of achiever.
         *
         * @return a string
         */
        public String getKind() {
            if ( part.isTerminatesEventPhase() ) {
                return "Ends event phase";
            } else if ( part.getGoals().contains( goal ) ) {
                return goal.isPositive() ? "Makes gain" : "Reduces risk";
            } else {
                return "";
            }
        }
    }
}
