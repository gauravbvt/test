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
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.io.xml.XppReader;
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
import java.io.StringReader;
import java.util.ArrayList;
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
            xstream.setMode( XStream.NO_REFERENCES );
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
            xstream.alias( "export", Export.class);
            xstream.registerConverter( new PlanConverter( XmlStreamer.this ) );
            xstream.registerConverter( new EventConverter( XmlStreamer.this ) );
            xstream.registerConverter( new JournalConverter( XmlStreamer.this ) );
            xstream.registerConverter( new CommandConverter( XmlStreamer.this ) );
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
            xstream.registerConverter( new ExportConverter( XmlStreamer.this ) );
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
    public Scenario restoreScenario( String xml ) {
        // MUST set to importingPlan
        XStream xstream = configuredXStream.get();
        DataHolder dataHolder = xstream.newDataHolder();
        dataHolder.put( "importing-plan", true );
        Map<String, Object> results = (Map<String, Object>) xstream.unmarshal(
                new XppReader( new StringReader( xml ) ),
                null,
                dataHolder );
        Map<Connector, List<ConnectionSpecification>> proxyConnectors =
                (Map<Connector, List<ConnectionSpecification>>) results.get( "proxyConnectors" );
        reconnectExternalFlows( proxyConnectors, false );
        return (Scenario) results.get( "scenario" );
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
        Map<Connector, List<ConnectionSpecification>> proxyConnectors =
                (Map<Connector, List<ConnectionSpecification>>) results.get( "proxyConnectors" );
        reconnectExternalFlows( proxyConnectors, false );
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
    public void importAll( FileInputStream stream ) throws IOException {
        ObjectInputStream in = configuredXStream.get().createObjectInputStream( stream );
        try {
            Map<String, Object> results = (Map<String, Object>) in.readObject();
            Map<Connector, List<ConnectionSpecification>> proxyConnectors =
                    (Map<Connector, List<ConnectionSpecification>>) results.get( "proxyConnectors" );
            reconnectExternalFlows( proxyConnectors, true );
            // Do nothing with idMap also in results.
        } catch ( ClassNotFoundException e ) {
            throw new IOException( "Failed to import plan.", e );
        }
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
            Map<Connector, List<ConnectionSpecification>> proxyConnectors,
            boolean loadingPlan ) {
        Set<Connector> toDelete = new HashSet<Connector>();
        for ( Connector proxyConnector : proxyConnectors.keySet() ) {
            List<ConnectionSpecification> conSpecs = proxyConnectors.get( proxyConnector );
            for ( ConnectionSpecification conSpec : conSpecs ) {
                List<Connector> externalConnectors = findMatchingConnectors( conSpec );
                if ( externalConnectors.size() > 1 ) {
                    LOG.warn( "Proxy connector in "
                            + conSpec.getFlowName()
                            + " matched multiple external connectors (expecting and using only one)." );
                }
                if ( externalConnectors.isEmpty() ) {
                    LOG.warn( "Proxy connector in "
                            + conSpec.getFlowName()
                            + " matched no external connector (expecting one)." );
                } else {
                    reconnectProxyConnector(
                            proxyConnector,
                            externalConnectors.get( 0 ),
                            conSpec,
                            loadingPlan );
                }
                toDelete.add( proxyConnector );
            }
        }
        for ( Connector proxyConnector : toDelete ) {
            proxyConnectors.remove( proxyConnector );
        }
    }

    // Make sure the reconnected external flow has its pre-export id if importing a plan.
    private void reconnectProxyConnector(
            Connector proxyConnector,
            Connector externalConnector,
            // proxyConnector is source?
            ConnectionSpecification conSpec,
            boolean loadingPlan ) {
        Flow localInnerFlow = proxyConnector.getInnerFlow();
        Part part = conSpec.isSource()
                ? (Part) proxyConnector.getInnerFlow().getTarget()
                : (Part) proxyConnector.getInnerFlow().getSource();
        ExternalFlow externalFlow;
        if ( loadingPlan ) {
            if ( conSpec.isSource() ) {
                externalFlow = (ExternalFlow) getQueryService().connect(
                        externalConnector, part, localInnerFlow.getName(), conSpec.getExternalFlowId()
                );
            } else {
                externalFlow = (ExternalFlow) getQueryService().connect(
                        part, externalConnector, localInnerFlow.getName(), conSpec.getExternalFlowId() );
            }
        } else {
            if ( conSpec.isSource() ) {
                externalFlow = (ExternalFlow) getQueryService().connect(
                        externalConnector, part, localInnerFlow.getName()
                );
            } else {
                externalFlow = (ExternalFlow) getQueryService().connect(
                        part, externalConnector, localInnerFlow.getName() );
            }
        }
        externalFlow.setChannels( localInnerFlow.getChannels() );
        externalFlow.setMaxDelay( localInnerFlow.getMaxDelay() );
        externalFlow.setSignificanceToSource( localInnerFlow.getSignificanceToSource() );
        externalFlow.setSignificanceToTarget( localInnerFlow.getSignificanceToTarget() );
        externalFlow.setAll( localInnerFlow.isAll() );
        externalFlow.setAskedFor( localInnerFlow.isAskedFor() );
        externalFlow.setDescription( localInnerFlow.getDescription() );
        externalFlow.setWaivedIssueDetections( localInnerFlow.getWaivedIssueDetections() );
        externalFlow.setAttachments( localInnerFlow.getAttachments() );
        localInnerFlow.disconnect();
    }

    @SuppressWarnings( "unchecked" )
    private List<Connector> findMatchingConnectors(
            final ConnectionSpecification conSpec ) {
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
                                            conSpec );
                        }
                    }
                    );
            while ( iterator.hasNext() ) connectors.add( iterator.next() );
        }
        return connectors;
    }

    private boolean connectorMatches( Connector externalConnector,
                                      ConnectionSpecification conSpec ) {
        // we are matching the part attached to the connector,
        // so it's input-edness is the reverse of that of the connector
        if ( externalConnector.isSource() == conSpec.isSource() ) return false;
        Flow externalInnerFlow = externalConnector.getInnerFlow();
        Part part = (Part) ( conSpec.isSource() ? externalInnerFlow.getSource() : externalInnerFlow.getTarget() );
        Long partIdValue = Long.parseLong( conSpec.getPartSpecification().getId() );
        boolean partIdMatches = partIdValue != null
                && partIdValue == part.getId();
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


}
