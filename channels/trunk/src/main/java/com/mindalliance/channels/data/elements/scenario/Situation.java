/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.List;

import com.mindalliance.channels.data.elements.reference.Environment;
import com.mindalliance.channels.data.elements.reference.Policy;
import com.mindalliance.channels.data.elements.resources.Resource;

/**
 * A realized environment, triggered by an event and in effect for the duration of the event.
 * For the duration of the situation, the operationality of some resources and the activation of some
 * policies differ from the default case. 
 * 
 * Situations can be composed (commutatively) when they co-occur
 * to determine what the resulting resource and policy environment is.
 * @author jf
 *
 */
public class Situation extends AbstractOccurrence {
	
	/**
	 * The realized environment 
	 */
	private Environment environment; // set for "atomic" situations
	private Event trigger; // set for "atomic" situations
	private List<Situation> components; // set for "composed" situations
	
	public Situation composeWith(Situation situation) {
		return null;
	}
	
	public boolean resourceOperational(Resource resource) {
		return false;
	}
	
	public boolean policyInEffect(Policy policy) {
		return false;
	}

}
