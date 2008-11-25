package com.mindalliance.channels.pages;

import com.mindalliance.channels.dao.Memory;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Scenario;
import junit.framework.TestCase;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;

import java.util.Iterator;

/**
 * Simple test using the WicketTester.
 */
@SuppressWarnings( { "HardCodedStringLiteral" } )
public class TestScenarioPage extends TestCase {

    private WicketTester tester;
    private Scenario scenario;
    private Memory dao;

    public TestScenarioPage() {
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        dao = new Memory();
        final Project project = new Project();
        project.setScenarioDao( dao );
        scenario = project.getScenarioDao().getDefaultScenario();
        tester = new WicketTester( project );
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

        parms.put( ScenarioPage.NODE_PARM, scenario.nodes().next().getId() );
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
    }

    /** Test submit with part modifications. */
    public void testPartSubmit() throws NotFoundException {
        tester.startPage( new ScenarioPage( scenario ) );
        tester.assertRenderedPage( ScenarioPage.class );
        tester.assertNoErrorMessage();

        final FormTester ft = tester.newFormTester( "big-form" );

        ft.submit();
        tester.assertRenderedPage( ScenarioPage.class );
        tester.assertNoErrorMessage();
    }

    public void testDeleteScenario() {

    }

}
