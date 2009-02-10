package com.mindalliance.channels;

import java.util.Iterator;
import java.util.List;
import java.io.Serializable;

/**
 * A communication channel
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 30, 2009
 * Time: 2:06:19 PM
 */
public class Channel implements Serializable {
    /**
     * The medium of communication
     */
    private Medium medium;

    /**
     * The address
     */
    private String address = "";

    public Channel() {
    }

    public Channel( Medium medium, String address ) {
        this.medium = medium;
        this.address = address == null ? "" : address;
    }

    public Medium getMedium() {
        return medium;
    }

    public void setMedium( Medium medium ) {
        this.medium = medium;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address == null ? "" : address;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj ) {
        if (obj instanceof Channel) {
            Channel channel = (Channel) obj;
            return address.equals( channel.getAddress() ) && medium == channel.getMedium();
        }
        else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = 1;
        if ( medium != null ) hash = hash * 31 + medium.hashCode();
        if ( address != null ) hash = hash * 31 + address.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( medium == null ? "NO MEDIUM" : medium.getName() );
        sb.append( ": " );
        sb.append( address.isEmpty() ? "UNKNOWN" : address );
        return sb.toString();
    }

    /**
     * Get string collating channels
     *
     * @param channels a set of channels
     * @return channels as string
     */
    public static String toString( List<Channel> channels ) {
        if ( channels.isEmpty() ) {
            return "No channel";
        } else {
            StringBuilder sb = new StringBuilder();
            Iterator<Channel> iter = channels.iterator();
            while ( iter.hasNext() ) {
                Channel channel = iter.next();
                sb.append( channel.toString() );
                if ( iter.hasNext() ) sb.append( ", " );
            }
            return sb.toString();
        }
    }

    /**
     * Tests if address is valid for the medium
     *
     * @return a boolean
     */
    public boolean isValid() {
        return medium != null && medium.isAddressValid( address );
    }

}
