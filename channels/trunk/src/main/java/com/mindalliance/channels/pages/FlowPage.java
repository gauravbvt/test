package com.mindalliance.channels.pages;

import com.mindalliance.channels.Dao;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.ScenarioAnalyst;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.FlowDiagram;
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
public class FlowPage extends WebPage {

    /** The log. */
    private static final Logger LOG = LoggerFactory.getLogger( ExportPage.class );

    /** The scenario being graphed. */
    private Scenario scenario;

    /** The selected node. */
    private Node node;

    public FlowPage( PageParameters parameters ) {
        super( parameters );

        final Dao dao = getScenarioDao();
        scenario = ScenarioPage.findScenario( dao, parameters );

        if ( scenario == null )
            redirectTo( dao.getDefaultScenario() );

        else {
            node = ScenarioPage.findNode( scenario, parameters );
            if ( node == null )
                redirectTo( scenario );
        }
    }

    @Override
    public String getMarkupType() {
        return "image/png";                                                               // NON-NLS
    }

    /**
     * Directly render the bytes of this page.
     * @param markupStream ignored
     */
    @Override
    protected void onRender( MarkupStream markupStream ) {
        try {
            final Response resp = getWebRequestCycle().getResponse();
            if ( resp instanceof WebResponse )
                setHeaders( (WebResponse) resp );

            getFlowDiagram().getPNG(
                    scenario, node, getScenarioAnalyst(), getResponse().getOutputStream() );
        } catch ( DiagramException e ) {
            LOG.error( "Error while generating diagram", e );
            // Don't do anuything else --> empty png
        }
    }

    private FlowDiagram getFlowDiagram() {
        return ( (Project) getApplication() ).getFlowDiagram();
    }

    private ScenarioAnalyst getScenarioAnalyst() {
        return ( (Project) getApplication() ).getScenarioAnalyst();
    }

    private Dao getScenarioDao() {
        return ( (Project) getApplication() ).getDao();
    }

    private void redirectTo( Scenario s ) {
        redirectTo( s.getDefaultPart() );
    }

    private void redirectTo( Node n ) {
        final long sid = n.getScenario().getId();
        final long nid = n.getId();
        setResponsePage(
                new RedirectPage(
                        MessageFormat.format( "?scenario={0}&node={1}", sid, nid ) ) );   // NON-NLS
    }

}
