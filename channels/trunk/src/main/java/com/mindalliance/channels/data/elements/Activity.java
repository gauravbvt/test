/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.data.Personable;

/**
 * The execution of a task by one or more persons. 
 * Activities are created during scenario analysis by matching the agents of a task 
 * with persons within the project's scope.
 * A single task can imply many activities that carry it out.
 * @author jf
 *
 */
public class Activity extends AbstractOccurrence {
	
	private Task task;
	private List<Personable> actors;

}
