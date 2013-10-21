package com.mindalliance.channels.pages.components.segment.checklist;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.ActionStep;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

/**
 * Checklist editor.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/26/13
 * Time: 12:34 PM
 */
public class ChecklistEditorPanel extends AbstractCommandablePanel {

    private String editedStepRef;
    private TextField<String> actionStepText;
    private WebMarkupContainer stepsContainer;
    private WebMarkupContainer confirmationContainer;

    public ChecklistEditorPanel( String id, IModel<? extends Identifiable> iModel ) {
        super( id, iModel );
        init();
    }

    private void init() {
        addStepPanels();
        addNewStep();
        addConfirmation();
    }

    private void addStepPanels() {
        stepsContainer = new WebMarkupContainer( "stepsContainer" );
        stepsContainer.setOutputMarkupId( true );
        addOrReplace( stepsContainer );
        ListView<Step> stepListView = new ListView<Step>(
                "steps",
                getSteps()
        ) {
            @Override
            protected void populateItem( ListItem<Step> item ) {
                Step step = item.getModelObject();
                item.add( new ChecklistStepPanel( "step", getPart(), step, isEdited( step ), item.getIndex() ) );
            }
        };
        stepsContainer.add( stepListView );
    }

    private boolean isEdited( Step step ) {
        return editedStepRef != null && step.getRef().equals( editedStepRef );
    }

    private void addNewStep() {
        WebMarkupContainer newStepContainer = new WebMarkupContainer( "newStepContainer" );
        newStepContainer.setVisible( isLockedByUser( getPart() ) );
        add( newStepContainer );
        actionStepText = new TextField<String>(
                "newStep",
                new PropertyModel<String>( this, "newActionStep" ) );
        actionStepText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addStepPanels();
                target.add( stepsContainer );
                target.add( actionStepText );
                update( target, new Change( Change.Type.Updated, getPart(), "checklist" ) );
            }
        } );
        addTipTitle( actionStepText, "Enter a new action step and press return" );
        newStepContainer.add( actionStepText );
    }

    private List<Step> getSteps() {
        List<Step> steps = getChecklist().listEffectiveSteps();
        getChecklist().sort( steps );
        return steps;
    }

    private Checklist getChecklist() {
        return getPart().getEffectiveChecklist();
    }

    private Part getPart() {
        return (Part) getModel().getObject();
    }

    public String getNewActionStep() {
        return "";
    }

    public void setNewActionStep( String val ) {
        if ( val != null ) {
            String action = ChannelsUtils.cleanUpPhrase( val );
            if ( !action.isEmpty() ) {
                ActionStep actionStep = new ActionStep( );
                actionStep.setAction( action );
                Command command = new UpdateSegmentObject( getUsername(),
                        getPart(),
                        "checklist.actionSteps",
                        actionStep,
                        UpdateObject.Action.Add );
                command.makeUndoable( false );
                doCommand( command );
                editedStepRef = actionStep.getRef();
            }
        }
    }

    private void addConfirmation() {
        confirmationContainer = new WebMarkupContainer( "confirmationContainer" );
        confirmationContainer.setOutputMarkupId( true );
        addOrReplace( confirmationContainer );
        makeVisible( confirmationContainer, !getChecklist().listEffectiveSteps().isEmpty() );
        AjaxCheckBox confirmedCheckBox = new AjaxCheckBox(
                "confirmed",
                new PropertyModel<Boolean>( this, "confirmed" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.Updated, getPart() );
                change.setProperty( "checklist" );
                update( target, change );
            }
        };
        confirmationContainer.add( confirmedCheckBox );
    }

    public boolean isConfirmed() {
        return getChecklist().isConfirmed();
    }

    public void setConfirmed( boolean val ) {
        Command command = new UpdateSegmentObject(
                getUsername(),
                getPart(),
                "checklist.confirmed",
                val,
                UpdateObject.Action.Set
        );
        command.makeUndoable( false );
        doCommand( command );
    }

    @Override
    public void changed( Change change ) {
        if ( change.isForInstanceOf( Part.class ) && change.isForProperty( "checklist" ) ) {
            if ( change.isExpanded() )  // step is expanded
                editedStepRef = (String) change.getQualifier( "stepRef" );
            else if ( change.isCollapsed() || change.hasQualifier( "deleted-step" ) ) // step collapsed or deleted
                editedStepRef = null;
            else
                super.changed( change );
        } else {
            super.changed( change );
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForInstanceOf( Part.class ) && change.isForProperty( "checklist" ) ) {
            addStepPanels();
            target.add( stepsContainer );
            addConfirmation();
            target.add( confirmationContainer );
            if ( !change.isExpanded() && !change.isCollapsed() ) {
                super.updateWith( target, change, updated );
            }
        } else {
            super.updateWith( target, change, updated );
        }
    }
}
