// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.util;

import java.io.Serializable;
import java.util.Date;

/**
 * A time interval.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 */
public class TimePeriod implements Serializable {

    private Date start;
    private Date end;

    /**
     * Default constructor.
     */
    public TimePeriod() {
    }

    /**
     * Return the value of end.
     */
    public Date getEnd() {
        return this.end;
    }

    /**
     * Set the value of end.
     * @param end The new value of end
     */
    public void setEnd( Date end ) {
        this.end = end;
    }

    /**
     * Return the value of start.
     */
    public Date getStart() {
        return this.start;
    }

    /**
     * Set the value of start.
     * @param start The new value of start
     */
    public void setStart( Date start ) {
        this.start = start;
    }
}
