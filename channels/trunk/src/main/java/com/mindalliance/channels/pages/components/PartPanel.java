package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Delay;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateScenarioObject;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.util.SemMatch;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.commons.lang.StringUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

/**
 * A view on a Part.
 */
public class PartPanel extends AbstractCommandablePanel {

    /**
     * The task property.
     */
    private static final String TASK_PROPERTY = "task";                     // NON-NLS

    /**
     * The actor property.
     */
    private static final String ACTOR_PROPERTY = "actor";                   // NON-NLS

    /**
     * The role property.
     */
    private static final String ROLE_PROPERTY = "role";                     // NON-NLS

    /**
     * The organization property.
     */
    private static final String ORG_PROPERTY = "organization";              // NON-NLS

    /**
     * The jurisdiction property.
     */
    private static final String JURISDICTION_PROPERTY = "jurisdiction";     // NON-NLS

    /**
     * The location property.
     */
    private static final String LOCATION_PROPERTY = "location";             // NON-NLS

    /**
     * The empty string.
     */
    private static final String EMPTY_STRING = "";

    /**
     * String comparator for equality tests.
     */
    private static final Collator COMPARATOR = Collator.getInstance();

    static {
        COMPARATOR.setStrength( Collator.PRIMARY );
    }

    /**
     * The part edited by this form.
     */
    private IModel<Part> model;
    /**
     * Text fields.
     */
    private List<TextField<String>> textFields = new ArrayList<TextField<String>>();
    /**
     * Initiated scenario choice.
     */
    private DropDownChoice initiatedScenarioChoice;


    //====================================
    public PartPanel( String id, IModel<Part> model ) {
        super( id );
        super.setOutputMarkupPlaceholderTag( false );
        this.model = model;

        addField( TASK_PROPERTY, getDqo().findAllTasks() );
        addField( ACTOR_PROPERTY, getDqo().findAllNames( Actor.class ) );
        addField( ROLE_PROPERTY, getDqo().findAllNames( Role.class ) );
        addField( ORG_PROPERTY, getDqo().findAllNames( Organization.class ) );
        addField( JURISDICTION_PROPERTY, getDqo().findAllNames( Place.class ) );
        addField( LOCATION_PROPERTY, getDqo().findAllNames( Place.class ) );
        addTimingFields();
    }

    private void addField( final String property, final Collection<String> choices ) {
        final TextField<String> field;
        if ( choices == null ) {
            field = new TextField<String>(
                    property,
                    new PropertyModel<String>( this, property ) );
        } else {
            field = new AutoCompleteTextField<String>(
                    property,
                    new PropertyModel<String>( this, property ) ) {
                protected Iterator<String> getChoices( String s ) {
                    List<String> candidates = new ArrayList<String>();
                    for ( String choice : choices ) {
                        if ( SemMatch.matches( s, choice ) ) candidates.add( choice );
                    }
                    return candidates.iterator();
                }
            };
        }
        field.setOutputMarkupId( true );
        field.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addIssues( field, getPart(), property );
                target.addComponent( field );
                update( target, new Change( Change.Type.Updated, getPart(), property ) );
            }
        } );

        // Add style mods from scenario analyst.
        addIssues( field, getPart(), property );
        field.setEnabled( isLockedByUser( getPart() ) );
        add( field );
        textFields.add( field );
    }

    /**
     * Add issues annotations to a component.
     *
     * @param component the component
     * @param object    the object of the issues
     * @param property  the property of concern. If null, get issues of object
     */
    protected void addIssues( FormComponent<?> component, ModelObject object, String property ) {
        Analyst analyst = ( (Project) getApplication() ).getAnalyst();
        String issue = property == null ?
                analyst.getIssuesSummary( object, false ) :
                analyst.getIssuesSummary( object, property );
        if ( !issue.isEmpty() ) {
            component.add(
                    new AttributeModifier(
                            "class", true, new Model<String>( "error" ) ) );
            component.add(
                    new AttributeModifier(
                            "title", true, new Model<String>( issue ) ) );                // NON-NLS
        } else {
            component.add(
                    new AttributeModifier(
                            "class", true, new Model<String>( "no-error" ) ) );
            component.add(
                    new AttributeModifier(
                            "title", true, new Model<String>( "" ) ) );
        }
    }


    private void addTimingFields() {
        final DelayPanel repeatsEveryPanel = new DelayPanel(
                "repeats-every",
                new PropertyModel<ModelObject>( this, "part" ),
                "repeatsEvery" );
        repeatsEveryPanel.setOutputMarkupId( true );
        repeatsEveryPanel.enable( getPart().isRepeating()
                && isLockedByUser( getPart() ) );
        add( repeatsEveryPanel );
        final DelayPanel completionTimePanel = new DelayPanel(
                "completion-time",
                new PropertyModel<ModelObject>( this, "part" ),
                "completionTime" );
        completionTimePanel.enable( getPart().isSelfTerminating()
                && isLockedByUser( getPart() ) );
        completionTimePanel.setOutputMarkupId( true );
        add( completionTimePanel );
        CheckBox selfTerminatingCheckBox = new CheckBox(
                "self-terminating",
                new PropertyModel<Boolean>( this, "selfTerminating" ) );
        selfTerminatingCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                completionTimePanel.enable( getPart().isSelfTerminating()
                        && isLockedByUser( getPart() ) );
                target.addComponent( completionTimePanel );
                update( target, new Change( Change.Type.Updated, getPart(), "selfTerminating" ) );
            }
        } );
        selfTerminatingCheckBox.setEnabled( isLockedByUser( getPart() ) );
        add( selfTerminatingCheckBox );
        CheckBox repeatingCheckBox = new CheckBox(
                "repeating",
                new PropertyModel<Boolean>( this, "repeating" ) );
        add( repeatingCheckBox );
        repeatingCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                repeatsEveryPanel.enable( getPart().isRepeating() );
                target.addComponent( repeatsEveryPanel );
                update( target, new Change( Change.Type.Updated, getPart(), "repeating" ) );
            }
        } );
        repeatingCheckBox.setEnabled( isLockedByUser( getPart() ) );
        CheckBox startWithScenarioCheckBox = new CheckBox(
                "startsWithScenario",
                new PropertyModel<Boolean>( this, "startsWithScenario" ) );
        startWithScenarioCheckBox.setEnabled( isLockedByUser( getPart() ) );
        add( startWithScenarioCheckBox );
        startWithScenarioCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "startsWithScenario" ) );
            }
        } );
        CheckBox terminatesScenarioCheckBox = new CheckBox(
                "terminatesScenario",
                new PropertyModel<Boolean>( this, "terminatesScenario" ) );
        terminatesScenarioCheckBox.setEnabled( isLockedByUser( getPart() ) );
        add( terminatesScenarioCheckBox );
        terminatesScenarioCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "terminatesScenario" ) );
            }
        } );
        final CheckBox initiatesScenarioCheckBox = new CheckBox(
                "initiatesScenario",
                new PropertyModel<Boolean>( this, "initiatesScenario" ) );
        initiatesScenarioCheckBox.setEnabled( isLockedByUser( getPart() ) );
        add( initiatesScenarioCheckBox );
        initiatesScenarioCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                initiatedScenarioChoice.setEnabled( initiatesScenarioCheckBox.getInput() != null);
                target.addComponent( initiatedScenarioChoice );
            }
        } );
        initiatedScenarioChoice = new DropDownChoice<Scenario>(
                "initiatedScenario",
                new PropertyModel<Scenario>( this, "initiatedScenario" ),
                new PropertyModel<List<? extends Scenario>>( this, "initiatableScenarios" ),
                new IChoiceRenderer<Scenario>() {
                    public Object getDisplayValue( Scenario scenario ) {
                        return StringUtils.abbreviate( scenario.getName(), 20 );
                    }

                    public String getIdValue( Scenario scenario, int i ) {
                        return Long.toString( scenario.getId() );
                    }
                }
        );
        initiatedScenarioChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "initiatedScenario" ) );
            }
        } );
        initiatedScenarioChoice.setEnabled( isLockedByUser( getPart() ) );
        add( initiatedScenarioChoice );
    }

    //====================================
    /**
     * Get all scenarios that could be initiated by this part.
     * @return a list of scenarios
     */
    public List<Scenario> getInitiatableScenarios() {
        List<Scenario> scenarios = new ArrayList<Scenario>();
        for ( Scenario sc : getDqo().list( Scenario.class ) ) {
            if ( sc != getPart().getScenario() ) scenarios.add( sc );
        }
        Collections.sort( scenarios, new Comparator<Scenario>() {
            public int compare( Scenario o1, Scenario o2 ) {
                return Collator.getInstance().compare( o1.getName(), o2.getName() );
            }
        } );
        return scenarios;
    }

    /**
     * Get the actor string.
     *
     * @return the name of the actor, or the empty string if null
     */
    public String getActor() {
        final Actor actor = getPart().getActor();
        return actor == null ? EMPTY_STRING : actor.getName();
    }

    /**
     * Get the jurisdiction string.
     *
     * @return the name of the jurisdiction, or the empty string if null
     */
    public String getJurisdiction() {
        final Place jurisdiction = getPart().getJurisdiction();
        return jurisdiction == null ? EMPTY_STRING : jurisdiction.getName();
    }

    /**
     * Get the location string.
     *
     * @return the name of the location, or the empty string if null
     */
    public String getLocation() {
        final Place location = getPart().getLocation();
        return location == null ? EMPTY_STRING : location.getName();
    }

    /**
     * Get the organization string.
     *
     * @return the name of the organization, or the empty string if null
     */
    public String getOrganization() {
        final Organization organization = getPart().getOrganization();
        return organization == null ? EMPTY_STRING : organization.getName();
    }

    /**
     * Get the role string.
     *
     * @return the name of the role, or the empty string if null
     */
    public String getRole() {
        final Role role = getPart().getRole();
        return role == null ? EMPTY_STRING : role.getName();
    }

    public String getTask() {
        return getPart().getTask();
    }

    /**
     * Set the part's actor.
     *
     * @param name if null or empty, set to null; otherwise, only set if different.
     */
    public void setActor( String name ) {
        Actor oldActor = getPart().getActor();
        String oldName = oldActor == null ? "" : oldActor.getName();
        Actor newActor = null;
        if ( name == null || name.trim().isEmpty() )
            newActor = null;
        else {
            if ( oldActor == null || !isSame( name, oldName ) )
                newActor = getDqo().findOrCreate( Actor.class, name );
        }
        doCommand( new UpdateScenarioObject( getPart(), "actor", newActor ) );
        getCommander().cleanup( Actor.class, oldName );
    }

    /**
     * Set the part's jurisdiction.
     *
     * @param name if null or empty, set to null; otherwise, only set if different.
     */
    public void setJurisdiction( String name ) {
        Place oldPlace = getPart().getJurisdiction();
        String oldName = oldPlace == null ? "" : oldPlace.getName();
        Place newPlace = null;
        if ( name == null || name.trim().isEmpty() )
            newPlace = null;
        else {
            if ( oldPlace == null || !isSame( name, oldName ) )
                newPlace = getDqo().findOrCreate( Place.class, name );
        }
        doCommand( new UpdateScenarioObject( getPart(), "jurisdiction", newPlace ) );
        getCommander().cleanup( Place.class, oldName );
    }

    /**
     * Set the part's location.
     *
     * @param name if null or empty, set to null; otherwise, only set if different.
     */
    public void setLocation( String name ) {
        Place oldPlace = getPart().getLocation();
        String oldName = oldPlace == null ? "" : oldPlace.getName();
        Place newPlace = null;
        if ( name == null || name.trim().isEmpty() )
            newPlace = null;
        else {
            if ( oldPlace == null || !isSame( name, oldName ) )
                newPlace = getDqo().findOrCreate( Place.class, name );
        }
        doCommand( new UpdateScenarioObject( getPart(), "location", newPlace ) );
        getCommander().cleanup( Place.class, oldName );
    }

    /**
     * Set the part's organization.
     *
     * @param name if null or empty, set to null; otherwise, only set if different.
     */
    public void setOrganization( String name ) {
        Organization oldOrg = getPart().getOrganization();
        String oldName = oldOrg == null ? "" : oldOrg.getName();
        Organization newOrg = null;
        if ( name == null || name.trim().isEmpty() )
            newOrg = null;
        else {
            if ( oldOrg == null || !isSame( name, oldName ) )
                newOrg = getDqo().findOrCreate( Organization.class, name );
        }
        doCommand( new UpdateScenarioObject( getPart(), "organization", newOrg ) );
        getCommander().cleanup( Organization.class, oldName );
    }

    /**
     * Set the part's role.
     *
     * @param name if null or empty, set to null; otherwise, only set if different.
     */
    public void setRole( String name ) {
        Role oldRole = getPart().getRole();
        String oldName = oldRole == null ? "" : oldRole.getName();
        Role newRole = null;
        if ( name == null || name.trim().isEmpty() )
            newRole = null;
        else {
            if ( oldRole == null || !isSame( name, oldName ) )
                newRole = getDqo().findOrCreate( Role.class, name );
        }
        doCommand( new UpdateScenarioObject( getPart(), "role", newRole ) );
        getCommander().cleanup( Role.class, oldName );
    }

    /**
     * Set the part's task.
     *
     * @param task the task
     */
    public void setTask( String task ) {
        doCommand( new UpdateScenarioObject( getPart(), "task", task ) );
    }

    public Delay getRepeatsEvery() {
        return getPart().getRepeatsEvery();
    }

    /**
     * Sets repeat period.
     *
     * @param delay a delay
     */
    public void setRepeatsEvery( Delay delay ) {
        doCommand( new UpdateScenarioObject( getPart(), "repeatsEvery", delay ) );
    }

    public Delay getCompletionTime() {
        return getPart().getCompletionTime();
    }

    /**
     * Sets completion time.
     *
     * @param delay a delay
     */
    public void setCompletionTime( Delay delay ) {
        doCommand( new UpdateScenarioObject( getPart(), "completionTime", delay ) );
    }

    /**
     * Is part self-terminating?
     *
     * @return a boolean
     */
    public boolean isSelfTerminating() {
        return getPart().isSelfTerminating();
    }

    /**
     * Sets whether self terminating.
     *
     * @param val a boolean
     */
    public void setSelfTerminating( boolean val ) {
        doCommand( new UpdateScenarioObject( getPart(), "selfTerminating", val ) );
    }

    /**
     * Is part repeating?
     *
     * @return a boolean
     */
    public boolean isRepeating() {
        return getPart().isRepeating();
    }

    /**
     * Sets whether repeating.
     *
     * @param val a boolean
     */
    public void setRepeating( boolean val ) {
        doCommand( new UpdateScenarioObject( getPart(), "repeating", val ) );
    }

    /**
     * Does part start with scenario?
     *
     * @return a boolean
     */
    public boolean isStartsWithScenario() {
        return getPart().isStartsWithScenario();
    }

    /**
     * Sets whether starts with scenario.
     *
     * @param val a boolean
     */
    public void setStartsWithScenario( boolean val ) {
        doCommand( new UpdateScenarioObject( getPart(), "startsWithScenario", val ) );
    }

    /**
     * Does part terminate scenario?
     *
     * @return a boolean
     */
    public boolean isTerminatesScenario() {
        return getPart().isTerminatesScenario();
    }

    /**
     * Sets whether terminates scenario.
     *
     * @param val a boolean
     */
    public void setTerminatesScenario( boolean val ) {
        doCommand( new UpdateScenarioObject( getPart(), "terminatesScenario", val ) );
    }

    /**
     * Does part initiates a scenario?
     *
     * @return a boolean
     */
    public boolean isInitiatesScenario() {
        return getPart().getInitiatedScenario() != null;
    }

    /**
     * Sets whether part initiates a scenario.
     *
     * @param initiates a boolean
     */
    public void setInitiatesScenario( boolean initiates ) {
        if (!initiates) {
            doCommand( new UpdateScenarioObject( getPart(), "initiatedScenario", null ) );
        }
    }


    /**
     * Scenario initiated by part, if any.
     *
     * @return a scenario
     */
    public Scenario getInitiatedScenario() {
        return getPart().getInitiatedScenario();
    }

    /**
     * Sets whether terminates scenario.
     *
     * @param sc a scenario
     */
    public void setInitiatedScenario( Scenario sc ) {
        doCommand( new UpdateScenarioObject( getPart(), "initiatedScenario", sc ) );
    }


    /**
     * Get edited part.
     *
     * @return a part
     */
    public final Part getPart() {
        return model.getObject();
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        if ( change.getType() == Change.Type.Updated ) {
            String property = change.getProperty();
            for ( TextField<String> field : textFields ) {
                if ( field.getId().equals( property ) )
                    addIssues( field, getPart(), property );
            }
            target.addComponent( initiatedScenarioChoice );
        }
        super.updateWith( target, change );
    }

}
