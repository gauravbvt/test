package com.mindalliance.channels;

import com.mindalliance.channels.analysis.profiling.Resource;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 12, 2009
 * Time: 7:56:21 PM
 */
public interface Resourceable {

    /**
     * Find all implied resources within the current project.
     * @return a list of resources
     */
    List<Resource> findAllResources();
}
