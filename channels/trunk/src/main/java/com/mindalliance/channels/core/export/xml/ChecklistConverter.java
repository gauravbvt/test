package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.checklist.ActionStep;
import com.mindalliance.channels.core.model.checklist.AssetProvisioning;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.Condition;
import com.mindalliance.channels.core.model.checklist.LocalCondition;
import com.mindalliance.channels.core.model.checklist.Outcome;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.model.checklist.StepGuard;
import com.mindalliance.channels.core.model.checklist.StepOrder;
import com.mindalliance.channels.core.model.checklist.StepOutcome;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/26/13
 * Time: 9:24 AM
 */
public class ChecklistConverter extends AbstractChannelsConverter {

    public ChecklistConverter( XmlStreamer.Context context ) {
        super( context );
    }

    @Override
    public boolean canConvert( Class aClass ) {
        return Checklist.class.isAssignableFrom( aClass );
    }

    @Override
    public void marshal(
            Object object,
            HierarchicalStreamWriter writer,
            MarshallingContext context ) {
        Checklist checklist = (Checklist) object;
        List<Step> effectiveSteps = checklist.listEffectiveSteps();
        List<Condition> effectiveConditions = checklist.listEffectiveConditions();
        List<Outcome> effectiveOutcomes = checklist.listEffectiveOutcomes();
        for ( ActionStep actionStep : checklist.getActionSteps() ) {
            writer.startNode( "actionStep" );
            writer.addAttribute( "required", Boolean.toString( actionStep.isRequired() ) );
            writer.startNode( "uid" );
            writer.setValue( actionStep.getUid() );
            writer.endNode();
            writer.startNode( "action" );
            writer.setValue( actionStep.getAction() );
            writer.endNode();
            writer.startNode( "instructions" );
            writer.setValue( actionStep.getInstructions() );
            writer.endNode();
            if ( actionStep.getAssetProvisioning() != null ) {
                AssetProvisioning assetProvisioning = actionStep.getAssetProvisioning();
                writer.startNode( "assetProvisioning" );
                writer.addAttribute( "assetId", Long.toString( assetProvisioning.getAssetId() ) );
                writer.addAttribute( "partId", Long.toString( assetProvisioning.getPartId() ) );
                writer.endNode();
            }
            writer.endNode();
        }
        for ( Condition condition : checklist.listEffectiveConditions() ) {
            if ( condition.isLocalCondition() ) {
                LocalCondition localCondition = (LocalCondition) condition;
                writer.startNode( "localCondition" );
                writer.setValue( localCondition.getState() );
                writer.endNode();
            }
        }
        for ( StepOrder stepOrder : checklist.listEffectiveStepOrders( effectiveSteps ) ) {
            writer.startNode( "stepOrder" );
            writer.startNode( "prerequisiteStepRef" );
            writer.setValue( stepOrder.getPrerequisiteStepRef() );
            writer.endNode();
            writer.startNode( "stepRef" );
            writer.setValue( stepOrder.getStepRef() );
            writer.endNode();
            writer.endNode();
        }
        for ( StepGuard stepGuard : checklist.listEffectiveAndExplicitStepGuards( effectiveSteps, effectiveConditions ) ) {
            writer.startNode( "stepGuard" );
            writer.addAttribute( "positive", Boolean.toString( stepGuard.isPositive() ) );
            writer.startNode( "conditionRef" );
            writer.setValue( stepGuard.getConditionRef() );
            writer.endNode();
            writer.startNode( "stepRef" );
            writer.setValue( stepGuard.getStepRef() );
            writer.endNode();
            writer.endNode();
        }
        for ( StepOutcome stepOutcome : checklist.listEffectiveStepOutcomes( effectiveSteps, effectiveOutcomes ) ) {
            writer.startNode( "stepOutcome" );
            writer.startNode( "outcomeRef" );
            writer.setValue( stepOutcome.getOutcomeRef() );
            writer.endNode();
            writer.startNode( "stepRef" );
            writer.setValue( stepOutcome.getStepRef() );
            writer.endNode();
            writer.endNode();
        }
        // confirmed
        writer.startNode( "confirmed" );
        writer.setValue( Boolean.toString( checklist.isConfirmed() ) );
        writer.endNode();
    }

    @Override
    public Object unmarshal(
            HierarchicalStreamReader reader,
            UnmarshallingContext context ) {
        Checklist checklist = new Checklist();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "actionStep" ) ) {
                boolean required = Boolean.parseBoolean( reader.getAttribute( "required" ) );
                String action = "";
                String instructions = "";
                String uid = "";
                AssetProvisioning assetProvisioning = null;
                if ( reader.hasMoreChildren() ) {
                    while ( reader.hasMoreChildren() ) {
                        reader.moveDown();
                        String name = reader.getNodeName();
                        if ( name.equals( "uid" ) ) {
                            uid = reader.getValue();
                        } else if ( name.equals( "action" ) ) {
                            action = reader.getValue();
                        } else if ( name.equals( "instructions" ) ) {
                            instructions = reader.getValue();
                        } else if ( name.equals( "assetProvisioning" ) ) {
                            long assetId = Long.parseLong( reader.getAttribute( "assetId" ) );
                            long partId = Long.parseLong( reader.getAttribute( "partId" ) );
                            assetProvisioning = new AssetProvisioning( assetId, partId );
                        }
                        reader.moveUp();
                    }
                } else { // obsolete
                    action = reader.getValue();
                }
                ActionStep actionStep = new ActionStep();
                actionStep.setUid( uid );
                actionStep.setAction( action );
                actionStep.setRequired( required );
                actionStep.setInstructions( instructions );
                actionStep.setAssetProvisioning( assetProvisioning );
                checklist.addActionStep( actionStep );
            } else if ( nodeName.equals( "localCondition" ) ) {
                LocalCondition localCondition = new LocalCondition( reader.getValue() );
                checklist.addLocalCondition( localCondition );
            } else if ( nodeName.equals( "stepOrder" ) ) {
                StepOrder stepOder = new StepOrder();
                reader.moveDown();
                assert reader.getNodeName().equals( "prerequisiteStepRef" );
                stepOder.setPrerequisiteStepRef( reader.getValue() );
                reader.moveUp();
                reader.moveDown();
                assert reader.getNodeName().equals( "stepRef" );
                stepOder.setStepRef( reader.getValue() );
                reader.moveUp();
                checklist.addStepOrder( stepOder );
            } else if ( nodeName.equals( "stepGuard" ) ) {
                StepGuard stepGuard = new StepGuard();
                boolean positive = Boolean.parseBoolean( reader.getAttribute( "positive" ) );
                stepGuard.setPositive( positive );
                reader.moveDown();
                assert reader.getNodeName().equals( "conditionRef" );
                stepGuard.setConditionRef( reader.getValue() );
                reader.moveUp();
                reader.moveDown();
                assert reader.getNodeName().equals( "stepRef" );
                stepGuard.setStepRef( reader.getValue() );
                reader.moveUp();
                checklist.addStepGuard( stepGuard );
            } else if ( nodeName.equals( "stepOutcome" ) ) {
                StepOutcome stepOutcome = new StepOutcome();
                reader.moveDown();
                assert reader.getNodeName().equals( "outcomeRef" );
                stepOutcome.setOutcomeRef( reader.getValue() );
                reader.moveUp();
                reader.moveDown();
                assert reader.getNodeName().equals( "stepRef" );
                stepOutcome.setStepRef( reader.getValue() );
                reader.moveUp();
                checklist.addStepOutcome( stepOutcome );
            } else if ( nodeName.equals( "confirmed" ) ) {
                checklist.setConfirmed( Boolean.parseBoolean( reader.getValue() ) );
            }
            reader.moveUp();
        }
        return checklist;
    }

}
