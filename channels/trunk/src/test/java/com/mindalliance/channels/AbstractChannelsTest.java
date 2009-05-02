package com.mindalliance.channels;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DefaultAnalyst;
import com.mindalliance.channels.analysis.IssueDetector;
import com.mindalliance.channels.analysis.detectors.AutoStartPartAlsoTriggered;
import com.mindalliance.channels.analysis.detectors.CyclicTriggering;
import com.mindalliance.channels.analysis.detectors.FlowToSelf;
import com.mindalliance.channels.analysis.detectors.FlowViolatesPolicy;
import com.mindalliance.channels.analysis.detectors.FlowWithUndefinedSource;
import com.mindalliance.channels.analysis.detectors.FlowWithUndefinedTarget;
import com.mindalliance.channels.analysis.detectors.FlowWithoutChannel;
import com.mindalliance.channels.analysis.detectors.FromUser;
import com.mindalliance.channels.analysis.detectors.InitiatedScenarioNeverStarted;
import com.mindalliance.channels.analysis.detectors.InvalidChannel;
import com.mindalliance.channels.analysis.detectors.NeverTriggeredSpecifiedTask;
import com.mindalliance.channels.analysis.detectors.NoRedundancy;
import com.mindalliance.channels.analysis.detectors.NonIncidentScenarioNeverInitiated;
import com.mindalliance.channels.analysis.detectors.OrphanedPart;
import com.mindalliance.channels.analysis.detectors.PartWithInvalidTiming;
import com.mindalliance.channels.analysis.detectors.PartWithRoleWithNoKnownActor;
import com.mindalliance.channels.analysis.detectors.PartWithoutRole;
import com.mindalliance.channels.analysis.detectors.PotentialDeadlock;
import com.mindalliance.channels.analysis.detectors.RedundantFlow;
import com.mindalliance.channels.analysis.detectors.RedundantPart;
import com.mindalliance.channels.analysis.detectors.ScenarioNeverTerminates;
import com.mindalliance.channels.analysis.detectors.StartedOrTerminatedPartWithoutTask;
import com.mindalliance.channels.analysis.detectors.TriggeredButNeverStartedDefinedTask;
import com.mindalliance.channels.analysis.detectors.UnconfirmedJob;
import com.mindalliance.channels.analysis.detectors.UnconnectedConnector;
import com.mindalliance.channels.analysis.detectors.UnnamedFlow;
import com.mindalliance.channels.attachments.FileBasedManager;
import com.mindalliance.channels.command.DefaultCommander;
import com.mindalliance.channels.command.DefaultLockManager;
import com.mindalliance.channels.dao.Memory;
import com.mindalliance.channels.export.xml.XmlStreamer;
import com.mindalliance.channels.graph.DefaultDiagramFactory;
import com.mindalliance.channels.graph.GraphvizRenderer;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.query.DefaultQueryService;
import junit.framework.TestCase;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.WicketTester;

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
    protected void setUp() {
        XmlStreamer xmlStreamer = new XmlStreamer();
        app = new Channels();

        Memory dao = new Memory();
        dao.setChannels( app );
        dao.setDataDirectoryPath( "target/data" );
        dao.setSnapshotThreshold( 10 );
        dao.reset();
        DefaultQueryService queryService = new DefaultQueryService( );
        app.setQueryService( queryService );
        queryService.setDao( dao );
        app.setExporter( xmlStreamer );
        app.setImporter( xmlStreamer );
        commander = new DefaultCommander();
        lockManager = new DefaultLockManager();
        lockManager.setQueryService( queryService );
        commander.setLockManager( lockManager );
        commander.setQueryService( queryService );
        FileBasedManager attachmentManager = new FileBasedManager();
        /*  <bean id="attachmentManager" class="com.mindalliance.channels.attachments.FileBasedManager">
            <property name="directory" value="target/channels-1.0-SNAPSHOT/uploads"/>
            <property name="path" value="uploads"/>
        </bean>*/
        attachmentManager.setDirectory( new File( "target/channels-1.0-SNAPSHOT/uploads" ) );
        attachmentManager.setPath( "uploads" );
        app.setAttachmentManager( attachmentManager );
        // Set default scenario
        // app.getScenarioDao().addScenario(new FireScenario());
        // Set flow diagram
        GraphvizRenderer<Node, Flow> graphRenderer = new GraphvizRenderer<Node, Flow>();
        graphRenderer.setDotPath( "/usr/bin" );
        graphRenderer.setAlgo( "dot" );
        graphRenderer.setTimeout( 5000 );
        DefaultDiagramFactory<Node, Flow> diagramFactory = new DefaultDiagramFactory<Node, Flow>();
        diagramFactory.setGraphRenderer( graphRenderer );
        diagramFactory.setImageDirectory( "src/site/resources/images" );
        diagramFactory.setQueryService( queryService );
        app.setDiagramFactory( diagramFactory );
        // Set scenario analyst
        // Initialize analyst
        DefaultAnalyst analyst = new DefaultAnalyst();
        List<IssueDetector> detectors = new ArrayList<IssueDetector>();
        detectors.add( new FromUser() );
        detectors.add( new FlowWithoutChannel() );
        detectors.add( new InvalidChannel() );
        detectors.add( new StartedOrTerminatedPartWithoutTask() );
        detectors.add( new PartWithoutRole() );
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
        detectors.add( new ScenarioNeverTerminates() );
        detectors.add( new TriggeredButNeverStartedDefinedTask() );
        detectors.add( new NeverTriggeredSpecifiedTask() );
        detectors.add( new AutoStartPartAlsoTriggered() );
        detectors.add( new NonIncidentScenarioNeverInitiated() );
        detectors.add( new InitiatedScenarioNeverStarted() );
        detectors.add( new CyclicTriggering() );
        detectors.add( new PotentialDeadlock() );
        detectors.add( new FlowViolatesPolicy() );
        detectors.add( new UnconfirmedJob() );
        for (IssueDetector detector : detectors ) {
            ((AbstractIssueDetector)detector).setQueryService( queryService );
        }
        analyst.setIssueDetectors( detectors );
        app.setAnalyst( analyst );

        tester = new WicketTester( app );
        tester.setParametersForNextRequest( new HashMap<String, String[]>() );
        Plan plan = new Plan();
        plan.setName( "Test" );
        plan.setClient( "Mind-Alliance" );
        plan.setDescription( "This is a test" );
        app.getPlans().add( plan );
        app.beginUsingPlan( plan );
        queryService.createScenario();
    }

    public void testNothing() {
        assertTrue( true );
    }

}
