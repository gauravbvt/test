/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.Taggable;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.pages.components.TagsPanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import com.mindalliance.channels.pages.components.plan.floating.PlanSearchingFloatingPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Editor on the details of a segment (name, description, etc).
 */
public class SegmentEditDetailsPanel extends AbstractCommandablePanel implements Guidable {

    /**
     * Plan manager.
     */
    @SpringBean
    private PlanManager planManager;

    /**
     * An issues panel for segment issues.
     */
    private IssuesPanel issuesPanel;

    /**
     * Link to event.
     */
    private ModelObjectLink phaseLink;

    /**
     * Choice of phases.
     */
    private DropDownChoice<Phase> phaseChoices;

    /**
     * Link to phase.
     */
    private ModelObjectLink eventLink;

    /**
     * Event level choice.
     */
    private DropDownChoice<String> eventLevelChoice;

    /**
     * Event timings panel.
     */
    private EventTimingsPanel eventTimingsPanel;

    /**
     * Name for unspecified level.
     */
    private static final String ANY = "Any";

    public SegmentEditDetailsPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "scoping";
    }

    @Override
    public String getHelpTopicId() {
        return "add-define-segment";
    }

    private void init() {
        setOutputMarkupId( true );
        addIdentityFields();
        addTagsPanel();
        addPhaseLink();
        addPhaseChoice();
        addEventLink();
        addEventLevelChoice();
        addEventField();
        addEventTimingsPanel();
        add( new AttachmentPanel( "attachments", new PropertyModel<Segment>( this, "segment" ) ) );
        addIssuesPanel();
    }

    private void addTagsPanel() {
        AjaxFallbackLink tagsLink = new AjaxFallbackLink( "tagsLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Expanded, Channels.PLAN_SEARCHING, PlanSearchingFloatingPanel.TAGS) );
            }
        };
        tagsLink.add( new AttributeModifier( "class", new Model<String>( "model-object-link" ) ) );
        add( tagsLink );
        TagsPanel tagsPanel = new TagsPanel( "tags", new Model<Taggable>( getSegment() ) );
        add( tagsPanel );
    }

    private void addEventLevelChoice() {
        eventLevelChoice = new DropDownChoice<String>( "event-level",
                                                       new PropertyModel<String>( this, "eventLevel" ),
                                                       getEventLevelChoices() );
        eventLevelChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getSegment(), "eventLevel" ) );
            }
        } );
        eventLevelChoice.setOutputMarkupId( true );
        eventLevelChoice.setEnabled( isLockedByUserIfNeeded( getSegment() ) && getSegment().getEvent() != null );
        addOrReplace( eventLevelChoice );
    }

    public String getEventLevel() {
        Level level = getSegment().getEventLevel();
        return level == null ? ANY : level.name();
    }

    public void setEventLevel( String val ) {
        Level level = val.equals( ANY ) ? null : Level.valueOf( val );
        doCommand( new UpdatePlanObject( getUser().getUsername(), getSegment(), "eventLevel", level ) );
    }

    private List<String> getEventLevelChoices() {
        List<String> levels = new ArrayList<String>();
        levels.add( ANY );
        for ( Level level : Level.values() ) {
            levels.add( level.name() );
        }
        return levels;
    }

    private void addIdentityFields() {
        TextField<String> nameField = new TextField<String>( "name", new PropertyModel<String>( this, "name" ) );
        add( new FormComponentLabel( "name-label", nameField ) );
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getSegment(), "name" ) );
            }
        } );
        nameField.setEnabled( isLockedByUserIfNeeded( getSegment() ) );
        add( nameField );

        TextArea<String> descField =
                new TextArea<String>( "description", new PropertyModel<String>( this, "description" ) );
        add( new FormComponentLabel( "description-label", descField ) );
        descField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getSegment(), "description" ) );
            }
        } );
        descField.setEnabled( isLockedByUserIfNeeded( getSegment() ) );
        add( descField );
    }

    private void addPhaseLink() {
        phaseLink = new ModelObjectLink( "phase-link",
                                         new PropertyModel<Event>( getSegment(), "phase" ),
                                         new Model<String>( "Phase" ) );
        phaseLink.setOutputMarkupId( true );
        addOrReplace( phaseLink );
    }

    private void addPhaseChoice() {
        phaseChoices = new DropDownChoice<Phase>( "phase-choices",
                                                  new PropertyModel<Phase>( this, "phase" ),
                                                  new PropertyModel<List<Phase>>( getPlan(), "phases" ) );
        phaseChoices.setOutputMarkupId( true );
        phaseChoices.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addPhaseLink();
                target.add( phaseLink );
                update( target, new Change( Change.Type.Updated, getSegment(), "phase" ) );
            }
        } );
        phaseChoices.setEnabled( isLockedByUserIfNeeded( getSegment() ) );
        addOrReplace( phaseChoices );
    }

    private void addEventField() {
        final List<String> choices = getQueryService().findAllEntityNames( Event.class );
        TextField<String> eventField =
                new AutoCompleteTextField<String>(
                        "event",
                        new PropertyModel<String>( this, "eventName" ),
                        getAutoCompleteSettings() ) {
                    @Override
                    protected Iterator<String> getChoices( String s ) {
                        List<String> candidates = new ArrayList<String>();
                        for ( String choice : choices ) {
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
                addEventLevelChoice();
                target.add( eventLevelChoice );
                update( target, new Change( Change.Type.Updated, getSegment(), "event" ) );
            }
        } );
        eventField.setEnabled( isLockedByUserIfNeeded( getSegment() ) );
        add( eventField );
    }

    private void addEventLink() {
        eventLink = new ModelObjectLink( "event-link",
                                         new PropertyModel<Event>( getSegment(), "event" ),
                                         new Model<String>( "event" ) );
        eventLink.setOutputMarkupId( true );
        addOrReplace( eventLink );
    }

    private void addEventTimingsPanel() {
        eventTimingsPanel = new EventTimingsPanel( "context", new PropertyModel<Segment>( this, "segment" ) );
        add( eventTimingsPanel );
    }

    private void addIssuesPanel() {
        issuesPanel = new IssuesPanel( "issues", new PropertyModel<ModelObject>( this, "segment" ), getExpansions() );
        issuesPanel.setOutputMarkupId( true );
        add( issuesPanel );
        makeVisible( issuesPanel, getAnalyst().hasIssues( getQueryService(), getSegment(), false ) );
    }

    /**
     * Get edited segment.
     *
     * @return a segment
     */
    public Segment getSegment() {
        return (Segment) getModel().getObject();
    }

    /**
     * Get segment name.
     *
     * @return a string
     */
    public String getName() {
        return getSegment().getName();
    }

    /**
     * Set segment name via command.
     *
     * @param name a string
     */
    public void setName( String name ) {
        if ( name != null && !name.isEmpty() )
            doCommand( new UpdatePlanObject( getUser().getUsername(), getSegment(), "name", name ) );
    }

    /**
     * Get segment description.
     *
     * @return a string
     */
    public String getDescription() {
        return getSegment().getDescription();
    }

    /**
     * Set segment name via command.
     *
     * @param desc a string
     */
    public void setDescription( String desc ) {
        doCommand( new UpdatePlanObject( getUser().getUsername(), getSegment(), "description", desc ) );
    }

    /**
     * Name of event initiated by part, if any.
     *
     * @return a plan event name
     */
    public String getEventName() {
        Event event = getEvent();
        return event != null ? event.getName() : "";
    }

    /**
     * Sets whether terminates the segment's event.
     *
     * @param name a plan event name
     */
    public void setEventName( String name ) {
        Event oldEvent = getSegment().getEvent();
        String oldName = oldEvent == null ? "" : oldEvent.getName();
        Event newEvent = null;
        if ( name == null || name.trim().isEmpty() )
            newEvent = ChannelsUser.plan().getDefaultEvent();
        else {
            if ( oldEvent == null || !isSame( name, oldName ) )
                newEvent = getQueryService().safeFindOrCreateType( Event.class, name );
        }
        doCommand( new UpdatePlanObject( getUser().getUsername(), getSegment(), "event", newEvent ) );
        getCommander().cleanup( Event.class, oldName );
    }

    /**
     * Name of phase for this segment.
     *
     * @return a phase
     */
    public Phase getPhase() {
        return getSegment().getPhase();
    }

    /**
     * Sets the segment's planning phase.
     *
     * @param phase a phase
     */
    public void setPhase( Phase phase ) {
        doCommand( new UpdatePlanObject( getUser().getUsername(), getSegment(), "phase", phase ) );
    }

    private Event getEvent() {
        return getSegment().getEvent();
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        makeVisible( target, issuesPanel, getAnalyst().hasIssues( getQueryService(), getSegment(), false ) );
        target.add( issuesPanel );
        super.updateWith( target, change, updated );
    }

    public void refresh( AjaxRequestTarget target ) {
        makeVisible( target, issuesPanel, getAnalyst().hasIssues( getQueryService(), getSegment(), false ) );
        target.add( issuesPanel );
        addPhaseChoice();
        target.add( phaseChoices );
    }
}
