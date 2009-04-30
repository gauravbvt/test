package com.mindalliance.channels.export.xml;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Place;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plan XML converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 22, 2009
 * Time: 12:05:43 PM
 */
public class PlanConverter extends AbstractChannelsConverter {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( PlanConverter.class );

    public PlanConverter() {
    }


    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Channels.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(
            Object obj,
            HierarchicalStreamWriter writer,
            MarshallingContext context ) {
        Channels app = (Channels) obj;
        DataQueryObject dqo = getDqo();
        writer.addAttribute( "uri", app.getUri() );
        writer.addAttribute( "version", app.getExporter().getVersion() );
        writer.addAttribute( "date", new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() ) );
        writer.startNode( "name" );
        writer.setValue( app.getPlanName() );
        writer.endNode();
        writer.startNode( "client" );
        writer.setValue( app.getClient() );
        writer.endNode();
        writer.startNode( "description" );
        writer.setValue( app.getDescription() );
        writer.endNode();
        context.put( "app", "true" );
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
        Channels app = Channels.instance();
        String uri = reader.getAttribute( "uri" );
        app.setUri( uri );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "name" ) ) {
                app.setPlanName( reader.getValue() );
            } else if ( nodeName.equals( "client" ) ) {
                app.setClient( reader.getValue() );
            } else if ( nodeName.equals( "description" ) ) {
                app.setDescription( reader.getValue() );
                // Entities
            } else if ( nodeName.equals( "actor" ) ) {
                context.convertAnother( app, Actor.class );
            } else if ( nodeName.equals( "organization" ) ) {
                context.convertAnother( app, Organization.class );
            } else if ( nodeName.equals( "role" ) ) {
                context.convertAnother( app, Role.class );
            } else if ( nodeName.equals( "place" ) ) {
                context.convertAnother( app, Place.class );
                // Scenarios
            } else if ( nodeName.equals( "scenario" ) ) {
                context.convertAnother( app, Scenario.class );
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
