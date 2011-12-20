package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.AssignedLocation;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.components.segment.ExpandedPartPanel;
import org.apache.wicket.model.Model;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Test behavoir of a part panel.
 */
@SuppressWarnings( {"HardCodedStringLiteral", "OverlyLongMethod"} )
public class TestPartPanel extends AbstractChannelsTest {

    private ExpandedPartPanel panel;
    private Part part;
    private Segment segment;

    @Override
    public void setUp() throws IOException {
        super.setUp();

        // Find first part in segment
        segment = queryService.getDefaultSegment();
        Iterator<Node> nodes = segment.nodes();
        part = null;
        while ( part == null && nodes.hasNext() ) {
            Node n = nodes.next();
            if ( n.isPart() )
                part = (Part) n;
        }

        PlanPage planPage = new PlanPage( segment, part );
        panel = new ExpandedPartPanel( "id", new Model<Part>( part ), new HashSet<Long>(), planPage );
        tester.startComponent( panel );
    }

    @Test
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
        assertEquals( s1, part.getJurisdiction().getName() );

        panel.setJurisdiction( s );
        assertEquals( s, part.getJurisdiction().getName() );

        panel.setJurisdiction( null );
        assertNull( part.getJurisdiction() );

        panel.setJurisdiction( "" );
        assertNull( part.getJurisdiction() );

        panel.setJurisdiction( "    " );
        assertNull( part.getJurisdiction() );
    }

    @Test
    public void testLocation() {
        AssignedLocation assignedLocation = new AssignedLocation();
        assignedLocation.setNamedPlace( new Place( ) );
        part.setLocation( assignedLocation );
        String s = "A";
        part.getKnownLocation().setName( s );
        assertSame( s, panel.getLocationName() );
        AssignedLocation assignedLocation2 = new AssignedLocation();
        assignedLocation2.setNamedPlace( new Place( s ) );
        part.setLocation( assignedLocation2 );
        assertSame( s, panel.getLocationName() );

        part.setLocation( null );
        String actual = panel.getLocationName();
        assertEquals( "", actual );

 /*       String s1 = "B";
        panel.setLocationName( s1 );
        assertNotSame( actual, part.getLocation() );
        assertEquals( s1, part.getLocation().getName() );

        panel.setLocation( s );
        assertEquals( s, part.getLocation().getName() );

        panel.setLocation( null );
        assertNull( part.getLocation() );

        panel.setLocation( "" );
        assertNull( part.getLocation() );

        panel.setLocation( "    " );
        assertNull( part.getLocation() );
*/    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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
     * @throws IOException if fails
     */
/*    @Test
    public void testForm() throws IOException {
        PlanPage page = new PlanPage( segment, part );
        tester.startPage( page );
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        FormTester ft = tester.newFormTester( "big-form" );
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
        assertEquals( "Bob", part.getActor().getFlowName() );
        assertNotNull( part.getRole() );
        assertEquals( "Dispatcher", part.getRole().getFlowName() );
        assertNotNull( part.getJurisdiction() );
        assertEquals( "World", part.getJurisdiction().getFlowName() );
        assertNotNull( part.getLocation() );
        assertEquals( "Somewhere", part.getLocation().getFlowName() );
    }*/
}
