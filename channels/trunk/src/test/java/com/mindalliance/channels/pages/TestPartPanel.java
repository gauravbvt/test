package com.mindalliance.channels.pages;

import com.mindalliance.channels.dao.Memory;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Jurisdiction;
import com.mindalliance.channels.model.Location;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Role;
import junit.framework.TestCase;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.WicketTester;

import java.util.Iterator;

/**
 * Test behavoir of a part panel.
 */
public class TestPartPanel extends TestCase {

    private PartPanel panel;
    private Part part;
    private WicketTester tester;

    public TestPartPanel() {
        final Project project = new Project();
        project.setScenarioDao( new Memory() );
        tester = new WicketTester( project );

        // Find first part in scenario
        final Iterator<Node> nodes = project.getScenarioDao().getDefaultScenario().nodes();
        part = null;
        while ( part == null && nodes.hasNext() ) {
            Node n = nodes.next();
            if ( n.isPart() )
                part = (Part) n;
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        panel = new PartPanel( "id", new Model<Part>( part ) );
        tester.startComponent( panel );
    }

    public void testJurisdiction() {
        final String s = "A";
        part.setJurisdiction( new Jurisdiction() );
        part.getJurisdiction().setName( s );
        assertSame( s, panel.getJurisdiction() );

        part.setJurisdiction( new Jurisdiction( s ) );
        assertSame( s, panel.getJurisdiction() );

        part.setJurisdiction( null );
        final String actual = panel.getJurisdiction();
        assertEquals( "", actual );

        final String s1 = "B";
        panel.setJurisdiction( s1 );
        assertNotSame( actual, part.getJurisdiction() );
        assertSame( s1, part.getJurisdiction().getName() );

        panel.setJurisdiction( s );
        assertSame( s1, part.getJurisdiction().getName() );

        panel.setJurisdiction( null );
        assertNull( part.getJurisdiction() );

        panel.setJurisdiction( "" );
        assertNull( part.getJurisdiction() );

        panel.setJurisdiction( "    " );
        assertNull( part.getJurisdiction() );
    }

    public void testLocation() {
        final String s = "A";
        part.setLocation( new Location() );
        part.getLocation().setName( s );
        assertSame( s, panel.getLocation() );

        part.setLocation( new Location( s ) );
        assertSame( s, panel.getLocation() );

        part.setLocation( null );
        final String actual = panel.getLocation();
        assertEquals( "", actual );

        final String s1 = "B";
        panel.setLocation( s1 );
        assertNotSame( actual, part.getLocation() );
        assertSame( s1, part.getLocation().getName() );

        panel.setLocation( s );
        assertSame( s1, part.getLocation().getName() );

        panel.setLocation( null );
        assertNull( part.getLocation() );

        panel.setLocation( "" );
        assertNull( part.getLocation() );

        panel.setLocation( "    " );
        assertNull( part.getLocation() );
    }

    public void testActor() {
        final String s = "A";
        part.setActor( new Actor() );
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
        assertSame( s1, part.getActor().getName() );

        panel.setActor( null );
        assertNull( part.getActor() );

        panel.setActor( "" );
        assertNull( part.getActor() );

        panel.setActor( "    " );
        assertNull( part.getActor() );
    }

    public void testOrganization() {
        final String s = "A";
        part.setOrganization( new Organization() );
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
        assertSame( s1, part.getOrganization().getName() );

        panel.setOrganization( null );
        assertNull( part.getOrganization() );

        panel.setOrganization( "" );
        assertNull( part.getOrganization() );

        panel.setOrganization( "    " );
        assertNull( part.getOrganization() );
    }

    public void testRole() {
        final String s = "A";
        part.setRole( new Role() );
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
        assertSame( s1, part.getRole().getName() );

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
}
