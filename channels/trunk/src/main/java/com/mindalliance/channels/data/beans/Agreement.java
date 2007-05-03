/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import java.util.List;

import com.mindalliance.channels.data.elements.Exchange;
import com.mindalliance.channels.data.elements.Organization;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * An agreement between organizations to carry out specified exchanges.
 * @author jf
 *
 */
public class Agreement extends AbstractJavaBean {
	
	private List<Pattern<Exchange>> exchangeSpecs; // exchange patterns
	private Organization source;
	private Organization recipient;

}
