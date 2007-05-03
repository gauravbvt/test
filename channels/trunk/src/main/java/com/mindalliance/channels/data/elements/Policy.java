/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.data.Regulatable;
import com.mindalliance.channels.data.beans.Pattern;

/**
 * A policy issued by some organization and enforced possibly by another.
 * @author jf
 *
 */
public class Policy extends AbstractElement {
	
	class Target {
		private Pattern<Regulatable> targetSpecs;
	}
	
	private Organization issuer;
	private Organization enforcer;
	private List<Target> forbidden;
	private List<Target> obligated;
	

}
