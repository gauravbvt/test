/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.data.Caused;
import com.mindalliance.channels.data.Regulatable;
import com.mindalliance.channels.data.TypeSet;
import com.mindalliance.channels.data.beans.Cause;
import com.mindalliance.channels.data.beans.Regulated;
import com.mindalliance.channels.data.beans.Type;

/**
 * A specification of activities. It is carried out by agents which specify roles or teams. 
 * A task is caused either by an event or a task (sub-task). 
 * It can create knowledge, produce artefacts and cause events and tasks. A Task represents
 * a separate activity for each role or team that matches one of its agents.
  * @author jf
 *
 */
public class Task extends AbstractOccurrence implements Caused, Regulatable {
	
	private Cause cause; // The task is in response to an event or caused by another task (sub-task)
	private List<Agent> agents;
	private TypeSet objectives = new TypeSet(Type.OBJECTIVE);
	private List<Regulated> regulatedAssertions;

	public List<Regulated> getRegulatedAssertions() {
		return regulatedAssertions;
	}
	
	/**
	 * Get cause
	 */
	public Cause getCause() {
		return cause;
	}

}
