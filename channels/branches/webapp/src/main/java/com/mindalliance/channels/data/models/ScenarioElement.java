// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.models;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.data.support.Element;

/**
 * An element that exists only in the context of a storyline. All
 * storyline elements are knowable.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface ScenarioElement extends Element {

    /**
     * Return the storyline this element is in.
     */
    @PropertyOptions( ignore = true )
    Storyline getStoryline();

    /**
     * Set the storyline.
     * @param storyline the scenario
     */
    void setStoryline( Storyline storyline );
}
