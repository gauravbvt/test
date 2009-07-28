package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.AttachmentManager;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.export.ConnectionSpecification;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    protected String getVersion() {
        return context.getVersion();
    }

    public XmlStreamer.Context getContext() {
        return context;
    }

    public QueryService getQueryService() {
        return context.getQueryService();
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
        return context.get( "exporting-plan" ) != null;
    }

    /**
     * Is a plan being imported?
     *
     * @param context unmarshalling context
     * @return a boolean
     */
    protected boolean isImportingPlan( UnmarshallingContext context ) {
        return context.get( "importing-plan" ) != null;
    }

    /**
     * Find or create an entity given name and possibly an id.
     *
     * @param entityClass a class extending ModelObject
     * @param name        a string
     * @param id          a string (null or convertible to a long)
     * @return a model object
     */
    protected <T extends ModelObject> T findOrCreate( Class<T> entityClass, String name, String id ) {
        if ( id == null ) {
            LOG.warn( "Recreating referenced " + entityClass.getSimpleName() + " without id" );
            return getQueryService().findOrCreate( entityClass, name );
        } else {
            return getQueryService().findOrCreate( entityClass, name, Long.valueOf( id ) );
        }
    }

    /**
     * Get idMap from context, initializing it if needed.
     *
     * @param context an unmarshalling context
     * @return a map
     */
    @SuppressWarnings( "unchecked" )
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
    @SuppressWarnings( "unchecked" )
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
        List<Issue> issues = getQueryService().findAllUserIssues( modelObject );
        for ( Issue issue : issues ) {
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
     * @param modelObject a model object
     * @param writer      a writer
     */
    protected void exportAttachments(
            ModelObject modelObject,
            HierarchicalStreamWriter writer ) {
        if ( !modelObject.getAttachments().isEmpty() ) {
            writer.startNode( "attachments" );
            for ( Attachment attachment : modelObject.getAttachments() ) {
                writer.startNode( "attachment" );
                writer.addAttribute( "type", attachment.getType().name() );
                writer.setValue( attachment.getUrl() );
                writer.endNode();
            }
            writer.endNode();
        }
    }

    /**
     * Import attachment tickets.
     *
     * @param modelObject a model object
     * @param reader      a reader
     */
    protected void importAttachments( ModelObject modelObject, HierarchicalStreamReader reader ) {
        AttachmentManager attachmentManager = getAttachmentManager();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "attachment" ) ) {
                Attachment.Type type = Attachment.Type.valueOf( reader.getAttribute( "type" ) );
                String url = reader.getValue();
                if ( attachmentManager.exists( url ) ) {
                    modelObject.addAttachment( new Attachment( url, type ) );
                } else {
                    LOG.warn( "Dropping attachment to " + url + " (not found)" );
                }
            }
            reader.moveUp();
        }
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
            modelObject.waiveIssueDetection( detection );
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
    @SuppressWarnings( "unchecked" )
    protected <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        T about;
        if ( getContext().getPlan().getId() == id ) {
            // in case the issue is about the plan being loaded -- it is not yet findable.
            about = (T) getContext().getPlan();
        } else {
            about = getQueryService().find( clazz, id );
        }
        return about;
    }

}
