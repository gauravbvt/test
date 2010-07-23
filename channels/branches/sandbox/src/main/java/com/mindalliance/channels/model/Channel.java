package com.mindalliance.channels.model;

import java.io.Serializable;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

/**
 * A communication channel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 30, 2009
 * Time: 2:06:19 PM
 */
public class Channel implements Serializable, Comparable<Channel> {

    /**
     * Bogus channels for reports.
     */
    public static final Channel Unknown = new Channel( TransmissionMedium.UNKNOWN, "(unknown channel)" );
    /**
     * Collator.
     */
    static private Collator collator = Collator.getInstance();

    /**
     * The medium of communication
     */
    private TransmissionMedium medium;

    /**
     * The address
     */
    private String address = "";


    public Channel() {
    }

    public Channel( Channel channel ) {
        medium = channel.getMedium();
        address = channel.getAddress();
    }

    public Channel( TransmissionMedium medium, String address ) {
        this.medium = medium;
        this.address = address == null ? "" : address;
    }

    public Channel( TransmissionMedium medium ) {
        this( medium, "" );
    }

    public TransmissionMedium getMedium() {
        return medium;
    }

    public void setMedium( TransmissionMedium medium ) {
        assert medium.isType();
        this.medium = medium;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address == null ? "" : address;
    }

    /**
     * Compares channels for equality.
     *
     * @param obj the object
     * @return if the same
     */
    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof Channel ) {
            Channel channel = (Channel) obj;
            return medium != null && channel.getMedium() != null
                    && address.equals( channel.getAddress() ) && medium.equals( channel.getMedium() );
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 1;
        if ( medium != null ) hash = hash * 31 + medium.hashCode();
        if ( address != null ) hash = hash * 31 + address.hashCode();
        return hash;
    }


    @Override
    public String toString() {
        String label = medium == null ? "Unspecified medium" : medium.getLabel();

        return address.isEmpty()
                ? MessageFormat.format( "via {0}", label )
                : MessageFormat.format( "{0}: {1}", label, address );
/*
                ? Medium.F2F.equals( medium )
                    ? label
                    : MessageFormat.format( "via {0}", label )
                : Medium.Other.equals( medium )
                    ? MessageFormat.format( "via {0}", getAddress() )
                    : MessageFormat.format( "{0}: {1}", label, address );
*/
    }

    public String getLabel() {
        String label = medium == null ? "Unspecified medium" : medium.getLabel();
        return address.isEmpty()
                ? MessageFormat.format( "{0}", label )
                : MessageFormat.format( "{0}: {1}", label, address );
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
                sb.append( channel.getLabel() );
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
        return medium != null && medium.isAddressValidIfSet( address );
    }

    /**
     * Test is address is valid.
     *
     * @return a boolean
     */
    public boolean hasValidAddress() {
        return medium != null && medium.isAddressValid( address );
    }

    /**
     * Is the medium unicast?
     *
     * @return a boolean
     */
    public boolean isUnicast() {
        return getMedium() != null && getMedium().isUnicast();
    }

    /**
     * Is the medium multicast?
     *
     * @return a boolean
     */
    public boolean isMulticast() {
        return getMedium() != null && getMedium().isMulticast();
    }

    /**
     * Is the medium broadcast?
     *
     * @return a boolean
     */
    public boolean isBroadcast() {
        return getMedium().isBroadcast();
    }

    /**
     * Whether the channel requires an address to be fully defined.
     *
     * @return a boolean
     */
    public boolean requiresAddress() {
        return medium.requiresAddress();
    }

    public int compareTo( Channel o ) {
        if ( medium == null && o.getMedium() == null ) return 0;
        if ( o.getMedium() == null ) return -1;
        if ( medium == null ) return 1;
        int comp = collator.compare( medium.getName(), o.getMedium().getName() );
        if ( comp == 0 ) {
            return collator.compare( address, o.getAddress() );
        } else {
            return comp;
        }
    }

    /**
     * Whether this channel references a given model object.
     *
     * @param mo a model object
     * @return a boolean
     */
    public boolean references( ModelObject mo ) {
        if ( mo instanceof TransmissionMedium ) {
            return ModelObject.areIdentical( mo, medium );
        } else {
            return false;
        }
    }

    public boolean isSecuredFor( List<Classification> classifications ) {
        return medium != null
                && ( medium.isDirect()
                || Classification.hasHigherOrEqualClassification( medium.getSecurity(), classifications ) );
    }

    /**
     * Whether the channel's medium is direct.
     *
     * @return a boolean
     */
    public boolean isDirect() {
        return medium != null && medium.isDirect();
    }

    /**
     * Merge two channels.
     *
     * @param channel a channels
     * @param other   a channels
     * @return a channels
     */
    public static Channel merge( Channel channel, Channel other ) {
        Channel merged = new Channel();
        merged.setMedium( channel.getMedium() );
        merged.setAddress(
                !channel.getAddress().isEmpty()
                        ? channel.getAddress()
                        : other.getAddress()
        );
        return merged;
    }
}
