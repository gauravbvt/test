/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.List;

import com.mindalliance.channels.data.Caused;
import com.mindalliance.channels.data.components.Cause;
import com.mindalliance.channels.data.elements.assertions.Regulatable;
import com.mindalliance.channels.data.elements.assertions.Regulated;
import com.mindalliance.channels.data.elements.reference.Type;
import com.mindalliance.channels.data.support.TypeSet;

/**
 * A specification of activities. It is carried out by agents which specify roles or teams. 
 * A task is caused either by an event or a task (sub-task). 
 * It can create knowledge, produce artefacts and cause events and tasks. A Task represents
 * a separate activity for each role or team that matches one of its agents.
  * @author jf
 *
 */
public class Task extends AbstractOccurrence implements Caused, Regulatable {
	
	private List<Agent> agents;
	private TypeSet objectives = new TypeSet(Type.OBJECTIVE);

	public List<Regulated> getRegulatedAssertions() {
		return null;
	}
	

}
