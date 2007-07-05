/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.components;

import com.mindalliance.channels.data.elements.resources.Channel;
import com.mindalliance.channels.data.support.Availability;
import com.mindalliance.channels.data.support.Level;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * How to contact someone or something: end point (address etc.),
 * channel, privacy level and availability.
 * 
 * @author jf
 */
public class ContactInfo extends AbstractJavaBean {

    private Channel channel;
    private String endPoint;
    private Level privacy;
    private Availability availability;

    public ContactInfo() {
    };

    /**
     * @return the availability
     */
    public Availability getAvailability() {
        return availability;
    }

    /**
     * @param availability the availability to set
     */
    public void setAvailability( Availability availability ) {
        this.availability = availability;
    }

    /**
     * @return the channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * @param channel the channel to set
     */
    public void setChannel( Channel channel ) {
        this.channel = channel;
    }

    /**
     * @return the endPoint
     */
    public String getEndPoint() {
        return endPoint;
    }

    /**
     * @param endPoint the endPoint to set
     */
    public void setEndPoint( String endPoint ) {
        this.endPoint = endPoint;
    }

    /**
     * @return the privacy
     */
    public Level getPrivacy() {
        return privacy;
    }

    /**
     * @param privacy the privacy to set
     */
    public void setPrivacy( Level privacy ) {
        this.privacy = privacy;
    }
}
