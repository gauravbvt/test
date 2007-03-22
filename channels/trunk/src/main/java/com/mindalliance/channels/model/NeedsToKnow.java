// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.reference.FormatType;
import com.mindalliance.channels.system.MetaInformation;
import com.mindalliance.channels.util.Duration;

/**
 * The specification of a need for information, more or less critical
 * and urgent, that is to be delivered  in one of a number of acceptable
 * formats, either upon request of via notification.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 */
public class NeedsToKnow extends Assertion {

    /**
     * How the information should be delivered.
     */
    public enum Delivery { notification, onRequest }

    /**
     * How much this information is needed.
     */
    public enum Criticality { low, medium, high }

    private MetaInformation information;
    private Delivery delivery;
    private Criticality criticality;
    private Duration window;
    private List<FormatType> formats = new ArrayList<FormatType>();

    /**
     * Default constructor.
     */
    NeedsToKnow() {
        super();
    }

    /**
     * Convenience constructor.
     * @param scenario the scenario
     */
    public NeedsToKnow( Scenario scenario ) {
        super( scenario );
    }

    /**
     * Return the value of criticality.
     */
    public Criticality getCriticality() {
        return this.criticality;
    }

    /**
     * Set the value of criticality.
     * @param criticality The new value of criticality
     */
    public void setCriticality( Criticality criticality ) {
        this.criticality = criticality;
    }

    /**
     * Return the value of delivery.
     */
    public Delivery getDelivery() {
        return this.delivery;
    }

    /**
     * Set the value of delivery.
     * @param delivery The new value of delivery
     */
    public void setDelivery( Delivery delivery ) {
        this.delivery = delivery;
    }

    /**
     * Return the value of formats.
     */
    public List<FormatType> getFormats() {
        return this.formats;
    }

    /**
     * Set the value of formats.
     * @param formats The new value of formats
     */
    public void setFormats( List<FormatType> formats ) {
        this.formats = formats;
    }

    /**
     * Add a format.
     * @param format the format
     */
    public void addFormat( FormatType format ) {
        this.formats.add( format );
    }

    /**
     * Remove a format.
     * @param format the format
     */
    public void removeFormat( FormatType format ) {
        this.formats.remove( format );
    }

    /**
     * Return the value of information.
     */
    public MetaInformation getInformation() {
        return this.information;
    }

    /**
     * Set the value of information.
     * @param information The new value of information
     */
    public void setInformation( MetaInformation information ) {
        this.information = information;
    }

    /**
     * Return the value of maximumDelay.
     */
    public Duration getWindow() {
        return this.window;
    }

    /**
     * Set the value of maximumDelay.
     * @param maximumDelay The new value of maximumDelay
     */
    public void setWindow( Duration maximumDelay ) {
        this.window = maximumDelay;
    }
}
