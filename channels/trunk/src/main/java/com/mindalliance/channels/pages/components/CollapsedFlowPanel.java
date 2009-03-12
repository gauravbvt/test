package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.pages.Project;
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

    public CollapsedFlowPanel( String id, final Flow flow, final boolean outcome ) {
        super( id );
        this.flow = flow;

        final Label label = new Label( "title",                                           // NON-NLS
                new PropertyModel( flow,
                        outcome ? "outcomeTitle" : "requirementTitle" ) );     // NON-NLS

        final String c = Channel.toString( flow.getEffectiveChannels() );
        final Label channel = new Label( "channels", new AbstractReadOnlyModel() {         // NON-NLS

            @Override
            public Object getObject() {
                return c != null && c.isEmpty() ? ""
                        : MessageFormat.format( "({0})", c );
            }
        } );
        channel.setVisible( c != null && !c.isEmpty() );
        add( channel );

        // Add style mods from scenario analyst.
        final Analyst analyst = ( (Project) getApplication() ).getAnalyst();
        final String issue = analyst.getIssuesSummary(
                flow, Analyst.INCLUDE_PROPERTY_SPECIFIC );
        if ( !issue.isEmpty() ) {
            label.add(
                    new AttributeModifier( "class", true, new Model<String>( "error" ) ) );   // NON-NLS
            label.add(
                    new AttributeModifier( "title", true, new Model<String>( issue ) ) );     // NON-NLS
        }

        add( label );

/*
        WebMarkupContainer replicateItem = new WebMarkupContainer( "replicate-item" );
        add( replicateItem );
        replicateItem.setVisible(
                ( outcome && getFlow().getTarget().isPart() )
                        || ( !outcome && getFlow().getSource().isPart() ) );
        replicateItem.add( new Link( "replicate" ) {
            @Override
            public void onClick() {
                Flow replica = flow.replicate( outcome );
                // PageParameters parameters = ((ScenarioPage)getWebPage()).getPageParameters();
                // TODO - Denis: Fix problem and remove patch
                PageParameters parameters = ( (ScenarioPage) getWebPage() )
                        .getParametersCollapsing( getFlow().getScenario().getId() );
                parameters.add( ScenarioPage.EXPAND_PARM, String.valueOf( replica.getId() ) );
                this.setResponsePage( getWebPage().getClass(), parameters );
            }
        } );

        // TODO replace expansion links by ajaxfallbacklinks
        add( new ExternalLink( "expand", getRequest().getURL() + "&expand=" + flow.getId() ) );
        add( new CheckBox( "delete",                                                      // NON-NLS
                new PropertyModel<Boolean>( this, "markedForDeletion" ) ) );   // NON-NLS
*/
       addFlowActionMenu( outcome );
    }

    private void addFlowActionMenu( boolean isOutcome ) {
        FlowActionsMenuPanel flowActionsMenu = new FlowActionsMenuPanel(
                "flowActionsMenu",
                new PropertyModel<Flow>( this, "flow" ),
                isOutcome,
                true );
        flowActionsMenu.setOutputMarkupId( true );
        add( flowActionsMenu );
    }

    public Flow getFlow() {
        return flow;
    }


}
