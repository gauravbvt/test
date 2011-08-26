package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.engine.command.Change;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.pages.components.MediaReferencesPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
        addIssueWarningImage();
        addFlowMediaPanel();
        addFlowActionMenu();
    }

    private void addFlowTitlePanel() {
        FlowTitlePanel titlePanel = new FlowTitlePanel( "title", getFlow() ,isSend() );
        // Add style classes
        titlePanel.add( new AttributeModifier( "class", true, new Model<String>( getCssClasses(  ) ) ) );
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
                        : MessageFormat.format( "[via {0}]", c );
            }
        } );
        makeVisible( channel, c != null && !c.isEmpty() );
        add( channel );
        add( titlePanel );
    }

    private void addIssueWarningImage() {
        WebMarkupContainer warning = new WebMarkupContainer( "warning" );
        String summary = getErrorSummary();
        boolean hasIssues = hasIssues();
        warning.add( new AttributeModifier(
                "src",
                true ,
                new Model<String>(
                        !summary.isEmpty()
                           ? "images/warning.png"
                           : hasIssues
                            ? "images/waived.png"
                            : ""
                )) );
        warning.add( new AttributeModifier(
                "alt",
                true ,
                new Model<String>(
                        !summary.isEmpty()
                           ? "issues detected"
                           : hasIssues
                            ? "all issues waived"
                            : ""
                )) );
        if ( !summary.isEmpty() ) {
            warning.add(
                    new AttributeModifier( "title", true, new Model<String>( summary ) ) );
        } else {
            if ( hasIssues ) {
                // All waived issues
                warning.add(
                        new AttributeModifier( "title", true, new Model<String>( "All issues waived" ) ) );
            }
        }
        warning.setVisible( hasIssues );
        add( warning );
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
