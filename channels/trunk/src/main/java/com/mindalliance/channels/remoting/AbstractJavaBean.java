// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.remoting;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;

/**
 * Basic common java beans functionality.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class AbstractJavaBean implements JavaBean {

    private transient PropertyChangeSupport pcs;
    private transient VetoableChangeSupport vcs;

    // ----------------------------
    /**
     * Default constructor.
     */
    public AbstractJavaBean() {
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

    // ----------------------------
    /**
     * Tell is this object has listeners (properties, vetos or both).
     */
    protected boolean hasListeners() {
        return ( this.pcs != null
                    && this.pcs.getPropertyChangeListeners().length > 0 )
            || ( this.vcs != null
                    && this.vcs.getVetoableChangeListeners().length > 0 );
    }

    // ----------------------------
    public void addPropertyChangeListener(
            PropertyChangeListener listener ) {

        getPcs().addPropertyChangeListener( listener );
    }

    public void addPropertyChangeListener(
            String propertyName, PropertyChangeListener listener ) {

        getPcs().addPropertyChangeListener( propertyName, listener );
    }

    protected void fireIndexedPropertyChange(
            String propertyName, int index,
            boolean oldValue, boolean newValue ) {

        if ( this.pcs != null )
            this.pcs.fireIndexedPropertyChange(
                    propertyName, index, oldValue, newValue );
    }

    protected void fireIndexedPropertyChange(
            String propertyName, int index, int oldValue, int newValue ) {

        if ( this.pcs != null )
            this.pcs.fireIndexedPropertyChange(
                    propertyName, index, oldValue, newValue );
    }

    protected void fireIndexedPropertyChange(
            String propertyName, int index,
            Object oldValue, Object newValue ) {

        if ( this.pcs != null )
            this.pcs.fireIndexedPropertyChange(
                    propertyName, index, oldValue, newValue );
    }

    protected void firePropertyChange(
            PropertyChangeEvent evt ) {
        if ( this.pcs != null )
            this.pcs.firePropertyChange( evt );
    }

    protected void firePropertyChange(
            String propertyName, boolean oldValue, boolean newValue ) {

        if ( this.pcs != null )
            this.pcs.firePropertyChange( propertyName, oldValue, newValue );
    }

    protected void firePropertyChange(
            String propertyName, int oldValue, int newValue ) {

        if ( this.pcs != null )
            this.pcs.firePropertyChange( propertyName, oldValue, newValue );
    }

    protected void firePropertyChange(
            String propertyName, Object oldValue, Object newValue ) {

        if ( this.pcs != null )
            this.pcs.firePropertyChange( propertyName, oldValue, newValue );
    }

    protected PropertyChangeListener[] getPropertyChangeListeners() {
        return getPcs().getPropertyChangeListeners();
    }

    protected PropertyChangeListener[] getPropertyChangeListeners(
            String propertyName ) {

        return getPcs().getPropertyChangeListeners( propertyName );
    }

    public void removePropertyChangeListener(
            PropertyChangeListener listener ) {

        if ( this.pcs != null )
            this.pcs.removePropertyChangeListener( listener );
    }

    public void removePropertyChangeListener(
            String propertyName, PropertyChangeListener listener ) {

        if ( this.pcs != null )
            this.pcs.removePropertyChangeListener( propertyName, listener );
    }

    protected boolean hasPropertyListeners( String propertyName ) {
        return this.pcs != null && this.pcs.hasListeners( propertyName );
    }

    // ----------------------------
    public void addVetoableChangeListener(
            String propertyName, VetoableChangeListener listener ) {

        getVcs().addVetoableChangeListener( propertyName, listener );
    }

    public void addVetoableChangeListener(
            VetoableChangeListener listener ) {

        getVcs().addVetoableChangeListener( listener );
    }

    protected void fireVetoableChange(
            PropertyChangeEvent evt ) throws PropertyVetoException {

        if ( this.vcs != null )
            this.vcs.fireVetoableChange( evt );
    }

    protected void fireVetoableChange(
            String propertyName, boolean oldValue, boolean newValue )
                throws PropertyVetoException {

        if ( this.vcs != null )
            this.vcs.fireVetoableChange( propertyName, oldValue, newValue );
    }

    protected void fireVetoableChange(
            String propertyName, int oldValue, int newValue )
                throws PropertyVetoException {

        if ( this.vcs != null )
            this.vcs.fireVetoableChange( propertyName, oldValue, newValue );
    }

    protected void fireVetoableChange(
            String propertyName, Object oldValue, Object newValue )
                throws PropertyVetoException {

        if ( this.vcs != null )
            this.vcs.fireVetoableChange( propertyName, oldValue, newValue );
    }

    protected VetoableChangeListener[] getVetoableChangeListeners() {
        return getVcs().getVetoableChangeListeners();
    }

    protected VetoableChangeListener[] getVetoableChangeListeners(
            String propertyName ) {
        return getVcs().getVetoableChangeListeners( propertyName );
    }

    public void removeVetoableChangeListener(
            String propertyName, VetoableChangeListener listener ) {

        if ( this.vcs != null )
            this.vcs.removeVetoableChangeListener( propertyName, listener );
    }

    public void removeVetoableChangeListener(
            VetoableChangeListener listener ) {

        if ( this.vcs != null )
            this.vcs.removeVetoableChangeListener( listener );
    }

    protected boolean hasVetoableListeners( String propertyName ) {
        return this.vcs != null && this.vcs.hasListeners( propertyName );
    }
}
