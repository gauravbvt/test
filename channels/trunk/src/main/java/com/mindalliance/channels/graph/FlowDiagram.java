package com.mindalliance.channels.graph;

import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.graph.DiagramException;
import org.apache.wicket.markup.html.link.ImageMap;

import java.io.InputStream;

/**
 * Information flow diagram generator interface.
 */
public interface FlowDiagram {

    /**
     * Produces the PNG stream of a directed graph diagram of the scenario.
     * @param scenario A scenario
     * @param selectedNode The scenario node currently selected
     * @return An InputStream
     * @throws com.mindalliance.channels.graph.DiagramException when diagram generation fails
     */
    InputStream getPNG( Scenario scenario, Node selectedNode ) throws DiagramException;

    /**
     * Produces the SVG stream of a directed graph diagram of the scenario.
     * @param scenario A scenario
     * @param selectedNode The scenario node currently selected
     * @return An InputStream
     * @throws com.mindalliance.channels.graph.DiagramException when diagram generation fails
     */
    InputStream getSVG( Scenario scenario, Node selectedNode ) throws DiagramException;

    /**
     * Gets an image map component for a directed graph diagram of the scenario
     * @param scenario A scenario
     * @param selectedNode The scenario node currently selected
     * @return an ImageMap
     * @throws com.mindalliance.channels.graph.DiagramException when diagram generation fails
     */
    ImageMap getImageMap( Scenario scenario, Node selectedNode ) throws DiagramException;

}
