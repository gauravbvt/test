/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.data.Communicable;
import com.mindalliance.channels.data.beans.Pattern;

/**
 * An agreement between organizations to carry out specified exchanges.
 * @author jf
 *
 */
public class Agreement extends AbstractElement {
	
	private List<Pattern<Communicable>> communicables; // pure information, documents etc.
	private Organization source;
	private Organization recipient;

}
