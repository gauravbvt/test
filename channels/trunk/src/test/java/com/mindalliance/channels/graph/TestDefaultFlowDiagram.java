package com.mindalliance.channels.graph;

import junit.framework.TestCase;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.dao.FireScenario;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

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

    FlowDiagram<Node, Flow> flowDiagram;

    @Override
    protected void setUp() {
        GraphvizRenderer<Node, Flow> graphRenderer = new GraphvizRenderer<Node, Flow>();
        graphRenderer.setDotPath( "/usr/bin" );
        graphRenderer.setAlgo( "neato" );
        flowDiagram = new DefaultFlowDiagram();
        flowDiagram.setGraphRenderer( graphRenderer );
        flowDiagram.setGraphBuilder( new DefaultGraphBuilder() );
        flowDiagram.setImageDirectory( "src/site/resources/images" );
    }

    public void testGetSVG() {
        Scenario scenario = new FireScenario();
        Node selectedNode = findSelected(scenario);
        try {
            StringWriter writer = new StringWriter();
            BufferedReader reader = new BufferedReader( new InputStreamReader( flowDiagram.getSVG( scenario, selectedNode ) ) );
            String line;
            while ( ( line = reader.readLine() ) != null ) {
                writer.write( line );
                writer.write( '\n' );
            }
            String svg = writer.toString();
            assertFalse( svg.isEmpty() );
            assertTrue( svg.startsWith( "<?xml" ) );
            System.out.print( svg );
        } catch ( Exception e ) {
            fail( e.toString() );
        }
    }

    public void testGetPNG() {
        Scenario scenario = new FireScenario();
        Node selectedNode = findSelected(scenario);
        try {
            FileOutputStream fileOut = new FileOutputStream( "test.png" );
            DataInputStream data = new DataInputStream( flowDiagram.getPNG( scenario, selectedNode ) );
            int count = 0;
            int size = 0;
            while ( ( size = data.available() ) > 0 ) {
                byte[] b = new byte[size];
                count += data.read( b );
                fileOut.write( b );
            }
            fileOut.flush();
            fileOut.close();
            assertTrue( count > 0 );
        } catch ( IOException e ) {
            fail( e.toString() );
        }
        catch ( DiagramException e ) {
            fail( e.toString() );
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

    private Node findSelected( Scenario scenario ) {
        Node selectedNode = null;
        Iterator<Node> nodes = scenario.nodes();
        if ( nodes.hasNext() ) {
            selectedNode = nodes.next();
            while ( !selectedNode.isPart() || nodes.hasNext() ) {
                selectedNode = nodes.next();
            }
        }
        return selectedNode;
    }

}
