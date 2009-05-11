package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.AbstractService;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.Importer;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.dao.Journal;
import com.mindalliance.channels.export.ConnectionSpecification;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Job;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Risk;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.UserIssue;
import com.mindalliance.channels.util.SemMatch;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * XML scenario importer.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 9:12:25 PM
 */
public class XmlStreamer extends AbstractService implements Importer, Exporter {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( XmlStreamer.class );

    /**
     * Version number.
     */
    private String version = "0.0";

    private Channels channels;

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
            xstream.alias( "plan", Plan.class );
            xstream.alias( "planevent", Event.class );
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
            xstream.alias( "risk", Risk.class );
            xstream.alias( "channel", Channel.class );
            xstream.alias( "job", Job.class );
            xstream.registerConverter( new PlanConverter( XmlStreamer.this ) );
            xstream.registerConverter( new EventConverter( XmlStreamer.this ) );
            xstream.registerConverter( new JournalConverter( XmlStreamer.this ) );
            xstream.registerConverter( new CommandConverter(XmlStreamer.this) );
            xstream.registerConverter( new ScenarioConverter( XmlStreamer.this ) );
            xstream.registerConverter( new RiskConverter( XmlStreamer.this ) );
            xstream.registerConverter( new PartConverter( XmlStreamer.this ) );
            xstream.registerConverter( new FlowConverter( XmlStreamer.this ) );
            xstream.registerConverter( new ActorConverter( XmlStreamer.this ) );
            xstream.registerConverter( new RoleConverter( XmlStreamer.this ) );
            xstream.registerConverter( new OrganizationConverter( XmlStreamer.this ) );
            xstream.registerConverter( new PlaceConverter( XmlStreamer.this ) );
            xstream.registerConverter( new UserIssueConverter( XmlStreamer.this ) );
            xstream.registerConverter( new ChannelConverter( XmlStreamer.this ) );
            xstream.registerConverter( new JobConverter( XmlStreamer.this ) );
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
        return version;
    }

    public void setVersion( String version ) {
        this.version = version;
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
    public void exportAll( OutputStream stream ) throws IOException {
        ObjectOutputStream out = configuredXStream.get()
                .createObjectOutputStream( stream, "export" );
        out.writeObject( Channels.getPlan() );
        out.close();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Map<Long, Long> importAll( FileInputStream stream ) throws IOException {
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
            throw new IOException( "Failed to import app", e );
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
            externalFlow = (ExternalFlow) getQueryService().connect(
                    externalConnector, part, localInnerFlow.getName()
            );
        } else {
            externalFlow = (ExternalFlow) getQueryService().connect(
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
                conSpec.getScenarioSpecification(),
                getQueryService() );
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
     * Get query service
     *
     * @return a query service
     */
    protected QueryService getQueryService() {
        return getChannels().getQueryService();
    }

    public Channels getChannels() {
        return channels;
    }

    public void setChannels( Channels channels ) {
        this.channels = channels;
    }
}
