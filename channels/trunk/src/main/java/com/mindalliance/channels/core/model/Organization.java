package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A company, agency, social club, etc.
 */
public class Organization extends AbstractUnicastChannelable
        implements GeoLocatable, Hierarchical, Specable {

    /**
     * Parent organization. May be null.
     */
    private Organization parent;

    /**
     * The primary location of the organization
     */
    private Place location;
    /**
     * The organization's mission.
     */
    private String mission = "";
    /**
     * Jobs.
     */
    private List<Job> jobs = new ArrayList<Job>();

    /**
     * Whether roles must have associated actors, else issues.
     */
    private boolean actorsRequired;    // todo - obsolete
    /**
     * Whether each sharing commitments from this organization requires an agreement.
     */
    private boolean agreementsRequired;                           // todo - obsolete
    /**
     * Confirmed agreement to share information with other organizations.
     */
    private List<Agreement> agreements = new ArrayList<Agreement>();   // todo - obsolete
    /**
     * The transmission media not (yet) deployed by an actual organization to its agents.
     */
    private List<TransmissionMedium> mediaNotDeployed = new ArrayList<TransmissionMedium>();       // todo - obsolete

    /**
     * Bogus organization used to signify that the organization is not known...
     */
    public static Organization UNKNOWN;
    /**
     * Whether the organization is a place holder for dynamically participating organizations.
     */
    public boolean placeHolder;
    /**
     * Identity of the actor who is authorized to place an organization
     * and confirms participation of that organization's actors without supervisors.
     */
    public Actor custodian;

    /**
     * Name of unknown organization.
     */
    public static String UnknownName = "(unknown)";

    public Organization() {
    }

    /**
     * Utility constructor for tests.
     *
     * @param name the name of the new object
     */
    public Organization( String name ) {
        super( name );
    }

    public static String classLabel() {
        return "organizations";
    }

    @Override
    public String getClassLabel() {
        return classLabel();
    }



    @Override
    public boolean validates( ModelEntity entity, Place locale ) {
        Organization org = (Organization) entity;
        return super.validates( org, locale )
                && ModelEntity.implies( org.location(), location, locale )
                && ModelEntity.implies( org.getParent(), getParent(), locale );
    }

    @Override
    public boolean narrowsOrEquals( ModelEntity other, Place locale ) {
        return super.narrowsOrEquals( other, locale )
                || other instanceof Organization && isWithin( (Organization) other, locale );
    }

    public boolean narrowsOrEqualsNotWithin( ModelEntity other, Place locale ) {
        return super.narrowsOrEquals( other, locale );
    }


    @Override
    protected List<ModelEntity> safeImplicitTypes( Set<ModelEntity> visited ) {
        Set<ModelEntity> implicitTypes = new HashSet<ModelEntity>();
        if ( !visited.contains( this ) ) {
            visited.add( this );
            if ( isActual() ) {
                for ( ModelEntity ancestor : ancestors() ) {
                    implicitTypes.addAll( ancestor.safeAllTypes( visited ) );
                }
            }
        }
        return new ArrayList<ModelEntity>( implicitTypes );
    }

    public boolean isActorsRequired() {
        return actorsRequired;
    }

/*
    public void setActorsRequired( boolean actorsRequired ) {
        this.actorsRequired = actorsRequired;
    }
*/

    public boolean isAgreementsRequired() {
        return agreementsRequired;
    }

    public void setAgreementsRequired( boolean agreementsRequired ) {
        this.agreementsRequired = agreementsRequired;
    }

    public boolean isEffectiveAgreementsRequired() {
        return agreementsRequired ||
                CollectionUtils.exists(
                        ancestors(),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (Organization) object ).isAgreementsRequired();
                            }
                        } );
    }

    public List<Agreement> getAgreements() {
        return agreements;
    }

/*
    public void setAgreements( List<Agreement> agreements ) {
        this.agreements = agreements;
    }

    public void addAgreement( Agreement agreement ) {
        if ( !agreements.contains( agreement ) )
            agreements.add( agreement );
    }

    public List<TransmissionMedium> getMediaNotDeployed() {
        return mediaNotDeployed;
    }

    public void setMediaNotDeployed( List<TransmissionMedium> mediaNotDeployed ) {
        this.mediaNotDeployed = mediaNotDeployed;
    }

    public void addMediumNotDeployed( TransmissionMedium medium ) {
        assert isActual();
        if ( !mediaNotDeployed.contains( medium ) )
            mediaNotDeployed.add( medium );
    }
*/

    public Organization getParent() {
        return parent;
    }

    public void setParent( Organization parent ) {
        assert parent == null || isActual() && parent.isActual()
                || isType();
        this.parent = parent;
    }

    public Place getLocation() {
        return location;
    }

    public void setLocation( Place location ) {
        assert location == null || isType() || location.isActual();
        this.location = location;
    }

    public String getMission() {
        return mission;
    }

    public void setMission( String mission ) {
        this.mission = mission;
    }

    public Actor getCustodian() {
        return custodian;
    }

    public void setCustodian( Actor custodian ) {
        this.custodian = custodian;
    }

    public boolean isPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder( boolean placeHolder ) {
        this.placeHolder = placeHolder;
    }

    /**
     * Whether this organization has an ancestor that narrows or equals a given organization.
     *
     * @param organization an organization
     * @param locale       the default location
     * @return a boolean
     */
    public boolean isWithin( Organization organization, Place locale ) {
        if ( getKind().equals( organization.getKind() ) )
            for ( Organization org : ancestors() )
                if ( org.narrowsOrEquals( organization, locale ) )
                    return true;

        return false;
    }

    /**
     * Find explicit or implied location.
     *
     * @return a place
     */
    public Place location() {
        if ( location != null ) return location;
        else {
            for ( Organization ancestor : ancestors() ) {
                if ( ancestor.getLocation() != null ) return ancestor.getLocation();
            }
        }
        return null;
    }

    /**
     * Whether this is the same or within a given organization
     *
     * @param organization an organization
     * @param locale       the default location
     * @return a boolean
     */
    public boolean isSameOrWithin( Organization organization, Place locale ) {
        return equals( organization ) || isWithin( organization, locale );
    }

    /**
     * A string that shows line of parent organizations
     *
     * @return a String
     */
    public String parentage() {
        StringBuilder sb = new StringBuilder();
        Iterator<Organization> iter = ancestors().iterator();
        while ( iter.hasNext() ) {
            sb.append( iter.next().getName() );
            if ( iter.hasNext() ) sb.append( ',' );
        }
        return sb.toString();
    }

    /**
     * List all ancestors, avoiding circularities.
     *
     * @return a list of organizations, without this one
     */
    public List<Organization> ancestors() {
        List<Organization> results = selfAndAncestors();
        results.remove( this );
        return results;
        /*Set<Organization> visited = new HashSet<Organization>();
        safeAncestors( visited );
        visited.remove( this );
        return new ArrayList<Organization>( visited );*/
    }

    public List<Organization> selfAndAncestors() {
        Set<Organization> visited = new HashSet<Organization>();
        safeAncestors( visited );
        return new ArrayList<Organization>( visited );
    }


    private void safeAncestors( Set<Organization> visited ) {
        if ( !visited.contains( this ) ) {
            visited.add( this );
            if ( getParent() != null )
                getParent().safeAncestors( visited );
        }
    }

    @Override
    public String toString() {
        return getParent() == null ? getName()
                : MessageFormat.format( "{0} - {1}", parentage(), getName() );
    }

    @SuppressWarnings( "unchecked" )
    public List<Job> getJobs() {
        // Filter out incomplete jobs (actor
        jobs = (List<Job>) CollectionUtils.select( jobs, new Predicate() {
            @Override
            public boolean evaluate( Object obj ) {
                Job job = (Job) obj;
                return job.isDefined();
            }
        } );
        return jobs;
    }

    public void setJobs( List<Job> jobs ) {
        this.jobs = jobs;
    }

    /**
     * Add a job.
     *
     * @param job a job
     */
    public void addJob( Job job ) {
        if ( !jobs.contains( job ) ) jobs.add( job );
    }

    /**
     * Remove a job.
     *
     * @param job a job
     */
    public void removeJob( Job job ) {
        jobs.remove( job );
    }

    /**
     * Return resources specs from jobs.
     *
     * @return a list of resource specs
     */
    public List<ResourceSpec> jobResourceSpecs() {
        List<ResourceSpec> resourceSpecs = new ArrayList<ResourceSpec>();
        for ( Job job : jobs )
            resourceSpecs.add( job.resourceSpec( this ) );

        return resourceSpecs;
    }

    @Override
    public boolean isUndefined() {
        return super.isUndefined()
                && !isPlaceHolder()
                && getParent() == null
                && location == null
                && jobs.isEmpty();
    }

    @Override
    public Place getPlaceBasis() {
        return location == null ? null : location.getPlaceBasis();
    }

    @Override
    public List<? extends GeoLocatable> getImpliedGeoLocatables() {
        return new ArrayList<Organization>();
    }

    @Override
    public String getGeoMarkerLabel() {
        return location != null ? location.getGeoMarkerLabel() : "";
    }

    @Override
    public List<? extends Hierarchical> getSuperiors( QueryService queryService ) {
        if ( isType() ) {
            return super.getSuperiors( queryService );
        } else {
            List<Hierarchical> superiors = new ArrayList<Hierarchical>();
            if ( getParent() != null ) superiors.add( getParent() );
            return superiors;
        }
    }

    @Override
    public List<AttachmentImpl.Type> getAttachmentTypes() {
        List<AttachmentImpl.Type> types = new ArrayList<AttachmentImpl.Type>();
        if ( !hasImage() )
            types.add( AttachmentImpl.Type.Image );
     //   types.add( AttachmentImpl.Type.MOU );
        types.addAll( super.getAttachmentTypes() );
        return types;
    }

    @Override
    public List<AttachmentImpl.Type> getAttachmentTypes( String attachablePath ) {
        List<AttachmentImpl.Type> types = new ArrayList<AttachmentImpl.Type>();
        if ( !hasImage() )
            types.add( AttachmentImpl.Type.Image );
  /*      if ( attachablePath.startsWith( "agreements" ) )
            types.add( AttachmentImpl.Type.MOU );
*/
        types.addAll( super.getAttachmentTypes() );
        return types;
    }


    @Override
    public boolean isIconized() {
        return true;
    }

    @Override
    public boolean references( final ModelObject mo ) {
        return super.references( mo )
                || ModelObject.areIdentical( getParent(), mo )
                || ModelObject.areIdentical( location, mo )
                || ModelObject.areIdentical( custodian, mo )
                ||
                CollectionUtils.exists(
                        jobs,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object obj ) {
                                return ( (Job) obj ).references( mo );
                            }
                        }
                );
    }

    @Override
    public Actor getActor() {
        return null;
    }

    @Override
    public Role getRole() {
        return null;
    }

    @Override
    public Organization getOrganization() {
        return this;
    }

    @Override
    public Place getJurisdiction() {
        return null;
    }

    /**
     * Get primordial parent.
     *
     * @return an organization
     */
    public Organization getTopOrganization() {
        if ( getParent() == null )
            return this;
        else {
            List<Organization> ancestors = ancestors();
            return ancestors.get( ancestors.size() - 1 );
        }
    }

    public Organization agreementRequiringParent() {
        return (Organization) CollectionUtils.find(
                ancestors(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Organization) object ).isAgreementsRequired();
                    }
                }
        );
    }

    public String getFullAddress() {
        Place location = getLocation();
        return location != null
                ? location.getFullAddress()
                : "";
    }

 /*   public boolean isMediumDeployed( final TransmissionMedium medium, final Place planLocale ) {
        return getMediaNotDeployed().isEmpty()
                || !CollectionUtils.exists(
                getMediaNotDeployed(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return medium.narrowsOrEquals( (TransmissionMedium) object, planLocale );
                    }
                }
        );
    }
*/
    /**
     * Whether a job is confirmed.
     *
     * @param job a job
     * @return a boolean
     */
    public boolean isConfirmed( Job job ) {
        return getJobs().contains( job );
    }

    public String getRequirementsDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append( "<p><b>" ).append( "Mission: " ).append( "</p><br/>" );
        sb.append( "<p>" ).append( getMission().isEmpty() ? "Any" : getMission() ).append( "</p><br/>" );
        sb.append( "<p><b>" ).append( "Inter-organization agreements required: " ).append( "</p><br/>" );
        sb.append( "<p>" ).append( isAgreementsRequired() ? "Yes" : "No" ).append( "</p><br/>" );
        sb.append( "<p><b>" ).append( "Custodian: " ).append( "</p><br/>" );
        sb.append( "<p>" ).append( getCustodian() == null
                ? "Any planner"
                : getCustodian().getName() ).append( "</p><br/>" );
        return sb.toString();
    }

    @Override
    public boolean hasAddresses() {
        return !isType() && !isPlaceHolder();
    }

    /**
     * Family relationships between organizations.
     */
    public enum FamilyRelationship {
        /**
         * Identity relationship (A equals B).
         */
        Identity,
        /**
         * Direct parent.
         */
        Parent,
        /**
         * Direct child.
         */
        Child,
        /**
         * Parent or parent of parent etc.
         */
        Ancestor,
        /**
         * Child or child of child etc.
         */
        Descendant,
        /**
         * Share same parent.
         */
        Sibling,
        /**
         * Share same ancestor but not same parent.
         */
        Cousin,
        /**
         * No family relationship.
         */
        None
    }


}

