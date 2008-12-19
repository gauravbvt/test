package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.ScenarioAnalyst;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Details of an expanded requirement.
 */
public class ExpandedReqPanel extends ExpandedFlowPanel {

    public ExpandedReqPanel( String id, Flow flow ) {
        super( id, flow, false );

        addOtherField();
        final Node node = getOther();
        if ( node.isConnector() && node.getScenario().equals( getNode().getScenario() ) ) {
            add( new ConnectedFlowList( "others", (Connector) node ) );                   // NON-NLS
        } else {
            add( new Label( "others", "" ) );                                             // NON-NLS
        }

        addChannelRow( flow );
    }

    private void addChannelRow( Flow flow ) {
        final WebMarkupContainer channelRow = new WebMarkupContainer( "channel-row" );    // NON-NLS

        final FormComponent<?> textField = new TextField<String>( "channel" );            // NON-NLS

        final FormComponentLabel label =
                new FormComponentLabel( "channel-label", textField );                     // NON-NLS
        channelRow.add( label );
        channelRow.add( textField );

        // Add style mods from scenario analyst.
        final ScenarioAnalyst analyst = ( (Project) getApplication() ).getScenarioAnalyst();
        final String issue = analyst.getIssuesSummary( getFlow(), textField.getId() );
        if ( !issue.isEmpty() ) {
            textField.add(
                new AttributeModifier( "class", true, new Model<String>( "error" ) ) );   // NON-NLS
            textField.add(
                new AttributeModifier( "title", true, new Model<String>( issue ) ) );     // NON-NLS
        }

        label.add( new Label( "channel-title",                                            // NON-NLS
                          flow.isAskedFor() ? "Source channel:" : "Target channel:" ) );

        channelRow.setVisible( !getOther().isConnector() || !flow.isAskedFor() );
        add( channelRow );
    }

    /** {@inheritDoc} */
    @Override
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
}
