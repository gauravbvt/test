// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.discoveries;

import java.util.List;

import com.mindalliance.channels.data.models.NeedsToKnow;

/**
 * Has information needs.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public interface Needy extends Assertable {

    /**
     * Get NeedsToKnow assertions.
     */
    List<NeedsToKnow> getNeedsToKnowAssertions();

}
