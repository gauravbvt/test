package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.dao.PlanDao;
import com.mindalliance.channels.core.export.ConnectionSpecification;
import com.mindalliance.channels.core.export.PartSpecification;
import com.mindalliance.channels.core.export.SegmentSpecification;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.Delay;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.InternalFlow;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.UserIssue;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( FlowConverter.class );
    /**
     * The exported-imported id map
     */
    private Map<Long, Long> idMap;
    /**
     * Local connectors that stand for external connectors.
     */
    private Map<Connector, List<ConnectionSpecification>> proxyConnectors;

    public FlowConverter( XmlStreamer.Context context ) {
        super( context );
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
        Segment currentSegment = (Segment) context.get( "segment" );
        if ( flow.isInternal() ) {
            writeFlowNodes( (InternalFlow) flow, writer, currentSegment );
        } else {
            writeFlowNodes( (ExternalFlow) flow, writer, currentSegment );
        }
        exportDetectionWaivers( flow, writer );
        exportAttachments( flow, writer );
        writer.startNode( "description" );
        writer.setValue( flow.getDescription() );
        writer.endNode();
        writeTags( writer, flow );
        // eois
        for ( ElementOfInformation eoi : flow.getLocalEois() ) {
            writer.startNode( "eoi" );
            context.convertAnother( eoi );
            writer.endNode();
        }
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
        writer.startNode( "classificationsLinked" );
        writer.setValue( String.valueOf( flow.isClassificationsLinked() ) );
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
        if ( flow.getIntent() != null ) {
            writer.startNode( "intent" );
            writer.setValue( flow.getIntent().name() );
            writer.endNode();
        }
        for ( Flow.Restriction restriction : flow.getRestrictions() ) {
            writer.startNode( "restriction" );
            writer.setValue( restriction.name() );
            writer.endNode();
        }
        if ( flow.isIfTaskFails() ) {
            writer.startNode( "ifTaskFails" );
            writer.setValue( Boolean.toString( flow.isIfTaskFails() ) );
            writer.endNode();
        }
        // Prohibited
        writer.startNode( "prohibited" );
        writer.setValue( Boolean.toString( flow.isProhibited() ) );
        writer.endNode();
        // References event phase
        writer.startNode( "referencesEventPhase" );
        writer.setValue( Boolean.toString( flow.isReferencesEventPhase() ) );
        writer.endNode();
        // Can bypass intermediate
        writer.startNode( "canBypassIntermediate" );
        writer.setValue( Boolean.toString( flow.isCanBypassIntermediate() ) );
        writer.endNode();
        // Receipt confirmation requested
        writer.startNode( "receiptConfirmationRequested" );
        writer.setValue( Boolean.toString( flow.isReceiptConfirmationRequested() ) );
        writer.endNode();
        // Flow information standardized as info product
        writer.startNode( "standardized" );
        writer.setValue( Boolean.toString( flow.isStandardized() ) );
        writer.endNode();
        // Flow is published
        if ( flow.isNeed() || flow.isCapability() ) {
            writer.startNode( "published" );
            writer.setValue( Boolean.toString( flow.isPublished() ) );
            writer.endNode();
        }
    }

    private void writeFlowNodes( InternalFlow flow,
                                 HierarchicalStreamWriter writer,
                                 Segment currentSegment ) {
        writer.startNode( "source" );
        writeNode( flow.getSource(), writer, currentSegment, flow );
        writer.endNode();
        writer.startNode( "target" );
        writeNode( flow.getTarget(), writer, currentSegment, flow );
        writer.endNode();
    }

    private void writeFlowNodes( ExternalFlow flow,
                                 HierarchicalStreamWriter writer,
                                 Segment currentSegment ) {
        writer.startNode( "source" );
        writeNode( ( flow.isPartTargeted() ? flow.getConnector() : flow.getPart() ),
                writer,
                currentSegment,
                flow );
        writer.endNode();
        writer.startNode( "target" );
        writeNode( flow.isPartTargeted() ? flow.getPart() : flow.getConnector(),
                writer,
                currentSegment,
                flow );
        writer.endNode();
    }

    private void writeNode( Node node,
                            HierarchicalStreamWriter writer,
                            Segment currentSegment,
                            Flow flow ) {
        if ( node.isPart() ) {
            writePart( (Part) node, writer );
        } else {
            writeConnector( (Connector) node, writer, currentSegment, flow );
        }
    }

    private void writePart( Part part, HierarchicalStreamWriter writer ) {
        writer.startNode( "part" );
        writer.addAttribute( "id", String.valueOf( part.getId() ) );
        writer.endNode();
    }

    private void writeConnector( Connector connector,
                                 HierarchicalStreamWriter writer,
                                 Segment currentSegment,
                                 Flow flow ) {
        writer.startNode( "connector" );
        writer.addAttribute( "id", "" + connector.getId() );
        // Connector is in other segment -- an external flow
        if ( connector.getSegment() != currentSegment ) {
            writer.addAttribute( "segment", connector.getSegment().getName() );
            writer.startNode( "segment-description" );
            writer.setValue( connector.getSegment().getDescription() );
            writer.endNode();
            Flow innerFlow = connector.getInnerFlow();
            writer.startNode( "flow" );
            writer.addAttribute( "name", innerFlow.getName() );
            if ( flow.isCanBypassIntermediate() )
                writer.addAttribute( "canBypassIntermediate", Boolean.toString( flow.isCanBypassIntermediate() ) );
            if ( flow.isReceiptConfirmationRequested() )
                writer.addAttribute( "receiptConfirmationRequested", Boolean.toString( flow.isReceiptConfirmationRequested() ) );
            for ( Flow.Restriction restriction : innerFlow.getRestrictions() ) {
                writer.startNode( "restriction" );
                writer.setValue( restriction.name() );
                writer.endNode();
            }
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
    @SuppressWarnings("unchecked")
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        idMap = getIdMap( context );
        proxyConnectors = getProxyConnectors( context );
        boolean importingPlan = this.isImportingPlan( context );
        Segment segment = (Segment) context.get( "segment" );
        String flowName = reader.getAttribute( "name" );
        Long flowId = Long.parseLong( reader.getAttribute( "id" ) );
        reader.moveDown();
        assert reader.getNodeName().equals( "source" );
        Node source = resolveNode( reader, segment, true, flowId, importingPlan );
        reader.moveUp();
        reader.moveDown();
        assert reader.getNodeName().equals( "target" );
        Node target = resolveNode( reader, segment, false, flowId, importingPlan );
        reader.moveUp();
        boolean preserveFlowId = importingPlan && !( isProxy( target ) || isProxy( source ) );
        Flow flow = makeFlow( source, target, flowName, flowId, preserveFlowId );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "description" ) ) {
                String description = reader.getValue();
                flow.setDescription( description );
            } else if ( nodeName.equals( "tags" ) ) {
                flow.addTags( reader.getValue() );
            } else if ( nodeName.equals( "detection-waivers" ) ) {
                importDetectionWaivers( flow, reader );
            } else if ( nodeName.equals( "attachments" ) ) {
                importAttachments( flow, reader );
            } else if ( nodeName.equals( "eoi" ) ) {
                ElementOfInformation eoi = (ElementOfInformation) context.convertAnother(
                        segment,
                        ElementOfInformation.class );
                flow.addLocalEoi( eoi );
            } else if ( nodeName.equals( "channel" ) ) {
                Channel channel = (Channel) context.convertAnother( segment, Channel.class );
                flow.addChannel( channel );
            } else if ( nodeName.equals( "maxDelay" ) ) {
                String maxDelay = reader.getValue();
                flow.setMaxDelay( Delay.parse( maxDelay ) );
            } else if ( nodeName.equals( "askedFor" ) ) {
                boolean askedFor = Boolean.valueOf( reader.getValue() );
                flow.setAskedFor( askedFor );
            } else if ( nodeName.equals( "significanceToSource" ) ) {
                Flow.Significance significance = Flow.Significance.valueOf( reader.getValue() );
                flow.setSignificanceToSource( significance );
            } else if ( nodeName.equals( "significanceToTarget" ) ) {
                Flow.Significance significance = Flow.Significance.valueOf( reader.getValue() );
                flow.setSignificanceToTarget( significance );
            } else if ( nodeName.equals( "all" ) ) {
                boolean all = Boolean.valueOf( reader.getValue() );
                flow.setAll( all );
            } else if ( nodeName.equals( "classificationsLinked" ) ) {
                boolean classificationsLinked = reader.getValue().equals( "true" );
                flow.setClassificationsLinked( classificationsLinked );
            } else if ( nodeName.equals( "issue" ) ) {
                context.convertAnother( segment, UserIssue.class );
            } else if ( nodeName.equals( "intent" ) ) {
                flow.setIntent( Flow.Intent.valueOf( reader.getValue() ) );
            } else if ( nodeName.equals( "restriction" ) ) {
                String restrictionName = reader.getValue();
                if ( restrictionName.equals( "SameOrganizationAndLocation" ) ) { // OBSOLETE
                    flow.addRestriction( Flow.Restriction.SameOrganization );
                    flow.addRestriction( Flow.Restriction.SameLocation );
                }
                flow.addRestriction( Flow.Restriction.valueOf( restrictionName ) );
            } else if ( nodeName.equals( "ifTaskFails" ) ) {
                flow.setIfTaskFails( Boolean.valueOf( reader.getValue() ) );
            } else if ( nodeName.equals( "prohibited" ) ) {
                flow.setProhibited( Boolean.valueOf( reader.getValue() ) );
            } else if ( nodeName.equals( "referencesEventPhase" ) ) {
                flow.setReferencesEventPhase( Boolean.valueOf( reader.getValue() ) );
            } else if ( nodeName.equals( "canBypassIntermediate" ) ) {
                flow.setCanBypassIntermediate( Boolean.valueOf( reader.getValue() ) );
            } else if ( nodeName.equals( "receiptConfirmationRequested" ) ) {
                flow.setReceiptConfirmationRequested( Boolean.valueOf( reader.getValue() ) );
            } else if ( nodeName.equals( "standardized" ) ) {
                flow.setStandardized( Boolean.valueOf( reader.getValue() ) );
                if ( flow.isStandardized() ) {
                    flow.setProductInfoFromName( getPlanDao() );
                }
            } else if ( nodeName.equals( "published" ) ) {
                flow.setPublished( Boolean.valueOf( reader.getValue() ) );
            } else {
                LOG.debug( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return flow;
    }

    private boolean isProxy( Node node ) {
        return node.isConnector() && proxyConnectors.containsKey( (Connector) node );
    }

    private Flow makeFlow(
            Node source, Node target, String name, Long flowId, boolean preserveId ) {

        PlanDao planDao = getPlanDao();
        Flow flow = planDao.connect( source, target, name, preserveId ? flowId : null );
        idMap.put( flowId, flow.getId() );
        return flow;
    }

    private Node resolveNode( HierarchicalStreamReader reader,
                              Segment segment,
                              boolean isSource,
                              Long flowId,
                              boolean importingPlan ) {
        Node node;
        reader.moveDown();
        String nodeName = reader.getNodeName();
        if ( reader.getNodeName().equals( "part" ) ) {
            node = resolvePart( reader, segment );
        } else {
            assert nodeName.equals( "connector" );
            node = resolveConnector( reader, segment, isSource, flowId, importingPlan );
        }
        reader.moveUp();
        return node;
    }

    private Part resolvePart( HierarchicalStreamReader reader,
                              Segment segment ) {
        Long id = Long.parseLong( reader.getAttribute( "id" ) );
         // When importing a segment (vs reloading a plan), ids are re-assigned
        return (Part) segment.getNode( idMap.get( id ) );
    }

    private Connector resolveConnector( HierarchicalStreamReader reader,
                                        Segment segment,
                                        boolean isSource,
                                        Long flowId,
                                        boolean importingPlan ) {
        Connector connector;
        String externalSegmentName = reader.getAttribute( "segment" );
        if ( importingPlan && externalSegmentName == null ) {
            // reuse prior id
            Long id = Long.parseLong( reader.getAttribute( "id" ) );
            connector = getPlanDao().createConnector( segment, id );
        } else {
            // use new id
            connector = getPlanDao().createConnector( segment, null );
        }
        if ( externalSegmentName != null ) {
            // Connector is in other segment
            registerAsProxy( connector, reader, isSource, externalSegmentName, flowId );
        }
        return connector;
    }

    private void registerAsProxy( Connector connector,
                                  HierarchicalStreamReader reader,
                                  boolean isSource,
                                  String externalSegmentName,
                                  Long flowId ) {
        String externalSegmentDescription = "";
        String flowName = null;
        String roleName = null;
        String organizationName = null;
        String task = null;
        String taskDescription = "";
        String partId = null;
        List<String> restrictions = new ArrayList<String>();
        boolean receiptConfirmationRequested = false;
        boolean canBypassIntermediate = false;
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "segment-description" ) ) {
                externalSegmentDescription = reader.getValue();
            } else if ( nodeName.equals( "part-id" ) ) {
                partId = reader.getValue();
            } else {
                String name = reader.getAttribute( "name" ).trim();
                if ( nodeName.equals( "flow" ) ) {
                    flowName = name;
                    canBypassIntermediate = Boolean.valueOf( reader.getAttribute( "canBypassIntermediate" ) );
                    receiptConfirmationRequested = Boolean.valueOf( reader.getAttribute( "receiptConfirmationRequested" ) );
                    if ( reader.getAttribute( "restriction" ) != null ) // TODO - OBSOLETE
                        restrictions.add( reader.getAttribute( "restriction" ) );
                    while ( reader.hasMoreChildren() ) {
                        reader.moveDown();
                        String subNodeName = reader.getNodeName();
                        if ( subNodeName.equals( "restriction" ) ) {
                            restrictions.add( reader.getValue() );
                        }
                        reader.moveUp();
                    }
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
        conSpec.setSegmentSpecification( new SegmentSpecification(
                externalSegmentName,
                externalSegmentDescription
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
        for ( String restriction : restrictions ) {
            conSpec.addRestriction( restriction );
        }
        conSpec.setReceiptConfirmationRequested( receiptConfirmationRequested );
        conSpec.setCanBypassIntermediate( canBypassIntermediate );
        addConnectionSpec( connector, conSpec );
    }

    private void addConnectionSpec( Connector connector, ConnectionSpecification conSpec ) {
        List<ConnectionSpecification> conSpecs = proxyConnectors.get( connector );
        if ( conSpecs == null ) {
            conSpecs = new ArrayList<ConnectionSpecification>();
            proxyConnectors.put( connector, conSpecs );
        }
        conSpecs.add( conSpec );
    }

}

