package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.Delay;
import com.mindalliance.channels.pages.Project;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XStream scenario converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 1:47:49 PM
 */
public class ScenarioConverter extends AbstractChannelsConverter {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ScenarioConverter.class );

    public ScenarioConverter() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Scenario.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal( Object object, HierarchicalStreamWriter writer,
                         MarshallingContext context ) {
        Scenario scenario = (Scenario) object;
        Project project = Project.getProject();
        DataQueryObject dqo = getDqo();
        context.put( "scenario", scenario );
        writer.addAttribute( "project", project.getUri() );
        writer.addAttribute( "version", project.getExporter().getVersion() );
        writer.addAttribute( "date", new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() ) );
        writer.addAttribute( "id", String.valueOf( scenario.getId() ) );
        writer.addAttribute( "name", scenario.getName() );
        writer.startNode( "description" );
        writer.setValue( scenario.getDescription() );
        writer.endNode();
        if ( scenario.getLocation() != null ) {
            writer.startNode( "location" );
            writer.setValue( scenario.getLocation().getName() );
            writer.endNode();
        }
        if ( scenario.isIncident() ) {
            writer.startNode( "incident" );
            writer.setValue( "" + scenario.isIncident() );
            writer.endNode();
        }
        if ( scenario.isSelfTerminating() ) {
            writer.startNode( "expected-duration" );
            writer.setValue( scenario.getCompletionTime().toString() );
            writer.endNode();
        }
        for ( Part initiator : scenario.getInitiators() ) {
            writer.startNode( "initiator" );
            writer.addAttribute( "scenario", initiator.getScenario().getName() );
            writer.startNode( "scenario-description" );
            writer.setValue( initiator.getScenario().getDescription() );
            writer.endNode();
            ConverterUtils.writePartSpecification( initiator, writer );
            writer.endNode();
        }
        // All entities if not within a project export
        if ( context.get( "project" ) == null ) {
            Iterator<ModelObject> entities = dqo.iterateEntities();
            while ( entities.hasNext() ) {
                ModelObject entity = entities.next();
                writer.startNode( entity.getClass().getSimpleName().toLowerCase() );
                context.convertAnother( entity );
                writer.endNode();
            }
        }
        // Scenario user issues
        List<Issue> issues = dqo.findAllUserIssues( scenario );
        for ( Issue issue : issues ) {
            writer.startNode( "issue" );
            context.convertAnother( issue );
            writer.endNode();
        }
        // Parts
        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            writer.startNode( "part" );
            context.convertAnother( parts.next() );
            writer.endNode();
        }
        Iterator<Flow> flows = scenario.flows();
        while ( flows.hasNext() ) {
            writer.startNode( "flow" );
            Flow flow = flows.next();
            writer.addAttribute( "id", String.valueOf( flow.getId() ) );
            writer.addAttribute( "name", flow.getName() );
            context.convertAnother( flow );
            writer.endNode();
        }
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Map<String, Long> idMap = getIdMap( context );
        getProxyConnectors( context );
        DataQueryObject dqo = Project.dqo();
        Scenario scenario = dqo.createScenario();
        Part defaultPart = scenario.getDefaultPart();
        context.put( "scenario", scenario );
        scenario.setName( reader.getAttribute( "name" ) );
        String oldId = reader.getAttribute( "id" );
        idMap.put( oldId, scenario.getId() );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "description" ) ) {
                scenario.setDescription( reader.getValue() );
            } else if ( nodeName.equals( "location" ) ) {
                scenario.setLocation( dqo.findOrCreate( Place.class, reader.getValue() ) );
            } else if ( nodeName.equals( "incident" ) ) {
                scenario.setIncident( reader.getValue().equals( "true" ) );
            } else if ( nodeName.equals( "expected-duration" ) ) {
                scenario.setSelfTerminating( true );
                scenario.setCompletionTime( Delay.parse( reader.getValue() ) );
            } else if ( nodeName.equals( "initiator" ) ) {
                resolveInitiator( reader, scenario );
                // Entities
            } else if ( nodeName.equals( "actor" ) ) {
                context.convertAnother( scenario, Actor.class );
            } else if ( nodeName.equals( "organization" ) ) {
                context.convertAnother( scenario, Organization.class );
            } else if ( nodeName.equals( "role" ) ) {
                context.convertAnother( scenario, Role.class );
            } else if ( nodeName.equals( "place" ) ) {
                context.convertAnother( scenario, Place.class );
                // Parts and flows
            } else if ( nodeName.equals( "part" ) ) {
                context.convertAnother( scenario, Part.class );
            } else if ( nodeName.equals( "flow" ) ) {
                context.convertAnother( scenario, Flow.class );
                // Issues
            } else if ( nodeName.equals( "issue" ) ) {
                context.convertAnother( scenario, UserIssue.class );
            } else {
                LOG.warn( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        // Remove automatically created default part
        scenario.removeNode( defaultPart );
        Map<String, Object> state = new HashMap<String, Object>();
        state.put( "scenario", scenario );
        state.put( "idMap", context.get( "idMap" ) );
        state.put( "proxyConnectors", context.get( "proxyConnectors" ) );
        state.put( "portalConnectors", context.get( "portalConnectors" ) );
        return state;
    }

    private void resolveInitiator( HierarchicalStreamReader reader, Scenario scenario ) {
        String externalScenarioName = reader.getAttribute( "scenario" );
        String externalScenarioDescription = "";
        String roleName = null;
        String organizationName = null;
        String task = null;
        String taskDescription = "";
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "scenario-description" ) ) {
                externalScenarioDescription = reader.getValue();
            } else if (!nodeName.equals("part-id")) {
                String name = reader.getAttribute( "name" ).trim();
                if ( nodeName.equals( "part-role" ) ) {
                    roleName = name;
                } else if ( nodeName.equals( "part-task" ) ) {
                    task = name;
                    taskDescription = reader.getValue();
                } else if ( nodeName.equals( "part-organization" ) ) {
                    organizationName = name;
                }
            }
            reader.moveUp();
        }
        List<Scenario> externalScenarios = ConverterUtils.findMatchingScenarios(
                externalScenarioName,
                externalScenarioDescription );
        for ( Scenario externalScenario : externalScenarios ) {
            List<Part> externalParts = ConverterUtils.findMatchingParts(
                    externalScenario,
                    roleName,
                    organizationName,
                    task,
                    taskDescription );
            for ( Part externalPart : externalParts ) {
                scenario.addInitiator( externalPart );
            }
        }

    }

}
