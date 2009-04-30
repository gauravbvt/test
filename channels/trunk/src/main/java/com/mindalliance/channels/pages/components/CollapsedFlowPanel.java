package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.pages.components.menus.FlowActionsMenuPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.text.MessageFormat;

/**
 * A collapsed flow.
 */
public class CollapsedFlowPanel extends AbstractCommandablePanel {

    /**
     * The underlying flow.
     */
    private Flow flow;

    public CollapsedFlowPanel( String id, Flow flow, boolean outcome ) {
        super( id );
        this.flow = flow;

        Label label = new Label( "title",                                                 // NON-NLS
                new PropertyModel( flow,
                        outcome ? "outcomeTitle" : "requirementTitle" ) );                // NON-NLS

        final String c = Channel.toString( flow.getEffectiveChannels() );
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
        String summary = analyst.getIssuesSummary( flow, Analyst.INCLUDE_PROPERTY_SPECIFIC );
        boolean hasIssues = analyst.hasIssues( flow, Analyst.INCLUDE_PROPERTY_SPECIFIC );
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
        addFlowActionMenu( outcome );
    }

    private void addFlowActionMenu( boolean isOutcome ) {
        FlowActionsMenuPanel flowActionsMenu = new FlowActionsMenuPanel(
                    "flowActionsMenu",
                    new PropertyModel<Flow>( this, "flow" ),
                    isOutcome,
                    true );
        add( flowActionsMenu );
    }

    public Flow getFlow() {
        return flow;
    }


}
