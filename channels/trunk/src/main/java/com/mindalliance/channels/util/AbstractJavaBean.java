// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.lang.reflect.Method;

import com.mindalliance.channels.JavaBean;

/**
 * Basic common java beans functionality.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:46 $
 */
public abstract class AbstractJavaBean implements JavaBean, Serializable {

    private transient PropertyChangeSupport pcs;
    private transient VetoableChangeSupport vcs;
    private transient BeanInfo beanInfo;

    // ----------------------------
    /**
     * Default constructor.
     */
    public AbstractJavaBean() {
    }

    // ----------------------------
    /**
     * Get the bean info for this class.
     * Cached for performance.
     */
    private synchronized BeanInfo getBeanInfo() {

        if ( this.beanInfo == null )
            try {
                this.beanInfo = Introspector.getBeanInfo( getClass() );

            } catch ( IntrospectionException e ) {
                throw new RuntimeException( e );
            }

        return this.beanInfo;
    }

    /**
     * Get a property descriptor from a getter or setter name.
     * For example "setX" would return property x.
     * @param accessor the setter name string
     * @return a property
     */
    protected PropertyDescriptor getPropertyDescriptor( String accessor ) {

        PropertyDescriptor[] pd = getBeanInfo().getPropertyDescriptors();

        for ( PropertyDescriptor p : pd ) {
            Method writeMethod = p.getWriteMethod();
            Method readMethod = p.getReadMethod();
            if ( writeMethod != null
                            && accessor.equals( writeMethod.getName() )
                    || readMethod != null
                            && accessor.equals( readMethod.getName() ) )
                return p;
        }

        return null;
    }

    // ----------------------------
    private synchronized PropertyChangeSupport getPcs() {
        if ( this.pcs == null )
            this.pcs = new PropertyChangeSupport( this );
        return this.pcs;
    }

    private synchronized VetoableChangeSupport getVcs() {
        if ( this.vcs == null )
            this.vcs = new VetoableChangeSupport( this );
        return this.vcs;
    }

    /**
     * Tell is this object has listeners (properties, vetos or both).
     */
    protected boolean hasListeners() {
        return ( this.pcs != null
                      && this.pcs.getPropertyChangeListeners().length > 0 )
            || ( this.vcs != null
                      && this.vcs.getVetoableChangeListeners().length > 0 );
    }

    /**
     * Add a property change listener.
     *
     * @param listener the listener
     */
    public void addPropertyChangeListener( PropertyChangeListener listener ) {

        getPcs().addPropertyChangeListener( listener );
    }

    /**
     * Add a property change listener on a property.
     *
     * @param listener the listener
     * @param propertyName the property
     */
    public void addPropertyChangeListener(
            String propertyName, PropertyChangeListener listener ) {

        getPcs().addPropertyChangeListener( propertyName, listener );
    }

    /**
     * Fire a property change event.
     *
     * @param propertyName the name of the property that changed.
     * @param oldValue the old value
     * @param newValue the new value
     */
    protected void firePropertyChange(
            String propertyName, Object oldValue, Object newValue ) {

        if ( this.pcs != null )
            this.pcs.firePropertyChange( propertyName, oldValue, newValue );
    }

    /**
     * Return the current listeners to all property change events.
     */
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return getPcs().getPropertyChangeListeners();
    }

    /**
     * Return the current listeners to a single property.
     * @param propertyName the property name
     */
    public PropertyChangeListener[] getPropertyChangeListeners(
            String propertyName ) {

        return getPcs().getPropertyChangeListeners( propertyName );
    }

    /**
     * Remove a property change listener.
     *
     * @param listener the listener to remove
     */
    public void removePropertyChangeListener(
            PropertyChangeListener listener ) {

        if ( this.pcs != null )
            this.pcs.removePropertyChangeListener( listener );
    }

    /**
     * Remove a property change listener from a property.
     *
     * @param propertyName the property
     * @param listener the listener to remove
     */
    public void removePropertyChangeListener(
            String propertyName, PropertyChangeListener listener ) {

        if ( this.pcs != null )
            this.pcs.removePropertyChangeListener( propertyName, listener );
    }

    /**
     * Test if there are listeners for a given property.
     * @param propertyName the property. If null, test if there are listeners
     * to all properties.
     */
    public boolean hasPropertyListeners( String propertyName ) {
        return this.pcs != null && this.pcs.hasListeners( propertyName );
    }

    // ----------------------------
    /**
     * Add a vetoable change listener for a given property.
     *
     * @param propertyName the property
     * @param listener the listener
     */
    public void addVetoableChangeListener(
            String propertyName, VetoableChangeListener listener ) {

        getVcs().addVetoableChangeListener( propertyName, listener );
    }

    /**
     * Add a vetoable change listener for all properties.
     *
     * @param listener the listener
     */
    public void addVetoableChangeListener( VetoableChangeListener listener ) {

        getVcs().addVetoableChangeListener( listener );
    }

    /**
     * Fire a vetoable change event.
     *
     * @param propertyName the property that is about to change
     * @param oldValue the previous value
     * @param newValue the proposed new value
     * @throws PropertyVetoException when a listener objects to the new value
     */
    protected void fireVetoableChange(
            String propertyName, Object oldValue, Object newValue )
        throws PropertyVetoException {

        if ( this.vcs != null )
            this.vcs.fireVetoableChange( propertyName, oldValue, newValue );
    }

    /**
     * Return currently registered listeners to all properties.
     */
    public VetoableChangeListener[] getVetoableChangeListeners() {
        return getVcs().getVetoableChangeListeners();
    }

    /**
     * Return currently registered listeners to a given property.
     * @param propertyName the property.
     */
    public VetoableChangeListener[] getVetoableChangeListeners(
            String propertyName ) {

        return getVcs().getVetoableChangeListeners( propertyName );
    }

    /**
     * Remove a listener from changes to a property.
     * @param propertyName the property
     * @param listener the listener
     */
    public void removeVetoableChangeListener(
            String propertyName, VetoableChangeListener listener ) {

        if ( this.vcs != null )
            this.vcs.removeVetoableChangeListener( propertyName, listener );
    }

    /**
     * Remove a listener from changes to all properties.
     * @param listener the listener
     */
    public void removeVetoableChangeListener(
            VetoableChangeListener listener ) {

        if ( this.vcs != null )
            this.vcs.removeVetoableChangeListener( listener );
    }

    /**
     * Test if there are vetoable listeners registered for a given property.
     * @param propertyName the property. If null, test is there are listeners
     * to all properties.
     */
    public boolean hasVetoableListeners( String propertyName ) {
        return this.vcs != null && this.vcs.hasListeners( propertyName );
    }
}
