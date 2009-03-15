package com.mindalliance.channels.pages;

import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.attachments.BitBucket;
import com.mindalliance.channels.dao.Memory;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.FlowDiagram;
import com.mindalliance.channels.service.ChannelsServiceImpl;
import junit.framework.TestCase;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
        ChannelsServiceImpl service = new ChannelsServiceImpl();
        service.setAddingSamples( true );
        service.setDao( dao );
        service.initialize();

        project.setService( service );
        project.setAttachmentManager( new BitBucket() );
        DiagramFactory dm = createMock( DiagramFactory.class );
        FlowDiagram fd = createMock(  FlowDiagram.class);
        expect( fd.makeImageMap( ) ).andReturn( "" ).anyTimes();
        expect( dm.newFlowDiagram( (Scenario) anyObject() ) )
                    .andReturn( fd ).anyTimes();
        replay( dm );
        replay( fd );
        project.setDiagramFactory( dm );

        Analyst sa = createNiceMock( Analyst.class );
        expect( sa.getIssuesSummary( (ModelObject) anyObject(), anyBoolean() ) )
                .andReturn( "" ).anyTimes();
        expect( sa.getIssuesSummary( (ModelObject) anyObject(), (String) anyObject() ) )
                .andReturn( "" ).anyTimes();
        expect( sa.findIssues( (ModelObject) anyObject(), anyBoolean() ))
                .andReturn( new ArrayList<Issue>().iterator() ).anyTimes();
        replay( sa );
        project.setAnalyst( sa );

        scenario = project.getService().getDefaultScenario();
        tester = new WicketTester( project );
        tester.setParametersForNextRequest( new HashMap<String,String[]>() );
    }

    /** Workaround for wicket form tester bug re: file upload. File must be set
     * otherwise all fields set to null. Resolved in wickets 1.4-RC2, coming out
     * soon to a theater near you...
     * @param ft the tester to fix
     * @param project
     * @see {https://issues.apache.org/jira/browse/WICKET-1931}
     * @todo remove when moving to Wickets 1.4-RC2
     * @throws java.io.IOException
     */
    public static void setFiles( FormTester ft, Project project ) throws IOException {

        Importer importer = createMock( Importer.class );
        expect( importer.importScenario( (InputStream) notNull() ) )
                .andReturn( project.getService().createScenario() );

        replay( importer );
        project.setImporter( importer );

        File file = new File( TestScenarioPage.class.getResource( "test.txt" ).getFile() );
        assertTrue( "Can't find " + file.getAbsolutePath(), file.exists() );
        ft.setFile( "sc-import", file, "text/plain" );
        ft.setFile( "attachments:upload", file, "text/plain" );
    }

    /**
     * @param project a Project
     * @todo remove when moving to Wickets 1.4-RC2
     */
    public static void checkFiles( Project project ) {
        verify( project.getImporter() );
    }

    public void testParms() {
        tester.startPage( ProjectPage.class );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        PageParameters parms = new PageParameters();
        parms.put( ProjectPage.SCENARIO_PARM, Long.toString( scenario.getId() ) );

        tester.startPage( ProjectPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( ProjectPage.SCENARIO_PARM, "-1" );
        tester.startPage( ProjectPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( ProjectPage.SCENARIO_PARM, "bla" );
        tester.startPage( ProjectPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( ProjectPage.SCENARIO_PARM, Long.toString( scenario.getId() ) );
        parms.put( ProjectPage.PART_PARM, "-1" );
        tester.startPage( ProjectPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( ProjectPage.PART_PARM, "bla" );
        tester.startPage( ProjectPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( ProjectPage.PART_PARM, Long.toString( scenario.getDefaultPart().getId() ) );
        tester.startPage( ProjectPage.class, parms );
        tester.assertRenderedPage( ProjectPage.class );
        tester.assertNoErrorMessage();

        parms.put( ProjectPage.EXPAND_PARM, "bla" );
        tester.startPage( ProjectPage.class, parms );
        tester.assertRenderedPage( ProjectPage.class );
        tester.assertNoErrorMessage();

        parms.add( ProjectPage.EXPAND_PARM, "burp" );
        tester.startPage( ProjectPage.class, parms );
        tester.assertRenderedPage( ProjectPage.class );
        tester.assertNoErrorMessage();
    }

    /** Test all nodes pages in default scenario. */
    public void testNodes() {
        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            tester.startPage( new ProjectPage( scenario, parts.next() ) );
            tester.assertRenderedPage( ProjectPage.class );
            tester.assertNoErrorMessage();
        }
    }

    public void testNewScenario() throws NotFoundException {
        tester.startPage( new ProjectPage( scenario ) );

        long size = dao.getScenarioCount();
        tester.clickLink( "big-form:sc-new" );
        assertEquals( size + 1, dao.getScenarioCount() );

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        tester.startPage( new ProjectPage( scenario ) );
        tester.clickLink( "big-form:sc-new" );
        assertEquals( size + 2, dao.getScenarioCount() );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();
    }

    /** Test submit with part modifications.
     * @throws NotFoundException on error */
    public void testEmptySubmit() throws NotFoundException, IOException {
        Part part = scenario.getDefaultPart();

        tester.startPage( new ProjectPage( scenario, part ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( ProjectPage.class );
        tester.assertNoErrorMessage();

        FormTester ft = tester.newFormTester( "big-form" );
        setFiles( ft, project );
        ft.submit();

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();
        checkFiles( project );
    }

    /** Test submit with part modifications.
     * @throws NotFoundException on error */
    public void testDescriptionSubmit1() throws NotFoundException, IOException {
        Part part = scenario.getDefaultPart();
        part.setDescription( "" );
        assertEquals( "", part.getDescription() );

        tester.startPage( new ProjectPage( scenario, part ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( ProjectPage.class );
        tester.assertNoErrorMessage();

        FormTester ft = tester.newFormTester( "big-form" );
        String desc = "New value";
        ft.setValue( "description", desc );
        setFiles( ft, project );
        ft.submit();

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        assertEquals( desc, part.getDescription() );
        checkFiles( project );
    }

    /** Test submit with part modifications.
     * @throws NotFoundException on error */
    public void testDescriptionSubmit2() throws NotFoundException, IOException {
        Part part = scenario.getDefaultPart();
        part.setDescription( "something" );

        tester.startPage( new ProjectPage( scenario, part ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( ProjectPage.class );
        tester.assertNoErrorMessage();

        FormTester ft = tester.newFormTester( "big-form" );
        String desc = "";
        ft.setValue( "description", desc );
        setFiles( ft, project );
        ft.submit();

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        assertEquals( desc, part.getDescription() );
        checkFiles( project );
    }

    public void testDeleteScenario() throws IOException, NotFoundException {
        assertEquals( 2, dao.getScenarioCount() );
        Scenario sc2 = project.getService().createScenario();
        sc2.setName( "Test" );
        assertEquals( 3, dao.getScenarioCount() );

        tester.startPage( new ProjectPage( scenario ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( ProjectPage.class );
        tester.assertNoErrorMessage();
        assertEquals( 3, dao.getScenarioCount() );

        FormTester ft = tester.newFormTester( "big-form" );
        setFiles( ft, project );
        ft.setValue( "sc-del", "true" );

        ft.submit();
        try {
            assertNull( dao.find( Scenario.class, scenario.getId() ) );
            fail();
        } catch ( NotFoundException ignored ) {}

        assertEquals( 3, dao.getScenarioCount() );
        // the setFiles() imports/creates a new scenario...

    }

    public void testGetParameters1() {
        Part part = scenario.getDefaultPart();
        PageParameters parms = ProjectPage.getParameters( scenario, part );

        assertEquals( scenario.getId(), (long) parms.getAsLong( "scenario" ) );
        assertEquals( part.getId(), (long) parms.getAsLong( "part" ) );
    }

    public void testGetParameters2() {
        Part part = scenario.getDefaultPart();

        Set<Long> expand = new HashSet<Long>( Arrays.asList( 1L, 2L ) );
        PageParameters parms = ProjectPage.getParameters( scenario, part, expand );

        assertEquals( scenario.getId(), (long) parms.getAsLong( "scenario" ) );
        assertEquals( part.getId(), (long) parms.getAsLong( "part" ) );

        Set<String> results = new HashSet<String>(
                Arrays.asList( parms.getStringArray( "expand" ) ) );

        assertEquals( 2, results.size() );
        assertTrue( results.contains( "1" ) );
        assertTrue( results.contains( "2" ) );
    }
}
