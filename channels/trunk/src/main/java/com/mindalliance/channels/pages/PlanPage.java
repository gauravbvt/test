package com.mindalliance.channels.pages;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.SurveyService;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.ScenarioObject;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.model.UserIssue;
import com.mindalliance.channels.pages.components.FlowCommitmentsPanel;
import com.mindalliance.channels.pages.components.FlowEOIsPanel;
import com.mindalliance.channels.pages.components.GeomapLinkPanel;
import com.mindalliance.channels.pages.components.IndicatorAwareForm;
import com.mindalliance.channels.pages.components.PartAssignmentsPanel;
import com.mindalliance.channels.pages.components.ScenarioImportPanel;
import com.mindalliance.channels.pages.components.ScenarioLink;
import com.mindalliance.channels.pages.components.ScenarioPanel;
import com.mindalliance.channels.pages.components.entities.EntityPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.menus.PlanActionsMenuPanel;
import com.mindalliance.channels.pages.components.menus.PlanShowMenuPanel;
import com.mindalliance.channels.pages.components.plan.PlanEditPanel;
import com.mindalliance.channels.pages.components.scenario.ScenarioEditPanel;
import com.mindalliance.channels.pages.components.surveys.SurveysPanel;
import com.mindalliance.channels.surveys.Survey;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * The plan's home page.
 * Note: When a user switches plan, this page *must* be reloaded.
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
     * The plan manager.
     */
    @SpringBean
    private PlanManager planManager;

    /**
     * Id of components that are expanded.
     */
    private Set<Long> expansions = new HashSet<Long>();
    /**
     * Aspects shown.
     */
    private Map<Long, List<String>> aspects = new HashMap<Long, List<String>>();
    /**
     * Page history.
     */
    private List<PageState> pageHistory = new ArrayList<PageState>();
    /**
     * Page history cursor.
     */
    private int historyCursor = -1;
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
     * Scenario edit panel.
     */
    private Component scenarioEditPanel;
    /**
     * The scenario panel.
     */
    private ScenarioPanel scenarioPanel;

    /**
     * The entity panel.
     */
    private Component entityPanel;
    /**
     * The assignments panel.
     */
    private Component assignmentsPanel;
    /**
     * The commitments panel.
     */
    private Component commitmentsPanel;
    /**
     * The flow EOIs panel.
     */
    private Component eoisPanel;
    /**
     * The scenarios map panel.
     */
    private Component planEditPanel;
    /**
     * The surveys panel.
     */
    private Component surveysPanel;
    /**
     * The aspect for entity panel.
     */
    // private String entityAspect = EntityPanel.DETAILS;
    /**
     * Refresh button container.
     */
    private WebMarkupContainer refreshNeededContainer;
    /**
     * Go back button container.
     */
    private WebMarkupContainer goBackContainer;
    /**
     * Go forward button container.
     */
    private WebMarkupContainer goForwardContainer;
    /**
     * When last refreshed.
     */
    private long lastRefreshed = System.currentTimeMillis();
    /**
     * Modal dialog window.
     */
    private ModalWindow dialogWindow;

    @SpringBean
    /**
     * Query service.
     */
    private QueryService queryService;
    /**
     * Survey service.
     */
    @SpringBean
    private SurveyService surveyService;

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
        init( sc, findPart( sc, parameters ), findExpansions( parameters ) );
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
        getCommander().releaseAllLocks( getUser().getUsername() );
        setScenario( sc );
        setPart( p );
        expansions = expanded;
        for ( Long id : expansions ) {
            getCommander().requestLockOn( id );
        }

        setVersioned( false );
        add( new Label( "sc-title", new Model<String>( "Channels: " + getPlan().getVersionedName() ) ) );

        form = new IndicatorAwareForm( "big-form" ) {
            @Override
            protected void onSubmit() {
                // Drop user history on submit
                getCommander().resetUserHistory( getUser().getUsername(), true );
                redirectHere();
            }
        };
        addHeader();
        addRefresh();
        addGoBackAndForward();
        getCommander().resynced();
        addPlanMenubar();
        addScenarioSelector();
        addPlanSwitcher();
        addModalDialog();
        addScenarioImportDialog();
        addScenarioPanel();
        addEntityPanel();
        addAssignmentsPanel();
        addCommitmentsPanel();
        addEOIsPanel();
        addScenarioEditPanel();
        addPlanEditPanel();
        addSurveysPanel( null );
        add( form );
        updateSelectorsVisibility();
        updateNavigation();
        LOG.debug( "Scenario page generated" );
        rememberState();
    }

    /**
     * Get aspect of scenario shown.
     *
     * @return a string
     */
    public String getScenarioAspect() {
        return getAspectShown( getScenario() );
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
        form.addOrReplace( createPartsMapLink() );

        // Scenario description
        scenarioDescriptionLabel = new Label( "sc-desc",                                  // NON-NLS
                new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        return StringUtils.abbreviate(
                                StringUtils.capitalize(
                                        scenario.getPhaseEventTitle() ),
                                SCENARIO_DESCRIPTION_MAX_LENGTH );
                    }
                }
        );
        scenarioDescriptionLabel.setOutputMarkupId( true );
        form.add( scenarioDescriptionLabel );
        form.add( new Label( "user", getUser().getUsername() ) );                              // NON-NLS
    }

    private void addScenarioImportDialog() {
        scenarioImportPanel = new ScenarioImportPanel( "scenario-import" );
        form.add( scenarioImportPanel );
    }

    private void addScenarioPanel() {
        scenarioPanel = new ScenarioPanel(
                "scenario",
                new PropertyModel<Scenario>( this, "scenario" ),
                new PropertyModel<Part>( this, "part" ),
                getReadOnlyExpansions()
        );
        form.add( scenarioPanel );
    }

    private GeomapLinkPanel createPartsMapLink() {
        List<GeoLocatable> geoLocatables = new ArrayList<GeoLocatable>();
        for ( Iterator<Part> parts = scenario.parts(); parts.hasNext(); )
            geoLocatables.add( parts.next() );

        GeomapLinkPanel panel = new GeomapLinkPanel( "geomapLink",
                new Model<String>( "Tasks with known locations in scenario " + scenario.getName() ),
                geoLocatables,
                new Model<String>( "Show parts in map" ) );

        panel.setOutputMarkupId( true );
        partsMapLink = panel;
        return panel;
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

    private void addGoBackAndForward() {
        goBackContainer = new WebMarkupContainer( "goBack" );
        goBackContainer.setOutputMarkupId( true );
        goBackContainer.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                goBack( target );
            }
        } );
        form.add( goBackContainer );
        goForwardContainer = new WebMarkupContainer( "goForward" );
        goForwardContainer.setOutputMarkupId( true );
        goForwardContainer.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                goForward( target );
            }
        } );
        form.add( goForwardContainer );
    }

    private void doTimedUpdate( AjaxRequestTarget target ) {
        if ( getCommander().isOutOfSync() ) {
            if ( !dialogWindow.isShown() ) dialogWindow.show( target );
        }
        getCommander().keepAlive( User.current().getUsername(), REFRESH_DELAY );
        getCommander().processDeaths();
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
        makeVisible( refreshNeededContainer, !reasonsToRefresh.isEmpty() );
        refreshNeededContainer.add(
                new AttributeModifier( "title", true,
                        new Model<String>( "Refresh:" + reasonsToRefresh ) ) );
    }

    private String getReasonsToRefresh() {
        String reasons = "";
        String lastModifier = getCommander().getLastModifier();
        long lastModified = getCommander().getLastModified();
        if ( lastModified > lastRefreshed && !lastModifier.isEmpty()
                && !lastModifier.equals( getUser().getUsername() ) )
            reasons = " -- Plan was modified by " + lastModifier;

        // Find expansions that were locked and are not unlocked
        for ( ModelObject mo : getEditableModelObjects( expansions ) ) {
            if ( !( mo instanceof Scenario || mo instanceof Plan ) && getCommander().isUnlocked( mo ) ) {
                reasons += " -- " + mo.getName() + " can now be edited.";
            }
        }
        return reasons;
    }

    private Set<ModelObject> getEditableModelObjects( Set<Long> expansions ) {

        Set<ModelObject> editables = new HashSet<ModelObject>();
        for ( Long id : expansions ) {
            try {
                editables.add( queryService.find( ModelObject.class, id ) );
            } catch ( NotFoundException ignored ) {
                // ignore
            }
        }
        return editables;
    }

    private void annotateScenarioName() {
        Analyst analyst = getApp().getAnalyst();
        String issue = analyst.getIssuesSummary( scenario, Analyst.INCLUDE_PROPERTY_SPECIFIC );
        scenarioNameLabel.add(
                new AttributeModifier( "class", true,                                     // NON-NLS
                        new Model<String>( issue.isEmpty() ? "no-error" : "error" ) ) );  // NON-NLS
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
        form.add( new Label( "username", getUser().getUsername() ) );
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
                new PropertyModel<List<? extends Plan>>( this, "plannablePlans" ) );
        planDropDownChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Selected, getPlan() ) );
            }
        } );
        switchPlanContainer.add( planDropDownChoice );
    }

    private void addModalDialog() {
        dialogWindow = new ModalWindow( "dialog" );
        dialogWindow.setResizable( false );
        dialogWindow.setContent( new DialogPanel(
                dialogWindow.getContentId(),
                new Model<String>(
                        "There is a new version of the plan. "
                                + "Closing this alert will switch you to it." ) ) );
        dialogWindow.setTitle( "Alert" );
        dialogWindow.setCookieName( "refresh-alert" );
        dialogWindow.setCloseButtonCallback( new ModalWindow.CloseButtonCallback() {
            public boolean onCloseButtonClicked( AjaxRequestTarget target ) {
                return true;
            }
        } );
        dialogWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback() {
            public void onClose( AjaxRequestTarget target ) {
                redirectToPlan();
            }
        } );
        dialogWindow.setHeightUnit( "px" );
        dialogWindow.setInitialHeight( 100 );
        dialogWindow.setInitialWidth( 400 );
        add( dialogWindow );
    }

    private void addEntityPanel() {
        ModelEntity entity = findExpandedEntity();
        if ( entity == null ) {
            entityPanel = new Label( "entity", "" );
            entityPanel.setOutputMarkupId( true );
            makeVisible( entityPanel, false );
        } else {
            entityPanel = new EntityPanel(
                    "entity",
                    new Model<ModelEntity>( entity ),
                    getReadOnlyExpansions(),
                    getAspectShown( entity ) );
        }
        form.addOrReplace( entityPanel );
    }

    private void addAssignmentsPanel() {
        Part partViewed = getPartViewed( "assignments" );
        if ( partViewed == null ) {
            assignmentsPanel = new Label( "assignments", "" );
            assignmentsPanel.setOutputMarkupId( true );
            makeVisible( assignmentsPanel, false );
        } else {
            assignmentsPanel = new PartAssignmentsPanel(
                    "assignments",
                    new Model<Part>( getPartViewed( "assignments" ) ),
                    getReadOnlyExpansions()
            );
        }
        form.addOrReplace( assignmentsPanel );
    }

    private void addCommitmentsPanel() {
        Flow flowViewed = getFlowViewed( "commitments" );
        if ( flowViewed == null ) {
            commitmentsPanel = new Label( "commitments", "" );
            commitmentsPanel.setOutputMarkupId( true );
            makeVisible( commitmentsPanel, false );
        } else {
            commitmentsPanel = new FlowCommitmentsPanel(
                    "commitments",
                    new Model<Flow>( getFlowViewed( "commitments" ) ),
                    getReadOnlyExpansions()
            );
        }
        form.addOrReplace( commitmentsPanel );
    }

    private void addEOIsPanel() {
        Flow flowViewed = getFlowViewed( "eois" );
        if ( flowViewed == null ) {
            eoisPanel = new Label( "eois", "" );
            eoisPanel.setOutputMarkupId( true );
            makeVisible( eoisPanel, false );
        } else {
            eoisPanel = new FlowEOIsPanel(
                    "eois",
                    new Model<Flow>( getFlowViewed( "eois" ) ),
                    getReadOnlyExpansions()
            );
        }
        form.addOrReplace( eoisPanel );
    }

    /**
     * Add scenario-related components.
     */
    private void addScenarioEditPanel() {
        boolean showScenarioEdit = expansions.contains( getScenario().getId() );
        if ( showScenarioEdit ) {
            scenarioEditPanel = new ScenarioEditPanel(
                    "sc-editor",                                                              // NON-NLS
                    new PropertyModel<Scenario>( this, "scenario" ),
                    getReadOnlyExpansions(),
                    getAspectShown( getScenario() ) );
        } else {
            scenarioEditPanel = new Label( "sc-editor", "" );
            scenarioEditPanel.setOutputMarkupId( true );
            makeVisible( scenarioEditPanel, false );
        }
        form.addOrReplace( scenarioEditPanel );
    }



    // Return the (presumably) only aspect shown, if any.

    private String getAspectShown( Identifiable identifiable ) {
        String aspectShown = null;
        List<String> aspectsShown = aspects.get( identifiable.getId() );
        if ( aspectsShown != null && !aspectsShown.isEmpty() ) {
            aspectShown = aspectsShown.get( 0 );
        }
        return aspectShown;
    }

    private void addPlanEditPanel() {
        Plan plan = getPlan();
        boolean showPlanEdit = expansions.contains( plan.getId() );
        if ( showPlanEdit ) {
            planEditPanel = new PlanEditPanel( "plan",
                    new Model<Plan>( plan ),
                    getReadOnlyExpansions(),
                    getAspectShown( plan ) );
        } else {
            planEditPanel = new Label( "plan", "" );
            planEditPanel.setOutputMarkupId( true );
            makeVisible( planEditPanel, false );
        }
        form.addOrReplace( planEditPanel );
    }

    private void addSurveysPanel( Survey survey ) {
        boolean showSurveys = expansions.contains( surveyService.getId() );
        if ( showSurveys ) {
            surveysPanel = new SurveysPanel( "surveys", survey,
                    getReadOnlyExpansions() );

        } else {
            surveysPanel = new Label( "surveys", "" );
            surveysPanel.setOutputMarkupId( true );
            makeVisible( surveysPanel, false );
        }
        form.addOrReplace( surveysPanel );
    }

    /**
     * Return current plan.
     *
     * @return a plan
     */
    public Plan getPlan() {
        return getUser().getPlan();
    }

    /**
     * Switch the user's current plan.
     *
     * @param plan a plan
     */
    public void setPlan( Plan plan ) {
        getUser().switchPlan( plan );
    }

    private ModelEntity findExpandedEntity() {
        for ( long id : expansions ) {
            try {
                ModelObject mo = queryService.find( ModelObject.class, id );
                if ( mo.isEntity() ) return (ModelEntity) mo;
            }
            catch ( NotFoundException ignored ) {
                // ignore
            }
        }

        return null;
    }

    public List<Scenario> getAllScenarios() {
        List<Scenario> allScenarios = new ArrayList<Scenario>( queryService.list( Scenario.class ) );
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
                                "/plan?scenario={0,number,0}&part={1,number,0}{2}",            // NON-NLS
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
        return queryService;
    }

    private Channels getApp() {
        return (Channels) getApplication();
    }

    private Commander getCommander() {
        // return Channels.instance().getCommander();
        return getApp().getCommander();
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
        if ( part != null ) {
            collapse( part );
            collapsePartObjects();
        }
        part = p;
        if ( part == null ) part = scenario.getDefaultPart();
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
        if ( scenario != null && !scenario.equals( sc ) ) {
            collapseScenarioObjects();
        }
        scenario = sc;
        if ( scenario == null )
            scenario = getQueryService().getDefaultScenario();
        if ( !getPart().getScenario().equals( scenario ) ) {
            setPart( scenario.getDefaultPart() );
        }
    }

    /**
     * Set a component's visibility.
     *
     * @param component a component
     * @param visible   a boolean
     */
    private static void makeVisible( Component component, boolean visible ) {
        component.add( new AttributeModifier( "style",
                true,
                new Model<String>( visible ? "" : "display:none" ) ) );
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
        // Never lock a scenario or plan, or anything in a production plan
        if ( getPlan().isDevelopment()
                && identifiable instanceof ModelObject
                && ( (ModelObject) identifiable ).isLockable() ) {
            getCommander().requestLockOn( identifiable );
        }
        if ( identifiable instanceof ModelObject && ( (ModelObject) identifiable ).isEntity() ) {
            // ModelObject entity = (ModelObject) identifiable;
            ModelObject previous = findExpandedEntity();
            if ( previous != null ) {
                /*String previousAspect = getAspectShown( previous );
                viewAspect( entity, previousAspect );*/
                collapse( previous );
            }
        }
        expansions.add( identifiable.getId() );
    }

    private void collapse( Identifiable identifiable ) {
        getCommander().releaseAnyLockOn( identifiable );
        expansions.remove( identifiable.getId() );
        // Close aspects of collapsed object
        if ( identifiable instanceof Flow ) {
            closeAspect( identifiable, "eois" );
        } else if ( !( identifiable instanceof Part ) )
            closeAspect( identifiable, null );
    }

    private void viewAspect( Identifiable identifiable, String aspect ) {
        if ( aspect == null || aspect.isEmpty() ) {
            aspects.remove( identifiable.getId() );
        } else {
            List<String> aspectsShown = aspects.get( identifiable.getId() );
            if ( aspectsShown == null ) aspectsShown = new ArrayList<String>();
            if ( !aspectsShown.contains( aspect ) )
                aspectsShown.add( aspect );
            aspects.put( identifiable.getId(), aspectsShown );
        }
    }

    private void closeAspect( Identifiable identifiable, String aspect ) {
        if ( aspect == null || aspect.isEmpty() ) {
            aspects.remove( identifiable.getId() );
        } else {
            List<String> aspectsShown = aspects.get( identifiable.getId() );
            if ( aspectsShown != null ) {
                aspectsShown.remove( aspect );
            }
        }
    }

    /**
     * Get part which given aspect is viewed
     *
     * @param aspect a string
     * @return a part or null
     */
    private Part getPartViewed( final String aspect ) {
        Part partViewed = null;
        Long partId = findIdForAspectViewed( Part.class, aspect );
        if ( partId != null ) {
            try {
                partViewed = getQueryService().find( Part.class, partId );
            } catch ( NotFoundException e ) {
                LOG.warn( "Part not found at " + partId );
            }
        }
        return partViewed;
    }

    /**
     * Get flow which given aspect is viewed
     *
     * @param aspect a string
     * @return a flow or null
     */
    private Flow getFlowViewed( final String aspect ) {
        Flow flowViewed = null;
        Long flowId = findIdForAspectViewed( Flow.class, aspect );
        if ( flowId != null ) {
            try {
                flowViewed = getQueryService().find( Flow.class, flowId );
            } catch ( NotFoundException e ) {
                LOG.warn( "Flow not found at " + flowId );
            }
        }
        return flowViewed;
    }

    // Assumes that only instance of clazz can view the given aspect at a time.
    private Long findIdForAspectViewed( final Class<? extends ModelObject> clazz, final String aspect ) {
        return (Long) CollectionUtils.find(
                aspects.keySet(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        Long id = (Long) obj;
                        try {
                            getQueryService().find( clazz, id );
                        } catch ( NotFoundException e ) {
                            return false;
                        }
                        return aspects.get( id ).contains( aspect );
                    }
                }
        );
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
     * {@inheritDoc}
     */
    public void changed( Change change ) {
        if ( change.isNone() ) return;
        Identifiable identifiable = change.getSubject();
        if ( !( identifiable instanceof Survey ) ) {
            if ( change.isCollapsed() || change.isRemoved() )
                collapse( identifiable );
            else if ( change.isExpanded() || change.isAdded() )
                expand( identifiable );
            else if ( change.isAspectViewed() ) {
                if ( change.getSubject() instanceof Flow ) {
                    Flow otherFlowViewed = getFlowViewed( change.getProperty() );
                    if ( otherFlowViewed != null )
                        closeAspect( otherFlowViewed, change.getProperty() );
                }
                viewAspect( identifiable, change.getProperty() );
            } else if ( change.isAspectClosed() )
                closeAspect( identifiable, change.getProperty() );
            else if ( change.isAspectReplaced() ) {
                closeAspect( identifiable, null );
                viewAspect( identifiable, change.getProperty() );
            }
        }
        if ( identifiable instanceof Survey ) {
            if ( change.isExpanded() ) {
                expand( surveyService );
            }
        }
        if ( identifiable instanceof Scenario ) {
            if ( change.isExists() ) {
                getCommander().resetUserHistory( getUser().getUsername(), false );
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
        rememberState();
    }

    /**
     * {@inheritDoc}
     */
    /*
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        refreshMenus( target );
        updateNavigation( target );
        if ( change.isNone() ) return;
        Identifiable identifiable = change.getSubject();
        if ( change.isUndoing()
                || change.isUnknown()
                || ( identifiable instanceof ModelObject
                && change.isUpdated()
                && change.isForProperty( "waivedIssueDetections" ) ) ) {
            refreshAll( target );
        }
        if ( change.isUpdated() ) {
            refreshAll( target );
        } else {
            if ( identifiable instanceof SurveyService ) {
                if ( change.isDisplay() ) {
                    refreshSurveysPanel( target, null );
                }
            }
            if ( identifiable instanceof Survey ) {
                if ( change.isExpanded() ) {
                    refreshSurveysPanel( target, (Survey) identifiable );
                }
            }
            if ( identifiable instanceof Plan ) {
                if ( change.isDisplay() ) {
                    refreshPlanEditPanel( target );
                } else if ( change.isSelected() || change.isRecomposed() ) {
                    redirectToPlan();
                } else if ( change.isExists() && change.isForProperty( "phases" ) ) {
                    refreshScenarioEditPanel( target );
                }
            }
            if ( identifiable instanceof Scenario ) {
                if ( change.isDisplay() ) {
                    refreshScenarioEditPanel( target );
                }
                if ( change.isAdded() || change.isSelected() ) {
                    refreshAll( target );
                } else if ( change.isRemoved() ) {
                    refreshAll( target );
                } else if ( change.isRecomposed() ) {
                    annotateScenarioName( getApp().getAnalyst() );
                    target.addComponent( scenarioNameLabel );
                    scenarioPanel.refresh( target );
                }
            }
            if ( identifiable instanceof Part ) {
                if ( change.isExists() ) {
                    scenarioPanel.refresh( target );
                } else if ( change.isSelected() ) {
                    // In case selecting the part switched scenarios
                    refreshScenarioEditPanel( target );
                    target.addComponent( scenarioDropDownChoice );
                    scenarioPanel.refresh( target );
                    target.addComponent( scenarioPanel );
                } else if ( change.isAspect( "assignments" ) ) {
                    refreshAssignmentsPanel( target );
                }
            }
            if ( identifiable instanceof Flow ) {
                if ( change.isAspect( "commitments" ) ) {
                    refreshCommitmentsPanel( target );
                } else if ( change.isCollapsed() || change.isAspect( "eois" ) ) {
                    refreshEOIsPanel( target );
                } else if ( !change.isDisplay() ) {
                    refreshAll( target );
                }
            }
            if ( identifiable instanceof ExternalFlow && !change.isDisplay() ) {
                target.addComponent( planEditPanel );
            }
            if ( identifiable instanceof ScenarioObject
                    || identifiable instanceof Issue
                    && ( (Issue) identifiable ).getAbout().getId() == scenario.getId() ) {
                annotateScenarioName( getApp().getAnalyst() );
                target.addComponent( scenarioNameLabel );
            }
            if ( identifiable instanceof Issue
                    && change.isExists()
                    && ( (Issue) identifiable ).getAbout().getId() == scenario.getId() ) {
                annotateScenarioName( getApp().getAnalyst() );
                target.addComponent( scenarioNameLabel );
                scenarioPanel.expandScenarioEditPanel( target );
                refreshPlanEditPanel( target );
                target.addComponent( planEditPanel );
            }
            if ( identifiable instanceof ModelObject
                    && ( (ModelObject) identifiable ).isEntity() ) {
                if ( change.isDisplay() ) {
                    refreshEntityPanel( target );
                } else {
                    refreshAll( target );
                }

            }
        }
        if ( change.getScript() != null ) {
            target.appendJavascript( change.getScript() );
        }
    }
*/
    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( !change.isNone() ) {
            if ( change.getSubject() instanceof Plan && change.isSelected() || change.isRecomposed() ) {
                redirectToPlan();
            } else if ( change.isUndoing() || change.isUnknown() ) {
                refresh( target, change, new ArrayList<Updatable>() );
            } else {
                refresh( target, change, updated );
            }
        }
        if ( change.getScript() != null ) {
            target.appendJavascript( change.getScript() );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void update( AjaxRequestTarget target, Object object, String action ) {
        // Do nothing
    }

    private void refreshAll( AjaxRequestTarget target ) {
        refresh( target, new Change( Change.Type.Unknown ), new ArrayList<Updatable>() );
    }

    /**
     * {@inheritDoc}
     */
    public void refresh( AjaxRequestTarget target, Change change, List<Updatable> updated, String aspect ) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void refresh( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        updateHeaders( target, change );
        refreshPlanMenus( target );
        updateNavigation( target );
        updateRefresh( target );
        updateSelectors( target, change );
        refreshChildren( target, change, updated );
        getCommander().clearTimeOut();
    }

    private void updateRefresh( AjaxRequestTarget target ) {
        updateRefreshNotice();
        target.addComponent( refreshNeededContainer );
    }

    private void updateHeaders( AjaxRequestTarget target, Change change ) {
        annotateScenarioName();
        target.addComponent( scenarioNameLabel );
        target.addComponent( scenarioDescriptionLabel );
        form.addOrReplace( createPartsMapLink() );
        target.addComponent( partsMapLink );
    }

    private void refreshPlanMenus( AjaxRequestTarget target ) {
        addPlanActionsMenu();
        target.addComponent( planActionsMenu );
        target.addComponent( planShowMenu );
    }

    private void updateSelectorsVisibility() {
        makeVisible( selectScenarioContainer, getAllScenarios().size() > 1 );
        makeVisible( switchPlanContainer, getPlannablePlans().size() > 1 );
    }

    private void updateSelectors( AjaxRequestTarget target, Change change ) {
        Identifiable identifiable = change.getSubject();
        if ( identifiable instanceof Part && change.isSelected() ) {
            // In case selecting the part switched scenarios
            target.addComponent( scenarioDropDownChoice );
        }
        updateSelectorsVisibility();
        target.addComponent( selectScenarioContainer );
        target.addComponent( switchPlanContainer );
    }

/*
    private void refreshMenus( AjaxRequestTarget target ) {
        addPlanActionsMenu();
        target.addComponent( planActionsMenu );
        target.addComponent( planShowMenu );
        scenarioPanel.refreshMenus( target );
        if ( planEditPanel instanceof PlanEditPanel )
            ( (PlanEditPanel) planEditPanel ).refreshMenus( target );
        if ( entityPanel instanceof EntityPanel )
            ( (EntityPanel) entityPanel ).refreshMenus( target );
        if ( scenarioEditPanel instanceof ScenarioEditPanel )
            ( (ScenarioEditPanel) scenarioEditPanel ).refreshMenus( target );
    }
*/

    private void refreshChildren( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        refreshPlanEditPanel( target, change, updated );
        refreshScenarioEditPanel( target, change, updated );
        refreshEntityPanel( target, change, updated );
        refreshAssignmentsPanel( target, change, updated );
        refreshCommitmentsPanel( target, change, updated );
        refreshEOIsPanel( target, change, updated );
        refreshSurveysPanel( target, change, updated );
        refreshScenarioPanel( target, change, updated );
    }

    private void refreshScenarioPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject();
        if ( identifiable instanceof Part && ( change.isSelected() || change.isDisplay()) ) {
            scenarioPanel.doRefresh( target, change );
            target.addComponent( scenarioPanel );
        } else {
            scenarioPanel.refresh( target, change, updated, getAspectShown( getScenario() ) );
        }
    }

    private void refreshPlanEditPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject();
        Plan plan = getPlan();
        if ( change.isUnknown() || change.isDisplay() && identifiable instanceof Plan ) {
            addPlanEditPanel();
            target.addComponent( planEditPanel );
        } else if ( planEditPanel instanceof PlanEditPanel ) {
            ( (PlanEditPanel) planEditPanel ).refresh(
                    target,
                    change,
                    updated,
                    getAspectShown( plan ) );
        }
    }

    private void refreshScenarioEditPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isUnknown()
                || change.isDisplay() && change.getSubject() instanceof Scenario
                || change.isSelected() && change.getSubject() instanceof Part ) {
            addScenarioEditPanel();
            target.addComponent( scenarioEditPanel );
            target.addComponent( scenarioDropDownChoice );
        } else if ( scenarioEditPanel instanceof ScenarioEditPanel ) {
            ( (ScenarioEditPanel) scenarioEditPanel ).refresh(
                    target,
                    change,
                    updated,
                    getAspectShown( getScenario() ) );
        }
    }

    private void refreshEntityPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        ModelEntity entity = findExpandedEntity();
        if ( change.isUnknown() || change.isDisplay() && change.getSubject() instanceof ModelEntity ) {
            addEntityPanel();
            target.addComponent( entityPanel );
        } else if ( entityPanel instanceof EntityPanel ) {
            ( (EntityPanel) entityPanel ).refresh(
                    target,
                    change,
                    updated,
                    getAspectShown( entity ) );
        }
    }

    private void refreshAssignmentsPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject();
        if ( change.isUnknown() || identifiable instanceof Part && change.isAspect() ) {
            addAssignmentsPanel();
            target.addComponent( assignmentsPanel );
        } else if ( assignmentsPanel instanceof PartAssignmentsPanel ) {
            ( (PartAssignmentsPanel) assignmentsPanel ).refresh(
                    target,
                    change,
                    updated );
        }
    }

    private void refreshCommitmentsPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject();
        if ( change.isUnknown() || identifiable instanceof Flow && change.isAspect() ) {
            addCommitmentsPanel();
            target.addComponent( commitmentsPanel );
        } else if ( commitmentsPanel instanceof FlowCommitmentsPanel ) {
            ( (FlowCommitmentsPanel) commitmentsPanel ).refresh(
                    target,
                    change,
                    updated );
        }
    }

    private void refreshEOIsPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject();
        if ( change.isUnknown() || identifiable instanceof Flow &&
                ( change.isCollapsed() || change.isAspect( "eois" ) ) ) {
            addEOIsPanel();
            target.addComponent( eoisPanel );
        } else if ( eoisPanel instanceof FlowEOIsPanel ) {
            ( (FlowEOIsPanel) eoisPanel ).refresh( target, change, updated );
        }
    }

    private void refreshSurveysPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject();
        if ( change.isUnknown() || change.isDisplay()
                && ( identifiable instanceof SurveyService || identifiable instanceof Survey ) ) {
            addSurveysPanel( identifiable instanceof Survey ? (Survey) identifiable : null );
            target.addComponent( surveysPanel );
        } else if ( surveysPanel instanceof SurveysPanel ) {
            ( (SurveysPanel) surveysPanel ).refresh(
                    target,
                    change,
                    updated,
                    getAspectShown( surveyService ) );
        }
    }

/*    private void refreshAll( AjaxRequestTarget target ) {
    // Re-acquire lock
    // setPart( getPart() );
    updateVisibility();
    updateNavigation( target );
    target.addComponent( planActionsMenu );
    target.addComponent( planShowMenu );
    target.addComponent( scenarioNameLabel );
    target.addComponent( scenarioDescriptionLabel );
    target.addComponent( selectScenarioContainer );
    annotateScenarioName( getApp().getAnalyst() );
    target.addComponent( scenarioNameLabel );
    refreshScenarioEditPanel( target );
    scenarioPanel.refresh( target );
    target.addComponent( scenarioPanel );
    refreshEntityPanel( target );
    refreshAssignmentsPanel( target );
    refreshCommitmentsPanel( target );
    refreshEOIsPanel( target );
    refreshPlanEditPanel( target );
    target.addComponent( planEditPanel );
    form.addOrReplace( createPartsMapLink() );
    target.addComponent( partsMapLink );
    updateRefreshNotice();
    target.addComponent( refreshNeededContainer );
    getCommander().clearTimeOut();
}*/


    private void updateNavigation() {
        goBackContainer.add( new AttributeModifier(
                "src",
                true,
                new Model<String>( isCanGoBack() ? "images/go_back.png" : "images/go_back_disabled.png" ) ) );
        goBackContainer.add( new AttributeModifier(
                "title",
                true,
                new Model<String>( isCanGoBack() ? "Go back" : "" ) ) );
        goForwardContainer.add( new AttributeModifier(
                "src",
                true,
                new Model<String>( isCanGoForward() ? "images/go_forward.png" : "images/go_forward_disabled.png" ) ) );
        goForwardContainer.add( new AttributeModifier(
                "title",
                true,
                new Model<String>( isCanGoForward() ? "Go forward" : "" ) ) );
    }

    private void updateNavigation( AjaxRequestTarget target ) {
        updateNavigation();
        target.addComponent( goBackContainer );
        target.addComponent( goForwardContainer );
    }

    /**
     * Get all plans that the current can modify.
     *
     * @return a list of plans
     */
    public List<Plan> getPlannablePlans() {
        return getUser().getPlannablePlans( getPlanManager() );
    }

    private User getUser() {
        return User.current();
    }

    private PlanManager getPlanManager() {
        return planManager;
    }

    private void update( AjaxRequestTarget target, Change change ) {
        changed( change );
        updateWith( target, change, new ArrayList<Updatable>() );
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
     * Get read-only aspects.
     *
     * @return a read-only map of Longs to lists of strings
     */
    private Map<Long, List<String>> getReadOnlyAspects() {
        Map<Long, List<String>> copy = new HashMap<Long, List<String>>();
        for ( Long id : aspects.keySet() ) {
            List<String> viewed = aspects.get( id );
            if ( viewed != null )
                copy.put( id, new ArrayList<String>( viewed ) );
        }
        return Collections.unmodifiableMap( copy );
    }

    /**
     * Open scenario import dialog.
     *
     * @param target an ajax request target
     */
    public void importScenario( AjaxRequestTarget target ) {
        scenarioImportPanel.open( target );
    }

    public boolean isCanGoBack() {
        return historyCursor > 0;
    }

    public boolean isCanGoForward() {
        return historyCursor < pageHistory.size() - 1;
    }

    private void rememberState() {
        // Delete any forward state
        int size = pageHistory.size();
        for ( int i = historyCursor + 1; i < size; i++ ) {
            pageHistory.remove( historyCursor + 1 );
        }
        // Add current state to history if different from current one
        PageState newState = new PageState();
        if ( historyCursor < 0 || !pageHistory.get( historyCursor ).equals( newState ) ) {
            pageHistory.add( newState );
            historyCursor = pageHistory.size() - 1;
        }
    }

    private void goBack( AjaxRequestTarget target ) {
        if ( isCanGoBack() ) {
            reinstate( pageHistory.get( --historyCursor ), target );
        }
    }

    private void goForward( AjaxRequestTarget target ) {
        if ( isCanGoForward() ) {
            reinstate( pageHistory.get( ++historyCursor ), target );
        }
    }

    /**
     * Collapse what's no longer expanded, expand what's not yet expanded,
     * change aspects viewed if needed for expanded,
     * set scenario if different and exists, set part if different and exists
     *
     * @param pageState a page state
     * @param target    an ajax request target
     */
    @SuppressWarnings( "unchecked" )
    private void reinstate( PageState pageState, AjaxRequestTarget target ) {
        // Expand what's expanded in page state but not in current expansions
        List<Long> expandSet = (List<Long>) CollectionUtils.subtract( pageState.getExpansions(), expansions );
        // Collapse what's in expansions but not in page state
        List<Long> collapseSet = (List<Long>) CollectionUtils.subtract( expansions, pageState.getExpansions() );
        for ( Long id : expandSet ) {
            try {
                ModelObject toExpand = getQueryService().find( ModelObject.class, id );
                expand( toExpand );
            } catch ( NotFoundException e ) {
                // Do nothing
            }
        }
        for ( Long id : collapseSet ) {
            try {
                ModelObject toCollapse = getQueryService().find( ModelObject.class, id );
                collapse( toCollapse );
            } catch ( NotFoundException e ) {
                // Do nothing
            }
        }
        // Reset aspects
        aspects = new HashMap<Long, List<String>>();
        for ( Long id : pageState.getAspects().keySet() ) {
            try {
                ModelObject viewedObject = getQueryService().find( ModelObject.class, id );
                for ( String previousAspect : pageState.getAspects().get( id ) ) {
                    viewAspect( viewedObject, previousAspect );
                }
            } catch ( NotFoundException e ) {
                // Do nothing
            }

        }
        try {
            Scenario previousScenario = getQueryService().find( Scenario.class, pageState.getScenarioId() );
            if ( !getScenario().equals( previousScenario ) ) {
                setScenario( previousScenario );
            }
            Part previousPart = (Part) getScenario().getNode( pageState.getPartId() );
            if ( !getPart().equals( previousPart ) ) {
                setPart( previousPart );
            }
        } catch ( NotFoundException e ) {
            // Do nothing
        }
        refreshAll( target );
    }


    /**
     * Dialog panel.
     */
    private class DialogPanel extends Panel {

        private DialogPanel( String id, IModel<String> iModel ) {
            super( id, iModel );
            Label alertLabel = new Label( "alert", iModel );
            add( alertLabel );
        }
    }

    /**
     * Page state.
     */
    private class PageState implements Serializable {
        /**
         * Scenario id.
         */
        private long scenarioId;
        /**
         * Part id.
         */
        private long partId;
        /**
         * Expansions
         */
        private Set<Long> expansions;
        /**
         * Aspects viewed.
         */
        private Map<Long, List<String>> aspects;

        private PageState() {
            scenarioId = getScenario().getId();
            partId = getPart().getId();
            this.expansions = new HashSet<Long>( getReadOnlyExpansions() );
            this.aspects = new HashMap<Long, List<String>>( getReadOnlyAspects() );
        }

        public long getScenarioId() {
            return scenarioId;
        }

        public void setScenarioId( long scenarioId ) {
            this.scenarioId = scenarioId;
        }

        public long getPartId() {
            return partId;
        }

        public void setPartId( long partId ) {
            this.partId = partId;
        }

        public Set<Long> getExpansions() {
            return expansions;
        }

        public void setExpansions( Set<Long> expansions ) {
            this.expansions = expansions;
        }

        public Map<Long, List<String>> getAspects() {
            return aspects;
        }

        public void setAspects( Map<Long, List<String>> aspects ) {
            this.aspects = aspects;
        }

        public boolean equals( Object obj ) {
            if ( obj instanceof PageState ) {
                PageState other = (PageState) obj;
                return scenarioId == other.getScenarioId()
                        && partId == other.getPartId()
                        && CollectionUtils.isEqualCollection( expansions, other.getExpansions() )
                        && hasSameAspects( other );
            } else {
                return false;
            }
        }

        private boolean hasSameAspects( PageState other ) {
            Map<Long, List<String>> otherAspects = other.getAspects();
            if ( !CollectionUtils.isEqualCollection(
                    aspects.keySet(),
                    otherAspects.keySet() ) )
                return false;
            for ( Long id : aspects.keySet() ) {
                if ( !CollectionUtils.isEqualCollection(
                        otherAspects.get( id ),
                        aspects.get( id ) ) )
                    return false;
            }
            return true;
        }
    }
}

