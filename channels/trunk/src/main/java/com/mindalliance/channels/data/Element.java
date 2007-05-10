/*
 * Created on Apr 25, 2007
 *
 */
package com.mindalliance.channels.data;

import java.util.List;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.Issue;
import com.mindalliance.channels.data.support.TypeSet;

/**
 * An event-enabled bean, stated or inferred, with a unique id, a name, a description, types and issues.
 * @author jf
 *
 */
public interface Element extends Unique, Typed, Named, Described, Assertable, JavaBean {
	
	/**
	 * Get the issues attached to the element.
	 * @return
	 */
	List<Issue> getIssues();
	
	/**
	 * Get the domains to which this element belongs given the types it is categorized with.
	 * @return
	 */
	TypeSet getDomains();

	/**
	 * Return whether a given user has authority over the element.
	 * @param user
	 * @return
	 */
	boolean hasAuthority(User user);
	
}
