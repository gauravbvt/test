package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Convenient wrapper for page links to scenarios and nodes.
 */
public class ScenarioLink extends ExternalLink {

    /** Initial buffer size for building links. */
    private static final int BUFFER_SIZE = 128;

    public ScenarioLink( String id, Scenario scenario ) {
        this( id, new PropertyModel<Node>( scenario, "defaultPart" ) );                   // NON-NLS
    }

    public ScenarioLink( String id, IModel<Node> node ) {
        super( id, linkFor( node ) );
    }

    public ScenarioLink( String id, IModel<Node> node, ModelObject expanded ) {
        super( id, linkFor( node, expanded.getId() ) );
    }

    /**
     * Shorthand for no expansions.
     * @param node the node
     * @return a relative url
     */
    public static IModel<String> linkFor( IModel<Node> node ) {
        return linkFor( node, new HashSet<Long>() );
    }

    /**
     * Shorthand for expanding only one section.
     * @param node the node
     * @param expansion the section to expand
     * @return a relative url
     */
    public static IModel<String> linkFor( IModel<Node> node, long expansion ) {
        Set<Long> set = new HashSet<Long>();
        set.add( expansion );
        return linkFor( node, set );
    }

    /**
     * Return a stable link to a given node in a scenario.
     * @param nodeModel the node model
     * @param expansions what to expand in the target
     * @return a relative url
     */
    public static IModel<String> linkFor(
            final IModel<Node> nodeModel, final Set<Long> expansions ) {

        return new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return linkStringFor( nodeModel.getObject(), expansions );
            }
        };
    }

    /**
     * Return a stable link to a given node in a scenario.
     * @param node the node model
     * @param expansions what to expand in the target
     * @return a relative url
     */
    public static String linkStringFor( Node node, Set<Long> expansions ) {
        String exs = expandString( expansions );
        Node n = node;
        if ( n.isConnector() ) {
            Iterator<Flow> outs = n.outcomes();
            Flow f;
            if ( outs.hasNext() ) {
                f = outs.next();
                n = f.getTarget();
            } else {
                f = n.requirements().next();
                n = f.getSource();
            }
            exs = expandString( f.getId() );
        }
        return MessageFormat.format( "?scenario={0,number,0}&amp;node={1,number,0}{2}",       // NON-NLS
                                     n.getScenario().getId(),
                                     n.getId(), exs );
    }

    private static String expandString( long id ) {
        Set<Long> ids = new HashSet<Long>();
        ids.add( id );
        return expandString( ids );
    }

    private static String expandString( Set<Long> expansions ) {
        StringBuilder exps = new StringBuilder( BUFFER_SIZE );
        for ( long id : expansions ) {
            exps.append( "&expand=" );                                                    // NON-NLS
            exps.append( Long.toString( id ) );
        }
        return exps.toString();
    }
}
