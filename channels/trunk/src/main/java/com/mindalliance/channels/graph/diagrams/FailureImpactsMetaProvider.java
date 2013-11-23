/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.engine.analysis.Analyst;
import org.springframework.core.io.Resource;

public class FailureImpactsMetaProvider extends FlowMapMetaProvider {

    public FailureImpactsMetaProvider( ModelObject modelObject, String outputFormat, Resource imageDirectory,
                                       Analyst analyst, CommunityService communityService ) {
        super( modelObject, outputFormat, imageDirectory, analyst, communityService );
    }

    @Override
    public String getGraphOrientation() {
        return "LR";
    }

    @Override
    protected String getNodeLabel( Node node ) {
        return node.isPart() ? "FAIL: " + super.getNodeLabel( node ) : super.getNodeLabel( node );
    }

}
