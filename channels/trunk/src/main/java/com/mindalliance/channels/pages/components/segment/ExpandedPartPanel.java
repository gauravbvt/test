package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateSegmentObject;
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
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.DelayPanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.pages.components.entities.EntityReferencePanel;
import org.apache.commons.lang.WordUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A view on a Part.
 */
public class ExpandedPartPanel extends AbstractCommandablePanel {

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
     * Links to entities that are part proeperty values.
     */
    private Map<String, ModelObjectLink> entityLinks = new HashMap<String, ModelObjectLink>();

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
     * Part issues panel.
     */
    private IssuesPanel partIssuesPanel;

    /**
     * Part description.
     */
    private TextArea<String> partDescription;

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
     * Whether executed as team by assignees.
     */
    private CheckBox asTeamCheckBox;
    /**
     * Whether starts with segment.
     */
    private CheckBox startWithSegmentCheckBox;
    /**
     * Whether terminates event segment responds to.
     */
    private CheckBox terminatesSegmentCheckBox;
    /**
     * Goals achieved by part.
     */
    private Component taskGoalsPanel;
    /**
     * Part summary.
     */
    private Label summaryLabel;
    /**
     * Attachments panel.
     */
    private AttachmentPanel attachmentsPanel;


    //====================================
    public ExpandedPartPanel( String id, IModel<Part> model, Set<Long> expansions ) {
        super( id, model, expansions );
        super.setOutputMarkupPlaceholderTag( false );
        setOutputMarkupId( true );
        this.model = model;
        addSummaryLabel();
        addPartDescription();
        addTaskField();
        addEntityFields();
        addEventInitiation();
        addAsTeam();
        addTimingFields();
        addGoals();
        addIssuesPanel();
        addAttachments();
        adjustFields();
    }

    private void addSummaryLabel() {
        summaryLabel = new Label( "partSummary", new PropertyModel( getPart(), "summary" ) );
        summaryLabel.setOutputMarkupId( true );
        summaryLabel.add( new AjaxEventBehavior( "onclick" ) {
            protected void onEvent( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Collapsed, getPart() ) );
            }
        } );
        addOrReplace( summaryLabel );
    }


    private void addPartDescription() {
        partDescription = new TextArea<String>( "description",                            // NON-NLS
                new PropertyModel<String>( this, "description" ) );
        partDescription.add( new AjaxFormComponentUpdatingBehavior( "onchange") {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "description" ) );
            }
        } );
        partDescription.setOutputMarkupId( true );
        add( partDescription );
    }

    private void addEntityFields() {
        entityFields = new ArrayList<EntityReferencePanel<? extends ModelEntity>>();
        addActorField();
        addRoleField();
        addOrganizationField();
        addJurisdictionField();
        addLocationField();
        addEntityLinks();
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

    private void addIssuesPanel() {
        partIssuesPanel = new IssuesPanel( "issues", model, getExpansions() );        // NON-NLS
        partIssuesPanel.setOutputMarkupId( true );
        addOrReplace( partIssuesPanel );
    }

    private void addAttachments() {
        attachmentsPanel = new AttachmentPanel( "attachments", model );
        add( attachmentsPanel );
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
        startWithSegmentCheckBox.setEnabled( isLockedByUser( getPart() ) );
        asTeamCheckBox.setEnabled( isLockedByUser( getPart() ) );
        terminatesSegmentCheckBox.setEnabled( isLockedByUser( getPart() ) );
        initiatedEventField.setEnabled( isLockedByUser( getPart() ) );
        partDescription.setEnabled( isLockedByUser( getPart() ) );
        boolean partHasIssues = getAnalyst().hasIssues( getPart(), false );
        makeVisible( partIssuesPanel, partHasIssues );
    }

    private void addActorField() {
        EntityReferencePanel<Actor> field = new EntityReferencePanel<Actor>(
                ACTOR_PROPERTY,
                new PropertyModel<Part>( this, "part" ),
                getAllActorNames(),
                ACTOR_PROPERTY,
                Actor.class );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addRoleField() {
        EntityReferencePanel<Role> field = new EntityReferencePanel<Role>(
                ROLE_PROPERTY,
                new PropertyModel<Part>( this, "part" ),
                getAllRoleNames(),
                ROLE_PROPERTY,
                Role.class );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addOrganizationField() {
        EntityReferencePanel<Organization> field = new EntityReferencePanel<Organization>(
                ORG_PROPERTY,
                new PropertyModel<Part>( this, "part" ),
                getAllOrganizationNames(),
                ORG_PROPERTY,
                Organization.class );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addJurisdictionField() {
        EntityReferencePanel<Place> field = new EntityReferencePanel<Place>(
                JURISDICTION_PROPERTY,
                new PropertyModel<Part>( this, "part" ),
                getAllPlaceNames(),
                JURISDICTION_PROPERTY,
                Place.class );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addLocationField() {
        EntityReferencePanel<Place> field = new EntityReferencePanel<Place>(
                LOCATION_PROPERTY,
                new PropertyModel<Part>( this, "part" ),
                getAllPlaceNames(),
                LOCATION_PROPERTY,
                Place.class );
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

    private void addEntityLinks() {
        String[] entityProps = {"location", "actor", "role",
                "jurisdiction", "organization", "initiatedEvent"};
        Part part = getPart();
        for ( String prop : entityProps ) {
            ModelObjectLink moLink = new ModelObjectLink(
                    prop + "-link",
                    new PropertyModel<ModelEntity>( part, prop ),
                    new Model<String>(
                            prop.equals( "initiatedEvent" )
                                    ? "event"
                                    : WordUtils.capitalize( prop.equals( "actor" )
                                    ? "agent"
                                    : prop ) ) );
            moLink.setOutputMarkupId( true );
            entityLinks.put( prop, moLink );
            addOrReplace( moLink );
        }
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
        startWithSegmentCheckBox = new CheckBox(
                "startsWithSegment",
                new PropertyModel<Boolean>( this, "startsWithSegment" ) );
        add( startWithSegmentCheckBox );
        startWithSegmentCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "startsWithSegment" ) );
            }
        } );
        add( new ModelObjectLink( "event-link",
                new PropertyModel<Event>( this, "part.segment.event" ),
                new PropertyModel<String>( this, "part.segment.event.name" ) ) );
        add( new ModelObjectLink( "phase-link",
                new PropertyModel<Event>( this, "part.segment.phase" ),
                new PropertyModel<String>( this, "part.segment.phase.name" ) ) );
        terminatesSegmentCheckBox = new CheckBox(
                "terminatesEventPhase",
                new PropertyModel<Boolean>( this, "terminatesEventPhase" ) );
        add( terminatesSegmentCheckBox );
        terminatesSegmentCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "terminatesEventPhase" ) );
            }
        } );
    }

    private void addAsTeam() {
        asTeamCheckBox = new CheckBox(
                "asTeam",
                new PropertyModel<Boolean>( this, "asTeam" ) );
        add( asTeamCheckBox );
        asTeamCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "asTeam" ) );
            }
        } );
    }

    private void addGoals() {
        if ( getPart().getSegment().getGoals().isEmpty() ) {
            taskGoalsPanel = new Label( "goals", new Model<String>( "(No goals)" ) );
        } else {
            taskGoalsPanel = new TaskGoalsPanel(
                    "goals",
                    new PropertyModel<Part>( this, "part" ),
                    getExpansions() );
        }
        taskGoalsPanel.setOutputMarkupId( true );
        addOrReplace( taskGoalsPanel );
        AjaxFallbackLink segmentGoalsLink = new AjaxFallbackLink( "segment-goals-link" ) {
            public void onClick( AjaxRequestTarget target ) {
                update(
                        target,
                        new Change(
                                Change.Type.AspectViewed,
                                getPart().getSegment(),
                                SegmentEditPanel.GOALS ) );
            }
        };
        segmentGoalsLink.setOutputMarkupId( true );
        addOrReplace( segmentGoalsLink );
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
        doCommand( new UpdateSegmentObject( getPart(), "actor", newActor ) );
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
        doCommand( new UpdateSegmentObject( getPart(), "jurisdiction", newPlace ) );
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
        doCommand( new UpdateSegmentObject( getPart(), "location", newPlace ) );
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
        doCommand( new UpdateSegmentObject( getPart(), "organization", newOrg ) );
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
        doCommand( new UpdateSegmentObject( getPart(), "role", newRole ) );
        getCommander().cleanup( Role.class, oldName );
    }

    /**
     * Set the part's task.
     *
     * @param task the task
     */
    public void setTask( String task ) {
        doCommand( new UpdateSegmentObject( getPart(), "task", task ) );
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
        doCommand( new UpdateSegmentObject( getPart(), "repeatsEvery", delay ) );
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
        doCommand( new UpdateSegmentObject( getPart(), "completionTime", delay ) );
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
        doCommand( new UpdateSegmentObject( getPart(), "selfTerminating", val ) );
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
        doCommand( new UpdateSegmentObject( getPart(), "repeating", val ) );
    }

    /**
     * Does part start with segment?
     *
     * @return a boolean
     */
    public boolean isStartsWithSegment() {
        return getPart().isStartsWithSegment();
    }

    /**
     * Sets whether starts with segment.
     *
     * @param val a boolean
     */
    public void setStartsWithSegment( boolean val ) {
        doCommand( new UpdateSegmentObject( getPart(), "startsWithSegment", val ) );
    }

    /**
     * Do assignees execute as team?
     *
     * @return a boolean
     */
    public boolean isAsTeam() {
        return getPart().isAsTeam();
    }

    /**
     * Sets whether assignees execute as team.
     *
     * @param val a boolean
     */
    public void setAsTeam( boolean val ) {
        doCommand( new UpdateSegmentObject( getPart(), "asTeam", val ) );
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
        doCommand( new UpdateSegmentObject( getPart(), "terminatesEventPhase", val ) );
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
     * Sets whether terminates the segment's event.
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
        doCommand( new UpdateSegmentObject( getPart(), "initiatedEvent", newEvent ) );
        getCommander().cleanup( Event.class, oldName );
    }

    /**
     * Get part description.
     *
     * @return a string
     */
    public String getDescription() {
        return getPart().getDescription();
    }

    /**
     * Set part description via command.
     *
     * @param val a string
     */
    public void setDescription( String val ) {
        doCommand( new UpdateSegmentObject( getPart(), "description", val ) );
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
        addGoals();
        // todo - remove
        target.addComponent( partDescription );
        // todo - remove
        target.addComponent( partIssuesPanel );
        target.addComponent( attachmentsPanel );
        target.addComponent( this );
    }

    /**
     * {@inheritDoc}
     */
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        refresh( target );
    }
    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( !change.isNone() ) {
            if ( change.isUpdated() ) {
                String property = change.getProperty();
                addSummaryLabel();
                target.addComponent( summaryLabel );
                for ( EntityReferencePanel entityReferencePanel : entityFields ) {
                    entityReferencePanel.updateIssues();
                    target.addComponent( entityReferencePanel );
                }
                if ( change.getSubject( getQueryService() ).equals( getPart() ) ) {
                    updateEntityLink( target, change );
                }
                if ( property.equals( "goals" ) ) {
                    addGoals();
                    target.addComponent( taskGoalsPanel );
                }
                target.addComponent( initiatedEventField );
            }
            if ( !change.isDisplay() && !change.isCopied() ) {
                makeVisible( target, partIssuesPanel,
                        getAnalyst().hasIssues( getPart(), false ) );
                target.addComponent( partIssuesPanel );
            }
        }
        super.updateWith( target, change, updated );
    }

    private void updateEntityLink( AjaxRequestTarget target, Change change ) {
        String property = change.getProperty();
        ModelObjectLink moLink = entityLinks.get( property );
        if ( moLink != null ) target.addComponent( moLink );
    }


}
