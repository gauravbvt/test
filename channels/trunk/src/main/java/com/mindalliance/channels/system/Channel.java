// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.reference.ChannelType;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * A communication medium over which information can be transmitted.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 * @opt useimports
 * @assoc * - * Channel
 */
public class Channel extends AbstractJavaBean {

    /**
     * Channel security classification.
     */
    public enum Security { None, Low, Medium, High }

    /**
     * Channel privacy types.
     */
    public enum Privacy  { Private, Public }

    /**
     * Channel reliability type.
     */
    public enum Reliability { Low, Medium, High }

    private ChannelType type;
    private String description;
    private Security security;
    private Privacy privacy;
    private Reliability reliability;
    private List<Channel> interoperatibleWith = new ArrayList<Channel>();

    /**
     * Default constructor.
     */
    public Channel() {
        super();
    }

    /**
     * Return the value of interoperatibleWith.
     */
    public List<Channel> getInteroperatibleWith() {
        return this.interoperatibleWith;
    }

    /**
     * Set the value of interoperatibleWith.
     * @param interoperatibleWith The new value of interoperatibleWith
     */
    public void setInteroperatibleWith( List<Channel> interoperatibleWith ) {
        this.interoperatibleWith = interoperatibleWith;
    }

    /**
     * Return the value of privacy.
     */
    public Privacy getPrivacy() {
        return this.privacy;
    }

    /**
     * Set the value of privacy.
     * @param privacy The new value of privacy
     */
    public void setPrivacy( Privacy privacy ) {
        this.privacy = privacy;
    }

    /**
     * Return the value of reliability.
     */
    public Reliability getReliability() {
        return this.reliability;
    }

    /**
     * Set the value of reliability.
     * @param reliability The new value of reliability
     */
    public void setReliability( Reliability reliability ) {
        this.reliability = reliability;
    }

    /**
     * Return the value of security.
     */
    public Security getSecurity() {
        return this.security;
    }

    /**
     * Set the value of security.
     * @param security The new value of security
     */
    public void setSecurity( Security security ) {
        this.security = security;
    }

    /**
     * Return the value of description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the value of description.
     * @param description The new value of description
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    /**
     * Return the value of type.
     */
    public ChannelType getType() {
        return this.type;
    }

    /**
     * Set the value of type.
     * @param type The new value of type
     */
    public void setType( ChannelType type ) {
        this.type = type;
    }
}
