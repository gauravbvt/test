package com.mindalliance.channels.model;

import com.mindalliance.channels.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * A transmission medium.
 * Only actual transmission media are created (for now).
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 25, 2009
 * Time: 2:56:10 PM
 */
public class TransmissionMedium extends ModelEntity {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( TransmissionMedium.class );

    /**
     * Unknown medium.
     */
    public static TransmissionMedium UNKNOWN;

    /**
     * Name of unknown medium.
     */
    private static String UnknownName = "(unknown)";

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
    /**
     * List of security classifications satisfied by this medium for the transmission of classified info.
     */
    private List<Classification> security = new ArrayList<Classification>();

    public TransmissionMedium() {
    }

    public TransmissionMedium( String name ) {
        super( name );
    }

    public TransmissionMedium( String name, String addressPattern ) {
        this( name, addressPattern, true );
    }

    public TransmissionMedium( String name, String addressPattern, boolean unicast ) {
        this( name );
        this.unicast = unicast;
        this.addressPattern = addressPattern;
        compilePattern( addressPattern );
    }

    private void compilePattern( String addressPattern ) {
        compiledPattern = null;
        try {
            compiledPattern = Pattern.compile( addressPattern );
        } catch ( PatternSyntaxException e ) {
            LOG.warn( "Invalid pattern: " + addressPattern );
        }

    }

    public static void createImmutables( List<TransmissionMedium> builtInMedia, QueryService queryService ) {
        UNKNOWN = queryService.findOrCreate( TransmissionMedium.class, UnknownName );
        UNKNOWN.makeImmutable();
        addBuiltIn( builtInMedia, queryService );
    }

    public static TransmissionMedium getUNKNOWN() {
        return UNKNOWN;
    }

    public static void setUNKNOWN( TransmissionMedium UNKNOWN ) {
        TransmissionMedium.UNKNOWN = UNKNOWN;
    }

    public static String getUnknownName() {
        return UnknownName;
    }

    public static void setUnknownName( String unknownName ) {
        UnknownName = unknownName;
    }

    public String getAddressPattern() {
        return addressPattern;
    }

    public void setAddressPattern( String addressPattern ) {
        this.addressPattern = addressPattern;
        compilePattern( addressPattern );
    }

    public Pattern getCompiledPattern() {
        return compiledPattern;
    }

    public boolean isUnicast() {
        return unicast;
    }

    public void setUnicast( boolean unicast ) {
        this.unicast = unicast;
    }

    /**
     * Whether the medium is for broadcast.
     *
     * @return a boolean
     */
    public boolean isBroadcast() {
        return !unicast;
    }

    public List<Classification> getSecurity() {
        return security;
    }

    public void setSecurity( List<Classification> security ) {
        this.security = security;
    }

    /**
     * Add a classification to the security of the medium.
     *
     * @param classification a classification
     */
    public void addSecurity( Classification classification ) {
        if ( !security.contains( classification ) )
            security.add( classification );
    }

    /**
     * Get the label.
     *
     * @return a string
     */
    public String getLabel() {
        return getName();
    }


    /**
     * Check if an address is valid.
     *
     * @param address the address
     * @return true if valid
     */
    public boolean isAddressValid( String address ) {
        if ( addressPattern.isEmpty() ) return true;
        if ( address.isEmpty() && isBroadcast() ) return true;
        Pattern p = getCompiledPattern();
        if ( p == null ) return false; // invalid address pattern - refuse all addresses
        Matcher m = p.matcher( address );
        return m.matches();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getLabel();
    }

    /**
     * Register pre-defined media on startup.
     *
     * @param builtInMedia a list of media
     * @param queryService a query service
     */
    public static void addBuiltIn( List<TransmissionMedium> builtInMedia, QueryService queryService ) {
        for ( TransmissionMedium medium : builtInMedia ) {
            queryService.getDao().add( medium );
            medium.makeImmutable();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeName() {
        return "Medium";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean meetsTypeRequirementTests( ModelEntity entityType ) {
        return unicast == ( (TransmissionMedium) entityType ).isUnicast();
    }
}
