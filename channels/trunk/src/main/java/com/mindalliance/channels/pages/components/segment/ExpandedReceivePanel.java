package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.model.Channelable;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.pages.ModelPage;
import com.mindalliance.channels.pages.components.ChannelListPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.Set;

/**
 * Details of an expanded receive.
 */
public class ExpandedReceivePanel extends ExpandedFlowPanel {

    public ExpandedReceivePanel( String id, IModel<Flow> model, Set<Long> expansions, int index, ModelPage modelPage ) {
        super( id, model, false, expansions, index, modelPage );
    }

    /** {@inheritDoc} */
    @Override
    protected boolean isChannelRelevant( Flow f ) {
        // return !( getOther().isConnector() && f.isAskedFor() && f.isInternal() );
        return true;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean isChannelEditable( Flow f ) {
        return f.isInternal() || !f.isAskedFor();
    }

    /** {@inheritDoc} */
    @Override
    protected WebMarkupContainer createChannelRow() {
        WebMarkupContainer result = new WebMarkupContainer( "channel-row" );              // NON-NLS
        result.setOutputMarkupPlaceholderTag( true );
        result.add( new Label( "channel-title", "Channels:" ) );

        ChannelListPanel channelListPanel = new ChannelListPanel(
                "channels",                                                               // NON-NLS
                new PropertyModel<Channelable>( this, "flow" ),
                this );                         // NON-NLS
        result.add( channelListPanel );
        return result;
    }
}
