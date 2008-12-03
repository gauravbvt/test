package com.mindalliance.channels.pages;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.ScenarioAnalyst;
import com.mindalliance.channels.dao.ScenarioDao;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.FlowDiagram;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.pages.RedirectPage;

import java.text.MessageFormat;

/**
 * PNG view of the flow graph.
 */
public class FlowPage extends Page {

    /** The log. */
    private static final Log LOG = LogFactory.getLog( ExportPage.class );

    /** The scenario being graphed. */
    private Scenario scenario;

    /** The selected node. */
    private Node node;

    public FlowPage( PageParameters parameters ) {
        super( parameters );

        final ScenarioDao scenarioDao = getScenarioDao();
        scenario = ScenarioPage.findScenario( scenarioDao, parameters );

        if ( scenario == null )
            redirectTo( scenarioDao.getDefaultScenario() );

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
            getFlowDiagram().getPNG(
                    scenario, node, getScenarioAnalyst(), getResponse().getOutputStream() );
        } catch ( DiagramException e ) {
            LOG.error( "Error while generating diagram", e );
            // Don't do anuything else --> empty png
        }
    }

    private FlowDiagram<Node,Flow> getFlowDiagram() {
        return ( (Project) getApplication() ).getFlowDiagram();
    }

    private ScenarioAnalyst getScenarioAnalyst() {
        return ( (Project) getApplication() ).getScenarioAnalyst();
    }

    private ScenarioDao getScenarioDao() {
        return ( (Project) getApplication() ).getScenarioDao();
    }

    private void redirectTo( Scenario s ) {
        redirectTo( s.nodes().next() );
    }

    private void redirectTo( Node n ) {
        final long sid = n.getScenario().getId();
        final long nid = n.getId();
        setResponsePage(
                new RedirectPage(
                        MessageFormat.format( "?scenario={0}&node={1}", sid, nid ) ) );   // NON-NLS
    }

}
