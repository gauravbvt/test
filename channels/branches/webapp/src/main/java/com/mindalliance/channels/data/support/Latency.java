/*
 * Created on May 2, 2007
 */
package com.mindalliance.channels.data.support;

import java.io.Serializable;

import com.beanview.annotation.PropertyOptions;

/**
 * A composite measure of latency, with minimum, average and maximum.
 * Minimum and maximum are used to compute best cases and worst cases.
 * 
 * @author jf
 */
public class Latency implements Serializable {

    private Duration minimum;
    private Duration average;
    private Duration maximum;

    @PropertyOptions(ignore=true)
    public Duration getExpectedLatency() {
        return average;
    }

    /**
     * @return the average
     */
    public Duration getAverage() {
        return average;
    }

    /**
     * @param average the average to set
     */
    public void setAverage( Duration average ) {
        this.average = average;
    }

    /**
     * @return the maximum
     */
    public Duration getMaximum() {
        return maximum;
    }

    /**
     * @param maximum the maximum to set
     */
    public void setMaximum( Duration maximum ) {
        this.maximum = maximum;
    }

    /**
     * @return the minimum
     */
    public Duration getMinimum() {
        return minimum;
    }

    /**
     * @param minimum the minimum to set
     */
    public void setMinimum( Duration minimum ) {
        this.minimum = minimum;
    }

}
