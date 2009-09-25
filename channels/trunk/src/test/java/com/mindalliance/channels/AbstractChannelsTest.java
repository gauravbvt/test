package com.mindalliance.channels;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DefaultAnalyst;
import com.mindalliance.channels.analysis.DefaultDetective;
import com.mindalliance.channels.analysis.IssueDetector;
import com.mindalliance.channels.analysis.detectors.ActorNotInOneOrganization;
import com.mindalliance.channels.analysis.detectors.AutoStartPartAlsoTriggered;
import com.mindalliance.channels.analysis.detectors.CommitmentWithoutRequiredAgreement;
import com.mindalliance.channels.analysis.detectors.CyclicTriggering;
import com.mindalliance.channels.analysis.detectors.EmptyNeedOrCapability;
import com.mindalliance.channels.analysis.detectors.FlowToSelf;
import com.mindalliance.channels.analysis.detectors.FlowViolatesPolicy;
import com.mindalliance.channels.analysis.detectors.FlowWithUndefinedSource;
import com.mindalliance.channels.analysis.detectors.FlowWithUndefinedTarget;
import com.mindalliance.channels.analysis.detectors.FlowWithoutChannel;
import com.mindalliance.channels.analysis.detectors.FromUser;
import com.mindalliance.channels.analysis.detectors.GeonameButNoLocation;
import com.mindalliance.channels.analysis.detectors.InconsistentImpactOnSourcePart;
import com.mindalliance.channels.analysis.detectors.InconsistentImpactOnTargetPart;
import com.mindalliance.channels.analysis.detectors.InvalidChannel;
import com.mindalliance.channels.analysis.detectors.NeverTriggeredSpecifiedTask;
import com.mindalliance.channels.analysis.detectors.NoRedundancy;
import com.mindalliance.channels.analysis.detectors.NoScenarioForEventPhase;
import com.mindalliance.channels.analysis.detectors.NoScenarioRepondsToIncident;
import com.mindalliance.channels.analysis.detectors.OrphanedPart;
import com.mindalliance.channels.analysis.detectors.PartWithInvalidTiming;
import com.mindalliance.channels.analysis.detectors.PartWithRoleButNoOrganization;
import com.mindalliance.channels.analysis.detectors.PartWithRoleWithNoKnownActor;
import com.mindalliance.channels.analysis.detectors.PartWithoutRole;
import com.mindalliance.channels.analysis.detectors.PersonInSystemRole;
import com.mindalliance.channels.analysis.detectors.PlaceContainedInSelf;
import com.mindalliance.channels.analysis.detectors.PlaceInheritsDifferentPostalCode;
import com.mindalliance.channels.analysis.detectors.PlaceInheritsDifferentStreetAddress;
import com.mindalliance.channels.analysis.detectors.PotentialDeadlock;
import com.mindalliance.channels.analysis.detectors.RedundantFlow;
import com.mindalliance.channels.analysis.detectors.RedundantPart;
import com.mindalliance.channels.analysis.detectors.RedundantPlace;
import com.mindalliance.channels.analysis.detectors.ScenarioEventNeverCaused;
import com.mindalliance.channels.analysis.detectors.ScenarioNeverEnds;
import com.mindalliance.channels.analysis.detectors.ScenarioNeverStarts;
import com.mindalliance.channels.analysis.detectors.ScenarioWithSameRisk;
import com.mindalliance.channels.analysis.detectors.ScenarioWithoutManagedRisk;
import com.mindalliance.channels.analysis.detectors.SinglePointOfFailure;
import com.mindalliance.channels.analysis.detectors.StartedOrTerminatedPartWithoutTask;
import com.mindalliance.channels.analysis.detectors.TriggeredButNeverStartedDefinedTask;
import com.mindalliance.channels.analysis.detectors.UnconfirmedJob;
import com.mindalliance.channels.analysis.detectors.UnconnectedConnector;
import com.mindalliance.channels.analysis.detectors.UnnamedFlow;
import com.mindalliance.channels.analysis.detectors.UnsatisfiedNeed;
import com.mindalliance.channels.analysis.detectors.UntimelyCriticalCommitment;
import com.mindalliance.channels.analysis.detectors.UntimelyTriggeringCommitment;
import com.mindalliance.channels.analysis.detectors.UnverifiedPostalCode;
import com.mindalliance.channels.analysis.detectors.UselessPart;
import com.mindalliance.channels.analysis.detectors.UserIsManyActors;
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

        DefaultQueryService queryService = new DefaultQueryService( planManager, attachmentManager );

        lockManager = new DefaultLockManager();
        lockManager.setQueryService( queryService );

        commander.setLockManager( lockManager );
        commander.setQueryService( queryService );

        // Set default scenario
        // app.getScenarioDao().addScenario(new FireScenario());
        // Set flow diagram
        GraphvizRenderer<Node, Flow> graphRenderer = new GraphvizRenderer<Node, Flow>();
        graphRenderer.setDotPath( "/usr/bin" );
        graphRenderer.setAlgo( "dot" );
        graphRenderer.setTimeout( 5000 );
        DefaultDiagramFactory<Node, Flow> diagramFactory = new DefaultDiagramFactory<Node, Flow>();
        diagramFactory.setGraphRenderer( graphRenderer );
        diagramFactory.setImageDirectory( new FileSystemResource( "src/webapp/WEB-INF/images" ) );
        diagramFactory.setQueryService( queryService );

        // Set scenario analyst
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

        queryService.createScenario();
    }

    protected void initTester() {
        tester = new WicketTester( app );
        tester.setParametersForNextRequest( new HashMap<String, String[]>() );
    }

    private List<IssueDetector> createDetectors( DefaultQueryService queryService ) {
        List<IssueDetector> detectors = new ArrayList<IssueDetector>();
        detectors.add( new NoScenarioRepondsToIncident() );
        detectors.add( new ScenarioWithoutManagedRisk() );
        detectors.add( new FromUser() );
        detectors.add( new FlowWithoutChannel() );
        detectors.add( new InvalidChannel() );
        detectors.add( new StartedOrTerminatedPartWithoutTask() );
        detectors.add( new PartWithoutRole() );
        detectors.add( new PartWithRoleButNoOrganization() );
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
        detectors.add( new ScenarioNeverEnds() );
        detectors.add( new TriggeredButNeverStartedDefinedTask() );
        detectors.add( new NeverTriggeredSpecifiedTask() );
        detectors.add( new AutoStartPartAlsoTriggered() );
        detectors.add( new ScenarioEventNeverCaused() );
        detectors.add( new ScenarioNeverStarts() );
        detectors.add( new CyclicTriggering() );
        detectors.add( new PotentialDeadlock() );
        detectors.add( new FlowViolatesPolicy() );
        detectors.add( new UnconfirmedJob() );
        detectors.add( new ActorNotInOneOrganization() );
        detectors.add( new ScenarioWithSameRisk() );
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
        detectors.add( new UntimelyTriggeringCommitment() );
        detectors.add( new InconsistentImpactOnSourcePart() );
        detectors.add( new InconsistentImpactOnTargetPart() );
        detectors.add( new UntimelyCriticalCommitment() );
        detectors.add( new SinglePointOfFailure() );
        detectors.add( new UserIsManyActors() );
        detectors.add( new NoScenarioForEventPhase() );
        for ( IssueDetector detector : detectors ) {
            ( (AbstractIssueDetector) detector ).setQueryService( queryService );
        }
        return detectors;
    }

    public void testNothing() {
        assertTrue( true );
    }

}
