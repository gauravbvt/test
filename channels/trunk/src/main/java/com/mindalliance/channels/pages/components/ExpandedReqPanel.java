package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;

import org.apache.wicket.model.IModel;

import java.util.Set;

/**
 * Details of an expanded requirement.
 */
public class ExpandedReqPanel extends ExpandedFlowPanel {

    public ExpandedReqPanel( String id, IModel<Flow> model, Set<Long> expansions ) {
        super( id, model, false, expansions );
    }

    /** {@inheritDoc} */
    @Override
    protected boolean isChannelRelevant( Flow f ) {
        return !( getOther().isConnector() && f.isAskedFor() && f.isInternal() );
    }

    /** {@inheritDoc} */
    @Override
    protected boolean isChannelEditable( Flow f ) {
        return f.isInternal() || !f.isAskedFor();
    }
}
