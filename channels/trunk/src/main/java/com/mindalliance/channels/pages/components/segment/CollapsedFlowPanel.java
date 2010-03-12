package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Flow;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.text.MessageFormat;

/**
 * A collapsed flow.
 */
public class CollapsedFlowPanel extends AbstractFlowPanel {

    public CollapsedFlowPanel( String id, IModel<Flow> flowModel, boolean isSend ) {
        super( id, flowModel, isSend, true );
        init();
    }

    private void init() {
        addLabel();
        addFlowActionMenu();
    }

    private void addLabel() {
        Label label = new Label( "title",                                                 // NON-NLS
                new PropertyModel( getFlow(),
                        isSend() ? "sendTitle" : "receiveTitle" ) );                // NON-NLS

        final String c = Channel.toString( getFlow().getEffectiveChannels() );
        Label channel = new Label( "channels", new AbstractReadOnlyModel() {              // NON-NLS

            @Override
            public Object getObject() {
                return c != null && c.isEmpty() ? ""
                        : MessageFormat.format( "[{0}]", c );
            }
        } );
        makeVisible( channel, c != null && !c.isEmpty() );
        add( channel );

        // Add style mods from analyst.
        Analyst analyst = ( (Channels) getApplication() ).getAnalyst();
        String summary = analyst.getIssuesSummary( getFlow(), Analyst.INCLUDE_PROPERTY_SPECIFIC );
        boolean hasIssues = analyst.hasIssues( getFlow(), Analyst.INCLUDE_PROPERTY_SPECIFIC );
        if ( !summary.isEmpty() ) {
            label.add(
                    new AttributeModifier( "class", true, new Model<String>( "error" ) ) );
            label.add(
                    new AttributeModifier( "title", true, new Model<String>( summary ) ) );
        } else {
            if ( hasIssues ) {
                // All waived issues
                label.add(
                        new AttributeModifier( "class", true, new Model<String>( "waived" ) ) );
                label.add(
                        new AttributeModifier( "title", true, new Model<String>( "All issues waived" ) ) );
            }
        }

        add( label );
    }


}
