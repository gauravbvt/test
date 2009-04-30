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
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.query.DataQueryObjectImpl;
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
    private Channels app;

    public TestScenarioPage() {
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        dao = new Memory();
        app = new Channels();
        DataQueryObjectImpl dqo = new DataQueryObjectImpl();
        dqo.setAddingSamples( true );
        dqo.setDao( dao );
        dqo.initialize();

        app.setDqo( dqo );
        app.setAttachmentManager( new BitBucket() );
        DiagramFactory dm = createMock( DiagramFactory.class );
        Diagram fd = createMock(  Diagram.class);
        expect( fd.makeImageMap( ) ).andReturn( "" ).anyTimes();
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
        expect( sa.findIssues( (ModelObject) anyObject(), anyBoolean() ))
                .andReturn( new ArrayList<Issue>().iterator() ).anyTimes();
        replay( sa );
        app.setAnalyst( sa );

        scenario = app.getDqo().getDefaultScenario();
        tester = new WicketTester( app );
        tester.setParametersForNextRequest( new HashMap<String,String[]>() );
    }

    /** Workaround for wicket form tester bug re: file upload. File must be set
     * otherwise all fields set to null. Resolved in wickets 1.4-RC2, coming out
     * soon to a theater near you...
     * @param ft the tester to fix
     * @param app
     * @see {https://issues.apache.org/jira/browse/WICKET-1931}
     * @todo remove when moving to Wickets 1.4-RC2
     * @throws java.io.IOException
     */
    public static void setFiles( FormTester ft, Channels app ) throws IOException {

        Importer importer = createMock( Importer.class );
        expect( importer.importScenario( (InputStream) notNull() ) )
                .andReturn( app.getDqo().createScenario() );

        replay( importer );
        app.setImporter( importer );

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
        verify( app.getImporter() );
    }

    public void testParms() {
        tester.startPage( ChannelsPage.class );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        PageParameters parms = new PageParameters();
        parms.put( ChannelsPage.SCENARIO_PARM, Long.toString( scenario.getId() ) );

        tester.startPage( ChannelsPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( ChannelsPage.SCENARIO_PARM, "-1" );
        tester.startPage( ChannelsPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( ChannelsPage.SCENARIO_PARM, "bla" );
        tester.startPage( ChannelsPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( ChannelsPage.SCENARIO_PARM, Long.toString( scenario.getId() ) );
        parms.put( ChannelsPage.PART_PARM, "-1" );
        tester.startPage( ChannelsPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( ChannelsPage.PART_PARM, "bla" );
        tester.startPage( ChannelsPage.class, parms );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        parms.put( ChannelsPage.PART_PARM, Long.toString( scenario.getDefaultPart().getId() ) );
        tester.startPage( ChannelsPage.class, parms );
        tester.assertRenderedPage( ChannelsPage.class );
        tester.assertNoErrorMessage();

        parms.put( ChannelsPage.EXPAND_PARM, "bla" );
        tester.startPage( ChannelsPage.class, parms );
        tester.assertRenderedPage( ChannelsPage.class );
        tester.assertNoErrorMessage();

        parms.add( ChannelsPage.EXPAND_PARM, "burp" );
        tester.startPage( ChannelsPage.class, parms );
        tester.assertRenderedPage( ChannelsPage.class );
        tester.assertNoErrorMessage();
    }

    /** Test all nodes pages in default scenario. */
    public void testNodes() {
        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            tester.startPage( new ChannelsPage( scenario, parts.next() ) );
            tester.assertRenderedPage( ChannelsPage.class );
            tester.assertNoErrorMessage();
        }
    }

    public void testNewScenario() throws NotFoundException {
        tester.startPage( new ChannelsPage( scenario ) );

        long size = dao.getScenarioCount();
        tester.clickLink( "big-form:sc-new" );
        assertEquals( size + 1, dao.getScenarioCount() );

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();

        tester.startPage( new ChannelsPage( scenario ) );
        tester.clickLink( "big-form:sc-new" );
        assertEquals( size + 2, dao.getScenarioCount() );
        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();
    }

    /** Test submit with part modifications.
     * @throws NotFoundException on error */
    public void testEmptySubmit() throws NotFoundException, IOException {
        Part part = scenario.getDefaultPart();

        tester.startPage( new ChannelsPage( scenario, part ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( ChannelsPage.class );
        tester.assertNoErrorMessage();

        FormTester ft = tester.newFormTester( "big-form" );
        setFiles( ft, app );
        ft.submit();

        tester.assertRenderedPage( RedirectPage.class );
        tester.assertNoErrorMessage();
        checkFiles( app );
    }

    /** Test submit with part modifications.
     * @throws NotFoundException on error */
    public void testDescriptionSubmit1() throws NotFoundException, IOException {
        Part part = scenario.getDefaultPart();
        part.setDescription( "" );
        assertEquals( "", part.getDescription() );

        tester.startPage( new ChannelsPage( scenario, part ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( ChannelsPage.class );
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

    /** Test submit with part modifications.
     * @throws NotFoundException on error */
    public void testDescriptionSubmit2() throws NotFoundException, IOException {
        Part part = scenario.getDefaultPart();
        part.setDescription( "something" );

        tester.startPage( new ChannelsPage( scenario, part ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( ChannelsPage.class );
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
        assertEquals( 2, dao.getScenarioCount() );
        Scenario sc2 = app.getDqo().createScenario();
        sc2.setName( "Test" );
        assertEquals( 3, dao.getScenarioCount() );

        tester.startPage( new ChannelsPage( scenario ) );
        tester.setupRequestAndResponse();
        tester.assertRenderedPage( ChannelsPage.class );
        tester.assertNoErrorMessage();
        assertEquals( 3, dao.getScenarioCount() );

        FormTester ft = tester.newFormTester( "big-form" );
        setFiles( ft, app );
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
        PageParameters parms = ChannelsPage.getParameters( scenario, part );

        assertEquals( scenario.getId(), (long) parms.getAsLong( "scenario" ) );
        assertEquals( part.getId(), (long) parms.getAsLong( "part" ) );
    }

    public void testGetParameters2() {
        Part part = scenario.getDefaultPart();

        Set<Long> expand = new HashSet<Long>( Arrays.asList( 1L, 2L ) );
        PageParameters parms = ChannelsPage.getParameters( scenario, part, expand );

        assertEquals( scenario.getId(), (long) parms.getAsLong( "scenario" ) );
        assertEquals( part.getId(), (long) parms.getAsLong( "part" ) );

        Set<String> results = new HashSet<String>(
                Arrays.asList( parms.getStringArray( "expand" ) ) );

        assertEquals( 2, results.size() );
        assertTrue( results.contains( "1" ) );
        assertTrue( results.contains( "2" ) );
    }
}
