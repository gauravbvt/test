package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.AbstractService;
import com.mindalliance.channels.AttachmentManager;
import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.Importer;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.dao.IdGenerator;
import com.mindalliance.channels.dao.Journal;
import com.mindalliance.channels.export.ConnectionSpecification;
import com.mindalliance.channels.export.ImportExportFactory;
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
public class XmlStreamer extends AbstractService implements ImportExportFactory {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( XmlStreamer.class );

    /**
     * Version number.
     */
    private String version = "0.0";

    /** The thing that generate ids... */
    private final IdGenerator idGenerator;

    public XmlStreamer( IdGenerator idGenerator ) {
        this.idGenerator = idGenerator;
    }

    /**
     * Create an import context.
     * @param service the query service
     * @param plan the current plan
     * @return an importer
     */
    public Importer createImporter( QueryService service, Plan plan ) {
        return new Context( service, plan );
    }

    /**
     * Create an export context.
     * @param service the query service
     * @param plan the current plan
     * @return an exporter
     */
    public Exporter createExporter( QueryService service, Plan plan ) {
        return new Context( service, plan );
    }

    /**
     * Get current version, for upgrading purposes.
     * @return a string
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
    public String getMimeType() {
        return "application/xml";
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    //=======================================================
    /**
     * Holder of a configured XStream instance in a given context.
     */
    public class Context implements Importer, Exporter {

        /**
         * The xstream instance
         */
        private XStream xstream = new XStream();

        /** The current plan. */
        private Plan plan;

        /** The current service. */
        private final QueryService queryService;

        private Context( QueryService queryService, Plan plan ) {
            this.plan = plan;
            this.queryService = queryService;
            addAliases( xstream );
            registerConverters( xstream );
        }

        public Plan getPlan() {
            return plan;
        }

        public QueryService getQueryService() {
            return queryService;
        }

        public AttachmentManager getAttachmentManager() {
            return queryService.getAttachmentManager();
        }

        public IdGenerator getIdGenerator() {
            return XmlStreamer.this.getIdGenerator();
        }

        public String getVersion() {
            return XmlStreamer.this.getVersion();
        }

        private void registerConverters( XStream stream ) {
            stream.registerConverter( new PlanConverter( this ) );
            stream.registerConverter( new EventConverter( this ) );
            stream.registerConverter( new JournalConverter( this ) );
            stream.registerConverter( new CommandConverter( this ) );
            stream.registerConverter( new ScenarioConverter( this ) );
            stream.registerConverter( new RiskConverter( this ) );
            stream.registerConverter( new PartConverter( this ) );
            stream.registerConverter( new FlowConverter( this ) );
            stream.registerConverter( new ActorConverter( this ) );
            stream.registerConverter( new RoleConverter( this ) );
            stream.registerConverter( new OrganizationConverter( this ) );
            stream.registerConverter( new PlaceConverter( this ) );
            stream.registerConverter( new UserIssueConverter( this ) );
            stream.registerConverter( new ChannelConverter( this ) );
            stream.registerConverter( new JobConverter( this ) );
            stream.registerConverter( new ExportConverter( this ) );
        }

        private void addAliases( XStream stream ) {
            stream.setMode( XStream.NO_REFERENCES );
            stream.alias( "command", AbstractCommand.class );
            stream.alias( "journal", Journal.class );
            stream.alias( "plan", Plan.class );
            stream.alias( "planevent", Event.class );
            stream.alias( "actor", Actor.class );
            stream.aliasType( "flow", Flow.class );
            stream.alias( "jurisdiction", Place.class );
            stream.alias( "location", Place.class );
            stream.alias( "organization", Organization.class );
            stream.alias( "part", Part.class );
            stream.alias( "role", Role.class );
            stream.alias( "resource", ResourceSpec.class );
            stream.alias( "issue", UserIssue.class );
            stream.alias( "scenario", Scenario.class );
            stream.alias( "risk", Risk.class );
            stream.alias( "channel", Channel.class );
            stream.alias( "job", Job.class );
            stream.alias( "export", Export.class );
        }

        /** {@inheritDoc} */
        public Map<String, Object> loadScenario( InputStream inputStream ) throws IOException {
            ObjectInputStream in = xstream.createObjectInputStream( inputStream );
            try {
                return (Map<String, Object>) in.readObject();
            } catch ( ClassNotFoundException e ) {
                throw new IOException( "Failed to import scenario", e );
            }
        }

        /** {@inheritDoc} */
        public Scenario restoreScenario( String xml ) {

            DataHolder dataHolder = xstream.newDataHolder();
            // MUST set to importingPlan
            dataHolder.put( "importing-plan", true );

            Map<String, Object> results = (Map<String, Object>) xstream.unmarshal(
                new XppReader( new StringReader( xml ) ), null, dataHolder );

            reconnectExternalFlows(
                (Map<Connector, List<ConnectionSpecification>>) results.get( "proxyConnectors" ),
                false );

            return (Scenario) results.get( "scenario" );
        }

        /** {@inheritDoc} */
        public Journal importJournal( FileInputStream stream ) throws IOException {
            ObjectInputStream in = xstream.createObjectInputStream( stream );
            try {
                return (Journal) in.readObject();
            } catch ( ClassNotFoundException e ) {
                throw new IOException( "Failed to import journal", e );
            }
        }

        /** {@inheritDoc} */
        public void export( Journal journal, OutputStream stream ) throws IOException {
            ObjectOutputStream out = xstream.createObjectOutputStream( stream, "export" );
            out.writeObject( journal );
            out.close();
        }

        /** {@inheritDoc} */
        public void importPlan( FileInputStream stream ) throws IOException {
            ObjectInputStream in = xstream.createObjectInputStream( stream );
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

        /** {@inheritDoc} */
        public void export( Plan plan, OutputStream stream ) throws IOException {
            ObjectOutputStream out = xstream.createObjectOutputStream( stream, "export" );
            out.writeObject( plan );
            out.close();
        }

        /** {@inheritDoc} */
        public void export( Scenario scenario, OutputStream stream ) throws IOException {
            ObjectOutputStream out = xstream.createObjectOutputStream( stream, "export" );
            out.writeObject( scenario );
            out.close();
        }

        /** {@inheritDoc} */
        public Scenario importScenario( InputStream stream ) throws IOException {
            ObjectInputStream in = xstream.createObjectInputStream( stream );
            try {
                Map<String, Object> results = (Map<String, Object>) in.readObject();
                reconnectExternalFlows(
                    (Map<Connector, List<ConnectionSpecification>>) results.get( "proxyConnectors" ),
                    false );
                return (Scenario) results.get( "scenario" );

            } catch ( ClassNotFoundException e ) {
                throw new IOException( "Failed to import scenario", e );
            }
        }

        /** {@inheritDoc} */
        public void reconnectExternalFlows(
                Map<Connector, List<ConnectionSpecification>> proxyConnectors, boolean loadingPlan ) {

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

        /**
         * Make sure the reconnected external flow has its pre-export id if importing a plan.
         * @param proxyConnector
         * @param externalConnector
         * @param conSpec
         * @param loadingPlan
         */
        private void reconnectProxyConnector(
                Connector proxyConnector,
                Connector externalConnector,
                // proxyConnector is source?
                ConnectionSpecification conSpec,
                boolean loadingPlan ) {

            Flow localInnerFlow = proxyConnector.getInnerFlow();
            Part part = conSpec.isSource() ? (Part) localInnerFlow.getTarget()
                                           : (Part) localInnerFlow.getSource();
            String innerName = localInnerFlow.getName();

            ExternalFlow externalFlow;
            if ( loadingPlan ) {
                Long id = conSpec.getExternalFlowId();
                externalFlow = conSpec.isSource() ?
                    (ExternalFlow) queryService.connect( externalConnector, part, innerName, id )
                  : (ExternalFlow) queryService.connect( part, externalConnector, innerName, id );
            } else {
                externalFlow = conSpec.isSource() ?
                    (ExternalFlow) queryService.connect( externalConnector, part, innerName )
                  : (ExternalFlow) queryService.connect( part, externalConnector, innerName );
            }
            copy( localInnerFlow, externalFlow );
            localInnerFlow.disconnect();
        }

        private void copy( Flow inner, Flow external ) {
            external.setChannels( inner.getChannels() );
            external.setMaxDelay( inner.getMaxDelay() );
            external.setSignificanceToSource( inner.getSignificanceToSource() );
            external.setSignificanceToTarget( inner.getSignificanceToTarget() );
            external.setAll( inner.isAll() );
            external.setAskedFor( inner.isAskedFor() );
            external.setDescription( inner.getDescription() );
            external.setWaivedIssueDetections( inner.getWaivedIssueDetections() );
            external.setAttachments( inner.getAttachments() );
        }

        private List<Connector> findMatchingConnectors( final ConnectionSpecification conSpec ) {
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
                                        connectorMatches( (Connector) node, conSpec );
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
            Part part = (Part) ( conSpec.isSource() ? externalInnerFlow.getSource()
                                                    : externalInnerFlow.getTarget() );
            Long partIdValue = Long.parseLong( conSpec.getPartSpecification().getId() );
            boolean partIdMatches = partIdValue != null && partIdValue == part.getId();
            return SemMatch.same( externalInnerFlow.getName(), conSpec.getFlowName() )
                    && ( partIdMatches
                         || ConverterUtils.partMatches( part, conSpec.getPartSpecification() ) );
        }

        /** {@inheritDoc} */
        public String getMimeType() {
            return XmlStreamer.this.getMimeType();
        }
    }

}
