package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.commands.AddCapability;
import com.mindalliance.channels.command.commands.AddNeed;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.pages.Updatable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A list of flows from a node, either receives or sends.
 */
public class FlowListPanel extends AbstractCommandablePanel {

    /**
     * The node for which flows are listed.
     */
    private IModel<Part> model;

    /**
     * True if sends are listed; false if receives are listed.
     */
    private boolean sends;
    /**
     * Flows list container.
     */
    private WebMarkupContainer flowsDiv;

    /**
     * Expansions
     */
    private Set<Long> expansions;


    public FlowListPanel( String id, IModel<Part> model, boolean sends, Set<Long> expansions ) {
        super( id );
        this.model = model;
        this.sends = sends;
        this.expansions = expansions;
        init();
    }

    private void init() {
        setSends( sends );
        setDefaultModel( new CompoundPropertyModel( this ) );
        add( new Label( "title" ) );                                                      // NON-NLS
        AjaxFallbackLink newLink = new AjaxFallbackLink( "new" ) {
            public void onClick( AjaxRequestTarget target ) {
                Part n = (Part) getNode();
                Command command = isSends()
                        ? new AddCapability( n )
                        : new AddNeed( n );
                Change change = doCommand( command );
                update( target, change );
            }
        };
        newLink.setVisible( getPlan().isDevelopment() );
        add( newLink );
        newLink.add( new Label( "addFlow", sends ? "Add capability" : "Add need" ) );
        addFlowsDiv();
    }

    private void addFlowsDiv() {
        flowsDiv = new WebMarkupContainer( "flows-div" );
        flowsDiv.setOutputMarkupId( true );
        add( flowsDiv );
        flowsDiv.add( createFlowPanels( sends ) );
    }

    private ListView<Flow> createFlowPanels( final boolean areSends ) {
        // final Set<Long> expansions = ( (ChannelsPage) getPage() ).findExpansions();
        return new ListView<Flow>( "flows", new PropertyModel<List<Flow>>( this, "flows" ) ) {
            protected void populateItem( ListItem<Flow> item ) {
                Flow flow = item.getModelObject();
                long flowId = flow.getId();
                if ( expansions.contains( flowId ) ) {
                    ExpandedFlowPanel flowPanel = areSends ?
                            new ExpandedOutPanel( "flow", new Model<Flow>( flow ), expansions )
                            : new ExpandedReqPanel( "flow", new Model<Flow>( flow ), expansions );
                    item.add( flowPanel );
                } else {
                    CollapsedFlowPanel flowPanel =
                            new CollapsedFlowPanel( "flow", new Model<Flow>( flow ), areSends );
                    item.add( flowPanel );
                }
            }
        };
    }

    /**
     * Get flows to list.
     *
     * @return a list of lofws
     */
    public List<Flow> getFlows() {
        List<Flow> flows = new ArrayList<Flow>();
        Iterator<Flow> iterator = sends ? getNode().sends() : getNode().receives();
        while ( iterator.hasNext() ) flows.add( iterator.next() );
        return flows;
    }

    /**
     * @return the title of this panel.
     */
    public String getTitle() {
        return isSends() ? "Send" : "Receive";
    }

    public final Node getNode() {
        return model.getObject();
    }

    public final boolean isSends() {
        return sends;
    }

    public final void setSends( boolean sends ) {
        this.sends = sends;
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject();
        if ( identifiable instanceof Flow ) {
            target.addComponent( flowsDiv );
        }
        super.updateWith( target, change, updated );
    }

    /**
     * Refresh list of flows.
     *
     * @param target an ajax request target
     */
    public void refresh( AjaxRequestTarget target ) {
        target.addComponent( flowsDiv );
    }

    /**
     * Refresh menus.
     *
     * @param target ajax request target
     */
    public void refreshMenus( AjaxRequestTarget target ) {
        target.addComponent( flowsDiv );
    }
}
