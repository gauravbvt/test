package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;

import org.apache.wicket.model.IModel;

/**
 * An expanded outcome flow.
 */
public class ExpandedOutPanel extends ExpandedFlowPanel {

    public ExpandedOutPanel( String id, IModel<Flow> model ) {
        super( id, model, true );
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
