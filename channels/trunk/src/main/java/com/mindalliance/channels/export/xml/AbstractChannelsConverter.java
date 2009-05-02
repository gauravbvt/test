package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.export.ConnectionSpecification;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

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
        return exporter.getChannels().getQueryService();
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

/*    protected Map<Connector, List<ConnectionSpecification>> getPortalConnectors( UnmarshallingContext context ) {
    Map<Connector, List<ConnectionSpecification>> portalConnectors =
            (Map<Connector, List<ConnectionSpecification>>) context.get( "portalConnectors" );
    if ( portalConnectors == null ) {
        portalConnectors = new HashMap<Connector, List<ConnectionSpecification>>();
        context.put( "portalConnectors", portalConnectors );
    }
    return portalConnectors;
}*/

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
}
