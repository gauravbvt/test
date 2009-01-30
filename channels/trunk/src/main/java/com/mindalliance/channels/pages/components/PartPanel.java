package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.ModelObjectLink;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.IModel;

import java.text.Collator;

/**
 * A view on a Part.
 */
public class PartPanel extends Panel {

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

        addField( TASK_PROPERTY );
        addField( ACTOR_PROPERTY );
        add( makeLink( "actor-link",                                           // NON-NLS
                new PropertyModel<ModelObject>( part, ACTOR_PROPERTY ) ) );
        addField( ROLE_PROPERTY );
        add( makeLink( "role-link",                                            // NON-NLS
                new PropertyModel<ModelObject>( part, ROLE_PROPERTY ) ) );
        addField( ORG_PROPERTY );
        add( makeLink( "org-link",                                             // NON-NLS
                new PropertyModel<ModelObject>( part, ORG_PROPERTY ) ) );
        addField( JURISDICTION_PROPERTY );
        add( makeLink( "juris-link",                                           // NON-NLS
                new PropertyModel<ModelObject>( part, JURISDICTION_PROPERTY ) ) );
        addField( LOCATION_PROPERTY );
        add( makeLink( "loc-link",                                             // NON-NLS
                new PropertyModel<ModelObject>( part, LOCATION_PROPERTY ) ) );
    }

    private ModelObjectLink makeLink( String id,
                                         final IModel<ModelObject> model ) {
        return new ModelObjectLink( id, new Model<ModelObject> (model.getObject()) );
    }

    private void addField( String property ) {
        final TextField<String> field = new TextField<String>( property );

        // Add style mods from scenario analyst.
        final Analyst analyst = ( (Project) getApplication() ).getAnalyst();
        final String issue = analyst.getIssuesSummary( getPart(), property );
        if ( !issue.isEmpty() ) {
            field.add(
                    new AttributeModifier( "class", true,
                            new Model<String>( "error" ) ) );   // NON-NLS
            field.add(
                    new AttributeModifier( "title", true,
                            new Model<String>( issue ) ) );     // NON-NLS
        }
        add( field );
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
                getPart().setActor( Actor.named( name ) );
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
                getPart().setJurisdiction( Place.named( name ) );
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
                getPart().setOrganization( Organization.named( name ) );
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
                getPart().setRole( Role.named( name ) );
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
}
