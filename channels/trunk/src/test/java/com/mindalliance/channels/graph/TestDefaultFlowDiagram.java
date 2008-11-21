package com.mindalliance.channels.graph;

import junit.framework.TestCase;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.dao.FireScenario;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.wicket.markup.html.link.ImageMap;
import org.apache.wicket.markup.MarkupStream;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 21, 2008
 * Time: 8:57:25 AM
 */
public class TestDefaultFlowDiagram extends TestCase {

    FlowDiagram<Node,Flow> flowDiagram;

    @Override
    protected void setUp() {
        GraphvizRenderer<Node,Flow> graphRenderer = new GraphvizRenderer<Node, Flow>();
        graphRenderer.setDotPath("/usr/bin/dot");
        flowDiagram = new DefaultFlowDiagram();
        flowDiagram.setGraphRenderer(graphRenderer);
        flowDiagram.setGraphBuilder(new DefaultGraphBuilder());
    }

    public void testGetSVG() {
        Scenario scenario = new FireScenario();
        Node node = scenario.nodes().next();
        try {
            StringWriter writer = new StringWriter();
            BufferedReader reader = new BufferedReader(new InputStreamReader(flowDiagram.getSVG(scenario, node)));
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.write('\n');
            }
            String svg = writer.toString();
            assertFalse(svg.isEmpty());
            assertTrue(svg.startsWith("<?xml"));
            System.out.print(svg);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    /* public void testGetImageMap() {
        Scenario scenario = new FireScenario();
        Node node = scenario.nodes().next();
        try {
            ImageMap imageMap = defaultFlowDiagram.getImageMap(scenario, node);
            MarkupStream stream = imageMap.getMarkupStream();
            String map = stream.toString();
            assertFalse(map.isEmpty());
            assertTrue(map.startsWith("<"));
            System.out.print(map);
        } catch (Exception e) {
            fail(e.toString());
        }
    }*/

}
