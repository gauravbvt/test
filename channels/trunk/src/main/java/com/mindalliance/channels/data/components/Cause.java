/*
 * Created on May 1, 2007
 */
package com.mindalliance.channels.data.components;

import com.mindalliance.channels.data.Occurrence;
import com.mindalliance.channels.util.AbstractJavaBean;
import com.mindalliance.channels.data.support.Duration;

/**
 * The cause of an occurrence which is either the start or end of an
 * occurrence. The creation of the effect may be delayed (e.g. event
 * begins 2 minutes after the task starts)
 * 
 * @author jf
 */
public class Cause<T extends Occurrence> extends AbstractJavaBean {

    private boolean start; // Is the cause the start of the
                            // occurrence or the end?
    private Duration delay; // How long after the start or end of the
                            // occurrence before the event begins
    private T occurrence;

    public Cause() {
        start = true;
        delay = new Duration();
    }

    /**
     * Get the occurrence that caused this.
     * 
     * @return
     */
    public T getOccurrence() {
        return occurrence;
    }

    /**
     * @param occurrence the occurrence to set
     */
    public void setOccurrence( T occurrence ) {
        this.occurrence = occurrence;
    }

    /**
     * @return the delay
     */
    public Duration getDelay() {
        return delay;
    }

    /**
     * @param delay the delay to set
     */
    public void setDelay( Duration delay ) {
        this.delay = delay;
    }

    /**
     * @return the isStart
     */
    public boolean isStart() {
        return start == true;
    }

    /**
     * @param isStart the isStart to set
     */
    public void setStart( boolean start ) {
        this.start = start;
    }

    public Duration getTime() {
        if ( occurrence == null )
            return delay;
        else {
            Duration occTime = occurrence.getTime();
            if ( start )
                return occTime.add( delay );
            else
                return occTime.add( occurrence.getDuration().add( delay ) );
        }
    }

}
