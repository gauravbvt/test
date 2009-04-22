package com.mindalliance.channels.export.xml;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Place;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

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
public class ProjectConverter extends AbstractChannelsConverter {

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
        DataQueryObject dqo = getDqo();
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
        context.put( "project", "true" );
        // All entities
        Iterator<ModelObject> entities = dqo.iterateEntities();
        while ( entities.hasNext() ) {
            ModelObject entity = entities.next();
            writer.startNode( entity.getClass().getSimpleName().toLowerCase() );
            context.convertAnother( entity );
            writer.endNode();
        }
        // All scenarios
        for ( Scenario scenario : dqo.list( Scenario.class ) ) {
            writer.startNode( "scenario" );
            context.convertAnother( scenario, new ScenarioConverter() );
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(
            HierarchicalStreamReader reader,
            UnmarshallingContext context ) {
        getIdMap( context );
        getProxyConnectors( context );
        // getPortalConnectors( context );
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
                // Entities
            } else if ( nodeName.equals( "actor" ) ) {
                context.convertAnother( project, Actor.class );
            } else if ( nodeName.equals( "organization" ) ) {
                context.convertAnother( project, Organization.class );
            } else if ( nodeName.equals( "role" ) ) {
                context.convertAnother( project, Role.class );
            } else if ( nodeName.equals( "place" ) ) {
                context.convertAnother( project, Place.class );
                // Scenarios
            } else if ( nodeName.equals( "scenario" ) ) {
                context.convertAnother( project, Scenario.class );
            } else {
                LOG.warn( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        Map<String, Object> state = new HashMap<String, Object>();
        state.put( "idMap", context.get( "idMap" ) );
        state.put( "proxyConnectors", context.get( "proxyConnectors" ) );
        state.put( "portalConnectors", context.get( "portalConnectors" ) );
        return state;
    }

}
