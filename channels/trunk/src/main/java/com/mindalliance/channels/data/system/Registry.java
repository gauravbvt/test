/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.system;


import java.util.List;
import java.util.Map;

import com.mindalliance.channels.data.elements.UserProfile;
import com.mindalliance.channels.data.user.Alert;
import com.mindalliance.channels.data.user.Conversation;
import com.mindalliance.channels.data.user.Todo;

/**
 * All user related data; their profiles and alerts/todos targeted at them.
 * @author jf
 *
 */
public class Registry extends AbstractQueryable {
	
	private Map<String,UserProfile> userProfiles; // username => user profile
	private List<Conversation> conversations;
	private List<Alert> alerts;
	private List<Todo> todos;


}
