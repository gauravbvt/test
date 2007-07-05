/*
 * Created on Apr 30, 2007
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.util.GUID;

/**
 * A problem uncovered about an element. An issue can have impacts.
 * 
 * @author jf
 */
public class Issue extends AbstractElement {

    private Element element;
    private List<Impact> impacts;

    public Issue() {
    }

    public Issue( GUID guid ) {
        super( guid );
    }

    /**
     * @return the element
     */
    public Element getElement() {
        return element;
    }

    /**
     * @param element the element to set
     */
    public void setElement( Element element ) {
        this.element = element;
    }

    /**
     * @return the impacts
     */
    public List<Impact> getImpacts() {
        return impacts;
    }

    /**
     * @param impacts the impacts to set
     */
    public void setImpacts( List<Impact> impacts ) {
        this.impacts = impacts;
    }

    /**
     * @param impact
     */
    public void addImpact( Impact impact ) {
        impacts.add( impact );
    }

    /**
     * @param impact
     */
    public void removeImpact( Impact impact ) {
        impacts.remove( impact );
    }
}
