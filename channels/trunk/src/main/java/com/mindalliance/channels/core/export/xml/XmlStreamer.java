/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.IssueDetectionWaiver;
import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.community.CommunityDao;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.AbstractModelObjectDao;
import com.mindalliance.channels.core.dao.Exporter;
import com.mindalliance.channels.core.dao.IdGenerator;
import com.mindalliance.channels.core.dao.ImportExportFactory;
import com.mindalliance.channels.core.dao.Importer;
import com.mindalliance.channels.core.dao.Journal;
import com.mindalliance.channels.core.dao.ModelDao;
import com.mindalliance.channels.core.export.ConnectionSpecification;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.AssignedLocation;
import com.mindalliance.channels.core.model.Availability;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.EventPhase;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.Transformation;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.UserIssue;
import com.mindalliance.channels.core.model.asset.AssetConnection;
import com.mindalliance.channels.core.model.asset.AssetConnections;
import com.mindalliance.channels.core.model.asset.AssetField;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.io.xml.XppReader;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.wicket.markup.MarkupType;
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
 * XML plan importer.
 */
public class XmlStreamer implements ImportExportFactory {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( XmlStreamer.class );

    /**
     * Version number.
     */
    private String version = "0.0";

    /**
     * The thing that generate ids...
     */
    private final IdGenerator idGenerator;


    /**
     * Where the attachments are managed...
     */
    private AttachmentManager attachmentManager;

    public XmlStreamer( IdGenerator idGenerator ) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Importer createImporter( String userName, AbstractModelObjectDao dao ) {
        return new Context( dao, userName );
    }

    @Override
    public Exporter createExporter( String userName, AbstractModelObjectDao dao ) {
        return new Context( dao, userName );
    }

    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    public void setAttachmentManager( AttachmentManager attachmentManager ) {
        this.attachmentManager = attachmentManager;
    }

    /**
     * Get current version, for upgrading purposes.
     *
     * @return a string
     */
    public String getVersion() {
        return version;
    }

    public void setVersion( String version ) {
        this.version = version;
    }

    /**
     * Return the mime type of imported/exported files.
     *
     * @return application/xml
     */
    public MarkupType getMimeType() {
        return new MarkupType( "xml", "application/xml" );
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    //=======================================================

    /**
     * Holder of a configured XStream instance in a given context.
     */
    public class Context implements Importer, Exporter {

        private final String userName;

        /**
         * The xstream instance.
         */
        private XStream xstream = new XStream();

        /**
         * The current service.
         */
        private final AbstractModelObjectDao modelObjectDao;

        private Context( AbstractModelObjectDao modelObjectDao, String userName ) {
            this.userName = userName;
            this.modelObjectDao = modelObjectDao;
            addAliases( xstream );
            registerConverters( xstream );
        }

        protected ModelDao getModelDao() {
            return (ModelDao) getModelObjectDao();
        }

        protected CommunityDao getCommunityDao() {
            return (CommunityDao) getModelObjectDao();
        }

        public PlanCommunity getPlanCommunity() {
            return getCommunityDao().getPlanCommunity();
        }

        public CollaborationModel getPlan() {
            return getModelDao().getCollaborationModel();
        }

        public AbstractModelObjectDao getModelObjectDao() {
            return modelObjectDao;
        }

        /**
         * The username responsible for the import.
         *
         * @return "daemon", usually...
         */
        public String getUserName() {
            return userName;
        }

        public AttachmentManager getAttachmentManager() {
            return XmlStreamer.this.getAttachmentManager();
        }

        @SuppressWarnings({"OverlyLongMethod", "OverlyCoupledMethod"})
        private void registerConverters( XStream stream ) {
            stream.registerConverter( new PlanCommunityConverter( this ) );
            stream.registerConverter( new ModelConverter( this ) );
            stream.registerConverter( new EventConverter( this ) );
            stream.registerConverter( new JournalConverter( this ) );
            stream.registerConverter( new CommandConverter( this ) );
            stream.registerConverter( new SegmentConverter( this ) );
            stream.registerConverter( new GoalConverter( this ) );
            stream.registerConverter( new PartConverter( this ) );
            stream.registerConverter( new CycleConverter( this ) );
            stream.registerConverter( new ChecklistConverter( this ) );
            stream.registerConverter( new FlowConverter( this ) );
            stream.registerConverter( new ElementOfInformationConverter( this ) );
            stream.registerConverter( new ActorConverter( this ) );
            stream.registerConverter( new RoleConverter( this ) );
            stream.registerConverter( new OrganizationConverter( this ) );
            stream.registerConverter( new PlaceConverter( this ) );
            stream.registerConverter( new UserIssueConverter( this ) );
            stream.registerConverter( new ChannelConverter( this ) );
            stream.registerConverter( new JobConverter( this ) );
            stream.registerConverter( new PhaseConverter( this ) );
            stream.registerConverter( new ClassificationConverter( this ) );
            stream.registerConverter( new AvailabilityConverter( this ) );
            stream.registerConverter( new TransmissionMediumConverter( this ) );
            stream.registerConverter( new InfoProductConverter( this ) );
            stream.registerConverter( new InfoFormatConverter( this ) );
            stream.registerConverter( new FunctionConverter( this ) );
            stream.registerConverter( new MaterialAssetConverter( this ) );
            stream.registerConverter( new AssetFieldConverter( this ) );
            stream.registerConverter( new AssetConnectionConverter( this ) );
            stream.registerConverter( new TransformationConverter( this ) );
            stream.registerConverter( new EventPhaseConverter( this ) );
            stream.registerConverter( new EventTimingConverter( this ) );
            stream.registerConverter( new AgreementConverter( this ) );
            stream.registerConverter( new RequirementConverter( this ) );
            stream.registerConverter( new AssignedLocationConverter( this ) );
            stream.registerConverter( new IssueDetectionWaiverConverter( this ) );
            stream.registerConverter( new ExportConverter( this ) );
        }

        @SuppressWarnings({"OverlyLongMethod", "OverlyCoupledMethod"})
        private void addAliases( XStream stream ) {
            stream.setMode( XStream.NO_REFERENCES );
            stream.alias( "command", AbstractCommand.class );
            stream.alias( "journal", Journal.class );
            stream.alias( "plancommunity", PlanCommunity.class );
            stream.alias( "plan", CollaborationModel.class );
            stream.alias( "classification", Classification.class );
            stream.alias( "availability", Availability.class );
            stream.alias( "medium", TransmissionMedium.class );
            stream.alias( "planevent", Event.class );
            stream.alias( "actor", Actor.class );
            stream.aliasType( "flow", Flow.class );
            stream.aliasType( "eoi", ElementOfInformation.class );
            stream.alias( "jurisdiction", Place.class );
            stream.alias( "location", Place.class );
            stream.alias( "organization", Organization.class );
            stream.alias( "part", Part.class );
            stream.alias( "role", Role.class );
            stream.alias( "resource", ResourceSpec.class );
            stream.alias( "issue", UserIssue.class );
            stream.alias( "segment", Segment.class );
            stream.alias( "goal", Goal.class );
            stream.alias( "assetfield", AssetField.class );
            stream.alias( "checklist", Checklist.class );
            stream.alias( "channel", Channel.class );
            stream.alias( "job", Job.class );
            stream.alias( "export", Export.class );
            stream.alias( "transformation", Transformation.class );
            stream.alias( "eventphase", EventPhase.class );
            stream.alias( "eventtiming", EventTiming.class );
            stream.alias( "requirement", Requirement.class );
            stream.alias( "assignedLocation", AssignedLocation.class );
            stream.alias( "infoproduct", InfoProduct.class );
            stream.alias( "function", Function.class );
            stream.alias( "asset", MaterialAsset.class );
            stream.alias( "format", InfoFormat.class );
            stream.alias( "assetconnection", AssetConnection.class );
            stream.alias( "issuedetectionwaiver", IssueDetectionWaiver.class );
        }

        @Override
        public Map<String, Object> loadSegment( InputStream inputStream ) throws IOException {
            ObjectInputStream in = xstream.createObjectInputStream( inputStream );
            try {
                return (Map<String, Object>) in.readObject();
            } catch ( ClassNotFoundException e ) {
                throw new IOException( "Failed to import segment", e );
            }
        }

        @Override
        public Segment restoreSegment( String xml ) {

            DataHolder dataHolder = xstream.newDataHolder();
            // MUST set to importingPlan
            dataHolder.put( "importing-model", true );

            Map<String, Object> results = (Map<String, Object>) xstream.unmarshal(
                    new XppReader( new StringReader( xml ) ), null, dataHolder );

            reconnectExternalFlows(
                    (Map<Connector, List<ConnectionSpecification>>) results.get( "proxyConnectors" ),
                    false );

            return (Segment) results.get( "segment" );
        }

        @Override
        public Journal importJournal( FileInputStream stream ) throws IOException {
            ObjectInputStream in = xstream.createObjectInputStream( stream );
            try {
                return (Journal) in.readObject();
            } catch ( ClassNotFoundException e ) {
                throw new IOException( "Failed to import journal", e );
            }
        }

        @Override
        public void export( Journal journal, OutputStream stream ) throws IOException {
            ObjectOutputStream out = xstream.createObjectOutputStream( stream, "export" );
            out.writeObject( journal );
            out.close();
        }

        @Override
        public void importPlan( FileInputStream stream ) throws IOException {
            ObjectInputStream in = xstream.createObjectInputStream( stream );
            try {
                Map<String, Object> results = (Map<String, Object>) in.readObject();
                Map<Connector, List<ConnectionSpecification>> proxyConnectors =
                        (Map<Connector, List<ConnectionSpecification>>) results.get( "proxyConnectors" );
                reconnectExternalFlows( proxyConnectors, true );
                // Do nothing with idMap also in results.
            } catch ( ClassNotFoundException e ) {
                throw new IOException( "Failed to import model.", e );
            }
        }

        @Override
        public void importPlanCommunity( FileInputStream stream ) throws IOException {
            ObjectInputStream in = xstream.createObjectInputStream( stream );
            try {
                in.readObject();
                // Do nothing with idMap also in results.
            } catch ( ClassNotFoundException e ) {
                throw new IOException( "Failed to import plan community.", e );
            }
        }

        @Override
        public void export( OutputStream stream ) throws IOException {
            ObjectOutputStream out = xstream.createObjectOutputStream( stream, "export" );
            out.writeObject( getModelObjectDao().getModelObjectContext() );
            out.close();
        }

        @Override
        public void export( Segment segment, OutputStream stream ) throws IOException {
            ObjectOutputStream out = xstream.createObjectOutputStream( stream, "export" );
            out.writeObject( "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" );
            out.writeObject( segment );
            out.close();
        }

        @Override
        public Segment importSegment( InputStream stream ) throws IOException {
            ObjectInputStream in = xstream.createObjectInputStream( stream );
            try {
                Map<String, Object> results = (Map<String, Object>) in.readObject();
                reconnectExternalFlows(
                        (Map<Connector, List<ConnectionSpecification>>) results.get( "proxyConnectors" ),
                        false );
                return (Segment) results.get( "segment" );

            } catch ( ClassNotFoundException e ) {
                throw new IOException( "Failed to import segment", e );
            }
        }

        private void reconnectExternalFlows(
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
         *
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
                externalFlow = (ExternalFlow) ( conSpec.isSource() ?
                        getModelDao().connect( externalConnector, part, innerName, id )
                        : getModelDao().connect( part, externalConnector, innerName, id ) );
            } else {
                externalFlow = (ExternalFlow) ( conSpec.isSource() ?
                        getModelDao().connect( externalConnector, part, innerName, null )
                        : getModelDao().connect( part, externalConnector, innerName, null ) );
            }
            copy( localInnerFlow, externalFlow );
            List<String> restrictions = conSpec.getRestrictions();
            for ( String restriction : restrictions ) {
                externalFlow.addRestriction( Flow.Restriction.valueOf( restriction ) );
            }
            externalFlow.setAssetConnections( new AssetConnections( conSpec.getAssetConnections() ) );
            externalFlow.setReceiptConfirmationRequested( conSpec.isReceiptConfirmationRequested() );
            externalFlow.setCanBypassIntermediate( conSpec.isCanBypassIntermediate() );
            getModelDao().disconnect( localInnerFlow );
        }

        private void copy( Flow inner, ExternalFlow external ) {
            external.setChannels( new ArrayList<Channel> ( inner.getChannels() ) );
            external.setMaxDelay( inner.getMaxDelay() );
            external.setSignificanceToSource( inner.getSignificanceToSource() );
            external.setSignificanceToTarget( inner.getSignificanceToTarget() );
            external.setAll( inner.isAll() );
            // external.setAskedFor( inner.isAskedFor() );
            // external.setEois( inner.copyEois() );
            external.setWaivedIssueDetections( new ArrayList<String>( inner.getWaivedIssueDetections() ) );
            external.setAttachments( new ArrayList<Attachment>( inner.getAttachments() ) );
            external.setIntent( inner.getIntent() );
            external.setReferencesEventPhase( inner.isReferencesEventPhase() );
            external.setIfTaskFails( inner.isIfTaskFails() );
            external.setRestrictions( new ArrayList<Flow.Restriction>( inner.getRestrictions() ) );
            external.setCanBypassIntermediate( inner.isCanBypassIntermediate() );
            external.setReceiptConfirmationRequested( inner.isReceiptConfirmationRequested() );
            external.setAssetConnections( new AssetConnections( inner.getAssetConnections() ) );
        }

        @SuppressWarnings("unchecked")
        private List<Connector> findMatchingConnectors( final ConnectionSpecification conSpec ) {
            List<Connector> connectors = new ArrayList<Connector>();
            List<Segment> segments = ConverterUtils.findMatchingSegments( getModelDao(), conSpec.getSegmentSpecification() );
            for ( Segment segment : segments ) {
                Iterator<Connector> iterator =
                        (Iterator<Connector>) new FilterIterator( segment.nodes(), new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                Node node = (Node) object;
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
            Part part = (Part) ( conSpec.isSource() ?
                    externalInnerFlow.getSource()
                    : externalInnerFlow.getTarget() );
            Long partIdValue = Long.parseLong( conSpec.getPartSpecification().getId() );
            boolean partIdMatches = partIdValue != null && partIdValue == part.getId();
            return Matcher.same( externalInnerFlow.getName(), conSpec.getFlowName() )
                    && ( partIdMatches
                    || ConverterUtils.partMatches( part, conSpec.getPartSpecification() ) );
        }

        @Override
        public MarkupType getMimeType() {
            return XmlStreamer.this.getMimeType();
        }

        @Override
        public String getVersion() {
            return XmlStreamer.this.getVersion();
        }
    }

}
