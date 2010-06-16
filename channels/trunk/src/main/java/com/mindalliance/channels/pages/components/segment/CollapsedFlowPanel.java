package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.pages.components.MediaReferencesPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.text.MessageFormat;

/**
 * A collapsed flow.
 */
public class CollapsedFlowPanel extends AbstractFlowPanel {
    /**
     * Flow model.
     */
    private IModel<Flow> flowModel;

    public CollapsedFlowPanel( String id, IModel<Flow> flowModel, boolean isSend, int index ) {
        super( id, flowModel, isSend, true, index );
        this.flowModel = flowModel;
        init();
    }

    private void init() {
        addFlowTitlePanel();
        addFlowMediaPanel();
        addFlowActionMenu();
    }

    private void addFlowTitlePanel() {
        FlowTitlePanel titlePanel = new FlowTitlePanel( "title", getFlow() ,isSend() );
        // Add style classes
        String summary = getErrorSummary();
        boolean hasIssues = hasIssues();
        if ( !summary.isEmpty() ) {
            titlePanel.add(
                    new AttributeModifier( "title", true, new Model<String>( summary ) ) );
        } else {
            if ( hasIssues ) {
                // All waived issues
                titlePanel.add(
                        new AttributeModifier( "title", true, new Model<String>( "All issues waived" ) ) );
            }
        }
        titlePanel.add( new AttributeModifier( "class", true, new Model<String>( getCssClasses( hasIssues, summary ) ) ) );
        titlePanel.add( new AjaxEventBehavior( "onclick" ) {
            protected void onEvent( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Expanded, getFlow() ) );
            }
        } );
        final String c = Channel.toString( getFlow().getEffectiveChannels() );
        Label channel = new Label( "channels", new AbstractReadOnlyModel() {

            @Override
            public Object getObject() {
                return c != null && c.isEmpty() ? ""
                        : MessageFormat.format( "[{0}]", c );
            }
        } );
        makeVisible( channel, c != null && !c.isEmpty() );
        add( channel );
        add( titlePanel );
    }

    private void addFlowMediaPanel() {
        MediaReferencesPanel flowMediaPanel = new MediaReferencesPanel(
                "flowMedia",
                flowModel,
                getExpansions()
        );
        flowMediaPanel.setOutputMarkupId( true );
        addOrReplace( flowMediaPanel );
    }


}
