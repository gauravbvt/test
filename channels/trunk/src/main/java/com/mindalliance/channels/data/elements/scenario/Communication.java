/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import com.mindalliance.channels.data.Caused;
import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.elements.resources.Channel;
import com.mindalliance.channels.data.elements.resources.Contactable;

/**
 * The movement of information from a source to a recipient over one or more
 * interoperable channels. A communication can be caused by another communication,
 * such as when a notification or request is passed along.
 * @author jf
 *
 */
abstract public class Communication extends AbstractOccurrence implements Caused {
	
	private Contactable source; 
	private Contactable recipient;
	private Information information; // What's communicated
	private Channel sourceChannel;
	private Channel recipientChannel;
	
}
