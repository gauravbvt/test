package com.mindalliance.channels.model;

import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.geo.GeoLocation;
import com.mindalliance.channels.query.QueryService;
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
    private boolean actorsRequired;
    /**
     * Whether each sharing commitments from this organization requires an agreement.
     */
    private boolean agreementsRequired;
    /**
     * Confirmed agreement to share information with other organizations.
     */
    private List<Agreement> agreements = new ArrayList<Agreement>();

    /**
     * Bogus organization used to signify that the organization is not known...
     */
    public static Organization UNKNOWN;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDefinedUsing( final ModelEntity entity ) {
        return super.isDefinedUsing( entity )
                ||
                CollectionUtils.exists(
                        ancestors(),
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                return ModelEntity.isEquivalentToOrDefinedUsing(
                                        (Organization) obj,
                                        entity );
                            }
                        }
                )
                || ModelEntity.isEquivalentToOrDefinedUsing( getLocation(), entity );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean overrideNarrows( ModelEntity other, Place locale ) {
        if ( !isActual() )
            return false;

        return other.isActual() ? isWithin( (Organization) other, locale )
                                : parent != null && parent.narrowsOrEquals( other, locale );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean meetsTypeRequirementTests( ModelEntity entityType, Place locale ) {
        // check that location and parent are compatible
        return ModelEntity.implies( location, ( (Organization) entityType ).getLocation(), locale )
                && ModelEntity.implies( parent, ( (Organization) entityType ).getParent(), locale );
    }

    public boolean isActorsRequired() {
        return actorsRequired;
    }

    public void setActorsRequired( boolean actorsRequired ) {
        this.actorsRequired = actorsRequired;
    }

    public boolean isAgreementsRequired() {
        return agreementsRequired;
    }

    public void setAgreementsRequired( boolean agreementsRequired ) {
        this.agreementsRequired = agreementsRequired;
    }

    public List<Agreement> getAgreements() {
        return agreements;
    }

    public void setAgreements( List<Agreement> agreements ) {
        this.agreements = agreements;
    }

    /**
     * Add an agreement if unique.
     *
     * @param agreement an agreement
     */
    public void addAgreement( Agreement agreement ) {
        if ( !agreements.contains( agreement ) ) {
            agreements.add( agreement );
        }
    }

    public Organization getParent() {
        return parent;
    }

    public void setParent( Organization parent ) {
        assert parent == null
                || ( isActual() && parent.isActual() )
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

    /**
     * Whether this organization has an ancestor that narrows or equals a given organization.
     *
     * @param organization an organization
     * @param locale the default location
     * @return a boolean
     */
    public boolean isWithin( Organization organization, Place locale ) {
        for ( Organization org : ancestors() )
            if ( org.narrowsOrEquals( organization, locale ) )
                return true;

        return false;
    }

    /**
     * Whether this is the same or within a given organization
     *
     * @param organization an organization
     * @param locale the default location
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
     * List all ancestors, avoiding circularities
     *
     * @return a list of organizations
     */
    public List<Organization> ancestors() {
        return safeAncestors( new HashSet<Organization>() );
    }

    private List<Organization> safeAncestors( Set<Organization> visited ) {
        List<Organization> ancestors = new ArrayList<Organization>();
        if ( !visited.contains( this ) ) {
            visited.add( this );
            if ( parent != null && !visited.contains( parent ) ) {
                ancestors.add( parent );
                ancestors.addAll( parent.safeAncestors( visited ) );
            }
        }
        return ancestors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return parent == null ? getName()
                : MessageFormat.format( "{0} - {1}", parent.toString(), getName() );
    }

    @SuppressWarnings( "unchecked" )
    public List<Job> getJobs() {
        // Filter out incomplete jobs (actor
        jobs = (List<Job>) CollectionUtils.select( jobs, new Predicate() {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUndefined() {
        return super.isUndefined() && parent == null && location == null && jobs.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public GeoLocation geoLocate() {
        return location != null ? location.geoLocate() : null;
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends GeoLocatable> getImpliedGeoLocatables( QueryService queryService ) {
        List<Organization> geoLocatables = new ArrayList<Organization>();
        for ( Organization organization : queryService.listEntitiesNarrowingOrEqualTo( this ) ) {
            if ( organization.isActual() ) {
                GeoLocation geoLoc = organization.geoLocate();
                if ( geoLoc != null ) {
                    geoLocatables.add( organization );
                }
            }
        }
        return geoLocatables;
    }

    /**
     * {@inheritDoc}
     *
     * @param queryService
     */
    public String getGeoMarkerLabel( QueryService queryService ) {
        return location != null ? location.getGeoMarkerLabel( queryService ) : "";
    }

    /**
     * {@inheritDoc}
     */
    public List<Hierarchical> getSuperiors() {
        List<Hierarchical> superiors = new ArrayList<Hierarchical>();
        if ( parent != null ) superiors.add( parent );
        return superiors;
    }

    /**
     * {@inheritDoc}
     */
    public List<Attachment.Type> getAttachmentTypes() {
        List<Attachment.Type> types = new ArrayList<Attachment.Type>();
        if ( !hasImage() )
            types.add( Attachment.Type.Image );
        types.addAll( super.getAttachmentTypes() );
        return types;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isIconized() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean references( final ModelObject mo ) {
        return super.references( mo )
                || ModelObject.areIdentical( parent, mo )
                || ModelObject.areIdentical( location, mo )
                ||
                CollectionUtils.exists(
                        jobs,
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                return ( (Job) obj ).references( mo );
                            }
                        }
                );
    }

    public Actor getActor() {
        return null;
    }

    public Role getRole() {
        return null;
    }

    public Organization getOrganization() {
        return this;
    }

    public Place getJurisdiction() {
        return null;
    }

    /**
     * Get primordial parent.
     *
     * @return an organization
     */
    public Organization getTopOrganization() {
        if ( parent == null ) return this;
        else {
            List<Organization> ancestors = ancestors();
            return ancestors.get( ancestors.size() - 1 );
        }
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

