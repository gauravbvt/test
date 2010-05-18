package com.mindalliance.channels.pages;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.model.UserIssue;
import com.mindalliance.channels.pages.components.GeomapLinkPanel;
import com.mindalliance.channels.pages.components.IndicatorAwareForm;
import com.mindalliance.channels.pages.components.SegmentImportPanel;
import com.mindalliance.channels.pages.components.SegmentLink;
import com.mindalliance.channels.pages.components.entities.EntityPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.PlanEditPanel;
import com.mindalliance.channels.pages.components.plan.menus.PlanActionsMenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.PlanShowMenuPanel;
import com.mindalliance.channels.pages.components.segment.FailureImpactsPanel;
import com.mindalliance.channels.pages.components.segment.FlowEOIsPanel;
import com.mindalliance.channels.pages.components.segment.MaximizedFlowPanel;
import com.mindalliance.channels.pages.components.segment.PartAssignmentsPanel;
import com.mindalliance.channels.pages.components.segment.SegmentEditPanel;
import com.mindalliance.channels.pages.components.segment.SegmentPanel;
import com.mindalliance.channels.pages.components.segment.SharingCommitmentsPanel;
import com.mindalliance.channels.pages.components.surveys.SurveysPanel;
import com.mindalliance.channels.pages.help.FlowLegendPanel;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.surveys.Survey;
import com.mindalliance.channels.surveys.SurveyService;
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
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
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
     * The 'segment' parameter in the URL.
     */
    static final String SEGMENT_PARM = "segment";                                       // NON-NLS

    /**
     * The 'part' parameter in the URL.
     */
    static final String PART_PARM = "node";                                               // NON-NLS

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanPage.class );

    /**
     * Length a segment title is abbreviated to
     */
    private static final int SEGMENT_TITLE_MAX_LENGTH = 40;

    /**
     * Length a segment title is abbreviated to
     */
    private static final int SEGMENT_DESCRIPTION_MAX_LENGTH = 94;

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
     * Label with name of segment.
     */
    private Label segmentNameLabel;

    /**
     * Link to mapping of parts.
     */
    private GeomapLinkPanel partsMapLink;

    /**
     * Label with description of segment.
     */
    private Label segmentDescriptionLabel;

    /**
     * Container of segment selector.
     */
    private WebMarkupContainer selectSegmentContainer;

    /**
     * Choice of segments.
     */
    private DropDownChoice<Segment> segmentDropDownChoice;

    /**
     * Container of plan switcher.
     */
    private WebMarkupContainer switchPlanContainer;

    /**
     * Segments action menu.
     */
    private MenuPanel planActionsMenu;

    /**
     * Segments show menu.
     */
    private MenuPanel planShowMenu;

    /**
     * The current part.
     */
    private Part part;

    /**
     * The current segment.
     */
    private Segment segment;

    /**
     * The big form -- used for attachments and segment imports only.
     */
    private IndicatorAwareForm form;

    /**
     * Import segment "dialog".
     */
    private SegmentImportPanel segmentImportPanel;

    /**
     * Segment edit panel.
     */
    private Component segmentEditPanel;

    /**
     * The segment panel.
     */
    private SegmentPanel segmentPanel;

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
     * The segments map panel.
     */
    private Component planEditPanel;

    /**
     * The surveys panel.
     */
    private Component surveysPanel;

    /**
     * Failure impacts panel.
     */
    private Component failureImpactsPanel;

    /**
     * The aspect for entity panel.
     */
    // private String entityAspect = EntityPanel.DETAILS;

    /**
     * Refresh button.
     */
    private Component refreshNeededComponent;

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

    /**
     * Maximized flow map panel.
     */
    private Component maximizedFlowPanel;

    /**
     * Whether the flow map is maximized.
     */
    private boolean flowMaximized;
    /**
     * Flows are explained.
     */
    private boolean flowsExplained;
    /**
     * Flow legend panel.
     */
    private Component flowLegendPanel;

    /**
     * Cumulated change to an expanded identifiable.
     */
    private Map<Identifiable, Change> changes = new HashMap<Identifiable, Change>();

    /**
     * Query service.
     */
    @SpringBean
    private QueryService queryService;

    /**
     * Survey service.
     */
    @SpringBean
    private SurveyService surveyService;

    /**
     * The plan manager.
     */
    @SpringBean
    private PlanManager planManager;

    /**
     * Used when page is called without parameters.
     * Set to default segment, default part, all collapsed.
     */
    public PlanPage() {
        this( new PageParameters() );
    }

    public PlanPage( PageParameters parameters ) {
        // Call super to remember parameters in links
        super( parameters );
        Segment sc = findSegment( queryService, parameters );
        init( sc, findPart( sc, parameters ), findExpansions( parameters ) );
    }

    /**
     * Utility constructor for tests.
     *
     * @param segment a segment
     */
    public PlanPage( Segment segment ) {
        this( segment, segment.getDefaultPart() );
    }

    /**
     * Utility constructor for tests.
     *
     * @param sc a segment
     * @param p  a part in the segment
     */
    public PlanPage( Segment sc, Part p ) {
        init( sc, p, new HashSet<Long>() );
    }

    private void init( Segment sc, Part p, Set<Long> expanded ) {
        final Commander commander = getCommander();
        commander.releaseAllLocks( getUser().getUsername() );
        setSegment( sc );
        setPart( p );
        expansions = expanded;
        for ( Long id : expansions ) {
            commander.requestLockOn( id );
        }
        setVersioned( false );

        add( new Label( "sg-title",
                new Model<String>( "Channels: " + getPlan().getVersionedName() ) ) );

        form = new IndicatorAwareForm( "big-form" ) {
            @Override
            protected void onSubmit() {
                // Drop user history on submit
                getCommander().resetUserHistory( getUser().getUsername(), true );
                redirectHere();
            }
        };
        addMaximizedFlowPanel( new Change( Change.Type.None ) );
        addHeader();
        addRefresh();
        addGoBackAndForward();
        commander.resynced();
        addPlanMenubar();
        addSegmentSelector();
        addPlanSwitcher();
        addModalDialog();
        addSegmentImportDialog();
        addSegmentPanel();
        addEntityPanel();
        addAssignmentsPanel();
        addCommitmentsPanel();
        addEOIsPanel();
        addFailureImpactsPanel();
        addSegmentEditPanel();
        addPlanEditPanel();
        addSurveysPanel( null );
        addFlowLegendPanel();
        add( form );

        updateSelectorsVisibility();
        updateNavigation();
        LOG.debug( "Segment page generated" );
        rememberState();
    }

    private void addMaximizedFlowPanel( Change change ) {
        if ( flowMaximized ) {
            maximizedFlowPanel = new MaximizedFlowPanel(
                    "maximized-flow",
                    new PropertyModel<Part>( this, "part" ),
                    change.isForProperty( "showGoals" ) );
        } else {
            maximizedFlowPanel = new Label( "maximized-flow" );
        }
        maximizedFlowPanel.setOutputMarkupId( true );
        makeVisible( maximizedFlowPanel, flowMaximized );
        form.addOrReplace( maximizedFlowPanel );
    }

    private void addFlowLegendPanel() {
        if ( !flowsExplained ) {
            flowLegendPanel = new Label( "flow-legend", "" );
            flowLegendPanel.setOutputMarkupId( true );
            makeVisible( flowLegendPanel, false );
        } else {
            flowLegendPanel = new FlowLegendPanel(
                    "flow-legend",
                    new Model<Segment>( getSegment() ) );
        }
        form.addOrReplace( flowLegendPanel );
    }


    /**
     * Get aspect of segment shown.
     *
     * @return a string
     */
    public String getSegmentAspect() {
        return getAspectShown( getSegment() );
    }

    private void addHeader() {
        segmentNameLabel = new Label( "header",
                new AbstractReadOnlyModel() {
                    @Override
                    public Object getObject() {
                        return StringUtils.abbreviate( segment.getName(),
                                SEGMENT_TITLE_MAX_LENGTH );
                    }
                } );
        segmentNameLabel.setOutputMarkupId( true );

        // Add style mods from analyst.
        annotateSegmentName();
        form.add( segmentNameLabel );

        // Add link to map of parts
        form.addOrReplace( createPartsMapLink() );

        // Segment description
        segmentDescriptionLabel = new Label( "sg-desc",                                  // NON-NLS
                new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        return StringUtils.abbreviate( StringUtils.capitalize(
                                segment.getPhaseEventTitle() ),
                                SEGMENT_DESCRIPTION_MAX_LENGTH );
                    }
                } );
        segmentDescriptionLabel.setOutputMarkupId( true );
        form.add( segmentDescriptionLabel );
        form.add( new Label( "user",
                getUser().getUsername() ) );                              // NON-NLS
    }

    private void addSegmentImportDialog() {
        segmentImportPanel = new SegmentImportPanel( "segment-import" );
        form.add( segmentImportPanel );
    }

    private void addSegmentPanel() {
        segmentPanel = new SegmentPanel( "segment",
                new PropertyModel<Segment>( this, "segment" ),
                new PropertyModel<Part>( this, "part" ),
                getReadOnlyExpansions() );
        form.add( segmentPanel );
    }

    private GeomapLinkPanel createPartsMapLink() {
        List<GeoLocatable> geoLocatables = new ArrayList<GeoLocatable>();
        for ( Iterator<Part> parts = segment.parts(); parts.hasNext(); )
            geoLocatables.add( parts.next() );
        GeomapLinkPanel panel = new GeomapLinkPanel( "geomapLink",
                new Model<String>(
                        "Tasks with known locations in plan segment "
                                + segment.getName() ),
                geoLocatables,
                new Model<String>( "Show tasks in map" ) );
        panel.setOutputMarkupId( true );
        partsMapLink = panel;
        return panel;
    }

    private void addRefresh() {
        refreshNeededComponent = new AjaxFallbackLink( "refresh-needed" ) {
            public void onClick( AjaxRequestTarget target ) {
                getCommander().clearTimeOut();
                reacquireLocks();
                lastRefreshed = System.currentTimeMillis();
                refreshAll( target );
            }
        };
        refreshNeededComponent.setOutputMarkupId( true );
        // Put timer on form since it is never updated or replaced
        form.add( new AbstractAjaxTimerBehavior( Duration.seconds( REFRESH_DELAY ) ) {
            @Override
            protected void onTimer( AjaxRequestTarget target ) {
                doTimedUpdate( target );
            }
        } );
        form.add( refreshNeededComponent );
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
            if ( !dialogWindow.isShown() )
                dialogWindow.show( target );
        }
        getCommander().keepAlive( User.current().getUsername(), REFRESH_DELAY );
        getCommander().processDeaths();
        getCommander().processTimeOuts();
        if ( getCommander().isTimedOut() ) {
            refreshAll( target );
        } else {
            updateRefreshNotice();
            target.addComponent( refreshNeededComponent );
        }
    }

    private void updateRefreshNotice() {
        String reasonsToRefresh = getReasonsToRefresh();
        makeVisible( refreshNeededComponent, !reasonsToRefresh.isEmpty() );
        refreshNeededComponent.add( new AttributeModifier( "title", true, new Model<String>(
                "Refresh:" + reasonsToRefresh ) ) );
    }

    private String getReasonsToRefresh() {
        String reasons = "";
        String lastModifier = getCommander().getLastModifier();
        long lastModified = getCommander().getLastModified();
        if ( lastModified > lastRefreshed && !lastModifier.isEmpty() && !lastModifier.equals(
                getUser().getUsername() ) )
            reasons = " -- Plan was modified by " + lastModifier;

        // Find expansions that were locked and are not unlocked
        for ( ModelObject mo : getEditableModelObjects( expansions ) ) {
            if ( !( mo instanceof Segment || mo instanceof Plan )
                    && getCommander().isUnlocked( mo ) ) {
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

    private void annotateSegmentName() {
        Analyst analyst = getApp().getAnalyst();
        String issue = analyst.getIssuesSummary( segment, Analyst.INCLUDE_PROPERTY_SPECIFIC );
        segmentNameLabel.add( new AttributeModifier( "class", true,// NON-NLS
                new Model<String>( issue.isEmpty() ? "no-error"
                        : "error" ) ) );  // NON-NLS
        segmentNameLabel.add( new AttributeModifier( "title", true,// NON-NLS
                new Model<String>( issue.isEmpty()
                        ? "No known issue"
                        : issue ) ) );
    }

    private void addPlanMenubar() {
        PropertyModel<Segment> sc = new PropertyModel<Segment>( this, "segment" );
        Set<Long> exps = getReadOnlyExpansions();
        planActionsMenu = new PlanActionsMenuPanel( "planActionsMenu", sc, exps );
        planActionsMenu.setOutputMarkupId( true );
        form.add( planActionsMenu );
        planShowMenu = new PlanShowMenuPanel( "planShowMenu", sc, exps );
        planShowMenu.setOutputMarkupId( true );
        form.add( planShowMenu );
        // form.add( new Label( "username", getUser().getUsername() ) );
    }

    private void addPlanActionsMenu() {
        planActionsMenu = new PlanActionsMenuPanel( "planActionsMenu",
                new PropertyModel<Segment>( this, "segment" ),
                getReadOnlyExpansions() );
        planActionsMenu.setOutputMarkupId( true );
        form.addOrReplace( planActionsMenu );
    }

    private void addSegmentSelector() {
        selectSegmentContainer = new WebMarkupContainer( "select-segment" );
        selectSegmentContainer.setOutputMarkupId( true );
        form.add( selectSegmentContainer );
        segmentDropDownChoice = new DropDownChoice<Segment>( "sg-sel",
                // NON-NLS
                new PropertyModel<Segment>( this,
                        "segment" ),
                // NON-NLS
                new PropertyModel<List<? extends Segment>>(
                        this,
                        "allSegments" ) );    // NON-NLS
        segmentDropDownChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) { // NON-NLS

            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Selected, getSegment() ) );
            }
        } );
        segmentDropDownChoice.setOutputMarkupId( true );
        selectSegmentContainer.add( segmentDropDownChoice );
    }

    private void addPlanSwitcher() {
        switchPlanContainer = new WebMarkupContainer( "switch-plan" );
        switchPlanContainer.setOutputMarkupId( true );
        form.add( switchPlanContainer );
        DropDownChoice<Plan> planDropDownChoice = new DropDownChoice<Plan>( "plan-sel",
                new PropertyModel<Plan>(
                        this,
                        "plan" ),
                new PropertyModel<List<? extends Plan>>(
                        this,
                        "plannablePlans" ) );
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
        dialogWindow.setContent( new DialogPanel( dialogWindow.getContentId(), new Model<String>(
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
            entityPanel = new EntityPanel( "entity",
                    new Model<ModelEntity>( entity ),
                    getReadOnlyExpansions(),
                    getAspectShown( entity ) );
        }
        form.addOrReplace( entityPanel );
    }

    private void addAssignmentsPanel() {
        Part partViewed = getModelObjectViewed( Part.class, "assignments" );
        if ( partViewed == null ) {
            assignmentsPanel = new Label( "assignments", "" );
            assignmentsPanel.setOutputMarkupId( true );
            makeVisible( assignmentsPanel, false );
        } else {
            assignmentsPanel = new PartAssignmentsPanel( "assignments",
                    new Model<Part>( partViewed ),
                    getReadOnlyExpansions() );
        }
        form.addOrReplace( assignmentsPanel );
    }

    private void addCommitmentsPanel() {
        Flow flowViewed = getModelObjectViewed( Flow.class, "commitments" );
        if ( flowViewed == null ) {
            commitmentsPanel = new Label( "commitments", "" );
            commitmentsPanel.setOutputMarkupId( true );
            makeVisible( commitmentsPanel, false );
        } else {
            commitmentsPanel = new SharingCommitmentsPanel( "commitments",
                    new Model<Flow>( flowViewed ),
                    getReadOnlyExpansions() );
        }
        form.addOrReplace( commitmentsPanel );
    }

    private void addEOIsPanel() {
        Flow flowViewed = getModelObjectViewed( Flow.class, "eois" );
        if ( flowViewed == null ) {
            eoisPanel = new Label( "eois", "" );
            eoisPanel.setOutputMarkupId( true );
            makeVisible( eoisPanel, false );
        } else {
            eoisPanel = new FlowEOIsPanel( "eois",
                    new Model<Flow>( flowViewed ),
                    getReadOnlyExpansions() );
        }
        form.addOrReplace( eoisPanel );
    }

    private void addFailureImpactsPanel() {
        SegmentObject segmentObject =
                (SegmentObject) getModelObjectViewed( ModelObject.class, "failure" );
        if ( segmentObject == null || !( segmentObject instanceof Part ||
                segmentObject instanceof Flow
                        && ( (Flow) segmentObject ).isSharing() ) ) {
            failureImpactsPanel = new Label( "impacts", "" );
            failureImpactsPanel.setOutputMarkupId( true );
            makeVisible( failureImpactsPanel, false );
        } else {
            failureImpactsPanel = new FailureImpactsPanel( "impacts",
                    new Model<SegmentObject>( segmentObject ),
                    getReadOnlyExpansions() );
        }
        form.addOrReplace( failureImpactsPanel );
    }

    /**
     * Add segment-related components.
     */
    private void addSegmentEditPanel() {
        boolean showSegmentEdit = expansions.contains( getSegment().getId() );
        if ( showSegmentEdit ) {
            segmentEditPanel = new SegmentEditPanel( "sg-editor",
                    // NON-NLS
                    new PropertyModel<Segment>( this, "segment" ),
                    getReadOnlyExpansions(),
                    getAspectShown( getSegment() ) );
        } else {
            segmentEditPanel = new Label( "sg-editor", "" );
            segmentEditPanel.setOutputMarkupId( true );
            makeVisible( segmentEditPanel, false );
        }
        form.addOrReplace( segmentEditPanel );
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
            surveysPanel = new SurveysPanel( "surveys", survey, getReadOnlyExpansions() );
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
        getUser().setPlan( plan );
    }

    private ModelEntity findExpandedEntity() {
        for ( long id : expansions ) {
            try {
                ModelObject mo = queryService.find( ModelObject.class, id );
                if ( mo.isEntity() )
                    return ( (ModelEntity) mo );
            } catch ( NotFoundException ignored ) {
                // ignore
            }
        }
        return null;
    }

    public List<Segment> getAllSegments() {
        List<Segment> allSegments = new ArrayList<Segment>( queryService.list( Segment.class ) );
        Collections.sort( allSegments, new Comparator<Segment>() {
            public int compare( Segment o1, Segment o2 ) {
                return Collator.getInstance().compare( o1.getName(), o2.getName() );
            }
        } );
        return allSegments;
    }

    /**
     * Find segment specified in parameters.
     *
     * @param queryService query service
     * @param parameters   the page parameters
     * @return a segment, or null if not found
     */
    public static Segment findSegment( QueryService queryService, PageParameters parameters ) {
        if ( parameters.containsKey( SEGMENT_PARM ) )
            try {
                return queryService.find( Segment.class, parameters.getLong( SEGMENT_PARM ) );
            } catch ( StringValueConversionException ignored ) {
                LOG.warn( "Invalid segment specified in parameters. Using default." );
            } catch ( NotFoundException ignored ) {
                LOG.warn( "Unknown segment specified in parameters. Using default." );
            } catch ( ClassCastException ignored ) {
                LOG.warn( "Other object specified as segment in parameters. Using default." );
            }
        return null;
    }

    /**
     * Find part specified in parameters.
     *
     * @param segment    the plan segment
     * @param parameters the page parameters
     * @return a part, or null if not found
     */
    public static Part findPart( Segment segment, PageParameters parameters ) {
        if ( parameters.containsKey( PART_PARM ) )
            try {
                if ( segment != null )
                    return (Part) segment.getNode( parameters.getLong( PART_PARM ) );
            } catch ( StringValueConversionException ignored ) {
                LOG.warn( "Invalid part specified in parameters. Using default." );
            }
        return null;
    }

    /**
     * Redirect to a plan segment.
     *
     * @param segment a segment
     */
    public void redirectTo( Segment segment ) {
        redirectTo( segment.getDefaultPart() );
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
        setResponsePage( new RedirectPage( SegmentLink.linkStringFor( p, ids ) ) );
    }

    /**
     * Redirect here.
     */
    public void redirectHere() {
        long sid = segment.getId();
        long nid = getPart().getId();
        StringBuffer exps = new StringBuffer( 128 );
        for ( long id : expansions ) {
            exps.append( "&expand=" );                                                    // NON-NLS
            exps.append( Long.toString( id ) );
        }
        setResponsePage( new RedirectPage( MessageFormat.format(
                "/plan?segment={0,number,0}&part={1,number,0}{2}",
                // NON-NLS
                sid,
                nid,
                exps ) ) );
    }

    /**
     * Return initialized parameters for given segment and part.
     *
     * @param segment  the segment
     * @param p        the part, maybe null (in which case, would link to first part in segment)
     * @param expanded components id that should be expanded
     * @return page parameters to use in links, etc.
     */
    public static PageParameters getParameters(
            Segment segment, Part p, Set<Long> expanded ) {
        PageParameters result = new PageParameters();
        result.put( SEGMENT_PARM, Long.toString( segment.getId() ) );
        if ( p != null ) {
            result.put( PART_PARM, Long.toString( p.getId() ) );
            for ( long id : expanded )
                result.add( EXPAND_PARM, Long.toString( id ) );
        }
        return result;
    }

    /**
     * Return initialized parameters for given segment and part.
     *
     * @param segment the segment
     * @param p       the part, maybe null (in which case, would link to first part in segment)
     * @return page parameters to use in links, etc.
     */
    public static PageParameters getParameters( Segment segment, Part p ) {
        Set<Long> expansions = Collections.emptySet();
        return getParameters( segment, p, expansions );
    }

    /**
     * Return initialized parameters for given segment and part.
     *
     * @param segment the segment
     * @param p       the part, maybe null (in which case, would link to first part in segment)
     * @param id      the id to expand
     * @return page parameters to use in links, etc.
     */
    public static PageParameters getParameters( Segment segment, Part p, long id ) {
        Set<Long> expansions = new HashSet<Long>( 1 );
        expansions.add( id );
        return getParameters( segment, p, expansions );
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
        if ( parameters == null )
            return new HashSet<Long>();
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
        return getApp().getCommander( getPlan() );
    }

    /**
     * Get set or default part.
     *
     * @return a part
     */
    public Part getPart() {
        if ( isZombie( part ) ) {
            Part part = segment.getDefaultPart();
            getCommander().requestLockOn( part );
            return part;
        } else {
            return part;
        }
    }

    private boolean isZombie( Part part ) {
        return part == null || segment.getNode( part.getId() ) == null || part.getSegment() == null;
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
        if ( part == null )
            part = segment.getDefaultPart();
        if ( part.getSegment() != segment )
            setSegment( part.getSegment() );
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
        if ( params == null )
            params = new PageParameters();
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
                if ( !exp.equals( idString ) )
                    result.add( EXPAND_PARM, exp );
        }
        return result;
    }

    public Segment getSegment() {
        return segment;
    }

    public void setSegment( Segment sc ) {
        if ( segment != null && !segment.equals( sc ) ) {
            collapseSegmentObjects();
        }
        segment = sc;
        if ( segment == null )
            segment = getQueryService().getDefaultSegment();
        if ( !getPart().getSegment().equals( segment ) ) {
            setPart( segment.getDefaultPart() );
        }
    }

    /**
     * Set a component's visibility.
     *
     * @param component a component
     * @param visible   a boolean
     */
    private static void makeVisible( Component component, boolean visible ) {
        component.add( new AttributeModifier( "style", true, new Model<String>(
                visible ? "" : "display:none" ) ) );
    }

    private void reacquireLocks() {
        // Part is always "expanded"
        getCommander().requestLockOn( getPart() );
        for ( Long id : expansions ) {
            try {
                ModelObject expanded = getQueryService().find( ModelObject.class, id );
                if ( !( expanded instanceof Segment || expanded instanceof Plan ) )
                    getCommander().requestLockOn( expanded );
            } catch ( NotFoundException e ) {
                LOG.warn( "Expanded model object not found at: " + id );
            }
        }
    }

    private void expand( Identifiable identifiable ) {
        // Never lock a segment or plan, or anything in a production plan
        if ( getPlan().isDevelopment() && identifiable instanceof ModelObject
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

    private boolean isExpanded( Identifiable identifiable ) {
        return expansions.contains( identifiable.getId() );
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
            if ( aspectsShown == null )
                aspectsShown = new ArrayList<String>();
            if ( !aspectsShown.contains( aspect ) )
                aspectsShown.add( aspect );
            expand( identifiable );
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

    private <T extends ModelObject> T getModelObjectViewed( Class<T> clazz, String aspect ) {
        T viewed = null;
        Long id = findIdForAspectViewed( clazz, aspect );
        if ( id != null ) {
            try {
                viewed = getQueryService().find( clazz, id );
            } catch ( NotFoundException e ) {
                LOG.warn( "Viewed object not found at " + id );
            }
        }
        return viewed;
    }

    // Assumes that only instance of clazz can view the given aspect at a time.
    private Long findIdForAspectViewed(
            final Class<? extends ModelObject> clazz, final String aspect ) {
        return (Long) CollectionUtils.find( aspects.keySet(), new Predicate() {
            public boolean evaluate( Object obj ) {
                Long id = (Long) obj;
                try {
                    getQueryService().find( clazz, id );
                } catch ( NotFoundException e ) {
                    return false;
                }
                return aspects.get( id ).contains( aspect );
            }
        } );
    }

    private void collapseSegmentObjects() {
        List<Identifiable> toCollapse = new ArrayList<Identifiable>();
        for ( long id : expansions ) {
            try {
                ModelObject expanded = getQueryService().find( ModelObject.class, id );
                if ( expanded instanceof SegmentObject ) {
                    if ( ( (SegmentObject) expanded ).getSegment() == segment ) {
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
        getCommander().clearTimeOut();
        if ( change.isNone() )
            return;
        Identifiable identifiable = change.getSubject();
        if ( !( identifiable instanceof Survey ) ) {
            if ( change.isCollapsed() || change.isRemoved() )
                collapse( identifiable );
            else if ( change.isExpanded() || change.isAdded() )
                expand( identifiable );
            else if ( change.isAspectViewed() ) {
                if ( change.getSubject() instanceof Flow ) {
                    Flow otherFlowViewed = getModelObjectViewed( Flow.class, change.getProperty() );
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
        if ( identifiable instanceof Segment ) {
            if ( change.isExists() ) {
                getCommander().resetUserHistory( getUser().getUsername(), false );
                if ( change.isAdded() ) {
                    setSegment( (Segment) identifiable );
                    setPart( null );
                } else {
                    assert change.isRemoved();
                    collapseSegmentObjects();
                    setSegment( null );
                }
            } else if ( change.isRecomposed() ) {
                collapseSegmentObjects();
                setPart( getPart() );
            } else if ( change.isSelected() ) {
                collapseSegmentObjects();
                setSegment( (Segment) identifiable );
                setPart( null );
            } else if ( change.isMaximized() ) {
                flowMaximized = true;
            } else if ( change.isMinimized() ) {
                flowMaximized = false;
            } else if ( change.isForProperty( "legend" ) ) {
                flowsExplained = change.isExplained();
            }
        } else if ( identifiable instanceof Part ) {
            if ( change.isAdded() || change.isSelected() ) {
                setPart( (Part) identifiable );
                flowMaximized = false;
                if ( change.isAdded() )
                    expand( identifiable );
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
                if ( flow.getSegment() != segment ) {
                    setSegment( flow.getSegment() );
                }
                if ( !flow.hasPart( getPart() ) ) {
                    setPart( flow.getLocalPart() );
                }
                expand( identifiable );
            }
        } else if ( identifiable instanceof UserIssue && change.isAdded() ) {
            UserIssue userIssue = (UserIssue) identifiable;
            ModelObject mo = userIssue.getAbout();
            if ( mo instanceof Segment ) {
                expand( identifiable );
            }
        }
        rememberState();
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( !change.isNone() ) {
            if ( change.getSubject() instanceof Plan && change.isSelected() ) {
                redirectToPlan();
            } else if ( change.isUndoing() || change.isUnknown() || change.isRecomposed()
                    || change.isAdded() && change.getSubject() instanceof Part ) {
                refresh( target, change, new ArrayList<Updatable>() );
            } else if ( change.isUpdated() && isExpanded( change.getSubject() ) ) {
                Change accumulatedChange = changes.get( change.getSubject() );
                if ( accumulatedChange == null ) {
                    changes.put( change.getSubject(), change );
                } else {
                    // more than one property changed
                    change.setProperty( "?" );
                }
            } else if ( change.isCollapsed() && changes.get( change.getSubject() ) != null ) {
                refreshAll( target );
            } else if ( change.getSubject() instanceof Flow && change.isSelected() ) {
                segmentPanel.resizePartPanels( target );
            } else if ( change.isCopied() ) {
                refreshAllMenus( target );
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
    public void refresh(
            AjaxRequestTarget target, Change change, List<Updatable> updated, String aspect ) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void refresh( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        updateMaximizedFlow( target, change );
        updateFlowLegend( target );
        updateHeaders( target );
        refreshPlanMenus( target );
        updateNavigation( target );
        updateRefresh( target );
        updateSelectors( target, change );
        refreshChildren( target, change, updated );
    }

    private void refreshAllMenus( AjaxRequestTarget target ) {
        refreshPlanMenus( target );
        refreshChildrenMenus( target );
    }

    private void updateMaximizedFlow( AjaxRequestTarget target, Change change ) {
        addMaximizedFlowPanel( change );
        if ( !flowMaximized ) segmentPanel.updateFlowMapOnMinimize( target, change );
        target.addComponent( maximizedFlowPanel );
    }

    private void updateFlowLegend( AjaxRequestTarget target ) {
        addFlowLegendPanel();
        target.addComponent( flowLegendPanel );
    }

    private void updateRefresh( AjaxRequestTarget target ) {
        updateRefreshNotice();
        target.addComponent( refreshNeededComponent );
    }

    private void updateHeaders( AjaxRequestTarget target ) {
        annotateSegmentName();
        target.addComponent( segmentNameLabel );
        target.addComponent( segmentDescriptionLabel );
        form.addOrReplace( createPartsMapLink() );
        target.addComponent( partsMapLink );
    }

    private void refreshPlanMenus( AjaxRequestTarget target ) {
        addPlanActionsMenu();
        target.addComponent( planActionsMenu );
        target.addComponent( planShowMenu );
    }

    private void updateSelectorsVisibility() {
        makeVisible( selectSegmentContainer, getAllSegments().size() > 1 );
        makeVisible( switchPlanContainer, getPlannablePlans().size() > 1 );
    }

    private void updateSelectors( AjaxRequestTarget target, Change change ) {
        Identifiable identifiable = change.getSubject();
        if ( identifiable instanceof Part && change.isSelected() ) {
            // In case selecting the part switched segments
            target.addComponent( segmentDropDownChoice );
        }
        updateSelectorsVisibility();
        target.addComponent( selectSegmentContainer );
        target.addComponent( switchPlanContainer );
    }

    private void refreshChildren(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        refreshPlanEditPanel( target, change, updated );
        refreshSegmentEditPanel( target, change, updated );
        refreshEntityPanel( target, change, updated );
        refreshAssignmentsPanel( target, change, updated );
        refreshCommitmentsPanel( target, change, updated );
        refreshEOIsPanel( target, change, updated );
        refreshSurveysPanel( target, change, updated );
        refreshSegmentPanel( target, change, updated );
        refreshFailureImpactsPanel( target, change, updated );
    }

    private void refreshChildrenMenus( AjaxRequestTarget target ) {
        if ( planEditPanel instanceof PlanEditPanel )
            ( (PlanEditPanel) planEditPanel ).refreshMenus( target );
        if ( segmentEditPanel instanceof SegmentEditPanel )
            ( (SegmentEditPanel) segmentEditPanel ).refreshMenus( target );
        if ( entityPanel instanceof EntityPanel )
            ( (EntityPanel) entityPanel ).refreshMenus( target );
        segmentPanel.refreshMenus( target );
    }

    private void refreshSegmentPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject();
        if ( identifiable instanceof SegmentObject
                && ( change.isSelected() || change.isDisplay() || change.isExists() ) ) {
            segmentPanel.doRefresh( target, change );
            // target.addComponent( segmentPanel );
        } else {
            segmentPanel.refresh( target, change, updated, getAspectShown( getSegment() ) );
        }
    }

    private void refreshPlanEditPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject();
        Plan plan = getPlan();
        if ( change.isUnknown() || change.isDisplay() && identifiable instanceof Plan ) {
            addPlanEditPanel();
            target.addComponent( planEditPanel );
        } else if ( planEditPanel instanceof PlanEditPanel ) {
            ( (PlanEditPanel) planEditPanel ).refresh( target,
                    change,
                    updated,
                    getAspectShown( plan ) );
        }
    }

    private void refreshSegmentEditPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isUnknown()
                || ( change.isDisplay() || change.isAdded() ) && change.getSubject() instanceof Segment
                || change.isSelected() && change.getSubject() instanceof Part ) {
            addSegmentEditPanel();
            target.addComponent( segmentEditPanel );
            target.addComponent( segmentDropDownChoice );
        } else if ( segmentEditPanel instanceof SegmentEditPanel ) {
            ( (SegmentEditPanel) segmentEditPanel ).refresh( target,
                    change,
                    updated,
                    getAspectShown( getSegment() ) );
        }
    }

    private void refreshEntityPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        ModelEntity entity = findExpandedEntity();
        if ( change.isUnknown()
                || change.isDisplay() && change.getSubject() instanceof ModelEntity ) {
            addEntityPanel();
            target.addComponent( entityPanel );
        } else if ( entityPanel instanceof EntityPanel ) {
            ( (EntityPanel) entityPanel ).refresh( target,
                    change,
                    updated,
                    getAspectShown( entity ) );
        }
    }

    private void refreshAssignmentsPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject();
        if ( change.isUnknown()
                || identifiable instanceof Part && change.isAspect( "assignments" ) ) {
            addAssignmentsPanel();
            target.addComponent( assignmentsPanel );
        } else if ( assignmentsPanel instanceof PartAssignmentsPanel ) {
            ( (PartAssignmentsPanel) assignmentsPanel ).refresh( target, change, updated );
        }
    }

    private void refreshCommitmentsPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject();
        if ( change.isUnknown()
                || identifiable instanceof Flow && change.isAspect( "commitments" ) ) {
            addCommitmentsPanel();
            target.addComponent( commitmentsPanel );
        } else if ( commitmentsPanel instanceof SharingCommitmentsPanel ) {
            ( (SharingCommitmentsPanel) commitmentsPanel ).refresh( target, change, updated );
        }
    }

    private void refreshEOIsPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject();
        if ( change.isUnknown() || identifiable instanceof Flow && ( change.isCollapsed()
                || change.isAspect( "eois" ) ) ) {
            addEOIsPanel();
            target.addComponent( eoisPanel );
        } else if ( eoisPanel instanceof FlowEOIsPanel ) {
            ( (FlowEOIsPanel) eoisPanel ).refresh( target, change, updated );
        }
    }

    private void refreshFailureImpactsPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject();
        if ( change.isUnknown() || identifiable instanceof SegmentObject && change.isAspect(
                "failure" ) ) {
            addFailureImpactsPanel();
            target.addComponent( failureImpactsPanel );
        } else if ( failureImpactsPanel instanceof FailureImpactsPanel ) {
            ( (FailureImpactsPanel) failureImpactsPanel ).refresh( target, change, updated );
        }
    }

    private void refreshSurveysPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject();
        if ( change.isUnknown() || change.isDisplay() && ( identifiable instanceof SurveyService
                || identifiable instanceof Survey ) ) {
            addSurveysPanel( identifiable instanceof Survey ? (Survey) identifiable : null );
            target.addComponent( surveysPanel );
        } else if ( surveysPanel instanceof SurveysPanel ) {
            ( (SurveysPanel) surveysPanel ).refresh( target,
                    change,
                    updated,
                    getAspectShown( surveyService ) );
        }
    }

    private void updateNavigation() {
        goBackContainer.add( new AttributeModifier( "src", true, new Model<String>( isCanGoBack()
                ? "images/go_back.png"
                : "images/go_back_disabled.png" ) ) );
        goBackContainer.add( new AttributeModifier( "title", true, new Model<String>(
                isCanGoBack() ? "Go back" : "" ) ) );
        goForwardContainer.add( new AttributeModifier( "src", true, new Model<String>(
                isCanGoForward() ? "images/go_forward.png" : "images/go_forward_disabled.png" ) ) );
        goForwardContainer.add( new AttributeModifier( "title", true, new Model<String>(
                isCanGoForward() ? "Go forward" : "" ) ) );
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
        return getPlanManager().getPlannablePlans( getUser() );
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
     * Open segment import dialog.
     *
     * @param target an ajax request target
     */
    public void importSegment( AjaxRequestTarget target ) {
        segmentImportPanel.open( target );
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
     * set segment if different and exists, set part if different and exists
     *
     * @param pageState a page state
     * @param target    an ajax request target
     */
    @SuppressWarnings( "unchecked" )
    private void reinstate( PageState pageState, AjaxRequestTarget target ) {
        // Expand what's expanded in page state but not in current expansions
        List<Long> expandSet =
                (List<Long>) CollectionUtils.subtract( pageState.getExpansions(), expansions );
        // Collapse what's in expansions but not in page state
        List<Long> collapseSet =
                (List<Long>) CollectionUtils.subtract( expansions, pageState.getExpansions() );
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
            Segment previousSegment =
                    getQueryService().find( Segment.class, pageState.getSegmentId() );
            if ( !getSegment().equals( previousSegment ) ) {
                setSegment( previousSegment );
            }
            Part previousPart = (Part) getSegment().getNode( pageState.getPartId() );
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
         * Segment id.
         */
        private long segmentId;

        /**
         * Part id.
         */
        private long partId;

        /**
         * Expansions
         */
        private Set<Long> expanded;

        /**
         * Aspects viewed.
         */
        private Map<Long, List<String>> aspects;

        private PageState() {
            segmentId = getSegment().getId();
            partId = getPart().getId();
            this.expanded = new HashSet<Long>( getReadOnlyExpansions() );
            this.aspects = new HashMap<Long, List<String>>( getReadOnlyAspects() );
        }

        public long getSegmentId() {
            return segmentId;
        }

        public void setSegmentId( long segmentId ) {
            this.segmentId = segmentId;
        }

        public long getPartId() {
            return partId;
        }

        public void setPartId( long partId ) {
            this.partId = partId;
        }

        public Set<Long> getExpansions() {
            return expanded;
        }

        public void setExpansions( Set<Long> expansions ) {
            this.expanded = new HashSet<Long>( expansions );
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
                return segmentId == other.getSegmentId() && partId == other.getPartId()
                        && CollectionUtils.isEqualCollection( expanded, other.getExpansions() )
                        && hasSameAspects( other );
            } else {
                return false;
            }
        }

        private boolean hasSameAspects( PageState other ) {
            Map<Long, List<String>> otherAspects = other.getAspects();
            if ( !CollectionUtils.isEqualCollection( aspects.keySet(), otherAspects.keySet() ) )
                return false;
            for ( Long id : aspects.keySet() ) {
                if ( !CollectionUtils.isEqualCollection( otherAspects.get( id ),
                        aspects.get( id ) ) )
                    return false;
            }
            return true;
        }
    }
} 

