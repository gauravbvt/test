// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.remoting;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

public class TestListener implements PropertyChangeListener,
        VetoableChangeListener {

    private int propCount;
    private int vetoCount;
    private PropertyChangeEvent lastProp;
    private PropertyChangeEvent lastVeto;

    public void reset() {
        this.propCount = 0;
        this.vetoCount = 0;
        this.lastProp = null;
        this.lastVeto = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    public void propertyChange(
            PropertyChangeEvent evt ) {
        this.propCount++;
        this.lastProp = evt;
    }

    /*
     * (non-Javadoc)
     *
     * @see VetoableChangeListener#vetoableChange(PropertyChangeEvent)
     */
    public void vetoableChange(
            PropertyChangeEvent evt ) throws PropertyVetoException {

        this.vetoCount++;
        this.lastVeto = evt;
    }

    public PropertyChangeEvent getLastProp() {
        return this.lastProp;
    }

    public PropertyChangeEvent getLastVeto() {
        return this.lastVeto;
    }

    public int getPropCount() {
        return this.propCount;
    }

    public int getVetoCount() {
        return this.vetoCount;
    }
}