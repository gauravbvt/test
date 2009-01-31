package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.pages.Project;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * XStream scenario converter.
 * Includes project's permanent resource specs
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 1:47:49 PM
 */
public class ScenarioConverter implements Converter {

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
        Service service = project.getService();
        context.put( "scenario", scenario );
        writer.addAttribute( "project", project.getUri() );
        writer.addAttribute( "version", project.getExporter().getVersion() );
        writer.addAttribute( "date", new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() ) );
        writer.addAttribute( "id", String.valueOf( scenario.getId() ) );
        writer.addAttribute( "name", scenario.getName() );
        writer.startNode( "description" );
        writer.setValue( scenario.getDescription() );
        writer.endNode();
        // Permanent resource specifications
        Iterator<ResourceSpec> resourceSpecs = service.iterate( ResourceSpec.class );
        while ( resourceSpecs.hasNext() ) {
            writer.startNode( "resource" );
            context.convertAnother( resourceSpecs.next() );
            writer.endNode();
        }
        // Scenario user issues
        List<Issue> issues = service.findAllUserIssues( scenario );
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
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Map<String, Long> idMap = new HashMap<String, Long>();
        context.put( "idMap", idMap );
        Scenario scenario = Project.service().createScenario();
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
            } else if ( nodeName.equals( "resource" ) ) {
                context.convertAnother( scenario, ResourceSpec.class );
            } else if ( nodeName.equals( "issue" ) ) {
                context.convertAnother( scenario, UserIssue.class );
            } else if ( nodeName.equals( "part" ) ) {
                context.convertAnother( scenario, Part.class );
            } else if ( nodeName.equals( "flow" ) ) {
                context.convertAnother( scenario, Flow.class );
            } else {
                throw new ConversionException( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        // Remove automatically created default part
        scenario.removeNode( defaultPart );
        return scenario;
    }

}
