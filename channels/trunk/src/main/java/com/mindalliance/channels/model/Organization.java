package com.mindalliance.channels.model;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.geo.GeoLocation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A company, agency, social club, etc.
 */
@Entity
public class Organization extends AbstractUnicastChannelable implements GeoLocatable, Hierarchical {

    /**
     * Parent organization. May be null.
     */
    private Organization parent;

    /**
     * The primary location of the organization
     */
    private Place location;
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
     * Bogus organization used to signify that the organization is not known...
     */
    public static final Organization UNKNOWN;

    static {
        UNKNOWN = new Organization( "(unknown)" );
        UNKNOWN.setId( 10000000L - 3 );
    }

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
    @Transient
    @Override
    public boolean isEntity() {
        return true;
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

    @ManyToOne( cascade = CascadeType.PERSIST, fetch = FetchType.LAZY )
    public Organization getParent() {
        return parent;
    }

    public void setParent( Organization parent ) {
        this.parent = parent;
    }

    @ManyToOne( cascade = CascadeType.PERSIST, fetch = FetchType.LAZY )
    public Place getLocation() {
        return location;
    }

    public void setLocation( Place location ) {
        this.location = location;
    }

    /**
     * Whether this organization has for parent a given organization (transitive)
     *
     * @param organization an organization
     * @return a boolean
     */
    public boolean isWithin( Organization organization ) {
        return parent != null && ( parent == organization || parent.isWithin( organization ) );

    }

    /**
     * Whether this is the same or within a given organization
     *
     * @param organization an organization
     * @return a boolean
     */
    public boolean isSameOrWithin( Organization organization ) {
        return this == organization || isWithin( organization );
    }

    /**
     * A string that shows line of parent organizations
     *
     * @return a String
     */
    public String parentage() {
        String parentage = parent == null ? "" : parent.getName() + "," + parent.parentage();
        return parentage.endsWith( "," ) ? parentage.substring( 0, parentage.length() - 1 )
                : parentage;
    }

    /**
     * List all ancestors.
     *
     * @return a list of organizations
     */
    public List<Organization> ancestors() {
        List<Organization> ancestors = new ArrayList<Organization>();
        if ( parent != null ) {
            ancestors.add( parent );
            ancestors.addAll( parent.ancestors() );
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

    @OneToMany
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
     * @param queryService a query service
     * @return a list of resource specs
     */
    public List<ResourceSpec> jobResourceSpecs( QueryService queryService ) {
        List<ResourceSpec> resourceSpecs = new ArrayList<ResourceSpec>();
        for ( Job job : jobs ) {
            resourceSpecs.add( job.resourceSpec( this ) );
        }
        return resourceSpecs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public boolean isUndefined() {
        return super.isUndefined() && parent == null && location == null && jobs.isEmpty();
    }

    /**
      * {@inheritDoc}
      */
     @Transient
    public GeoLocation geoLocate() {
        return location != null ? location.geoLocate() : null;
    }

    /**
      * {@inheritDoc}
      */
     @Transient
    public String getGeoMarkerLabel() {
        return location != null ? location.getGeoMarkerLabel() : "";
    }

    /**
       * {@inheritDoc}
       */
      @Transient
    public List<Hierarchical> getSuperiors() {
        List<Hierarchical> superiors =  new ArrayList<Hierarchical>();
        if ( parent != null) superiors.add( parent );
        return superiors;
    }
}

