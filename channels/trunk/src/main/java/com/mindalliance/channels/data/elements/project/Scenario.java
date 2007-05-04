/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.project;

import java.util.List;

import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.elements.assertions.Assertion;
import com.mindalliance.channels.data.elements.scenario.Activity;
import com.mindalliance.channels.data.elements.scenario.Communication;
import com.mindalliance.channels.data.elements.scenario.Event;
import com.mindalliance.channels.data.elements.scenario.Flow;
import com.mindalliance.channels.data.elements.scenario.Occurrence;
import com.mindalliance.channels.data.elements.scenario.Product;
import com.mindalliance.channels.data.elements.scenario.SharingNeed;
import com.mindalliance.channels.data.elements.scenario.Situation;
import com.mindalliance.channels.data.elements.scenario.Task;
/**
 * A hypothetical scenario caused by one or more incidents (events with causes external to the scenario) that drives
 * responses with their attendant information needs and productions, and generated events that drive further responses etc.
 * The analysis of a scenario uncovers situations, activities, sharing needs and flows, as well as issues (attached to elements).
 * @author jf
 *
 */
public class Scenario extends AbstractElement {
	
	private Model model;
	private List<Occurrence> occurrences; // What happens
	private List<Product> products;
	private List<Assertion> assertions;
	private List<SharingNeed> sharingNeeds; // Information sharing needs
	private List<Flow> flows; // Realized sharingNeeds
	
	public List<Event> getEvents() {
		return null;
	}
	public List<Task> getTasks() {
		return null;
	}
	public List<Activity> getActivities() {
		return null;
	}
	public List<Communication> getCommunications() {
		return null;
	}
	public List<Situation> getSituations() {
		return null;
	}
}
