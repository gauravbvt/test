package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;

/**
 * An expanded outcome flow.
 */
public class ExpandedOutPanel extends ExpandedFlowPanel {

    public ExpandedOutPanel( String id, Flow flow ) {
        super( id, flow, true );
    }

    /** {@inheritDoc} */
    @Override
    protected boolean isChannelRelevant( Flow f ) {
        return !getOther().isConnector() || f.isAskedFor() || !f.isInternal();
    }

    /** {@inheritDoc} */
    @Override
    protected boolean isChannelEditable( Flow f ) {
        return f.isInternal() || f.isAskedFor();
    }
}
