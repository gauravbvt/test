package com.mindalliance.channels.model;

import com.mindalliance.channels.QueryService;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Transient;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An object with name, id and description, comparable by its toString() values.
 */
@Entity
@Inheritance( strategy = InheritanceType.JOINED )
public abstract class ModelObject implements Comparable<ModelObject>, Identifiable {

    /**
     * Unique id of this object.
     */
    private long id;

    /**
     * Name of this object.
     */
    private String name = "";

    /**
     * The description.
     */
    private String description = "";

    /**
     * Time the object was last modified. Set by aspect.
     */
    // private Date lastModified;
    /**
     * List of waived issue detections (issue detector class simple names)
     */
    private List<String> waivedIssueDetections = new ArrayList<String>();
    /**
     * List of attachment tickets.
     */
    private List<String> attachmentTickets = new ArrayList<String>();

    //=============================
    protected ModelObject() {
    }

    /**
     * Utility constructor for tests.
     *
     * @param name the name of the new object
     */
    protected ModelObject( String name ) {
        this();
        setName( name );
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public final void setId( long id ) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this object.
     *
     * @param name the name. Will complain if null.
     */
    public void setName( String name ) {
        this.name = name == null ? "" : name;
    }

    @Lob
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of this object.
     *
     * @param description the description. Will set to empty string if null.
     */
    public void setDescription( String description ) {
        this.description = description == null ? "" : description;
    }

    //=============================
    /**
     * Compare with another named object.
     *
     * @param o the object.
     * @return 0 if equals, -1 if this object smaller than the other, 1 if greater
     */
    public int compareTo( ModelObject o ) {
        int result = Collator.getInstance().compare( toString(), o.toString() );
        if ( result == 0 )
            result = getId() > o.getId() ? 1
                    : getId() < o.getId() ? -1
                    : 0;
        return result;
    }

    //=============================
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object obj ) {
        return this == obj
                || obj instanceof ModelObject
                && id == ( (ModelObject) obj ).getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Long.valueOf( id ).hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }

    @Transient
    public Date getLastModified() {
        // TODO implement last modified with aspect
        return new Date();
    }

    // TODO objectify strings if and when persistence is to work...
    @Transient
    public List<String> getWaivedIssueDetections() {
        return waivedIssueDetections;
    }

    public void setWaivedIssueDetections( List<String> waivedIssueDetections ) {
        this.waivedIssueDetections = waivedIssueDetections;
    }

    @Transient
    public List<String> getAttachmentTickets() {
        return attachmentTickets;
    }

    public void setAttachmentTickets( List<String> attachmentTickets ) {
        this.attachmentTickets = attachmentTickets;
    }

    /**
     * Waive a kind of issue detection.
     *
     * @param detection a string
     */
    public void waiveIssueDetection( String detection ) {
        if ( !waivedIssueDetections.contains( detection ) )
            waivedIssueDetections.add( detection );
    }

    /**
     * Un-waive a kind of issue detection.
     *
     * @param detection a string
     */
    public void unwaiveIssueDetection( String detection ) {
        waivedIssueDetections.remove( detection );
    }

    public boolean isWaived( String detection ) {
        return waivedIssueDetections.contains( detection );
    }

    /**
     * Get a label
     *
     * @return a string
     */
    @Transient
    public String getLabel() {
        return getName();
    }

    /**
     * Whether the model object is an entity
     *
     * @return a boolean
     */
    @Transient
    public boolean isEntity() {
        return false;
    }

    /**
     * Whether no properties other than name are set.
     *
     * @return a boolean
     */
    @Transient
    public boolean isUndefined() {
        return description.isEmpty();
    }

    /**
     * Executed just before the model object is removed.
     *
     * @param queryService a query service
     */
    public void beforeRemove( QueryService queryService ) {
        queryService.getAttachmentManager().detachAll( getAttachmentTickets() );
    }

    /**
     * Add attachment ticket.
     *
     * @param ticket a string
     */
    public void addAttachmentTicket( String ticket ) {
        if ( !attachmentTickets.contains( ticket ) ) attachmentTickets.add( ticket );
    }

    /**
     * Remove attachment ticket.
     *
     * @param ticket a string
     */
    public void removeAttachmentTicket( String ticket ) {
        attachmentTickets.remove( ticket );
    }
}
