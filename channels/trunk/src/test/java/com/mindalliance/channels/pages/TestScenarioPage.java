package com.mindalliance.channels.pages;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.ScenarioAnalyst;
import com.mindalliance.channels.dao.Memory;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.graph.FlowDiagram;
import junit.framework.TestCase;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Simple test using the WicketTester.
 */
@SuppressWarnings( { "HardCodedStringLiteral" } )
public class TestScenarioPage extends TestCase {

    private WicketTester tester;
    private Scenario scenario;
    private Memory dao;
    private Project project;

    public TestScenarioPage() {
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        dao = new Memory();
        project = new Project();
        project.setScenarioDao( dao );
        final FlowDiagram<Node,Flow> fd = createMock( FlowDiagram.class );
        expect( fd.getImageMap( (Scenario) anyObject(), (ScenarioAnalyst) anyObject() ) )
                .andReturn( "" ).anyTimes();
        replay( fd );
        project.setFlowDiagram( fd );

        scenario = project.getScenarioDao().getDefaultScenario();
        tester = new WicketTester( project );
    }

    /** Workaround for wicket form tester bug re: file upload. File must be set
     * otherwise all fields set to null. Resolved in wickets 1.4-RC2, coming out
     * soon to a theater near you...
     * @param ft the tester to fix
     * @param project
     * @see {https://issues.apache.org/jira/browse/WICKET-1931}
     * @todo remove when moving to Wickets 1.4-RC2
     */
    public static void setFiles( FormTester ft, Project project ) throws IOException {

        final Importer importer = createMock( Importer.class );
        expect( importer.importScenario( (InputStream) notNull() ) ).andReturn( new Scenario() );

        replay( importer );
        project.setImporter( importer );

        final File file = new File( TestScenarioPage.class.getResource( "test.txt" ).getFile() );
        assertTrue( "Can't find " + file.getAbsolutePath(), file.exists() );
        ft.setFile( "sc-import", file, "text/plain" );
        ft.setFile( "attachments:upload", file, "text/plain" );
    }

    /**
     * @param project
     * @todo remove when moving to Wickets 1.4-RC2
     */
    public static void checkFiles( Project project ) {
        verify( project.getImporter() );
    }

    public void testParms() {
        tester.startPage( ScenarioPage.class );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        final PageParameters parms = new PageParameters();
        parms.put( ScenarioPage.SCENARIO_PARM, scenario.getId() );

        tester.startPage( ScenarioPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( ScenarioPage.SCENARIO_PARM, -1 );
        tester.startPage( ScenarioPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( ScenarioPage.SCENARIO_PARM, "bla" );
        tester.startPage( ScenarioPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( ScenarioPage.SCENARIO_PARM, scenario.getId() );
        parms.put( ScenarioPage.NODE_PARM, -1 );
        tester.startPage( ScenarioPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( ScenarioPage.NODE_PARM, "bla" );
        tester.startPage( ScenarioPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( ScenarioPage.NODE_PARM, scenario.getDefaultNode().getId() );
        tester.startPage( ScenarioPage.class, parms );
        tester.assertRenderedPage( ScenarioPage.class );
        tester.assertNoErrorMessage();

        parms.put( ScenarioPage.EXPAND_PARM, "bla" );
        tester.startPage( ScenarioPage.class, parms );
        tester.assertRenderedPage( ScenarioPage.class );
        tester.assertNoErrorMessage();

        parms.add( ScenarioPage.EXPAND_PARM, "burp" );
        tester.startPage( ScenarioPage.class, parms );
        tester.assertRenderedPage( ScenarioPage.class );
        tester.assertNoErrorMessage();
    }

    /** Test all nodes pages in default scenario. */
    public void testNodes() {
        final Iterator<Node> nodes = scenario.nodes();
        while ( nodes.hasNext() ) {
            tester.startPage( new ScenarioPage( scenario, nodes.next() ) );
            tester.assertRenderedPage( ScenarioPage.class );
            tester.assertNoErrorMessage();
        }
    }

    public void testNewScenario() throws NotFoundException {
        tester.startPage( new ScenarioPage( scenario ) );

        final int size = dao.getScenarioCount();
        tester.clickLink( "big-form:sc-new" );
        assertEquals( size+1, dao.getScenarioCount() );

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        tester.startPage( new ScenarioPage( scenario ) );
        tester.clickLink( "big-form:sc-new" );
        assertEquals( size+2, dao.getScenarioCount() );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();
    }

    /** Test submit with part modifications.
     * @throws NotFoundException on error */
    public void testEmptySubmit() throws NotFoundException, IOException {
        final Node node = scenario.getDefaultNode();

        tester.startPage( new ScenarioPage( scenario, node ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( ScenarioPage.class );
        tester.assertNoErrorMessage();

        final FormTester ft = tester.newFormTester( "big-form" );
        setFiles( ft, project );
        ft.submit();

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();
        checkFiles( project );
    }

    /** Test submit with part modifications.
     * @throws NotFoundException on error */
    public void testDescriptionSubmit1() throws NotFoundException, IOException {
        final Node node = scenario.getDefaultNode();
        node.setDescription( "" );
        assertEquals( "", node.getDescription() );

        tester.startPage( new ScenarioPage( scenario, node ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( ScenarioPage.class );
        tester.assertNoErrorMessage();

        final FormTester ft = tester.newFormTester( "big-form" );
        final String desc = "New value";
        ft.setValue( "description", desc );
        setFiles( ft, project );
        ft.submit();

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        assertEquals( desc, node.getDescription() );
        checkFiles( project );
    }

    /** Test submit with part modifications.
     * @throws NotFoundException on error */
    public void testDescriptionSubmit2() throws NotFoundException, IOException {
        final Node node = scenario.getDefaultNode();
        node.setDescription( "something" );

        tester.startPage( new ScenarioPage( scenario, node ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( ScenarioPage.class );
        tester.assertNoErrorMessage();

        final FormTester ft = tester.newFormTester( "big-form" );
        final String desc = "";
        ft.setValue( "description", desc );
        setFiles( ft, project );
        ft.submit();

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        assertEquals( desc, node.getDescription() );
        checkFiles( project );
    }

    public void testDeleteScenario() throws IOException, NotFoundException {
        final Scenario sc2 = Scenario.createDefault();
        sc2.setName( "Test" );
        dao.addScenario( sc2 );

        tester.startPage( new ScenarioPage( scenario ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( ScenarioPage.class );
        tester.assertNoErrorMessage();
        assertEquals( 2, dao.getScenarioCount() );

        final FormTester ft = tester.newFormTester( "big-form" );
        setFiles( ft, project );
        ft.setValue( "sc-del", "true" );

        ft.submit();
        try {
            assertNull( dao.findScenario( scenario.getId() ) );
            fail();
        } catch ( NotFoundException ignored ) {}
        
        final Scenario defaultScenario = dao.getDefaultScenario();

        tester.startPage( new ScenarioPage( defaultScenario ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( ScenarioPage.class );
        tester.assertNoErrorMessage();
        assertEquals( 1, dao.getScenarioCount() );

        final FormTester ft2 = tester.newFormTester( "big-form" );
        setFiles( ft2, project );
        ft2.setValue( "sc-del", "true" );

        ft2.submit();
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();
        assertSame( defaultScenario, dao.findScenario( defaultScenario.getId() ) );
        assertEquals( 1, dao.getScenarioCount() );
    }

    public void testGetParameters1() {
        final Node node = scenario.getDefaultNode();
        final PageParameters parms = ScenarioPage.getParameters( scenario, node );

        assertEquals( scenario.getId(), (long) parms.getAsLong( "scenario" ) );
        assertEquals( node.getId(), (long) parms.getAsLong( "node" ) );
    }

    public void testGetParameters2() {
        final Node node = scenario.getDefaultNode();

        final Set<Long> expand = new HashSet<Long>( Arrays.asList( 1L, 2L ) );
        final PageParameters parms = ScenarioPage.getParameters( scenario, node, expand );

        assertEquals( scenario.getId(), (long) parms.getAsLong( "scenario" ) );
        assertEquals( node.getId(), (long) parms.getAsLong( "node" ) );

        final Set<String> results = new HashSet<String>(
                Arrays.asList( parms.getStringArray( "expand" ) ) );

        assertEquals( 2, results.size() );
        assertTrue( results.contains( "1" ) );
        assertTrue( results.contains( "2" ) );
    }
}
