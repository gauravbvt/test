package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.util.SemMatch;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.Channels;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;

import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

/**
 * Editor on the details of a scenario (name, description, etc).
 */
public class ScenarioEditPanel extends AbstractCommandablePanel {
    /**
     * An issues panel for scenario issues.
     */
    private IssuesPanel issuesPanel;
    /**
     * How long before scenario self-terminates, if applicable.
     */
    private DelayPanel completionTimePanel;
    /**
     * Expansions.
     */
    private Set<Long> expansions;
    /**
     * The edited scenario.
     */
    private final IModel<Scenario> model;

    public ScenarioEditPanel( String id,
                              IModel<Scenario> model,
                              Set<Long> expansions ) {
        super( id );
        this.model = model;
        this.expansions = expansions;
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
        addLocationField();
        addTimingFields();
        addIssuesPanel();
        add( new AttachmentPanel( "attachments", getModel() ) );
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
        add( descField );
        CheckBox incidentCheckBox = new CheckBox(
                "incident",
                new PropertyModel<Boolean>( this, "incident" ) );
        incidentCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getScenario(), "incident" ) );
            }
        } );
        add( incidentCheckBox );
    }

    private void addIssuesPanel() {
        issuesPanel = new IssuesPanel(
                "issues",
                new PropertyModel<ModelObject>( this, "scenario" ),
                expansions );
        issuesPanel.setOutputMarkupId( true );
        add( issuesPanel );
        makeVisible( issuesPanel, Channels.analyst().hasIssues( model.getObject(), false ) );
    }

    private void addLocationField() {
        final List<String> choices = getDqo().findAllNames( Place.class ) ;
        AutoCompleteTextField<String> locationField = new AutoCompleteTextField<String>(
                "location",
                new PropertyModel<String>( this, "location" ) ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( SemMatch.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        add( new FormComponentLabel( "location-label", locationField ) );
        locationField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getScenario(), "location" ) );
            }
        } );
        add( locationField );
    }

    private void addTimingFields() {
        completionTimePanel = new DelayPanel(
                "completion-time",
                new PropertyModel<ModelObject>( this, "scenario" ),
                "completionTime" );
        completionTimePanel.enable( getScenario().isSelfTerminating() );
        completionTimePanel.setOutputMarkupId( true );
        add( completionTimePanel );
        CheckBox selfTerminatingCheckBox = new CheckBox(
                "self-terminating",
                new PropertyModel<Boolean>( this, "selfTerminating" ) );
        selfTerminatingCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                completionTimePanel.enable( getScenario().isSelfTerminating() );
                target.addComponent( completionTimePanel );
                update( target, new Change( Change.Type.Updated, getScenario(), "selfTerminating" ) );
            }
        } );
        add( selfTerminatingCheckBox );
    }

    public IModel<Scenario> getModel() {
        return model;
    }

    /**
     * Get edited scenario.
     * @return a scenario
     */
    public Scenario getScenario() {
        return getModel().getObject();
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
     * Get whether scenario is incident.
     *
     * @return a boolean
     */
    public boolean isIncident() {
        return getScenario().isIncident();
    }

    /**
     * Set scenario as incident via command.
     *
     * @param val a boolean
     */
    public void setIncident( boolean val ) {
        doCommand( new UpdatePlanObject( getScenario(), "incident", val ) );
    }

    /**
     * Get the location string.
     *
     * @return the name of the location, or the empty string if null
     */
    public String getLocation() {
        final Place location = getScenario().getLocation();
        return location == null ? "" : location.getName();
    }

    /**
     * Set the part's location.
     *
     * @param name if null or empty, set to null; otherwise, only set if different.
     */
    public void setLocation( String name ) {
        Place oldPlace = getScenario().getLocation();
        String oldName = oldPlace == null ? "" : oldPlace.getName();
        Place newPlace = null;
        if ( name == null || name.trim().isEmpty() )
            newPlace = null;
        else {
            if ( oldPlace == null || !isSame( name, oldName ) )
                newPlace = getDqo().findOrCreate( Place.class, name );
        }
        doCommand( new UpdatePlanObject( getScenario(), "location", newPlace ) );
        getCommander().cleanup( Place.class, oldName );
    }

    /**
     * Is part self-terminating?
     *
     * @return a boolean
     */
    public boolean isSelfTerminating() {
        return getScenario().isSelfTerminating();
    }

    /**
     * Sets whether self terminating.
     *
     * @param val a boolean
     */
    public void setSelfTerminating( boolean val ) {
        doCommand( new UpdatePlanObject( getScenario(), "selfTerminating", val ) );
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        makeVisible( target, issuesPanel, Channels.analyst().hasIssues( model.getObject(), false ) );
        target.addComponent( issuesPanel );
        super.updateWith( target, change );
    }

    /**
     * Change visibility.
     *
     * @param target  an ajax request target
     * @param visible a boolean
     */
    public void setVisibility( AjaxRequestTarget target, boolean visible ) {
        makeVisible( target, this, visible );
        if ( visible )
            makeVisible( issuesPanel, Channels.analyst().hasIssues( model.getObject(), false ) );
    }

    public void refresh( AjaxRequestTarget target ) {
        makeVisible( target, issuesPanel, Channels.analyst().hasIssues( model.getObject(), false ) );
        target.addComponent( issuesPanel );
        completionTimePanel.enable( getScenario().isSelfTerminating() );
        target.addComponent( completionTimePanel );
    }
}
