package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.Tag;
import com.mindalliance.channels.core.model.UserIssue;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Information sharing requirements converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 9/27/11
 * Time: 9:46 AM
 */
public class RequirementConverter extends AbstractChannelsConverter {

    /**
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( RequirementConverter.class );

    public RequirementConverter( XmlStreamer.Context context ) {
        super( context );
    }

    @Override
    public boolean canConvert( Class type ) {
        return Requirement.class.isAssignableFrom( type );
    }

    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
        Requirement requirement = (Requirement) source;
        writer.addAttribute( "id", String.valueOf( requirement.getId() ) );
        writer.startNode( "name" );
        writer.setValue( requirement.getName() );
        writer.endNode();
        writer.startNode( "description" );
        writer.setValue( requirement.getDescription() );
        writer.endNode();
        writeTags( writer, requirement );
        // information
        if ( !requirement.getInformation().isEmpty() ) {
            writer.startNode( "information" );
            writer.setValue( requirement.getInformation() );
            writer.endNode();
        }
        // required tags
        if ( !requirement.getInfoTags().isEmpty() ) {
            writer.startNode( "infoTags" );
            writer.setValue( Tag.tagsToString( requirement.getInfoTags() ) );
            writer.endNode();
        }
        // eois
        for ( String eoi : requirement.getEois() ) {
            writer.startNode( "eoi" );
            writer.setValue( eoi );
            writer.endNode();
        }
        // Cardinality
        Requirement.Cardinality cardinality = requirement.getCardinality();
        writer.startNode( "minCount" );
        writer.setValue( Integer.toString( cardinality.getMinCount() ) );
        writer.endNode();
        writer.startNode( "safeCount" );
        writer.setValue( Integer.toString( cardinality.getSafeCount() ) );
        writer.endNode();
        Integer maxCount = cardinality.getMaxCount();
        if ( maxCount != null ) {
            writer.startNode( "maxCount" );
            writer.setValue( Integer.toString( cardinality.getMaxCount() ) );
            writer.endNode();
        }
        // Committer spec
        writer.startNode( "committerSpec" );
        writeAssignmentSpec( requirement.getCommitterSpec(), writer, context );
        writer.endNode();
        // Beneficiary spec
        writer.startNode( "beneficiarySpec" );
        writeAssignmentSpec( requirement.getBeneficiarySpec(), writer, context );
        writer.endNode();
        // Other model object properties
        writeTags( writer, requirement );
        exportDetectionWaivers( requirement, writer );
        exportAttachments( requirement, writer );
        exportDetectionWaivers( requirement, writer );
        exportUserIssues( requirement, writer, context );
    }

    private void writeAssignmentSpec(
            Requirement.AssignmentSpec assignmentSpec,
            HierarchicalStreamWriter writer,
            MarshallingContext context ) {
        // task name
        if ( !assignmentSpec.getTaskName().isEmpty() ) {
            writer.startNode( "taskName" );
            writer.setValue( assignmentSpec.getTaskName() );
            writer.endNode();
        }
        // required tags
        if ( !assignmentSpec.getTaskTags().isEmpty() ) {
            writer.startNode( "taskTags" );
            writer.setValue( Tag.tagsToString( assignmentSpec.getTaskTags() ) );
            writer.endNode();
        }
        // Cardinality
        Requirement.Cardinality cardinality = assignmentSpec.getCardinality();
        writer.startNode( "minCount" );
        writer.setValue( Integer.toString( cardinality.getMinCount() ) );
        writer.endNode();
        writer.startNode( "safeCount" );
        writer.setValue( Integer.toString( cardinality.getSafeCount() ) );
        writer.endNode();
        Integer maxCount = cardinality.getMaxCount();
        if ( maxCount != null ) {
            writer.startNode( "maxCount" );
            writer.setValue( Integer.toString( cardinality.getMaxCount() ) );
            writer.endNode();
        }
        // event
        Event event = assignmentSpec.getEvent();
        if ( event != null ) {
            writer.startNode( "event" );
            writer.addAttribute( "id", Long.toString( event.getId() ) );
            writer.addAttribute( "kind", "Type" );
            writer.setValue( event.getName() );
            writer.endNode();
        }
        // timing
        Phase.Timing timing = assignmentSpec.getTiming();
        if ( timing != null ) {
            writer.startNode( "timing" );
            writer.setValue( timing.name() );
            writer.endNode();
        }
        // agent spec
        Requirement.AgentSpec spec = assignmentSpec.getAgentSpec();
        if ( spec.getActor() != null ) {
            writer.startNode( "actor" );
            writer.addAttribute( "id", Long.toString( spec.getActor().getId() ) );
            writer.setValue( spec.getActor().getName() );
            writer.endNode();
        }
        if ( spec.getJurisdiction() != null ) {
            writer.startNode( "jurisdiction" );
            writer.addAttribute( "id", Long.toString( spec.getJurisdiction().getId() ) );
            writer.addAttribute( "kind", spec.getJurisdiction().isType() ? "Type" : "Actual" );
            writer.setValue( spec.getJurisdiction().getName() );
            writer.endNode();
        }
        if ( spec.getFixedOrgId() != null ) {
            writer.startNode( "fixedOrg" );
            writer.addAttribute( "id", Long.toString( spec.getFixedOrgId() ) );
            writer.endNode();
        }
        if ( spec.getPlaceholder() != null ) {
            writer.startNode( "placeholder" );
            writer.addAttribute( "id", Long.toString( spec.getPlaceholder().getId() ) );
            writer.addAttribute( "kind", spec.getPlaceholder().isType() ? "Type" : "Actual" );
            writer.setValue( spec.getPlaceholder().getName() );
            writer.endNode();
        }
    }


    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Long id = Long.parseLong( reader.getAttribute( "id" ) );
        Requirement requirement = getCommunityDao().createRequirement( id );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( "name".equals( nodeName ) ) {
                requirement.setName( reader.getValue() );
            } else if ( "description".equals( nodeName ) ) {
                requirement.setDescription( reader.getValue() );
            } else if ( nodeName.equals( "information" ) ) {
                requirement.setInformation( reader.getValue() );
            } else if ( "infoTags".equals( nodeName ) ) {
                requirement.addInfoTags( reader.getValue() );
            } else if ( nodeName.equals( "eoi" ) ) {
                requirement.addEoi( reader.getValue() );
            } else if ( "minCount".equals( nodeName ) ) {
                requirement.getCardinality().setMinCount( Integer.parseInt( reader.getValue() ) );
            } else if ( "maxCount".equals( nodeName ) ) {
                requirement.getCardinality().setMaxCount( Integer.parseInt( reader.getValue() ) );
            } else if ( "safeCount".equals( nodeName ) ) {
                requirement.getCardinality().setSafeCount( Integer.parseInt( reader.getValue() ) );
            } else if ( nodeName.equals( "committerSpec" ) ) {
                requirement.setCommitterSpec( readAssignmentSpec( reader, context ) );
            } else if ( nodeName.equals( "beneficiarySpec" ) ) {
                requirement.setBeneficiarySpec( readAssignmentSpec( reader, context ) );
            } else if ( "tags".equals( nodeName ) ) {
                requirement.addTags( reader.getValue() );
            } else if ( "detection-waivers".equals( nodeName ) ) {
                importDetectionWaivers( requirement, reader );
            } else if ( "attachments".equals( nodeName ) ) {
                importAttachments( requirement, reader );
            } else if ( "issue".equals( nodeName ) ) {
                context.convertAnother( requirement, UserIssue.class );
            } else {
                LOG.debug( "Unknown element: " + nodeName );
            }
            reader.moveUp();
        }
        return requirement;
    }

    private Requirement.AssignmentSpec readAssignmentSpec(
            HierarchicalStreamReader reader,
            UnmarshallingContext context ) {
        Requirement.AssignmentSpec assignmentSpec = new Requirement().makeNewAssignmentSpec();
        Requirement.AgentSpec agentSpec = assignmentSpec.getAgentSpec();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "taskName" ) ) {
                assignmentSpec.setTaskName( reader.getValue() );
            } else if ( "taskTags".equals( nodeName ) ) {
                assignmentSpec.addRequiredTags( reader.getValue() );
            } else if ( "minCount".equals( nodeName ) ) {
                assignmentSpec.getCardinality().setMinCount( Integer.parseInt( reader.getValue() ) );
            } else if ( "maxCount".equals( nodeName ) ) {
                assignmentSpec.getCardinality().setMaxCount( Integer.parseInt( reader.getValue() ) );
            } else if ( "safeCount".equals( nodeName ) ) {
                assignmentSpec.getCardinality().setSafeCount( Integer.parseInt( reader.getValue() ) );
            } else if ( nodeName.equals( "event" ) ) {
                String idString = reader.getAttribute( "id" );
                assignmentSpec.setEvent( getEntity(
                        Event.class,
                        reader.getValue(),
                        Long.parseLong( idString ),
                        ModelEntity.Kind.Type,
                        context ) );
            } else if ( nodeName.equals( "timing" ) ) {
                assignmentSpec.setTiming( Phase.Timing.valueOf( reader.getValue() ) );
            } else if ( nodeName.equals( "actor" ) ) {
                String idString = reader.getAttribute( "id" );
                agentSpec.setActor( getEntity(
                        Actor.class,
                        reader.getValue(),
                        Long.parseLong( idString ),
                        ModelEntity.Kind.Actual,
                        context ) );
            } else if ( nodeName.equals( "fixedOrg" ) ) {
                String idString = reader.getAttribute( "id" );
                agentSpec.setFixedOrgId( Long.parseLong( idString ) );
            } else if ( nodeName.equals( "placeholder" ) ) {
                String idString = reader.getAttribute( "id" );
                ModelEntity.Kind kind = kind( reader.getAttribute( "kind" ) );
                agentSpec.setPlaceholder( getEntity(
                        Organization.class,
                        reader.getValue(),
                        Long.parseLong( idString ),
                        kind, context ) );
            } else if ( nodeName.equals( "jurisdiction" ) ) {
                String idString = reader.getAttribute( "id" );
                ModelEntity.Kind kind = kind( reader.getAttribute( "kind" ) );
                agentSpec.setJurisdiction( getEntity(
                        Place.class,
                        reader.getValue(),
                        Long.parseLong( idString ),
                        kind, context ) );
            }
            reader.moveUp();
        }
        return assignmentSpec;
    }

}
