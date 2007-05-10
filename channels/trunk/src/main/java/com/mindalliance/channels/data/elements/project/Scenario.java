/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.project;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.Occurrence;
import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.elements.assertions.Assertion;
import com.mindalliance.channels.data.elements.scenario.Activity;
import com.mindalliance.channels.data.elements.scenario.Communication;
import com.mindalliance.channels.data.elements.scenario.Event;
import com.mindalliance.channels.data.elements.scenario.Flow;
import com.mindalliance.channels.data.elements.scenario.Product;
import com.mindalliance.channels.data.elements.scenario.SharingNeed;
import com.mindalliance.channels.data.elements.scenario.Situation;
import com.mindalliance.channels.data.elements.scenario.Task;
import com.mindalliance.channels.util.GUID;
/**
 * A hypothetical scenario caused by one or more incidents (events with causes external to the scenario) that drives
 * responses with their attendant information needs and productions, and generated events that drive further responses etc.
 * The analysis of a scenario uncovers situations, activities, sharing needs and flows, as well as issues (attached to elements).
 * @author jf
 *
 */
public class Scenario extends AbstractElement {
	
	private Model model; // backpointer
	private List<Occurrence> occurrences; // What happens
	private List<Product> products; // What gets produced
	private List<Assertion> assertions; // What's asserted
	private List<SharingNeed> sharingNeeds; // Information sharing needs
	private List<Flow> flows; // Realized sharingNeeds
	
	public Scenario() {
		super();
	}
	public Scenario(GUID guid) {
		super(guid);
	}
	
	public List<Event> getEvents() {
		List<Event> events = new ArrayList<Event>();
		for (Occurrence occ : getOccurrences()) {
			if (occ instanceof Event) {
				events.add((Event)occ);
			}
		}
		return events;
	}
	public List<Task> getTasks() {
		List<Task> tasks = new ArrayList<Task>();
		for (Occurrence occ : getOccurrences()) {
			if (occ instanceof Task) {
				tasks.add((Task)occ);
			}
		}
		return tasks;
	}
	// TODO
	public List<Activity> getActivities() {
		return null;
	}
	// TODO
	public List<Communication> getCommunications() {
		return null;
	}
	// TODO
	public List<Situation> getSituations() {
		return null;
	}
	/**
	 * @return the assertions
	 */
	public List<Assertion> getAssertions() {
		return assertions;
	}
	/**
	 * @param assertions the assertions to set
	 */
	public void setAssertions(List<Assertion> assertions) {
		this.assertions = assertions;
	}
	/**
	 * 
	 * @param assertion
	 */
	public void addAssertion(Assertion assertion) {
		assertions.add(assertion);
	}
	/**
	 * 
	 * @param assertion
	 */
	public void removeAssertion(Assertion assertion) {
		assertions.remove(assertion);
	}
	/**
	 * @return the flows
	 */
	public List<Flow> getFlows() {
		return flows;
	}
	/**
	 * @param flows the flows to set
	 */
	public void setFlows(List<Flow> flows) {
		this.flows = flows;
	}
	/**
	 * 
	 * @param flow
	 */
	public void addFlows(Flow flow) {
		flows.add(flow);
	}
	/**
	 * 
	 * @param flow
	 */
	public void removeFlows(Flow flow) {
		flows.remove(flow);
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
	/**
	 * @return the occurrences
	 */
	public List<Occurrence> getOccurrences() {
		return occurrences;
	}
	/**
	 * @param occurrences the occurrences to set
	 */
	public void setOccurrences(List<Occurrence> occurrences) {
		this.occurrences = occurrences;
	}
	/**
	 * 
	 * @param occurrence
	 */
	public void addOccurrence(Occurrence occurrence) {
		occurrences.add(occurrence);
	}
	/**
	 * 
	 * @param occurrence
	 */
	public void removeOccurrence(Occurrence occurrence) {
		occurrences.remove(occurrence);
	}
	/**
	 * @return the products
	 */
	public List<Product> getProducts() {
		return products;
	}
	/**
	 * @param products the products to set
	 */
	public void setProducts(List<Product> products) {
		this.products = products;
	}
	/**
	 * 
	 * @param product
	 */
	public void addProduct(Product product) {
		products.add(product);
	}
	/**
	 * 
	 * @param product
	 */
	public void removeProduct(Product product) {
		products.remove(product);
	}
	/**
	 * @return the sharingNeeds
	 */
	public List<SharingNeed> getSharingNeeds() {
		return sharingNeeds;
	}
	/**
	 * @param sharingNeeds the sharingNeeds to set
	 */
	public void setSharingNeeds(List<SharingNeed> sharingNeeds) {
		this.sharingNeeds = sharingNeeds;
	}
	public void addSharingNeed(SharingNeed sharingNeed) {
		sharingNeeds.add(sharingNeed);
	}
	public void removeSharingNeed(SharingNeed sharingNeed) {
		sharingNeeds.remove(sharingNeed);
	}
}
