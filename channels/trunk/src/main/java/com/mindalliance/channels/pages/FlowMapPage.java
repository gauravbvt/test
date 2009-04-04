package com.mindalliance.channels.pages;

import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.Diagram;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Response;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.protocol.http.WebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * PNG view of the flow graph.
 */
public class FlowMapPage extends WebPage {

    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( FlowMapPage.class );

    /**
     * The selected node.
     */
    private Node node;
    /**
     * The flow diagram displayed
     */
    private Diagram flowDiagram;

    public FlowMapPage( PageParameters parameters ) {
        super( parameters );

        DataQueryObject dqo = getDqo();
        Scenario scenario = ProjectPage.findScenario( dqo, parameters );

        if ( scenario == null )
            redirectTo( dqo.getDefaultScenario() );

        else {
            if ( parameters.containsKey( "node" ) && parameters.getString( "node" ).equals( "NONE" ) ) {
                node = null;
            } else {
                node = ProjectPage.findPart( scenario, parameters );
                if ( node == null )
                    redirectTo( scenario );
            }
        }
        flowDiagram = Project.diagramFactory().newFlowMapDiagram( scenario, node );
        if ( parameters.containsKey( "size" ) ) {
            double[] size = convertSize( parameters.getString( "size" ) );
            flowDiagram.setDiagramSize( size[0], size[1] );
        }
        if ( parameters.containsKey( "orientation" ) ) {
            flowDiagram.setOrientation( parameters.getString( "orientation" ) );
        }
    }

    private double[] convertSize( String s ) {
        String[] sizes = s.split( "," );
        assert sizes.length == 2;
        double[] size = new double[2];
        size[0] = Double.parseDouble( sizes[0] );
        size[1] = Double.parseDouble( sizes[1] );
        return size;
    }

    @Override
    public String getMarkupType() {
        return "image/png";                                                               // NON-NLS
    }

    /**
     * Directly render the bytes of this page.
     *
     * @param markupStream ignored
     */
    @Override
    protected void onRender( MarkupStream markupStream ) {
        try {
            final Response resp = getWebRequestCycle().getResponse();
            if ( resp instanceof WebResponse )
                setHeaders( (WebResponse) resp );

            flowDiagram.render(
                    DiagramFactory.PNG, getResponse().getOutputStream() );
        } catch ( DiagramException e ) {
            LOG.error( "Error while generating diagram", e );
            // Don't do anuything else --> empty png
        }
    }

    private DataQueryObject getDqo() {
        return ( (Project) getApplication() ).getDqo();
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
