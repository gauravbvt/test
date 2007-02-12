// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import java.util.Set;

/**
 * The Channels model interface.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface Model {

    /**
     * Return components that should be asserted by the rule engine.
     */
    Set<JavaBean> getAssertableObjects();

}
