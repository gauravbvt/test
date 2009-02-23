package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Someone or something playing a part in a scenario.
 */
@Entity
public class Actor extends ModelObject {

    /** Bogus actor used to signify that the actor is not known... */
    public static final Actor UNKNOWN = new Actor( "(unknown)" );
    /**
     * The title of the actor.
     * TODO: quickie -- assumes that an actor has at most one job title in at most one organization
     */
    private String jobTitle = "";

    public Actor() {
    }

    /**
     * Utility constructor for tests.
     *
     * @param name the name of the new object
     */
    public Actor( String name ) {
        super( name );
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle( String jobTitle ) {
        this.jobTitle = jobTitle;
    }

    /**
     * Find or create an actor by name
     *
     * @param name String a given name
     * @return a new or existing actor, or null is name is null or empty
     */
    public static Actor named( String name ) {
        if ( name == null || name.isEmpty() ) return null;
        return Project.service().findOrCreate( Actor.class, name );
    }

    /**
     * {@inheritDoc}
     */
    @Override @Transient
    public boolean isEntity() {
        return true;
    }


}
