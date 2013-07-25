package com.mindalliance.channels.pages.components.segment.checklist;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.Condition;
import com.mindalliance.channels.core.model.checklist.LocalCondition;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.model.checklist.StepGuard;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/26/13
 * Time: 9:26 PM
 */
public class StepGuardPanel extends AbstractCommandablePanel {

    private static final int MAX_SIZE = 50;
    private Part part;
    private Step step;
    private boolean positive;
    private boolean otherCondition = false;
    private TextField<String> newConditionField;

    public StepGuardPanel( String id, Part part, Step step, boolean positive ) {
        super( id );
        this.part = part;
        this.step = step;
        this.positive = positive;
        init();
    }

    private void init() {
        addConditionChoices();
        addNewConditionField();
    }

    private void addConditionChoices() {
        DropDownChoice<Condition> conditionChoices = new DropDownChoice<Condition>(
                "conditions",
                new PropertyModel<Condition>( this, "addedCondition" ),
                getConditionChoices(),
                new IChoiceRenderer<Condition>() {
                    @Override
                    public Object getDisplayValue( Condition object ) {
                        Condition condition = (Condition) object;
                        if ( isNewCondition( condition ) )
                            return "Other...";
                        else
                            return StringUtils.abbreviate( ( (Condition) object ).getLabel(), MAX_SIZE );
                    }

                    @Override
                    public String getIdValue( Condition object, int index ) {
                        return Integer.toString( index );
                    }
                }

        );
        conditionChoices.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                makeVisible( newConditionField, otherCondition );
                target.add( newConditionField );
                if ( !otherCondition ) {
                    Change change = new Change( Change.Type.Updated, part );
                    change.setProperty( "checklist" );
                    update( target, change );
                }
            }
        } );
        add( conditionChoices );
    }

    private List<Condition> getConditionChoices() {
        List<Condition> conditions = getChecklist().listEffectiveConditions();
        conditions.removeAll( getChecklist().listConditionsFor( step ) );
        Collections.sort( conditions, new Comparator<Condition>() {
            @Override
            public int compare( Condition c1, Condition c2 ) {
                return c1.getLabel().compareTo( c2.getLabel() );
            }
        } );
        conditions.add( new LocalCondition() );
        return conditions;
    }

    public Condition getAddedCondition() {
        return null;
    }

    public void setAddedCondition( Condition condition ) {
        if ( isNewCondition( condition ) ) {
            otherCondition = true;
        } else {
            otherCondition = false;
            StepGuard stepGuard = new StepGuard( condition, step, positive );
            Command command = new UpdateSegmentObject(
                    getUsername(),
                    part,
                    "checklist.stepGuards",
                    stepGuard,
                    UpdateObject.Action.Add
            );
            command.makeUndoable( false );
            doCommand( command );
        }
    }

    private boolean isNewCondition( Condition condition ) {
        return condition.isLocalCondition() && ( (LocalCondition) condition ).isEmpty();
    }

    private void addNewConditionField() {
        newConditionField = new TextField<String>(
                "newCondition",
                new PropertyModel<String>( this, "newCondition" )
        );
        newConditionField.setOutputMarkupId( true );
        newConditionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                makeVisible( newConditionField, false );
                target.add( newConditionField );
                Change change = new Change( Change.Type.Updated, part );
                change.setProperty( "checklist" );
                update( target, change );
            }
        } );
        makeVisible( newConditionField, otherCondition );
        addOrReplace( newConditionField );
    }

    public String getNewCondition() {
        return null;
    }

    public void setNewCondition( String val ) {
        if ( val != null ) {
            String state = ChannelsUtils.cleanUpPhrase( val );
            if ( !val.isEmpty() ) {
                LocalCondition localCondition = new LocalCondition( state );
                Command addLocalCondition = new UpdateSegmentObject(
                        getUsername(),
                        part,
                        "checklist.localConditions",
                        localCondition,
                        UpdateObject.Action.Add
                );
                addLocalCondition.makeUndoable( false );
                doCommand( addLocalCondition );

                StepGuard stepGuard = new StepGuard( localCondition, step, positive );
                Command addStepGuard = new UpdateSegmentObject(
                        getUsername(),
                        part,
                        "checklist.stepGuards",
                        stepGuard,
                        UpdateObject.Action.Add
                );
                addStepGuard.makeUndoable( false );
                doCommand( addStepGuard );
            }
            otherCondition = false;
        }
    }

    private Checklist getChecklist() {
        return part.getEffectiveChecklist();
    }
}
