package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;

/**
 * Details of an expanded requirement.
 */
public class ExpandedReqPanel extends ExpandedFlowPanel {

    public ExpandedReqPanel( String id, Flow flow ) {
        super( id, flow, false );
    }

    /** {@inheritDoc} */
    @Override
    protected boolean isChannelRelevant( Flow f ) {
        return !( getOther().isConnector() && f.isAskedFor() && f.isInternal() );
    }
}
