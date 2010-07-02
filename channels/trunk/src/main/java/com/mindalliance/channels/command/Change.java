package com.mindalliance.channels.command;

import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 * A description of state change.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 15, 2009
 * Time: 8:09:56 AM
 */
public class Change implements Serializable {

    /**
     * A kind of change.
     */
    public enum Type {
        /**
         * No change.
         */
        None,
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
         * Change failed because refresh needed.
         */
        NeedsRefresh

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
     * Reference to the changed model object.
     */
    private ModelObjectRef identifiableRef;
    /**
     * The property which value was changed, null if N/A.
     */
    private String property;
    /**
     * The change is caused by undoing or redoing.
     */
    private boolean undoing;
    /**
     * A script to run because of the change.
     */
    private String script;

    public Change() {
    }

    public Change( Type type ) {
        this.type = type;
    }

    public Change( Type type, Identifiable subject ) {
        this( type, subject, null );
    }

    public Change( Type type, Identifiable subject, String property ) {
        this.type = type;
        identifiableRef = new ModelObjectRef( subject );
        this.property = property;
    }

    public Identifiable getSubject( QueryService queryService ) {
        return identifiableRef == null ? null : identifiableRef.resolve( queryService );
    }

    public void setSubject( ModelObject subject ) {
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

    /**
     * Whether change is of an instance of a given class.
     *
     * @param clazz a class extending Identifiable
     * @return a boolean
     */
    public boolean isForInstanceOf( Class<? extends Identifiable> clazz ) {
        return identifiableRef != null && identifiableRef.isForInstanceOf( clazz );
    }

    /**
     * Get id of what has changed.
     * @return  a long
     */
    public long  getId() {
        return identifiableRef == null ? Long.MIN_VALUE : identifiableRef.getId();
    }

    /**
     * Get name of class of what changed.
     * @return  a string
     */
    public String getClassName() {
        return identifiableRef != null ? identifiableRef.getClassName() : null;
    }


    /**
     * Gets the updated property value or null if N/A.
     *
     * @param queryService a queryService
     * @return an object
     */
    public Object getChangedPropertyValue( QueryService queryService ) throws NotFoundException {
        Object value = null;
        if ( type == Type.Updated ) {
            try {
                value = PropertyUtils.getProperty( getSubject( queryService ), property );
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
        return isAspect() && this.isForProperty( property );
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
     * @return  a boolean
     */
    public boolean isExplained() {
        return type == Type.Explained;
    }

    /**
     * Type is unexplained.
     * @return  a boolean
     */
    public boolean isUnexplained() {
        return type == Type.Unexplained;
    }

    /**
     * Type is needs refresh.
     * @return  a boolean
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
