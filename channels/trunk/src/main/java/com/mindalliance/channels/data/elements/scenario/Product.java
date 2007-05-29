/*
 * Created on May 2, 2007
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.List;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.data.Storable;
import com.mindalliance.channels.data.components.Cause;
import com.mindalliance.channels.data.components.Caused;
import com.mindalliance.channels.data.elements.Occurrence;
import com.mindalliance.channels.data.elements.assertions.StoredIn;
import com.mindalliance.channels.data.support.Duration;
import com.mindalliance.channels.util.GUID;

abstract public class Product extends AbstractScenarioElement implements
        Caused, Storable {

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.data.Storable#getStoredInAssertions()
     */
    private Cause<Task> cause;

    public Product() {
        super();
    }

    public Product( GUID guid ) {
        super( guid );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.data.Timed#getDuration()
     */
    public Duration getDuration() {
        return Duration.NONE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.data.Timed#isAfter(com.mindalliance.channels.data.Occurrence)
     */
    public boolean isAfter( Occurrence occurrence ) {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.data.Timed#isBefore(com.mindalliance.channels.data.Occurrence)
     */
    public boolean isBefore( Occurrence occurrence ) {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.data.Timed#isDuring(com.mindalliance.channels.data.Occurrence)
     */
    public boolean isDuring( Occurrence occurrence ) {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.data.Timed#getTime()
     */
    public Duration getTime() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.data.Storable#getStoredInAssertions()
     */
    @PropertyOptions(ignore=true)
    public List<StoredIn> getStoredInAssertions() {
        return null;
    }

    public Cause<Task> getCause() {
        return (Cause<Task>) cause;
    }

    /**
     * @param cause the cause to set
     */
    public void setCause( Cause<Task> cause ) {
        this.cause = cause;
    }

}
