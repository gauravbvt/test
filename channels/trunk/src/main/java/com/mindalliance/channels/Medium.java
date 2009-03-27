package com.mindalliance.channels;

import javax.persistence.Transient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A communication medium.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 30, 2009
 * Time: 7:14:12 AM
 */

public enum Medium {

    /**
     * Telephone.
     */
    Phone( "Work phone", "(\\d*\\D*\\d{3}\\D*\\d{3}\\D*\\d{4}(\\s*\\D+\\s*\\d+)?)|(\\d{3})" ),
    /**
     * Telephone.
     */
    HomePhone( "Home phone", "\\d*\\D*\\d{3}\\D*\\d{3}\\D*\\d{4}" ),
    /**
     * Fax.
     */
    Fax( "Fax", "\\d*\\D*\\d{3}\\D*\\d{3}\\D*\\d{4}" ),
    /**
     * Cell phone.
     */
    Cell( "Cell", "\\d*\\D*\\d{3}\\D*\\d{3}\\D*\\d{4}" ),
    /**
     * Phone conference.
     */
    PhoneConf( "Phone conference", "\\d*\\D*\\d{3}\\D*\\d{3}\\D*\\d{4}(\\s*\\D+\\s*\\d+)?", false ),
    /**
     * Email.
     */
    Email( "Email", "[^@\\s]+@[^@\\s]+\\.\\w+" ),
    /**
     * Notification system.
     */
    Notifier( "Notification system", ".+", false ),
    /**
     * Instant messaging.
     */
    IM( "IM", ".+" ),
    /**
     * Radio.
     */
    Radio( "Radio", ".+", false ),
    /**
     * Television.
     */
    TV( "Television", ".+", false ),
    /**
     * Courier, like Fedex or UPS.
     */
    Courier( "Courier", ".+" ),
    /**
     * In person, one on one.
     */
    F2F( "Face-to-face", ".+" ),
    /**
     *
     */
    Meeting( "Meeting", ".+", false ),
    /**
     * Miscellaneous unicast.
     */
    OtherUnicast( "Unicast", ".+" ),
    /**
     * Miscellaneous broadcast.
     */
    Other( "Broadcast", ".+", false );


    /**
     * The medium's name
     */
    private String name = "";
    /**
     * A pattern for validation
     */
    private String addressPattern = "";
    /**
     * The compiled pattern
     */
    private Pattern compiledPattern;
    /**
     * Whether the medium is unicast or broadcast.
     */
    private boolean unicast = true;

    Medium( String name, String addressPattern ) {
        this( name, addressPattern, true );
    }

    Medium( String name, String addressPattern, boolean unicast ) {
        this.name = name;
        this.unicast = unicast;
        this.addressPattern = addressPattern;
        compiledPattern = Pattern.compile( addressPattern );
    }

    /**
     * Get Medium by name.
     * Return Other if none found.
     *
     * @param name a String
     * @return a Medium
     */
    public static Medium named( String name ) {
        for ( Medium m : Medium.values() ) {
            if ( m.getName().equals( name ) )
                return m;
        }
        return Other;
    }

    public String getName() {
        return name;
    }

    public boolean isUnicast() {
        return unicast;
    }

    @Transient
    public boolean isBroadcast() {
        return !unicast;
    }

    public void setUnicast( boolean unicast ) {
        this.unicast = unicast;
    }

    public String getAddressPattern() {
        return addressPattern;
    }

    private Pattern getCompiledPattern() {
        return compiledPattern;
    }

    /**
     * Check if an address is valid.
     *
     * @param address the address
     * @return true if valid
     */
    public boolean isAddressValid( String address ) {
        if ( addressPattern.isEmpty() ) return true;
        Pattern p = getCompiledPattern();
        Matcher m = p.matcher( address );
        return m.matches();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * List all media.
     *
     * @return a list of medium's
     */
    public static List<Medium> media() {
        return Arrays.asList( Medium.values() );
    }

    /**
     * List unicast media.
     *
     * @return a list of medium's
     */
    public static List<Medium> unicastMedia() {
        List<Medium> unicastMedia = new ArrayList<Medium>();
        for ( Medium medium : values() ) {
            if ( medium.isUnicast() ) unicastMedia.add( medium );
        }
        return unicastMedia;
    }

    /**
     * List broadcast media.
     *
     * @return a list of medium's
     */
    public static List<Medium> broadcastMedia() {
        List<Medium> broadcastMedia = new ArrayList<Medium>();
        for ( Medium medium : values() ) {
            if ( medium.isBroadcast() ) broadcastMedia.add( medium );
        }
        return broadcastMedia;
    }
}
