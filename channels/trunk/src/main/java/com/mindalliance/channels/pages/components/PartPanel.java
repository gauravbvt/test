package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Delay;
import com.mindalliance.channels.util.SemMatch;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.ScenarioPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.IModel;

import java.text.Collator;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * A view on a Part.
 */
public class PartPanel extends AbstractUpdatablePanel {

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
     * The task property.
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
    private Part part;

    //====================================
    public PartPanel( String id, Part part ) {
        super( id );
        super.setDefaultModel( new CompoundPropertyModel( this ) );
        super.setOutputMarkupPlaceholderTag( false );
        setPart( part );

        addField( TASK_PROPERTY, findAllTaskNames() );
        addField( ACTOR_PROPERTY, findAllActorNames() );
        add( makeLink( "actor-link",                                           // NON-NLS
                new PropertyModel<ModelObject>( part, ACTOR_PROPERTY ) ) );
        addField( ROLE_PROPERTY, findAllRoleNames() );
        add( makeLink( "role-link",                                            // NON-NLS
                new PropertyModel<ModelObject>( part, ROLE_PROPERTY ) ) );
        addField( ORG_PROPERTY, findAllOrganizationNames() );
        add( makeLink( "org-link",                                             // NON-NLS
                new PropertyModel<ModelObject>( part, ORG_PROPERTY ) ) );
        addField( JURISDICTION_PROPERTY, findAllPaceNames() );
        add( makeLink( "juris-link",                                           // NON-NLS
                new PropertyModel<ModelObject>( part, JURISDICTION_PROPERTY ) ) );
        addField( LOCATION_PROPERTY, findAllPaceNames() );
        add( makeLink( "loc-link",                                             // NON-NLS
                new PropertyModel<ModelObject>( part, LOCATION_PROPERTY ) ) );
        addTimingFields();
    }

    private Set<String> findAllActorNames() {
        Set<String> names = new HashSet<String>();
        for ( Actor actor : Project.service().list( Actor.class ) )
            names.add( actor.getName() );
/*
        for ( ResourceSpec rs : Project.service().findAllResourceSpecs() ) {
            if ( !rs.isAnyActor() ) names.add( rs.getActorName() );
        }
*/
        return names;
    }

    private Set<String> findAllRoleNames() {
        Set<String> names = new HashSet<String>();
        for ( Role actor : Project.service().list( Role.class ) )
            names.add( actor.getName() );
        return names;
    }

    private Set<String> findAllOrganizationNames() {
        Set<String> names = new HashSet<String>();
        for ( Organization actor : Project.service().list( Organization.class ) )
            names.add( actor.getName() );
        return names;
    }

    private Set<String> findAllPaceNames() {
        Set<String> names = new HashSet<String>();
        for ( Place actor : Project.service().list( Place.class ) )
            names.add( actor.getName() );
        return names;
    }

    private Set<String> findAllTaskNames() {
        Set<String> names = new HashSet<String>();
        Iterator<Part> allParts = part.getScenario().parts();
        while ( allParts.hasNext() ) {
            names.add( allParts.next().getTask() );
        }
        return names;
    }

    private ModelObjectLink makeLink( String id,
                                      final IModel<ModelObject> model ) {
        return new ModelObjectLink( id, new Model<ModelObject>( model.getObject() ) );
    }

    private void addField( final String property, final Collection<String> choices ) {
        final TextField<String> field;
        if ( choices == null ) {
            field = new TextField<String>( property );
        } else {
            field = new AutoCompleteTextField<String>( property ) {
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
                addIssues( field, part, property );
                target.addComponent( field );
                target.addComponent( ( (ScenarioPage) getPage() ).getGraph() );
                target.addComponent( ( (ScenarioPage) getPage() ).getNodeTitle() );
                updateWith( target );
            }
        } );

        // Add style mods from scenario analyst.
        addIssues( field, part, property );
        add( field );

    }

    private void addField( final String property ) {
        addField( property, null );
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
                            "class", true, new Model<String>( "error" ) ) );              // NON-NLS
            component.add(
                    new AttributeModifier(
                            "title", true, new Model<String>( issue ) ) );                // NON-NLS
        }
    }


    private void addTimingFields() {
        final DelayPanel repeatsEveryPanel = new DelayPanel(
                "repeats-every",
                new PropertyModel<Delay>( part, "repeatsEvery" ) );
        repeatsEveryPanel.setOutputMarkupId( true );
        repeatsEveryPanel.enable( part.isRepeating() );
        add( repeatsEveryPanel );
        final DelayPanel completionTimePanel = new DelayPanel(
                "completion-time",
                new PropertyModel<Delay>( part, "completionTime" ) );
        completionTimePanel.enable( part.isSelfTerminating() );
        completionTimePanel.setOutputMarkupId( true );
        add( completionTimePanel );
        CheckBox selfTerminatingCheckBox = new CheckBox(
                "self-terminating",
                new PropertyModel<Boolean>( part, "selfTerminating" ) );
        selfTerminatingCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                completionTimePanel.enable( part.isSelfTerminating() );
                target.addComponent( completionTimePanel );
                updateWith( target );
            }
        } );
        add( selfTerminatingCheckBox );
        CheckBox repeatingCheckBox = new CheckBox(
                "repeating",
                new PropertyModel<Boolean>( part, "repeating" ) );
        add( repeatingCheckBox );
        repeatingCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                repeatsEveryPanel.enable( part.isRepeating() );
                target.addComponent( repeatsEveryPanel );
                target.addComponent( ( (ScenarioPage) getPage() ).getGraph() );
                updateWith( target );
            }
        } );
    }

    /**
     * Test if strings are equivalent.
     *
     * @param name   the new name
     * @param target the original name
     * @return true if strings are equivalent
     */
    private static boolean isSame( String name, String target ) {
        return COMPARATOR.compare( name, target ) == 0;
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
        if ( name == null || name.trim().isEmpty() )
            getPart().setActor( null );
        else {
            final Actor actor = getPart().getActor();
            if ( actor == null || !isSame( name, actor.getName() ) )
                getPart().setActor( Project.service().findOrCreate( Actor.class, name ) );
        }
    }

    /**
     * Set the part's jurisdiction.
     *
     * @param name if null or empty, set to null; otherwise, only set if different.
     */
    public void setJurisdiction( String name ) {
        if ( name == null || name.trim().isEmpty() )
            getPart().setJurisdiction( null );
        else {
            final Place jurisdiction = getPart().getJurisdiction();
            if ( jurisdiction == null || !isSame( name, jurisdiction.getName() ) )
                getPart().setJurisdiction( Project.service().findOrCreate( Place.class, name ) );
        }
    }

    /**
     * Set the part's location.
     *
     * @param name if null or empty, set to null; otherwise, only set if different.
     */
    public void setLocation( String name ) {
        if ( name == null || name.trim().isEmpty() )
            getPart().setLocation( null );
        else {
            final Place location = getPart().getLocation();
            if ( location == null || !isSame( name, location.getName() ) )
                getPart().setLocation( new Place( name ) );
        }
    }

    /**
     * Set the part's organization.
     *
     * @param name if null or empty, set to null; otherwise, only set if different.
     */
    public void setOrganization( String name ) {
        if ( name == null || name.trim().isEmpty() )
            getPart().setOrganization( null );
        else {
            final Organization organization = getPart().getOrganization();
            if ( organization == null || !isSame( name, organization.getName() ) )
                getPart().setOrganization(
                        Project.service().findOrCreate(
                                Organization.class, name ) );
        }
    }

    /**
     * Set the part's role.
     *
     * @param name if null or empty, set to null; otherwise, only set if different.
     */
    public void setRole( String name ) {
        if ( name == null || name.trim().isEmpty() )
            getPart().setRole( null );
        else {
            final Role role = getPart().getRole();
            if ( role == null || !isSame( name, role.getName() ) )
                getPart().setRole( Project.service().findOrCreate( Role.class, name ) );
        }
    }

    /**
     * Set the part's task.
     *
     * @param task the task
     */
    public void setTask( String task ) {
        getPart().setTask( task );
    }

    public final Part getPart() {
        return part;
    }

    public final void setPart( Part part ) {
        this.part = part;
    }

    /**
     * {@inheritDoc}
     */
    public void update( AjaxRequestTarget target ) {
        target.addComponent( ( (ScenarioPage) getPage() ).getGraph() );
        updateWith( target );
    }
}
