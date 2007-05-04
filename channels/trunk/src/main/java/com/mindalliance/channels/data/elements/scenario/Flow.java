/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.List;

import com.mindalliance.channels.data.elements.AbstractElement;

/** 
 * A sequence of commnunications that partially or completely fulfill a sharing need.
 * @author jf
 *
 */
public class Flow extends AbstractElement {
	
	private SharingNeed sharingNeed; // SharingNeed realized
	private List<Communication> communications; // Communications that realize the sharingNeed

}
