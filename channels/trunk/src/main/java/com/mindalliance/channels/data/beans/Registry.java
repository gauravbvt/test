/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.beans;


import java.util.List;
import com.mindalliance.channels.data.elements.User;

/**
 * All user related data; their profiles and alerts/todos targeted at them.
 * @author jf
 *
 */
public class Registry extends AbstractQueryable {
	
	private List<User> users;
	private List<Conversation> conversations;
	private List<Alert> alerts;
	private List<Todo> todos;

	/**
	 * @return the alerts
	 */
	public List<Alert> getAlerts() {
		return alerts;
	}

	/**
	 * @param alerts the alerts to set
	 */
	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}

	/**
	 * @return the todos
	 */
	public List<Todo> getTodos() {
		return todos;
	}

	/**
	 * @param todos the todos to set
	 */
	public void setTodos(List<Todo> todos) {
		this.todos = todos;
	}

	/**
	 * @return the users
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

	/**
	 * @return the conversations
	 */
	public List<Conversation> getConversations() {
		return conversations;
	}

	/**
	 * @param conversations the conversations to set
	 */
	public void setConversations(List<Conversation> conversations) {
		this.conversations = conversations;
	}

}
