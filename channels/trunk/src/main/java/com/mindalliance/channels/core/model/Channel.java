package com.mindalliance.channels.core.model;

import java.io.Serializable;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
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

    private InfoFormat format;


    public Channel() {
    }

    public Channel( Channel channel ) {
        medium = channel.getMedium();
        address = channel.getAddress();
        format = channel.getFormat();
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
        return address == null ? "" : address;
    }

    public void setAddress( String address ) {
        this.address = address == null ? "" : address;
    }

    public InfoFormat getFormat() {
        return format;
    }

    public void setFormat( InfoFormat format ) {
        this.format = format;
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
                    && address.equals( channel.getAddress() ) && medium.equals( channel.getMedium() )
                    && ModelEntity.areEqualOrNull( channel.getFormat(), getFormat() );
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
        if ( format != null ) hash = hash * 31 + format.hashCode();
        return hash;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String label = medium == null ? "Unspecified medium" : medium.getLabel();

        sb.append( address.isEmpty()
                ? MessageFormat.format( "via {0}", label )
                : MessageFormat.format( "{0}: {1}", label, address ) );

        if ( format != null ) {
            sb.append( " as " ).append( format.getLabel() );
        }
        return sb.toString();
    }

    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        String label = medium == null ? "Unspecified medium" : medium.getLabel();
        sb.append( address.isEmpty()
                ? MessageFormat.format( "{0}", label )
                : MessageFormat.format( "{0}: {1}", label, address ) );
        if ( format != null ) {
            sb.append( " using " ).append( format.getLabel() );
        }
        return sb.toString();
    }


    /**
     * Get string collating channels
     *
     * @param channels a set of channels
     * @return channels as string
     */
    public static String toString( List<Channel> channels ) {
        return toString( channels, ", " );
    }

    /**
     * Get string collating channels
     *
     * @param channels a set of channels
     * @param sep      a string
     * @return channels as string
     */
    public static String toString( List<Channel> channels, String sep ) {
        if ( channels.isEmpty() ) {
            return "No channel";
        } else {
            StringBuilder sb = new StringBuilder();
            Iterator<Channel> iter = channels.iterator();
            while ( iter.hasNext() ) {
                Channel channel = iter.next();
                sb.append( channel.getLabel() );
                if ( iter.hasNext() ) sb.append( sep );
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

    public int compareTo( Channel other ) {
        if ( medium == null && other.getMedium() == null ) return 0;
        if ( other.getMedium() == null ) return -1;
        if ( medium == null ) return 1;
        int comp = collator.compare( medium.getName(), other.getMedium().getName() );
        if ( comp == 0 ) {
            comp = collator.compare( address, other.getAddress() );
        }
        if ( comp == 0 ) {
            comp = ( other.getFormat() == null )
                    ? -1
                    : ( format == null )
                        ? 1
                        : collator.compare( format.getName(), other.getFormat().getName() );
        }
        return comp;
    }

    /**
     * Whether this channel references a given model object.
     *
     * @param mo a model object
     * @return a boolean
     */
    public boolean references( ModelObject mo ) {
        return mo instanceof TransmissionMedium && ModelObject.areIdentical( mo, medium )
                || mo instanceof InfoFormat && ModelObject.areIdentical( mo, format );
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
        merged.setFormat(
                channel.getFormat() != null
                        ? channel.getFormat()
                        : other.getFormat()
        );
        return merged;
    }

    /**
     * Calculate the intersection of two lists of channels.
     *
     * @param channels      a list of channels
     * @param otherChannels a list of channels
     * @param locale        the default location
     * @return a list of channels
     */
    public static List<Channel> intersect(
            List<Channel> channels, List<Channel> otherChannels, Place locale ) {
        List<Channel> intersection = new ArrayList<Channel>();
        List<Channel> shorter;
        List<Channel> longer;
        if ( channels.size() <= otherChannels.size() ) {
            shorter = channels;
            longer = otherChannels;
        } else {
            shorter = otherChannels;
            longer = channels;
        }
        for ( Channel channel : shorter ) {
            for ( Channel other : longer ) {
                Channel withNarrowerMediumAndFormat =
                        channel.narrowsOrEquals( other, locale )
                                ? channel
                                : other.narrowsOrEquals( channel, locale )
                                ? other
                                : null;
                if ( withNarrowerMediumAndFormat != null ) {
                    intersection.add( withNarrowerMediumAndFormat );
                    break;
                }
            }
        }
        return intersection;
    }

    public boolean narrowsOrEquals( Channel other, Place locale ) {
        assert getMedium() != null && other.getMedium() != null;
        return getMedium().narrowsOrEquals( other.getMedium(), locale )
                && ( other.getFormat() == null
                || ( getFormat() != null && getFormat().narrowsOrEquals( other.getFormat(), locale ) ) );
    }
}
