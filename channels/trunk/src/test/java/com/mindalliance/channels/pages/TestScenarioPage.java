package com.mindalliance.channels.pages;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.DiagramFactory;
import com.mindalliance.channels.Importer;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.attachments.BitBucket;
import com.mindalliance.channels.dao.Memory;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.SimpleIdGenerator;
import com.mindalliance.channels.export.DummyExporter;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.query.DefaultQueryService;
import junit.framework.TestCase;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.notNull;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

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
@SuppressWarnings( {"HardCodedStringLiteral"} )
public class TestScenarioPage extends TestCase {

    private WicketTester tester;
    private Scenario scenario;
    private Memory dao;
    private Channels app;

    public TestScenarioPage() {
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        app = new Channels();
        PlanManager planManager = new PlanManager( new DummyExporter(), new SimpleIdGenerator() );
        planManager.afterPropertiesSet();
        DefaultQueryService queryService = new DefaultQueryService( planManager, new BitBucket() );
//        queryService.setAddingSamples( true );

        queryService.afterPropertiesSet();

        app.setQueryService( queryService );
        DiagramFactory dm = createMock( DiagramFactory.class );
        Diagram fd = createMock( Diagram.class );
        expect( fd.makeImageMap() ).andReturn( "" ).anyTimes();
        expect( dm.newFlowMapDiagram( (Scenario) anyObject(), null, null, null ) )
                .andReturn( fd ).anyTimes();
        replay( dm );
        replay( fd );
        app.setDiagramFactory( dm );

        Analyst sa = createNiceMock( Analyst.class );
        expect( sa.getIssuesSummary( (ModelObject) anyObject(), anyBoolean() ) )
                .andReturn( "" ).anyTimes();
        expect( sa.getIssuesSummary( (ModelObject) anyObject(), (String) anyObject() ) )
                .andReturn( "" ).anyTimes();
        expect( sa.listIssues( (ModelObject) anyObject(), anyBoolean() ).iterator() )
                .andReturn( new ArrayList<Issue>().iterator() ).anyTimes();
        replay( sa );
        app.setAnalyst( sa );

        scenario = app.getQueryService().getDefaultScenario();
        tester = new WicketTester( app );
        tester.setParametersForNextRequest( new HashMap<String, String[]>() );
    }

    /**
     * Workaround for wicket form tester bug re: file upload. File must be set
     * otherwise all fields set to null. Resolved in wickets 1.4-RC2, coming out
     * soon to a theater near you...
     *
     * @param ft  the tester to fix
     * @param app
     * @throws java.io.IOException
     * @todo remove when moving to Wickets 1.4-RC2
     * @see {https://issues.apache.org/jira/browse/WICKET-1931}
     */
    public static void setFiles( FormTester ft, Channels app ) throws IOException {

        Importer importer = createMock( Importer.class );
        expect( importer.importScenario( (InputStream) notNull() ) )
                .andReturn( app.getQueryService().createScenario() );

        replay( importer );
//        app.setImporterFactory( importer );

        File file = new File( TestScenarioPage.class.getResource( "test.txt" ).getFile() );
        assertTrue( "Can't find " + file.getAbsolutePath(), file.exists() );
        ft.setFile( "sc-import", file, "text/plain" );
        ft.setFile( "attachments:upload", file, "text/plain" );
    }

    /**
     * @param app the Channels app
     * @todo remove when moving to Wickets 1.4-RC2
     */
    public static void checkFiles( Channels app ) {
        verify( app.getImportExportFactory() );
    }

    public void testParms() {
        tester.startPage( PlanPage.class );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        PageParameters parms = new PageParameters();
        parms.put( PlanPage.SCENARIO_PARM, Long.toString( scenario.getId() ) );

        tester.startPage( PlanPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( PlanPage.SCENARIO_PARM, "-1" );
        tester.startPage( PlanPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( PlanPage.SCENARIO_PARM, "bla" );
        tester.startPage( PlanPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( PlanPage.SCENARIO_PARM, Long.toString( scenario.getId() ) );
        parms.put( PlanPage.PART_PARM, "-1" );
        tester.startPage( PlanPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( PlanPage.PART_PARM, "bla" );
        tester.startPage( PlanPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( PlanPage.PART_PARM, Long.toString( scenario.getDefaultPart().getId() ) );
        tester.startPage( PlanPage.class, parms );
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        parms.put( PlanPage.EXPAND_PARM, "bla" );
        tester.startPage( PlanPage.class, parms );
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        parms.add( PlanPage.EXPAND_PARM, "burp" );
        tester.startPage( PlanPage.class, parms );
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();
    }

    /**
     * Test all nodes pages in default scenario.
     */
    public void testNodes() {
        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            tester.startPage( new PlanPage( scenario, parts.next() ) );
            tester.assertRenderedPage( PlanPage.class );
            tester.assertNoErrorMessage();
        }
    }

    private long getScenarioCount() {
        return (long) dao.list( Scenario.class ).size();
    }

    public void testNewScenario() throws NotFoundException {
        tester.startPage( new PlanPage( scenario ) );

        long size = getScenarioCount();
        tester.clickLink( "big-form:sc-new" );
        assertEquals( size + 1, getScenarioCount() );

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        tester.startPage( new PlanPage( scenario ) );
        tester.clickLink( "big-form:sc-new" );
        assertEquals( size + 2, getScenarioCount() );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();
    }

    /**
     * Test submit with part modifications.
     *
     * @throws NotFoundException on error
     */
    public void testEmptySubmit() throws NotFoundException, IOException {
        Part part = scenario.getDefaultPart();

        tester.startPage( new PlanPage( scenario, part ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        FormTester ft = tester.newFormTester( "big-form" );
        setFiles( ft, app );
        ft.submit();

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();
        checkFiles( app );
    }

    /**
     * Test submit with part modifications.
     *
     * @throws NotFoundException on error
     */
    public void testDescriptionSubmit1() throws NotFoundException, IOException {
        Part part = scenario.getDefaultPart();
        part.setDescription( "" );
        assertEquals( "", part.getDescription() );

        tester.startPage( new PlanPage( scenario, part ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        FormTester ft = tester.newFormTester( "big-form" );
        String desc = "New value";
        ft.setValue( "description", desc );
        setFiles( ft, app );
        ft.submit();

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        assertEquals( desc, part.getDescription() );
        checkFiles( app );
    }

    /**
     * Test submit with part modifications.
     *
     * @throws NotFoundException on error
     */
    public void testDescriptionSubmit2() throws NotFoundException, IOException {
        Part part = scenario.getDefaultPart();
        part.setDescription( "something" );

        tester.startPage( new PlanPage( scenario, part ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();

        FormTester ft = tester.newFormTester( "big-form" );
        String desc = "";
        ft.setValue( "description", desc );
        setFiles( ft, app );
        ft.submit();

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        assertEquals( desc, part.getDescription() );
        checkFiles( app );
    }

    public void testDeleteScenario() throws IOException, NotFoundException {
        assertEquals( 2, getScenarioCount() );
        Scenario sc2 = app.getQueryService().createScenario();
        sc2.setName( "Test" );
        assertEquals( 3, getScenarioCount() );

        tester.startPage( new PlanPage( scenario ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( PlanPage.class );
        tester.assertNoErrorMessage();
        assertEquals( 3, getScenarioCount() );

        FormTester ft = tester.newFormTester( "big-form" );
        setFiles( ft, app );
        ft.setValue( "sc-del", "true" );

        ft.submit();
        try {
            assertNull( dao.find( Scenario.class, scenario.getId() ) );
            fail();
        } catch ( NotFoundException ignored ) {
        }

        assertEquals( 3, getScenarioCount() );
        // the setFiles() imports/creates a new scenario...

    }

    public void testGetParameters1() {
        Part part = scenario.getDefaultPart();
        PageParameters parms = PlanPage.getParameters( scenario, part );

        assertEquals( scenario.getId(), (long) parms.getAsLong( "scenario" ) );
        assertEquals( part.getId(), (long) parms.getAsLong( "part" ) );
    }

    public void testGetParameters2() {
        Part part = scenario.getDefaultPart();

        Set<Long> expand = new HashSet<Long>( Arrays.asList( 1L, 2L ) );
        PageParameters parms = PlanPage.getParameters( scenario, part, expand );

        assertEquals( scenario.getId(), (long) parms.getAsLong( "scenario" ) );
        assertEquals( part.getId(), (long) parms.getAsLong( "part" ) );

        Set<String> results = new HashSet<String>(
                Arrays.asList( parms.getStringArray( "expand" ) ) );

        assertEquals( 2, results.size() );
        assertTrue( results.contains( "1" ) );
        assertTrue( results.contains( "2" ) );
    }
}
