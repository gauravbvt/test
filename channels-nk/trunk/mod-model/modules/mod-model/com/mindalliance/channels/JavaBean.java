// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;

/**
 * Standard behavior expected from a Java bean.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision: 227 $
 * @opt operations
 */
public interface JavaBean {

    /**
     * Add a property change listener.
     * @param listener the listener.
     */
    void addPropertyChangeListener( PropertyChangeListener listener );

    /**
     * Add a property change listener to a specific property.
     * @param propertyName the property
     * @param listener the listener
     */
    void addPropertyChangeListener(
            String propertyName, PropertyChangeListener listener );

    /**
     * Remove a property change listener.
     * @param listener the listener.
     */
    void removePropertyChangeListener( PropertyChangeListener listener );

    /**
     * Remove a property change listener from a specific property.
     * @param propertyName the property
     * @param listener the listener
     */
    void removePropertyChangeListener(
            String propertyName, PropertyChangeListener listener );

    /**
     * Add a vetoable change listener to a specific property.
     * @param propertyName the property
     * @param listener the listener
     */
    void addVetoableChangeListener(
            String propertyName, VetoableChangeListener listener );

    /**
     * Add a vetoable change listener.
     * @param listener the listener
     */
    void addVetoableChangeListener( VetoableChangeListener listener );

    /**
     * Remove a vetoable change listener to a specific property.
     * @param propertyName the property
     * @param listener the listener
     */
    void removeVetoableChangeListener(
            String propertyName, VetoableChangeListener listener );

    /**
     * Remove a vetoable change listener.
     * @param listener the listener
     */
    void removeVetoableChangeListener( VetoableChangeListener listener );

}
