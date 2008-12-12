package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import org.apache.wicket.markup.html.link.ExternalLink;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Convenient wrapper for page links to scenarios and nodes.
 */
public class ScenarioLink extends ExternalLink {

    public ScenarioLink( String id, Scenario scenario ) {
        this( id, scenario.getDefaultPart() );
    }

    public ScenarioLink( String id, Node node ) {
        super( id, linkFor( node ) );
    }

    /**
     * Return a stable link to a given node in a scenario.
     * @param node the node
     * @return a relative url
     */
    public static String linkFor( Node node ) {
        if ( node.isConnector() ) {
            final Iterator<Flow> outs = node.outcomes();
            final boolean isOutput = outs.hasNext();
            final Flow f = isOutput ? outs.next() : node.requirements().next();
            final Node target = isOutput ? f.getTarget() : f.getSource();
            final Set<Long> s = new HashSet<Long>();
            s.add( f.getId() );
            return linkFor( target, s );
        } else
            return MessageFormat.format( "?scenario={0}&node={1}",                        // NON-NLS
                                         node.getScenario().getId(),
                                         node.getId() );
    }

    /**
     * Return a stable link to a given node in a scenario.
     * @param node the node
     * @param expansions sections to expand
     * @return a relative url
     */
    public static String linkFor( Node node, Set<Long> expansions ) {
        final long sid = node.getScenario().getId();
        final long nid = node.getId();
        final StringBuffer exps = new StringBuffer( 128 );
        for ( long id : expansions ) {
            exps.append( "&expand=" );                                                    // NON-NLS
            exps.append( Long.toString( id ) );
        }
        return MessageFormat.format( "?scenario={0}&node={1}{2}",                         // NON-NLS
                                     sid, nid, exps );

    }
}
