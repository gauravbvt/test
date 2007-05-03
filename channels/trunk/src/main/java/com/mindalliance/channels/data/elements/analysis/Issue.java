/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.elements.analysis;

import java.util.List;

import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.elements.Element;


/**
 * A problem uncovered about an element. An issue can have impacts.
 * @author jf
 *
 */
public class Issue extends AbstractElement {
	
	private Element element;
	private List<Impact> impacts;

}
