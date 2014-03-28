package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.model.asset.AssetConnectable;
import com.mindalliance.channels.core.model.asset.AssetConnection;
import com.mindalliance.channels.core.model.asset.AssetConnections;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.Commitments;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class TransmissionMedium extends ModelEntity implements AssetConnectable {

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
    public static String UnknownName = "(unknown)";

    /**
     * A pattern for validation
     */
    private String addressPattern = "";
    /**
     * The compiled pattern
     */
    private Pattern compiledPattern;
    /**
     * Kind of transmission targeting.
     */
    private Cast cast;
    /**
     * Place within which actual medium is operational.
     * Global reach if null.
     */
    private Place reach;
    /**
     * Type of agent qualified to operate this transmission medium.
     */
    private Actor qualification;
    /**
     * Media this medium delegates transmissions to.
     * E.g. Phone tele-conference delegates to phone,
     * A Notification System delegates to phone, email, IM...
     */
    private List<TransmissionMedium> delegatedToMedia = new ArrayList<TransmissionMedium>();
    /**
     * Whether the medium is de facto available to all, address-free and fully secured.
     * Can only be made true for built-in media.
     */
    private boolean direct = false;
    /**
     * Whether commmunication is done in "real-time" vs "store and forward".
     */
    private boolean synchronous = true;

    /**
     * Whether this is medium for contact info.
     */
    private boolean forContactInfo = false;
    /**
     * List of security classifications satisfied by this medium for the transmission of classified info.
     */
    private List<Classification> security = new ArrayList<Classification>();

    /**
     * Asset connections for this medium.
     */
    private AssetConnections assetConnections = new AssetConnections();


    public TransmissionMedium() {
        setType();
    }

    public TransmissionMedium( String name ) {
        super( name );
        setType();
    }

    @Override
    public boolean isInvolvedIn( Assignments allAssignments, Commitments allCommitments ) {
        return CollectionUtils.exists(
                allCommitments.toList(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Flow sharing = ( (Commitment) object ).getSharing();
                        return CollectionUtils.exists(
                                sharing.getChannels(),
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        return ( (Channel) object ).getMedium().narrowsOrEquals( TransmissionMedium.this );
                                    }
                                }
                        );
                    }
                }
        );
    }

    public static String classLabel() {
        return "transmission media";
    }

    @Override
    public String getClassLabel() {
        return classLabel();
    }


    private void compilePattern( String addressPattern ) {
        compiledPattern = null;
        try {
            if ( addressPattern != null && !addressPattern.isEmpty() )
                compiledPattern = Pattern.compile( addressPattern );
        } catch ( PatternSyntaxException e ) {
            LOG.warn( "Invalid pattern: " + addressPattern );
        }

    }

    /**
     * Get the unambiguous cast for the entity.
     * If ambiguous, return null.
     *
     * @return a cast
     */
    public Cast getCast() {
        return cast;
    }

    public void setCast( Cast cast ) {
        this.cast = cast;
    }

    public void setCast( String val ) {
        setCast( Cast.valueOf( val ) );
    }

    public boolean isForContactInfo() {
        return forContactInfo;
    }

    public void setForContactInfo( boolean forContactInfo ) {
        this.forContactInfo = forContactInfo;
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

    /**
     * Get address pattern, empty string if none.
     * If actual, get the longest address pattern of any type.
     *
     * @return a string
     */
    public String getAddressPattern() {
        return addressPattern;
    }

    /**
     * Get address pattern in effect, given tags.
     *
     * @return a string
     */
    public String getEffectiveAddressPattern() {
        Pattern pattern = getEffectiveCompiledAddressPattern();
        return pattern == null ? "" : pattern.pattern();
    }

    /**
     * Return the effective compiled pattern. Can be null.
     * It is self if pattern is not empty, esle the pattern is inherited.
     * Traverse tags "upward" breadth-first. When conflict at some level, use longest pattern.
     *
     * @return a  compiled pattern
     */
    @SuppressWarnings("unchecked")
    public Pattern getEffectiveCompiledAddressPattern() {
        if ( compiledPattern != null || getTypes().isEmpty() ) {
            return compiledPattern;
        } else {
            Set<ModelEntity> visited = new HashSet<ModelEntity>();
            visited.add( this );
            List<ModelEntity> unvisited = (List<ModelEntity>) CollectionUtils.subtract( getTypes(), visited );
            return findEffectiveCompiledAddressPattern( unvisited, visited );
        }
    }

    @SuppressWarnings("unchecked")
    private Pattern findEffectiveCompiledAddressPattern( List<ModelEntity> media, Set<ModelEntity> visited ) {
        if ( media.isEmpty() ) {
            return null;
        }
        visited.addAll( media );
        List<Pattern> patterns = new ArrayList<Pattern>();
        for ( ModelEntity medium : media ) {
            Pattern pattern = ( (TransmissionMedium) medium ).getCompiledPattern();
            if ( pattern != null ) patterns.add( pattern );
        }
        Collections.sort( patterns, new Comparator<Pattern>() {
            public int compare( Pattern p1, Pattern p2 ) {
                if ( p1.pattern().length() > p2.pattern().length() ) return -1;
                if ( p2.pattern().length() > p1.pattern().length() ) return 1;
                return 0;
            }
        } );
        if ( !patterns.isEmpty() ) {
            return patterns.get( 0 );
        } else {
            Set<ModelEntity> inheritedMedia = new HashSet<ModelEntity>();
            for ( ModelEntity medium : media ) {
                inheritedMedia.addAll( medium.getTypes() );
            }
            List<ModelEntity> unvisited = (List<ModelEntity>) CollectionUtils.subtract( inheritedMedia, visited );
            return findEffectiveCompiledAddressPattern( unvisited, visited );
        }
    }

    public void setAddressPattern( String addressPattern ) {
        this.addressPattern = addressPattern == null ? "" : addressPattern;
        compilePattern( addressPattern );
    }

    public Pattern getCompiledPattern() {
        return compiledPattern;
    }

    public Place getReach() {
        return reach;
    }

    public void setReach( Place reach ) {
        this.reach = reach;
    }

    public List<TransmissionMedium> getDelegatedToMedia() {
        return delegatedToMedia;
    }

    public void setDelegatedToMedia( List<TransmissionMedium> delegatedToMedia ) {
        this.delegatedToMedia = delegatedToMedia;
    }

    public boolean isUnicast() {
        return getEffectiveCast() == Cast.Unicast;
    }

    public boolean isMulticast() {
        return getEffectiveCast() == Cast.Multicast;
    }

    /**
     * Make type unicast.
     */
    public void setUnicast() {
        setCast( Cast.Unicast );
    }

    /**
     * Make type multicast.
     */
    public void setMulticast() {
        setCast( Cast.Multicast );
    }

    /**
     * Make type broadcast.
     */
    public void setBroadcast() {
        setCast( Cast.Broadcast );
    }

    public boolean isDirect() {
        return direct;
    }

    public void setDirect( boolean direct ) {
        this.direct = direct;
    }

    public boolean isSynchronous() {
        return synchronous;
    }

    public void setSynchronous( boolean synchronous ) {
        this.synchronous = synchronous;
    }

    /**
     * Whether the medium is for broadcast.
     *
     * @return a boolean
     */
    public boolean isBroadcast() {
        return getEffectiveCast() == Cast.Broadcast;
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

    public Actor getQualification() {
        return qualification;
    }

    public void setQualification( Actor qualification ) {
        assert qualification == null || qualification.isType();
        this.qualification = qualification;
    }

    /**
     * Check if an address is valid if set.
     *
     * @param address the address
     * @return true if valid
     */
    public boolean isAddressValidIfSet( String address ) {
        return address.isEmpty() || isAddressValid( address );
    }

    /**
     * Check if an address is valid if set.
     *
     * @param address the address
     * @return true if valid
     */
    public boolean isAddressValid( String address ) {
        Pattern pattern = getEffectiveCompiledAddressPattern();
        return pattern == null
                || pattern.matcher( address ).matches();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getLabel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeName() {
        return "medium";
    }

    /**
     * {@inheritDoc}
     */
    public String getKindLabel() {
        return "medium";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validates( ModelEntity entity, Place locale ) {
        return super.validates( entity, locale )
                && getEffectiveCast() == ( (TransmissionMedium) entity ).getEffectiveCast();
    }

    /**
     * The address pattern could not be compiled.
     *
     * @return a boolean
     */
    public boolean hasInvalidAddressPattern() {
        return !addressPattern.isEmpty() && compiledPattern == null;
    }

    /**
     * Whether this medium requires an address.
     *
     * @return a boolean
     */
    public boolean requiresAddress() {
        return !getAddressPatterns().isEmpty();
    }

    private List<String> getAddressPatterns() {
        Set<String> patterns = new HashSet<String>();
        if ( !addressPattern.isEmpty() && !addressPattern.equals( ".*" ) )
            patterns.add( addressPattern );
        for ( ModelEntity type : getAllTypes() ) {
            String pattern = ( (TransmissionMedium) type ).getAddressPattern();
            // drop empty or universal pattern
            if ( !pattern.isEmpty() && !pattern.equals( ".*" ) ) {
                patterns.add( pattern );
            }
        }
        return new ArrayList<String>( patterns );
    }

    /**
     * Add medium to the list of delegated-to media.
     *
     * @param delegatedToMedium a medium
     */
    public void addDelegatedToMedium( TransmissionMedium delegatedToMedium ) {
        if ( !delegatedToMedia.contains( delegatedToMedium ) )
            delegatedToMedia.add( delegatedToMedium );
    }

    /**
     * Get the cast that applies. Defaults to unicast.
     *
     * @return a cast (uni*, multi*, broad*) or null
     */
    public Cast getEffectiveCast() {
        if ( cast != null ) {
            return cast;
        } else {
            Cast inherited = getInheritedCast();
            return inherited != null ? inherited : Cast.Unicast;
        }
    }

    /**
     * Get inherited cats, if any.
     *
     * @return a cast or null
     */
    public Cast getInheritedCast() {
        if ( cast != null ) {
            return null;
        } else {
            List<Cast> casts = new ArrayList<Cast>();
            for ( ModelEntity ancestor : getAllTypes() ) {
                Cast c = ( (TransmissionMedium) ancestor ).getCast();
                if ( c != null && !casts.contains( c ) ) casts.add( c );
            }
            if ( casts.isEmpty() ) {
                return null;
            } else {
                Collections.sort( casts );
                return casts.get( 0 );
            }
        }
    }

    /**
     * Return aggregated local and inherited security classifications.
     *
     * @param collaborationModel the plan
     * @return a list of secrecy classifications
     */
    public List<Classification> getEffectiveSecurity( CollaborationModel collaborationModel ) {
        List<Classification> effective = new ArrayList<Classification>( security );
        for ( ModelEntity ancestor : getAllTypes() ) {
            List<Classification> classifications = ( (TransmissionMedium) ancestor ).getSecurity();
            for ( Classification classification : classifications ) {
                if ( !Classification.encompass( effective, classification, collaborationModel ) ) {
                    effective.add( classification );
                }
            }
        }
        return new ArrayList<Classification>( effective );
    }

    /**
     * Aggregate local and inherited delegated-to mediua, without redundancies.
     *
     * @param locale the default location
     * @return a list of transmission media
     */
    public List<TransmissionMedium> getEffectiveDelegates( Place locale ) {
        List<TransmissionMedium> effectiveMedia = new ArrayList<TransmissionMedium>( delegatedToMedia );
        for ( ModelEntity ancestor : getAllTypes() ) {
            for ( TransmissionMedium delegate : ( (TransmissionMedium) ancestor ).getDelegatedToMedia() ) {
                boolean redundant = false;
                for ( TransmissionMedium effectiveMedium : effectiveMedia ) {
                    if ( effectiveMedium.narrowsOrEquals( delegate, locale ) ) {
                        redundant = true;
                    } else if ( delegate.narrowsOrEquals( effectiveMedium, locale ) ) {
                        effectiveMedia.remove( effectiveMedium );
                    }
                }
                if ( !redundant ) effectiveMedia.add( delegate );
            }
        }
        return effectiveMedia;
    }

    /**
     * {@inheritDoc}
     */
    public boolean references( ModelObject mo ) {
        return super.references( mo )
                || ( mo instanceof TransmissionMedium && delegatedToMedia.contains( (TransmissionMedium) mo ) )
                || ModelObject.areIdentical( mo, qualification );
    }

    /**
     * Whether this medium could delegate to another medium based on their effective casts.
     *
     * @param other a medium
     * @return a boolean
     */
    public boolean canDelegateTo( TransmissionMedium other ) {
        Cast effectiveCast = getEffectiveCast();
        Cast otherEffectiveCast = other.getEffectiveCast();
        return effectiveCast != null
                && otherEffectiveCast != null
                && effectiveCast.canDelegateTo( otherEffectiveCast );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUndefined() {
        return super.isUndefined()
                && qualification == null
                && cast == null
                && delegatedToMedia.isEmpty()
                && security.isEmpty()
                && reach == null
                && addressPattern.isEmpty();
    }

    /**
     * Get all inherited delegated-to media.
     *
     * @return a list of transmission media
     */
    public List<TransmissionMedium> getInheritedDelegates() {
        Set<TransmissionMedium> inherited = new HashSet<TransmissionMedium>();
        for ( ModelEntity type : getAllTypes() ) {
            inherited.addAll( ( (TransmissionMedium) type ).getDelegatedToMedia() );
        }
        return new ArrayList<TransmissionMedium>( inherited );
    }

    /**
     * Get list of all local and inherited delegated-to media.
     *
     * @return a list of media
     */
    public List<TransmissionMedium> getEffectiveDelegatedToMedia() {
        Set<TransmissionMedium> effective = new HashSet<TransmissionMedium>( getDelegatedToMedia() );
        effective.addAll( this.getInheritedDelegates() );
        return new ArrayList<TransmissionMedium>( effective );
    }

    /**
     * Medium is explicitly or implicitly synchronous.
     *
     * @param planLocale
     * @return
     */
    public boolean isEffectiveSynchronous( final Place planLocale ) {
        List<TransmissionMedium> effectiveDelegates = getEffectiveDelegates( planLocale );
        return isSynchronous()
                ||
                CollectionUtils.exists(
                        getAllTypes(),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (TransmissionMedium) object ).isSynchronous();
                            }
                        }
                )
                ||
                ( !effectiveDelegates.isEmpty() && !CollectionUtils.exists(
                        effectiveDelegates,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return !( (TransmissionMedium) object ).isEffectiveSynchronous( planLocale );
                            }
                        }
                ) );
    }

    // AssetConnectable


    @Override
    public boolean isCanStockAssets() {
        return false;
    }

    @Override
    public boolean isCanProduceAssets() {
        return false;
    }

    @Override
    public boolean isCanUseAssets() {
        return true;
    }

    @Override
    public boolean isCanProvisionAssets() {
        return false;
    }

    @Override
    public boolean isCanBeAssetDemand() {
        return false;
    }

    @Override
    public AssetConnections getAssetConnections() {
        return assetConnections;
    }

    @Override
    public AssetConnection.Type getDefaultAssetConnectionType() {
        return AssetConnection.Type.Using;
    }

    public void addAssetConnection( AssetConnection assetConnection ) {
        assetConnections.add( assetConnection );
    }

    public enum Cast {

        /**
         * Broadcast medium.
         * One to many unknown agents.
         */
        Broadcast,
        /**
         * Multicast medium.
         * Transmission to many known agents.
         */
        Multicast,
        /**
         * Unicast medium.
         * Transmission to a known agent.
         */
        Unicast;

        /**
         * Whether a medium of this cast can delegate to a medium of another cast.
         * Broadcast can delegate to all,
         * Multicast to multicast or unicast,
         * Unicast only to unicast
         *
         * @param delegate a cast
         * @return a boolean
         */
        public boolean canDelegateTo( Cast delegate ) {
            return delegate.compareTo( this ) >= 0;
        }

    }
}
