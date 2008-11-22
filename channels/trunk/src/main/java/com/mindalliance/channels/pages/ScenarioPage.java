package com.mindalliance.channels.pages;

import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.dao.ScenarioDao;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.StringValueConversionException;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * The scenario editor page.
 */
public final class ScenarioPage extends WebPage {

    /** The 'scenario' parameter in the URL. */
    static final String SCENARIO_PARM = "scenario";             // NON-NLS

    /** The 'part' parameter in the URL. */
    static final String NODE_PARM = "node";                     // NON-NLS

    /** The 'expand' parameter in the URL. */
    static final String EXPAND_PARM = "expand";                 // NON-NLS

    /** Class logger. */
    private static final Log LOG = LogFactory.getLog( ScenarioPage.class );

    /** Description field name. */
    private static final String DESC_FIELD = "description";     // NON-NLS

    /** Specialty field name. */
    private static final String SPECIALTY_FIELD = "specialty";  // NON-NLS

    /**
     * Used when page is called without parameters.
     * Redirect to default scenario, default node, all collapsed.
     */
    public ScenarioPage() {
        this( new PageParameters() );
    }

    public ScenarioPage( PageParameters parameters ) {
        // Call super to remember parameters in links
        super( parameters );

        final ScenarioDao scenarioDao = getScenarioDao();
        final Scenario scenario = findScenario( scenarioDao, parameters );

        if ( scenario == null )
            redirectTo( scenarioDao.getDefaultScenario() );

        else {
            final Node node = findNode( scenario, parameters );
            if ( node != null )
                init( scenario, node, findExpansions( parameters ) );
            else
                redirectTo( scenario );
        }
    }

    /**
     * Utility constructor for tests.
     * @param scenario a scenario
     * @param node a node in the scenario
     */
    ScenarioPage( Scenario scenario, Node node ) {
        final Set<String> expansions = Collections.emptySet();
        init( scenario, node, expansions );
    }

    private static Set<String> findExpansions( PageParameters parameters ) {
        final Set<String> result = new HashSet<String>( parameters.size() );
        if ( parameters.containsKey( EXPAND_PARM ) )
            result.addAll( Arrays.asList( parameters.getStringArray( EXPAND_PARM ) ) );

        return result;
    }

    /**
     * Find scenario specified in parameters.
     * @param scenarioDao the scenario container
     * @param parameters the page parameters
     * @return a scenario, or null if not found
     */
    private static Scenario findScenario( ScenarioDao scenarioDao, PageParameters parameters ) {
        if ( parameters.containsKey( SCENARIO_PARM ) )
            try {
                return scenarioDao.findScenario( parameters.getLong( SCENARIO_PARM ) );
            } catch ( StringValueConversionException ignored ) {
                LOG.warn( "Invalid scenario specified in parameters. Using default." );
            } catch ( NotFoundException ignored ) {
                LOG.warn( "Unknown scenario specified in parameters. Using default." );
            }
        return null;
    }

    /**
     * Find node specified in parameters.
     * @param scenario the scenario
     * @param parameters the page parameters
     * @return a node, or null if not found
     */
    private static Node findNode( Scenario scenario, PageParameters parameters ) {
        if ( parameters.containsKey( NODE_PARM ) )
            try {
                return scenario.getNode( parameters.getLong( NODE_PARM ) );
            } catch ( StringValueConversionException ignored ) {
                LOG.warn( "Invalid node specified in parameters. Using default." );
            }
        return null;
    }

    private void redirectTo( Scenario scenario ) {
        final Iterator<Node> nodes = scenario.nodes();
        // TODO remove the next line when able to navigate across nodes
        nodes.next(); nodes.next(); nodes.next();
        redirectTo( scenario, nodes.next() );
    }

    private void redirectTo( Scenario scenario, Node node ) {
        final long sid = scenario.getId();
        final long nid = node.getId();
        setResponsePage(
                new RedirectPage(
                        MessageFormat.format( "?scenario={0}&node={1}", sid, nid ) ) );   // NON-NLS
    }

    private void init( Scenario scenario, Node node, Set<String> expansions ) {
        final PropertyModel<String> title =
                new PropertyModel<String>( scenario, "name" );                            // NON-NLS
        add( new Label( "sc-title", title ) );                                            // NON-NLS
        add( new Label( "header", title ) );                                              // NON-NLS
        add( new Label( "sc-desc", new PropertyModel<String>( scenario, DESC_FIELD ) ) ); // NON-NLS

        add( new Label( "node-title", new PropertyModel<String>( node, "title" ) ) );     // NON-NLS
        add( new Label( "node-desc", new PropertyModel<String>( node, DESC_FIELD ) ) );   // NON-NLS

        if ( node.isPart() )
            add( new PartPanel( SPECIALTY_FIELD, new Model<Part>( (Part) node ) ) );
        else
            add( new Label( SPECIALTY_FIELD, "" ) );

        LOG.info( "Scenario page generated" );
    }

    /**
     * Get the scenario manager from project via the application context.
     * @return the scenario DAO
     */
    private ScenarioDao getScenarioDao() {
        return ( (Project) getApplication() ).getScenarioDao();
    }
}
