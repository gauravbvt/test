/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.elements.assertions;

import com.mindalliance.channels.data.Needy;
import com.mindalliance.channels.data.ScenarioElement;
import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.support.Level;
import com.mindalliance.channels.data.support.Duration;
import com.mindalliance.channels.util.GUID;

/**
 * Assertion made about someone needing information, possibly about a
 * scenario element, to be delivered via notification or by responding
 * to a request.
 * 
 * @author jf
 */
public class NeedsToKnow extends Assertion {

    enum Delivery {
        NOTIFICATION, REQUEST_RESPONSE
    };

    private Delivery delivery; // the delivery method
    private Information information; // what information
    private ScenarioElement subject; // about what scenario element
                                        // if any (situational
                                        // awareness need)
    private Level criticality; // How critical is this information
    private Duration window; // After which knowing is of no value
                                // (if an agent needs to know, the
                                // window should not exceed the
                                // duration of the task)

    public NeedsToKnow() {
        super();
    }

    public NeedsToKnow( GUID guid ) {
        super( guid );
    }

    public Needy getNeedy() {
        return (Needy) getAbout();
    }

    /**
     * @return the criticality
     */
    public Level getCriticality() {
        return criticality;
    }

    /**
     * @param criticality the criticality to set
     */
    public void setCriticality( Level criticality ) {
        this.criticality = criticality;
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
     * @return the delivery method
     */
    public Delivery getDelivery() {
        return delivery;
    }

    /**
     * @param method the delivery method to set
     */
    public void setDelivery( Delivery delivery ) {
        this.delivery = delivery;
    }

    /**
     * @return the subject
     */
    public ScenarioElement getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject( ScenarioElement subject ) {
        this.subject = subject;
    }

    /**
     * @return the window
     */
    public Duration getWindow() {
        return window;
    }

    /**
     * @param window the window to set
     */
    public void setWindow( Duration window ) {
        this.window = window;
    }

}
