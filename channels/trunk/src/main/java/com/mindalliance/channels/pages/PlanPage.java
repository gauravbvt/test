package com.mindalliance.channels.pages;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.ScenarioObject;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.model.UserIssue;
import com.mindalliance.channels.pages.components.GeomapLinkPanel;
import com.mindalliance.channels.pages.components.IndicatorAwareForm;
import com.mindalliance.channels.pages.components.ScenarioImportPanel;
import com.mindalliance.channels.pages.components.ScenarioLink;
import com.mindalliance.channels.pages.components.ScenarioPanel;
import com.mindalliance.channels.pages.components.entities.EntityPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.menus.PlanActionsMenuPanel;
import com.mindalliance.channels.pages.components.menus.PlanShowMenuPanel;
import com.mindalliance.channels.pages.components.plan.PlanEditPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * The plan's home page.
 */
public final class PlanPage extends WebPage implements Updatable {
    /**
     * Delay between refresh check callbacks.
     */
    public static final int REFRESH_DELAY = 10;
    /**
     * The 'expand' parameter in the URL.
     */
    public static final String EXPAND_PARM = "expand";                                    // NON-NLS

    /**
     * The 'scenario' parameter in the URL.
     */
    static final String SCENARIO_PARM = "scenario";                                       // NON-NLS

    /**
     * The 'part' parameter in the URL.
     */
    static final String PART_PARM = "node";                                               // NON-NLS

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanPage.class );

    /**
     * Length a scenario title is abbreviated to
     */
    private static final int SCENARIO_TITLE_MAX_LENGTH = 40;

    /**
     * Length a scenario title is abbreviated to
     */
    private static final int SCENARIO_DESCRIPTION_MAX_LENGTH = 94;
    /**
     * The current user.
     */
    @SpringBean
    private User user;
    /**
     * The plan manager.
     */
    @SpringBean
    private PlanManager planManager;

    @SpringBean
    Commander commander;

    /**
     * Id of components that are expanded.
     */
    private Set<Long> expansions;

    /**
     * Ids of expanded entities.
     */
    private List<EntityExpansion> expandedEntities = new ArrayList<EntityExpansion>();

    /**
     * Label with name of scenario.
     */
    private Label scenarioNameLabel;
    /**
     * Link to mapping of parts.
     */
    private GeomapLinkPanel partsMapLink;
    /**
     * Label with description of scenario.
     */
    private Label scenarioDescriptionLabel;
    /**
     * Container of scenario selector.
     */
    private WebMarkupContainer selectScenarioContainer;
    /**
     * Choice of scenarios.
     */
    private DropDownChoice<Scenario> scenarioDropDownChoice;
    /**
     * Container of plan switcher.
     */
    private WebMarkupContainer switchPlanContainer;
    /**
     * Container of link to admin page.
     */
    private WebMarkupContainer adminContainer;
    /**
     * Scenarios action menu.
     */
    private MenuPanel planActionsMenu;
    /**
     * Scenarios show menu.
     */
    private MenuPanel planShowMenu;
    /**
     * The current part.
     */
    private Part part;

    /**
     * The current scenario.
     */
    private Scenario scenario;

    /**
     * The big form -- used for attachments and scenario imports only.
     */
    private IndicatorAwareForm form;
    /**
     * Import scenario "dialog".
     */
    private ScenarioImportPanel scenarioImportPanel;
    /**
     * The scenario panel.
     */
    private ScenarioPanel scenarioPanel;

    /**
     * The entity panel.
     */
    private Component entityPanel;

    /**
     * The scenarios map panel.
     */
    private Component planEditPanel;
    /**
     * The aspect for entity panel.
     */
    private String entityAspect = EntityPanel.DETAILS;
    /**
     * Refresh button container.
     */
    private WebMarkupContainer refreshNeededContainer;
    /**
     * When last refreshed.
     */
    private long lastRefreshed = System.currentTimeMillis();

    /**
     * Used when page is called without parameters.
     * Set to default scenario, default part, all collapsed.
     */
    public PlanPage() {
        this( new PageParameters() );
    }

    public PlanPage( PageParameters parameters ) {
        // Call super to remember parameters in links
        super( parameters );
        QueryService queryService = getQueryService();
        Scenario sc = findScenario( queryService, parameters );
        Part p = findPart( scenario, parameters );
        init( sc, p, findExpansions( parameters ) );
    }

    /**
     * Utility constructor for tests.
     *
     * @param scenario a scenario
     */
    public PlanPage( Scenario scenario ) {
        this( scenario, scenario.getDefaultPart() );
    }

    /**
     * Utility constructor for tests.
     *
     * @param sc a scenario
     * @param p  a part in the scenario
     */
    public PlanPage( Scenario sc, Part p ) {
        init( sc, p, new HashSet<Long>() );
    }


    private void init( Scenario sc, Part p, Set<Long> expanded ) {
        getCommander().releaseAllLocks( user.getUsername() );
        setScenario( sc );
        setPart( p );
        expansions = expanded;
        for ( Long id : expansions ) {
            getCommander().requestLockOn( id );
        }
        setVersioned( false );
        setStatelessHint( true );
        add( new Label( "sc-title",
                new Model<String>( "Channels: " + getPlan().getName() ) ) );

        form = new IndicatorAwareForm( "big-form" ) {
            @Override
            protected void onSubmit() {
                // Drop user history on submit
                getApp().getCommander().resetUserHistory( user.getUsername(), true );
            }
        };
        add( form );
        addHeader();
        addRefresh();
        addPlanMenubar();
        addScenarioSelector();
        addPlanSwitcher();
        scenarioImportPanel = new ScenarioImportPanel( "scenario-import" );
        form.add( scenarioImportPanel );
        scenarioPanel = new ScenarioPanel(
                "scenario",
                new PropertyModel<Scenario>( this, "scenario" ),
                new PropertyModel<Part>( this, "part" ),
                getReadOnlyExpansions() );
        form.add( scenarioPanel );
        addEntityPanel();
        addPlanEditPanel();
        updateVisibility();
        LOG.debug( "Scenario page generated" );
    }

    private void addHeader() {
        scenarioNameLabel = new Label(
                "header",                                                                 // NON-NLS
                new AbstractReadOnlyModel() {
                    @Override
                    public Object getObject() {
                        return StringUtils.abbreviate(
                                scenario.getName(), SCENARIO_TITLE_MAX_LENGTH );
                    }
                }
        );
        scenarioNameLabel.setOutputMarkupId( true );
        // Add style mods from scenario analyst.
        annotateScenarioName();
        form.add( scenarioNameLabel );
        // Add link to map of parts
        addPartsMapLink();
        // Scenario description
        scenarioDescriptionLabel = new Label( "sc-desc",                                  // NON-NLS
                new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        return StringUtils.abbreviate(
                                scenario.getDescription(),
                                SCENARIO_DESCRIPTION_MAX_LENGTH );
                    }
                }
        );
        scenarioDescriptionLabel.setOutputMarkupId( true );
        form.add( scenarioDescriptionLabel );
        form.add( new Label( "user", user.getUsername() ) );                                  // NON-NLS
    }

    private void addPartsMapLink() {
        List<GeoLocatable> geoLocatables = new ArrayList<GeoLocatable>();
        Iterator<Part> parts = getScenario().parts();
        while ( parts.hasNext() ) {
            geoLocatables.add( parts.next() );
        }
        partsMapLink = new GeomapLinkPanel(
                "geomapLink",
                new Model<String>( "Tasks with known locations in scenario " + getScenario().getName() ),
                geoLocatables,
                new Model<String>( "Show parts in map" ) );
        partsMapLink.setOutputMarkupId( true );
        form.addOrReplace( partsMapLink );
    }

    private void addRefresh() {
        refreshNeededContainer = new WebMarkupContainer( "refresh-needed" );
        refreshNeededContainer.setOutputMarkupId( true );
        refreshNeededContainer.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                reacquireLocks();
                lastRefreshed = System.currentTimeMillis();
                refreshAll( target );
            }
        } );
        // Put timer on form since it is never updated or replaced
        form.add( new AbstractAjaxTimerBehavior( Duration.seconds( REFRESH_DELAY ) ) {
            @Override
            protected void onTimer( AjaxRequestTarget target ) {
                doTimedUpdate( target );
            }
        } );

        form.add( refreshNeededContainer );
        updateRefreshNotice();
    }

    private void doTimedUpdate( AjaxRequestTarget target ) {
        getCommander().processTimeOuts();
        if ( getCommander().isTimedOut() ) {
            refreshAll( target );
        } else {
            updateRefreshNotice();
            target.addComponent( refreshNeededContainer );
        }
    }


    private void updateRefreshNotice() {
        String reasonsToRefresh = getReasonsToRefresh();
        makeVisible(
                refreshNeededContainer,
                !reasonsToRefresh.isEmpty() );
        refreshNeededContainer.add( new AttributeModifier(
                "title",
                true,
                new Model<String>( "Refresh:" + reasonsToRefresh ) ) );
    }

    private String getReasonsToRefresh() {
        String reasons = "";
        String lastModifier = getCommander().getLastModifier();
        long lastModified = getCommander().getLastModified();
        if ( lastModified > lastRefreshed
                && !lastModifier.isEmpty()
                && !lastModifier.equals( user.getUsername() ) ) {
            reasons = " -- Plan was modified by " + lastModifier;
        }
        // Find expansions that were locked and are not unlocked
        Set<ModelObject> editables = getEditableModelObjects();
        for ( ModelObject mo : editables ) {
            if ( !( mo instanceof Scenario || mo instanceof Plan )
                    && getCommander().isUnlocked( mo ) ) {
                reasons += " -- " + mo.getName() + " can now be edited.";
            }
        }
        return reasons;
    }

    private Set<ModelObject> getEditableModelObjects() {
        Set<ModelObject> editables = new HashSet<ModelObject>();
        for ( Long id : expansions ) {
            try {
                ModelObject mo = getQueryService().find( ModelObject.class, id );
                editables.add( mo );
            } catch ( NotFoundException ignored ) {
                // ignore
            }
        }
        editables.add( getPart() );
        return editables;
    }

    private void annotateScenarioName() {
        Analyst analyst = getApp().getAnalyst();
        String issue = analyst.getIssuesSummary( scenario, Analyst.INCLUDE_PROPERTY_SPECIFIC );
        scenarioNameLabel.add(
                new AttributeModifier( "class", true,                                     // NON-NLS
                        new Model<String>( issue.isEmpty() ? "no-error" : "error" ) ) );      // NON-NLS
        scenarioNameLabel.add(
                new AttributeModifier( "title", true,                                     // NON-NLS
                        new Model<String>( issue.isEmpty() ? "No known issue" : issue ) ) );
    }

    private void addPlanMenubar() {
        PropertyModel<Scenario> sc = new PropertyModel<Scenario>( this, "scenario" );
        Set<Long> exps = getReadOnlyExpansions();

        planActionsMenu = new PlanActionsMenuPanel( "planActionsMenu", sc, exps );
        planActionsMenu.setOutputMarkupId( true );
        form.add( planActionsMenu );

        planShowMenu = new PlanShowMenuPanel( "planShowMenu", sc, exps );
        planShowMenu.setOutputMarkupId( true );
        form.add( planShowMenu );
        form.add( new Label( "username", user.getUsername() ) );
        adminContainer = new WebMarkupContainer( "admin" );
        form.add( adminContainer );
    }

    private void addPlanActionsMenu() {
        planActionsMenu = new PlanActionsMenuPanel(
                "planActionsMenu",
                new PropertyModel<Scenario>( this, "scenario" ),
                getReadOnlyExpansions() );
        planActionsMenu.setOutputMarkupId( true );
        form.addOrReplace( planActionsMenu );
    }

    private void addScenarioSelector() {
        selectScenarioContainer = new WebMarkupContainer( "select-scenario" );
        selectScenarioContainer.setOutputMarkupId( true );
        form.add( selectScenarioContainer );
        scenarioDropDownChoice = new DropDownChoice<Scenario>(
                "sc-sel",                                                                 // NON-NLS
                new PropertyModel<Scenario>( this, "scenario" ),                          // NON-NLS
                new PropertyModel<List<? extends Scenario>>( this, "allScenarios" ) );    // NON-NLS
        scenarioDropDownChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) { // NON-NLS

            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Selected, getScenario() ) );
            }
        } );
        scenarioDropDownChoice.setOutputMarkupId( true );
        selectScenarioContainer.add( scenarioDropDownChoice );
    }

    private void addPlanSwitcher() {
        switchPlanContainer = new WebMarkupContainer( "switch-plan" );
        switchPlanContainer.setOutputMarkupId( true );
        form.add( switchPlanContainer );
        DropDownChoice<Plan> planDropDownChoice = new DropDownChoice<Plan>(
                "plan-sel",
                new PropertyModel<Plan>( this, "plan" ),
                new PropertyModel<List<? extends Plan>>( this, "writablePlans" ) );
        planDropDownChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Selected, getPlan() ) );
            }
        } );
        switchPlanContainer.add( planDropDownChoice );
    }

    private void addEntityPanel() {
        ModelObject entity = findExpandedEntity();
        if ( entity == null ) {
            entityPanel = new Label( "entity", "" );
        } else {
            entityPanel = new EntityPanel(
                    "entity",
                    new Model<ModelObject>( entity ),
                    getReadOnlyExpansions(),
                    entityAspect );
        }
        makeVisible( entityPanel, entity != null );
        entityPanel.setOutputMarkupId( true );
        form.addOrReplace( entityPanel );
    }

    private void addPlanEditPanel() {
        boolean showPlanEdit = expansions.contains( getPlan().getId() );

        planEditPanel = showPlanEdit
                ? new PlanEditPanel(
                "plan",
                new Model<Plan>( getPlan() ),
                getReadOnlyExpansions() )
                : new Label( "plan", "" );

        makeVisible( planEditPanel, showPlanEdit );
        planEditPanel.setOutputMarkupId( true );
        form.addOrReplace( planEditPanel );
    }

    /**
     * Return current plan.
     *
     * @return a plan
     */
    public Plan getPlan() {
        return user.getPlan();
    }

    /**
     * Switch the user's current plan.
     *
     * @param plan a plan
     */
    public void setPlan( Plan plan ) {
        user.switchPlan( plan );
    }

    private ModelObject findExpandedEntity() {
        for ( long id : expansions ) {
            try {
                ModelObject mo = getQueryService().find( ModelObject.class, id );
                if ( mo.isEntity() ) return mo;
            }
            catch ( NotFoundException ignored ) {
                // ignore
            }
        }

        return null;
    }

    public List<Scenario> getAllScenarios() {
        List<Scenario> allScenarios = new ArrayList<Scenario>( getApp().getQueryService().list( Scenario.class ) );
        Collections.sort( allScenarios, new Comparator<Scenario>() {
            public int compare( Scenario o1, Scenario o2 ) {
                return Collator.getInstance().compare( o1.getName(), o2.getName() );
            }
        } );

        return allScenarios;
    }

    /**
     * Find scenario specified in parameters.
     *
     * @param queryService query service
     * @param parameters   the page parameters
     * @return a scenario, or null if not found
     */
    public static Scenario findScenario( QueryService queryService, PageParameters parameters ) {
        if ( parameters.containsKey( SCENARIO_PARM ) )
            try {
                return queryService.find( Scenario.class, parameters.getLong( SCENARIO_PARM ) );
            } catch ( StringValueConversionException ignored ) {
                LOG.warn( "Invalid scenario specified in parameters. Using default." );
            } catch ( NotFoundException ignored ) {
                LOG.warn( "Unknown scenario specified in parameters. Using default." );
            } catch ( ClassCastException ignored ) {
                LOG.warn( "Other object specified as scenario in parameters. Using default." );
            }
        return null;
    }

    /**
     * Find part specified in parameters.
     *
     * @param scenario   the scenario
     * @param parameters the page parameters
     * @return a part, or null if not found
     */
    public static Part findPart( Scenario scenario, PageParameters parameters ) {
        if ( parameters.containsKey( PART_PARM ) )
            try {
                if ( scenario != null )
                    return (Part) scenario.getNode( parameters.getLong( PART_PARM ) );
            } catch ( StringValueConversionException ignored ) {
                LOG.warn( "Invalid part specified in parameters. Using default." );
            }
        return null;
    }

    /**
     * Redirect to a scenario.
     *
     * @param scenario a scenario
     */
    public void redirectTo( Scenario scenario ) {
        redirectTo( scenario.getDefaultPart() );
    }

    /**
     * Redirect to current plan page.
     */
    public void redirectToPlan() {
        setResponsePage( new RedirectPage( "plan" ) );
    }

    /**
     * redirect to a part.
     *
     * @param p a part
     */
    private void redirectTo( Part p ) {
        Set<Long> ids = Collections.emptySet();
        setResponsePage( new RedirectPage( ScenarioLink.linkStringFor( p, ids ) ) );
    }

    /**
     * Redirect here.
     * // TODO - UNUSED
     */
    public void redirectHere() {
        long sid = scenario.getId();
        long nid = getPart().getId();
        StringBuffer exps = new StringBuffer( 128 );
        for ( long id : expansions ) {
            exps.append( "&expand=" );                                                    // NON-NLS
            exps.append( Long.toString( id ) );
        }
        setResponsePage(
                new RedirectPage(
                        MessageFormat.format(
                                "?scenario={0,number,0}&part={1,number,0}{2}",            // NON-NLS
                                sid,
                                nid,
                                exps ) ) );
    }

    /**
     * Return initialized parameters for given scenario and part.
     *
     * @param scenario the scenario
     * @param p        the part, maybe null (in which case, would link to first part in scenario)
     * @param expanded components id that should be expanded
     * @return page parameters to use in links, etc.
     */
    public static PageParameters getParameters(
            Scenario scenario, Part p, Set<Long> expanded ) {

        PageParameters result = new PageParameters();
        result.put( SCENARIO_PARM, Long.toString( scenario.getId() ) );
        if ( p != null ) {
            result.put( PART_PARM, Long.toString( p.getId() ) );
            for ( long id : expanded )
                result.add( EXPAND_PARM, Long.toString( id ) );
        }
        return result;
    }

    /**
     * Return initialized parameters for given scenario and part.
     *
     * @param scenario the scenario
     * @param p        the part, maybe null (in which case, would link to first part in scenario)
     * @return page parameters to use in links, etc.
     */
    public static PageParameters getParameters( Scenario scenario, Part p ) {
        Set<Long> expansions = Collections.emptySet();
        return getParameters( scenario, p, expansions );
    }

    /**
     * Return initialized parameters for given scenario and part.
     *
     * @param scenario the scenario
     * @param p        the part, maybe null (in which case, would link to first part in scenario)
     * @param id       the id to expand
     * @return page parameters to use in links, etc.
     */
    public static PageParameters getParameters( Scenario scenario, Part p, long id ) {
        Set<Long> expansions = new HashSet<Long>( 1 );
        expansions.add( id );
        return getParameters( scenario, p, expansions );
    }

    /**
     * Find expansions in page parameters
     *
     * @return set of ids
     */
    public Set<Long> findExpansions() {
        return findExpansions( getPageParameters() );
    }

    /**
     * Find expansions in page parameters
     *
     * @param parameters page parameters
     * @return set of ids
     */
    public static Set<Long> findExpansions( PageParameters parameters ) {
        if ( parameters == null ) return new HashSet<Long>();
        Set<Long> result = new HashSet<Long>( parameters.size() );
        if ( parameters.containsKey( EXPAND_PARM ) ) {
            List<String> stringList = Arrays.asList( parameters.getStringArray( EXPAND_PARM ) );
            for ( String id : stringList )
                try {
                    result.add( Long.valueOf( id ) );
                } catch ( NumberFormatException ignored ) {
                    LOG.warn( MessageFormat.format( "Invalid expansion parameter: {0}", id ) );
                }
        }
        return result;
    }

    /**
     * Get the channels query service from the application.
     *
     * @return the query service
     */
    private QueryService getQueryService() {
        return getApp().getQueryService();
    }

    private Channels getApp() {
        return Channels.instance();
    }

    private Commander getCommander() {
        return commander;
        // return getApp().getCommander();
    }

    /**
     * Get set or default part.
     *
     * @return a part
     */
    public Part getPart() {
        if ( isZombie( part ) ) {
            Part part = scenario.getDefaultPart();
            getCommander().requestLockOn( part );
            return part;
        } else {
            return part;
        }
    }

    private boolean isZombie( Part part ) {
        return part == null
                || scenario.getNode( part.getId() ) == null
                || part.getScenario() == null;
    }

    /**
     * Set part shown.
     *
     * @param p a part
     */
    public void setPart( Part p ) {
        if ( part != null ) getCommander().releaseAnyLockOn( part );
        part = p;
        if ( part == null ) part = scenario.getDefaultPart();
        getCommander().requestLockOn( part );
        if ( part.getScenario() != scenario ) setScenario( part.getScenario() );
    }

    /**
     * Return page parameters with an added expand parameter.
     *
     * @param id a model object id
     * @return page parameters
     */
    public PageParameters getParametersExpanding( long id ) {
        PageParameters result = getPageParameters();
        if ( !findExpansions().contains( id ) ) {
            result.add( EXPAND_PARM, Long.toString( id ) );
        }
        return result;
    }

    /**
     * To support tests.
     *
     * @return page parameters
     */
    @Override
    public PageParameters getPageParameters() {
        PageParameters params = super.getPageParameters();
        if ( params == null ) params = new PageParameters();
        return params;
    }

    /**
     * Returns page parameters with an expand parameter removed.
     *
     * @param id a model object id
     * @return page parameters
     */
    public PageParameters getParametersCollapsing( long id ) {
        PageParameters result = getPageParameters();
        String[] expanded = result.getStringArray( EXPAND_PARM );
        String idString = Long.toString( id );
        result.remove( EXPAND_PARM );
        if ( expanded != null ) {
            for ( String exp : expanded )
                if ( !exp.equals( idString ) ) result.add( EXPAND_PARM, exp );
        }
        return result;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario( Scenario sc ) {
        if ( scenario != null && scenario != sc ) {
            collapseScenarioObjects();
        }
        scenario = sc;
        if ( scenario == null )
            scenario = getQueryService().getDefaultScenario();
    }

    private void collapseScenarioObjects() {
        List<Identifiable> toCollapse = new ArrayList<Identifiable>();
        // List<Identifiable> toReexpand = new ArrayList<Identifiable>();
        for ( long id : expansions ) {
            try {
                ModelObject expanded = getQueryService().find( ModelObject.class, id );
/*
                if ( expanded instanceof Scenario ) {
                    toCollapse.add( expanded );
                    toReexpand.add( getScenario() );
                }
*/
                if ( expanded instanceof ScenarioObject ) {
                    if ( ( (ScenarioObject) expanded ).getScenario() == scenario ) {
                        toCollapse.add( expanded );
                    }
                }
            } catch ( NotFoundException e ) {
                LOG.warn( "Failed to find expanded " + id );
            }
        }
        for ( Identifiable identifiable : toCollapse ) {
            collapse( identifiable );
        }
/*
        for ( Identifiable identifiable : toReexpand ) {
            expand( identifiable );
        }
*/
    }

    private void collapsePartObjects() {
        List<Identifiable> toCollapse = new ArrayList<Identifiable>();
        for ( long id : expansions ) {
            try {
                ModelObject expanded = getQueryService().find( ModelObject.class, id );
                if ( expanded instanceof Flow ) {
                    toCollapse.add( expanded );
                } else {
                    if ( expanded instanceof Issue ) {
                        Issue issue = (Issue) expanded;
                        ModelObject about = issue.getAbout();
                        if ( about instanceof Flow || about instanceof Part ) {
                            toCollapse.add( expanded );
                        }
                    }
                }
            } catch ( NotFoundException e ) {
                LOG.warn( "Failed to find expanded " + id );
            }
        }
        for ( Identifiable identifiable : toCollapse ) {
            collapse( identifiable );
        }
    }

    /**
     * Set a component's visibility.
     *
     * @param component a component
     * @param visible   a boolean
     */
    private static void makeVisible( Component component, boolean visible ) {
        component.add(
                new AttributeModifier(
                        "style",
                        true,
                        new Model<String>( visible ? "display:inline" : "display:none" ) ) );
    }

    private void reacquireLocks() {
        // Part is always "expanded"
        getCommander().requestLockOn( getPart() );
        for ( Long id : expansions ) {
            try {
                ModelObject expanded = getQueryService().find( ModelObject.class, id );
                if ( !( expanded instanceof Scenario || expanded instanceof Plan ) )
                    getCommander().requestLockOn( expanded );
            } catch ( NotFoundException e ) {
                LOG.warn( "Expanded model object not found at: " + id );
            }
        }
    }

    private void expand( Identifiable identifiable ) {
        // First collapse any already expanded entity
        if ( identifiable instanceof ModelObject
                && ( (ModelObject) identifiable ).isEntity() ) {
            ModelObject entity = findExpandedEntity();
            if ( entity != null ) {
                expansions.remove( entity.getId() );
                entityAspect = getEntityPanelAspect();
                getCommander().releaseAnyLockOn( entity );
                expandedEntities.add( 0, new EntityExpansion( entityAspect, entity.getId() ) );
            }
            // expandedEntities.remove( identifiable.getId() );
        }
        // Never lock a scenario or plan
        if ( !( identifiable instanceof Scenario || identifiable instanceof Plan ) ) {
            getCommander().requestLockOn( identifiable );
        }
        expansions.add( identifiable.getId() );
    }

    private void collapse( Identifiable identifiable ) {
        if ( identifiable instanceof ModelObject
                && ( (ModelObject) identifiable ).isEntity() ) {
            if ( !expandedEntities.isEmpty() ) {
                EntityExpansion entityExpansion = expandedEntities.remove( 0 );
                getCommander().requestLockOn( entityExpansion.getEntityId() );
                expansions.add( entityExpansion.getEntityId() );
                entityAspect = entityExpansion.getAspect();
            }
        }
        getCommander().releaseAnyLockOn( identifiable );
        expansions.remove( identifiable.getId() );
    }

    private String getEntityPanelAspect() {
        return entityPanel instanceof EntityPanel ? ( (EntityPanel) entityPanel ).getAspectShown()
                : EntityPanel.DETAILS;
    }


    /**
     * {@inheritDoc}
     */
    public void changed( Change change ) {
        if ( change.isNone() ) return;

        Identifiable identifiable = change.getSubject();
        if ( change.isCollapsed() || change.isRemoved() )
            collapse( identifiable );
        else if ( change.isExpanded() || change.isAdded() )
            expand( identifiable );

        if ( identifiable instanceof Scenario ) {
            if ( change.isExists() ) {
                getCommander().resetUserHistory( user.getUsername(), false );
                if ( change.isAdded() ) {
                    setScenario( (Scenario) identifiable );
                    setPart( null );
                } else {
                    assert change.isRemoved();
                    collapseScenarioObjects();
                    setScenario( null );
                }
            } else if ( change.isRecomposed() ) {
                collapseScenarioObjects();
                setPart( getPart() );
            } else if ( change.isSelected() ) {
                collapseScenarioObjects();
                setScenario( (Scenario) identifiable );
                setPart( null );
            }
        } else if ( identifiable instanceof Part ) {
            if ( change.isAdded() || change.isSelected() ) {
                collapse( getPart() );
                collapsePartObjects();
                setPart( (Part) identifiable );
                expand( getPart() );
            } else if ( change.isRemoved() ) {
                collapse( getPart() );
                collapsePartObjects();
                setPart( null );
                expand( getPart() );
            }
        } else if ( identifiable instanceof Flow ) {
            if ( change.isUpdated() && change.getProperty().equals( "other" ) ) {
                expand( identifiable );
            } else if ( change.isSelected() ) {
                Flow flow = (Flow) identifiable;
                expand( identifiable );
                if ( flow.getScenario() != scenario ) {
                    setScenario( flow.getScenario() );
                }
                setPart( flow.getLocalPart() );
            }
        } else if ( identifiable instanceof UserIssue && change.isAdded() ) {
            UserIssue userIssue = (UserIssue) identifiable;
            ModelObject mo = userIssue.getAbout();
            if ( mo instanceof Scenario ) {
                expand( identifiable );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        refreshMenus( target );
        if ( change.isNone() ) return;
        Identifiable identifiable = change.getSubject();
        if ( change.isUndoing()
                || change.isUnknown()
                || ( identifiable instanceof ModelObject
                && change.isUpdated()
                && change.getProperty().equals( "waivedIssueDetections" ) ) ) {
            refreshAll( target );
        }
        if ( change.isUpdated() ) {
            refreshAll( target );
        } else {

            if ( identifiable instanceof Plan ) {
                if ( change.isDisplay() ) {
                    addPlanEditPanel();
                    target.addComponent( planEditPanel );
                } else if ( change.isSelected() ) {
                    redirectToPlan();
                }
            }
            if ( identifiable instanceof Scenario ) {
                if ( change.isDisplay() ) {
                    scenarioPanel.resetScenarioEditPanel( target );
                }
                if ( change.isAdded() || change.isSelected() ) {
                    refreshAll( target );
                } else if ( change.isRemoved() ) {
                    refreshAll( target );
                } else if ( change.isRecomposed() ) {
                    annotateScenarioName();
                    target.addComponent( scenarioNameLabel );
                    scenarioPanel.refresh( target );
                    target.addComponent( scenarioPanel );
                }
            }
            if ( identifiable instanceof Part ) {
                if ( change.isExists() ) {
                    scenarioPanel.refresh( target );
                    target.addComponent( scenarioPanel );
                } else if ( change.isSelected() ) {
                    // In case selecting the part switched scenarios
                    target.addComponent( scenarioDropDownChoice );
                    scenarioPanel.refresh( target );
                    target.addComponent( scenarioPanel );
                }
            }
            if ( identifiable instanceof Flow ) {
                if ( !change.isDisplay() ) refreshAll( target );
            }
            if ( identifiable instanceof ExternalFlow && !change.isDisplay() ) {
                target.addComponent( planEditPanel );
            }
            if ( identifiable instanceof ScenarioObject
                    || identifiable instanceof Issue
                    && ( (Issue) identifiable ).getAbout().getId() == scenario.getId() ) {
                annotateScenarioName();
                target.addComponent( scenarioNameLabel );
            }
            if ( identifiable instanceof Issue
                    && change.isExists()
                    && ( (Issue) identifiable ).getAbout().getId() == scenario.getId() ) {
                annotateScenarioName();
                target.addComponent( scenarioNameLabel );
                scenarioPanel.expandScenarioEditPanel( target );
                addPlanEditPanel();
                target.addComponent( planEditPanel );
            }
            if ( identifiable instanceof ModelObject
                    && ( (ModelObject) identifiable ).isEntity() ) {
                if ( change.isDisplay() ) {
                    addEntityPanel();
                    target.addComponent( entityPanel );
                } else {
                    refreshAll( target );
                }

            }
        }
        if ( change.getScript() != null ) {
            target.appendJavascript( change.getScript() );
        }
    }

    private void refreshMenus( AjaxRequestTarget target ) {
        addPlanActionsMenu();
        target.addComponent( planActionsMenu );
        target.addComponent( planShowMenu );
        scenarioPanel.refreshMenus( target );
        if ( planEditPanel instanceof PlanEditPanel )
            ( (PlanEditPanel) planEditPanel ).refreshMenus( target );
        if ( entityPanel instanceof EntityPanel )
            ( (EntityPanel) entityPanel ).refreshMenus( target );
    }

    private void refreshAll( AjaxRequestTarget target ) {
        // Re-acquire lock
        // setPart( getPart() );
        updateVisibility();
        target.addComponent( planActionsMenu );
        target.addComponent( planShowMenu );
        target.addComponent( scenarioNameLabel );
        target.addComponent( scenarioDescriptionLabel );
        target.addComponent( selectScenarioContainer );
        annotateScenarioName();
        target.addComponent( scenarioNameLabel );
        scenarioPanel.refreshScenarioEditPanel( target );
        scenarioPanel.refresh( target );
        target.addComponent( scenarioPanel );
        if ( entityPanel instanceof EntityPanel )
            ( (EntityPanel) entityPanel ).refresh( target );
        target.addComponent( entityPanel );
        if ( planEditPanel instanceof PlanEditPanel )
            ( (PlanEditPanel) planEditPanel ).refresh( target );
        addPartsMapLink();
        target.addComponent( partsMapLink );
        updateRefreshNotice();
        target.addComponent( refreshNeededContainer );
        getCommander().clearTimeOut();
    }

    private void updateVisibility() {
        makeVisible( selectScenarioContainer, getAllScenarios().size() > 1 );
        makeVisible( switchPlanContainer, getWritablePlans().size() > 1 );
        makeVisible( adminContainer, user.isAdmin() );
    }

    /**
     * Get all plans that the current can modify.
     *
     * @return a list of plans
     */
    public List<Plan> getWritablePlans() {
        return user.getWritablePlans( getPlanManager() );
    }

    private PlanManager getPlanManager() {
        return planManager;
    }

    private void update( AjaxRequestTarget target, Change change ) {
        changed( change );
        updateWith( target, change );
    }

    /**
     * Get read-only expansions.
     *
     * @return a read-only set of Longs
     */
    private Set<Long> getReadOnlyExpansions() {
        return Collections.unmodifiableSet( expansions );
    }

    /**
     * Open scenario import dialog.
     *
     * @param target an ajax request target
     */
    public void importScenario( AjaxRequestTarget target ) {
        scenarioImportPanel.open( target );
    }

    /**
     * Entity expansion record.
     */
    private class EntityExpansion implements Serializable {

        private String aspect = "details";
        private Long entityId;

        private EntityExpansion( String apsect, Long entityId ) {
            this.aspect = apsect;
            this.entityId = entityId;
        }

        public String getAspect() {
            return aspect;
        }

        public Long getEntityId() {
            return entityId;
        }

    }
}

