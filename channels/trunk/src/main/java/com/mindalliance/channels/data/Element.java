/*
 * Created on Apr 25, 2007
 *
 */
package com.mindalliance.channels.data;

import java.util.List;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.data.beans.Issue;

/**
 * An event-enabled bean, stated or inferred, with a unique id, a name, a description, types and issues.
 * @author jf
 *
 */
public interface Element extends Unique, Named, Described, Typed, JavaBean {
	
	List<Issue> getIssues();
	
}
