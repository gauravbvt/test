// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.GUID;

/**
 * A communication medium over which information can be transmitted.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Channel extends AbstractNamedObject {

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

    private String kind;
    private Security security;
    private Privacy privacy;
    private Reliability reliability;
    private List<Channel> interoperatibleWith = new ArrayList<Channel>();

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    Channel( GUID guid ) {
        super( guid );
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
     * Return the value of kind.
     */
    public String getKind() {
        return this.kind;
    }

    /**
     * Set the value of kind.
     * @param kind The new value of kind
     */
    public void setKind( String kind ) {
        this.kind = kind;
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
}
