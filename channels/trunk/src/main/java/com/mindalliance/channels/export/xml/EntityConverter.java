package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Entity;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.pages.Project;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.List;

/**
 * Abstract XStream converter for Entities
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 16, 2009
 * Time: 4:10:27 PM
 */
public abstract class EntityConverter implements Converter {

    public EntityConverter() {

    }

    /**
     * {@inheritDoc}
     */
    public void marshal( Object object,
                         HierarchicalStreamWriter writer,
                         MarshallingContext context ) {
        Entity entity = (Entity) object;
        writer.addAttribute( "id", String.valueOf( entity.getId() ) );
        writer.startNode( "name" );
        writer.setValue( entity.getName() == null ? "" : entity.getName() );
        writer.endNode();
        writer.startNode( "description" );
        writer.setValue( entity.getDescription() == null ? "" : entity.getDescription() );
        writer.endNode();
        // User issues
        List<Issue> issues = Project.dao().findAllUserIssues( entity );
        for ( Issue issue : issues ) {
            writer.startNode( "issue" );
            context.convertAnother( issue );
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal( HierarchicalStreamReader reader,
                             UnmarshallingContext context ) {
        Entity entity;
        // String id = reader.getAttribute( "id" );
        // do nothing for now with id -- will use it to disambiguate homonymous entities
        String name = "";
        String description = "";
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "name" ) ) {
                name = reader.getValue();
            } else if ( nodeName.equals( "description" ) ) {
                description = reader.getValue();
            } else {
                throw new ConversionException( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        entity = findOrMakeEntity( name );
        entity.setDescription( description );
        return entity;
    }

    /**
     * Find or make an entity
     *
     * @param name entity's name
     * @return an entity
     */
    abstract Entity findOrMakeEntity( String name );

}
