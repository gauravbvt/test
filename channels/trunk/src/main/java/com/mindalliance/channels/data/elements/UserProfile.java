/*
 * Created on Apr 25, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;
import java.util.Properties;

import com.mindalliance.channels.data.elements.resources.Person;
import com.mindalliance.channels.util.AbstractJavaBean;
import com.mindalliance.channels.util.GUID;


/**
 * A user profile gives access to user-specific information and may relate the user to a person
 * that can be involved in scenarios via the roles it plays.
 * @author jf
 *
 */
public class UserProfile extends AbstractJavaBean {

	private String userName;
	private Properties preferences;
	private String emailAddress;
	private Person person; // set if the user is also a resource that can be involved in scenarios
	private List<GUID> readAlertGUIDs; // guids of alerts read
	private List<GUID> doneTodoGUIDs; // guids of todos done
	
}
