package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;

import org.apache.wicket.model.IModel;

import java.util.Set;

/**
 * An expanded outcome flow.
 */
public class ExpandedOutPanel extends ExpandedFlowPanel {

    public ExpandedOutPanel( String id, IModel<Flow> model, Set<Long> expansions ) {
        super( id, model, true, expansions );
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
