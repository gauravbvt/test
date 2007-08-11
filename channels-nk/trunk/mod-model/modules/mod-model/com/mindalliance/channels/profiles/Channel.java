// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.profiles;

import java.util.ArrayList;
import java.util.List;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.definitions.CategorySet;
import com.mindalliance.channels.definitions.Category.Taxonomy;
import com.mindalliance.channels.support.CollectionType;
import com.mindalliance.channels.support.GUID;
import com.mindalliance.channels.support.Latency;
import com.mindalliance.channels.support.Level;

/**
 * A communication medium with a security level, restrictions as to
 * formats it can transmit, and possibly interoperable with other
 * system. A Channel has a latency.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 * @navassoc - - * Channel
 */
public class Channel extends Resource {

    private Level security;
    private Level reliability;
    private List<Channel> interoperables = new ArrayList<Channel>();
    private CategorySet supportedFormats = new CategorySet( Taxonomy.Format );
    private Latency latency = new Latency();

    /**
     * Default constructor.
     */
    public Channel() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Channel( GUID guid ) {
        super( guid, Taxonomy.Any );
    }

    /**
     * Return the interoperables channels. Information can travel
     * from this channel to the others.
     */
    @CollectionType( type = Channel.class )
    public List<Channel> getInteroperables() {
        return interoperables;
    }

    /**
     * Set the interoperable channels.
     * @param interoperables the interoperables to set
     */
    public void setInteroperables( List<Channel> interoperables ) {
        this.interoperables = interoperables;
    }

    /**
     * Return the latency.
     */
    public Latency getLatency() {
        return latency;
    }

    /**
     * Set the latency.
     * @param latency the latency
     */
    public void setLatency( Latency latency ) {
        this.latency = latency;
    }

    /**
     * Return the reliability.
     */
    public Level getReliability() {
        return reliability;
    }

    /**
     * Set the reliability.
     * @param reliability the reliability to set
     */
    public void setReliability( Level reliability ) {
        this.reliability = reliability;
    }

    /**
     * Return the security.
     */
    public Level getSecurity() {
        return security;
    }

    /**
     * Set the security.
     * @param security the security to set
     */
    public void setSecurity( Level security ) {
        this.security = security;
    }

    /**
     * Return the supported formats.
     */
    @PropertyOptions( ignore = true )
    public CategorySet getSupportedFormats() {
        return supportedFormats;
    }

    /**
     * Set the supported formats.
     * @param supportedFormats the supportedFormats to set
     */
    public void setSupportedFormats( CategorySet supportedFormats ) {
        this.supportedFormats = supportedFormats;
    }
}
