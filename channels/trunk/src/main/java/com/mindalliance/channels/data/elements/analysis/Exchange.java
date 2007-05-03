/*
 * Created on Apr 27, 2007
 *
 */
package com.mindalliance.channels.data.elements.analysis;

import java.util.List;

import com.mindalliance.channels.data.elements.assertions.Agreeable;
import com.mindalliance.channels.data.elements.assertions.AgreedTo;
import com.mindalliance.channels.data.elements.assertions.Known;
import com.mindalliance.channels.data.elements.assertions.NeedsToKnow;
import com.mindalliance.channels.data.elements.assertions.Regulatable;
import com.mindalliance.channels.data.elements.assertions.Regulated;
import com.mindalliance.channels.data.elements.scenario.AbstractScenarioElement;

/**
 * A match between a need to know and a know denoting a requirement for communication.
 * @author jf
 *
 */
public class Exchange extends AbstractScenarioElement implements Regulatable, Agreeable {

	private NeedsToKnow needToKnow;
	private Known known;

	public List<Regulated> getRegulatedAssertions() {
		return null;
	}

	public List<AgreedTo> getAgreedToAssertions() {
		return null;
	}
	
}
