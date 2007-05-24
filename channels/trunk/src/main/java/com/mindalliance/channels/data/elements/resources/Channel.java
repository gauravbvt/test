/*
 * Created on Apr 30, 2007
 */
package com.mindalliance.channels.data.elements.resources;

import java.util.List;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.data.reference.TypeSet;
import com.mindalliance.channels.data.support.Latency;
import com.mindalliance.channels.data.support.Level;
import com.mindalliance.channels.util.CollectionType;
import com.mindalliance.channels.util.GUID;

/**
 * A communication medium with a security level, restrictions as to
 * formats it can transmit, and possibly interoperable with other
 * system. A Channel has a latency.
 * 
 * @author jf
 */
public class Channel extends AbstractResource {

    private Level security; // LOW, MEDIUM or HIGH
    private Level reliability;
    private List<Channel> interoperables; // information can travel
                                            // from this one to the
                                            // other
    private TypeSet supportedFormats;
    private Latency latency;

    public Channel() {
        super();
    }

    public Channel( GUID guid ) {
        super( guid );
    }

    /**
     * @return the interoperables
     */
    @CollectionType(type=Channel.class)
    public List<Channel> getInteroperables() {
        return interoperables;
    }

    /**
     * @param interoperables the interoperables to set
     */
    public void setInteroperables( List<Channel> interoperables ) {
        this.interoperables = interoperables;
    }

    /**
     * @return the latency
     */
    public Latency getLatency() {
        return latency;
    }

    /**
     * @param latency the latency to set
     */
    public void setLatency( Latency latency ) {
        this.latency = latency;
    }

    /**
     * @return the reliability
     */
    public Level getReliability() {
        return reliability;
    }

    /**
     * @param reliability the reliability to set
     */
    public void setReliability( Level reliability ) {
        this.reliability = reliability;
    }

    /**
     * @return the security
     */
    public Level getSecurity() {
        return security;
    }

    /**
     * @param security the security to set
     */
    public void setSecurity( Level security ) {
        this.security = security;
    }

    /**
     * @return the supportedFormats
     */
    @PropertyOptions(ignore=true)
    public TypeSet getSupportedFormats() {
        return supportedFormats;
    }

    /**
     * @param supportedFormats the supportedFormats to set
     */
    public void setSupportedFormats( TypeSet supportedFormats ) {
        this.supportedFormats = supportedFormats;
    }

}
