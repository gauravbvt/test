// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.cap.remoting;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

/**
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
interface RemoteListener 
        extends PropertyChangeListener, VetoableChangeListener {

    /* (non-Javadoc)
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    public abstract void propertyChange( PropertyChangeEvent evt );

    /* (non-Javadoc)
     * @see VetoableChangeListener#vetoableChange(PropertyChangeEvent)
     */
    public abstract void vetoableChange( PropertyChangeEvent evt ) 
        throws PropertyVetoException ;

}