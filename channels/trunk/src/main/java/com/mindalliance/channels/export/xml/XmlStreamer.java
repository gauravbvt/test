package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Channel;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Job;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.util.SemMatch;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.dao.Journal;
import com.mindalliance.channels.export.Exporter;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.export.ConnectionSpecification;
import com.mindalliance.channels.pages.Project;
import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.collections.Predicate;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

/**
 * XML scenario importer.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 9:12:25 PM
 */
public class XmlStreamer implements Importer, Exporter {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( XmlStreamer.class );


    /**
     * Holder of a configured XStream instance.
     */
    private class ConfiguredXStream {

        /**
         * The xstream instance
         */
        private XStream xstream;

        public ConfiguredXStream() {
            xstream = new XStream();
            configure();
        }

        public XStream getXstream() {
            return xstream;
        }

        /**
         * Configuration of xstream instance
         */
        private void configure() {
            xstream.alias( "command", AbstractCommand.class );
            xstream.alias( "journal", Journal.class );
            xstream.alias( "project", Project.class );
            xstream.alias( "actor", Actor.class );
            xstream.aliasType( "flow", Flow.class );
            xstream.alias( "jurisdiction", Place.class );
            xstream.alias( "location", Place.class );
            xstream.alias( "organization", Organization.class );
            xstream.alias( "part", Part.class );
            xstream.alias( "role", Role.class );
            xstream.alias( "resource", ResourceSpec.class );
            xstream.alias( "issue", UserIssue.class );
            xstream.alias( "scenario", Scenario.class );
            xstream.alias( "channel", Channel.class );
            xstream.alias( "job", Job.class );
            xstream.registerConverter( new ProjectConverter() );
            xstream.registerConverter( new JournalConverter() );
            xstream.registerConverter( new CommandConverter() );
            xstream.registerConverter( new ScenarioConverter() );
            xstream.registerConverter( new PartConverter() );
            xstream.registerConverter( new FlowConverter() );
            xstream.registerConverter( new ActorConverter() );
            xstream.registerConverter( new RoleConverter() );
            xstream.registerConverter( new OrganizationConverter() );
            xstream.registerConverter( new PlaceConverter() );
            xstream.registerConverter( new UserIssueConverter() );
            xstream.registerConverter( new ChannelConverter() );
            xstream.registerConverter( new JobConverter() );
        }

        /**
         * Access to xstream instance.
         *
         * @return -- an xstream
         */
        public XStream get() {
            return xstream;
        }

    }

    /**
     * Configured xstream
     */
    private ConfiguredXStream configuredXStream;

    /**
     * Constructor
     */
    public XmlStreamer() {
        configuredXStream = new ConfiguredXStream();
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return "0.1";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Map<String, Object> loadScenario( InputStream stream ) throws IOException {
        ObjectInputStream in = configuredXStream.get().createObjectInputStream( stream );
        Map<String, Object> results;
        try {
            results = (Map<String, Object>) in.readObject();
        } catch ( ClassNotFoundException e ) {
            throw new IOException( "Failed to import scenario", e );
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Scenario importScenario( InputStream stream ) throws IOException {
        ObjectInputStream in = configuredXStream.get().createObjectInputStream( stream );
        Map<String, Object> results;
        try {
            results = (Map<String, Object>) in.readObject();
        } catch ( ClassNotFoundException e ) {
            throw new IOException( "Failed to import scenario", e );
        }
        Map<String, Long> idMap = (Map<String, Long>) results.get( "idMap" );
        Map<Connector, ConnectionSpecification> proxyConnectors =
                (Map<Connector, ConnectionSpecification>) results.get( "proxyConnectors" );
        reconnectExternalFlows( idMap, proxyConnectors );
        return (Scenario) results.get( "scenario" );
    }

    /**
     * {@inheritDoc}
     */
    public void exportScenario( Scenario scenario, OutputStream stream ) throws IOException {
        ObjectOutputStream out = configuredXStream.get()
                .createObjectOutputStream( stream, "export" );
        out.writeObject( scenario );
        out.close();
    }

    /**
     * {@inheritDoc}
     */
    public void exportProject( OutputStream stream ) throws IOException {
        ObjectOutputStream out = configuredXStream.get()
                .createObjectOutputStream( stream, "export" );
        out.writeObject( Project.getProject() );
        out.close();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Map<Long, Long> importProject( FileInputStream stream ) throws IOException {
        Map<Long, Long> idMap;
        ObjectInputStream in = configuredXStream.get().createObjectInputStream( stream );
        try {
            Map<String, Object> results = (Map<String, Object>) in.readObject();
            Map<String, Long> map = (Map<String, Long>) results.get( "idMap" );
            Map<Connector, ConnectionSpecification> proxyConnectors =
                    (Map<Connector, ConnectionSpecification>) results.get( "proxyConnectors" );
            reconnectExternalFlows( map, proxyConnectors );
            idMap = new HashMap<Long, Long>();
            for ( String key : map.keySet() ) {
                idMap.put( Long.valueOf( key ), map.get( key ) );
            }
        } catch ( ClassNotFoundException e ) {
            throw new IOException( "Failed to import project", e );
        }
        return idMap;
    }

    /**
     * {@inheritDoc}
     */
    public void exportJournal( Journal journal, OutputStream stream ) throws IOException {
        ObjectOutputStream out = configuredXStream.get()
                .createObjectOutputStream( stream, "export" );
        out.writeObject( journal );
        out.close();
    }

    /**
     * {@inheritDoc}
     */
    public Journal importJournal( FileInputStream stream ) throws IOException {
        ObjectInputStream in = configuredXStream.get().createObjectInputStream( stream );
        Journal journal;
        try {
            journal = (Journal) in.readObject();
        } catch ( ClassNotFoundException e ) {
            throw new IOException( "Failed to import journal", e );
        }
        return journal;
    }

    /**
     * {@inheritDoc}
     */
    public String getMimeType() {
        return "application/xml";
    }

    @SuppressWarnings( "unchecked" )
    /**
     * {@inheritDoc}
     */
    public void reconnectExternalFlows(
            Map<String, Long> idMap,
            Map<Connector, ConnectionSpecification> proxyConnectors ) {
        Set<Connector> toDelete = new HashSet<Connector>();
        for ( Connector proxyConnector : proxyConnectors.keySet() ) {
            ConnectionSpecification conSpec = proxyConnectors.get( proxyConnector );
            List<Connector> externalConnectors = findMatchingConnectors( conSpec, idMap );
            if ( externalConnectors.size() > 1 ) {
                LOG.warn( "Proxy connector in "
                        + conSpec.getFlowName()
                        + " matched multiple external connectors (expecting one)." );
            }
            for ( Connector externalConnector : externalConnectors ) {
                reconnectProxyConnector( proxyConnector, externalConnector, conSpec.isSource(), idMap );
                toDelete.add( proxyConnector );
            }
        }
        for ( Connector proxyConnector : toDelete ) {
            proxyConnectors.remove( proxyConnector );
        }
    }

    private void reconnectProxyConnector(
            Connector proxyConnector,
            Connector externalConnector,
            // proxyConnector is source?
            boolean isSource,
            Map<String, Long> idMap ) {
        Flow localInnerFlow = proxyConnector.getInnerFlow();
        Part part = isSource
                ? (Part) proxyConnector.getInnerFlow().getTarget()
                : (Part) proxyConnector.getInnerFlow().getSource();
        ExternalFlow externalFlow;
        if ( isSource ) {
            externalFlow = (ExternalFlow) getDqo().connect(
                    externalConnector, part, localInnerFlow.getName()
            );
        } else {
            externalFlow = (ExternalFlow) getDqo().connect(
                    part, externalConnector, localInnerFlow.getName() );
        }
        externalFlow.setChannels( localInnerFlow.getChannels() );
        externalFlow.setMaxDelay( localInnerFlow.getMaxDelay() );
        externalFlow.setSignificanceToSource( localInnerFlow.getSignificanceToSource() );
        externalFlow.setSignificanceToTarget( localInnerFlow.getSignificanceToTarget() );
        externalFlow.setAll( localInnerFlow.isAll() );
        externalFlow.setAskedFor( localInnerFlow.isAskedFor() );
        externalFlow.setDescription( localInnerFlow.getDescription() );
        externalFlow.setWaivedIssueDetections( localInnerFlow.getWaivedIssueDetections() );
        replaceInIdMap( localInnerFlow, externalFlow, idMap );
        localInnerFlow.disconnect();
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

    @SuppressWarnings( "unchecked" )
    private List<Connector> findMatchingConnectors(
            final ConnectionSpecification conSpec,
            final Map<String, Long> idMap ) {
        List<Connector> connectors = new ArrayList<Connector>();
        List<Scenario> scenarios = ConverterUtils.findMatchingScenarios(
                conSpec.getScenarioSpecification() );
        for ( Scenario scenario : scenarios ) {
            Iterator<Connector> iterator =
                    (Iterator<Connector>) new FilterIterator( scenario.nodes(), new Predicate() {
                        public boolean evaluate( Object obj ) {
                            Node node = (Node) obj;
                            return node.isConnector() &&
                                    connectorMatches( (Connector) node,
                                            conSpec, idMap );
                        }
                    }
                    );
            while ( iterator.hasNext() ) connectors.add( iterator.next() );
        }
        return connectors;
    }

    private boolean connectorMatches( Connector externalConnector,
                                      ConnectionSpecification conSpec,
                                      Map<String, Long> idMap ) {
        // we are matching the part attached to the connector,
        // so it's input-edness is the reverse of that of the connector
        if ( externalConnector.isSource() == conSpec.isSource() ) return false;
        Flow externalInnerFlow = externalConnector.getInnerFlow();
        Part part = (Part) ( conSpec.isSource() ? externalInnerFlow.getSource() : externalInnerFlow.getTarget() );
        String partIdValue = conSpec.getPartSpecification().getId();
        Long mappedPartId = idMap.get( partIdValue );
        boolean partIdMatches = partIdValue != null 
                                    && mappedPartId != null
                                    && mappedPartId == part.getId();
        return SemMatch.same( externalInnerFlow.getName(), conSpec.getFlowName() )
                && ( partIdMatches || ConverterUtils.partMatches( part, conSpec.getPartSpecification() ) );
    }

    /**
     * Get Data Query Object
     *
     * @return a data query object
     */
    protected DataQueryObject getDqo() {
        return Project.getProject().getDqo();
    }


}
