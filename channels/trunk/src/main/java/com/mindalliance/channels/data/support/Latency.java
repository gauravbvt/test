/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data.support;

import java.io.Serializable;

import com.mindalliance.channels.data.support.Duration;

/**
 * A composite measure of latency, with minimum, average and maximum.
 * Minimum and maximum are used to compute best cases and worst cases.
 * @author jf
 *
 */
public class Latency implements Serializable {
	
	private Duration minimum;
	private Duration average;
	private Duration maximum;
	
	public Duration getExpectedLatency() {
		return average;
	}

}
