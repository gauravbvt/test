package com.mindalliance.channels.graph;

import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.ScenarioAnalyst;
import org.apache.wicket.markup.html.link.ImageMap;

import java.io.OutputStream;

/**
 * Information flow diagram generator interface.
 */
public interface FlowDiagram<V, E> {

    /**
     * The PNG format
     */
    static final String PNG = "png";
    /**
     * The SVG format
     */
    static final String SVG = "svg";
    /**
     * The image map format
     */
    static final String IMAGE_MAP = "imap";

    void setGraphRenderer( GraphRenderer<V, E> graphRenderer );

    void setGraphBuilder( GraphBuilder graphBuilder );

    public void setImageDirectory( String imageDirectory );

    /**
     * Produces the PNG stream of a directed graph diagram of the scenario.
     *
     * @param scenario     A scenario
     * @param selectedNode The scenario node currently selected
     * @param analyst      The scenario analyst used to detect issues
     * @param png          Output stream contaiing the diagram as PNG
     * @throws com.mindalliance.channels.graph.DiagramException
     *          when diagram generation fails
     */
    void getPNG( Scenario scenario, Node selectedNode, ScenarioAnalyst analyst, OutputStream png ) throws DiagramException;

    /**
     * Produces the SVG stream of a directed graph diagram of the scenario.
     *
     * @param scenario     A scenario
     * @param selectedNode The scenario node currently selected
     * @param analyst      The scenario analyst used to detect issues
     * @param svg          Output stream contaiing the diagram as SVG
     * @throws com.mindalliance.channels.graph.DiagramException
     *          when diagram generation fails
     */
    void getSVG( Scenario scenario, Node selectedNode, ScenarioAnalyst analyst, OutputStream svg ) throws DiagramException;

    /**
     * Gets an image map component for a directed graph diagram of the scenario
     *
     * @param scenario     A scenario
     * @param analyst      The scenario analyst used to detect issues
     * @return a String
     * @throws com.mindalliance.channels.graph.DiagramException
     *          when diagram generation fails
     */
    String getImageMap( Scenario scenario, ScenarioAnalyst analyst ) throws DiagramException;

}
