package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.UserIssue;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( XmlStreamer.class );

    protected EntityConverter( XmlStreamer.Context context ) {
        super( context );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal( Object object,
                         HierarchicalStreamWriter writer,
                         MarshallingContext context ) {
        ModelEntity entity = (ModelEntity) object;
        writer.addAttribute( "id", String.valueOf( entity.getId() ) );
        assert ( entity.getKind() != null );
        writer.addAttribute( "kind", entity.getKind().name() );
        String name = entity.getName() == null ? "" : entity.getName();
        writer.addAttribute( "name", name );
        writer.startNode( "description" );
        writer.setValue( entity.getDescription() == null ? "" : entity.getDescription() );
        writer.endNode();
        writeTags( writer, entity );
        for ( ModelEntity type : entity.getTypes() ) {
            writer.startNode( "type" );
            writer.addAttribute( "id", type.getId() + "" );
            writer.addAttribute( "kind", type.getKind().name() );
            writer.setValue( type.getName() );
            writer.endNode();
        }
        exportDetectionWaivers( entity, writer );
        exportAttachments( entity, writer );
        writeSpecifics( entity, writer, context );
        // User issues
        exportUserIssues( entity, writer, context );
    }

    /**
     * Write specific properties to xml stream.
     *
     * @param entity  the entity  being converted
     * @param writer  the xml stream
     * @param context a context
     */
    abstract protected void writeSpecifics( ModelEntity entity,
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
        String kind = reader.getAttribute( "kind" );
        if ( kind == null ) {
            LOG.warn( "Kind of entity is not set" );
            kind = ModelEntity.defaultKindFor( getEntityClass() ).name();
        }
        // The default kind is Actual
        boolean isType = ( kind != null && ModelEntity.Kind.valueOf( kind ) == ModelEntity.Kind.Type );
        ModelEntity entity = getEntity(
                getEntityClass(),
                name,
                id,
                isType,
                importingPlan,
                idMap );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "description" ) ) {
                entity.setDescription( reader.getValue() );
            } else if ( nodeName.equals( "tags") ) {
                entity.addTags( reader.getValue() );
            } else if ( nodeName.equals( "tag" ) || nodeName.equals( "type" ) ) {  // todo obsolete "tag" as of Jan. 27, 2011
                Long typeId = Long.parseLong( reader.getAttribute( "id" ) );
                String typeName = reader.getValue();
                ModelEntity type = getEntity(
                        getEntityClass(),
                        typeName,
                        typeId,
                        // always a type
                        true,
                        importingPlan,
                        idMap );
                entity.addType( type );
            } else if ( nodeName.equals( "detection-waivers" ) ) {
                importDetectionWaivers( entity, reader );
            } else if ( nodeName.equals( "attachments" ) ) {
                importAttachments( entity, reader );
            } else if ( nodeName.equals( "issue" ) ) {
                context.convertAnother( entity, UserIssue.class );
            } else {
                setSpecific( entity, nodeName, reader, context );
            }
            reader.moveUp();
        }
        return entity;
    }

    /**
     * The entity's class.
     *
     * @return a class
     */
    abstract protected Class<? extends ModelEntity> getEntityClass();

    /**
     * Set an entity model object's specific property from xml.
     *
     * @param entity   the entity
     * @param nodeName the name of the property
     * @param reader   the xml stream
     * @param context  a context
     */
    abstract protected void setSpecific(
            ModelEntity entity,
            String nodeName,
            HierarchicalStreamReader reader,
            UnmarshallingContext context );


}
