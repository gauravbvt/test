package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.export.ConnectionSpecification;
import com.mindalliance.channels.export.PartSpecification;
import com.mindalliance.channels.export.ScenarioSpecification;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Delay;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.InternalFlow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.UserIssue;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.ArrayList;
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
    private Map<Long, Long> idMap;
    /**
     * Local connectors that stand for external connectors.
     */
    private Map<Connector, List<ConnectionSpecification>> proxyConnectors;

    public FlowConverter( Exporter exporter ) {
        super( exporter );
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
        exportDetectionWaivers( flow, writer );
        exportAttachmentTickets( flow, writer, isExportingPlan( context ) );
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
            exportUserIssues( flow, writer, context );
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
        writer.addAttribute( "id", "" + connector.getId() );
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
        }
        writer.endNode();
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        idMap = getIdMap( context );
        proxyConnectors = getProxyConnectors( context );
        boolean importingPlan = this.isImportingPlan( context );
        Scenario scenario = (Scenario) context.get( "scenario" );
        String flowName = reader.getAttribute( "name" );
        Long flowId = Long.parseLong( reader.getAttribute( "id" ) );
        reader.moveDown();
        assert reader.getNodeName().equals( "source" );
        Node source = resolveNode( reader, scenario, true, flowId, importingPlan );
        reader.moveUp();
        reader.moveDown();
        assert reader.getNodeName().equals( "target" );
        Node target = resolveNode( reader, scenario, false, flowId, importingPlan );
        reader.moveUp();
        boolean preserveFlowId = importingPlan && !( isProxy( target ) || isProxy( source ) );
        Flow flow = makeFlow( source, target, flowName, flowId, preserveFlowId );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "description" ) ) {
                String description = reader.getValue();
                flow.setDescription( description );
            } else if ( nodeName.equals( "detection-waivers" ) ) {
                importDetectionWaivers( flow, reader );
            } else if ( nodeName.equals( "attachments" ) ) {
                importAttachmentTickets( flow, reader );
            } else if ( nodeName.equals( "channel" ) ) {
                Channel channel = (Channel) context.convertAnother( scenario, Channel.class );
                flow.addChannel( channel );
            } else if ( nodeName.equals( "maxDelay" ) ) {
                String maxDelay = reader.getValue();
                flow.setMaxDelay( Delay.parse( maxDelay ) );
            } else if ( nodeName.equals( "askedFor" ) ) {
                boolean askedFor = reader.getValue().equals( "true" );
                flow.setAskedFor( askedFor );
            } else if ( nodeName.equals( "significanceToSource" ) ) {
                Flow.Significance significance = Flow.Significance.valueOf( reader.getValue() );
                flow.setSignificanceToSource( significance );
            } else if ( nodeName.equals( "significanceToTarget" ) ) {
                Flow.Significance significance = Flow.Significance.valueOf( reader.getValue() );
                flow.setSignificanceToTarget( significance );
            } else if ( nodeName.equals( "all" ) ) {
                boolean all = reader.getValue().equals( "true" );
                flow.setAll( all );
            } else if ( nodeName.equals( "issue" ) ) {
                context.convertAnother( scenario, UserIssue.class );
            } else {
                throw new ConversionException( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return flow;
    }

    private boolean isProxy( Node node ) {
        return node.isConnector() && proxyConnectors.containsKey( (Connector) node );
    }

    private Flow makeFlow(
            Node source,
            Node target,
            String name,
            Long flowId,
            boolean preserveId ) {

        QueryService queryService = getQueryService();
        Flow flow = queryService.connect(
                source,
                target,
                name,
                preserveId ? flowId : null );
        idMap.put( flowId, flow.getId() );
        return flow;
    }


    private Node resolveNode( HierarchicalStreamReader reader,
                              Scenario scenario,
                              boolean isSource,
                              Long flowId,
                              boolean importingPlan ) {
        Node node;
        reader.moveDown();
        String nodeName = reader.getNodeName();
        if ( reader.getNodeName().equals( "part" ) ) {
            node = resolvePart( reader, scenario );
        } else {
            assert nodeName.equals( "connector" );
            node = resolveConnector( reader, scenario, isSource, flowId, importingPlan );
        }
        reader.moveUp();
        return node;
    }

    private Part resolvePart( HierarchicalStreamReader reader,
                              Scenario scenario ) {
        Long id = Long.parseLong( reader.getAttribute( "id" ) );
        // When importing a scenario (vs reloading a plan), ids are re-assigned
        return (Part) scenario.getNode( idMap.get( id ) );
    }

    private Connector resolveConnector( HierarchicalStreamReader reader,
                                        Scenario scenario,
                                        boolean isSource,
                                        Long flowId,
                                        boolean importingPlan ) {
        Connector connector;
        String externalScenarioName = reader.getAttribute( "scenario" );
        if ( importingPlan && externalScenarioName == null ) {
            // reuse prior id
            Long id = Long.parseLong( reader.getAttribute( "id" ) );
            connector = getQueryService().createConnector( scenario, id );
        } else {
            // use new id
            connector = getQueryService().createConnector( scenario );
        }
        if ( externalScenarioName != null ) {
            // Connector is in other scenario
            registerAsProxy( connector, reader, isSource, externalScenarioName, flowId );
        }
        return connector;
    }

    private void registerAsProxy( Connector connector,
                                  HierarchicalStreamReader reader,
                                  boolean isSource,
                                  String externalScenarioName,
                                  Long flowId ) {
        String externalScenarioDescription = "";
        String flowName = null;
        String roleName = null;
        String organizationName = null;
        String task = null;
        String taskDescription = "";
        String partId = null;
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "scenario-description" ) ) {
                externalScenarioDescription = reader.getValue();
            } else if ( nodeName.equals( "part-id" ) ) {
                partId = reader.getValue();
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
        ConnectionSpecification conSpec = new ConnectionSpecification();
        conSpec.setScenarioSpecification( new ScenarioSpecification(
                externalScenarioName,
                externalScenarioDescription
        ) );
        conSpec.setFlowName( flowName );
        conSpec.setSource( isSource );
        conSpec.setPartSpecification( new PartSpecification(
                partId,
                task,
                taskDescription,
                roleName,
                organizationName ) );
        conSpec.setExternalFlowId( flowId );
        addConnectionSpec( connector, conSpec );
    }

    private void addConnectionSpec( Connector connector, ConnectionSpecification conSpec ) {
        List<ConnectionSpecification> conSpecs = proxyConnectors.get( connector );
        if ( conSpecs == null ) {
            conSpecs = new ArrayList<ConnectionSpecification>();
            proxyConnectors.put(  connector, conSpecs );
        }
        conSpecs.add( conSpec );
    }

}

