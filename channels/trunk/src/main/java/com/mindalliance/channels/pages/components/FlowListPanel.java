package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.commands.AddCapability;
import com.mindalliance.channels.command.commands.AddNeed;
import com.mindalliance.channels.pages.ScenarioPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A list of flows from a node, either requirements or outcomes.
 */
public class FlowListPanel extends AbstractCommandablePanel {

    /**
     * The node for which flows are listed.
     */
    private Node node;

    /**
     * True if outcomes are listed; false if requirements are listed.
     */
    private boolean outcomes;
    /**
     * Flows list container.
     */
    private WebMarkupContainer flowsDiv;


    public FlowListPanel( String id, Node node, boolean outcomes ) {
        super( id );
        setNode( node );
        setOutcomes( outcomes );
        setDefaultModel( new CompoundPropertyModel( this ) );
        add( new Label( "title" ) );                                                      // NON-NLS

        add( new Link( "new" ) {                                                          // NON-NLS

            @Override
            public void onClick() {
                Part n = (Part)getNode();
                Command command = isOutcomes()
                        ? new AddCapability( n )
                        : new AddNeed( n );
                Flow f = (Flow)doCommand( command );
                Scenario s = n.getScenario();
                Set<Long> newExpansions = new HashSet<Long>( ( (ScenarioPage) getPage() ).findExpansions() );
                newExpansions.add( f.getId() );
                newExpansions.remove( f.getScenario().getId() ); // TODO - Denis : FIX PROBLEM AND REMOVE PATCH
                setResponsePage( ScenarioPage.class,
                        ScenarioPage.getParameters( s, n, newExpansions ) );
            }
        } );
        flowsDiv = new WebMarkupContainer("flows-div");
        flowsDiv.setOutputMarkupId( true );
        add( flowsDiv );
    }

    protected void onBeforeRender() {
        super.onBeforeRender();
        flowsDiv.add( createFlowPanels( outcomes ) );
    }

    private ListView<Flow> createFlowPanels( final boolean outcomes ) {
        final Set<Long> expansions = ( (ScenarioPage) getPage() ).findExpansions();
        return new ListView<Flow>( "flows", new PropertyModel<List<Flow>>(this, "flows") ) {
            protected void populateItem( ListItem<Flow> item ) {
                Flow flow = item.getModelObject();
                long flowId = flow.getId();
                if ( expansions.contains( flowId ) ) {
                    requestLockOn( flow );
                    ExpandedFlowPanel flowPanel = outcomes ?
                            new ExpandedOutPanel( "flow", flow )
                            : new ExpandedReqPanel( "flow", flow );
                    item.add( flowPanel);
                } else {
                     releaseAnyLockOn( flow );
                     CollapsedFlowPanel flowPanel =
                             new CollapsedFlowPanel( "flow", flow, outcomes );
                    item.add( flowPanel);
                }
            }
        };
    }

    public List<Flow> getFlows() {
        List<Flow> flows = new ArrayList<Flow>();
        Iterator<Flow> iterator = outcomes ? node.outcomes() : node.requirements();
        while( iterator.hasNext() ) flows.add( iterator.next() );
        return flows;
    }

    /**
     * @return the title of this panel.
     */
    public String getTitle() {
        return isOutcomes() ? "Send" : "Receive";
    }

    public final Node getNode() {
        return node;
    }

    public final void setNode( Node node ) {
        this.node = node;
    }

    public final boolean isOutcomes() {
        return outcomes;
    }

    public final void setOutcomes( boolean outcomes ) {
        this.outcomes = outcomes;
    }

    public void updateWith( AjaxRequestTarget target, Object context ) {
        target.addComponent( flowsDiv );
        super.updateWith( target, context );
    }

}
