// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

import java.util.List;
import java.util.Set;

/**
 * A channels model.
 * This is the net result of the work of users on a project.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @composed "" - "*" JavaBean
 */
public interface Model extends JavaBean {

    /**
     * Return the name of this model.
     */
    String getName();

    /**
     * Return some high-level objectives of this model.
     */
    List<String> getObjectives();

    /**
     * Return components that should be asserted by the rule engine.
     */
    Set<JavaBean> getAssertableObjects();

}
