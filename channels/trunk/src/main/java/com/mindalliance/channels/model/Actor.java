package com.mindalliance.channels.model;

/**
 * Someone or something playing a part in a scenario.
 */
public class Actor extends ModelObject {

    /** True if actor is a system. A person otherwise. */
    private boolean system;

    public Actor() {
    }

    /**
     * Utility constructor for tests.
     * @param name the name of the new object
     */
    public Actor( String name ) {
        super( name );
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem( boolean system ) {
        this.system = system;
    }

}
