/*
 * Created on May 1, 2007
 */
package com.mindalliance.channels.data;

import java.util.List;

import com.mindalliance.channels.data.elements.assertions.OptedOut;

/**
 * Can be opted out of by roles, teams and persons so that it does not
 * apply to them.
 * 
 * @author jf
 */
public interface OptOutable extends Assertable {

    /**
     * Return OptedOut assertions
     * 
     * @return
     */
    List<OptedOut> getOptedOutAssertions();

}
