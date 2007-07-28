// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.profiles;

import com.mindalliance.channels.data.support.AbstractJavaBean;
import com.mindalliance.channels.data.support.Availability;
import com.mindalliance.channels.data.support.Level;

/**
 * How to contact someone or something: end point (address etc.),
 * channel, privacy level and availability.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 * @composed - - 1 Channel
 */
public class ContactInfo extends AbstractJavaBean {

    private Channel channel;
    private String endPoint;
    private Level privacy;
    private Availability availability;

    /**
     * Default constructor.
     */
    public ContactInfo() {
    }

    /**
     * Return the availability.
     */
    public Availability getAvailability() {
        return availability;
    }

    /**
     * Set the availability.
     * @param availability the availability
     */
    public void setAvailability( Availability availability ) {
        this.availability = availability;
    }

    /**
     * Return the channel.
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Set the channel.
     * @param channel the channel
     */
    public void setChannel( Channel channel ) {
        this.channel = channel;
    }

    /**
     * Return the endPoint.
     */
    public String getEndPoint() {
        return endPoint;
    }

    /**
     * Set the endPoint.
     * @param endPoint the endPoint
     */
    public void setEndPoint( String endPoint ) {
        this.endPoint = endPoint;
    }

    /**
     * Return the privacy.
     */
    public Level getPrivacy() {
        return privacy;
    }

    /**
     * Set the privacy.
     * @param privacy the privacy
     */
    public void setPrivacy( Level privacy ) {
        this.privacy = privacy;
    }
}
