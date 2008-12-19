package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ModelObject;
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

    public ScenarioLink( String id, Node node, ModelObject expanded ) {
        super( id, linkFor( node, expanded.getId() ) );
    }

    /**
     * Shorthand for no expansions.
     * @param node the node
     * @return a relative url
     */
    public static String linkFor( Node node ) {
        return linkFor( node, new HashSet<Long>() );
    }

    /**
     * Shorthand for expanding only one section.
     * @param node the node
     * @param expansion the section to expand
     * @return a relative url
     */
    public static String linkFor( Node node, long expansion ) {
        final Set<Long> set = new HashSet<Long>();
        set.add( expansion );
        return linkFor( node, set );
    }

    /**
     * Return a stable link to a given node in a scenario.
     * @param node the node
     * @param expansions what to expand in the target
     * @return a relative url
     */
    public static String linkFor( Node node, Set<Long> expansions ) {
        if ( node.isConnector() ) {
            final Iterator<Flow> outs = node.outcomes();
            final boolean isOutput = outs.hasNext();
            final Flow f = isOutput ? outs.next() : node.requirements().next();
            final Node target = isOutput ? f.getTarget() : f.getSource();
            return linkFor( target, f.getId() );
        } else
            return MessageFormat.format( "?scenario={0}&node={1}{2}",                     // NON-NLS
                                         node.getScenario().getId(),
                                         node.getId(),
                                         expandString( expansions ) );
    }

    private static String expandString( Set<Long> expansions ) {
        final StringBuilder exps = new StringBuilder( 128 );
        for ( long id : expansions ) {
            exps.append( "&expand=" );                                                    // NON-NLS
            exps.append( Long.toString( id ) );
        }
        return exps.toString();
    }
}
