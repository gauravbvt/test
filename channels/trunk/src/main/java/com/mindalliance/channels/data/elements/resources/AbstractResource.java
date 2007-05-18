/*
 * Created on Apr 30, 2007
 */
package com.mindalliance.channels.data.elements.resources;

import com.mindalliance.channels.data.Resource;
import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.elements.scenario.Circumstance;
import com.mindalliance.channels.util.GUID;

/**
 * Base implementation for resources
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class AbstractResource extends AbstractElement implements
        Resource {

    private boolean operational;

    public AbstractResource() {
        super();
    }

    public AbstractResource( GUID guid ) {
        super( guid );
    }

    /**
     * @param operational the operational to set
     */
    public void setOperational( boolean operational ) {
        this.operational = operational;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.data.Describable#getDescriptor()
     */
    public Information getDescriptor() {
        return getTypeSet().getDescriptor();
    }

    public boolean isOperationalIn( Circumstance circumstance ) {
        return false;
    }

    public boolean isOperational() {
        return false;
    }

}
