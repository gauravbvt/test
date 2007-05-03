/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.reference;

import java.util.List;

import com.mindalliance.channels.data.elements.AbstractElement;
/**
 * A taxonomy
 * @author jf
 *
 */
public class Typology extends AbstractElement {
	
	private List<Type> types;
	private Type root; // The type all types imply by default.


}
