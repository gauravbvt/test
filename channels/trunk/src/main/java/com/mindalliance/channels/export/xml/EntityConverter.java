package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.export.xml.XmlStreamer.Context;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelEntity.Kind;
import com.mindalliance.channels.model.UserIssue;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Abstract XStream converter for Entities. Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved. Proprietary
 * and Confidential. User: jf Date: Jan 16, 2009 Time: 4:10:27 PM
 */
public abstract class EntityConverter extends AbstractChannelsConverter {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( XmlStreamer.class );

    protected EntityConverter( Context context ) {
        super( context );
    }

    @Override
    public void marshal(
            Object object, HierarchicalStreamWriter writer, MarshallingContext context ) {
        ModelEntity entity = (ModelEntity) object;
        writer.addAttribute( "id", String.valueOf( entity.getId() ) );
        assert entity.getKind() != null;
        writer.addAttribute( "kind", entity.getKind().name() );
        String name = entity.getName() == null ? "" : entity.getName();
        writer.addAttribute( "name", name );
        writer.startNode( "description" );
        writer.setValue( entity.getDescription() == null ? "" : entity.getDescription() );
        writer.endNode();
        writeTags( writer, entity );
        for ( ModelEntity type : entity.getTypes() ) {
            writer.startNode( "type" );
            writer.addAttribute( "id", String.valueOf( type.getId() ) );
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
     * @param entity the entity  being converted
     * @param writer the xml stream
     * @param context a context
     */
    protected abstract void writeSpecifics(
            ModelEntity entity, HierarchicalStreamWriter writer, MarshallingContext context );

    @Override
    @SuppressWarnings( "unchecked" )
    public Object unmarshal(
            HierarchicalStreamReader reader, UnmarshallingContext context ) {
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
        boolean isType = kind != null && Kind.valueOf( kind ) == Kind.Type;
        ModelEntity entity = getEntity( getEntityClass(), name, id, isType, importingPlan, idMap );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( "description".equals( nodeName ) ) {
                entity.setDescription( reader.getValue() );
            } else if ( "tags".equals( nodeName ) ) {
                entity.addTags( reader.getValue() );
            } else if ( "tag".equals( nodeName )
                        || "type".equals( nodeName ) ) {  // todo obsolete "tag" as of Jan. 27, 2011
                Long typeId = Long.parseLong( reader.getAttribute( "id" ) );
                String typeName = reader.getValue();
                ModelEntity type = getEntity( getEntityClass(), typeName, typeId,
                                              // always a type
                                              true, importingPlan, idMap );
                entity.addType( type );
            } else if ( "detection-waivers".equals( nodeName ) ) {
                importDetectionWaivers( entity, reader );
            } else if ( "attachments".equals( nodeName ) ) {
                importAttachments( entity, reader );
            } else if ( "issue".equals( nodeName ) ) {
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
    protected abstract Class<? extends ModelEntity> getEntityClass();

    /**
     * Set an entity model object's specific property from xml.
     *
     * @param entity the entity
     * @param nodeName the name of the property
     * @param reader the xml stream
     * @param context a context
     */
    protected abstract void setSpecific(
            ModelEntity entity, String nodeName, HierarchicalStreamReader reader, UnmarshallingContext context );
}
