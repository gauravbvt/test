package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateScenarioObject;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Delay;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
     * Entity reference fields.
     */
    private List<EntityReferencePanel<? extends ModelEntity>> entityFields =
            new ArrayList<EntityReferencePanel<? extends ModelEntity>>();
    /**
     * Task.
     */
    private TextField<String> taskField;
    /**
     * Initiated event.
     */
    private TextField<String> initiatedEventField;
    /**
     * Repetition.
     */
    private DelayPanel repeatsEveryPanel;
    /**
     * Completion.
     */
    private DelayPanel completionTimePanel;
    /**
     * Self-termination.
     */
    private CheckBox selfTerminatingCheckBox;
    /**
     * Whether repeating.
     */
    private CheckBox repeatingCheckBox;
    /**
     * Whether starts with scenario.
     */
    private CheckBox startWithScenarioCheckBox;
    /**
     * Whether terminates event scenario responds to.
     */
    private CheckBox terminatesScenarioCheckBox;
    /**
     * Mitigations by part.
     */
    private Component mitigationsPanel;

    //====================================
    public PartPanel( String id, IModel<Part> model ) {
        super( id );
        super.setOutputMarkupPlaceholderTag( false );
        this.model = model;

        addTaskField();
        addEntityFields();
        addEventInitiation();
        addTimingFields();
        addMitigations();
        adjustFields();
    }

    private void addEntityFields() {
        entityFields = new ArrayList<EntityReferencePanel<? extends ModelEntity>>();
        addActorField();
        addRoleField();
        addOrganizationField();
        addJurisdictionField();
        addLocationField();
    }

    public List<String> getAllTasks() {
        return getQueryService().findAllTasks();
    }

    public List<String> getAllActorNames() {
        return getQueryService().findAllEntityNames( Actor.class );
    }

    public List<String> getAllRoleNames() {
        return getQueryService().findAllEntityNames( Role.class );
    }

    public List<String> getAllOrganizationNames() {
        return getQueryService().findAllEntityNames( Organization.class );
    }

    public List<String> getAllPlaceNames() {
        return getQueryService().findAllEntityNames( Place.class );
    }

    public List<String> getAllEventNames() {
        return getQueryService().findAllEntityNames( Event.class );
    }

    private void adjustFields() {
        taskField.setEnabled( isLockedByUser( getPart() ) );
        for ( EntityReferencePanel entityReferencePanel : entityFields ) {
            entityReferencePanel.enable( isLockedByUser( getPart() ) );
        }
        repeatsEveryPanel.enable( getPart().isRepeating()
                && isLockedByUser( getPart() ) );
        completionTimePanel.enable( getPart().isSelfTerminating()
                && isLockedByUser( getPart() ) );
        selfTerminatingCheckBox.setEnabled( isLockedByUser( getPart() ) );
        repeatingCheckBox.setEnabled( isLockedByUser( getPart() ) );
        startWithScenarioCheckBox.setEnabled( isLockedByUser( getPart() ) );
        terminatesScenarioCheckBox.setEnabled( isLockedByUser( getPart() ) );
        initiatedEventField.setEnabled( isLockedByUser( getPart() ) );
    }

    private void addActorField() {
        EntityReferencePanel<Actor> field = new EntityReferencePanel<Actor>(
                ACTOR_PROPERTY,
                new PropertyModel<Part>( this, "part" ),
                getAllActorNames(),
                ACTOR_PROPERTY,
                Actor.class,
                null );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addRoleField() {
        EntityReferencePanel<Role> field = new EntityReferencePanel<Role>(
                ROLE_PROPERTY,
                new PropertyModel<Part>( this, "part" ),
                getAllRoleNames(),
                ROLE_PROPERTY,
                Role.class,
                null );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addOrganizationField() {
        EntityReferencePanel<Organization> field = new EntityReferencePanel<Organization>(
                ORG_PROPERTY,
                new PropertyModel<Part>( this, "part" ),
                getAllOrganizationNames(),
                ORG_PROPERTY,
                Organization.class,
                null );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addJurisdictionField() {
        EntityReferencePanel<Place> field = new EntityReferencePanel<Place>(
                JURISDICTION_PROPERTY,
                new PropertyModel<Part>( this, "part" ),
                getAllPlaceNames(),
                JURISDICTION_PROPERTY,
                Place.class,
                null );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addLocationField() {
        EntityReferencePanel<Place> field = new EntityReferencePanel<Place>(
                LOCATION_PROPERTY,
                new PropertyModel<Part>( this, "part" ),
                getAllPlaceNames(),
                LOCATION_PROPERTY,
                Place.class,
                null );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addTaskField() {
        final PropertyModel<List<String>> choices = new PropertyModel<List<String>>( this, "allTasks" );
        taskField = new AutoCompleteTextField<String>(
                TASK_PROPERTY,
                new PropertyModel<String>( this, TASK_PROPERTY ) ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices.getObject() ) {
                    if ( getQueryService().likelyRelated( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();

            }
        };
        taskField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "task" ) );
            }
        } );
        add( taskField );
    }

    private void addEventInitiation() {
        final PropertyModel<List<String>> choices = new PropertyModel<List<String>>( this, "allEventNames" );
        initiatedEventField = new AutoCompleteTextField<String>(
                "initiatedEvent",
                new PropertyModel<String>( this, "initiatedEventName" ) ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices.getObject() ) {
                    if ( getQueryService().likelyRelated( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();

            }
        };
        initiatedEventField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "initiatedEvent" ) );
            }
        } );
        add( initiatedEventField );
    }

    private void addTimingFields() {
        repeatsEveryPanel = new DelayPanel(
                "repeats-every",
                new PropertyModel<ModelObject>( this, "part" ),
                "repeatsEvery" );
        repeatsEveryPanel.setOutputMarkupId( true );
        add( repeatsEveryPanel );
        completionTimePanel = new DelayPanel(
                "completion-time",
                new PropertyModel<ModelObject>( this, "part" ),
                "completionTime" );
        completionTimePanel.setOutputMarkupId( true );
        add( completionTimePanel );
        selfTerminatingCheckBox = new CheckBox(
                "self-terminating",
                new PropertyModel<Boolean>( this, "selfTerminating" ) );
        selfTerminatingCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                completionTimePanel.enable( getPart().isSelfTerminating()
                        && isLockedByUser( getPart() ) );
                target.addComponent( completionTimePanel );
                update( target, new Change( Change.Type.Updated, getPart(), "selfTerminating" ) );
            }
        } );
        add( selfTerminatingCheckBox );
        repeatingCheckBox = new CheckBox(
                "repeating",
                new PropertyModel<Boolean>( this, "repeating" ) );
        add( repeatingCheckBox );
        repeatingCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                repeatsEveryPanel.enable( getPart().isRepeating() );
                target.addComponent( repeatsEveryPanel );
                update( target, new Change( Change.Type.Updated, getPart(), "onclick" ) );
            }
        } );
        startWithScenarioCheckBox = new CheckBox(
                "startsWithScenario",
                new PropertyModel<Boolean>( this, "startsWithScenario" ) );
        add( startWithScenarioCheckBox );
        startWithScenarioCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "startsWithScenario" ) );
            }
        } );
        add( new ModelObjectLink( "event-link",
                new PropertyModel<Event>( this, "part.scenario.event" ),
                new PropertyModel<String>( this, "part.scenario.event.name" ) ) );
        add( new ModelObjectLink( "phase-link",
                new PropertyModel<Event>( this, "part.scenario.phase" ),
                new PropertyModel<String>( this, "part.scenario.phase.name" ) ) );
        terminatesScenarioCheckBox = new CheckBox(
                "terminatesEventPhase",
                new PropertyModel<Boolean>( this, "terminatesEventPhase" ) );
        add( terminatesScenarioCheckBox );
        terminatesScenarioCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "terminatesEventPhase" ) );
            }
        } );
    }

    private void addMitigations() {
        if ( getPart().getScenario().getRisks().isEmpty() ) {
            mitigationsPanel = new Label( "mitigations", new Model<String>( "(No managed risks)" ) );
        } else {
            mitigationsPanel = new MitigationsPanel(
                    "mitigations",
                    new PropertyModel<Part>( this, "part" ),
                    getExpansions() );
        }
        mitigationsPanel.setOutputMarkupId( true );
        addOrReplace( mitigationsPanel );
    }

    //====================================

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
                newActor = getQueryService().findOrCreate( Actor.class, name );
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
                newPlace = getQueryService().findOrCreate( Place.class, name );
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
                newPlace = getQueryService().findOrCreate( Place.class, name );
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
                newOrg = getQueryService().findOrCreate( Organization.class, name );
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
                newRole = getQueryService().findOrCreate( Role.class, name );
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
     * Does part terminate event?
     *
     * @return a boolean
     */
    public boolean isTerminatesEventPhase() {
        return getPart().isTerminatesEventPhase();
    }

    /**
     * Sets whether terminates event.
     *
     * @param val a boolean
     */
    public void setTerminatesEventPhase( boolean val ) {
        doCommand( new UpdateScenarioObject( getPart(), "terminatesEventPhase", val ) );
    }

    /**
     * Event initiated by part, if any.
     *
     * @return a plan event
     */
    public Event getInitiatedEvent() {
        return getPart().getInitiatedEvent();
    }

    /**
     * Name of event initiated by part, if any.
     *
     * @return a plan event name
     */
    public String getInitiatedEventName() {
        Event event = getInitiatedEvent();
        return event != null ? event.getName() : "";
    }

    /**
     * Sets whether terminates the scenario's event.
     *
     * @param name a plan event name
     */
    public void setInitiatedEventName( String name ) {
        Event oldEvent = getPart().getInitiatedEvent();
        String oldName = oldEvent == null ? "" : oldEvent.getName();
        Event newEvent = null;
        if ( name == null || name.trim().isEmpty() )
            newEvent = null;
        else {
            if ( oldEvent == null || !isSame( name, oldName ) )
                newEvent = getQueryService().findOrCreateType( Event.class, name );
        }
        doCommand( new UpdateScenarioObject( getPart(), "initiatedEvent", newEvent ) );
        getCommander().cleanup( Event.class, oldName );
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
     * Refresh part panel.
     *
     * @param target an ajax request target
     */
    public void refresh( AjaxRequestTarget target ) {
        addEntityFields();
        adjustFields();
        addMitigations();
        target.addComponent( this );
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.getType() == Change.Type.Updated ) {
            String property = change.getProperty();
            for ( EntityReferencePanel entityReferencePanel : entityFields ) {
                entityReferencePanel.updateIssues();
                target.addComponent( entityReferencePanel );
            }
            if ( property.equals( "mitigations" ) ) {
                addMitigations();
                target.addComponent( mitigationsPanel );
            }
            target.addComponent( initiatedEventField );
        }
        super.updateWith( target, change, updated );
    }

}
