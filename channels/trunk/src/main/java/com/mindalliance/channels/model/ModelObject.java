package com.mindalliance.channels.model;

import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * An object with name, id and description, comparable by its toString() values.
 */
public abstract class ModelObject extends AbstractAttachable implements Comparable<ModelObject>, Nameable, Modelable, Taggable {

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
     * Tags.
     */
    private List<Tag> tags = new ArrayList<Tag>();

    /**
     * Time the object was last modified. Set by aspect.
     */
    // private Date lastModified;
    /**
     * List of waived issue detections (issue detector class simple names)
     */
    private List<String> waivedIssueDetections = new ArrayList<String>();

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

    /**
     * True if both null or they are equal.
     *
     * @param modelObject a model object
     * @param other       an other model object
     * @return a boolean
     */
    public static boolean areEqualOrNull( ModelObject modelObject, ModelObject other ) {
        return ( modelObject == null && other == null )
                || ( modelObject != null && other != null && modelObject.equals( other ) );
    }

    /**
     * {@inheritDoc}
     */
    public String getTypeName() {
        return getClass().getSimpleName().toLowerCase();
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    /**
     * Whether the model object is immutable.
     *
     * @return a boolean
     */
    public boolean isImmutable() {
        // Default
        return false;
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

    public String getDescription() {
        return description;
    }

    /**
     * Set the description of this object.
     *
     * @param val the description. Will set to empty string if null.
     */
    public void setDescription( String val ) {
        description = val == null ? "" : val;
    }

    /**
     * {@inheritDoc}
     */
    public ModelObject getModelObject() {
        return this;
    }

    @Override
    public List<Tag> getTags() {
        return tags;
    }

    public void setTags( List<Tag> tags ) {
        this.tags = tags;
    }

    public void addTag( String s ) {
        addTag( new Tag( s ) );
    }

    public void addTag (Tag tag ) {
        if ( !tags.contains( tag ) )
            tags.add( tag );
    }


    public void addTags( String s ) {
        for (Tag tag : Tag.tagsFromString( s ) ) {
            addTag( tag );
        }
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
                || obj != null
                && getClass().equals( obj.getClass() )
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
     * Return default hashCode.
     *
     * @return an integer
     */
    public int systemHashCode() {
        return super.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName();
    }

    public Date getLastModified() {
        // TODO implement last modified with aspect
        return new Date();
    }

    // TODO objectify strings if and when persistence is to work...
    public List<String> getWaivedIssueDetections() {
        return waivedIssueDetections;
    }

    public void setWaivedIssueDetections( List<String> waivedIssueDetections ) {
        this.waivedIssueDetections = waivedIssueDetections;
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
    public String getLabel() {
        return getName();
    }

    /**
     * Whether the model object is an entity
     *
     * @return a boolean
     */
    public boolean isEntity() {
        return false;
    }

    /**
     * Whether no properties other than name are set.
     *
     * @return a boolean
     */
    public boolean isUndefined() {
        return description.isEmpty();
    }


    /**
     * Whether this is an unknown entity.
     *
     * @return aboolean
     */
    public boolean isUnknown() {
        return equals( Actor.UNKNOWN )
                || equals( Event.UNKNOWN )
                || equals( Organization.UNKNOWN )
                || equals( Place.UNKNOWN )
                || equals( Role.UNKNOWN )
                || equals( Phase.UNKNOWN )
                || equals( TransmissionMedium.UNKNOWN );
    }

    /**
     * Clean up before removal.
     *
     * @param queryService a query service
     */
    public void beforeRemove( QueryService queryService ) {
        // DO nothing
    }

    /**
     * Get the type of model object.
     *
     * @return a string
     */
    public String getModelObjectType() {
        return getClass().getSimpleName();
    }

    /**
     * Has an image attachment.
     *
     * @return a boolean
     */
    public boolean hasImage() {
        return CollectionUtils.exists(
                getAttachments(),
                PredicateUtils.invokerPredicate( "isPicture" )
        );
    }

    /**
     * Get url of image attachment if any.
     *
     * @return a string
     */
    public String getImageUrl() {
        Attachment attachment = (Attachment) CollectionUtils.find(
                getAttachments(),
                PredicateUtils.invokerPredicate( "isPicture" )
        );
        if ( attachment != null ) {
            return attachment.getUrl();
        } else
            return null;
    }

    /**
     * Whether this can be represented by an icon.
     *
     * @return a boolean
     */
    public boolean isIconized() {
        return false;
    }

    /**
     * Two model objects are not null and equal.
     *
     * @param mo    a model object
     * @param other a model object
     * @return a boolean
     */
    public static boolean areIdentical( ModelObject mo, ModelObject other ) {
        return mo != null && other != null && mo.equals( other );
    }

    /**
     * Return a list of all classes of model object participating in reference counts.
     * UserIssue is purposely left out.
     *
     * @return a list of model object classes
     */
    public static List<Class> referencingClasses() {
        Class[] classes = {
                Actor.class, Event.class, Organization.class, Phase.class, Place.class, Phase.class,
                Role.class, Segment.class, Part.class, Flow.class, TransmissionMedium.class,
                Participation.class
        };
        return Arrays.asList( classes );
    }

    /**
     * Whether this references a given model object.
     *
     * @param mo a model object
     * @return a boolean
     */
    public boolean references( ModelObject mo ) {
        // default
        return false;
    }

    /**
     * Get type label.
     *
     * @return a string
     */
    public String getKindLabel() {
        return getTypeName();
    }

    /**
     * Whether a prohibiting policy is attached.
     *
     * @return a boolean
     */
    public boolean isProhibited() {
        return CollectionUtils.exists(
                getAttachments(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (Attachment) object ).isProhibition();
                    }
                }
        );
    }

    /**
     * Whether a mandating policy is attached.
     *
     * @return a boolean
     */
    public boolean isMandated() {
        return CollectionUtils.exists(
                getAttachments(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (Attachment) object ).isMandate();
                    }
                }
        );
    }

}
