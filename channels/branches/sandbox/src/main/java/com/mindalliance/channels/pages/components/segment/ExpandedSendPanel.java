package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.model.Channelable;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.pages.components.ChannelListPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.Set;

/**
 * An expanded send flow.
 */
public class ExpandedSendPanel extends ExpandedFlowPanel {

    public ExpandedSendPanel( String id, IModel<Flow> model, Set<Long> expansions, int index ) {
        super( id, model, true, expansions, index );
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

    /** {@inheritDoc} */
    @Override
    protected WebMarkupContainer createChannelRow() {
        WebMarkupContainer result = new WebMarkupContainer( "channel-row" );              // NON-NLS
        result.setOutputMarkupPlaceholderTag( true );
        result.add( new Label( "channel-title", "Channels:" ) );

        ChannelListPanel channelListPanel = new ChannelListPanel(
                "channels",                                                               // NON-NLS
                new PropertyModel<Channelable>( this, "flow" ) );                         // NON-NLS
        result.add( channelListPanel );
        return result;
    }
}
