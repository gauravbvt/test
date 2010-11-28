package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.pages.RedirectPage;

import java.text.MessageFormat;

/**
 * PNG view of the flow graph.
 */
public class FlowMapPage extends PngWebPage {

    /**
     * Class logger.
     */
    // private static final Logger LOG = LoggerFactory.getLogger( FlowMapPage.class );
    /**
     * The selected node.
     */
    private Node node;
    /**
     * The segment to diagram.
     */
    private Segment segment;
    /**
     * Whether to show goals.
     */
    private boolean showingGoals;
    /**
     * Whether to show connectors.
     */
    private boolean showingConnectors;

    public FlowMapPage( PageParameters parameters ) {
        super( parameters );

        QueryService queryService = getQueryService();
        segment = PlanPage.findSegment( queryService, parameters );

        if ( segment == null )
            redirectTo( queryService.getDefaultSegment() );

        else {
            if ( parameters.containsKey( "node" ) && parameters.getString( "node" ).equals( "NONE" ) ) {
                node = null;
            } else {
                node = PlanPage.findPart( segment, parameters );
                if ( node == null )
                    redirectTo( segment );
            }
        }
        showingGoals = parameters.containsKey( "showingGoals" )
                && parameters.getBoolean( "showingGoals" );
        showingConnectors = parameters.containsKey( "showingConnectors" )
                && parameters.getBoolean( "showingConnectors" );
    }

    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram( double[] diagramSize, String orientation ) throws DiagramException {
        return getDiagramFactory().newFlowMapDiagram(
                segment,
                node,
                diagramSize,
                orientation,
                showingGoals,
                showingConnectors );
    }


    private void redirectTo( Segment s ) {
        redirectTo( s.getDefaultPart() );
    }

    private void redirectTo( Node n ) {
        final long sid = n.getSegment().getId();
        final long nid = n.getId();
        setResponsePage(
                new RedirectPage(
                        MessageFormat.format(
                                "?segment={0,number,0}&node={1,number,0}&showingGoals={2}&showingConnectors={3}",
                                sid,
                                nid,
                                showingGoals,
                                showingConnectors ) ) );
    }

}
