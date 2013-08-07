package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.ChannelsLockable;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An object with name, id and description, comparable by its toString() values.
 */
public abstract class ModelObject
        extends AbstractAttachable
        implements Comparable<ModelObject>, Modelable, Taggable, ChannelsLockable {

    public static final List<String> CLASS_LABELS;

    public static enum Context {  // order is important: MODEL < COMMUNITY < USER
        MODEL,
        COMMUNITY,
        USER
    }

    static {
        CLASS_LABELS = classLabels();
    }

    /**
     * Unique id of this object.
     */
    private long id;

    /**
     * The uri of the context in which the object is persisted.
     */
    private String contextUri;

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
     * List of waived issue detections (issue detector class simple names)
     */
    private List<String> waivedIssueDetections = new ArrayList<String>();

    /**
     * Whether the model object is persisted.
     */
    private boolean persistent = true;

    /**
     * The context for the model object.
     */
    private Context context = Context.MODEL;   // default is model context


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

    public Context getContextType() {
        return context;
    }

    public boolean isInModel() {
        return context == Context.MODEL;
    }

    public void setInModel() {
        context = Context.MODEL;
    }

    public boolean isInCommunity() {
        return context == Context.COMMUNITY;
    }

    public void setInCommunity() {
        context = Context.COMMUNITY;
    }

    public boolean isInUser() {
        return context == Context.USER;
    }

    public void setInUser() {
        context = Context.USER;
    }

    public void setContextUri( String contextUri ) {
        this.contextUri = contextUri;
    }

    public String getContextUri() {
        return contextUri;
    }

    /**
     * True if both null or they are equal.
     *
     * @param modelObject a model object
     * @param other       an other model object
     * @return a boolean
     */
    public static boolean areEqualOrNull( ModelObject modelObject, ModelObject other ) {
        return modelObject == null && other == null
                || ( modelObject != null && other != null && modelObject.equals( other ) );
    }

    public String getTypeName() {
        return getClass().getSimpleName().toLowerCase();
    }

    @Override
    public boolean isModifiableInProduction() {
        return false;
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
        return description == null ? "" : description;
    }

    /**
     * Set the description of this object.
     *
     * @param val the description. Will set to empty string if null.
     */
    public void setDescription( String val ) {
        description = val == null ? "" : val;
    }

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

    public void setTagsAsString( String s ) {
        setTags( Tag.tagsFromString( s ) );
    }

    public String getTagsAsString() {
        return Tag.tagsToString( tags );
    }

    public void addTag( String s ) {
        addTag( new Tag( s ) );
    }

    public void addTag( Tag tag ) {
        if ( !tags.contains( tag ) )
            tags.add( tag );
    }


    public void addTags( String s ) {
        for ( Tag tag : Tag.tagsFromString( s ) ) {
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

    @Override
    public boolean equals( Object obj ) {
        return this == obj
                || obj != null
                && getClass().equals( obj.getClass() )
                && context == ( (ModelObject) obj ).getContextType()
                && id == ( (ModelObject) obj ).getId();
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash + 31 * Long.valueOf( id ).hashCode();
        hash = hash + 31 * getContextType().hashCode();
        return hash;
    }

    /**
     * Return default hashCode.
     *
     * @return an integer
     */
    public int systemHashCode() {
        return super.hashCode();
    }

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
                || equals( TransmissionMedium.UNKNOWN )
                || equals( Requirement.UNKNOWN )
                || equals( InfoProduct.UNKNOWN )
                || equals( InfoFormat.UNKNOWN )
                || equals( Function.UNKNOWN );
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
     * Has an image attachment.
     *
     * @return a boolean
     */
    public boolean hasHelp() {
        return CollectionUtils.exists(
                getAttachments(),
                PredicateUtils.invokerPredicate( "isHelp" )
        );
    }

    public boolean hasAttachmentOfType( final AttachmentImpl.Type attachmentType ) {
        return CollectionUtils.exists(
                getAttachments(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Attachment) object ).getType().equals( attachmentType );
                    }
                }
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
     * Plan and UserIssue are purposely left out.
     *
     * @return a list of model object classes
     */
    public static List<Class> referencingClasses() {
        Class[] classes = {
                Plan.class, PlanCommunity.class,  // added
                Actor.class, Event.class, Organization.class, Phase.class, Place.class, Phase.class,
                Role.class, Segment.class, Part.class, Flow.class, TransmissionMedium.class,
                Requirement.class, InfoFormat.class, InfoProduct.class, Function.class
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
    public boolean hasProhibitionPolicy() {
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
    public boolean hasMandatingPolicy() {
        return CollectionUtils.exists(
                getAttachments(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (Attachment) object ).isMandate();
                    }
                }
        );
    }

    public static boolean isNullOrUnknown( ModelObject modelObject ) {
        return modelObject == null || modelObject.isUnknown();
    }

    public Map<String, Object> mapState() {
        Map<String, Object> state = new HashMap<String, Object>();
        state.put( "name", getName() );
        state.put( "description", getDescription() );
        state.put( "tags", Tag.tagsToString( getTags() ) );
        state.put( "attachments", new ArrayList<Attachment>( getAttachments() ) );
        state.put( "waivedIssueDetections", new ArrayList<String>( getWaivedIssueDetections() ) );
        return state;
    }

    @SuppressWarnings("unchecked")
    public void initFromMap( Map<String, Object> state, CommunityService communityService ) {
        setName( (String) state.get( "name" ) );
        setDescription( (String) state.get( "description" ) );
        setTagsAsString( (String) state.get( "tags" ) );
        setAttachments( new ArrayList<Attachment>( (List<Attachment>) state.get( "attachments" ) ) );
        setWaivedIssueDetections( (ArrayList<String>) state.get( "waivedIssueDetections" ) );
    }

    public static List<String> classLabels() {
        List<String> typeLabels = new ArrayList<String>();
        typeLabels.add( Segment.classLabel() );
        typeLabels.add( Requirement.classLabel() );
        typeLabels.add( Part.classLabel() );
        typeLabels.add( Flow.classLabel() );
        typeLabels.addAll( ModelEntity.classLabels() );
        Collections.sort( typeLabels );
        List<String> results = new ArrayList<String>();
        results.add( Plan.classLabel() );
        results.addAll( typeLabels );
        return results;
    }

    public String getClassLabel() {
        return getTypeName(); // default
    }

     public abstract boolean isSegmentObject();

    public static boolean isUnknownModelObject( Identifiable identifiable ) {
        return identifiable instanceof ModelObject && ( (ModelObject) identifiable ).isUnknown();
    }

    public boolean isTransient() {
        return !persistent;
    }

    public void makeTransient() {
        persistent = false;
    }
}
