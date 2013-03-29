package com.mindalliance.channels.pages.components.segment.checklist;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.ActionStep;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.CommunicationStep;
import com.mindalliance.channels.core.model.checklist.Condition;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.model.checklist.StepGuard;
import com.mindalliance.channels.core.model.checklist.StepOrder;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/26/13
 * Time: 1:23 PM
 */
public class ChecklistStepPanel extends AbstractCommandablePanel {

    private static final int MAX_SIZE = 65;
    private static final boolean POSITIVE = true;

    private Part part;
    private Step step;
    private int index;
    private boolean edited;
    private WebMarkupContainer stepContainer;

    public ChecklistStepPanel( String id, Part part, Step step, boolean edited, int index ) {
        super( id );
        this.part = part;
        this.step = step;
        this.index = index;
        this.edited = edited && isLockedByUser( part );
        init();
    }

    private void init() {
        stepContainer = new WebMarkupContainer( "stepContainer" );
        String cssClasses = index % 2 == 0  ? "data-table step even-step" : "data-table step odd-step" ;
        if ( edited )
            cssClasses = cssClasses + " expanded-step";
        stepContainer.add( new AttributeModifier( "class",  cssClasses ) );
        add( stepContainer );
        addStepLabel();
        addDeleteStep();
        addEditDoneButton();
        addStepRequired();
        addIfGuards();
        addUnlessGuards();
        addPrerequisiteSteps();
    }

    private void addStepRequired() {
        WebMarkupContainer requiredContainer = new WebMarkupContainer( "requiredContainer" );
        requiredContainer.setVisible( edited && step.isActionStep() );
        stepContainer.add( requiredContainer );
        AjaxCheckBox requiredCheckBox = new AjaxCheckBox(
                "required",
                new PropertyModel<Boolean>( this, "required" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.Updated, part );
                change.setProperty( "checklist" );
                update( target, change );
            }
        };
        requiredContainer.add( requiredCheckBox );
    }

    public boolean isRequired() {
        return step.isActionStep() && ( (ActionStep) step ).isRequired();
    }

    public void setRequired( boolean val ) {
        if ( step.isActionStep() ) {
            ActionStep actionStep = (ActionStep) step;
            int index = getChecklist().getActionSteps().indexOf( actionStep );
            if ( index >= 0 ) {
                Command command = new UpdateSegmentObject(
                        getUsername(),
                        part,
                        "checklist.actionSteps[" + index + "].required",
                        val,
                        UpdateObject.Action.Set
                );
                command.makeUndoable( false );
                doCommand( command );
            }
        }
    }

    private void addStepLabel() {
        Label actLabel = new Label( "act", step.getLabel() );
        actLabel.add( new AttributeModifier( "class", stepCSSClasses( step ) ) );
        stepContainer.add( actLabel );
    }

    private String stepCSSClasses( Step step ) {
        StringBuilder sb = new StringBuilder();
        if ( edited )
            sb.append( " edited-step" );
        if ( step.isActionStep() ) {
            sb.append( " action-step" );
        } else {
            if ( step.isCommunicationStep() ) {
                CommunicationStep communicationStep = (CommunicationStep) step;
                sb.append( communicationStep.isNotification() ? " notification-step" : " request-step" );
            }
        }
        return sb.toString();
    }

    private void addDeleteStep() {
        AjaxLink<String> deleteStepLink = new AjaxLink<String>( "deleteStep" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                int index = getChecklist().getActionSteps().indexOf( (ActionStep) step );
                if ( index >= 0 ) {
                    Command command = new UpdateSegmentObject( getUsername(),
                            part,
                            "checklist.actionSteps",
                            step,
                            UpdateObject.Action.Remove );
                    command.makeUndoable( false );
                    doCommand( command ); // delete step
                    getChecklist().cleanUp();
                    Change change = new Change( Change.Type.Updated, part );
                    change.setProperty( "checklist" );
                    change.addQualifier( "deleted-step", step );
                    update( target, change );
                }
            }
        };
        addTipTitle( deleteStepLink, "Delete this step" );
        deleteStepLink.setVisible( step.isActionStep() && edited );
        stepContainer.add( deleteStepLink );
    }

    private void addEditDoneButton() {
        AjaxLink<String> editDoneLink = new AjaxLink<String>( "editDoneButton" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                Change change = edited
                        ? new Change( Change.Type.Collapsed, part )
                        : new Change( Change.Type.Expanded, part );
                change.setProperty( "checklist" );
                change.addQualifier( "stepRef", step.getRef() );
                update( target, change );
            }
        };
        editDoneLink.add( new AttributeModifier( "class", edited ? "less edit-step" : "more edit-step" ) );
        editDoneLink.add( new Label( "editDone", edited ? "Done" : "Edit" ) );
        stepContainer.add( editDoneLink );
    }

    private void addIfGuards() {
        WebMarkupContainer ifsContainer = new WebMarkupContainer( "ifsContainer" );
        ifsContainer.setVisible( edited || getChecklist().hasGuards( step, POSITIVE ) );
        stepContainer.add( ifsContainer );
        List<StepGuard> ifGuards = getIfGuards();
        ListView<StepGuard> ifListView = new ListView<StepGuard>(
                "ifs",
                ifGuards
        ) {
            @Override
            protected void populateItem( ListItem<StepGuard> item ) {
                final StepGuard stepGuard = item.getModelObject();
                Condition condition = getChecklist().deRefCondition( stepGuard.getConditionRef() );
                String conditionString = condition.getLabel();
                Label label = new Label( "if", StringUtils.abbreviate( conditionString, MAX_SIZE ) );
                item.add( label );
                if ( conditionString.length() > MAX_SIZE ) {
                    addTipTitle( label, conditionString );
                }
                // delete
                AjaxLink<String> deleteIfLink = new AjaxLink<String>( "deleteIf" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        int index = getChecklist().getStepGuards().indexOf( stepGuard );
                        if ( index >= 0 ) {
                            Command command = new UpdateSegmentObject( getUsername(),
                                    part,
                                    "checklist.stepGuards",
                                    stepGuard,
                                    UpdateObject.Action.Remove );
                            command.makeUndoable( false );
                            doCommand( command ); // delete if guard
                            getChecklist().cleanUp();
                            Change change = new Change( Change.Type.Updated, part );
                            change.setProperty( "checklist" );
                            update( target, change );
                        }
                    }
                };
                addTipTitle( deleteIfLink, "Delete this condition" );
                deleteIfLink.setVisible( edited );
                item.add( deleteIfLink );
            }
        };
        ifListView.setOutputMarkupId( true );
        makeVisible( ifListView, !ifGuards.isEmpty() );
        ifsContainer.add( ifListView );
        // add new
        addNewIfGuard( ifsContainer );
    }

    private void addNewIfGuard( WebMarkupContainer ifsContainer ) {
        WebMarkupContainer addIfContainer = new WebMarkupContainer( "addIfContainer" );
        addIfContainer.setVisible( edited );
        addIfContainer.add( new StepGuardPanel( "addIf", part, step, POSITIVE ) );
        ifsContainer.add( addIfContainer );
    }

    @SuppressWarnings("unchecked")
    private List<StepGuard> getIfGuards() {
        return (List<StepGuard>) CollectionUtils.select(
                getChecklist().getStepGuards(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (StepGuard) object ).isPositive();
                    }
                }
        );
    }

    private void addUnlessGuards() {
        List<StepGuard> unlessGuards = getUnlessGuards();
        WebMarkupContainer unlessesContainer = new WebMarkupContainer( "unlessesContainer" );
        unlessesContainer.setVisible( edited || getChecklist().hasGuards( step, !POSITIVE ) );
        stepContainer.add( unlessesContainer );
        ListView<StepGuard> unlessListView = new ListView<StepGuard>(
                "unlesses",
                unlessGuards
        ) {
            @Override
            protected void populateItem( ListItem<StepGuard> item ) {
                final StepGuard stepGuard = item.getModelObject();
                Condition condition = getChecklist().deRefCondition( stepGuard.getConditionRef() );
                String conditionString = condition.getLabel();
                Label label = new Label( "unless", StringUtils.abbreviate( conditionString, MAX_SIZE ) );
                item.add( label );
                if ( conditionString.length() > MAX_SIZE ) {
                    addTipTitle( label, conditionString );
                }
                // delete
                AjaxLink<String> deleteIfLink = new AjaxLink<String>( "deleteUnless" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        int index = getChecklist().getStepGuards().indexOf( stepGuard );
                        if ( index >= 0 ) {
                            Command command = new UpdateSegmentObject( getUsername(),
                                    part,
                                    "checklist.stepGuards",
                                    stepGuard,
                                    UpdateObject.Action.Remove );
                            command.makeUndoable( false );
                            doCommand( command ); // delete unless guard
                            getChecklist().cleanUp();
                            Change change = new Change( Change.Type.Updated, part );
                            change.setProperty( "checklist" );
                            update( target, change );
                        }
                    }
                };
                addTipTitle( deleteIfLink, "Delete this condition" );
                deleteIfLink.setVisible( edited );
                item.add( deleteIfLink );
            }
        };
        unlessListView.setOutputMarkupId( true );
        makeVisible( unlessListView, !unlessGuards.isEmpty() );
        unlessesContainer.add( unlessListView );
        // add new
        addNewUnlessGuard( unlessesContainer );
    }

    private void addNewUnlessGuard( WebMarkupContainer unlessesContainer ) {
        WebMarkupContainer addUnlessContainer = new WebMarkupContainer( "addUnlessContainer" );
        addUnlessContainer.setVisible( edited );
        addUnlessContainer.add( new StepGuardPanel( "addUnless", part, step, !POSITIVE ) );
        unlessesContainer.add( addUnlessContainer );
    }

    @SuppressWarnings("unchecked")
    private List<StepGuard> getUnlessGuards() {
        return (List<StepGuard>) CollectionUtils.select(
                getChecklist().getStepGuards(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (StepGuard) object ).isPositive();
                    }
                }
        );
    }

    private void addPrerequisiteSteps() {
        List<StepOrder> stepOrders = getStepOrders();
        WebMarkupContainer aftersContainer = new WebMarkupContainer( "aftersContainer" );
        aftersContainer.setVisible( edited
                || getChecklist().hasPrerequisites( step ) );
        stepContainer.add( aftersContainer );
        ListView<StepOrder> prerequisiteListView = new ListView<StepOrder>(
                "afters",
                stepOrders
        ) {
            @Override
            protected void populateItem( ListItem<StepOrder> item ) {
                final StepOrder stepOrder = item.getModelObject();
                Step prerequisite = getChecklist().derefStep( stepOrder.getPrerequisiteStepRef() );
                String label = prerequisite.getPrerequisiteLabel();
                Label stepLabel = new Label( "after", StringUtils.abbreviate( label, MAX_SIZE ) );
                if ( label.length() > MAX_SIZE ) {
                    addTipTitle( stepLabel, label );
                }
                item.add( stepLabel );
                // delete
                AjaxLink<String> deleteIfLink = new AjaxLink<String>( "deleteAfter" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        int index = getChecklist().getStepOrders().indexOf( stepOrder );
                        if ( index >= 0 ) {
                            Command command = new UpdateSegmentObject( getUsername(),
                                    part,
                                    "checklist.stepOrders",
                                    stepOrder,
                                    UpdateObject.Action.Remove );
                            command.makeUndoable( false );
                            doCommand( command );
                            Change change = new Change( Change.Type.Updated, part );
                            change.setProperty( "checklist" );
                            update( target, change );
                        }
                    }
                };
                addTipTitle( deleteIfLink, "Delete this prerequisite" );
                deleteIfLink.setVisible( edited );
                item.add( deleteIfLink );
            }
        };
        aftersContainer.add( prerequisiteListView );
        // add new
        addNewPrerequisiteStep( aftersContainer );
    }

    private void addNewPrerequisiteStep( WebMarkupContainer aftersContainer ) {
        WebMarkupContainer addAfterContainer = new WebMarkupContainer( "addAfterContainer" );
        addAfterContainer.setVisible( edited );
        aftersContainer.add( addAfterContainer );
        DropDownChoice<Step> prereqStepChoice = new DropDownChoice<Step>(
                "addAfter",
                new PropertyModel<Step>( this, "newPrerequisiteStep" ),
                getPrerequisiteChoices(),
                new IChoiceRenderer<Step>() {

                    @Override
                    public Object getDisplayValue( Step object ) {
                        return StringUtils.abbreviate( ( (Step) object ).getPrerequisiteLabel(), MAX_SIZE );
                    }

                    @Override
                    public String getIdValue( Step object, int index ) {
                        return Integer.toString( index );
                    }
                }
        );
        prereqStepChoice.add(  new AjaxFormComponentUpdatingBehavior( "onchange") {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.Updated, part );
                change.setProperty( "checklist" );
                update( target, change );
            }
        } );
        addAfterContainer.add( prereqStepChoice );
    }

    @SuppressWarnings("unchecked")
    private List<Step> getPrerequisiteChoices() {
        List<Step> choices = getChecklist().listEffectiveSteps();
        choices.remove( step );
        for ( StepOrder stepOrder : getChecklist().listStepOrdersFor( step ) ) {
            Step prerequisiteStep = getChecklist().derefStep( stepOrder.getStepRef() );
            if ( prerequisiteStep  != null ) choices.remove( prerequisiteStep );
        }
        Collections.sort( choices, new Comparator<Step>() {
            @Override
            public int compare( Step s1, Step s2 ) {
                return s1.getLabel().compareTo( s2.getLabel() );
            }
        } );
        return choices;
    }

    public Step getNewPrerequisiteStep() {
        return null;
    }

    public void setNewPrerequisiteStep( Step prereq ) {
        StepOrder stepOrder = new StepOrder( prereq, step );
        Command command = new UpdateSegmentObject( getUsername(),
                part,
                "checklist.stepOrders",
                stepOrder,
                UpdateObject.Action.Add );
        command.makeUndoable( false );
        doCommand( command ); // add step order

    }


    private List<StepOrder> getStepOrders() {
        return getChecklist().listStepOrdersFor( step );
    }

    private Checklist getChecklist() {
        return part.getChecklist();
    }


}
