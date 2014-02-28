package com.mindalliance.channels.pages.components.community.requirements;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.Taggable;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.TagsPanel;
import com.mindalliance.channels.pages.components.plan.floating.ModelSearchingFloatingPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Required assignment spec "when" panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/4/11
 * Time: 1:09 PM
 */
public class RequiredWhenPanel extends AbstractCommandablePanel {

    private final boolean isBeneficiary;
    private TextField<String> taskNameField;
    private TextField<String> eventField;
    private ModelObjectLink eventLink;
    private DropDownChoice<String> timingChoices;

    public RequiredWhenPanel( String id, Model<Requirement> requirementModel, boolean isBeneficiary ) {
        super( id, requirementModel );
        this.isBeneficiary = isBeneficiary;
        init();
    }

    private void init() {
        addTaskField();
        addTaskTagsPanel();
        addTimingChoices();
        addEventField();
        addEventLink();
        adjustFields();
    }

    private void adjustFields() {
        boolean lockedByUser = isLockedByUser( getRequirement() );
        taskNameField.setEnabled( lockedByUser );
        eventField.setEnabled( lockedByUser );
        timingChoices.setEnabled( isLockedByUser( getRequirement() ) && getAssignmentSpec().getEvent() != null );
    }

    private void addTaskField() {
        final PropertyModel<List<String>> choices = new PropertyModel<List<String>>( this, "allTasks" );
        taskNameField =
                new AutoCompleteTextField<String>(
                        "taskName",
                        new PropertyModel<String>( this, "taskName" ),
                        getAutoCompleteSettings() ) {
                    @Override
                    protected Iterator<String> getChoices( String s ) {
                        List<String> candidates = new ArrayList<String>();
                        for ( String choice : choices.getObject() ) {
                            if ( getQueryService().likelyRelated( s, choice ) )
                                candidates.add( choice );
                        }
                        return candidates.iterator();
                    }
                };
        taskNameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getRequirement(), propertyPath( "taskName" ) ) );
            }
        } );
        add( taskNameField );
    }

    private void addTaskTagsPanel() {
        TagsPanel tagsPanel = new TagsPanel(
                "taskTags",
                new Model<Taggable>( getRequirement() ),
                propertyPath( "taskTags" ) );
        add( tagsPanel );
        AjaxLink tagsLink = new AjaxLink( "tagsLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.AspectViewed, Channels.MODEL_SEARCHING, ModelSearchingFloatingPanel.TAGS) );
            }
        };
        tagsLink.add( new AttributeModifier( "class", new Model<String>( "model-object-link" ) ) );
        add( tagsLink );
    }

    private void addTimingChoices() {
        timingChoices = new DropDownChoice<String>(
                "phaseChoices",
                new PropertyModel<String>( this, "timingName" ),
                getTimingChoices()
        );
        timingChoices.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getRequirement(), propertyPath( "timing" ) ) );
            }
        } );
        timingChoices.setEnabled( isLockedByUser( getRequirement() ) && getAssignmentSpec().getEvent() != null );
        timingChoices.setOutputMarkupId( true );
        addOrReplace( timingChoices );
    }

    private List<String> getTimingChoices() {
        List<String> timingChoices = new ArrayList<String>();
        timingChoices.add( Phase.Timing.ANY_TIME );
        if ( getAssignmentSpec().getEvent() != null ) {
            for ( Phase.Timing timing : Phase.Timing.values() ) {
                timingChoices.add( Phase.Timing.translateTiming( timing ) );
            }
        }
        return timingChoices;
    }


    private void addEventField() {
        final PropertyModel<List<String>> choices = new PropertyModel<List<String>>( this, "allEventNames" );
        eventField = new AutoCompleteTextField<String>(
                "event",
                new PropertyModel<String>( this, "eventName" ),
                getAutoCompleteSettings() ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices.getObject() ) {
                    if ( getQueryService().likelyRelated( s, choice ) )
                        candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        eventField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addEventLink();
                target.add( eventLink );
                addTimingChoices();
                timingChoices.setEnabled(
                        isLockedByUser( getRequirement() )
                                && getAssignmentSpec().getEvent() != null );
                target.add( timingChoices );
                update( target, new Change( Change.Type.Updated, getRequirement(), propertyPath( "event" ) ) );
            }
        } );
        add( eventField );
    }

    private void addEventLink() {
        eventLink = new ModelObjectLink( "event-link",
                new PropertyModel<ModelEntity>(
                        getRequirement(),
                        propertyPath( "event" ) ),
                new Model<String>( "event" ) );
        eventLink.setOutputMarkupId( true );
        addOrReplace( eventLink );
    }

    public List<String> getAllTasks() {
        return getQueryService().findAllTasks();
    }

    public String getTaskName() {
        return getAssignmentSpec().getTaskName();
    }

    public void setTaskName( String val ) {
        if ( val != null && !val.trim().isEmpty() ) {
            String name = val.trim();
            doCommand( new UpdateModelObject( getUsername(), getRequirement(), propertyPath( "taskName" ), name ) );
        }
    }

    /**
     * Get phase event timing name.
     *
     * @return a string
     */
    public String getTimingName() {
        if ( getAssignmentSpec().getEvent() == null )
            return Phase.Timing.ANY_TIME;
        else
            return Phase.Timing.translateTiming( getAssignmentSpec().getTiming() );
    }

    /**
     * Set phase event timing.
     *
     * @param name an event timing name
     */
    public void setTimingName( String name ) {
        Phase.Timing timing = Phase.Timing.translateTiming( name );
        doCommand( new UpdateModelObject( getUser().getUsername(), getRequirement(), propertyPath( "timing" ), timing ) );
    }

    public List<String> getAllEventNames() {
        return getQueryService().findAllEntityNames( Event.class );
    }

    public Event getEvent() {
        return getAssignmentSpec().getEvent();
    }

    public String getEventName() {
        Event event = getEvent();
        return event == null ? "" : event.getName();
    }

    public void setEventName( String name ) {
        Event oldEvent = getEvent();
        String oldName = oldEvent == null ? "" : oldEvent.getName();
        Event newEvent = null;
        if ( name == null || name.trim().isEmpty() )
            newEvent = null;
        else {
            if ( oldEvent == null || !isSame( name, oldName ) )
                newEvent = getQueryService().safeFindOrCreateType( Event.class, name );
        }
        doCommand( new UpdateModelObject( getUsername(), getRequirement(), propertyPath( "event" ), newEvent ) );
        getCommander().cleanup( Event.class, oldName );
    }


    private Requirement.AssignmentSpec getAssignmentSpec() {
        return isBeneficiary
                ? getRequirement().getBeneficiarySpec()
                : getRequirement().getCommitterSpec();
    }

    private String propertyPath( String property ) {
        return ( isBeneficiary ? "beneficiarySpec" : "committerSpec" ) + "." + property;
    }

    private Requirement getRequirement() {
        return (Requirement) getModel().getObject();
    }


}
