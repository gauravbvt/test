/*
 * Created on May 2, 2007
 */
package com.mindalliance.channels.data.elements.assertions;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.data.Knowable;
import com.mindalliance.channels.data.components.Knowledgeable;
import com.mindalliance.channels.data.reference.Information;
import com.mindalliance.channels.util.GUID;

/**
 * Assertion made about some scenario element known of by someone.
 * 
 * @author jf
 */
public class Known extends Assertion {

    private Information information; // what's known
    private Knowledgeable knower; // know by whom: an agent ("whoever does
                            // this task knows this"), role ("anyone
                            // in this role knows this") or team who
                            // knows this

    public Known() {
        super();
    }

    public Known( GUID guid ) {
        super( guid );
    }

    @PropertyOptions(ignore=true)
    public Knowable getKnowable() {
        return (Knowable) getAbout();
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

    
    /**
     * Return the value of knower.
     */
    public Knowledgeable getKnower() {
        return knower;
    }

    
    /**
     * Set the value of knower.
     * @param knower The new value of knower
     */
    public void setKnower( Knowledgeable knower ) {
        this.knower = knower;
    }

}
