// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.models;

import com.mindalliance.channels.definitions.Information;
import com.mindalliance.channels.support.Duration;
import com.mindalliance.channels.support.GUID;
import com.mindalliance.channels.support.Level;

/**
 * Assertion made about someone needing information, possibly about a
 * scenario element, to be delivered via notification or by responding
 * to a request.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class NeedsToKnow extends Assertion {

    /**
     * The type of delivery for a piece of information.
     */
    enum Delivery {
        /** A notification is desired from the source of information. */
        NOTIFICATION,

        /** Information is deliver upon request. */
        REQUEST_RESPONSE
    };

    private Delivery delivery;
    private Information information;
    private ScenarioElement subject;
    private Level criticality;
    private Duration window;

    /**
     * Default constructor.
     */
    public NeedsToKnow() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public NeedsToKnow( GUID guid ) {
        super( guid );
    }

    /**
     * Return the criticality (how critical is this information).
     */
    public Level getCriticality() {
        return criticality;
    }

    /**
     * Set the criticality.
     * @param criticality the criticality
     */
    public void setCriticality( Level criticality ) {
        this.criticality = criticality;
    }

    /**
     * Return the information.
     */
    public Information getInformation() {
        return information;
    }

    /**
     * Set the information.
     * @param information the information to set
     */
    public void setInformation( Information information ) {
        this.information = information;
    }

    /**
     * Return the delivery method.
     */
    public Delivery getDelivery() {
        return delivery;
    }

    /**
     * Set the delivery method.
     * @param delivery the delivery method
     */
    public void setDelivery( Delivery delivery ) {
        this.delivery = delivery;
    }

    /**
     * Return the subject, if any (situational awareness need).
     */
    public ScenarioElement getSubject() {
        return subject;
    }

    /**
     * Set the subject.
     * @param subject the subject
     */
    public void setSubject( ScenarioElement subject ) {
        this.subject = subject;
    }

    /**
     * Return the window after which knowing is of no value
     * (if an agent needs to know, the window should not exceed the
     * duration of the task).
     */
    public Duration getWindow() {
        return window;
    }

    /**
     * Set the window.
     * @param window the window to set
     */
    public void setWindow( Duration window ) {
        this.window = window;
    }

}
