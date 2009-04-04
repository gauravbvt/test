package com.mindalliance.channels.pages;

import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.network.ScenarioRelationship;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Response;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Generation of the plan map PNG.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 3, 2009
 * Time: 4:26:10 PM
 */
public class PlanMapPage extends WebPage {
    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanMapPage.class );

    /**
     * The selected scenario.
     */
    private Scenario scenario;
    /**
     * The selected scenario relationship.
     */
    private ScenarioRelationship scRel;
    /**
     * The plan map diagram displayed
     */
    private Diagram planMapDiagram;

    public PlanMapPage( PageParameters parameters ) {
        super( parameters );

        DataQueryObject dqo = getDqo();

        if ( parameters.containsKey( "scenario" ) && !parameters.getString( "scenario" ).equals( "NONE" ) ) {
            Long scenarioId = Long.valueOf(parameters.getString( "scenario" ));
            try {
                scenario = dqo.find(Scenario.class, scenarioId);
            } catch ( NotFoundException e ) {
                LOG.warn("Scenario not found at :" + scenarioId, e);
            }
        }
        if ( parameters.containsKey( "connection" ) && !parameters.getString( "connection" ).equals( "NONE" ) ) {
            Long scRelId = Long.valueOf(parameters.getString( "connection" ));
            scRel = new ScenarioRelationship();
            scRel.setId( scRelId, getDqo() );
        }
        List<Scenario> allScenarios = dqo.list( Scenario.class );
        planMapDiagram = Project.diagramFactory().newPlanMapDiagram(allScenarios, scenario, scRel );
        if ( parameters.containsKey( "size" ) ) {
            double[] size = convertSize( parameters.getString( "size" ) );
            planMapDiagram.setDiagramSize( size[0], size[1] );
        }
        if ( parameters.containsKey( "orientation" ) ) {
            planMapDiagram.setOrientation( parameters.getString( "orientation" ) );
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
        return "image/png";
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

            planMapDiagram.render(
                    DiagramFactory.PNG, getResponse().getOutputStream() );
        } catch ( DiagramException e ) {
            LOG.error( "Error while generating diagram", e );
            // Don't do anuything else --> empty png
        }
    }

    private DataQueryObject getDqo() {
        return ( (Project) getApplication() ).getDqo();
    }

}
