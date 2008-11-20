package com.mindalliance.channels.graph;

import com.mindalliance.channels.FlowDiagram;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Part;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.apache.wicket.markup.html.link.ImageMap;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 2:56:50 PM
 */
public class DefaultFlowDiagram implements FlowDiagram {

    static private final String PNG = "png";
    static private final String IMAGE_MAP = "imap";

    /**
     * Produces the PNG stream of a directed graph diagram of the scenario.
     *
     * @param scenario     A scenario
     * @param selectedPart The scenario part currently selected
     * @return An InputStream
     * @throws com.mindalliance.channels.graph.DiagramException when diagram generation fails
     */
    public InputStream getPNG(Scenario scenario, Part selectedPart) throws DiagramException {
        GraphBuilder dotBuilder = new GraphBuilder(scenario);
        return new GraphvizRenderer().render(dotBuilder.getDot(selectedPart), PNG);
    }

    /**
     * Gets an image map component for a directed graph diagram of the scenario
     *
     * @param scenario     A scenario
     * @param selectedPart The scenario part currently selected
     * @return an ImageMap
     * @throws com.mindalliance.channels.graph.DiagramException when diagram generation fails
     */
    public ImageMap getImageMap(Scenario scenario, Part selectedPart) throws DiagramException {
        GraphBuilder dotBuilder = new GraphBuilder(scenario);
        GraphvizRenderer renderer = new GraphvizRenderer();
        InputStream in = renderer.render(dotBuilder.getDot(selectedPart), IMAGE_MAP);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new DiagramException("Failed to read generated diagram", e);
        }
        return new ImageMap(sb.toString());
    }

}
