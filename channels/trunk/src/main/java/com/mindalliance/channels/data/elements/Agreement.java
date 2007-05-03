/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.data.elements.analysis.Exchange;
import com.mindalliance.channels.data.elements.resources.Organization;
import com.mindalliance.channels.data.support.Pattern;

/**
 * An agreement between organizations to carry out specified exchanges.
 * @author jf
 *
 */
public class Agreement extends AbstractElement {
	
	private List<Pattern<Exchange>> exchangeSpecs; // exchange patterns
	private Organization source;
	private Organization recipient;

}
