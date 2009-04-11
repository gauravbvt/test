package com.mindalliance.channels;

import com.mindalliance.channels.analysis.DefaultAnalyst;
import com.mindalliance.channels.analysis.IssueDetector;
import com.mindalliance.channels.analysis.detectors.FlowViolatesPolicy;
import com.mindalliance.channels.analysis.detectors.FlowWithUndefinedSource;
import com.mindalliance.channels.analysis.detectors.FlowWithUndefinedTarget;
import com.mindalliance.channels.analysis.detectors.FlowWithoutChannel;
import com.mindalliance.channels.analysis.detectors.FromUser;
import com.mindalliance.channels.analysis.detectors.NoRedundancy;
import com.mindalliance.channels.analysis.detectors.OrphanedPart;
import com.mindalliance.channels.analysis.detectors.PartWithoutRole;
import com.mindalliance.channels.analysis.detectors.StartedOrTerminatedPartWithoutTask;
import com.mindalliance.channels.analysis.detectors.PotentialDeadlock;
import com.mindalliance.channels.analysis.detectors.UnconnectedConnector;
import com.mindalliance.channels.analysis.detectors.UnnamedFlow;
import com.mindalliance.channels.analysis.detectors.PartWithInvalidTiming;
import com.mindalliance.channels.analysis.detectors.InvalidChannel;
import com.mindalliance.channels.analysis.detectors.RedundantPart;
import com.mindalliance.channels.analysis.detectors.RedundantFlow;
import com.mindalliance.channels.analysis.detectors.UnconfirmedJob;
import com.mindalliance.channels.analysis.detectors.FlowToSelf;
import com.mindalliance.channels.analysis.detectors.PartWithRoleWithNoKnownActor;
import com.mindalliance.channels.analysis.detectors.TriggeredButNeverStartedDefinedTask;
import com.mindalliance.channels.analysis.detectors.NeverTriggeredSpecifiedTask;
import com.mindalliance.channels.analysis.detectors.AutoStartPartAlsoTriggered;
import com.mindalliance.channels.analysis.detectors.CyclicTriggering;
import com.mindalliance.channels.analysis.detectors.ScenarioNeverTerminates;
import com.mindalliance.channels.attachments.FileBasedManager;
import com.mindalliance.channels.query.DataQueryObjectImpl;
import com.mindalliance.channels.dao.Memory;
import com.mindalliance.channels.export.xml.XmlStreamer;
import com.mindalliance.channels.graph.GraphvizRenderer;
import com.mindalliance.channels.graph.DefaultDiagramFactory;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.command.DefaultCommander;
import com.mindalliance.channels.command.DefaultLockManager;
import junit.framework.TestCase;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.WicketTester;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 3, 2008
 * Time: 8:24:56 PM
 */
public class AbstractChannelsTest extends TestCase {

    protected Project project;
    protected WicketTester tester;

    @Override
    protected void setUp() {
        XmlStreamer xmlStreamer = new XmlStreamer();
        project = new Project();
        project.setUri( "mindalliance.com/channels/demo" );

        Memory dao = new Memory();
        dao.setDataDirectoryPath( "target/data" );
        dao.setSnapshotThreshold( 10 );
        dao.reset();
        DataQueryObjectImpl dqo = new DataQueryObjectImpl( dao );

        project.setDqo( dqo );
        project.setExporter( xmlStreamer );
        project.setImporter( xmlStreamer );
        DefaultCommander commander = new DefaultCommander();
        DefaultLockManager lockManager = new DefaultLockManager();
        lockManager.setDqo( dqo );
        project.setLockManager( lockManager );
        commander.setLockManager( lockManager );
        commander.setDqo( dqo );
        project.setCommander( commander );
        project.setProjectName( "Test" );
        project.setClient( "Mind-Alliance" );
        project.setDescription( "This is a test project" );
        FileBasedManager attachmentManager = new FileBasedManager();
        /*  <bean id="attachmentManager" class="com.mindalliance.channels.attachments.FileBasedManager">
            <property name="directory" value="target/channels-1.0-SNAPSHOT/uploads"/>
            <property name="path" value="uploads"/>
        </bean>*/
        attachmentManager.setDirectory( new File( "target/channels-1.0-SNAPSHOT/uploads" ) );
        attachmentManager.setPath( "uploads" );
        project.setAttachmentManager( attachmentManager );
        // Set default scenario
        // project.getScenarioDao().addScenario(new FireScenario());
        // Set flow diagram
        GraphvizRenderer<Node, Flow> graphRenderer = new GraphvizRenderer<Node, Flow>();
        graphRenderer.setDotPath( "/usr/bin" );
        graphRenderer.setAlgo( "dot" );
        graphRenderer.setTimeout( 5000 );
        DefaultDiagramFactory<Node, Flow> diagramFactory = new DefaultDiagramFactory<Node, Flow>();
        diagramFactory.setGraphRenderer( graphRenderer );
        diagramFactory.setImageDirectory( "src/site/resources/images" );
        diagramFactory.setDqo( dqo );
        project.setDiagramFactory( diagramFactory );
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
        detectors.add( new CyclicTriggering() );
        detectors.add( new PotentialDeadlock() );
        detectors.add( new FlowViolatesPolicy() );
        detectors.add( new UnconfirmedJob() );
        analyst.setIssueDetectors( detectors );
        project.setAnalyst( analyst );

        tester = new WicketTester( project );
        tester.setParametersForNextRequest( new HashMap<String, String[]>() );
    }

    public void testNothing() {
        assertTrue( true );
    }

}
