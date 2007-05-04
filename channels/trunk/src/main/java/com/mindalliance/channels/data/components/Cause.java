/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.components;

import com.mindalliance.channels.data.elements.scenario.Occurrence;
import com.mindalliance.channels.util.AbstractJavaBean;
import com.mindalliance.channels.util.Duration;

/**
 * The cause of an occurrence which is either the start or end of an occurrence.
 * The creation of the effect may be delayed (e.g. event begins 2 minutes after the task starts)
 * @author jf
 *
 */
abstract public class Cause<T extends Occurrence> extends AbstractJavaBean {

	private boolean isStart; // Is the cause the start of the occurrence or the end?
	private Duration delay; // How long after the start or end of the occurrence before the event begins
	
	abstract public T getOccurrence();
	
}
