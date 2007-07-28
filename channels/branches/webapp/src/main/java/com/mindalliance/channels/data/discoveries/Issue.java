// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.discoveries;

import java.util.List;

import com.mindalliance.channels.data.profiles.InferableObject;
import com.mindalliance.channels.data.support.Element;
import com.mindalliance.channels.data.support.GUID;

/**
 * A problem uncovered about an element. An issue can have impacts.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Issue extends InferableObject {

    private Element element;
    private List<Impact> impacts;

    /**
     * Default constructor.
     */
    public Issue() {
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Issue( GUID guid ) {
        super( guid );
    }

    /**
     * Return the element.
     */
    public Element getElement() {
        return element;
    }

    /**
     * Set the element.
     * @param element the element
     */
    public void setElement( Element element ) {
        this.element = element;
    }

    /**
     * Return the impacts.
     */
    public List<Impact> getImpacts() {
        return impacts;
    }

    /**
     * Set the impacts.
     * @param impacts the impacts
     */
    public void setImpacts( List<Impact> impacts ) {
        this.impacts = impacts;
    }

    /**
     * Add an impact.
     * @param impact the impact
     */
    public void addImpact( Impact impact ) {
        impacts.add( impact );
    }

    /**
     * Remove an impact.
     * @param impact the impact
     */
    public void removeImpact( Impact impact ) {
        impacts.remove( impact );
    }
}
