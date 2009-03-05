package com.mindalliance.channels;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
/**
 * A communication channel
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 30, 2009
 * Time: 2:06:19 PM
 */
@Entity
public class Channel implements Serializable {

    /** Bogus channels for reports. */
    public static final Channel Unknown = new Channel( null, "(unknown channel)" );

    /**
     * The medium of communication
     */
    private Medium medium;

    /**
     * The address
     */
    private String address = "";

    /** An identifier for persistence. */
    private long id;

    public Channel() {
    }

    public Channel(Channel channel) {
        medium = channel.getMedium();
        address = channel.getAddress();
    }

    public Channel( Medium medium, String address ) {
        this.medium = medium;
        this.address = address == null ? "" : address;
    }

    @Enumerated( value = EnumType.STRING )
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

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    /**
     * Compares channels for equality.
     * @param obj the object
     * @return if the same
     */
    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof Channel ) {
            Channel channel = (Channel) obj;
            if (medium == null || channel.getMedium() == null)
                return false;
            else
                return address.equals( channel.getAddress() ) && medium == channel.getMedium();
        }
        else {
            return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int hash = 1;
        if ( medium != null ) hash = hash * 31 + medium.hashCode();
        if ( address != null ) hash = hash * 31 + address.hashCode();
        return hash;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( medium == null ? "NO MEDIUM" : medium.getName() );
        sb.append( ": " );
        sb.append( address.isEmpty() ? "?" : address );
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
    @Transient
    public boolean isValid() {
        return medium != null && medium.isAddressValid( address );
    }
}
