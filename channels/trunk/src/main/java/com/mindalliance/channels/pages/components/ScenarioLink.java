package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.markup.html.link.ExternalLink;

import java.text.MessageFormat;

/**
 * Convenient wrapper for page links to scenarios and nodes.
 */
public class ScenarioLink extends ExternalLink {

    public ScenarioLink( String id, Scenario scenario ) {
        this( id, scenario, scenario.nodes().next() );
    }

    public ScenarioLink( String id, Scenario scenario, Node node ) {
        super( id, linkFor( scenario, node ) );
    }

    /**
     * Return a stable link to a given node in a scenario.
     * @param scenario the scenario
     * @param node the node
     * @return a relative url
     */
    public static String linkFor( Scenario scenario, Node node ) {
        return MessageFormat.format( "?scenario={0}&node={1}",                            // NON-NLS
                                     scenario.getId(),
                                     node.getId() );
    }
}
