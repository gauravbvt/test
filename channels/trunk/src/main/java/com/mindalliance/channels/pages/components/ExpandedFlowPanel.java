package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Connector;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.ScenarioAnalyst;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.AttributeModifier;
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
import java.util.Set;
import java.util.TreeSet;

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
        add( new ScenarioLink( "hide", getNode() ) );                                     // NON-NLS
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

        // Add style mods from scenario analyst.
        final ScenarioAnalyst analyst = ( (Project) getApplication() ).getScenarioAnalyst();
        final String issue = analyst.getIssuesSummary( getFlow(), component.getId() );
        if ( !issue.isEmpty() ) {
            component.add(
                new AttributeModifier( "class", true, new Model<String>( "error" ) ) );   // NON-NLS
            component.add(
                new AttributeModifier( "title", true, new Model<String>( issue ) ) );     // NON-NLS
        }
    }

    private void addOtherField() {
        final DropDownChoice<Node> other = new DropDownChoice<Node>( "other",             // NON-NLS
            new PropertyModel<Node>( this, "other" ),                                     // NON-NLS
            new PropertyModel<List<? extends Node>>( this, "otherNodes" ) );              // NON-NLS

        final Node otherNode = getOther();
        final ScenarioLink details = new ScenarioLink( "other-details", otherNode );      // NON-NLS
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
        final Flow f = getFlow();
        return f.isInternal() ? isOutcome() ? f.getTarget()
                                            : f.getSource()
                              : ( (ExternalFlow) f ).getConnector();
    }

    /**
     * Set the node at the other side of this flow
     * @param other the new source or target
     */
    public void setOther( Node other ) {
        final Flow f = getFlow();
        final Scenario s = f.getSource().getScenario();
        if ( isOutcome() ) {
            final Node source = f.getSource();
            f.disconnect();
            setFlow( s.connect( source, other ) );

        } else {
            final Node target = f.getTarget();
            f.disconnect();
            setFlow( s.connect( other, target ) );
        }
    }

    /**
     * @return list of nodes that can be potential targets of the underlying flow.
     */
    public List<? extends Node> getOtherNodes() {
        final Node node = getNode();
        final Node other = getOther();
        final Scenario scenario = node.getScenario();
        final Set<Node> result = new TreeSet<Node>();

        // Add other parts of this scenario
        final Iterator<Node> nodes = scenario.nodes();
        while ( nodes.hasNext() ) {
            final Node n = nodes.next();
            if ( !node.equals( n ) && ( other.equals( n ) || !n.isConnector() ) )
                result.add( n );
        }

        // Add inputs/outputs of other scenarios
        final Iterator<Scenario> scenarios = scenario.getDao().scenarios();
        while ( scenarios.hasNext() ) {
            final Scenario s = scenarios.next();
            if ( !scenario.equals( s ) ) {
                final Iterator<Connector> c = isOutcome() ? s.inputs() : s.outputs();
                while ( c.hasNext() )
                    result.add( c.next() );
            }
        }

        return new ArrayList<Node>( result );
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void setMarkedForDeletion( boolean markedForDeletion ) {
        this.markedForDeletion = markedForDeletion;
    }


}
