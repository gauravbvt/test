/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.elements.assertions;

import com.mindalliance.channels.data.Assertable;
import com.mindalliance.channels.data.elements.scenario.AbstractScenarioElement;
import com.mindalliance.channels.util.GUID;

/**
 * An assertion about a scenario element.
 * 
 * @author jf
 */
abstract public class Assertion extends AbstractScenarioElement {

    private Assertable about;

    public Assertion() {
    }

    public Assertion( GUID guid ) {
        super( guid );
    }

    /**
     * Returns what the assertion is about.
     * 
     * @return
     */
    public Assertable getAbout() {
        return about;
    }

}
