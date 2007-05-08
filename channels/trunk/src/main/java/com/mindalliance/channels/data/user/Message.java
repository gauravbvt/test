/*
 * Created on May 3, 2007
 *
 */
package com.mindalliance.channels.data.user;

import java.util.List;

/**
 * A statement made in the context of a conversation, possibly in reply to another message
 * @author jf
 *
 */
public class Message extends Statement {

	private List<Message> replies; // can be null
	private boolean retracted = false; // true if issuer retracted it
	
}
