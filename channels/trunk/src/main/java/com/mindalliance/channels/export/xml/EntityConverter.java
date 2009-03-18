package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.pages.Project;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.List;

/**
 * Abstract XStream converter for Entities.
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
        ModelObject entity = (ModelObject) object;
        assert entity.isEntity();
        writer.addAttribute( "id", String.valueOf( entity.getId() ) );
        String name = entity.getName() == null ? "" : entity.getName();
        writer.addAttribute( "name", name );
        writer.startNode( "description" );
        writer.setValue( entity.getDescription() == null ? "" : entity.getDescription() );
        writer.endNode();
        writeSpecifics( entity, writer, context );
        // User issues
        List<Issue> issues = Project.service().findAllUserIssues( entity );
        for ( Issue issue : issues ) {
            writer.startNode( "issue" );
            context.convertAnother( issue );
            writer.endNode();
        }
    }

    /**
     * Write specific properties to xml stream.
     *
     * @param entity  the entity model object being converted
     * @param writer  the xml stream
     * @param context a context
     */
    abstract protected void writeSpecifics( ModelObject entity,
                                            HierarchicalStreamWriter writer,
                                            MarshallingContext context );

    /**
     * {@inheritDoc}
     */
    public Object unmarshal( HierarchicalStreamReader reader,
                             UnmarshallingContext context ) {
        // String id = reader.getAttribute( "id" );
        // do nothing for now with id -- will use it to disambiguate homonymous entities
        String name = reader.getAttribute( "name" );
        ModelObject entity = findOrMakeEntity( name );
        if ( entity != null ) {
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                if ( nodeName.equals( "description" ) ) {
                    entity.setDescription( reader.getValue() );
                } else {
                    setSpecific( entity, nodeName, reader, context );
                }
                reader.moveUp();
            }
        }
        return entity;
    }

    /**
     * Set an entity model object's specific property from xml.
     *
     * @param entity   the entity
     * @param nodeName the name of the property
     * @param reader  the xml stream
     * @param context a context
     */
    abstract protected void setSpecific(
            ModelObject entity,
            String nodeName,
            HierarchicalStreamReader reader,
            UnmarshallingContext context );

    /**
     * Find or make an entity.
     *
     * @param name entity's name
     * @return an entity
     */
    abstract ModelObject findOrMakeEntity( String name );

}
