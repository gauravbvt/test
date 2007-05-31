// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements.resources;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.elements.scenario.Circumstance;
import com.mindalliance.channels.data.reference.Information;
import com.mindalliance.channels.util.GUID;

/**
 * Base implementation for resources.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class AbstractResource extends AbstractElement
    implements Resource {

    private boolean operational;

    /**
     * Default constructor.
     */
    public AbstractResource() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public AbstractResource( GUID guid ) {
        super( guid );
    }

    /**
     * Get the descriptor.
     */
    @PropertyOptions(ignore=true)
    public Information getDescriptor() {
        return getTypeSet().getDescriptor();
    }

    /**
     * Test if this resource is operational in a given
     * circumstance.
     * @param circumstance the circumstance
     */
    public boolean isOperationalIn( Circumstance circumstance ) {
        return false;
    }

    /**
     * Return the value of operational.
     */
    public boolean isOperational() {
        return this.operational;
    }

    /**
     * Set the value of operational.
     * @param operational The new value of operational
     */
    public void setOperational( boolean operational ) {
        this.operational = operational;
    }
}
