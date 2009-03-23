package com.mindalliance.channels.export.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.Scenario;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project XML converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 22, 2009
 * Time: 12:05:43 PM
 */
public class ProjectConverter implements Converter {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( ProjectConverter.class );

    public ProjectConverter() {
    }


    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Project.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(
            Object obj,
            HierarchicalStreamWriter writer,
            MarshallingContext context ) {
        Project project = (Project) obj;
        Service service = project.getService();
        writer.addAttribute( "uri", project.getUri() );
        writer.addAttribute( "version", project.getExporter().getVersion() );
        writer.addAttribute( "date", new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() ) );
        writer.startNode( "name" );
        writer.setValue( project.getProjectName() );
        writer.endNode();
        writer.startNode( "client" );
        writer.setValue( project.getClient() );
        writer.endNode();
        writer.startNode( "description" );
        writer.setValue( project.getDescription() );
        writer.endNode();
        for ( Scenario scenario : service.list( Scenario.class ) ) {
            writer.startNode( "scenario" );
            context.convertAnother( scenario, new ScenarioConverter( ) );
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(
            HierarchicalStreamReader reader,
            UnmarshallingContext context ) {
        Map<String, Long> idMap = new HashMap<String,Long>();
        context.put( "idMap", idMap );
        Project project = Project.getProject();
        String uri = reader.getAttribute( "uri" );
        project.setUri( uri );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "name" ) ) {
                project.setProjectName( reader.getValue() );
            } else if ( nodeName.equals( "client" ) ) {
                project.setClient( reader.getValue() );
            } else if ( nodeName.equals( "description" ) ) {
                project.setDescription( reader.getValue() );
            } else if ( nodeName.equals( "scenario" ) ) {
                context.convertAnother( project, Scenario.class );
            } else {
                LOG.warn( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return context.get("idMap");
    }

}
