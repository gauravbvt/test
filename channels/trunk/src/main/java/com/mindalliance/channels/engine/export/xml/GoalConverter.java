package com.mindalliance.channels.engine.export.xml;

import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 1, 2010
 * Time: 11:27:15 AM
 */
public class GoalConverter extends AbstractChannelsConverter {

    public GoalConverter( XmlStreamer.Context context ) {
        super( context );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Goal.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(
            Object object,
            HierarchicalStreamWriter writer,
            MarshallingContext context ) {
        Goal goal = (Goal) object;
        if ( goal.getCategory() != null ) {
            writer.startNode( "category" );
            writer.setValue( goal.getCategory().name() );
            writer.endNode();
        }
        if ( goal.getOrganization() != null ) {
            Organization organization = goal.getOrganization();
            writer.startNode( "organization" );
            writer.addAttribute( "id", Long.toString( organization.getId() ) );
            writer.addAttribute( "kind", organization.getKind().name() );
            writer.setValue( organization.getName() );
            writer.endNode();
        }
        writer.startNode( "intent" );
        writer.setValue( goal.isPositive() ? "gain" : "mitigate" );
        writer.endNode();
        writer.startNode( "level" );
        writer.setValue( goal.getLevel().toString() );
        writer.endNode();
        writer.startNode( "endsWithSegment" );
        writer.setValue( "" + goal.isEndsWithSegment() );
        writer.endNode();
        writer.startNode( "description" );
        writer.setValue( goal.getDescription() );
        writer.endNode();
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(
            HierarchicalStreamReader reader,
            UnmarshallingContext context ) {
        Goal goal = new Goal();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "category" ) ) {
                Goal.Category category = Goal.Category.valueOf( reader.getValue() );
                goal.setCategory( category );
            } else if ( nodeName.equals( "organization" ) ) {
                Long id = Long.parseLong( reader.getAttribute( "id" ) );
                String kind = reader.getAttribute( "kind" );
                Organization org = getEntity(
                        Organization.class,
                        reader.getValue(),
                        id,
                        ModelEntity.Kind.valueOf( kind ),
                        context );
                goal.setOrganization( org );
            } else if ( nodeName.equals( "level" ) ) {
                goal.setLevel( Level.valueOf( reader.getValue() ) );
            } else if ( nodeName.equals( "intent" ) ) {
                goal.setPositive( reader.getValue().equals( "gain" ) );
            } else if ( nodeName.equals( "endsWithSegment" ) ) {
                goal.setEndsWithSegment( reader.getValue().equals( "true" ) );
            } else if ( nodeName.equals( "description" ) ) {
                goal.setDescription( reader.getValue() );
            }
            reader.moveUp();
        }
        return goal;
    }
}
