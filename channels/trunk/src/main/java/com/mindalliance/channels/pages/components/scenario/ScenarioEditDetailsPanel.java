package com.mindalliance.channels.pages.components.scenario;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.util.Matcher;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
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
 * Editor on the details of a scenario (name, description, etc).
 */
public class ScenarioEditDetailsPanel extends AbstractCommandablePanel {

    /**
     * Plan manager.
     */
    @SpringBean
    private PlanManager planManager;

    /**
     * An issues panel for scenario issues.
     */
    private IssuesPanel issuesPanel;
    /**
     * Link to event.
     */
    private ModelObjectLink eventLink;

    public ScenarioEditDetailsPanel( String id,
                                     IModel<? extends Identifiable> model,
                                     Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        setOutputMarkupId( true );
        AjaxFallbackLink<?> closeLink = new AjaxFallbackLink( "close" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.Collapsed, getScenario() );
                update( target, change );
            }
        };
        add( closeLink );
        addIdentityFields();
        addEventLink();
        addEventField();
        addIssuesPanel();
        add( new AttachmentPanel( "attachments", new PropertyModel<Scenario>( this, "scenario" ) ) );
    }

    private void addIdentityFields() {
        TextField<String> nameField = new TextField<String>(
                "name",
                new PropertyModel<String>( this, "name" ) );
        add( new FormComponentLabel( "name-label", nameField ) );
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getScenario(), "name" ) );
            }
        } );
        nameField.setEnabled( getPlan().isDevelopment() );
        add( nameField );

        TextArea<String> descField = new TextArea<String>(
                "description",
                new PropertyModel<String>( this, "description" ) );
        add( new FormComponentLabel( "description-label", descField ) );
        descField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getScenario(), "description" ) );
            }
        } );
        descField.setEnabled( getPlan().isDevelopment() );
        add( descField );
    }

    private void addEventField() {
        final List<String> choices = getQueryService().findAllNames( Event.class );
        TextField<String> eventField = new AutoCompleteTextField<String>(
                "event",
                new PropertyModel<String>( this, "eventName" ) ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( Matcher.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();

            }
        };
        eventField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addEventLink();
                target.addComponent( eventLink );
                update( target, new Change( Change.Type.Updated, getScenario(), "event" ) );
            }
        } );
        eventField.setEnabled( getPlan().isDevelopment() );
        add( eventField );
    }

    private void addEventLink() {
        eventLink = new ModelObjectLink( "event-link",
                new PropertyModel<Event>( getScenario(), "event" ),
                new Model<String>( "event" ) );
        eventLink.setOutputMarkupId( true );
        addOrReplace( eventLink );
    }

    private void addIssuesPanel() {
        issuesPanel = new IssuesPanel(
                "issues",
                new PropertyModel<ModelObject>( this, "scenario" ),
                getExpansions() );
        issuesPanel.setOutputMarkupId( true );
        add( issuesPanel );
        makeVisible( issuesPanel, getAnalyst().hasIssues( getScenario(), false ) );
    }


    /**
     * Get edited scenario.
     *
     * @return a scenario
     */
    public Scenario getScenario() {
        return (Scenario) getModel().getObject();
    }

    /**
     * Get scenario name.
     *
     * @return a string
     */
    public String getName() {
        return getScenario().getName();
    }

    /**
     * Set scenario name via command.
     *
     * @param name a string
     */
    public void setName( String name ) {
        doCommand( new UpdatePlanObject( getScenario(), "name", name ) );
    }

    /**
     * Get scenario description.
     *
     * @return a string
     */
    public String getDescription() {
        return getScenario().getDescription();
    }

    /**
     * Set scenario name via command.
     *
     * @param desc a string
     */
    public void setDescription( String desc ) {
        doCommand( new UpdatePlanObject( getScenario(), "description", desc ) );
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
     * Sets whether terminates the scenario's event.
     *
     * @param name a plan event name
     */
    public void setEventName( String name ) {
        Event oldEvent = getScenario().getEvent();
        String oldName = oldEvent == null ? "" : oldEvent.getName();
        Event newEvent = null;
        if ( name == null || name.trim().isEmpty() )
            newEvent = planManager.getCurrentPlan().getDefaultEvent();
        else {
            if ( oldEvent == null || !isSame( name, oldName ) )
                newEvent = getQueryService().findOrCreate( Event.class, name );
        }
        doCommand( new UpdatePlanObject( getScenario(), "event", newEvent ) );
        getCommander().cleanup( Event.class, oldName );
    }

    private Event getEvent() {
        return getScenario().getEvent();
    }


    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        makeVisible( target, issuesPanel, getAnalyst().hasIssues( getScenario(), false ) );
        target.addComponent( issuesPanel );
        super.updateWith( target, change );
    }

    // TODO - needed?
    public void refresh( AjaxRequestTarget target ) {
        makeVisible( target, issuesPanel, getAnalyst().hasIssues( getScenario(), false ) );
        target.addComponent( issuesPanel );
    }
}
