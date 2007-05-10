/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.user;

import java.util.List;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.Element;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * Conversation, possibly private, about some element
 * @author jf
 *
 */
public class Conversation extends AbstractJavaBean {

	private Element topic;
	private List<User> guests; // private conversation
	private List<Message> messages; // top messages (not replies)
	/**
	 * @return the guests
	 */
	public List<User> getGuests() {
		return guests;
	}
	/**
	 * @param guests the guests to set
	 */
	public void setGuests(List<User> guests) {
		this.guests = guests;
	}
	/**
	 * 
	 * @param user
	 */
	public void addGuest(User user) {
		guests.add(user);
	}
	/**
	 * 
	 * @param user
	 */
	public void removeGuest(User user) {
		guests.remove(user);
	}
	/**
	 * @return the messages
	 */
	public List<Message> getMessages() {
		return messages;
	}
	/**
	 * @param messages the messages to set
	 */
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	/**
	 * 
	 * @param message
	 */
	public void addMessage(Message message) {
		messages.add(message);
	}
	/**
	 * 
	 * @param message
	 */
	public void removeMessage(Message message) {
		messages.remove(message);
	}
	/**
	 * @return the topic
	 */
	public Element getTopic() {
		return topic;
	}
	/**
	 * @param topic the topic to set
	 */
	public void setTopic(Element topic) {
		this.topic = topic;
	}
}
