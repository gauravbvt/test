package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.DiagramFactory;
import com.mindalliance.channels.attachments.BitBucket;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.SimpleIdGenerator;
import com.mindalliance.channels.export.DummyExporter;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.TestSegmentPage;
import com.mindalliance.channels.query.DefaultQueryService;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Test behavoir of a part panel.
 */
@SuppressWarnings( {"HardCodedStringLiteral", "OverlyLongMethod"} )
public class TestPartPanel extends AbstractChannelsTest {

    private ExpandedPartPanel panel;
    private Part part;
    private WicketTester tester;
    private Segment segment;
    private Channels channelsApp;

    public TestPartPanel() {
    }

    @Override
    protected void setUp() throws IOException {
        super.setUp();
        channelsApp = new Channels();
        PlanManager planManager = new PlanManager( new DummyExporter(), new SimpleIdGenerator() );
        planManager.afterPropertiesSet();
        DefaultQueryService queryService = new DefaultQueryService( planManager, new BitBucket() );

//        queryService.setAddingSamples( true );
        channelsApp.setQueryService( queryService );
        DiagramFactory dm = createMock( DiagramFactory.class );
        Diagram fd = createMock( Diagram.class );
        expect( fd.makeImageMap() ).andReturn( "" ).anyTimes();
        expect( dm.newFlowMapDiagram( (Segment) anyObject(), (Node) EasyMock.isNull(), (double[]) EasyMock.isNull(), (String) EasyMock.isNull() ) )
                .andReturn( fd ).anyTimes();
        replay( dm );
        replay( fd );
        channelsApp.setDiagramFactory( dm );

        Analyst sa = createNiceMock( Analyst.class );
        expect( sa.getIssuesSummary( (ModelObject) anyObject(), anyBoolean() ) ).andReturn( "" ).anyTimes();
        expect( sa.getIssuesSummary( (ModelObject) anyObject(), (String) anyObject() ) )
                .andReturn( "" ).anyTimes();
        expect( sa.listIssues( (ModelObject) anyObject(), anyBoolean() ).iterator() )
                .andReturn( new ArrayList<Issue>().iterator() ).anyTimes();
        replay( sa );
        channelsApp.setAnalyst( sa );

        tester = new WicketTester( channelsApp );
        tester.setParametersForNextRequest( new HashMap<String, String[]>() );

        // Find first part in segment
        segment = channelsApp.getQueryService().getDefaultSegment();
        Iterator<Node> nodes = segment.nodes();
        part = null;
        while ( part == null && nodes.hasNext() ) {
            Node n = nodes.next();
            if ( n.isPart() )
                part = (Part) n;
        }

        panel = new ExpandedPartPanel( "id", new Model<Part>( part ), new HashSet<Long>() );
        tester.startComponent( panel );
    }

    public void testJurisdiction() {
        part.setJurisdiction( new Place() );
        String s = "A";
        part.getJurisdiction().setName( s );
        assertSame( s, panel.getJurisdiction() );

        part.setJurisdiction( new Place( s ) );
        assertSame( s, panel.getJurisdiction() );

        part.setJurisdiction( null );
        String actual = panel.getJurisdiction();
        assertEquals( "", actual );

        String s1 = "B";
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
        String s = "A";
        part.getLocation().setName( s );
        assertSame( s, panel.getLocation() );

        part.setLocation( new Place( s ) );
        assertSame( s, panel.getLocation() );

        part.setLocation( null );
        String actual = panel.getLocation();
        assertEquals( "", actual );

        String s1 = "B";
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
        String s = "A";
        part.getActor().setName( s );
        assertSame( s, panel.getActor() );

        part.setActor( new Actor( s ) );
        assertSame( s, panel.getActor() );

        part.setActor( null );
        String actual = panel.getActor();
        assertEquals( "", actual );

        String s1 = "B";
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
        String s = "A";
        part.getOrganization().setName( s );
        assertSame( s, panel.getOrganization() );

        part.setOrganization( new Organization( s ) );
        assertSame( s, panel.getOrganization() );

        part.setOrganization( null );
        String actual = panel.getOrganization();
        assertEquals( "", actual );

        String s1 = "B";
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
        String s = "A";
        part.getRole().setName( s );
        assertSame( s, panel.getRole() );

        part.setRole( new Role( s ) );
        assertSame( s, panel.getRole() );

        part.setRole( null );
        String actual = panel.getRole();
        assertEquals( "", actual );

        String s1 = "B";
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
        String s = "A";
        panel.setTask( s );
        assertSame( s, part.getTask() );

        String s1 = "B";
        part.setTask( s1 );
        assertSame( s1, panel.getTask() );
    }

    /**
     * Test all fields in the form using page tester.
     *
     * @throws java.io.IOException if fails
     */
    public void testForm() throws IOException {
        PlanPage page = new PlanPage( segment, part );
        tester.startPage( page );
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        FormTester ft = tester.newFormTester( "big-form" );
        TestSegmentPage.setFiles( ft, channelsApp );
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
        TestSegmentPage.checkFiles( channelsApp );
    }
}
