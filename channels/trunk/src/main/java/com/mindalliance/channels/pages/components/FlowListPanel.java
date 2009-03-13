package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.commands.AddCapability;
import com.mindalliance.channels.command.commands.AddNeed;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
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
    private IModel<Node> model;

    /**
     * True if outcomes are listed; false if requirements are listed.
     */
    private boolean outcomes;
    /**
     * Flows list container.
     */
    private WebMarkupContainer flowsDiv;

    /**
     * Expansions
     */
    private Set<Long> expansions;


    public FlowListPanel( String id, IModel<Node> model, boolean outcomes, Set<Long> expansions ) {
        super( id );
        this.model = model;
        this.outcomes = outcomes;
        this.expansions = expansions;
        init();
    }

    private void init() {
        setOutcomes( outcomes );
        setDefaultModel( new CompoundPropertyModel( this ) );
        add( new Label( "title" ) );                                                      // NON-NLS
        AjaxFallbackLink newLink = new AjaxFallbackLink( "new" ) {
            public void onClick( AjaxRequestTarget target ) {
                Part n = (Part) getNode();
                Command command = isOutcomes()
                        ? new AddCapability( n )
                        : new AddNeed( n );
                Flow newFlow = (Flow) doCommand( command );
                updateWith( target, newFlow.getId() );
            }
        };
        add( newLink );
        flowsDiv = new WebMarkupContainer( "flows-div" );
        flowsDiv.setOutputMarkupId( true );
        add( flowsDiv );
        flowsDiv.add( createFlowPanels( outcomes ) );
    }

    private ListView<Flow> createFlowPanels( final boolean outcomes ) {
        // final Set<Long> expansions = ( (ScenarioPage) getPage() ).findExpansions();
        return new ListView<Flow>( "flows", new PropertyModel<List<Flow>>( this, "flows" ) ) {
            protected void populateItem( ListItem<Flow> item ) {
                Flow flow = item.getModelObject();
                long flowId = flow.getId();
                if ( expansions.contains( flowId ) ) {
                    requestLockOn( flow );
                    ExpandedFlowPanel flowPanel = outcomes ?
                            new ExpandedOutPanel( "flow", item.getModel() )
                            : new ExpandedReqPanel( "flow", item.getModel() );
                    item.add( flowPanel );
                } else {
                    releaseAnyLockOn( flow );
                    CollapsedFlowPanel flowPanel =
                            new CollapsedFlowPanel( "flow", flow, outcomes );
                    item.add( flowPanel );
                }
            }
        };
    }

    public List<Flow> getFlows() {
        List<Flow> flows = new ArrayList<Flow>();
        Iterator<Flow> iterator = outcomes ? getNode().outcomes() : getNode().requirements();
        while ( iterator.hasNext() ) flows.add( iterator.next() );
        return flows;
    }

    /**
     * @return the title of this panel.
     */
    public String getTitle() {
        return isOutcomes() ? "Send" : "Receive";
    }

    public final Node getNode() {
        return model.getObject();
    }

    public final boolean isOutcomes() {
        return outcomes;
    }

    public final void setOutcomes( boolean outcomes ) {
        this.outcomes = outcomes;
    }

}
