package com.mindalliance.channels.model;

import com.mindalliance.channels.query.QueryService;

import java.util.List;

/**
 * A model object in the scope of a plan segment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 4, 2009
 * Time: 3:56:52 PM
 */
public interface SegmentObject extends Identifiable {
    /**
     * Get the model object's containing segment.
     *
     * @return a plan segment
     */
    Segment getSegment();

    /**
     * Get the essential downstream sharing flows.
     *
     * @param assumeFails assume alternate downstream flows fail
     * @param queryService a query service
     * @return a list of flows.
     */
    List<Flow> getEssentialFlows( boolean assumeFails, QueryService queryService );

    String getTitle();

}
