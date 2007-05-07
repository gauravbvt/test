/*
 * Created on May 7, 2007
 *
 */
package com.mindalliance.channels.data;

import com.mindalliance.channels.data.support.Duration;

/**
 * Something that happens at some point after "time zero"
 * @author jf
 *
 */
public interface Timed {
	/**
	 * How much time passed since time zero and this.
	 * @return
	 */
	Duration getTime();

}
