/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.List;

import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.util.GUID;

/** 
 * A sequence of commnunications that partially or completely fulfill a sharing need.
 * @author jf
 *
 */
public class Flow extends AbstractElement {
	
	private SharingNeed sharingNeed; // SharingNeed realized
	private List<Communication> communications; // Communications that realize the sharingNeed
	
	public Flow() {
		super();
	}
	public Flow(GUID guid) {
		super(guid);
	}
	/**
	 * @return the communications
	 */
	public List<Communication> getCommunications() {
		return communications;
	}
	/**
	 * @param communications the communications to set
	 */
	public void setCommunications(List<Communication> communications) {
		this.communications = communications;
	}
	/**
	 * @return the sharingNeed
	 */
	public SharingNeed getSharingNeed() {
		return sharingNeed;
	}
	/**
	 * @param sharingNeed the sharingNeed to set
	 */
	public void setSharingNeed(SharingNeed sharingNeed) {
		this.sharingNeed = sharingNeed;
	}

}
