/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import com.mindalliance.channels.data.Caused;
import com.mindalliance.channels.data.Contactable;
import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.elements.resources.Channel;
import com.mindalliance.channels.util.GUID;

/**
 * The movement of information from a source to a recipient over one or more
 * interoperable system. A communication can be caused by another communication,
 * such as when a notification or request is passed along.
 * @author jf
 *
 */
abstract public class Communication extends AbstractOccurrence implements Caused {
	
	private Contactable source; // From whom
	private Contactable recipient; // To whom
	private Information information; // What's communicated
	private Channel sourceChannel; // Sending channel
	private Channel recipientChannel; // Receiving channel
	
	public Communication() {
		super();
	}
	public Communication(GUID guid) {
		super(guid);
	}
	/**
	 * @return the information
	 */
	public Information getInformation() {
		return information;
	}
	/**
	 * @param information the information to set
	 */
	public void setInformation(Information information) {
		this.information = information;
	}
	/**
	 * @return the recipient
	 */
	public Contactable getRecipient() {
		return recipient;
	}
	/**
	 * @param recipient the recipient to set
	 */
	public void setRecipient(Contactable recipient) {
		this.recipient = recipient;
	}
	/**
	 * @return the recipientChannel
	 */
	public Channel getRecipientChannel() {
		return recipientChannel;
	}
	/**
	 * @param recipientChannel the recipientChannel to set
	 */
	public void setRecipientChannel(Channel recipientChannel) {
		this.recipientChannel = recipientChannel;
	}
	/**
	 * @return the source
	 */
	public Contactable getSource() {
		return source;
	}
	/**
	 * @param source the source to set
	 */
	public void setSource(Contactable source) {
		this.source = source;
	}
	/**
	 * @return the sourceChannel
	 */
	public Channel getSourceChannel() {
		return sourceChannel;
	}
	/**
	 * @param sourceChannel the sourceChannel to set
	 */
	public void setSourceChannel(Channel sourceChannel) {
		this.sourceChannel = sourceChannel;
	}
	
}
