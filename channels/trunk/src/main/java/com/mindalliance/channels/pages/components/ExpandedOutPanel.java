package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Channelable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

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

    /** {@inheritDoc} */
    @Override
    protected WebMarkupContainer createChannelRow() {
        WebMarkupContainer result = new WebMarkupContainer( "channel-row" );              // NON-NLS
        result.setOutputMarkupPlaceholderTag( true );
        result.add( new Label( "channel-title", new AbstractReadOnlyModel<String>() {     // NON-NLS

            @Override
            public String getObject() {
                return getFlow().isAskedFor() ? "Incoming:" : "Outgoing:";
            }
        } ) );

        ChannelListPanel channelListPanel = new ChannelListPanel(
                "channels",                                                               // NON-NLS
                new PropertyModel<Channelable>( this, "flow" ) );                         // NON-NLS
        result.add( channelListPanel );
        return result;
    }
}
