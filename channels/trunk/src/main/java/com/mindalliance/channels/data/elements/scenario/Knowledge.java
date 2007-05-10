/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.util.GUID;

/**
 * Created information.
 * @author jf
 *
 */
public class Knowledge extends Product {
	
	// What new information was created
	private Information information; // Information's contents aggregated from this knowledge's expanded types (e.g. diagnosis, treatment, prognostic)

	
	public Knowledge() {
		super();
	}

	public Knowledge(GUID guid) {
		super(guid);
	}

	/**
	 * @return the information
	 */
	public Information getInformation() {
		return information;
	}

	/**
	 * @param information the information to set
	 */
	public void setInformation(Information information) {
		this.information = information;
	}


}
