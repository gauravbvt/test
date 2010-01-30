package com.mindalliance.channels;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DefaultAnalyst;
import com.mindalliance.channels.analysis.DefaultDetective;
import com.mindalliance.channels.analysis.IssueDetector;
import com.mindalliance.channels.analysis.detectors.*;
import com.mindalliance.channels.attachments.FileBasedManager;
import com.mindalliance.channels.command.DefaultCommander;
import com.mindalliance.channels.command.DefaultLockManager;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.SimpleIdGenerator;
import com.mindalliance.channels.export.xml.XmlStreamer;
import com.mindalliance.channels.graph.DefaultDiagramFactory;
import com.mindalliance.channels.graph.GraphvizRenderer;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.query.DefaultQueryService;
import junit.framework.TestCase;
import org.apache.wicket.util.tester.WicketTester;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 3, 2008
 * Time: 8:24:56 PM
 */
public class AbstractChannelsTest extends TestCase {

    protected Channels app;
    protected Plan plan;
    protected DefaultCommander commander;
    protected DefaultLockManager lockManager;
    protected DefaultQueryService queryService;
    protected WicketTester tester;

    @Override
    protected void setUp() throws IOException {
        SimpleIdGenerator simpleIdGenerator = new SimpleIdGenerator();

        XmlStreamer xmlStreamer = new XmlStreamer( simpleIdGenerator );

        commander = new DefaultCommander();
        PlanManager planManager = new PlanManager( xmlStreamer, simpleIdGenerator );
        planManager.setDataDirectory( new FileSystemResource( "target/data" ) );
        planManager.setSnapshotThreshold( 10 );

        FileBasedManager attachmentManager = new FileBasedManager();
//        attachmentManager.setDirectory( new FileSystemResource( "work/uploads" ) );
        attachmentManager.setUploadPath( "uploads" );

        queryService = new DefaultQueryService( planManager, attachmentManager );

        lockManager = new DefaultLockManager();
        lockManager.setQueryService( queryService );

        commander.setLockManager( lockManager );
        commander.setQueryService( queryService );

        // Set default segment
        // Set flow diagram
        GraphvizRenderer<Node, Flow> graphRenderer = new GraphvizRenderer<Node, Flow>();
        graphRenderer.setDotPath( "/usr/bin" );
        graphRenderer.setAlgo( "dot" );
        graphRenderer.setTimeout( 5000 );
        DefaultDiagramFactory<Node, Flow> diagramFactory = new DefaultDiagramFactory<Node, Flow>();
        diagramFactory.setGraphRenderer( graphRenderer );
        diagramFactory.setImageDirectory( new FileSystemResource( "src/webapp/WEB-INF/images" ) );
        diagramFactory.setQueryService( queryService );

        // Set analyst
        // Initialize analyst
        DefaultAnalyst analyst = new DefaultAnalyst();
        DefaultDetective detective = new DefaultDetective();
        List<IssueDetector> detectors = createDetectors( queryService );
        detective.setIssueDetectors( detectors );
        analyst.setDetective( detective );
        app = new Channels();
        app.setQueryService( queryService );
        app.setImportExportFactory( xmlStreamer );
        app.setDiagramFactory( diagramFactory );
        app.setAnalyst( analyst );

        plan = new Plan();
        plan.setName( "Test" );
        plan.setClient( "Mind-Alliance" );
        plan.setDescription( "This is a test" );

        User test = User.current();
        test.setPlan( plan );
        planManager.add( plan );
        planManager.afterPropertiesSet();
        queryService.afterPropertiesSet();
        planManager.reset();
        commander.afterPropertiesSet();

        queryService.createSegment();
    }

    protected void initTester() {
        tester = new WicketTester( app );
        tester.setParametersForNextRequest( new HashMap<String, String[]>() );
    }

    private List<IssueDetector> createDetectors( DefaultQueryService queryService ) {
        List<IssueDetector> detectors = new ArrayList<IssueDetector>();
        detectors.add( new NoSegmentRepondsToIncident() );
        detectors.add( new SegmentWithoutManagedRisk() );
        detectors.add( new FromUser() );
        detectors.add( new FlowWithoutChannel() );
        detectors.add( new InvalidChannel() );
        detectors.add( new StartedOrTerminatedPartWithoutTask() );
        detectors.add( new PartWithoutRole() );
        detectors.add( new PartWithActorOrRoleButNoOrganization() );
        detectors.add( new PartWithInvalidTiming() );
        detectors.add( new UnnamedFlow() );
        detectors.add( new FlowWithUndefinedSource() );
        detectors.add( new FlowWithUndefinedTarget() );
        detectors.add( new OrphanedPart() );
        detectors.add( new RedundantPart() );
        detectors.add( new RedundantFlow() );
        detectors.add( new UnconnectedConnector() );
        detectors.add( new NoRedundancy() );
        detectors.add( new FlowToSelf() );
        detectors.add( new PartWithRoleWithNoKnownActor() );
        detectors.add( new SegmentNeverEnds() );
        detectors.add( new TriggeredButNeverStartedDefinedTask() );
        detectors.add( new NeverTriggeredSpecifiedTask() );
        detectors.add( new AutoStartPartAlsoTriggered() );
        detectors.add( new SegmentEventNeverCaused() );
        detectors.add( new SegmentNeverStarts() );
        detectors.add( new CyclicTriggering() );
        detectors.add( new PotentialDeadlock() );
        detectors.add( new FlowViolatesPolicy() );
        detectors.add( new UnconfirmedJob() );
        detectors.add( new ActorNotInOneOrganization() );
        detectors.add( new SegmentWithSameRisk() );
        detectors.add( new UselessPart() );
        detectors.add( new GeonameButNoLocation() );
        detectors.add( new UnverifiedPostalCode() );
        detectors.add( new PlaceContainedInSelf() );
        detectors.add( new PlaceInheritsDifferentStreetAddress() );
        detectors.add( new PlaceInheritsDifferentPostalCode() );
        detectors.add( new RedundantPlace() );
        detectors.add( new EmptyNeedOrCapability() );
        detectors.add( new UnsatisfiedNeed() );
        detectors.add( new PersonInSystemRole() );
        detectors.add( new CommitmentWithoutRequiredAgreement() );
        detectors.add( new UntimelyTriggeringSharing() );
        detectors.add( new InconsistentImpactOnSourcePart() );
        detectors.add( new InconsistentImpactOnTargetPart() );
        detectors.add( new UntimelyCriticalSharing() );
        detectors.add( new SinglePointOfFailure() );
        detectors.add( new UserIsManyActors() );
        detectors.add( new NoSegmentForEventPhase() );
        detectors.add( new InvalidEntityTyping() );
        detectors.add( new FlowDeclassifies() );
        detectors.add( new UselessActor() );
        detectors.add( new UnplayedPart() );
        detectors.add( new SharingWithoutCommitments() );
        detectors.add( new InsufficientClearance() );
        detectors.add( new AgreementEncompassedByOther() );
        detectors.add( new AgreementWithoutCommitment() );
        detectors.add( new CommittmentWithoutRequiredUnicastChannel() );
        detectors.add( new CommittmentOverUnsecuredChannel() );
        for ( IssueDetector detector : detectors ) {
            ( (AbstractIssueDetector) detector ).setQueryService( queryService );
        }
        return detectors;
    }

    public void testNothing() {
        assertTrue( true );
    }


}
