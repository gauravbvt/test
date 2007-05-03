/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.project;

import java.util.List;

import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.elements.analysis.Activity;
import com.mindalliance.channels.data.elements.analysis.Exchange;
import com.mindalliance.channels.data.elements.analysis.Flow;
import com.mindalliance.channels.data.elements.analysis.Situation;
import com.mindalliance.channels.data.elements.scenario.Event;
import com.mindalliance.channels.data.elements.scenario.Task;
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
	private List<Task> tasks; // Specified activities that can cause further event and end products (knowledge and artefacts)
	// Analysis based on events and tasks, and assertions made about them, their components and the resources they specify.
	private List<Situation> situations; // Realized environments imposing time-limited constraints on policies and resources
	private List<Activity> activities; // Task executions by persons
	private List<Exchange> exchanges; // Information sharing requirements
	private List<Flow> flows; // Realized exchanges
	
	
}
