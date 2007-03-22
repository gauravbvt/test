// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import com.mindalliance.channels.system.Channel;
import com.mindalliance.channels.system.InformationAsset;
import com.mindalliance.channels.system.Role;

/**
 * The direct transfer of information for purpose of notification
 * or information request between one information resource or agent
 * and another over a shared, possibly composite, channel.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 * @navassoc - sender   1 Role
 * @navassoc - receiver 1 Role
 */
public class Communication extends Occurence {

    /**
     * The purpose of a communication.
     */
    public enum Purpose { Request, Notification };

    private Purpose purpose;
    private Role sender;
    private Role receiver;
    private Channel channel;
    private InformationAsset what;

    /**
     * Default constructor.
     */
    Communication() {
    }

    /**
     * Default constructor.
     * @param scenario the scenario
     */
    public Communication( Scenario scenario ) {
        super( scenario );
    }

    /**
     * Return the value of channel.
     */
    public Channel getChannel() {
        return this.channel;
    }

    /**
     * Set the value of channel.
     * @param channel The new value of channel
     */
    public void setChannel( Channel channel ) {
        this.channel = channel;
    }

    /**
     * Return the value of purpose.
     */
    public Purpose getPurpose() {
        return this.purpose;
    }

    /**
     * Set the value of purpose.
     * @param purpose The new value of purpose
     */
    public void setPurpose( Purpose purpose ) {
        this.purpose = purpose;
    }

    /**
     * Return the value of receiver.
     */
    public Role getReceiver() {
        return this.receiver;
    }

    /**
     * Set the value of receiver.
     * @param receiver The new value of receiver
     */
    public void setReceiver( Role receiver ) {
        this.receiver = receiver;
    }

    /**
     * Return the value of sender.
     */
    public Role getSender() {
        return this.sender;
    }

    /**
     * Set the value of sender.
     * @param sender The new value of sender
     */
    public void setSender( Role sender ) {
        this.sender = sender;
    }

    /**
     * Return the value of what.
     */
    public InformationAsset getWhat() {
        return this.what;
    }

    /**
     * Set the value of what.
     * @param what The new value of what
     */
    public void setWhat( InformationAsset what ) {
        this.what = what;
    }
}
