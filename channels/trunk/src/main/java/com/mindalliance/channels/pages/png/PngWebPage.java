package com.mindalliance.channels.pages.png;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.pages.Project;

/**
 * Abstract superclass for all PNG-generating pages.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 7, 2009
 * Time: 10:13:12 AM
 */
public abstract class PngWebPage extends WebPage {

    /**
      * The log.
      */
     private static final Logger LOG = LoggerFactory.getLogger( PngWebPage.class );

    private PageParameters parameters;


    public PngWebPage( PageParameters parameters ) {
        super( parameters );
        this.parameters = parameters;
    }

    /**
     * Get data query object.
     * @return a data query object
     */
    protected DataQueryObject getDqo() {
        return ( (Project) getApplication() ).getDqo();
    }

    /**
     * Get a diagram factory.
     * @return a diagram factory
     */
    protected DiagramFactory getDiagramFactory() {
        return Project.diagramFactory();
    }

    @Override
    public String getMarkupType() {
        return "image/png";
    }

    /**
     * COnvert size parameter.
     * @param s size parameter value
     * @return an array of two double values
     */
    protected double[] convertSize( String s ) {
        String[] sizes = s.split( "," );
        assert sizes.length == 2;
        double[] size = new double[2];
        size[0] = Double.parseDouble( sizes[0] );
        size[1] = Double.parseDouble( sizes[1] );
        return size;
    }

    /**
     * Configure diagram for size and orientation.
     * @param diagram a diagram
     */
    protected void configureDiagram( Diagram diagram ) {
        if ( parameters.containsKey( "size" ) ) {
            double[] size = convertSize( parameters.getString( "size" ) );
            diagram.setDiagramSize( size[0], size[1] );                                
        }
        if ( parameters.containsKey( "orientation" ) ) {
            diagram.setOrientation( parameters.getString( "orientation" ) );
        }        
    }

    /**
     * Directly render the bytes of this page.
     *
     * @param markupStream ignored
     */
    @Override
    protected void onRender( MarkupStream markupStream ) {
        //TODO - is rendered twice (select part in flow map)
        Diagram diagram = makeDiagram();
        configureDiagram( diagram );
        try {
            final Response resp = getWebRequestCycle().getResponse();
            if ( resp instanceof WebResponse )
                setHeaders( (WebResponse) resp );
            diagram.render( DiagramFactory.PNG, getResponse().getOutputStream() );
        } catch ( DiagramException e ) {
            LOG.error( "Error while generating diagram", e );
            // Don't do anything else --> empty png
        }
    }

    /**
     * Create the diagram.
     * @return a diagram
     */
    abstract protected Diagram makeDiagram();


}
