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
import com.mindalliance.channels.analysis.detectors.PartWithoutTask;
import com.mindalliance.channels.analysis.detectors.PotentialDeadlock;
import com.mindalliance.channels.analysis.detectors.UnconnectedConnector;
import com.mindalliance.channels.analysis.detectors.UnnamedFlow;
import com.mindalliance.channels.attachments.FileBasedManager;
import com.mindalliance.channels.service.ChannelsServiceImpl;
import com.mindalliance.channels.dao.Memory;
import com.mindalliance.channels.export.xml.XmlStreamer;
import com.mindalliance.channels.graph.DefaultFlowDiagram;
import com.mindalliance.channels.graph.DefaultGraphBuilder;
import com.mindalliance.channels.graph.GraphvizRenderer;
import com.mindalliance.channels.pages.Project;
import junit.framework.TestCase;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.WicketTester;

import java.util.ArrayList;
import java.util.List;

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

        Dao dao = new Memory();
        ChannelsServiceImpl channels = new ChannelsServiceImpl( dao );

        project.setService( channels );
        project.setExporter( xmlStreamer );
        project.setImporter( xmlStreamer );
        project.setGraphBuilder( new DefaultGraphBuilder() );
        project.setProjectName("Test");
        project.setClient("Mind-Alliance");
        project.setDescription("This is a test project");
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
        DefaultFlowDiagram flowDiagram = new DefaultFlowDiagram();
        flowDiagram.setGraphRenderer( graphRenderer );
        flowDiagram.setUrlFormat( "?scenario={0}&amp;node={1}" );
        flowDiagram.setScenarioUrlFormat( "?scenario={0}" );
        flowDiagram.setImageDirectory( "src/site/resources/images" );
        project.setFlowDiagram( flowDiagram );
        // Set scenario analyst
        // Initialize analyst
        DefaultAnalyst analyst = new DefaultAnalyst();
        List<IssueDetector> detectors = new ArrayList<IssueDetector>();
        detectors.add( new FromUser() );
        detectors.add( new FlowWithoutChannel() );
        detectors.add( new PartWithoutTask() );
        detectors.add( new PartWithoutRole() );
        detectors.add( new UnnamedFlow() );
        detectors.add( new FlowWithUndefinedSource() );
        detectors.add( new FlowWithUndefinedTarget() );
        detectors.add( new OrphanedPart() );
        detectors.add( new UnconnectedConnector() );
        detectors.add( new NoRedundancy() );
        detectors.add( new PotentialDeadlock() );
        detectors.add( new FlowViolatesPolicy() );
        analyst.setIssueDetectors( detectors );
        project.setAnalyst( analyst );
        // Register media
        ChannelsServiceImpl.registerDefaultMedia( channels );
        tester = new WicketTester( project );
    }

    public void testNothing() {
        assertTrue( true );
    }

}
