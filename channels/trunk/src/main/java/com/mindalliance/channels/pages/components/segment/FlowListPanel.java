package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.commands.AddCapability;
import com.mindalliance.channels.command.commands.AddNeed;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.AttributeModifier;
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
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A list of flows from a node, either receives or sends.
 */
public class FlowListPanel extends AbstractCommandablePanel {

    @SpringBean
    QueryService queryService;

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
     * List of flow panels.
     */
    List<AbstractFlowPanel> flowPanels;

    /**
     * Expansions.
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
        newLink.add( new Label( "addFlow", sends ? "Add info sent" : "Add info received" ) );
        addFlowsDiv();
    }

    private void addFlowsDiv() {
        flowsDiv = new WebMarkupContainer( "flows-div" );
        flowsDiv.setOutputMarkupId( true );
        addOrReplace( flowsDiv );
        ListView<Flow> flowPanels = createFlowPanels( sends );
        flowsDiv.add( flowPanels );
    }

    private ListView<Flow> createFlowPanels( final boolean areSends ) {
        // final Set<Long> expansions = ( (ChannelsPage) getPage() ).findExpansions();
        flowPanels = new ArrayList<AbstractFlowPanel>(  );
        return new ListView<Flow>( "flows", new PropertyModel<List<Flow>>( this, "flows" ) ) {
            protected void populateItem( ListItem<Flow> item ) {
                Flow flow = item.getModelObject();
                long flowId = flow.getId();
                AbstractFlowPanel flowPanel;
                if ( expansions.contains( flowId ) ) {
                    flowPanel = areSends ?
                            new ExpandedSendPanel(
                                    "flow",
                                    new Model<Flow>( flow ),
                                    expansions,
                                    item.getIndex() )
                            : new ExpandedReceivePanel( "flow", new Model<Flow>( flow ), expansions, item.getIndex() );
                } else {
                    flowPanel = new CollapsedFlowPanel(
                            "flow",
                            new Model<Flow>( flow ),
                            areSends,
                            item.getIndex() );
                }
                flowPanel.add( new AttributeModifier(
                        "class",
                        true,
                        new Model<String>( getCssClasses( item ) ) ) );
                flowPanels.add( flowPanel );
                item.add( flowPanel );
            }
        };
    }

    String getCssClasses( ListItem<Flow> item ) {
        Flow flow = item.getModelObject();
        String evenOdd = ( item.getIndex() % 2 == 0 ? "even" : "odd" );
        String priority = flow.getPriorityCssClass( queryService );
        return evenOdd + " " + priority;
    }


    /**
     * Get flows sorted: sharing > not sharing, by priority, by title..
     *
     * @return a list of lofws
     */
    public List<Flow> getFlows() {
        List<Flow> flows = new ArrayList<Flow>();
        Iterator<Flow> iterator = sends ? getNode().sends() : getNode().receives();
        while ( iterator.hasNext() ) flows.add( iterator.next() );
        Collections.sort( flows, new Comparator<Flow>() {
            public int compare( Flow flow, Flow other ) {
//                if ( expansions.contains( flow.getId() ) ) return -1;
                if ( flow.isSharing() && !other.isSharing() ) return -1;
                if ( other.isSharing() && !flow.isSharing() ) return 1;
                int comparison = 0;
                if ( flow.isSharing() ) {
                    Level impact = queryService.computeSharingPriority( flow );
                    Level otherImpact = queryService.computeSharingPriority( other );
                    // reverse order
                    comparison = otherImpact.compareTo( impact );
                }
                if ( comparison == 0 ) {
                    String title = sends ? flow.getSendTitle() : flow.getReceiveTitle();
                    String otherTitle = sends ? other.getSendTitle() : other.getReceiveTitle();
                    comparison = Collator.getInstance().compare( title.toLowerCase(), otherTitle.toLowerCase() );
                }
                return comparison;
            }
        } );
        return flows;
    }

    /**
     * @return the title of this panel.
     */
    public String getTitle() {
        return isSends() ? "Sends" : "Receives";
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
        target.appendJavascript( PlanPage.IE7CompatibilityScript );
        if ( change.isDisplay() || change.isUndoing() ) {
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
        target.appendJavascript( PlanPage.IE7CompatibilityScript );
        target.addComponent( flowsDiv );
    }

    /**
     * Refresh menus.
     *
     * @param target ajax request target
     */
    public void refreshMenus( AjaxRequestTarget target ) {
        for ( AbstractFlowPanel flowPanel : flowPanels ) {
           flowPanel.refreshMenu( target );
        }
    }
}
