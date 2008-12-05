package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Details of an expanded flow.
 */
public class ExpandedFlowPanel extends Panel {

    /** The flow edited by this panel. */
    private Flow flow;

    /** True if outcome, otherwise a requirement. */
    private boolean outcome;

    /** True if this flow is marked for deletion. */
    private boolean markedForDeletion;

    public ExpandedFlowPanel( String id, Flow flow, boolean outcome ) {
        super( id );
        setFlow( flow );
        setOutcome( outcome );
        setDefaultModel( new CompoundPropertyModel<Flow>( flow ) );

        // TODO add any/all depending on actor

        add( new Label( "title", new PropertyModel( flow,                                 // NON-NLS
                            outcome ? "outcomeTitle" : "requirementTitle" ) ) );          // NON-NLS

        // TODO don't collapse everything on hide
        add( new ScenarioLink( "hide", getNode().getScenario(), getNode() ) );            // NON-NLS
        add( new CheckBox( "delete",                                                      // NON-NLS
                           new PropertyModel<Boolean>( this, "markedForDeletion" ) ) );   // NON-NLS

        addOtherField();
        addLabeled( "name-label", new TextField<String>( "name" ) );                      // NON-NLS

        add( new CheckBox( "critical" ) );                                                // NON-NLS
        add( new CheckBox( "askedFor" ) );                                                // NON-NLS

        addLabeled( "channel-label",     new TextField<String>( "channel"    ) );         // NON-NLS
        addLabeled( "maxDelay-label",    new TextField<String>( "maxDelay"   ) );         // NON-NLS
        addLabeled( "description-label", new TextArea<String>( "description" ) );         // NON-NLS
        add( new AttachmentPanel( "attachments", flow ) );                                // NON-NLS
    }

    private void addLabeled( String id, FormComponent<?> component ) {
        add( new FormComponentLabel( id, component ) );
        add( component );
    }

    private void addOtherField() {
        final DropDownChoice<Node> other = new DropDownChoice<Node>( "other",             // NON-NLS
            new PropertyModel<Node>( this, "other" ),                                     // NON-NLS
            new PropertyModel<List<? extends Node>>( this, "otherNodes" ) );              // NON-NLS

        final ScenarioLink details = new ScenarioLink(
                "other-details", getNode().getScenario(), getOther() );                   // NON-NLS
        details.add(
            new Label( "type",                                                            // NON-NLS
                       new Model<String>( isOutcome() ? "Target" : "Source" ) ) );

        final FormComponentLabel otherLabel =
            new FormComponentLabel( "other-label", other );                               // NON-NLS
        otherLabel.add( details );
        add( otherLabel );

        add( other );
    }

    public final Flow getFlow() {
        return flow;
    }

    public final void setFlow( Flow flow ) {
        this.flow = flow;
    }

    public final boolean isOutcome() {
        return outcome;
    }

    public final void setOutcome( boolean outcome ) {
        this.outcome = outcome;
    }

    /**
     * @return the node on this side of the flow
     */
    public final Node getNode() {
        return isOutcome() ? getFlow().getSource()
                           : getFlow().getTarget();
    }

    /**
     * Get the node at the other side of this flow: the source if requirement, the target if
     * outcome.
     * @return the other side of this flow.
     */
    public Node getOther() {
        return isOutcome() ? getFlow().getTarget()
                           : getFlow().getSource();
    }

    /**
     * Set the node at the other side of this flow
     * @param node the new source or target
     */
    public void setOther( Node node ) {
        final Scenario s = node.getScenario();
        final Flow f = getFlow();
        if ( isOutcome() ) {
            final Node target = f.getTarget();
            target.removeRequirement( f );
            f.setTarget( node );
            node.addRequirement( f );
            if ( target.isConnector() )
                s.removeNode( target );
        } else {
            final Node source = f.getSource();
            source.removeOutcome( f );
            f.setSource( node );
            node.addOutcome( f );
            if ( source.isConnector() )
                s.removeNode( source );
        }
    }

    /**
     * @return list of nodes that can be potential targets of the underlying flow.
     */
    public List<? extends Node> getOtherNodes() {
        final Node node = getNode();
        final Node other = getOther();
        final Scenario scenario = node.getScenario();
        final List<Node> result = new ArrayList<Node>( scenario.getNodeCount() );

        final Iterator<Node> nodes = scenario.nodes();
        while ( nodes.hasNext() ) {
            final Node n = nodes.next();
            if ( !node.equals( n ) && ( other.equals( n ) || !n.isConnector() ) )
                result.add( n );
        }

        return result;
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void setMarkedForDeletion( boolean markedForDeletion ) {
        this.markedForDeletion = markedForDeletion;
    }


}
