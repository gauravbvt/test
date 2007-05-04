/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.elements.resources;

import java.util.List;

import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.support.Pattern;

/**
 * An agreement by an organization to carry out specified exchanges with specified organizations.
 * @author jf
 *
 */
public class Agreement extends AbstractElement {
		
	private Organization organization; // Who makes the commitment
	private List<Information> information; // Description of information the organization agrees to share
	private Pattern<Organization> recipient; // Specification of the recipient organizations

}
