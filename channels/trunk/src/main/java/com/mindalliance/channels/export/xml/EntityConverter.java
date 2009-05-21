package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.UserIssue;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.Map;

/**
 * Abstract XStream converter for Entities.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 16, 2009
 * Time: 4:10:27 PM
 */
public abstract class EntityConverter extends AbstractChannelsConverter {

    public EntityConverter( Exporter exporter ) {
        super( exporter );
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
        exportDetectionWaivers( entity, writer );
        exportAttachmentTickets( entity, writer, this.isExportingPlan( context ) );
        writeSpecifics( entity, writer, context );
        // User issues
        exportUserIssues( entity, writer, context );
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
    @SuppressWarnings( "unchecked" )
    public Object unmarshal( HierarchicalStreamReader reader,
                             UnmarshallingContext context ) {
        Map<Long, Long> idMap = getIdMap( context );
        boolean importingPlan = isImportingPlan( context );
        String name = reader.getAttribute( "name" );
        Long id = Long.parseLong( reader.getAttribute( "id" ) );
        ModelObject entity = getEntity( name, id, importingPlan, idMap );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "description" ) ) {
                entity.setDescription( reader.getValue() );
            } else if ( nodeName.equals( "detection-waivers" ) ) {
                importDetectionWaivers( entity, reader );
            } else if ( nodeName.equals( "attachments" ) ) {
                importAttachmentTickets( entity, reader );
            } else if ( nodeName.equals( "issue" ) ) {
                context.convertAnother( entity, UserIssue.class );
            }else {
                setSpecific( entity, nodeName, reader, context );
            }
            reader.moveUp();
        }
        return entity;
    }

    private ModelObject getEntity( String name, Long id, boolean importingPlan, Map<Long, Long> idMap ) {
        ModelObject entity = findOrMakeEntity( name, id, importingPlan );
        idMap.put( id, entity.getId() );
        return entity;
    }

    /**
     * Set an entity model object's specific property from xml.
     *
     * @param entity   the entity
     * @param nodeName the name of the property
     * @param reader   the xml stream
     * @param context  a context
     */
    abstract protected void setSpecific(
            ModelObject entity,
            String nodeName,
            HierarchicalStreamReader reader,
            UnmarshallingContext context );

    /**
     * Find or make an entity with id if importing plan.
     *
     * @param name entity's name
     * @param id a Long
     *@param importingPlan a boolean
     * @return an entity
     */
    abstract ModelObject findOrMakeEntity( String name, Long id, boolean importingPlan );

}
