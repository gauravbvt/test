/*
 * Created on May 2, 2007
 */
package com.mindalliance.channels.data.elements.assertions;

import com.mindalliance.channels.data.Actor;
import com.mindalliance.channels.data.Knowable;
import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.util.GUID;

/**
 * Assertion made about some scenario element known of by someone.
 * 
 * @author jf
 */
public class Known extends Assertion {

    private Information information; // what's known
    private Actor actor; // know by whom: an agent ("whoever does
                            // this task knows this"), role ("anyone
                            // in this role knows this") or team who
                            // knows this

    public Known() {
        super();
    }

    public Known( GUID guid ) {
        super( guid );
    }

    public Knowable getKnowable() {
        return (Knowable) getAbout();
    }

    /**
     * @return the actor
     */
    public Actor getActor() {
        return actor;
    }

    /**
     * @param actor the actor to set
     */
    public void setActor( Actor actor ) {
        this.actor = actor;
    }

    /**
     * @return the information
     */
    public Information getInformation() {
        return information;
    }

    /**
     * @param information the information to set
     */
    public void setInformation( Information information ) {
        this.information = information;
    }

}
