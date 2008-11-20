package com.mindalliance.channels;

import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
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
     * @param selectedPart The scenario part currently selected
     * @return An InputStream
     * @throws com.mindalliance.channels.graph.DiagramException when diagram generation fails
     */
    InputStream getPNG( Scenario scenario, Part selectedPart ) throws DiagramException;

    /**
     * Gets an image map component for a directed graph diagram of the scenario
     * @param scenario A scenario
     * @param selectedPart The scenario part currently selected
     * @return an ImageMap
     * @throws com.mindalliance.channels.graph.DiagramException when diagram generation fails
     */
    ImageMap getImageMap( Scenario scenario, Part selectedPart ) throws DiagramException;

}
