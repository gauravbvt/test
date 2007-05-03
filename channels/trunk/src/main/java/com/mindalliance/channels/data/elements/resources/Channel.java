/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.elements.resources;

import java.util.List;

import com.mindalliance.channels.data.elements.reference.Type;
import com.mindalliance.channels.data.support.Latency;
import com.mindalliance.channels.data.support.Level;
import com.mindalliance.channels.data.support.TypeSet;

/**
 * A communication medium with a security level, restrictions as to formats it can transmit,
 * and possibly interoperable with other channels. A Channel has a latency.
 * @author jf
 *
 */
public class Channel extends AbstractResource {

	private Level security; // LOW, MEDIUM or HIGH
	private Level reliability;
	private List<Channel> interoperables; // information can travel from this one to the other
	private TypeSet supportedFormats = new TypeSet(Type.FORMAT);
	private Latency latency;
	
}
