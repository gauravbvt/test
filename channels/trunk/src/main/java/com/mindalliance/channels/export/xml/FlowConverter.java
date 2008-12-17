package com.mindalliance.channels.export.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.ModelObject;

import java.util.Map;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 3:28:38 PM
 */
public class FlowConverter implements Converter {

    public boolean canConvert( Class aClass ) {
        return Flow.class.isAssignableFrom( aClass );
    }

    public void marshal( Object object, HierarchicalStreamWriter writer, MarshallingContext context ) {
        Flow flow = (Flow) object;
        String sourceId;
        String targetId;
        if ( flow.isInternal() ) {
            sourceId = getId( flow.getSource() );
            targetId = getId( flow.getTarget() );
        } else {
            ExternalFlow xflow = (ExternalFlow) flow;
            if ( xflow.isInput() ) {
                sourceId = "connector";
                targetId = getId( xflow.getPart() );
            } else {
                sourceId = getId( xflow.getPart() );
                targetId = "connector";
            }
        }
        writer.addAttribute( "source", sourceId );
        writer.addAttribute( "target", targetId );
        writer.startNode( "name" );
        writer.setValue( flow.getName() );
        writer.endNode();
        writer.startNode( "description" );
        writer.setValue( flow.getDescription() );
        writer.endNode();
        if ( flow.getChannel() != null ) {
            writer.startNode( "channel" );
            writer.setValue( flow.getChannel() );
            writer.endNode();
        }
        writer.startNode( "critical" );
        writer.setValue( String.valueOf( flow.isCritical() ) );
        writer.endNode();
        writer.startNode( "askedFor" );
        writer.setValue( String.valueOf( flow.isAskedFor() ) );
        writer.endNode();
        if ( flow.getMaxDelay() != null ) {
            writer.startNode( "maxDelay" );
            writer.setValue( flow.getMaxDelay() );
            writer.endNode();
        }
    }

    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Map<String, Long> idMap = (Map<String, Long>) context.get( "idMap" );
        Scenario scenario = (Scenario) context.get( "scenario" );
        String sourceId = reader.getAttribute( "source" );
        String targetId = reader.getAttribute( "target" );
        Flow flow = createFlow( sourceId, targetId, scenario, idMap );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "description" ) ) {
                flow.setDescription( reader.getValue() );
            } else if ( nodeName.equals( "name" ) ) {
                String name = reader.getValue();
                flow.setName( name );
            } else if ( nodeName.equals( "channel" ) ) {
                flow.setChannel( reader.getValue() );
            } else if ( nodeName.equals( "maxDelay" ) ) {
                flow.setMaxDelay( reader.getValue() );
            } else if ( nodeName.equals( "askedFor" ) ) {
                flow.setAskedFor( reader.getValue().equals( "true" ) );
            } else if ( nodeName.equals( "critical" ) ) {
                flow.setCritical( reader.getValue().equals( "true" ) );
            } else {
                throw new ConversionException( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return flow;
    }

    private Flow createFlow( String sourceId, String targetId, Scenario scenario, Map<String, Long> idMap ) {
        Flow flow;
        if ( sourceId.equals( "connector" ) ) {
            flow = scenario.getNode( idMap.get( targetId ) ).createRequirement();
        } else if ( targetId.equals( "connector" ) ) {
            flow = scenario.getNode( idMap.get( sourceId ) ).createOutcome();
        } else {
            flow = scenario.connect( scenario.getNode( idMap.get( sourceId ) ),
                    scenario.getNode( idMap.get( targetId ) ) );
        }
        return flow;
    }

    private String getId( ModelObject modelObject ) {
        if ( modelObject instanceof Connector ) {
            return "connector";
        } else {
            return String.valueOf( modelObject.getId() );
        }
    }

}
