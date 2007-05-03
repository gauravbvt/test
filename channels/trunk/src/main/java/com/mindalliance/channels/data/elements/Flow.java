/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

/** 
 * A sequence of commnunications that partially or completely fulfill a required exchange.
 * A flow may depend on the completion of other flows to proceed.
 * @author jf
 *
 */
public class Flow extends AbstractElement {
	
	private Exchange exchange; // Exchange realized
	private List<Communication> communications; // Communications that realize the exchange
	private List<Flow> dependsOn; // 

}
