package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.attachments.BitBucket;
import com.mindalliance.channels.service.ChannelsServiceImpl;
import com.mindalliance.channels.dao.Memory;
import com.mindalliance.channels.graph.FlowDiagram;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.ScenarioPage;
import com.mindalliance.channels.pages.TestScenarioPage;
import junit.framework.TestCase;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Test behavoir of a part panel.
 */
@SuppressWarnings( { "HardCodedStringLiteral", "OverlyLongMethod" } )
public class TestPartPanel extends TestCase {

    private PartPanel panel;
    private Part part;
    private WicketTester tester;
    private Scenario scenario;
    private Project project;

    public TestPartPanel() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        project = new Project();
        final ChannelsServiceImpl service = new ChannelsServiceImpl();
        service.setAddingSamples( true );
        service.setDao( new Memory() );

        project.setService( service );
        project.setAttachmentManager( new BitBucket() );
        final FlowDiagram fd = createMock( FlowDiagram.class );
        expect( fd.getImageMap( (Scenario) anyObject(), (Analyst) anyObject() ) )
                .andReturn( "" ).anyTimes();
        replay( fd );
        project.setFlowDiagram( fd );

        final Analyst sa = createNiceMock( Analyst.class );
        expect( sa.getIssuesSummary( (ModelObject) anyObject(), anyBoolean()) ).andReturn( "" ).anyTimes();
        expect( sa.getIssuesSummary( (ModelObject) anyObject(), (String) anyObject() ) )
                .andReturn( "" ).anyTimes();
        expect( sa.findIssues( (ModelObject) anyObject(), anyBoolean() ))
                .andReturn( new ArrayList<Issue>().iterator() ).anyTimes();
        replay( sa );
        project.setAnalyst( sa );

        tester = new WicketTester( project );
        tester.setParametersForNextRequest( new HashMap<String,String[]>() );

        // Find first part in scenario
        scenario = project.getService().getDefaultScenario();
        final Iterator<Node> nodes = scenario.nodes();
        part = null;
        while ( part == null && nodes.hasNext() ) {
            Node n = nodes.next();
            if ( n.isPart() )
                part = (Part) n;
        }

        panel = new PartPanel( "id", part );
        tester.startComponent( panel );
    }

    public void testJurisdiction() {
        part.setJurisdiction( new Place() );
        final String s = "A";
        part.getJurisdiction().setName( s );
        assertSame( s, panel.getJurisdiction() );

        part.setJurisdiction( new Place( s ) );
        assertSame( s, panel.getJurisdiction() );

        part.setJurisdiction( null );
        final String actual = panel.getJurisdiction();
        assertEquals( "", actual );

        final String s1 = "B";
        panel.setJurisdiction( s1 );
        assertNotSame( actual, part.getJurisdiction() );
        assertSame( s1, part.getJurisdiction().getName() );

        panel.setJurisdiction( s );
        assertSame( s, part.getJurisdiction().getName() );

        panel.setJurisdiction( null );
        assertNull( part.getJurisdiction() );

        panel.setJurisdiction( "" );
        assertNull( part.getJurisdiction() );

        panel.setJurisdiction( "    " );
        assertNull( part.getJurisdiction() );
    }

    public void testLocation() {
        part.setLocation( new Place() );
        final String s = "A";
        part.getLocation().setName( s );
        assertSame( s, panel.getLocation() );

        part.setLocation( new Place( s ) );
        assertSame( s, panel.getLocation() );

        part.setLocation( null );
        final String actual = panel.getLocation();
        assertEquals( "", actual );

        final String s1 = "B";
        panel.setLocation( s1 );
        assertNotSame( actual, part.getLocation() );
        assertSame( s1, part.getLocation().getName() );

        panel.setLocation( s );
        assertSame( s, part.getLocation().getName() );

        panel.setLocation( null );
        assertNull( part.getLocation() );

        panel.setLocation( "" );
        assertNull( part.getLocation() );

        panel.setLocation( "    " );
        assertNull( part.getLocation() );
    }

    public void testActor() {
        part.setActor( new Actor() );
        final String s = "A";
        part.getActor().setName( s );
        assertSame( s, panel.getActor() );

        part.setActor( new Actor( s ) );
        assertSame( s, panel.getActor() );

        part.setActor( null );
        final String actual = panel.getActor();
        assertEquals( "", actual );

        final String s1 = "B";
        panel.setActor( s1 );
        assertNotSame( actual, part.getActor() );
        assertSame( s1, part.getActor().getName() );

        panel.setActor( s );
        assertSame( s, part.getActor().getName() );

        panel.setActor( null );
        assertNull( part.getActor() );

        panel.setActor( "" );
        assertNull( part.getActor() );

        panel.setActor( "    " );
        assertNull( part.getActor() );
    }

    public void testOrganization() {
        part.setOrganization( new Organization() );
        final String s = "A";
        part.getOrganization().setName( s );
        assertSame( s, panel.getOrganization() );

        part.setOrganization( new Organization( s ) );
        assertSame( s, panel.getOrganization() );

        part.setOrganization( null );
        final String actual = panel.getOrganization();
        assertEquals( "", actual );

        final String s1 = "B";
        panel.setOrganization( s1 );
        assertNotSame( actual, part.getOrganization() );
        assertSame( s1, part.getOrganization().getName() );

        panel.setOrganization( s );
        assertSame( s, part.getOrganization().getName() );

        panel.setOrganization( null );
        assertNull( part.getOrganization() );

        panel.setOrganization( "" );
        assertNull( part.getOrganization() );

        panel.setOrganization( "    " );
        assertNull( part.getOrganization() );
    }

    public void testRole() {
        part.setRole( new Role() );
        final String s = "A";
        part.getRole().setName( s );
        assertSame( s, panel.getRole() );

        part.setRole( new Role( s ) );
        assertSame( s, panel.getRole() );

        part.setRole( null );
        final String actual = panel.getRole();
        assertEquals( "", actual );

        final String s1 = "B";
        panel.setRole( s1 );
        assertNotSame( actual, part.getRole() );
        assertSame( s1, part.getRole().getName() );

        panel.setRole( s );
        assertSame( s, part.getRole().getName() );

        panel.setRole( null );
        assertNull( part.getRole() );

        panel.setRole( "" );
        assertNull( part.getRole() );

        panel.setRole( "    " );
        assertNull( part.getRole() );
    }

    public void testTask() {
        final String s = "A";
        panel.setTask( s );
        assertSame( s, part.getTask() );

        final String s1 = "B";
        part.setTask( s1 );
        assertSame( s1, panel.getTask() );
    }

    /**
     * Test all fields in the form using page tester.
     */
    public void testForm() throws IOException {
        ScenarioPage page = new ScenarioPage( scenario, part );
        tester.startPage( page );
        tester.assertRenderedPage( ScenarioPage.class );
        tester.assertNoErrorMessage();

        final FormTester ft = tester.newFormTester( "big-form" );
        TestScenarioPage.setFiles( ft, project );
        ft.setValue( "description", "some description" );
        ft.setValue( "specialty:task", "multitasking" );
        ft.setValue( "specialty:actor", "Bob" );
        ft.setValue( "specialty:role", "Dispatcher" );
        ft.setValue( "specialty:jurisdiction", "World" );
        ft.setValue( "specialty:location", "Somewhere" );

        ft.submit();
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        assertEquals( "some description", part.getDescription() );
        assertEquals( "multitasking", part.getTask() );
        assertNotNull( part.getActor() );
        assertEquals( "Bob", part.getActor().getName() );
        assertNotNull( part.getRole() );
        assertEquals( "Dispatcher", part.getRole().getName() );
        assertNotNull( part.getJurisdiction() );
        assertEquals( "World", part.getJurisdiction().getName() );
        assertNotNull( part.getLocation() );
        assertEquals( "Somewhere", part.getLocation().getName() );
        TestScenarioPage.checkFiles( project );
    }
}
