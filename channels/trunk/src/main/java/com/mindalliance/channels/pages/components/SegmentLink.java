package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.Segment;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Convenient wrapper for page links to segments and nodes.
 */
public class SegmentLink extends ExternalLink {

    /** Initial buffer size for building links. */
    private static final int BUFFER_SIZE = 128;

    public SegmentLink( String id, Segment segment, CollaborationModel collaborationModel ) {
        this( id, new PropertyModel<Node>( segment, "defaultPart" ), collaborationModel );                   // NON-NLS
    }

    public SegmentLink( String id, IModel<Node> node, CollaborationModel collaborationModel ) {
        super( id, linkFor( node, collaborationModel ) );
    }

    public SegmentLink( String id, IModel<Node> node, ModelObject expanded, CollaborationModel collaborationModel ) {
        super( id, linkFor( node, expanded.getId(), collaborationModel ) );
    }

    /**
     * Shorthand for no expansions.
     * @param node the node
     * @return a relative url
     */
    public static IModel<String> linkFor( IModel<Node> node, CollaborationModel collaborationModel ) {
        return linkFor( node, new HashSet<Long>(), collaborationModel );
    }

    /**
     * Shorthand for expanding only one section.
     * @param node the node
     * @param expansion the section to expand
     * @return a relative url
     */
    public static IModel<String> linkFor( IModel<Node> node, long expansion, CollaborationModel collaborationModel ) {
        Set<Long> set = new HashSet<Long>();
        set.add( expansion );
        return linkFor( node, set, collaborationModel );
    }

    /**
     * Return a stable link to a given node in a segment.
     * @param nodeModel the node model
     * @param expansions what to expand in the target
     * @return a relative url
     */
    public static IModel<String> linkFor(
            final IModel<Node> nodeModel, final Set<Long> expansions, final CollaborationModel collaborationModel ) {

        return new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return linkStringFor( nodeModel.getObject(), expansions, collaborationModel );
            }
        };
    }

    /**
     * Return a stable link to a given node in a segment.
     * @param node the node model
     * @param expansions what to expand in the target
     * @return a relative url
     */
    public static String linkStringFor( Node node, Set<Long> expansions, CollaborationModel collaborationModel ) {
        String exs = expandString( expansions );
        Node n = node;
        if ( n.isConnector() ) {
            Iterator<Flow> outs = n.sends();
            Flow f;
            if ( outs.hasNext() ) {
                f = outs.next();
                n = f.getTarget();
            } else {
                f = n.receives().next();
                n = f.getSource();
            }
            exs = expandString( f.getId() );
        }
        return MessageFormat.format( "?model={0}&v={1,number,0}&segment={2,number,0}&node={3,number,0}{4}",
                collaborationModel,
                                     collaborationModel.getVersion(),
                                     n.getSegment().getId(),
                                     n.getId(),
                                     exs );
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
