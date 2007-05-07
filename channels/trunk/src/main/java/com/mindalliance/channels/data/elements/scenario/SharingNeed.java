/*
 * Created on Apr 27, 2007
 *
 */
package com.mindalliance.channels.data.elements.scenario;

import java.util.List;

import com.mindalliance.channels.data.elements.assertions.Agreeable;
import com.mindalliance.channels.data.elements.assertions.AgreedTo;
import com.mindalliance.channels.data.elements.assertions.Known;
import com.mindalliance.channels.data.elements.assertions.NeedsToKnow;
import com.mindalliance.channels.data.elements.assertions.Regulatable;
import com.mindalliance.channels.data.elements.assertions.Regulated;

/**
 * A match between a need to know and a know denoting a requirement for communication.
 * A SharingNeed is an occurrence; it exists for a period of time.
 * @author jf
 *
 */
public class SharingNeed extends AbstractOccurrence implements Regulatable, Agreeable {

	private NeedsToKnow needToKnow;
	private Known known;

	public List<Regulated> getRegulatedAssertions() {
		return null;
	}

	public List<AgreedTo> getAgreedToAssertions() {
		return null;
	}
	
}
