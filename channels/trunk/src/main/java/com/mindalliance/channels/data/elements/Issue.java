/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;



/**
 * A problem uncovered about an element. An issue can have impacts.
 * @author jf
 *
 */
public class Issue extends AbstractElement {
	
	private Element element;
	private List<Impact> impacts;

}
