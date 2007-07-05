/*
 * Created on May 1, 2007
 */
package com.mindalliance.channels.data;

import java.util.List;

import com.mindalliance.channels.data.elements.assertions.Regulated;

/**
 * Subject to policy regulations.
 * 
 * @author jf
 */
public interface Regulatable extends Assertable {

    /**
     * Return Regulated assertions
     * 
     * @return
     */
    List<Regulated> getRegulatedAssertions();

}
