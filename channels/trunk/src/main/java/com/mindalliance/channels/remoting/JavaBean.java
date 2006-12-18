// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.remoting;

import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;

/**
 * Standard behavior expected from a Java bean.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface JavaBean {

    // ----------------------------
    void addPropertyChangeListener( PropertyChangeListener listener );

    void addPropertyChangeListener(
            String propertyName, PropertyChangeListener listener );

    void removePropertyChangeListener( PropertyChangeListener listener );

    void removePropertyChangeListener(
            String propertyName, PropertyChangeListener listener );

    // ----------------------------
    void addVetoableChangeListener(
            String propertyName, VetoableChangeListener listener );

    void addVetoableChangeListener( VetoableChangeListener listener );

    void removeVetoableChangeListener(
            String propertyName, VetoableChangeListener listener );

    void removeVetoableChangeListener( VetoableChangeListener listener );

}