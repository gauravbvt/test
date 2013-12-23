package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Attachment.Type;
import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * Is participation as this actor open to all users?
     */
    private boolean openParticipation = false;

    /**
     * Maximum number of participants as this actor.
     * -1 means no limit.
     */
    private int maxParticipation = 1;
    /**
     * Can only one user participate as this actor?
     */
    private boolean singularParticipation = true;
    /**
     * Is participating as this actor restricted to users also participating
     * as another actor with a same employer as this?
     */
    private boolean participationRestrictedToEmployed = false;
    /**
     * Participation must be confirmed by a supervisor to be effective.
     */
    private boolean supervisedParticipation = false;
    /**
     * Is the user's identity and contact info visible in protocols from this actor?
     */
    private boolean anonymousParticipation = false;

    /**
     * The actor's time-based availability.
     * Null means 24/7.
     */
    private WorkTime availability = new WorkTime();

    /**
     * Clearances.
     */
    private List<Classification> clearances = new ArrayList<Classification>();

    /**
     * Spoken languages.
     */
    private List<String> languages = new ArrayList<String>();


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
    public boolean isInvolvedIn( Assignments allAssignments, Commitments allCommitments ) {
        return !allAssignments.with( this ).isEmpty();
    }

    public static String classLabel() {
        return "agents";
    }

    @Override
    public String getClassLabel() {
        return classLabel();
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

    public boolean isOpenParticipation() {
        return openParticipation;
    }

    public void setOpenParticipation( boolean openParticipation ) {
        this.openParticipation = openParticipation;
    }

    public int getMaxParticipation() {
        return maxParticipation;
    }

    public void setMaxParticipation( int maxParticipation ) {
        this.maxParticipation = maxParticipation;
    }

    public boolean isSingularParticipation() {
        return maxParticipation == 1;
    }

    public void setSingularParticipation( boolean singularParticipation ) {   // todo - obsolete
        maxParticipation = singularParticipation ? 1 : -1;
    }

    public boolean isParticipationRestrictedToEmployed() {
        return participationRestrictedToEmployed;
    }

    public void setParticipationRestrictedToEmployed( boolean participationRestrictedToEmployed ) {
        this.participationRestrictedToEmployed = participationRestrictedToEmployed;
    }

    public boolean isSupervisedParticipation() {
        return supervisedParticipation;
    }

    public void setSupervisedParticipation( boolean supervisedParticipation ) {
        this.supervisedParticipation = supervisedParticipation;
    }

    public boolean isAnonymousParticipation() {
        return anonymousParticipation;
    }

    public void setAnonymousParticipation( boolean anonymousParticipation ) {
        this.anonymousParticipation = anonymousParticipation;
    }

    @Override
    public WorkTime getAvailability() {
        return availability == null ? new WorkTime(  ) : availability;
    }

    public void setAvailability( WorkTime val ) {
        availability = ( val == null )
                ? new WorkTime()
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

    public boolean isUndefined() {
        return super.isUndefined()
                && languages.isEmpty()
                && clearances.isEmpty()
                && availability.isAlways();
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
        return getName();
 /*       String name = getName().trim();
        if ( this == UNKNOWN || name.indexOf( ',' ) >= 0 ) return name;
        else if ( isType() || system || placeHolder ) return name;
        else {
            int index = name.lastIndexOf( ' ' );
            if ( index >= 0 ) {
                String s = name.substring( 0, index );
                return name.substring( index + 1 ) + ", " + s;
            } else
                return name;
        }*/
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
                flow.getEffectiveEois(),
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

    @Override
    public boolean hasAddresses() {
        return false;    // Only users participating as agents have addresses.
    }

    /**
     * Whether can be associated with at most one user.
     *
     * @return a boolean
     */
    public boolean isSingular() {
        return isSingularParticipation();
    }

    public String getParticipationPlurality() {
        return isSingular() ? "one user" : "many users";
    }

    public boolean isParticipationUserAssignable() {
        return isOpenParticipation() || isParticipationRestrictedToEmployed() || isSupervisedParticipation();
    }

    public boolean isUnconstrainedParticipation() {
        return isOpenParticipation()
                && !isSingularParticipation()
                && !isSupervisedParticipation();
    }

    public String getRequirementsDescription( Plan plan ) {
        StringBuilder sb = new StringBuilder();
/*
        sb.append( "<p><b>" ).append( isSystem() ? "A system" : "A person" ).append( "</p><br/>" );
        if ( !getDescription().isEmpty() ) {
            sb.append( "<p>" ).append( getDescription() ).append( "</p><br/>" );
        }
        sb.append( "<p><b>" ).append( "Availability: " ).append( "</b>" );
        sb.append( getAvailability() ).append( "</p><br/>" );
        sb.append( "<p><b>" ).append( "Languages: " ).append( "</b>" );
        sb.append( ChannelsUtils.listToString( getEffectiveLanguages( plan ), ", ", " and " ) )
                .append( "</p><br/>" );
        sb.append( "<p><b>" ).append( "Clearances: " ).append( "</b>" );
        sb.append( getClearances().isEmpty()
                ? "None"
                : ChannelsUtils.listToString( getClearances(), ", ", " and " ) );
        sb.append( "</p><br/>" );
        sb.append( "<b>" ).append( "Other requirements: " ).append( "</b>" );
*/
        sb.append( isSystem() ? "A system" : "A person" ).append( "\n\n" );
        if ( !getDescription().isEmpty() ) {
            sb.append( getDescription() ).append( "\n\n" );
        }
        sb.append( "Availability: " );
        sb.append( getAvailability() ).append( "\n\n" );
        sb.append( "Languages: " );
        sb.append( ChannelsUtils.listToString( getEffectiveLanguages( plan ), ", ", " and " ) )
                .append( "\n\n" );
        sb.append( "Clearances: " );
        sb.append( getClearances().isEmpty()
                ? "None"
                : ChannelsUtils.listToString( getClearances(), ", ", " and " ) );
        sb.append( "\n\nOther requirements: " );
        List<String> qualifications = new ArrayList<String>();
        for ( ModelEntity type : getAllTypes() ) {
            if ( !type.getDescription().isEmpty() ) {
                qualifications.add( type.getDescription() );
            }
        }
        if ( qualifications.isEmpty() )
            sb.append( "None" );
        else {
            for ( String qualification : qualifications ) {
                sb.append( qualification ).append( "\n\n" );
            }
        }
        return sb.toString();

    }

    public boolean isAnyNumberOfParticipants() {
        return maxParticipation == -1;
    }

    @Override
    public List<? extends Hierarchical> getSuperiors( QueryService queryService ) {
        if ( isType() ) {
            return super.getSuperiors( queryService );
        } else {
            List<Hierarchical> superiors = new ArrayList<Hierarchical>();
            superiors.addAll( queryService.findAllSupervisorsOf( this ) );
            return superiors;
        }
    }

    /**
     * Whether at most one user can participate as this actor, taking into account placeholder employers.
     * @param queryService a query service
     * @return a boolean
     */
    public boolean isAbsoluteSingularParticipation( QueryService queryService ) {
        if ( isSingularParticipation() ) {
            Set<Organization> primaryEmployers = new HashSet<Organization>(  );
            for ( Employment employment : queryService.findAllEmploymentsForActor( this ) ) {
                if ( employment.getJob().isPrimary() )
                    primaryEmployers.add( employment.getOrganization() );
            }
            // For the template to be valid, there should be at most one primary employer, but it might not be valid.
            if ( primaryEmployers.size() > 0 ) { // verify none of the primary employers are placeholders with multiple participation
                return !CollectionUtils.exists(
                        primaryEmployers,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                Organization organization = (Organization)object;
                                return organization.isPlaceHolder() && !organization.isSingleParticipation();
                            }
                        }
                );
            } else {
                return true; // no placeholder employer with participation > 1
            }
        } else {
            return false;
        }
    }
}
