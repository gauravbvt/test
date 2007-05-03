/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;
/**
 * A hypothetical scenario caused by one or more incidents (events with causes external to the scenario) that drives
 * responses with their attendant information needs and productions, and generated events that drive further responses etc.
 * The analysis of a scenario uncovers situations, activities, exchanges and flows, as well as issues (attached to elements).
 * @author jf
 *
 */
public class Scenario extends AbstractElement {
	
	private Model model;
	// Construction
	private List<Event> events; // Any event or task without cause happens at time zero + delay
	private List<Task> tasks; // Responses by specified agents that cause further event and produce information
	// Analysis based on events and tasks, and assertions made about them, their components and the resources they specify.
	private List<Activity> activities; // Task executions by persons
	private List<Situation> situations; // Realized environments imposing time-limited constraints on policies and resources
	private List<Exchange> exchanges; // Information sharing requirements
	private List<Flow> flows; // Realized exchanges
	
	/**
	 * @return the events
	 */
	public List<Event> getEvents() {
		return events;
	}
	/**
	 * @param events the events to set
	 */
	public void setEvents(List<Event> events) {
		this.events = events;
	}
	/**
	 * @return the exchanges
	 */
	public List<Exchange> getExchanges() {
		return exchanges;
	}
	/**
	 * @param exchanges the exchanges to set
	 */
	public void setExchanges(List<Exchange> exchanges) {
		this.exchanges = exchanges;
	}
	/**
	 * @return the situations
	 */
	public List<Situation> getSituations() {
		return situations;
	}
	/**
	 * @param situations the situations to set
	 */
	public void setSituations(List<Situation> situations) {
		this.situations = situations;
	}
	/**
	 * @return the tasks
	 */
	public List<Task> getTasks() {
		return tasks;
	}
	/**
	 * @param tasks the tasks to set
	 */
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}
	
}
