/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.user;

import java.util.List;

import com.mindalliance.channels.data.elements.Element;
import com.mindalliance.channels.data.elements.UserProfile;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * Conversation, possibly private, about some element
 * @author jf
 *
 */
public class Conversation extends AbstractJavaBean {

	private Element topic;
	private List<UserProfile> guests; // private conversation
	private List<Message> messages; // top messages (not replies)
}
