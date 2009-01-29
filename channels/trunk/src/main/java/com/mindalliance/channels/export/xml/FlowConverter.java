package com.mindalliance.channels.export.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.InternalFlow;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.util.SemMatch;
import com.mindalliance.channels.Delay;
import com.mindalliance.channels.pages.Project;

import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.collections.Predicate;

/**
 * An XStream Flow converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 3:28:38 PM
 */
public class FlowConverter implements Converter {
    /**
     * The exported-imported id map
     */
    private Map<String, Long> idMap;

    public FlowConverter() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Flow.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal( Object object, HierarchicalStreamWriter writer,
                         MarshallingContext context ) {
        Flow flow = (Flow) object;
        Scenario currentScenario = (Scenario) context.get( "scenario" );
        if ( flow.isInternal() ) {
            writeFlow( (InternalFlow) flow, writer, currentScenario );
        } else {
            writeFlow( (ExternalFlow) flow, writer, currentScenario );
        }
        writer.startNode( "description" );
        writer.setValue( flow.getDescription() );
        writer.endNode();
        if ( flow.getChannel() != null ) {
            writer.startNode( "channel" );
            writer.setValue( flow.getChannel() );
            writer.endNode();
        }
        writer.startNode( "all" );
        writer.setValue( String.valueOf( flow.isAll() ) );
        writer.endNode();
        writer.startNode( "critical" );
        writer.setValue( String.valueOf( flow.isCritical() ) );
        writer.endNode();
        writer.startNode( "askedFor" );
        writer.setValue( String.valueOf( flow.isAskedFor() ) );
        writer.endNode();
        if ( flow.getMaxDelay() != null ) {
            writer.startNode( "maxDelay" );
            writer.setValue( flow.getMaxDelay().toString() );
            writer.endNode();
        }
        // Flow user issues (exported only if an internal flow)
        if ( flow.isInternal() ) {
            List<Issue> issues = Project.dao().findAllUserIssues( flow );
            for ( Issue issue : issues ) {
                writer.startNode( "issue" );
                context.convertAnother( issue );
                writer.endNode();
            }
        }
    }

    private void writeFlow( InternalFlow flow,
                            HierarchicalStreamWriter writer,
                            Scenario currentScenario ) {
        writer.startNode( "source" );
        writeNode( flow.getSource(), writer, currentScenario );
        writer.endNode();
        writer.startNode( "target" );
        writeNode( flow.getTarget(), writer, currentScenario );
        writer.endNode();
    }

    private void writeFlow( ExternalFlow flow,
                            HierarchicalStreamWriter writer,
                            Scenario currentScenario ) {
        writer.startNode( "source" );
        writeNode( !flow.isInput()
                ? flow.getPart()
                : flow.getConnector(), writer, currentScenario );
        writer.endNode();
        writer.startNode( "target" );
        writeNode( flow.isInput() ? flow.getPart() : flow.getConnector(), writer, currentScenario );
        writer.endNode();
    }

    private void writeNode( Node node,
                            HierarchicalStreamWriter writer,
                            Scenario currentScenario ) {
        if ( node.isPart() ) {
            writePart( (Part) node, writer );
        } else {
            writeConnector( (Connector) node, writer, currentScenario );
        }
    }

    private void writePart( Part part, HierarchicalStreamWriter writer ) {
        writer.startNode( "part" );
        writer.addAttribute( "id", String.valueOf( part.getId() ) );
        writer.endNode();
    }

    private void writeConnector( Connector connector,
                                 HierarchicalStreamWriter writer,
                                 Scenario currentScenario ) {
        writer.startNode( "connector" );
        if ( connector.getScenario() != currentScenario ) {
            writer.addAttribute( "scenario", connector.getScenario().getName() );
            Flow innerFlow = connector.getInnerFlow();
            writer.startNode( "flow" );
            writer.addAttribute( "name", innerFlow.getName() );
            writer.endNode();
            Part part = (Part) ( connector.isInput()
                    ? innerFlow.getTarget()
                    : innerFlow.getSource() );
            writePartSpecification( part, writer );
        } else {
            Iterator<ExternalFlow> externalFlows = connector.externalFlows();
            while ( externalFlows.hasNext() ) {
                ExternalFlow externalFlow = externalFlows.next();
                Part externalPart = externalFlow.getPart();
                writer.startNode( "connected-to" );
                writer.addAttribute( "scenario", externalPart.getScenario().getName() );
                writePartSpecification( externalPart, writer );
                writer.endNode();
            }
        }
        writer.endNode();
    }

    private void writePartSpecification( Part part, HierarchicalStreamWriter writer ) {
        if ( part.getRole() != null ) {
            writer.startNode( "part-role" );
            writer.addAttribute( "name", part.getRole().getName() );
            writer.endNode();
        }
        if ( part.getTask() != null ) {
            writer.startNode( "part-task" );
            writer.addAttribute( "name", part.getTask() );
            writer.endNode();
        }
        if ( part.getOrganization() != null ) {
            writer.startNode( "part-organization" );
            writer.addAttribute( "name", part.getOrganization().getName() );
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        idMap = (Map<String, Long>) context.get( "idMap" );
        Scenario scenario = (Scenario) context.get( "scenario" );
        String flowName = reader.getAttribute( "name" );
        String idValue = reader.getAttribute( "id" );
        reader.moveDown();
        assert reader.getNodeName().equals( "source" );
        List<Node> sources = resolveNodes( reader, scenario, idMap, true );
        reader.moveUp();
        reader.moveDown();
        assert reader.getNodeName().equals( "target" );
        // If a node is a "connector specification", multiple actual connectors might match
        List<Node> targets = resolveNodes( reader, scenario, idMap, false );
        reader.moveUp();
        List<Flow> flows = makeFlows( scenario, sources, targets, idValue );
        while ( reader.hasMoreChildren() ) {
            for ( Flow flow : flows ) flow.setName( flowName );
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "description" ) ) {
                String description = reader.getValue();
                for ( Flow flow : flows ) flow.setDescription( description );
            } else if ( nodeName.equals( "channel" ) ) {
                String channel = reader.getValue();
                for ( Flow flow : flows ) flow.setChannel( channel );
            } else if ( nodeName.equals( "maxDelay" ) ) {
                String maxDelay = reader.getValue();
                for ( Flow flow : flows ) flow.setMaxDelay( Delay.parse(maxDelay) );
            } else if ( nodeName.equals( "askedFor" ) ) {
                boolean askedFor = reader.getValue().equals( "true" );
                for ( Flow flow : flows ) flow.setAskedFor( askedFor );
            } else if ( nodeName.equals( "critical" ) ) {
                boolean critical = reader.getValue().equals( "true" );
                for ( Flow flow : flows ) flow.setCritical( critical );
            } else if ( nodeName.equals( "all" ) ) {
                boolean all = reader.getValue().equals( "true" );
                for ( Flow flow : flows ) flow.setCritical( all );
            } else if ( nodeName.equals( "issue" ) ) {
                context.convertAnother( scenario, UserIssue.class );
            } else {
                throw new ConversionException( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return flows;
    }

    private List<Flow> makeFlows( Scenario scenario, List<Node> sources, List<Node> targets, String idValue ) {
        List<Flow> flows = new ArrayList<Flow>();
        for ( Node source : sources ) {
            for ( Node target : targets ) {
                Flow flow = scenario.connect( source, target );
                flows.add( flow );
                // Register flow id if internal because it is guaranteed to be the exported flow
                if ( flow.isInternal() ) {
                    // at most one internal flow per exported flow
                    assert idMap.get(  idValue ) == null;
                    idMap.put( idValue, flow.getId() );
                }
            }
        }
        return flows;
    }

    private List<Node> resolveNodes( HierarchicalStreamReader reader,
                                     Scenario scenario,
                                     Map<String, Long> idMap,
                                     boolean isSource ) {
        reader.moveDown();
        List<Node> nodes = new ArrayList<Node>();
        String nodeName = reader.getNodeName();
        if ( reader.getNodeName().equals( "part" ) ) {
            nodes.add( resolvePart( reader, scenario, idMap ) );
        } else {
            assert nodeName.equals( "connector" );
            nodes.addAll( resolveConnectors( reader, scenario, isSource ) );
        }
        reader.moveUp();
        return nodes;
    }

    private Part resolvePart( HierarchicalStreamReader reader,
                              Scenario scenario,
                              Map<String, Long> idMap ) {
        String id = reader.getAttribute( "id" );
        return (Part) scenario.getNode( idMap.get( id ) );
    }

    private List<Connector> resolveConnectors( HierarchicalStreamReader reader,
                                               Scenario scenario,
                                               boolean isSource ) {
        // TODO - shoudn't this be about the "type" of a scenario?
        List<Connector> connectors = new ArrayList<Connector>();
        String externalScenarioName = reader.getAttribute( "scenario" );
        if ( externalScenarioName == null ) {
            Connector connector = scenario.createConnector();
            connectors.add( connector );
            // try to connect external part that match specifications
            connectMatchingExternalParts( connector, reader, isSource );
        } else {
            // Find an unambiguously matching external connector, else create internal connector
            String flowName = null;
            String roleName = null;
            String organizationName = null;
            String task = null;
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                String name = reader.getAttribute( "name" ).trim();
                if ( nodeName.equals( "flow" ) ) {
                    flowName = name;
                } else if ( nodeName.equals( "part-role" ) ) {
                    roleName = name;
                } else if ( nodeName.equals( "part-task" ) ) {
                    task = name;
                } else if ( nodeName.equals( "part-organization" ) ) {
                    organizationName = name;
                }
                reader.moveUp();
            }
            List<Connector> matchingConnectors = findMatchingConnectors( externalScenarioName,
                    flowName, roleName, organizationName, task, isSource );
            connectors.addAll( matchingConnectors );
        }
        return connectors;
    }

    private void connectMatchingExternalParts( Connector connector,
                                               HierarchicalStreamReader reader,
                                               boolean isSource ) {
        // for each external flow part
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            assert reader.getNodeName().equals( "connected-to" );
            String scenarioName = reader.getAttribute( "scenario" );
            try {
                Scenario externalScenario = Project.dao().
                        findScenario( scenarioName );
                String roleName = null;
                String organizationName = null;
                String task = null;
                while ( reader.hasMoreChildren() ) {
                    reader.moveDown();
                    String nodeName = reader.getNodeName();
                    String name = reader.getAttribute( "name" ).trim();
                    if ( nodeName.equals( "part-role" ) ) {
                        roleName = name;
                    } else if ( nodeName.equals( "part-task" ) ) {
                        task = name;
                    } else if ( nodeName.equals( "part-organization" ) ) {
                        organizationName = name;
                    }
                    reader.moveUp();
                }
                List<Part> externalParts = findMatchingExternalParts( externalScenario,
                        roleName,
                        organizationName,
                        task );
                for ( Part externalPart : externalParts ) {
                    if ( isSource ) {
                        externalScenario.connect( externalPart, connector );
                    } else {
                        externalScenario.connect( connector, externalPart );
                    }
                }
            }
            catch ( NotFoundException e ) {
                // TODO - replace by logging
                System.out.println( "scenario " + scenarioName + " not found" );
            }
            reader.moveUp();
        }

    }

    @SuppressWarnings( "unchecked" )
    private List<Part> findMatchingExternalParts( Scenario scenario,
                                                  final String roleName,
                                                  final String organizationName,
                                                  final String task ) {
        List<Part> externalParts = new ArrayList<Part>();
        Iterator<Part> iterator =
                (Iterator<Part>) new FilterIterator( scenario.parts(), new Predicate() {
                    public boolean evaluate( Object obj ) {
                        Part part = (Part) obj;
                        return matches( part, roleName, organizationName, task );
                    }
                }
                );
        while ( iterator.hasNext() ) externalParts.add( iterator.next() );
        return externalParts;
    }

    @SuppressWarnings( "unchecked" )
    private List<Connector> findMatchingConnectors( String scenarioName,
                                                    final String flowName,
                                                    final String roleName,
                                                    final String organizationName,
                                                    final String task,
                                                    final boolean isSource ) {
        List<Connector> connectors = new ArrayList<Connector>();
        try {
            Scenario scenario = Project.dao().findScenario( scenarioName );
            Iterator<Connector> iterator =
                    (Iterator<Connector>) new FilterIterator( scenario.nodes(), new Predicate() {
                        public boolean evaluate( Object obj ) {
                            Node node = (Node) obj;
                            return node.isConnector() &&
                                    matches( (Connector) node,
                                            isSource,
                                            flowName,
                                            roleName,
                                            organizationName,
                                            task );
                        }
                    }
                    );
            while ( iterator.hasNext() ) connectors.add( iterator.next() );
        } catch ( NotFoundException e ) {
            return connectors;
        }
        return connectors;
    }

    private boolean matches( Part part,
                             String roleName,
                             String organizationName,
                             String task ) {
        if ( roleName != null ) {
            if ( part.getRole() == null
                    || !SemMatch.same( part.getRole().getName(), roleName ) ) return false;
        }
        if ( organizationName != null ) {
            if ( part.getOrganization() == null
                    || !SemMatch.same( part.getOrganization().getName(), organizationName ) )
                return false;
        }
        if ( task != null ) {
            if ( part.getTask() == null || !SemMatch.same( part.getTask(), task ) )
                return false;
        }
        return true;
    }

    private boolean matches( Connector connector,
                             boolean isSource,
                             String flowName,
                             String roleName,
                             String organizationName,
                             String task ) {
        // we are matching the part attached to the connector,
        // so it's input-edness is the reverse of that of the connector
        if ( connector.isInput() == isSource ) return false;
        Flow innerFlow = connector.getInnerFlow();
        Part part = (Part) ( isSource ? innerFlow.getSource() : innerFlow.getTarget() );
        return SemMatch.same( innerFlow.getName(), flowName )
                && matches( part, roleName, organizationName, task );
    }

}

