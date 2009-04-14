package com.mindalliance.channels;

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
public class Organization extends AbstractUnicastChannelable {

    /**
     * The unknown organization.
     */
    public static final Organization UNKNOWN = new Organization( "(unknown)" );

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
     * Whether reoles must have associated actors, else issues.
     */
    private boolean actorsRequired;

    // private List<Job> jobs = new ArrayList<Job>(); // TODO - reflect in DataQueryObject.FindAllResourceSpecs()

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
     * @param dqo a data query object
     * @return a list of resource specs
     */
    public List<ResourceSpec> jobResourceSpecs( DataQueryObject dqo ) {
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

}

