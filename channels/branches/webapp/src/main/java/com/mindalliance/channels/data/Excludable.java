/*
 * Created on May 7, 2007
 */
package com.mindalliance.channels.data;

import java.util.List;

import com.mindalliance.channels.data.elements.assertions.Excluded;

/**
 * Something which can not co-exist with something else.
 * 
 * @author jf
 */
public interface Excludable extends Assertable {

    /**
     * Return Excluded assertions.
     * 
     * @return
     */
    List<Excluded> getExcludedAssertions();

}
