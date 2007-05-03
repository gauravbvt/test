/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import java.util.List;

import com.mindalliance.channels.data.Element;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * A problem uncovered about an element. An issue can have impacts.
 * @author jf
 *
 */
public class Issue extends AbstractJavaBean {
	
	private Element element;
	private List<Impact> impacts;

}
