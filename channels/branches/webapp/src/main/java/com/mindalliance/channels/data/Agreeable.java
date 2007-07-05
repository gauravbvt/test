/*
 * Created on May 2, 2007
 */
package com.mindalliance.channels.data;

import java.util.List;

import com.mindalliance.channels.data.elements.assertions.AgreedTo;

/**
 * Something that can be agreed to.
 * 
 * @author jf
 */
public interface Agreeable extends Assertable {

    /**
     * Return AgreedTo assertions
     * 
     * @return
     */
    List<AgreedTo> getAgreedToAssertions();

}
