package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Attachment.Type;
import com.mindalliance.channels.core.Matcher;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Someone or something playing a part in a segment.
 */
public class Actor extends AbstractUnicastChannelable implements Classifiable, Specable, Available {

    /**
     * The name of the unknown actor.
     */
    public static final String UnknownName = "(unknown contact)";

    /**
     * Bogus actor used to signify that the actor is not known...
     */
    public static Actor UNKNOWN;

    /**
     * Whether the actor is a system, vs. a person.
     */
    private boolean system;

    /**
     * Whether this (actual) agent is an archetype.
     */
    private boolean archetype = false;
    /**
     * Whether this (actual) agent is a place holder (to be named by assigned participant).
     */
    private boolean placeHolder = false;
    /**
     * The actor's time-based availability.
     * Null means 24/7.
     */
    private Availability availability = new Availability();

    /**
     * Clearances.
     */
    private List<Classification> clearances = new ArrayList<Classification>();

    /**
     * Spoken languages.
     */
    private List<String> languages = new ArrayList<String>();


    /**
     * Whether placeholder is singular.
     */
    private boolean placeHolderSingular = false;

    public Actor() {
    }

    /**
     * Utility constructor for tests.
     *
     * @param name the name of the new object
     */
    public Actor( String name ) {
        super( name );
    }

    @Override
    public String getKindLabel() {
        return "agent";
    }

    @Override
    public boolean isActual() {
        return !equals( UNKNOWN ) && super.isActual();
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem( boolean system ) {
        this.system = system;
    }

    public boolean isArchetype() {
        return archetype;
    }

    public void setArchetype( boolean archetype ) {
        this.archetype = archetype;
        if ( archetype ) placeHolder = false;
    }

    public boolean isPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder( boolean placeHolder ) {
        this.placeHolder = placeHolder;
        if ( placeHolder ) archetype = false;
    }

    public boolean isPlaceHolderSingular() {
        return placeHolderSingular;
    }

    public boolean isPlaceHolderPlural() {
        return isPlaceHolder() && !isPlaceHolderSingular();
    }

    public void setPlaceHolderSingular( boolean placeHolderSingular ) {
        this.placeHolderSingular = placeHolderSingular;
    }

    @Override
    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability( Availability val ) {
        availability = ( val == null )
                ? new Availability()
                : val;
    }

    public List<Classification> getClearances() {
        return clearances;
    }

    public void setClearances( List<Classification> clearances ) {
        this.clearances = clearances;
    }

    /**
     * Add a classification to the actor's clearances if unique.
     *
     * @param classification a classification
     * @return a boolean - whether added
     */
    public boolean addClearance( Classification classification ) {
        if ( !clearances.contains( classification ) ) {
            clearances.add( classification );
            return true;
        } else {
            return false;
        }
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages( List<String> languages ) {
        this.languages = languages;
    }

    /**
     * Add a language to the actor's spoken languages if unique.
     *
     * @param language a language
     * @return a boolean - whether added
     */
    public boolean addLanguage( String language ) {
        if ( language != null ) {
            if ( !languages.contains( language ) ) {
                languages.add( language );
                return true;
            } else {
                return false;
            }
        } else return false;
    }

    /**
     * Actor can speak a given language.
     *
     * @param language the name of a language
     * @param plan     a plan
     * @return a boolean
     */
    public boolean speaksLanguage( String language, Plan plan ) {
        final String lang = language.trim().toLowerCase();
        return languages.isEmpty() && Matcher.same( plan.getDefaultLanguage().toLowerCase(), lang )
                || CollectionUtils.exists(
                languages,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return Matcher.same( (String) object, lang );
                    }
                }
        );
    }

    /**
     * Languages spoken by actor.
     * Plan's default language is assumed if none set.
     *
     * @param plan a plan
     * @return a list of strings
     */
    public List<String> getEffectiveLanguages( Plan plan ) {
        HashSet<String> effective = new HashSet<String>();
        effective.addAll( languages );
        for ( ModelEntity type : getAllTypes() ) {
            effective.addAll( ( (Actor) type ).getLanguages() );
        }
        if ( effective.isEmpty() ) {
           effective.add( plan.getDefaultLanguage() );
        }
        return new ArrayList<String>( effective );
    }

    /**
     * Whether an actor can converse with another, language-wise.
     *
     * @param other an actor
     * @param plan  a plan
     * @return a boolean
     */
    public boolean canSpeakWith( Actor other, Plan plan ) {
        for ( String lang : getEffectiveLanguages( plan ) ) {
            for ( String otherLang : other.getEffectiveLanguages( plan ) ) {
                if ( Matcher.same( lang, otherLang ) ) return true;
            }
        }
        return false;
    }

    /**
     * Return a normalized version of the name.
     *
     * @return a string
     */
    public String getNormalizedName() {
        String name = getName().trim();
        if ( this == UNKNOWN || name.indexOf( ',' ) >= 0 ) return name;
        else if ( isType() || archetype || system || placeHolder ) return name;
        else {
            int index = name.lastIndexOf( ' ' );
            if ( index >= 0 ) {
                String s = name.substring( 0, index );
                return name.substring( index + 1 ) + ", " + s;
            } else
                return name;
        }
    }

    public String getLastName() {
        String name = getName().trim();
        if ( this == UNKNOWN || name.indexOf( ',' ) >= 0 ) return name;
        else {
            int index = name.lastIndexOf( ' ' );
            if ( index >= 0 ) {
                return name.substring( index + 1 );
            } else
                return name;
        }
    }

    /**
     * Whether the actor is a person (i.e. not a system).
     *
     * @return a boolean
     */
    public boolean isPerson() {
        return !system;
    }

    @Override
    public List<Type> getAttachmentTypes() {
        List<Type> types = new ArrayList<Type>();
        if ( !hasImage() )
            types.add( Type.Image );
        types.addAll( super.getAttachmentTypes() );
        return types;
    }

    @Override
    public boolean isIconized() {
        return true;
    }

    @Override
    public List<Classification> getClassifications() {
        return clearances;
    }

    /**
     * Whether the actor is cleared for the information in a flow.
     *
     * @param flow a flow
     * @param plan a plan
     * @return a boolean
     */
    public boolean isClearedFor( Flow flow, final Plan plan ) {
        // No eoi in the flow has classification that is not encompassed by the actor's clearance.
        return !CollectionUtils.exists(
                flow.getEois(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object obj ) {
                        ElementOfInformation eoi = (ElementOfInformation) obj;
                        return !Classification.encompass(
                                getClearances(),
                                eoi.getClassifications(), plan );
                    }
                }
        );
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public Role getRole() {
        return null;
    }

    @Override
    public Organization getOrganization() {
        return null;
    }

    @Override
    public Place getJurisdiction() {
        return null;
    }

    /**
     * Whether can be associated with at most one user.
     *
     * @return a boolean
     */
    public boolean isSingular() {
        return !archetype && ( !placeHolder || placeHolderSingular );
    }

    public String getParticipationPlurality() {
        return isSingular() ? "one user" : "many users";
    }
}
