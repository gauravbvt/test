/*
 * Created on Apr 27, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.data.Agreeable;
import com.mindalliance.channels.data.Regulatable;
import com.mindalliance.channels.data.beans.AgreedTo;
import com.mindalliance.channels.data.beans.Known;
import com.mindalliance.channels.data.beans.NeedsToKnow;
import com.mindalliance.channels.data.beans.Regulated;

/**
 * A match between a need to know and a know denoting a requirement for communication.
 * @author jf
 *
 */
public class Exchange extends AbstractScenarioElement implements Regulatable, Agreeable {

	private NeedsToKnow needToKnow;
	private Known known;
	private List<Regulated> regulatedAssertions;
	private List<AgreedTo> agreedToAssertions;

	public List<Regulated> getRegulatedAssertions() {
		return regulatedAssertions;
	}

	public List<AgreedTo> getAgreedToAssertions() {
		return agreedToAssertions;
	}
	
}
