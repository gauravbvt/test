package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.Information;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Objective;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/21/13
 * Time: 3:35 PM
 */
public class FunctionConverter extends EntityConverter {

    public FunctionConverter( XmlStreamer.Context context ) {
        super( context );
    }

    @Override
    protected Class<? extends ModelEntity> getEntityClass() {
        return Function.class;
    }

    @Override
    public boolean canConvert( Class type ) {
        return Function.class.isAssignableFrom( type );
    }

    @Override
    protected void writeSpecifics( ModelEntity entity, HierarchicalStreamWriter writer, MarshallingContext context ) {
        Function function = (Function) entity;
        // goal categories
        for ( Objective objective : function.getObjectives() ) {
            writer.startNode( "objective" );
            writer.addAttribute( "positive", Boolean.toString( objective.isPositive() ) );
            writer.setValue( objective.getGoalCategory().name() );
            writer.endNode();
        }
        // info needed
        for ( Information infoNeeded : function.getInfoNeeded() ) {
            writer.startNode( "infoNeeded" );
            writer.startNode( "name" );
            writer.setValue( infoNeeded.getName() );
            writer.endNode();
            if ( infoNeeded.getInfoProduct() != null ) {
                writer.startNode( "infoProduct" );
                writer.addAttribute( "id", Long.toString( infoNeeded.getInfoProduct().getId() ) );
                writer.addAttribute( "kind", "Type" );
                writer.setValue( infoNeeded.getInfoProduct().getName() );
                writer.endNode();
            }
            for ( ElementOfInformation eoi : infoNeeded.getEois() ) {
                writer.startNode( "eoi" );
                context.convertAnother( eoi );
                writer.endNode();
            }
            writer.endNode();
        }
        // info acquired
        for ( Information infoAcquired : function.getInfoAcquired() ) {
            writer.startNode( "infoAcquired" );
            writer.startNode( "name" );
            writer.setValue( infoAcquired.getName() );
            writer.endNode();
            if ( infoAcquired.getInfoProduct() != null ) {
                writer.startNode( "infoproduct" );
                writer.addAttribute( "id", Long.toString( infoAcquired.getInfoProduct().getId() ) );
                writer.addAttribute( "kind", "Type" );
                writer.setValue( infoAcquired.getInfoProduct().getName() );
                writer.endNode();
            }
            for ( ElementOfInformation eoi : infoAcquired.getEois() ) {
                writer.startNode( "eoi" );
                context.convertAnother( eoi );
                writer.endNode();
            }
            writer.endNode();
        }
    }

    @Override
    protected void setSpecific( ModelEntity entity,
                                String nodeName,
                                HierarchicalStreamReader reader,
                                UnmarshallingContext context ) {
        Function function = (Function) entity;
        // goal category
        if ( nodeName.equals( "objective" ) ) {
            boolean positive = Boolean.parseBoolean( reader.getAttribute( "positive" ) );
            Goal.Category goalCategory = Goal.Category.valueOf( reader.getValue() );
            Objective objective = new Objective( goalCategory, positive );
            function.addObjective( objective );
            // info needed
        } else if ( nodeName.equals( "infoNeeded" ) ) {
            Information info = new Information();
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                nodeName = reader.getNodeName();
                if ( nodeName.equals( "name" ) ) {
                    info.setName( reader.getValue() );
                } else if ( nodeName.equals( "infoproduct" ) ) {
                    String idString = reader.getAttribute( "id" );
                    info.setInfoProduct( getEntity(
                            InfoProduct.class,
                            reader.getValue(),
                            Long.parseLong( idString ),
                            ModelEntity.Kind.Type,
                            context ) );
                } else if ( nodeName.equals( "eoi" ) ) {
                    info.addEoi(
                            (ElementOfInformation) context.convertAnother(
                                    getModel(),
                                    ElementOfInformation.class ) );
                }
                reader.moveUp();
            }
            function.addInfoNeeded( info );
            // info acquired
        } else if ( nodeName.equals( "infoAcquired" ) ) {
            Information info = new Information();
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                nodeName = reader.getNodeName();
                if ( nodeName.equals( "name" ) ) {
                    info.setName( reader.getValue() );
                } else if ( nodeName.equals( "infoproduct" ) ) {
                    String idString = reader.getAttribute( "id" );
                    info.setInfoProduct( getEntity(
                            InfoProduct.class,
                            reader.getValue(),
                            Long.parseLong( idString ),
                            ModelEntity.Kind.Type,
                            context ) );
                } else if ( nodeName.equals( "eoi" ) ) {
                    info.addEoi(
                            (ElementOfInformation) context.convertAnother(
                                    getModel(),
                                    ElementOfInformation.class ) );
                }
                reader.moveUp();
            }
            function.addInfoAcquired( info );
         }
    }

}
