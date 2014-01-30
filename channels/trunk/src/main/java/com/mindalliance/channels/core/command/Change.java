package com.mindalliance.channels.core.command;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.NotFoundException;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * A description of state change.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 15, 2009
 * Time: 8:09:56 AM
 */
public class Change implements Serializable {

    private static final String USER_ROLE_ID = "userRoleId";
    private static final String SECTION_ID = "sectionId";
    private static final String TOPIC_ID = "topicId";

    /**
     * A kind of change.
     */
    public enum Type {
        /**
         * No change.
         */
        None,
        /**
         * Failed.
         */
        Failed,
        /**
         * An update.
         */
        Updated,
        /**
         * Instance creation.
         */
        Added,
        /**
         * Instance destruction.
         */
        Removed,
        /**
         * Expansion requested.
         */
        Expanded,
        /**
         * Collapse requested.
         */
        Collapsed,
        /**
         * Composition changed.
         */
        Recomposed,
        /**
         * Unknown change.
         */
        Unknown,
        /**
         * Selection.
         */
        Selected,
        /**
         * Copy taken.
         */
        Copied,
        /**
         * View requested.
         */
        AspectViewed,
        /**
         * View replacement requested.
         */
        AspectReplaced,
        /**
         * View closed.
         */
        AspectClosed,
        /**
         * Maximized.
         */
        Maximized,
        /**
         * Minimized.
         */
        Minimized,
        /**
         * Explained.
         */
        Explained,
        /**
         * Not explained.
         */
        Unexplained,
        /**
         * Communication requested to other planner..
         */
        Communicated,
        /**
         * Change failed because refresh needed.
         */
        NeedsRefresh,
        /**
         * Change is a refresh.
         */
        Refresh,
        /**
         * Open the guide on a section and topic.
         */
        Guide,
        /**
         * Changes the in-context help topic.
         */
        HelpTopic;

    }


    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( Change.class );

    /**
     * The type of change.
     */
    private Type type = Type.Unknown;
    /**
     * An id not attached to a model object.
     */
    private Long id = null;
    /**
     * Reference to the changed model object.
     */
    private ModelObjectRef identifiableRef;
    /**
     * The property which value was changed, null if N/A.
     */
    private String property;
    /**
     * Property qualifiers.
     */
    private Map<String, Serializable> qualifiers = new HashMap<String, Serializable>();
    /**
     * The change is caused by undoing or redoing.
     */
    private boolean undoing;
    /**
     * A script to run because of the change.
     */
    private String script;
    /**
     * Message to user about the change.
     * Null means no message.
     */
    private String message;

    public Change() {
    }

    /**
     * Create a no-change with message.
     *
     * @param message a string
     * @return a change
     */
    public static Change message( String message ) {
        Change change = new Change( Change.Type.None );
        change.setMessage( message );
        return change;
    }

    /**
     * Creates a failed change with alert javascript.
     *
     * @param alert a string
     * @return a change
     */
    public static Change failed( String alert ) {
        Change change = new Change( Change.Type.Failed );
        change.setScript( "alert('Failed: " + alert + "');" );
        return change;
    }

    public Change( Type type ) {
        this.type = type;
    }

    public Change( Type type, Identifiable subject ) {
        this( type, subject, null );
    }

    public Change( Type type, String property ) {
        this.type = type;
        this.property = property;
    }

    public Change( Type type, Identifiable subject, String property ) {
        this.type = type;
        this.id = subject.getId();
        identifiableRef = new ModelObjectRef( subject );
        this.property = property;
    }

    public Change( Type type, Long id ) {
        this.type = type;
        this.id = id;
    }

    public Change( Type type, Long id, String property ) {
        this.type = type;
        this.id = id;
        this.property = property;
    }

    public static Change guide( String userRoleId, String sectionId, String topicId ) {
        Change change = new Change( Type.Guide );
        if ( userRoleId != null )
            change.addQualifier( "userRoleId", userRoleId );
        change.addQualifier( "sectionId", sectionId );
        change.addQualifier( "topicId", topicId );
        return change;
    }

    public static Change guide( String sectionId, String topicId ) {
        Change change = new Change( Type.Guide );
        change.addQualifier( "sectionId", sectionId );
        change.addQualifier( "topicId", topicId );
        return change;
    }

    public static Change helpTopic( String userRoleId, String sectionId, String topicId ) {
        Change change = new Change( Type.HelpTopic );
        change.addQualifier( "userRoleId", userRoleId );
        change.addQualifier( "sectionId", sectionId );
        change.addQualifier( "topicId", topicId );
        return change;
    }

    public String getUserRoleId() {
        return (String)getQualifier( USER_ROLE_ID );
    }

    public String getSectionId() {
        return (String)getQualifier( SECTION_ID );
    }

    public String getTopicId() {
        return (String)getQualifier( TOPIC_ID );
    }


    public boolean isByIdOnly() {
        return id != null && identifiableRef == null;
    }

    public Identifiable getSubject( CommunityService communityService ) {
        return identifiableRef == null ? null : identifiableRef.resolve( communityService );
    }

    public void setSubject( Identifiable subject ) {
        identifiableRef = new ModelObjectRef( subject );
    }

    public Type getType() {
        return type;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty( String property ) {
        this.property = property;
    }

    public Map<String, Serializable> getQualifiers() {
        return qualifiers;
    }

    public void setQualifiers( Map<String, Serializable> qualifiers ) {
        this.qualifiers = qualifiers;
    }

    /**
     * Add a named qualifier.
     *
     * @param name a string
     * @param val  a serializable
     */
    public void addQualifier( String name, Serializable val ) {
        qualifiers.put( name, val );
    }

    /**
     * Whether qualifier at index, if any, equals given value.
     *
     * @param name a string
     * @param val  a serializable
     * @return a boolean
     */
    public boolean hasQualifier( String name, String val ) {
        Serializable value = qualifiers.get( name );
        return value != null && value.equals( val );
    }

    /**
     * Whether a qualifier of a given name is set.
     *
     * @param name a string
     * @return a boolean
     */
    public boolean hasQualifier( String name ) {
        return qualifiers.containsKey( name );
    }

    /**
     * Get named qualifier, if any.
     *
     * @param name a string
     * @return a serializable
     */
    public Serializable getQualifier( String name ) {
        return qualifiers.get( name );

    }

    public boolean isUndoing() {
        return undoing;
    }

    public void setUndoing( boolean undoing ) {
        this.undoing = undoing;
    }

    public String getScript() {
        return script;
    }

    public void setScript( String script ) {
        this.script = script;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage( String message ) {
        this.message = message.replaceAll( "\"", "" );
    }

    /**
     * Whether change is of an instance of a given class.
     *
     * @param clazz a class extending Identifiable
     * @return a boolean
     */
    public boolean isForInstanceOf( Class<? extends Identifiable> clazz ) {
        return identifiableRef != null && identifiableRef.isForInstanceOf( clazz );
    }

    public void setId( Long id ) {
        this.id = id;
    }

    /**
     * Get id of what has changed.
     *
     * @return a long
     */
    public long getId() {
        if ( id == null ) {
            return identifiableRef == null ? Long.MIN_VALUE : identifiableRef.getId();
        } else {
            return id;
        }
    }

    /**
     * Get name of class of what changed.
     *
     * @return a string
     */
    public String getClassName() {
        return identifiableRef != null ? identifiableRef.getClassName() : null;
    }


    /**
     * Gets the updated property value or null if N/A.
     *
     * @param communityService a community service
     * @return an object
     * @throws com.mindalliance.channels.core.model.NotFoundException
     *          if fails to retrieve property value
     */
    public Object getChangedPropertyValue( CommunityService communityService ) throws NotFoundException {
        Object value = null;
        if ( type == Type.Updated ) {
            try {
                value = PropertyUtils.getProperty( getSubject( communityService ), property );
            } catch ( IllegalAccessException e ) {
                throw new RuntimeException( e );
            } catch ( InvocationTargetException e ) {
                LOG.warn( "Can't get changed property value." );
            } catch ( NoSuchMethodException e ) {
                throw new RuntimeException( e );
            }
        }
        return value;
    }

    /**
     * Whether type is Unknown.
     *
     * @return a boolean
     */
    public boolean isUnknown() {
        return type == Type.Unknown;
    }
    
    public boolean isRefresh() {
        return type == Type.Refresh;
    }

    /**
     * Whether type is Failed.
     *
     * @return a boolean
     */
    public boolean isFailed() {
        return type == Type.Failed;
    }

    /**
     * Whether type is Added.
     *
     * @return a boolean
     */
    public boolean isAdded() {
        return type == Type.Added;
    }

    /**
     * Whether type is Removed.
     *
     * @return a boolean
     */
    public boolean isRemoved() {
        return type == Type.Removed;
    }

    /**
     * Whether type is Updated.
     *
     * @return a boolean
     */
    public boolean isUpdated() {
        return type == Type.Updated;
    }

    /**
     * Whether type is Recomposed.
     *
     * @return a boolean
     */
    public boolean isRecomposed() {
        return type == Type.Recomposed;
    }

    /**
     * Whether type is Expanded.
     *
     * @return a boolean
     */
    public boolean isExpanded() {
        return type == Type.Expanded;
    }

    /**
     * Whether type is Collapsed.
     *
     * @return a boolean
     */
    public boolean isCollapsed() {
        return type == Type.Collapsed;
    }

    /**
     * Whether type is AspectViewed.
     *
     * @return a boolean
     */
    public boolean isAspectViewed() {
        return type == Type.AspectViewed;
    }

    /**
     * Whether type is AspectClosed.
     *
     * @return a boolean
     */
    public boolean isAspectClosed() {
        return type == Type.AspectClosed;
    }

    /**
     * Whether type is AspectClosed.
     *
     * @return a boolean
     */
    public boolean isAspectReplaced() {
        return type == Type.AspectReplaced;
    }

    /**
     * Whether type is guide.
     * @return a boolean
     */
    public boolean isGuide() {
        return type == Type.Guide;
    }

    /**
     * Whether the type is help topic.
     * @return a boolean
     */
    public boolean isHelpTopic() {
        return type == Type.HelpTopic;
    }

    /**
     * Whether type is Aspect related.
     *
     * @return a boolean
     */
    public boolean isAspect() {
        return isAspectViewed() || isAspectClosed() || isAspectReplaced();
    }


    /**
     * Whether a particular aspect (via property) is viewed.
     *
     * @param property a string
     * @return a boolean
     */
    public boolean isAspectViewed( String property ) {
        return isAspectViewed() && this.isForProperty( property );
    }

    /**
     * Whether a particular aspect (via property) is closed.
     *
     * @param property a string
     * @return a boolean
     */
    public boolean isAspectClosed( String property ) {
        return isAspectClosed() && this.isForProperty( property );
    }

    /**
     * Whether a particular aspect (via property) is replaced by another.
     *
     * @param property a string
     * @return a boolean
     */
    public boolean isAspectReplaced( String property ) {
        return isAspectReplaced() && this.isForProperty( property );
    }

    /**
     * Whether a particular aspect (via property) is viewed or closed.
     *
     * @param property a string
     * @return a boolean
     */
    public boolean isAspect( String property ) {
        return isAspect() && isForProperty( property );
    }

    /**
     * Whether type is Selected.
     *
     * @return a boolean
     */
    public boolean isSelected() {
        return type == Type.Selected;
    }

    /**
     * Whether type is Collapsed.
     *
     * @return a boolean
     */
    public boolean isExists() {
        return isAdded() || isRemoved();
    }

    /**
     * Whether either Expanded or Collapsed.
     *
     * @return a boolean
     */
    public boolean isDisplay() {
        return isExpanded() || isCollapsed() || isAspect();
    }

    /**
     * Whether a copy was taken.
     *
     * @return a boolean
     */
    public boolean isCopied() {
        return type == Type.Copied;
    }

    /**
     * Whether type is None.
     *
     * @return a boolean
     */
    public boolean isNone() {
        return type == Type.None;
    }

    /**
     * Something in the plan may have been modified.
     *
     * @return a boolean
     */
    public boolean isModified() {
        return isUnknown() || isExists() || isUpdated() || isRecomposed() || isUndoing();
    }

    /**
     * Type is maximized.
     *
     * @return a boolean
     */
    public boolean isMaximized() {
        return type == Type.Maximized;
    }

    /**
     * Type is maximized.
     *
     * @return a boolean
     */
    public boolean isMinimized() {
        return type == Type.Minimized;
    }

    /**
     * Type is explained.
     *
     * @return a boolean
     */
    public boolean isExplained() {
        return type == Type.Explained;
    }

    /**
     * Type is unexplained.
     *
     * @return a boolean
     */
    public boolean isUnexplained() {
        return type == Type.Unexplained;
    }

    /**
     * Type is communicated.
     *
     * @return a boolean
     */
    public boolean isCommunicated() {
        return type == Type.Communicated;
    }

    /**
     * Type is needs refresh.
     *
     * @return a boolean
     */
    public boolean isRefreshNeeded() {
        return type == Type.NeedsRefresh;
    }

    /**
     * Whether change is to a given property.
     *
     * @param prop a string
     * @return a boolean
     */
    public boolean isForProperty( String prop ) {
        return property != null && prop != null && property.equals( prop );
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( type.name() );
        sb.append( " " );
        sb.append( identifiableRef == null ? "" : identifiableRef.toString() );
        sb.append( property == null ? "" : ( " (" + property + ")" ) );
        return sb.toString();
    }
}
