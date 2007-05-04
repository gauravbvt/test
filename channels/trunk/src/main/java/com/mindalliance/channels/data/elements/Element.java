/*
 * Created on Apr 25, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.data.Described;
import com.mindalliance.channels.data.Named;
import com.mindalliance.channels.data.Typed;
import com.mindalliance.channels.data.Unique;

/**
 * An event-enabled bean, stated or inferred, with a unique id, a name, a description, types and issues.
 * @author jf
 *
 */
public interface Element extends Unique, Typed, Named, Described, JavaBean {
	
	List<Issue> getIssues();
	
}
