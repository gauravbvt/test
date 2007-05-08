/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.user;

import java.util.List;

/**
 * An action item targeting selected users and that may depend on other action items being completed.
 * @author jf
 *
 */
public class Todo extends Announcement {
	
	private List<Todo> dependencies;

}
