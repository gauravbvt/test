package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import org.springframework.core.io.Resource;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 12, 2010
 * Time: 1:11:42 PM
 */
public class FailureImpactsMetaProvider extends FlowMapMetaProvider {

    public FailureImpactsMetaProvider(
            ModelObject modelObject,
            String outputFormat,
            Resource imageDirectory,
            Analyst analyst ) {
        super( modelObject, outputFormat, imageDirectory, analyst );
    }

    protected String getNodeLabel( Node node ) {
        if ( node.isPart() ) {
            return "FAIL: " + super.getNodeLabel( node );
        } else {
            return super.getNodeLabel( node );
        }
    }

}
