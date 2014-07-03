package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.Attachable;
import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.community.CommunityDao;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.AbstractModelObjectDao;
import com.mindalliance.channels.core.dao.ModelDao;
import com.mindalliance.channels.core.export.ConnectionSpecification;
import com.mindalliance.channels.core.model.AttachmentImpl;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Tag;
import com.mindalliance.channels.core.model.Taggable;
import com.mindalliance.channels.core.model.UserIssue;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract XStream converter base class for Channels.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 13, 2009
 * Time: 3:52:08 PM
 */
public abstract class AbstractChannelsConverter implements Converter {

    /**
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( AbstractChannelsConverter.class );

    /**
     * An xmlStreamer.
     */
    private XmlStreamer.Context context;

    protected AbstractChannelsConverter( XmlStreamer.Context context ) {
        this.context = context;
    }

    protected SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" );
    }

    protected String getVersion() {
        return context.getVersion();
    }

    public XmlStreamer.Context getContext() {
        return context;
    }

    boolean isInDomainContext() {
        return getDao() instanceof ModelDao;
    }

    ModelDao getModelDao() {
        return getContext().getModelDao();
    }

    CommunityDao getCommunityDao() {
        return getContext().getCommunityDao();
    }

    AbstractModelObjectDao getDao() {
        return getContext().getModelObjectDao();
    }


    public AttachmentManager getAttachmentManager() {
        return context.getAttachmentManager();
    }

    /**
     * Is a plan being exported?
     *
     * @param context marshalling context
     * @return a boolean
     */
    protected boolean isExportingPlan( MarshallingContext context ) {
        return context.get( "exporting-model" ) != null;
    }

    /**
     * Is a plan being imported?
     *
     * @param context unmarshalling context
     * @return a boolean
     */
    protected boolean isImportingPlan( UnmarshallingContext context ) {
        return context.get( "importing-model" ) != null;
    }

    /**
     * Find or create an entity given name and possibly an id.
     *
     * @param entityClass a class extending ModelObject
     * @param name        a string
     * @param id          a string (null or convertible to a long)
     * @return a model object
     */
    protected <T extends ModelEntity> T findOrCreate( Class<T> entityClass, String name, String id ) {
        if ( id == null ) {
            LOG.warn( "Recreating referenced " + entityClass.getSimpleName() + " without id" );
            return getDao().findOrCreate( entityClass, name, null );
        } else {
            return getDao().findOrCreate( entityClass, name, Long.valueOf( id ) );
        }
    }

    /**
     * Find or create an entity type given name and possibly an id.
     *
     * @param entityClass a class extending ModelObject
     * @param name        a string
     * @param id          a string (null or convertible to a long)
     * @return a model object
     */
    protected <T extends ModelEntity> T findOrCreateType( Class<T> entityClass, String name, String id ) {
        if ( id == null ) {
            LOG.warn( "Recreating referenced " + entityClass.getSimpleName() + " without id" );
            return getDao().findOrCreateType( entityClass, name, null );
        } else {
            return getDao().findOrCreateType( entityClass, name, Long.valueOf( id ) );
        }
    }


    /**
     * Get idMap from context, initializing it if needed.
     *
     * @param context an unmarshalling context
     * @return a map
     */
    @SuppressWarnings("unchecked")
    protected Map<Long, Long> getIdMap( UnmarshallingContext context ) {
        Map<Long, Long> idMap = (Map<Long, Long>) context.get( "idMap" );
        if ( idMap == null ) {
            idMap = new HashMap<Long, Long>();
            context.put( "idMap", idMap );
        }
        return idMap;
    }

    /**
     * Get proxy connectors: connectors meant to be replaced by external connectors.
     *
     * @param context an unmarshalling context
     * @return a map
     */
    @SuppressWarnings("unchecked")
    protected Map<Connector, List<ConnectionSpecification>> getProxyConnectors( UnmarshallingContext context ) {
        Map<Connector, List<ConnectionSpecification>> proxyConnectors =
                (Map<Connector, List<ConnectionSpecification>>) context.get( "proxyConnectors" );
        if ( proxyConnectors == null ) {
            proxyConnectors = new HashMap<Connector, List<ConnectionSpecification>>();
            context.put( "proxyConnectors", proxyConnectors );
        }
        return proxyConnectors;
    }

    /**
     * Export a model object's user issues.
     *
     * @param modelObject a model object
     * @param writer      a writer
     * @param context     a marshalling context
     */
    protected void exportUserIssues(
            ModelObject modelObject,
            HierarchicalStreamWriter writer,
            MarshallingContext context ) {
        List<UserIssue> issues = getDao().findAllUserIssues( modelObject );
        for ( UserIssue issue : issues ) {
            writer.startNode( "issue" );
            context.convertAnother( issue );
            writer.endNode();
        }
    }

    /**
     * Export issue detection waivers.
     *
     * @param modelObject a model object
     * @param writer      a writer
     */
    protected void exportDetectionWaivers( ModelObject modelObject, HierarchicalStreamWriter writer ) {
        if ( !modelObject.getWaivedIssueDetections().isEmpty() ) {
            writer.startNode( "detection-waivers" );
            for ( String detection : modelObject.getWaivedIssueDetections() ) {
                writer.startNode( "detection" );
                writer.setValue( detection );
                writer.endNode();
            }
            writer.endNode();
        }
    }

    /**
     * Export attachments.
     *
     * @param attachable an attachable
     * @param writer     a writer
     */
    protected void exportAttachments(
            Attachable attachable,
            HierarchicalStreamWriter writer ) {
        if ( !attachable.getAttachments().isEmpty() ) {
            writer.startNode( "attachments" );
            for ( Attachment attachment : attachable.getAttachments() ) {
                writer.startNode( "attachment" );
                writer.addAttribute( "type", attachment.getType().name() );
                writer.addAttribute( "url", attachment.getUrl() );
                writer.setValue( attachment.getName() );
                writer.endNode();
            }
            writer.endNode();
        }
    }

    /**
     * Import attachment tickets.
     *
     * @param attachable an attachable
     * @param reader     a reader
     */
    protected void importAttachments( Attachable attachable, HierarchicalStreamReader reader ) {
        AttachmentManager attachmentManager = getAttachmentManager();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "attachment" ) ) {
                String typeName = reader.getAttribute( "type" );
                if ( typeName.equals( ( "InfoStandards" ) ) ) {  // obsolete
                    LOG.warn( "Dropping info standards attachment - obsolete" );
                } else {
                    Attachment.Type type = Attachment.Type.valueOf( typeName );
                    String name;
                    String url;
                    if ( IteratorUtils.toList( reader.getAttributeNames() ).contains( "url" ) ) {
                        // new format
                        url = reader.getAttribute( "url" );
                        name = reader.getValue();
                    } else {
                        // old format
                        url = reader.getValue();
                        name = "";
                    }
                    if ( attachmentExists( url ) ) {
                        attachmentManager.addAttachment( new AttachmentImpl( url, type, name ), attachable );
                    } else {
                        LOG.warn( "Dropping attachment to {} (not found)", url );
                    }
                }
            }
            reader.moveUp();
        }
    }

    private boolean attachmentExists( String url ) {
        if ( isInDomainContext() )
            return getAttachmentManager().exists( getModel(), url );
        else
            return getAttachmentManager().exists( getPlanCommunity(), url );
    }

    /**
     * Import issue detection waivers.
     *
     * @param modelObject a model object
     * @param reader      a reader
     */
    protected void importDetectionWaivers( ModelObject modelObject, HierarchicalStreamReader reader ) {
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            assert ( nodeName.equals( "detection" ) );
            String detection = reader.getValue();
            modelObject.addWaiveIssueDetection( detection );
            reader.moveUp();
        }
    }

    /**
     * Find model object.
     *
     * @param clazz a class
     * @param id    a long
     * @return a model object
     * @throws NotFoundException if not found
     */
    @SuppressWarnings("unchecked")
    protected <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        if ( isPlanContext() && getContext().getPlan().getId() == id ) {
            // in case the issue is about the plan being loaded -- it is not yet findable.
            return (T) getContext().getPlan();
        }
        if ( isCommunityContext() && getContext().getPlanCommunity().getId() == id ) {
            // in case the issue is about the plan being loaded -- it is not yet findable.
            return (T) getContext().getPlanCommunity();
        }
        return getDao().find( clazz, id );
    }

    protected boolean isPlanContext() {
        return getDao() instanceof ModelDao;
    }

    protected boolean isCommunityContext() {
        return getDao() instanceof CommunityDao;
    }

    /**
     * Find or make entity
     *
     * @param clazz   entity class
     * @param name    entity name
     * @param id      entity id
     * @param kind    a model entity kind (type or actual)
     * @param context an unmarshalling context
     * @return an entity model object
     */
    protected <T extends ModelEntity> T getEntity(
            Class<T> clazz,
            String name,
            Long id,
            ModelEntity.Kind kind,
            UnmarshallingContext context ) {
        return getEntity(
                clazz,
                name,
                id,
                kind == ModelEntity.Kind.Type,
                isImportingPlan( context ),
                getIdMap( context )
        );
    }

    /**
     * Find or make entity.
     *
     * @param clazz         entity class
     * @param name          entity name
     * @param id            entity id
     * @param isType        whether the entity is a type vs actual
     * @param importingPlan boolean
     * @param idMap         id map
     * @return an entity model object
     */
    protected <T extends ModelEntity> T getEntity(
            Class<T> clazz, String name, Long id, boolean isType, boolean importingPlan, Map<Long, Long> idMap ) {

        T entity;
        if ( isType ) {
            entity = getDao().findOrCreateType( clazz, name, importingPlan ? id : null );
            entity.setType();
        } else {
            entity = getDao().findOrCreate( clazz, name, importingPlan ? id : null );
            entity.setActual();
        }
        idMap.put( id, entity.getId() );
        return entity;
    }

    /**
     * Get current plan.
     *
     * @return a plan
     */
    protected CollaborationModel getModel() {
        return getModelDao().getCollaborationModel();
    }

    /**
     * Get current plan.
     *
     * @return a plan
     */
    protected PlanCommunity getPlanCommunity() {
        return getCommunityDao().getPlanCommunity();
    }


    /**
     * Get entity kind given the kind's name.
     *
     * @param name a string
     * @return an entity kind
     */
    protected ModelEntity.Kind kind( String name ) {
        return name == null
                ? ModelEntity.Kind.Actual
                : "Type".equals( name )
                ? ModelEntity.Kind.Type
                : ModelEntity.Kind.Actual;
    }

    /**
     * Export tags.
     *
     * @param writer   a hierarchical stream writer
     * @param taggable a taggable
     */
    protected void writeTags( HierarchicalStreamWriter writer, Taggable taggable ) {
        if ( !taggable.getRawTags().isEmpty() ) {
            writer.startNode( "tags" );
            writer.setValue( Tag.tagsToString( taggable.getRawTags() ) );
            writer.endNode();
        }
    }

}
