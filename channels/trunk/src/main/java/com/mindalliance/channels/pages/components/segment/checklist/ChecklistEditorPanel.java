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

    public ChecklistEditorPanel( String id, IModel<? extends Identifiable> iModel ) {
        super( id, iModel );
        init();
    }

    private void init() {
        addStepPanels();
        addNewStepField();
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

    private void addNewStepField() {
        actionStepText = new TextField<String>(
                "newStep",
                new PropertyModel<String>( this, "newActionStep" ) );
        actionStepText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addStepPanels();
                target.add( stepsContainer );
                target.add( actionStepText );
            }
        } );
        addTipTitle( actionStepText, "Enter a new action step and press return" );
        actionStepText.setVisible( isLockedByUser( getPart() ) );
        add( actionStepText );
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
        String action = ChannelsUtils.cleanUpPhrase( val );
        if ( !action.isEmpty() ) {
            ActionStep actionStep = new ActionStep( action );
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

    private void addConfirmation() {
        AjaxCheckBox confirmedCheckBox = new AjaxCheckBox(
                "confirmed",
                new PropertyModel<Boolean>( this, "confirmed" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.Updated, getPart() );
                update( target, change );
            }
        };
        add( confirmedCheckBox );
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
            if ( change.isExpanded() )
                editedStepRef = (String) change.getQualifier( "stepRef" );
            else if ( change.isCollapsed() || change.hasQualifier( "deleted-step" ) )
                editedStepRef = null;
        } else {
            super.changed( change );
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForInstanceOf( Part.class ) && change.isForProperty( "checklist" ) ) {
            addStepPanels();
            target.add( stepsContainer );
        } else {
            super.updateWith( target, change, updated );
        }
    }
}
