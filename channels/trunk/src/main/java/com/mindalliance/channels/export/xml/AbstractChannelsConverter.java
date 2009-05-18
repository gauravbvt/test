package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.AttachmentManager;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.export.ConnectionSpecification;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
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
    private Exporter exporter;


    public AbstractChannelsConverter( Exporter exporter ) {
        this.exporter = exporter;
    }

    protected String getVersion() {
        return exporter.getVersion();
    }

    public Exporter getExporter() {
        return exporter;
    }

    /**
     * Get query service
     *
     * @return a query service
     */
    protected QueryService getQueryService() {
        return Channels.instance().getQueryService();
    }

    /**
     * Get attachment manager service.
     *
     * @return an attachment manager
     */
    protected AttachmentManager getAttachmentManager() {
        return Channels.instance().getAttachmentManager();
    }

    protected boolean isExportingPlan( MarshallingContext context ) {
        return context.get( "exporting-plan" ) != null;
    }

    /**
     * Get idMap from context, initializing it if needed.
     *
     * @param context an unmarshalling context
     * @return a map
     */
    @SuppressWarnings( "unchecked" )
    protected Map<String, Long> getIdMap( UnmarshallingContext context ) {
        Map<String, Long> idMap = (Map<String, Long>) context.get( "idMap" );
        if ( idMap == null ) {
            idMap = new HashMap<String, Long>();
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
    protected Map<Connector, ConnectionSpecification> getProxyConnectors( UnmarshallingContext context ) {
        Map<Connector, ConnectionSpecification> proxyConnectors =
                (Map<Connector, ConnectionSpecification>) context.get( "proxyConnectors" );
        if ( proxyConnectors == null ) {
            proxyConnectors = new HashMap<Connector, ConnectionSpecification>();
            context.put( "proxyConnectors", proxyConnectors );
        }
        return proxyConnectors;
    }

    /**
     * Make a substitution in the idmap
     *
     * @param previous    an identifiable
     * @param replacement an identifiable
     * @param idMap       a map
     */
    protected void replaceInIdMap( Identifiable previous, Identifiable replacement, Map<String, Long> idMap ) {
        for ( Map.Entry<String, Long> entry : idMap.entrySet() ) {
            if ( entry.getValue() == previous.getId() ) {
                entry.setValue( replacement.getId() );
                return;
            }
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
     * Export attachmnet tickets.
     *
     * @param modelObject   a model object
     * @param writer        a writer
     * @param exportingPlan whether exporting a whole plan (vs a single scenario)
     */
    protected void exportAttachmentTickets(
            ModelObject modelObject,
            HierarchicalStreamWriter writer,
            boolean exportingPlan ) {
        if ( !modelObject.getAttachmentTickets().isEmpty() ) {
            writer.startNode( "attachments" );
            if ( exportingPlan ) {
                for ( String ticket : modelObject.getAttachmentTickets() ) {
                    writer.startNode( "ticket" );
                    writer.setValue( ticket );
                    writer.endNode();
                }
            } else {
                // only export attached URLs directly (file attachments are not portables)
                for ( String ticket : modelObject.getAttachmentTickets() ) {
                    Attachment attachment = getAttachmentManager().getAttachment( ticket );
                    if ( attachment.isUrl() ) {
                        writer.startNode( "url" );
                        writer.addAttribute( "type", attachment.getType().name() );
                        writer.setValue( attachment.getUrl() );
                        writer.endNode();
                    }
                }

            }
            writer.endNode();
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
     * Import attachment tickets.
     *
     * @param modelObject a model object
     * @param reader      a reader
     */
    protected void importAttachmentTickets( ModelObject modelObject, HierarchicalStreamReader reader ) {
        AttachmentManager attachmentManager = getAttachmentManager();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "ticket" ) ) {
                String ticket = reader.getValue();
                modelObject.addAttachmentTicket( ticket );
            } else if ( nodeName.equals( "url" ) ) {
                String url = null;
                try {
                    String type = reader.getAttribute( "type" );
                    url = reader.getValue();
                    String ticket = attachmentManager.attach(
                            Attachment.Type.valueOf( type ),
                            new URL( url ),
                            modelObject.getAttachmentTickets() );
                    modelObject.addAttachmentTicket( ticket );
                } catch ( MalformedURLException e ) {
                    LOG.warn( "Can't attach URL " + url + " to " + modelObject, e);
                }
            }
            reader.moveUp();
        }
    }
}
