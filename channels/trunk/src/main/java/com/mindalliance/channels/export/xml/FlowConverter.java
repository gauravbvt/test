package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Delay;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.InternalFlow;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.util.SemMatch;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * An XStream Flow converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 3:28:38 PM
 */
public class FlowConverter extends AbstractChannelsConverter {
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
            writeFlowNodes( (InternalFlow) flow, writer, currentScenario );
        } else {
            writeFlowNodes( (ExternalFlow) flow, writer, currentScenario );
        }
        writer.startNode( "description" );
        writer.setValue( flow.getDescription() );
        writer.endNode();
        // channels
        for ( Channel channel : flow.getChannels() ) {
            writer.startNode( "channel" );
            context.convertAnother( channel );
            writer.endNode();
        }
        writer.startNode( "all" );
        writer.setValue( String.valueOf( flow.isAll() ) );
        writer.endNode();
        writer.startNode( "significanceToSource" );
        writer.setValue( String.valueOf( flow.getSignificanceToSource().name() ) );
        writer.endNode();
        writer.startNode( "significanceToTarget" );
        writer.setValue( String.valueOf( flow.getSignificanceToTarget().name() ) );
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
            for ( Issue issue : Project.dqo().findAllUserIssues( flow ) ) {
                writer.startNode( "issue" );
                context.convertAnother( issue );
                writer.endNode();
            }
        }
    }

    private void writeFlowNodes( InternalFlow flow,
                                 HierarchicalStreamWriter writer,
                                 Scenario currentScenario ) {
        writer.startNode( "source" );
        writeNode( flow.getSource(), writer, currentScenario );
        writer.endNode();
        writer.startNode( "target" );
        writeNode( flow.getTarget(), writer, currentScenario );
        writer.endNode();
    }

    private void writeFlowNodes( ExternalFlow flow,
                                 HierarchicalStreamWriter writer,
                                 Scenario currentScenario ) {
        writer.startNode( "source" );
        writeNode( ( flow.isPartTargeted() ? flow.getConnector() : flow.getPart() ), writer, currentScenario );
        writer.endNode();
        writer.startNode( "target" );
        writeNode( flow.isPartTargeted() ? flow.getPart() : flow.getConnector(), writer, currentScenario );
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
        // Connector is in other scenario -- an external flow
        if ( connector.getScenario() != currentScenario ) {
            writer.addAttribute( "scenario", connector.getScenario().getName() );
            writer.startNode( "scenario-description" );
            writer.setValue( connector.getScenario().getDescription() );
            writer.endNode();
            Flow innerFlow = connector.getInnerFlow();
            writer.startNode( "flow" );
            writer.addAttribute( "name", innerFlow.getName() );
            writer.endNode();
            Part part = (Part) ( connector.isSource()
                    ? innerFlow.getTarget()
                    : innerFlow.getSource() );
            ConverterUtils.writePartSpecification( part, writer );
            // Connector is in this scenario
        } else {
            // keep specs of external flows it participates in
            Iterator<ExternalFlow> externalFlows = connector.externalFlows();
            while ( externalFlows.hasNext() ) {
                ExternalFlow externalFlow = externalFlows.next();
                Part externalPart = externalFlow.getPart();
                writer.startNode( "connected-to" );
                writer.addAttribute( "scenario", externalPart.getScenario().getName() );
                writer.addAttribute( "flow", externalFlow.getName() );
                writer.startNode( "scenario-description" );
                writer.setValue( connector.getScenario().getDescription() );
                writer.endNode();
                ConverterUtils.writePartSpecification( externalPart, writer );
                writer.endNode();
            }
        }
        writer.endNode();
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
        List<Flow> flows = makeFlows( sources, targets, flowName, idValue );
        while ( reader.hasMoreChildren() ) {
            // for ( Flow flow : flows ) flow.setName( flowName );
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "description" ) ) {
                String description = reader.getValue();
                for ( Flow flow : flows ) flow.setDescription( description );
            } else if ( nodeName.equals( "channel" ) ) {
                Channel channel = (Channel) context.convertAnother( scenario, Channel.class );
                for ( Flow flow : flows ) flow.addChannel( channel );
            } else if ( nodeName.equals( "maxDelay" ) ) {
                String maxDelay = reader.getValue();
                for ( Flow flow : flows ) flow.setMaxDelay( Delay.parse( maxDelay ) );
            } else if ( nodeName.equals( "askedFor" ) ) {
                boolean askedFor = reader.getValue().equals( "true" );
                for ( Flow flow : flows ) flow.setAskedFor( askedFor );
            } else if ( nodeName.equals( "significanceToSource" ) ) {
                Flow.Significance significance = Flow.Significance.valueOf( reader.getValue() );
                for ( Flow flow : flows ) flow.setSignificanceToSource( significance );
            } else if ( nodeName.equals( "significanceToTarget" ) ) {
                Flow.Significance significance = Flow.Significance.valueOf( reader.getValue() );
                for ( Flow flow : flows ) flow.setSignificanceToTarget( significance );
                // TODO - temporary
            } else if ( nodeName.equals( "critical" ) ) {
                boolean critical = reader.getValue().equals( "true" );
                for ( Flow flow : flows ) if ( critical ) flow.becomeCritical();
            } else if ( nodeName.equals( "all" ) ) {
                boolean all = reader.getValue().equals( "true" );
                for ( Flow flow : flows ) flow.setAll( all );
            } else if ( nodeName.equals( "issue" ) ) {
                context.convertAnother( scenario, UserIssue.class );
            } else {
                throw new ConversionException( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return flows;
    }

    private List<Flow> makeFlows(
            List<Node> sources, List<Node> targets, String name, String idValue ) {

        List<Flow> flows = new ArrayList<Flow>();
        DataQueryObject dqo = Project.dqo();
        for ( Node source : sources ) {
            for ( Node target : targets ) {
                Flow flow = dqo.connect( source, target, name );
                flows.add( flow );
                // Register flow id if internal because it is guaranteed to be the exported flow
                if ( flow.isInternal() ) {
                    // at most one internal flow per exported flow
                    assert idMap.get( idValue ) == null;
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
        List<Connector> connectors = new ArrayList<Connector>();
        String externalScenarioName = reader.getAttribute( "scenario" );
        if ( externalScenarioName == null ) {
            // Connector is in same scenario
            Connector connector = Project.dqo().createConnector( scenario );
            connectors.add( connector );
            // try to connect external part that match specifications
            connectMatchingExternalParts( connector, reader, isSource );
        } else {
            // Find an unambiguously matching external connector, else create internal connector
            String externalScenarioDescription = "";
            String flowName = null;
            String roleName = null;
            String organizationName = null;
            String task = null;
            String taskDescription = "";
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                if ( nodeName.equals( "scenario-description" ) ) {
                    externalScenarioDescription = reader.getValue();
                } else {
                    String name = reader.getAttribute( "name" ).trim();
                    if ( nodeName.equals( "flow" ) ) {
                        flowName = name;
                    } else if ( nodeName.equals( "part-role" ) ) {
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
            List<Connector> matchingConnectors = findMatchingConnectors(
                    externalScenarioName,
                    externalScenarioDescription,
                    flowName,
                    roleName,
                    organizationName,
                    task,
                    taskDescription,
                    isSource );
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
            String externalScenarioName = reader.getAttribute( "scenario" );
            String externalScenarioDescription = "";
            String flowName = reader.getAttribute( "flow" );
            DataQueryObject dqo = Project.dqo();
            String roleName = null;
            String organizationName = null;
            String task = null;
            String taskDescription = "";
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                if ( nodeName.equals( "scenario-description" ) ) {
                    externalScenarioDescription = reader.getValue();
                } else {
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
                List<Part> externalParts = ConverterUtils.findMatchingParts( externalScenario,
                        roleName,
                        organizationName,
                        task,
                        taskDescription );
                for ( Part externalPart : externalParts ) {
                    if ( isSource ) {
                        dqo.connect( externalPart, connector, flowName );
                    } else {
                        dqo.connect( connector, externalPart, flowName );
                    }
                }
            }
            reader.moveUp();
        }

    }


    @SuppressWarnings( "unchecked" )
    private List<Connector> findMatchingConnectors( String scenarioName,
                                                    String scenarioDescription,
                                                    final String flowName,
                                                    final String roleName,
                                                    final String organizationName,
                                                    final String task,
                                                    final String taskDescription,
                                                    final boolean isSource ) {
        List<Connector> connectors = new ArrayList<Connector>();
        List<Scenario> scenarios = ConverterUtils.findMatchingScenarios( scenarioName, scenarioDescription );
        for ( Scenario scenario : scenarios ) {
            Iterator<Connector> iterator =
                    (Iterator<Connector>) new FilterIterator( scenario.nodes(), new Predicate() {
                        public boolean evaluate( Object obj ) {
                            Node node = (Node) obj;
                            return node.isConnector() &&
                                    connectorMatches( (Connector) node,
                                            isSource,
                                            flowName,
                                            roleName,
                                            organizationName,
                                            task,
                                            taskDescription );
                        }
                    }
                    );
            while ( iterator.hasNext() ) connectors.add( iterator.next() );
        }
        return connectors;
    }


    private boolean connectorMatches( Connector connector,
                                      boolean isSource,
                                      String flowName,
                                      String roleName,
                                      String organizationName,
                                      String task,
                                      String taskDescription ) {
        // we are matching the part attached to the connector,
        // so it's input-edness is the reverse of that of the connector
        if ( connector.isSource() == isSource ) return false;
        Flow innerFlow = connector.getInnerFlow();
        Part part = (Part) ( isSource ? innerFlow.getSource() : innerFlow.getTarget() );
        // TODO match task description
        return SemMatch.same( innerFlow.getName(), flowName )
                && ConverterUtils.partMatches( part, roleName, organizationName, task, taskDescription );
    }

}

