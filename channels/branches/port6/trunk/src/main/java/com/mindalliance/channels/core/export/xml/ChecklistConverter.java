package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.checklist.ActionStep;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.Condition;
import com.mindalliance.channels.core.model.checklist.LocalCondition;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.model.checklist.StepGuard;
import com.mindalliance.channels.core.model.checklist.StepOrder;
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
        Checklist checklist = (Checklist)object;
        List<Step> effectiveSteps = checklist.listEffectiveSteps();
        List<Condition> effectiveConditions = checklist.listEffectiveConditions();
        for ( ActionStep actionStep : checklist.getActionSteps() ) {
            writer.startNode( "actionStep" );
            writer.addAttribute( "required", Boolean.toString( actionStep.isRequired() ) );
            writer.setValue( actionStep.getAction() );
            writer.endNode();
        }
        for (LocalCondition localCondition : checklist.getLocalConditions()) {
            writer.startNode( "localCondition" );
            writer.setValue( localCondition.getState() );
            writer.endNode();
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
        for ( StepGuard stepGuard : checklist.listEffectiveStepGuards( effectiveSteps, effectiveConditions )) {
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
       // confirmed
            writer.startNode( "confirmed" );
            writer.setValue( Boolean.toString( checklist.isConfirmed() ) );
            writer.endNode();
    }

    @Override
    public Object unmarshal(
            HierarchicalStreamReader reader,
            UnmarshallingContext context ) {
        Checklist checklist = new Checklist(  );
        while( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "actionStep" ) ) {
                boolean required = Boolean.parseBoolean( reader.getAttribute( "required" ) );
                ActionStep actionStep = new ActionStep( reader.getValue() );
                actionStep.setRequired( required );
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
            }  else if ( nodeName.equals( "stepGuard" ) ) {
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
            } else if ( nodeName.equals( "confirmed" ) ) {
                checklist.setConfirmed( Boolean.parseBoolean( reader.getValue() ) );
            }
            reader.moveUp();
        }
        return checklist;
    }

}
