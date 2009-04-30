package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.pages.ChannelsPage;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * PNG view of the flow graph.
 */
public class FlowMapPage extends PngWebPage {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( FlowMapPage.class );
    /**
     * The selected node.
     */
    private Node node;
    /**
     * The scenario to diagram.
     */
    private Scenario scenario;

    public FlowMapPage( PageParameters parameters ) {
        super( parameters );

        DataQueryObject dqo = getDqo();
        scenario = ChannelsPage.findScenario( dqo, parameters );

        if ( scenario == null )
            redirectTo( dqo.getDefaultScenario() );

        else {
            if ( parameters.containsKey( "node" ) && parameters.getString( "node" ).equals( "NONE" ) ) {
                node = null;
            } else {
                node = ChannelsPage.findPart( scenario, parameters );
                if ( node == null )
                    redirectTo( scenario );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram( double[] diagramSize, String orientation ) {
        LOG.info("Generating PNG: scenario = " + scenario + ", node = " + node);
        return getDiagramFactory().newFlowMapDiagram( scenario, node, diagramSize, orientation );
    }


    private void redirectTo( Scenario s ) {
        redirectTo( s.getDefaultPart() );
    }

    private void redirectTo( Node n ) {
        final long sid = n.getScenario().getId();
        final long nid = n.getId();
        setResponsePage(
                new RedirectPage(
                        MessageFormat.format(
                                "?scenario={0,number,0}&node={1,number,0}",
                                sid,
                                nid ) ) );   // NON-NLS
    }

}
