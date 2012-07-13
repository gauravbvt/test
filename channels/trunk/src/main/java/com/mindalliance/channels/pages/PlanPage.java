/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages;

import com.google.code.jqwicket.ui.notifier.NotifierWebMarkupContainer;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.GeoLocatable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.model.Subject;
import com.mindalliance.channels.core.model.UserIssue;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.pages.components.AbstractMultiAspectPanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.DisseminationPanel;
import com.mindalliance.channels.pages.components.GeomapLinkPanel;
import com.mindalliance.channels.pages.components.IndicatorAwareWebContainer;
import com.mindalliance.channels.pages.components.ModelObjectSurveysPanel;
import com.mindalliance.channels.pages.components.entities.EntityPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.PlanEditPanel;
import com.mindalliance.channels.pages.components.plan.floating.AllFeedbackFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.AllIssuesFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.AllTagsFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.AllTypesFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.BibliographyFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.PlanClassificationsFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.PlanEvaluationFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.PlanEventsFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.PlanIndexFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.PlanOrganizationsFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.PlanParticipationFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.PlanRequirementsFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.PlanSegmentsFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.PlanVersionsFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.ProtocolsMapFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.TaskMoverFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.WhosWhoFloatingPanel;
import com.mindalliance.channels.pages.components.plan.menus.PlanActionsMenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.PlanImprovingMenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.PlanParticipationMenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.PlanScopingMenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.PlanSearchingMenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.PlanShowMenuPanel;
import com.mindalliance.channels.pages.components.segment.ExpandedFlowPanel;
import com.mindalliance.channels.pages.components.segment.FailureImpactsPanel;
import com.mindalliance.channels.pages.components.segment.FlowEOIsPanel;
import com.mindalliance.channels.pages.components.segment.MaximizedFlowPanel;
import com.mindalliance.channels.pages.components.segment.OverridesPanel;
import com.mindalliance.channels.pages.components.segment.PartAssignmentsPanel;
import com.mindalliance.channels.pages.components.segment.SegmentEditPanel;
import com.mindalliance.channels.pages.components.segment.SegmentPanel;
import com.mindalliance.channels.pages.components.segment.SharingCommitmentsPanel;
import com.mindalliance.channels.pages.components.social.rfi.DataCollectionPanel;
import com.mindalliance.channels.pages.components.support.FlowLegendPanel;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import com.mindalliance.channels.social.model.Feedback;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.StringValueConversionException;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;

import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The plan editing page.
 * Note: When a user switches plan, this page *must* be reloaded.
 */
public final class PlanPage extends AbstractChannelsWebPage {
    /**
     * The 'expand' parameter in the URL.
     */
    public static final String EXPAND_PARM = "expand";

    /**
     * The 'segment' parameter in the URL.
     */
    public static final String SEGMENT_PARM = "segment";

    /**
     * The 'part' parameter in the URL.
     */
    static final String PART_PARM = "node";

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanPage.class );

    /**
     * Length a segment name is abbreviated to.
     */
    private static final int SEGMENT_NAME_MAX_LENGTH = 50;

    /**
     * Length a plan name is abbreviated to.
     */
    private static final int PLAN_NAME_MAX_LENGTH = 50;

    /**
     * Length a segment title is abbreviated to.
     */
    private static final int SEGMENT_DESCRIPTION_MAX_LENGTH = 94;
    /**
     * IE7 compatibility script.
     */
    public static String IE7CompatibilityScript;

    /**
     * The mail sender.
     */
    @SpringBean
    private MailSender mailSender;
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
 //   private List<PageState> pageHistory = new ArrayList<PageState>();

    /**
     * Page history cursor.
     */
    private int historyCursor = -1;

    /**
     * Link to mapping of parts.
     */
    private GeomapLinkPanel partsMapLink;

    private Map<Long, Boolean> showSimpleForm = new HashMap<Long, Boolean>();

    private PlanScopingMenuPanel scopingMenu;
    private PlanSearchingMenuPanel searchingMenu;
    private PlanParticipationMenuPanel participationMenu;
    private PlanImprovingMenuPanel improvingMenu;

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
    private Form form;
    /**
     * Ajax activity spinner.
     */
    private WebMarkupContainer spinner;

    /**
     * The segment panel.
     */
    private SegmentPanel segmentPanel;

    /// FLOATING PANELS

    /**
     * Segment edit panel.
     */
    private Component segmentEditPanel;

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
    //   private Component surveysPanel;

    /**
     * Failure impacts panel.
     */
    private Component failureImpactsPanel;


    /**
     * Dissemination panel.
     */
    private Component disseminationPanel;

    /**
     * Surveys panel.
     */
    private Component modelObjectSurveysPanel;

    /**
     * Overrides panel.
     */
    private Component overridesPanel;

    /**
     * Flow legend panel.
     */
    private Component flowLegendPanel;

    /**
     * Feedbacks panel.
     */
    private Component allFeedbackPanel;

    /**
     * All surveys panel
     */
    private Component allSurveyPanel;
    /**
     * All requirements panel.
     */
    private Component planRequirementsPanel;
    private Component allEventsPanel;
    private Component allOrganizationsPanel;
    private Component allSegmentsPanel;
    private Component allClassificationsPanel;
    private Component protocolsMapPanel;
    private Component planEvaluationPanel;
    private Component taskMoverPanel;
    private Component allIssuesPanel;
    private Component planVersionsPanel;
    private Component planParticipationPanel;
    private Component planIndexPanel;
    private Component allTagsPanel;
    private Component allTypesPanel;
    private Component whosWhoPanel;
    private Component bibliographyPanel;

    /**
     * Refresh button.
     */
    private Component refreshNeededComponent;
    /**
     * Go back link.
     */
//    private AjaxLink<String> goBackLink;
    /**
     * Go forward link.
     */
//    private AjaxLink<String> goForwardLink;
    /**
     * Geomap link panel.
     */
    private GeomapLinkPanel geomapLinkPanel;
    /**
     * Segment issues link.
     */
    private AjaxLink<String> segmentIssuesLink;
    /**
     * Plan path.
     */
    private WebMarkupContainer planPath;
    /**
     * Notifier.
     */
    private NotifierWebMarkupContainer notifier;
    /**
     * When last refreshed.
     */
    private long lastRefreshed = System.currentTimeMillis();

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
     * Cumulated change to an expanded identifiable.
     */
    private Map<Long, Change> changes = new HashMap<Long, Change>();
    /**
     * Message shown in message panel.
     */
    private String message;


    static {
        IE7CompatibilityScript =
                // "alert($.browser.msie + ' ' + parseInt( $.browser.version ) );\n" +
                "$(document).ready(function() {\n" +
                        "if ( $.browser.msie && parseInt( $.browser.version ) < 8 ) {\n" +
                        "    var zIndexFix = 110;\n" +
                        "    $('div.flow > div').each(function() {\n" +
                        "        $(this).css('zIndex', -zIndexFix);\n" +
                        "        zIndexFix += 10;\n" +
                        "    })\n" +
                        "};\n" +
                        "});";
    }

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
        Segment sc = findSegment( getQueryService(), parameters );
        init( sc, findPart( sc, parameters ), findExpansions( parameters ) );
    }


    /**
     * /**
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
        super();
        init( sc, p, new HashSet<Long>() );
    }

    // Guide scripting support
    public SegmentPanel getSegmentPanel() {
        return segmentPanel;
    }

    public void renderHead( HtmlHeaderContainer container ) {
        container.getHeaderResponse().renderJavaScript( PlanPage.IE7CompatibilityScript, null );
        super.renderHead( container );
    }

    private void init( Segment sc, Part p, Set<Long> expanded ) {
        // TODO - uncomment when getting client info works on first invocation without restart exception
        /*
        User user = user;
        WebClientInfo clientInfo = (WebClientInfo) WebRequestCycle.get().getClientInfo();
        user.setClientInfo( clientInfo );
        */
        final Commander commander = getCommander();
        commander.keepAlive( getUser().getUsername(), REFRESH_DELAY );
        commander.releaseAllLocks( getUser().getUsername() );
        setSegment( sc );
        setPart( p );
        expansions = expanded;
        expansions.add(  Channels.ALL_SEGMENTS );
        for ( Long id : expansions ) {
            commander.requestLockOn( getUser().getUsername(), id );
        }
        setVersioned( false );
        expanded.add( Channels.SOCIAL_ID );
        expanded.add( Channels.GUIDE_ID );
        add( new Label( "sg-title",
                new Model<String>( "Channels: " + getPlan().getVersionedName() ) ) );
        addBody();
        commander.resynced( getUser().getUsername() );
        LOG.debug( "Segment page generated" );
//        rememberState();
    }

    private void addBody() {
        WebMarkupContainer body = new IndicatorAwareWebContainer( "indicator", "spinner" );
        add( body );
        addNotifier( body );
        addModalDialog( "dialog", null, body );
        addForm( body );
    }

    private void addForm( WebMarkupContainer body ) {
        form = new Form( "big-form" ) {
            @Override
            protected void onSubmit() {
                // Do nothing - everything is done via Ajax, even file uploads
                // System.out.println( "Form submitted" );
            }
        };
        form.setMultiPart( true );
        body.add( form );
        addHeader();
        addMaximizedFlowPanel( new Change( Change.Type.None ) );
        addFloatingPanels();
        addSegmentPanel();
        addFooter();
    }

    private void addHeader() {
        addHomeLink();
        addRefreshNow();
      //  addGoBackAndForward();
        addActivitiesMenubar();
        addPlanMenubar();
        addFeedback();
        addHelp();
        addPlanPath();
        addSpinner();
        addPartsMapLink();
        addSegmentIssuesLink();
        updateNavigation();
    }

    private void addHomeLink() {
        WebMarkupContainer homeLink = new WebMarkupContainer( "homeLink" );
        homeLink.add( new AttributeModifier(
                "href",
                makeHomeUrl() ) );
        form.add( homeLink );
    }



    private void addFloatingPanels() {
        addEntityPanel();
        addAssignmentsPanel();
        addCommitmentsPanel();
        addEOIsPanel();
        addFailureImpactsPanel();
        addDisseminationPanel( null, false );
        addModelObjectSurveysPanel();
        addOverridesPanel();
        addSegmentEditPanel();
        addPlanEditPanel( null );
        addFlowLegendPanel();
        // scoping
        addRequirementsPanel();
        addAllEventsPanel();
        addAllOrganizationsPanel();
        addAllSegmentsPanel();
        addAllClassificationsPanel();
        // improving
        addTaskMoverPanel();
        addProtocolsMapPanel();
        addPlanEvaluationPanel();
        addAllIssuesPanel();
        addPlanVersionsPanel();
        // participating
        addPlanParticipationPanel();
        addAllFeedbackPanel();
        addDataCollectionPanel();
        // searching
        addPlanIndexPanel();
        addAllTagsPanel();
        addAllTypesPanel();
        addWhosWhoPanel();
        addBibliographyPanel();
    }

    private void addRefreshNow() {
        refreshNeededComponent = new AjaxFallbackLink( "refresh-needed" ) {
            public void onClick( AjaxRequestTarget target ) {
                getCommander().clearTimeOut( getUser().getUsername() );
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
                try {
                    doTimedUpdate( target );
                    makeVisible( spinner, false );
/*
                    addSpinner();
                    target.add( spinner );
*/
                } catch ( Exception e ) {
                    LOG.error( "Failed to do timed update", e );
                    ErrorPage.emailException(
                            new Exception( "Timed update failed", e ),
                            mailSender,
                            getSupportCommunity(),
                            getUser()
                    );
                    redirectToPlan();
                }
            }
        } );
        form.add( refreshNeededComponent );
        updateRefreshNowNotice();
    }

 /*   private void addGoBackAndForward() {
        goBackLink = new AjaxLink<String>( "goBack" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                goBack( target );
            }
        };
        form.add( goBackLink );

        goForwardLink = new AjaxLink<String>( "goForward" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                goForward( target );
            }
        };
        form.add( goForwardLink );
    }
*/
    private void updateNavigation() {
 /*       goBackLink.add( new AttributeModifier(
                "class",
                isCanGoBack() ? "back" : "back disabled" ) );
        goForwardLink.add( new AttributeModifier(
                "class",
                isCanGoForward() ? "forward" : "forward disabled" ) );
*/
        String issuesSummary = getSegmentIssuesSummary();
        makeVisible( segmentIssuesLink, !issuesSummary.isEmpty() );
        segmentIssuesLink.add( new AttributeModifier( "title", issuesSummary ) );
        addPartsMapLink();
    }

    private String getSegmentIssuesSummary() {
        Analyst analyst = getApp().getAnalyst();
        return analyst.getIssuesSummary( getQueryService(), segment, Analyst.INCLUDE_PROPERTY_SPECIFIC );
    }


    private void updateNavigation( AjaxRequestTarget target ) {
        updateNavigation();
        // target.add( goBackLink );
        // target.add( goForwardLink );
        target.add( segmentIssuesLink );
        target.add( geomapLinkPanel );
        addPlanPath();
        target.add( planPath );
    }

    private void addActivitiesMenubar() {
        addScopingMenu();
        addImprovingMenu();
        addParticipationMenu();
        addSearchingMenu();
    }

    private void addScopingMenu() {
        scopingMenu = new PlanScopingMenuPanel(
                "scopingMenu",
                new PropertyModel<Segment>( this, "segment" ),
                getReadOnlyExpansions() );
        form.addOrReplace( scopingMenu );
    }

    private void addImprovingMenu() {
        improvingMenu = new PlanImprovingMenuPanel(
                "improvingMenu",
                new PropertyModel<Segment>( this, "segment" ),
                getReadOnlyExpansions() );
        form.addOrReplace( improvingMenu );
    }

    private void addParticipationMenu() {
        participationMenu = new PlanParticipationMenuPanel(
                "participationMenu",
                new PropertyModel<Segment>( this, "segment" ),
                getReadOnlyExpansions() );
        form.addOrReplace( participationMenu );
    }

    private void addSearchingMenu() {
        searchingMenu = new PlanSearchingMenuPanel(
                "searchingMenu",
                new PropertyModel<Segment>( this, "segment" ),
                getReadOnlyExpansions() );
        form.addOrReplace( searchingMenu );
    }

    private void addPlanMenubar() {
        addPlanActionsMenu();
        addPlanShowMenu();
    }

    private void addPlanShowMenu() {
        planShowMenu = new PlanShowMenuPanel(
                "planShowMenu",
                new PropertyModel<Segment>( this, "segment" ),
                getReadOnlyExpansions() );
        form.addOrReplace( planShowMenu );
    }

    private void addPlanActionsMenu() {
        planActionsMenu = new PlanActionsMenuPanel( "planActionsMenu",
                new PropertyModel<Segment>( this, "segment" ),
                getReadOnlyExpansions() );
        form.addOrReplace( planActionsMenu );
    }

    private void addPartsMapLink() {
        List<GeoLocatable> geoLocatables = new ArrayList<GeoLocatable>();
        for ( Iterator<Part> parts = segment.parts(); parts.hasNext(); )
            geoLocatables.add( parts.next() );
        geomapLinkPanel = new GeomapLinkPanel( "geomapLink",
                new Model<String>(
                        "Tasks with known locations in plan segment "
                                + segment.getName() ),
                geoLocatables,
                new Model<String>( "Show tasks in map" ) );
        geomapLinkPanel.setOutputMarkupId( true );
        partsMapLink = geomapLinkPanel;
        makeVisible( geomapLinkPanel, geomapLinkPanel.hasMappableContent() );
        form.addOrReplace( geomapLinkPanel );
    }

    private void addFeedback() {
        form.add( new UserFeedbackPanel( "feedback", Feedback.PLANNING ) );
    }


    private void addSegmentIssuesLink() {
        segmentIssuesLink = new AjaxLink<String>( "segmentIssuesLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Expanded, getSegment() ) );
            }
        };
        segmentIssuesLink.setOutputMarkupId( true );
        form.addOrReplace( segmentIssuesLink );
    }


    private void addSpinner() {
        spinner = new WebMarkupContainer( "spinner" );
        spinner.setOutputMarkupId( true );
        spinner.add( new AttributeModifier( "id", new Model<String>( "spinner" ) ) );
        form.addOrReplace( spinner );
    }

    private void addPlanPath() {
        planPath = new WebMarkupContainer( "planPath" );
        planPath.setOutputMarkupId( true );
        form.addOrReplace( planPath );
        addHomeInPath();
        addSelectedPlanInPath();
        addSelectedSegmentInPath();
        addOtherSegmentsInPath();
    }

    private void addHomeInPath() {
        AjaxLink<String> homeLink = new AjaxLink<String>( "homeLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                setResponsePage( UserPage.class, planParameters( getPlan() ) );
            }
        };
        planPath.add( homeLink );
    }

    private void addSelectedPlanInPath() {
        AjaxLink<String> selectedPlanLink = new AjaxLink<String>( "selectedPlanLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Expanded, getPlan() ) );
            }
        };
        planPath.add( selectedPlanLink );
        String planName = getPlan().getName();
        Label selectedPlanNameLabel = new Label(
                "selectedPlan",
                StringUtils.abbreviate( planName, PLAN_NAME_MAX_LENGTH )
        );
        if ( planName.length() > PLAN_NAME_MAX_LENGTH ) {
            selectedPlanNameLabel.add( new AttributeModifier( "title", planName ) );
        }
        selectedPlanLink.add( selectedPlanNameLabel );
    }

    private void addSelectedSegmentInPath() {
        AjaxLink<String> selectedSegmentLink = new AjaxLink<String>( "selectedSegmentLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Expanded, getSegment() ) );
            }
        };
        planPath.add( selectedSegmentLink );
        String segmentName = getSegment().getName();
        Label selectedSegmentNameLabel = new Label(
                "selectedSegment",
                StringUtils.abbreviate( segmentName, SEGMENT_NAME_MAX_LENGTH )
        );
        if ( segmentName.length() > SEGMENT_NAME_MAX_LENGTH ) {
            selectedSegmentNameLabel.add( new AttributeModifier( "title", segmentName ) );
        }
        selectedSegmentLink.add( selectedSegmentNameLabel );
    }


    private void addOtherSegmentsInPath() {
        ListView<Segment> otherSegmentsListView = new ListView<Segment>(
                "otherSegments",
                getOtherSegments()
        ) {
            @Override
            protected void populateItem( final ListItem<Segment> item ) {
                AjaxLink<String> otherPlanLink = new AjaxLink<String>( "otherSegmentLink" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        setSegment( item.getModelObject() );
                        update( target, new Change( Change.Type.Selected, getSegment() ) );
                    }
                };
                otherPlanLink.add( new Label( "otherSegmentName", item.getModelObject().toString() ) );
                item.add( otherPlanLink );
            }
        };
        planPath.add( otherSegmentsListView );
    }

    private List<Segment> getOtherSegments() {
        List<Segment> otherSegments = new ArrayList<Segment>( getAllSegments() );
        otherSegments.remove( getSegment() );
        return otherSegments;
    }


    private void addMaximizedFlowPanel( Change change ) {
        if ( flowMaximized ) {
            boolean showGoals;
            boolean showConnectors;
            boolean hideNoop;
            String props = change.getProperty();
            showGoals = props != null && props.contains( "showGoals" );
            showConnectors = props != null && props.contains( "showConnectors" );
            hideNoop = props != null && props.contains( "hideNoop" );
            maximizedFlowPanel = new MaximizedFlowPanel(
                    "maximized-flow",
                    new PropertyModel<Segment>( this, "segment" ),
                    new PropertyModel<Part>( this, "part" ),
                    showGoals,
                    showConnectors,
                    hideNoop );
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

    private void addAllFeedbackPanel() {
        addAllFeedbackPanel( null );
    }

    private void addDataCollectionPanel() {
        addDataCollectionPanel( null, null );
    }

    private void addAllFeedbackPanel( Feedback feedback ) {
        if ( !expansions.contains( Feedback.UNKNOWN.getId() ) ) {
            allFeedbackPanel = new Label( "feedbacks", "" );
            allFeedbackPanel.setOutputMarkupId( true );
            makeVisible( allFeedbackPanel, false );
        } else {
            allFeedbackPanel = new AllFeedbackFloatingPanel(
                    "feedbacks",
                    new Model<Plan>( getPlan() ),
                    true );
            if ( !feedback.isUnknown() ) {
                ( (AllFeedbackFloatingPanel) allFeedbackPanel ).select( feedback );
            }
        }
        form.addOrReplace( allFeedbackPanel );
    }

    private void addDataCollectionPanel( RFISurvey rfiSurvey, Change change ) {
        if ( !expansions.contains( RFISurvey.UNKNOWN.getId() ) ) {
            allSurveyPanel = new Label( "dataCollection", "" );
            allSurveyPanel.setOutputMarkupId( true );
            makeVisible( allSurveyPanel, false );
        } else {
            if ( change == null || !change.hasQualifier( "tab" ) )
                allSurveyPanel = new DataCollectionPanel(
                        "dataCollection",
                        new Model<RFISurvey>( rfiSurvey ) );
            else
                allSurveyPanel = new DataCollectionPanel(
                        "dataCollection",
                        new Model<RFISurvey>( rfiSurvey ),
                        (String) change.getQualifier( "tab" ) );
        }
        form.addOrReplace( allSurveyPanel );
    }

    private void addRequirementsPanel() {
        addRequirementsPanel( null );
    }

    private void addRequirementsPanel( Requirement requirement ) {
        if ( !expansions.contains( Requirement.UNKNOWN.getId() ) ) {
            planRequirementsPanel = new Label( "requirements", "" );
            planRequirementsPanel.setOutputMarkupId( true );
            makeVisible( planRequirementsPanel, false );
        } else {
            planRequirementsPanel = new PlanRequirementsFloatingPanel(
                    "requirements",
                    new Model<Requirement>( requirement ) );
        }
        form.addOrReplace( planRequirementsPanel );
    }

    private void addAllEventsPanel() {
        if ( !expansions.contains( Channels.ALL_EVENTS ) ) {
            allEventsPanel = new Label( "allEvents", "" );
            allEventsPanel.setOutputMarkupId( true );
            makeVisible( allEventsPanel, false );
        } else {
            allEventsPanel = new PlanEventsFloatingPanel(
                    "allEvents",
                    new Model<Event>( Event.UNKNOWN ) );
        }
        form.addOrReplace( allEventsPanel );
    }

    private void addAllOrganizationsPanel() {
        if ( !expansions.contains( Channels.ALL_ORGANIZATIONS ) ) {
            allOrganizationsPanel = new Label( "allOrganizations", "" );
            allOrganizationsPanel.setOutputMarkupId( true );
            makeVisible( allOrganizationsPanel, false );
        } else {
            allOrganizationsPanel = new PlanOrganizationsFloatingPanel(
                    "allOrganizations",
                    new Model<Organization>( Organization.UNKNOWN ) );
        }
        form.addOrReplace( allOrganizationsPanel );
    }

    private void addAllSegmentsPanel() {
        if ( !expansions.contains( Channels.ALL_SEGMENTS ) ) {
            allSegmentsPanel = new Label( "allSegments", "" );
            allSegmentsPanel.setOutputMarkupId( true );
            makeVisible( allSegmentsPanel, false );
        } else {
            allSegmentsPanel = new PlanSegmentsFloatingPanel(
                    "allSegments",
                    new Model<Plan>( getPlan() ) );
        }
        form.addOrReplace( allSegmentsPanel );
    }

    private void addAllClassificationsPanel() {
        if ( !expansions.contains( Channels.ALL_CLASSIFICATIONS ) ) {
            allClassificationsPanel = new Label( "classifications", "" );
            allClassificationsPanel.setOutputMarkupId( true );
            makeVisible( allClassificationsPanel, false );
        } else {
            allClassificationsPanel = new PlanClassificationsFloatingPanel(
                    "classifications",
                    new Model<Plan>( getPlan() ) );
        }
        form.addOrReplace( allClassificationsPanel );
    }

    private void addTaskMoverPanel() {
        if ( !expansions.contains( Channels.TASK_MOVER ) ) {
            taskMoverPanel = new Label( "taskMover", "" );
            taskMoverPanel.setOutputMarkupId( true );
            makeVisible( taskMoverPanel, false );
        } else {
            taskMoverPanel = new TaskMoverFloatingPanel(
                    "taskMover",
                    new Model<Segment>( getSegment() ) );
        }
        form.addOrReplace( taskMoverPanel );
    }

    private void addProtocolsMapPanel() {
        if ( !expansions.contains( Channels.PROTOCOLS_MAP ) ) {
            protocolsMapPanel = new Label( "protocolsMap", "" );
            protocolsMapPanel.setOutputMarkupId( true );
            makeVisible( protocolsMapPanel, false );
        } else {
            protocolsMapPanel = new ProtocolsMapFloatingPanel(
                    "protocolsMap",
                    new Model<Plan>( getPlan() ) );
        }
        form.addOrReplace( protocolsMapPanel );
    }

    private void addPlanEvaluationPanel() {
        if ( !expansions.contains( Channels.PLAN_EVALUATION ) ) {
            planEvaluationPanel = new Label( "planEvaluation", "" );
            planEvaluationPanel.setOutputMarkupId( true );
            makeVisible( planEvaluationPanel, false );
        } else {
            planEvaluationPanel = new PlanEvaluationFloatingPanel(
                    "planEvaluation",
                    new Model<Plan>( getPlan() ) );
        }
        form.addOrReplace( planEvaluationPanel );
    }

    private void addAllIssuesPanel() {
        if ( !expansions.contains( Channels.ALL_ISSUES ) ) {
            allIssuesPanel = new Label( "allIssues", "" );
            allIssuesPanel.setOutputMarkupId( true );
            makeVisible( allIssuesPanel, false );
        } else {
            allIssuesPanel = new AllIssuesFloatingPanel(
                    "allIssues",
                    new Model<Plan>( getPlan() ) );
        }
        form.addOrReplace( allIssuesPanel );
    }

    private void addPlanVersionsPanel() {
        if ( !expansions.contains( Channels.PLAN_VERSIONS ) ) {
            planVersionsPanel = new Label( "planVersions", "" );
            planVersionsPanel.setOutputMarkupId( true );
            makeVisible( planVersionsPanel, false );
        } else {
            planVersionsPanel = new PlanVersionsFloatingPanel(
                    "planVersions",
                    new Model<Plan>( getPlan() ) );
        }
        form.addOrReplace( planVersionsPanel );
    }

    private void addPlanParticipationPanel() {
        if ( !expansions.contains( Channels.PLAN_PARTICIPATION ) ) {
            planParticipationPanel = new Label( "planParticipation", "" );
            planParticipationPanel.setOutputMarkupId( true );
            makeVisible( planParticipationPanel, false );
        } else {
            planParticipationPanel = new PlanParticipationFloatingPanel(
                    "planParticipation",
                    new Model<Plan>( getPlan() ) );
        }
        form.addOrReplace( planParticipationPanel );
    }

    private void addPlanIndexPanel() {
        if ( !expansions.contains( Channels.PLAN_INDEX ) ) {
            planIndexPanel = new Label( "planIndex", "" );
            planIndexPanel.setOutputMarkupId( true );
            makeVisible( planIndexPanel, false );
        } else {
            planIndexPanel = new PlanIndexFloatingPanel(
                    "planIndex",
                    new Model<Plan>( getPlan() ) );
        }
        form.addOrReplace( planIndexPanel );
    }

    private void addAllTagsPanel() {
        if ( !expansions.contains( Channels.ALL_TAGS ) ) {
            allTagsPanel = new Label( "allTags", "" );
            allTagsPanel.setOutputMarkupId( true );
            makeVisible( allTagsPanel, false );
        } else {
            allTagsPanel = new AllTagsFloatingPanel(
                    "allTags",
                    new Model<Plan>( getPlan() ) );
        }
        form.addOrReplace( allTagsPanel );
    }

    private void addAllTypesPanel() {
        if ( !expansions.contains( Channels.ALL_TYPES ) ) {
            allTypesPanel = new Label( "allTypes", "" );
            allTypesPanel.setOutputMarkupId( true );
            makeVisible( allTypesPanel, false );
        } else {
            allTypesPanel = new AllTypesFloatingPanel(
                    "allTypes",
                    new Model<Plan>( getPlan() ) );
        }
        form.addOrReplace( allTypesPanel );
    }

    private void addWhosWhoPanel() {
        if ( !expansions.contains( Channels.WHOS_WHO ) ) {
            whosWhoPanel = new Label( "whoswho", "" );
            whosWhoPanel.setOutputMarkupId( true );
            makeVisible( whosWhoPanel, false );
        } else {
            whosWhoPanel = new WhosWhoFloatingPanel(
                    "whoswho",
                    new Model<Plan>( getPlan() ) );
        }
        form.addOrReplace( whosWhoPanel );
    }

    private void addBibliographyPanel() {
        if ( !expansions.contains( Channels.BIBLIOGRAPHY ) ) {
            bibliographyPanel = new Label( "bibliography", "" );
            bibliographyPanel.setOutputMarkupId( true );
            makeVisible( bibliographyPanel, false );
        } else {
            bibliographyPanel = new BibliographyFloatingPanel(
                    "bibliography",
                    new Model<Plan>( getPlan() ) );
        }
        form.addOrReplace( bibliographyPanel );
    }


    /**
     * Get aspect of segment shown.
     *
     * @return a string
     */
    public String getSegmentAspect() {
        return getAspectShown( getSegment() );
    }

    /*   private void addSegmentDescriptionLabel() {
            segmentDescriptionLabel = new Label( "sg-desc",
                    new AbstractReadOnlyModel<String>() {
                        @Override
                        public String getObject() {
                            return StringUtils.abbreviate( StringUtils.capitalize(
                                    segment.getPhaseEventTitle() ),
                                    SEGMENT_DESCRIPTION_MAX_LENGTH );
                        }
                    } );
            segmentDescriptionLabel.setOutputMarkupId( true );
            segmentDescriptionLabel.add(
                    new AttributeModifier(
                            "title",
                            new AbstractReadOnlyModel<String>() {
                                @Override
                                public String getObject() {
                                    return segment.getPhaseEventTitle();
                                }
                            } ) );
            form.addOrReplace( segmentDescriptionLabel );
        }
    */

    private void addFooter() {
        form.add( new Label( "user",
                getUser().getUsername() ) );
    }

    /*   private void addSegmentImportDialog() {
            segmentImportPanel = new SegmentImportPanel( "segment-import" );
            form.add( segmentImportPanel );
        }
    */

    private void addSegmentPanel() {
        segmentPanel = new SegmentPanel( "segment",
                new PropertyModel<Segment>( this, "segment" ),
                new PropertyModel<Part>( this, "part" ),
                getReadOnlyExpansions() );
        form.add( segmentPanel );
    }


    private void addHelp() {
        BookmarkablePageLink<HelpPage> helpLink = new BookmarkablePageLink<HelpPage>( "help-link", HelpPage.class );
        helpLink.add( new AttributeModifier( "target", new Model<String>( "help" ) ) );
        /* helpLink.setPopupSettings( new PopupSettings(
PopupSettings.RESIZABLE |
      PopupSettings.SCROLLBARS |
      PopupSettings.MENU_BAR |
      PopupSettings.TOOL_BAR ) );*/

        form.add( helpLink );
    }

    private void addNotifier( WebMarkupContainer body ) {
        notifier = new NotifierWebMarkupContainer( "notifier" );
        body.add( notifier );
    }

    private String getMessage() {
        return message == null ? "" : message;
    }


    private void doTimedUpdate( AjaxRequestTarget target ) {
        if ( getCommander().isOutOfSync( getUser().getUsername() ) ) {
            showNewPlanVersionWarningDialog( target );
        }
        getCommander().keepAlive( getUser().getUsername(), REFRESH_DELAY );
        getCommander().processTimeOuts();
        if ( getCommander().isTimedOut( getUser().getUsername() ) ) {
            if ( getPlan().isDevelopment() ) refreshAll( target );
            getCommander().clearTimeOut( getUser().getUsername() );
        } else {
            updateRefreshNowNotice();
            if ( getPlan().isDevelopment() ) {
                target.add( refreshNeededComponent );
            }
        }
        segmentPanel.updateSocialPanel( target );
    }

    private void showNewPlanVersionWarningDialog( AjaxRequestTarget target ) {
        WarningPanel warningPanel = new WarningPanel( getModalContentId(), new Model<String>(
                "There is a new version of the plan. "
                        + "Closing this alert will switch you to it." ) );
        showDialog(
                "Warning: New Plan Version",
                200,
                300,
                warningPanel,
                PlanPage.this,
                target
        );
    }

    private void updateRefreshNowNotice() {
        String reasonsToRefresh = getReasonsToRefresh();
        if ( !reasonsToRefresh.isEmpty() ) {
            LOG.debug( "Refresh now requested" );
        }
        makeVisible( refreshNeededComponent, !reasonsToRefresh.isEmpty() );
        refreshNeededComponent.add( new AttributeModifier( "title", new Model<String>(
                "Refresh:" + reasonsToRefresh ) ) );
    }

    private String getReasonsToRefresh() {
        String reasons = "";
        if ( getPlan().isDevelopment() ) {
            String lastModifier = getCommander().getLastModifier();
            long lastModified = getCommander().getLastModified();
            if ( lastModified > lastRefreshed && !lastModifier.isEmpty() && !lastModifier.equals(
                    getUser().getUsername() ) )
                reasons = " -- Plan was modified by " + lastModifier;

            // Find expansions that were locked and are now unlocked
            for ( ModelObject mo : getEditableModelObjects( expansions ) ) {
                if ( !getCommander().isLockedByUser( getUser().getUsername(), mo ) ) {
                    String aspect = getAspectShown( mo );
                    if ( aspect == null || aspectRequiresLock( mo, aspect ) )
                        if ( getCommander().isUnlocked( mo ) ) {
                            reasons += " -- " + mo.getLabel() + " can now be edited.";
                        }
                }
            }
        }
        return reasons;
    }

    private Set<ModelObject> getEditableModelObjects( Set<Long> expansions ) {
        Set<ModelObject> editables = new HashSet<ModelObject>();
        for ( Long id : expansions ) {
            try {
                editables.add( getQueryService().find( ModelObject.class, id ) );
            } catch ( NotFoundException ignored ) {
                // ignore
            }
        }
        return editables;
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
                    getPart().isSend( flowViewed ),
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

    private void addDisseminationPanel( Subject subject, boolean showTargets ) {
        SegmentObject segmentObject = (SegmentObject) getModelObjectViewed( ModelObject.class, "dissemination" );
        if ( segmentObject == null ) {
            disseminationPanel = new Label( "dissemination", "" );
            disseminationPanel.setOutputMarkupId( true );
            makeVisible( disseminationPanel, false );
        } else {
            disseminationPanel = new DisseminationPanel( "dissemination",
                    new Model<SegmentObject>( segmentObject ),
                    subject,
                    showTargets,
                    getReadOnlyExpansions() );
        }
        form.addOrReplace( disseminationPanel );
    }

    private void addModelObjectSurveysPanel() {
        ModelObject modelObject = getModelObjectViewed( ModelObject.class, "surveys" );
        if ( modelObject == null ) {
            modelObjectSurveysPanel = new Label( "moSurveys", "" );
            modelObjectSurveysPanel.setOutputMarkupId( true );
            makeVisible( modelObjectSurveysPanel, false );
        } else {
            modelObjectSurveysPanel = new ModelObjectSurveysPanel(
                    "moSurveys",
                    new Model<ModelObject>( modelObject ) );
        }
        form.addOrReplace( modelObjectSurveysPanel );

    }

    private void addOverridesPanel() {
        Part viewed = (Part) getModelObjectViewed( ModelObject.class, "overrides" );
        if ( viewed == null ) {
            overridesPanel = new Label( "overrides", "" );
            overridesPanel.setOutputMarkupId( true );
            makeVisible( overridesPanel, false );
        } else {
            overridesPanel = new OverridesPanel( "overrides",
                    new Model<Part>( viewed ),
                    getReadOnlyExpansions() );
        }
        form.addOrReplace( overridesPanel );
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

    private void addPlanEditPanel( Change change ) {
        Plan plan = getPlan();
        boolean showPlanEdit = expansions.contains( plan.getId() );
        if ( showPlanEdit ) {
            planEditPanel = new PlanEditPanel( "plan",
                    new Model<Plan>( plan ),
                    getReadOnlyExpansions(),
                    getAspectShown( plan ),
                    change );
        } else {
            planEditPanel = new Label( "plan", "" );
            planEditPanel.setOutputMarkupId( true );
            makeVisible( planEditPanel, false );
        }
        form.addOrReplace( planEditPanel );
    }

    private ModelEntity findExpandedEntity() {
        for ( long id : expansions ) {
            try {
                ModelObject mo = getQueryService().find( ModelObject.class, id );
                if ( mo.isEntity() )
                    return ( (ModelEntity) mo );
            } catch ( NotFoundException ignored ) {
                // ignore
            }
        }
        return null;
    }

    private ModelObject findExpanded( ModelObject subject ) {
        Class clazz = subject instanceof ModelEntity
                ? ModelEntity.class
                : subject.getClass();
        for ( long id : expansions ) {
            try {
                ModelObject mo = getQueryService().find( ModelObject.class, id );
                if ( clazz.isAssignableFrom( mo.getClass() ) )
                    return ( mo );
            } catch ( NotFoundException ignored ) {
                // ignore
            }
        }
        return null;
    }


    public List<Segment> getAllSegments() {
        List<Segment> allSegments = new ArrayList<Segment>( getQueryService().list( Segment.class ) );
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
        if ( parameters.getNamedKeys().contains( SEGMENT_PARM ) )
            try {
                return queryService.find( Segment.class, parameters.get( SEGMENT_PARM ).toLong() );
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
        if ( parameters.getNamedKeys().contains( PART_PARM ) )
            try {
                if ( segment != null )
                    return (Part) segment.getNode( parameters.get( PART_PARM ).toLong() );
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
/*
    public void redirectTo( Segment segment ) {
        redirectTo( segment.getDefaultPart() );
    }
*/

    /**
     * Redirect to current plan page.
     */
    public void redirectToPlan() {
        setResponsePage( PlanPage.class, planParameters( getPlan() ) );
    }

    /**
     * redirect to a part.
     *                                                              "
     * @param p a part
     */
/*
    private void redirectTo( Part p ) {
        Set<Long> ids = Collections.emptySet();
        setResponsePage( new RedirectPage( SegmentLink.linkStringFor( p, ids, getPlan() ) ) );
    }
*/

    /**
     * Redirect here.
     */
/*
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
*/

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
        result.set( SEGMENT_PARM, Long.toString( segment.getId() ) );
        if ( p != null ) {
            result.set( PART_PARM, Long.toString( p.getId() ) );
            for ( long id : expanded )
                result.set( EXPAND_PARM, Long.toString( id ) );
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
        Set<Long> result = new HashSet<Long>();
        if ( parameters.getNamedKeys().contains( EXPAND_PARM ) ) {
            List<StringValue> stringList = parameters.getValues( EXPAND_PARM );
            for ( StringValue id : stringList )
                try {
                    result.add( id.toLong() );
                } catch ( NumberFormatException ignored ) {
                    LOG.warn( MessageFormat.format( "Invalid expansion parameter: {0}", id ) );
                }
        }
        return result;
    }

    /**
     * Get set or default part.
     *
     * @return a part
     */
    public Part getPart() {
        if ( isZombie( part ) ) {
            Part part = segment.getDefaultPart();
            getCommander().requestLockOn( getUser().getUsername(), part );
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
            result.set( EXPAND_PARM, Long.toString( id ) );
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
        List<StringValue> expanded = result.getValues( EXPAND_PARM );
        String idString = Long.toString( id );
        result.remove( EXPAND_PARM );
        if ( expanded != null ) {
            for ( StringValue exp : expanded )
                if ( !exp.toString().equals( idString ) )
                    result.set( EXPAND_PARM, exp );
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


    private void reacquireLocks() {
        // Part is always "expanded"
        getCommander().requestLockOn( getUser().getUsername(), getPart() );
        for ( Long id : expansions ) {
            if ( id >= 0 ) {
                try {
                    ModelObject expanded = getQueryService().find( ModelObject.class, id );
                    String aspect = getAspectShown( expanded );
                    if ( aspect != null )
                        tryAcquiringLockForAspect( new Change( Change.Type.NeedsRefresh, expanded ), aspect );
                    else
                        tryAcquiringLock( new Change( Change.Type.NeedsRefresh, expanded ) );
                    // getCommander().requestLockOn( expanded );
                }catch( NotFoundException e){
                    LOG.info( "Expanded model object not found at: " + id );
                }
            } else {
                tryAcquiringLock( new Change( Change.Type.NeedsRefresh, id ) );
            }
        }
    }

    private void expand( Identifiable identifiable ) {
        if ( identifiable != null )
            expand( new Change( Change.Type.None, identifiable ) );
    }

    private void expand( Change change ) {
        tryAcquiringLock( change );
        if ( isSingleExpansion( change ) ) {
            ModelObject subject = (ModelObject) change.getSubject( getQueryService() );
            ModelObject previous = findExpanded( subject );
            if ( previous != null && !previous.equals( subject ) ) {
                collapse( new Change( Change.Type.None, previous ) );
            }
        }
        expansions.add( change.getId() );
    }

    private boolean isSingleExpansion( Change change ) {
        return change.isForInstanceOf( ModelEntity.class );
    }

    private void tryAcquiringLock( Change change ) {
        if ( change.isByIdOnly() && getPlan().isDevelopment() ) {
            getCommander().requestLockOn( getUser().getUsername(), change.getId() );
        } else if ( change.isForInstanceOf( Identifiable.class ) ) {
            Identifiable identifiable = change.getSubject( getQueryService() );
            if ( !ModelObject.isUnknownModelObject( identifiable )
                    && ( identifiable.isModifiableInProduction() || getPlan().isDevelopment() )
                    && getCommander().isLockable( change.getClassName() ) ) {
                getCommander().requestLockOn( getUser().getUsername(), change.getId() );
            }
        }
    }

    private void expandOtherSegmentIfNeeded( Segment toExpand ) {
        Segment expanded = null;
        Iterator<Long> iter = expansions.iterator();
        Long id = null;
        while ( expanded == null && iter.hasNext() ) {
            try {
                id = iter.next();
                ModelObject mo = getQueryService().find( ModelObject.class, id );
                if ( mo instanceof Segment ) expanded = (Segment) mo;
            } catch ( NotFoundException e ) {
                LOG.info( "Failed to find expanded " + id );
            }
        }
        if ( expanded != null && !expanded.equals( toExpand ) ) {
            collapse( expanded );
            expand( toExpand );
        }
    }

    private boolean isExpanded( long id ) {
        return expansions.contains( id );
    }

    private void collapse( Identifiable identifiable ) {
        if ( identifiable != null )
            collapse( new Change( Change.Type.None, identifiable ) );
    }

    private void collapse( Change change ) {
        tryReleasingLock( change );
        expansions.remove( change.getId() );
        // Close aspects of collapsed object
        if ( change.isForInstanceOf( Flow.class ) ) {
            closeAspect( change, ExpandedFlowPanel.EOIS );
        } else if ( !( change.isForInstanceOf( Part.class ) ) )
            closeAspect( change, null );
    }

    private void tryReleasingLock( Change change ) {
        getCommander().releaseAnyLockOn( getUser().getUsername(), change.getId() );
    }

    private void tryAcquiringLockForAspect( Change change, String aspect ) {
        if ( aspectRequiresLock( change, aspect ) ) {
            tryAcquiringLock( change );
        }
    }

    private void viewAspect( Identifiable identifiable, String aspect ) {
        viewAspect( new Change( Change.Type.None, identifiable ), aspect );
    }

    private void viewAspect( Change change, String aspect ) {
        aspects.remove( change.getId() );
        // tryReleasingLock( change );
        if ( aspect != null && !aspect.isEmpty() ) {
            updateAspects( change, aspect );
        }
    }

    private void updateAspects( Change change, String aspect ) {
        List<String> aspectsShown = aspects.get( change.getId() );
        if ( aspectsShown == null )
            aspectsShown = new ArrayList<String>();
        if ( !aspectsShown.contains( aspect ) )
            aspectsShown.add( aspect );
        aspects.put( change.getId(), aspectsShown );
        tryAcquiringLockForAspect( change, aspect );
    }

    private boolean aspectRequiresLock( Change change, String aspect ) {
        return aspectRequiresLock( change.getSubject( getQueryService() ), aspect );
    }

    // TODO - deal with Channels.ALL_EVENTS etc., consolidate test in the *EditPanels
    private boolean aspectRequiresLock( Identifiable identifiable, String aspect ) {
        if ( aspect == null ) {
            return false;
        } else if ( aspect.equals( AbstractMultiAspectPanel.DETAILS ) ) {
            return true;
        } else if ( identifiable instanceof Segment ) {
            return aspect.equals( SegmentEditPanel.GOALS );
        } else if ( identifiable instanceof Flow )
            return aspect.equals( ExpandedFlowPanel.EOIS );
        else return false;
    }

    private boolean closingAspectReleasesLock( Change change, String aspect ) {
        return !( change.isForInstanceOf( Flow.class ) && aspect.equals( ExpandedFlowPanel.EOIS ) );
    }


    private void closeAspect( Identifiable identifiable, String aspect ) {
        closeAspect( new Change( Change.Type.None, identifiable ), aspect );
    }

    private void closeAspect( Change change, String aspect ) {
        if ( aspect == null || aspect.isEmpty() ) {
            aspects.remove( change.getId() );
        } else {
            List<String> aspectsShown = aspects.get( change.getId() );
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
                LOG.info( "Viewed object not found at " + id );
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
                LOG.info( "Failed to find expanded " + id );
            }
        }
        for ( Identifiable identifiable : toCollapse ) {
            collapse( identifiable );
        }
    }

    private void expandFlow( Change change ) {
        Flow flowToExpand = (Flow) change.getSubject( getQueryService() );
        // collapse other flows
        List<Identifiable> toCollapse = new ArrayList<Identifiable>();
        for ( long id : expansions ) {
            try {
                ModelObject expanded = getQueryService().find( ModelObject.class, id );
                if ( expanded instanceof Flow ) {
                    toCollapse.add( expanded );
                }
            } catch ( NotFoundException e ) {
                LOG.info( "Failed to find expanded " + id );
            }
        }
        for ( Identifiable identifiable : toCollapse ) {
            collapse( identifiable );
        }
        expand( flowToExpand );
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
                LOG.info( "Failed to find expanded " + id );
            }
        }
        for ( Identifiable identifiable : toCollapse ) {
            collapse( identifiable );
        }
    }

    public boolean isShowSimpleForm( Identifiable identifiable ) {
        if ( showSimpleForm.containsKey( identifiable.getId() ) )
            return showSimpleForm.get( identifiable.getId() );
        else return true;
    }

    public void setShowSimpleForm( Identifiable identifiable, boolean simple ) {
        showSimpleForm.put( identifiable.getId(), simple );
    }

    /**
     * {@inheritDoc}
     */
    public void changed( Change change ) {
        getCommander().clearTimeOut( getUser().getUsername() );
        translateChange( change );
        if ( change.getMessage() != null ) {
            message = change.getMessage();
        }
        if ( change.isNone() )
            return;
        else
            getCommander().updateUserActive( getUser().getUsername() );
        if ( change.isUnknown() ) {
            if ( !getPlan().getSegments().contains( segment ) ) {
                segment = getPlan().getDefaultSegment();
                setPart( null );
            }
        } else if ( change.isCollapsed() || change.isRemoved() )
            collapse( change );
        else if ( change.isExpanded() || change.isAdded() ) {
            if ( change.isForInstanceOf( Flow.class ) )
                expandFlow( change );
            else
                expand( change );
            if ( change.getProperty() != null ) {
                viewAspect( change, change.getProperty() );
            }
        } else if ( change.isAspectViewed() ) {
            if ( change.isForInstanceOf( Flow.class ) ) {
                Flow otherFlowViewed = getModelObjectViewed( Flow.class, change.getProperty() );
                if ( otherFlowViewed != null )
                    closeAspect( otherFlowViewed, change.getProperty() );
            }
            viewAspect( change, change.getProperty() );
        } else if ( change.isAspectClosed() )
            closeAspect( change, change.getProperty() );
        else if ( change.isAspectReplaced() ) {
            closeAspect( change, null );
            viewAspect( change, change.getProperty() );
        } else if ( change.isCommunicated() ) {
            expand( new Change( Change.Type.Expanded, Channels.SOCIAL_ID ) );
        }
        if ( change.isForInstanceOf( Segment.class ) ) {
            Segment changedSegment = (Segment) change.getSubject( getQueryService() );
            if ( change.isExists() ) {
                getCommander().resetUserHistory( getUser().getUsername(), false );
                if ( change.isAdded() ) {
                    setSegment( changedSegment );
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
                setSegment( changedSegment );
                expandOtherSegmentIfNeeded( changedSegment );
                setPart( null );
            } else if ( change.isMaximized() ) {
                flowMaximized = true;
            } else if ( change.isMinimized() ) {
                flowMaximized = false;
            } else if ( change.isForProperty( "legend" ) ) {
                flowsExplained = change.isExplained();
            }
        } else if ( change.isForInstanceOf( Part.class ) ) {
            Part changedPart = (Part) change.getSubject( getQueryService() );
            if ( change.isAdded() || change.isSelected() ) {
                setPart( changedPart );
                flowMaximized = false;
                if ( change.isAdded() )
                    expand( change );
            } else if ( change.isRemoved() ) {
                collapse( getPart() );
                collapsePartObjects();
                setPart( null );
                expand( getPart() );
            }
        } else if ( change.isForInstanceOf( Flow.class ) ) {
            Flow changedFlow = (Flow) change.getSubject( getQueryService() );
            if ( change.isUpdated() && change.isForProperty( "other" ) ) {
                expandFlow( change );
            } else if ( change.isSelected() ) {
                /* if ( flowMaximized ) {
                    change.setType( Change.Type.Recomposed );
                } else {
                    change.setType( Change.Type.Expanded );
                }*/
                flowMaximized = false;
                if ( !changedFlow.getSegment().equals( segment ) ) {
                    setSegment( changedFlow.getSegment() );
                    // change.setType( Change.Type.Recomposed );
                }
                if ( !changedFlow.hasPart( getPart() ) ) {
                    setPart( changedFlow.getLocalPart() );
//                    change.setType( Change.Type.Recomposed );
                }
                collapseSegmentObjects();
                expandFlow( change );
            }
        } else if ( change.isForInstanceOf( UserIssue.class ) && change.isAdded() ) {
            UserIssue userIssue = (UserIssue) change.getSubject( getQueryService() );
            ModelObject mo = userIssue.getAbout();
            if ( mo instanceof Segment ) {
                expand( userIssue );
            }
        }
        // rememberState();
    }

    private void translateChange( Change change ) {
        if ( change.isForInstanceOf( Requirement.class ) && ( change.isSelected() || change.isExpanded() ) ) {
            change.setType( Change.Type.Expanded );
            change.addQualifier( "requirement", change.getId() );
            change.setSubject( Requirement.UNKNOWN );
            change.setId( Requirement.UNKNOWN.getId() );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        // Hide message panel on changed message ( not null )
        String message = change.getMessage();
        if ( message != null ) {
            notifier.create( target,
                    "Notification",
                    message );
            if ( message.contains( "copied" ) ) {
                refreshAllMenus( target );
            }
        }
        if ( !change.isNone() ) {
            refreshAllMenus( target );
            segmentPanel.updateGuidePanel( target );
            if ( change.isForInstanceOf( Plan.class ) && change.isSelected() ) {  // Not caused anymore
                redirectToPlan();
            } else if ( change.isAspectReplaced() ) {
                replaceAspect( change, target );
            } else if ( change.isAspectViewed()
                    || change.isAspectClosed()
                    || change.isExpanded()
                    || ( change.isCollapsed() && changes.get( change.getId() ) == null ) ) {
                openOrCloseChild( change, target );
            } else if ( change.isUndoing() || change.isUnknown() || change.isRecomposed()
                    || change.isAdded() && change.isForInstanceOf( Part.class ) ) {
                refresh( target, change, new ArrayList<Updatable>() );
            } else if ( change.isUpdated() && isExpanded( change.getId() ) ) {
                Change accumulatedChange = changes.get( change.getId() );
                if ( accumulatedChange == null ) {
                    changes.put( change.getId(), change );
                } else {
                    // more than one property changed
                    change.setProperty( "?" );
                }
            } else if ( change.isCollapsed() && changes.get( change.getId() ) != null ) {
                refresh( target, change, updated );
            } else if ( change.isForInstanceOf( Flow.class ) && change.isExpanded() ) {
                updateMaximizedFlow( target, change );
                refreshHeadersMenusAndNavigation( target, change, updated );
                // refreshSegmentPanel( target, change, updated );
            }/* else if ( change.isCopied() ) {
                refreshAllMenus( target );
            } */ else if ( change.isRefreshNeeded() ) {
                change.setScript( "alert('The action failed because the page was out of sync.');" );
                refreshAll( target );
            } else if ( change.isCommunicated() ) {
                segmentPanel.newMessage( target, change );
                segmentPanel.refreshSocialPanel( target, change );
            } else if ( change.isUnexplained() || change.isExplained() ) {
                updateFlowLegend( target );
            } else {
                refresh( target, change, updated );
            }
        }
        if ( change.getScript() != null ) {
            target.appendJavaScript( change.getScript() );
        }
    }

    private void openOrCloseChild( Change change, AjaxRequestTarget target ) {
        List<Updatable> updated = new ArrayList<Updatable>();
        if ( change.isForInstanceOf( ModelObject.class ) && change.isForProperty( "surveys" ) ) {
            refreshModelObjectSurveysPanel( target, change, updated );
        } else if ( change.isForInstanceOf( Plan.class ) ) {
            refreshPlanEditPanel( target, change, updated );
        } else if ( change.isForInstanceOf( Segment.class ) ) {
            refreshSegmentEditPanel( target, change, updated );
        } else if ( change.isForInstanceOf( ModelEntity.class ) ) {
            refreshEntityPanel( target, change, updated );
        } else if ( change.isForInstanceOf( Part.class ) && change.isForProperty( "assignments" ) ) {
            refreshAssignmentsPanel( target, change, updated );
        } else if ( change.isForInstanceOf( Part.class ) && change.isForProperty( "overrides" ) ) {
            refreshOverridesPanel( target, change, updated );
        } else if ( change.isForInstanceOf( SegmentObject.class ) && change.isForProperty( "failure" ) ) {
            refreshFailureImpactsPanel( target, change, updated );
        } else if ( change.isForInstanceOf( SegmentObject.class ) && change.isForProperty( "dissemination" ) ) {
            refreshDisseminationPanel( target, change, updated );
        } else if ( change.isForInstanceOf( Flow.class ) && change.isForProperty( "commitments" ) ) {
            refreshCommitmentsPanel( target, change, updated );
        } else if ( change.isForInstanceOf( Flow.class ) && change.isForProperty( "eois" ) ) {
            refreshEOIsPanel( target, change, updated );
            if ( (Boolean) change.getQualifier( "updated" ) ) {
                refreshSegmentPanel( target, change, updated );
            }
        } else if ( change.getId() == Channels.SOCIAL_ID
                || change.getId() == Channels.GUIDE_ID
                || change.isForInstanceOf( SegmentObject.class ) ) {
            refreshSegmentPanel( target, change, updated );
            /*} else if ( change.isForInstanceOf( Survey.class ) ) {
                refreshSurveysPanel( target, change, updated );
            */
        } else if ( change.isForInstanceOf( Feedback.class ) ) {
            refreshAllFeedbackPanel( target, change, updated );
        } else if ( change.isForInstanceOf( Requirement.class ) ) {
            refreshRequirementsPanel( target, change, updated );
        } else if ( change.getId() == Channels.ALL_EVENTS ) {
            refreshAllEventsPanel( target, change, updated );
        } else if ( change.getId() == Channels.ALL_ORGANIZATIONS ) {
            refreshAllOrganizationsPanel( target, change, updated );
        } else if ( change.getId() == Channels.ALL_SEGMENTS ) {
            refreshAllSegmentsPanel( target, change, updated );
        } else if ( change.getId() == Channels.ALL_CLASSIFICATIONS ) {
            refreshAllClassificationsPanel( target, change, updated );
        } else if ( change.getId() == Channels.TASK_MOVER ) {
            refreshTaskMoverPanel( target, change, updated );
        } else if ( change.getId() == Channels.PROTOCOLS_MAP ) {
            refreshProtocolsMapPanel( target, change, updated );
        } else if ( change.getId() == Channels.PLAN_EVALUATION ) {
            refreshPlanEvaluationPanel( target, change, updated );
        } else if ( change.getId() == Channels.ALL_ISSUES ) {
            refreshAllIssuesPanel( target, change, updated );
        } else if ( change.getId() == Channels.PLAN_VERSIONS ) {
            refreshPlanVersionsPanel( target, change, updated );
        } else if ( change.getId() == Channels.PLAN_INDEX ) {
            refreshPlanIndexPanel( target, change, updated );
        } else if ( change.getId() == Channels.ALL_TAGS ) {
            refreshAllTagsPanel( target, change, updated );
        } else if ( change.getId() == Channels.ALL_TYPES ) {
            refreshAllTypesPanel( target, change, updated );
        } else if ( change.getId() == Channels.WHOS_WHO ) {
            refreshWhosWhoPanel( target, change, updated );
        } else if ( change.getId() == Channels.BIBLIOGRAPHY ) {
            refreshBibliographyPanel( target, change, updated );
        } else if ( change.getId() == Channels.PLAN_PARTICIPATION ) {
            refreshPlanParticipationPanel( target, change, updated );
        } else if ( change.isForInstanceOf( RFISurvey.class ) ) {
            refreshDataCollectionPanel( target, change, updated );
        }
        refreshHeadersMenusAndNavigation( target, change, updated );
    }

    private void replaceAspect( Change change, AjaxRequestTarget target ) {
        String aspect = change.getProperty();
        Component maPanel =
                change.isForInstanceOf( Plan.class )
                        ? planEditPanel
                        : change.isForInstanceOf( Segment.class )
                        ? segmentEditPanel
                        : change.isForInstanceOf( ModelEntity.class )
                        ? entityPanel
                        : null;
        if ( maPanel != null && maPanel instanceof AbstractMultiAspectPanel ) {
            ( (AbstractMultiAspectPanel) maPanel ).showAspect( aspect, change, target );
        } else {
            LOG.warn( "Aspect not replaced from" + change );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void update( AjaxRequestTarget target, Object object, String action ) {
        // Do nothing
    }

    private void refreshAll( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Refresh );
        // change.setProperty( "refresh" );
        refresh( target, change, new ArrayList<Updatable>() );
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
        updateRefresh( target );
        updateNavigation( target );
        refreshChildren( target, change, updated );
        refreshHeadersMenusAndNavigation( target, change, updated );
        changes = new HashMap<Long, Change>();
    }

    private void refreshHeadersMenusAndNavigation( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        updateHeaders( target );
        refreshPlanMenus( target );
        updateNavigation( target );
    }

    /**
     * {@inheritDoc}
     */
    public void refresh( AjaxRequestTarget target, Change change ) {
        refresh( target, change, new ArrayList<Updatable>() );
    }

    private void refreshAllMenus( AjaxRequestTarget target ) {
        refreshPlanMenus( target );
        refreshChildrenMenus( target );
    }

    private void updateMaximizedFlow( AjaxRequestTarget target, Change change ) {
        if ( change.isRefresh() || change.isMaximized() || change.isMinimized() || change.isSelected() || change.isExpanded() || change.isRecomposed() ) {
            addMaximizedFlowPanel( change );
            if ( !flowMaximized ) segmentPanel.updateFlowMapOnMinimize( target, change );
            target.add( maximizedFlowPanel );
        }
    }

    private void updateFlowLegend( AjaxRequestTarget target ) {
        addFlowLegendPanel();
        target.add( flowLegendPanel );
    }

    private void updateAllFeedbackPanel( AjaxRequestTarget target ) {
        addAllFeedbackPanel();
        target.add( allFeedbackPanel );
    }

    private void updateDataCollectionPanel( AjaxRequestTarget target ) {
        addDataCollectionPanel();
        target.add( allSurveyPanel );
    }

    private void updateRefresh( AjaxRequestTarget target ) {
        updateRefreshNowNotice();
        target.add( refreshNeededComponent );
    }

    private void updateHeaders( AjaxRequestTarget target ) {
        addPlanPath();
        target.add( planPath );
        updateNavigation( target );
        addPartsMapLink();
        target.add( partsMapLink );
    }

    private void refreshPlanMenus( AjaxRequestTarget target ) {
        addActivitiesMenubar();
        addPlanActionsMenu();
        addPlanShowMenu();
        target.add( planActionsMenu );
        target.add( planShowMenu );
        target.add( scopingMenu );
        target.add( improvingMenu );
        target.add( participationMenu );
        target.add( searchingMenu );
    }


    private void refreshChildren(
            AjaxRequestTarget target,
            Change change,
            List<Updatable> updated ) {
        refreshPlanEditPanel( target, change, updated );
        refreshSegmentEditPanel( target, change, updated );
        refreshEntityPanel( target, change, updated );
        refreshAssignmentsPanel( target, change, updated );
        refreshCommitmentsPanel( target, change, updated );
        refreshEOIsPanel( target, change, updated );
        refreshSegmentPanel( target, change, updated );
        refreshFailureImpactsPanel( target, change, updated );
        refreshDisseminationPanel( target, change, updated );
        refreshModelObjectSurveysPanel( target, change, updated );
        refreshOverridesPanel( target, change, updated );
        refreshRequirementsPanel( target, change, updated );
        refreshAllEventsPanel( target, change, updated );
        refreshAllOrganizationsPanel( target, change, updated );
        refreshAllSegmentsPanel( target, change, updated );
        refreshAllClassificationsPanel( target, change, updated );
        refreshTaskMoverPanel( target, change, updated );
        refreshProtocolsMapPanel( target, change, updated );
        refreshPlanEvaluationPanel( target, change, updated );
        refreshAllIssuesPanel( target, change, updated );
        refreshPlanVersionsPanel( target, change, updated );
        refreshPlanParticipationPanel( target, change, updated );
        refreshPlanIndexPanel( target, change, updated );
        refreshAllTagsPanel( target, change, updated );
        refreshAllTypesPanel( target, change, updated );
        refreshWhosWhoPanel( target, change, updated );
        refreshBibliographyPanel( target, change, updated );
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
        if ( change.isForInstanceOf( SegmentObject.class )
                && ( change.isSelected() || change.isDisplay() || change.isExists() ) ) {
            segmentPanel.doRefresh( target, change );
            // target.add( segmentPanel );
        } else {
            segmentPanel.refresh( target, change, updated, getAspectShown( getSegment() ) );
        }
    }

    private void refreshPlanEditPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getQueryService() );
        Plan plan = getPlan();
        if ( change.isRefresh() ||
                change.isDisplay() && identifiable instanceof Plan ) {
            addPlanEditPanel( change );
            target.add( planEditPanel );
        } else if ( planEditPanel instanceof PlanEditPanel ) {
            ( (PlanEditPanel) planEditPanel ).refresh( target,
                    change,
                    updated,
                    getAspectShown( plan ) );
        }
    }

    private void refreshSegmentEditPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getQueryService() );
        if ( change.isRefresh() ||
                ( change.isDisplay() || change.isAdded() )
                        && identifiable != null
                        && identifiable instanceof Segment
                ||
                identifiable != null && change.isSelected() && identifiable instanceof Part ) {
            addSegmentEditPanel();
            target.add( segmentEditPanel );
            addPlanPath();
            target.add( planPath );
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
        if ( change.isRefresh() || entity == null ||
                change.isDisplay()
                        && change.isForInstanceOf( ModelEntity.class ) ) {
            addEntityPanel();
            target.add( entityPanel );
        } else if ( entityPanel instanceof EntityPanel ) {
            ( (EntityPanel) entityPanel ).refresh( target,
                    change,
                    updated,
                    getAspectShown( entity ) );
        }
    }                                                     //

    private void refreshAssignmentsPanel(
            AjaxRequestTarget target,
            Change change,
            List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getQueryService() );
        if ( change.isRefresh() ||
                identifiable != null
                        && identifiable instanceof Part
                        && change.isAspect( "assignments" ) ) {
            addAssignmentsPanel();
            target.add( assignmentsPanel );
        } else if ( assignmentsPanel instanceof PartAssignmentsPanel ) {
            ( (PartAssignmentsPanel) assignmentsPanel ).refresh( target, change, updated );
        }
    }

    private void refreshCommitmentsPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getQueryService() );
        if ( change.isRefresh() ||
                identifiable != null
                        && identifiable instanceof Flow
                        && change.isAspect( "commitments" ) ) {
            addCommitmentsPanel();
            target.add( commitmentsPanel );
        } else if ( commitmentsPanel instanceof SharingCommitmentsPanel ) {
            ( (SharingCommitmentsPanel) commitmentsPanel ).refresh( target, change, updated );
        }
    }

    private void refreshEOIsPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getQueryService() );
        if ( change.isRefresh() ||
                identifiable != null
                        && identifiable instanceof Flow
                        && ( change.isCollapsed()
                        || change.isAspect( "eois" ) ) ) {
            addEOIsPanel();
            target.add( eoisPanel );
        } else if ( eoisPanel instanceof FlowEOIsPanel ) {
            ( (FlowEOIsPanel) eoisPanel ).refresh( target, change, updated );
        }
    }

    private void refreshFailureImpactsPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getQueryService() );
        if ( change.isRefresh() ||
                identifiable != null
                        && identifiable instanceof SegmentObject
                        && change.isAspect(
                        "failure" ) ) {
            addFailureImpactsPanel();
            target.add( failureImpactsPanel );
        } else if ( failureImpactsPanel instanceof FailureImpactsPanel ) {
            ( (FailureImpactsPanel) failureImpactsPanel ).refresh( target, change, updated );
        }
    }

    private void refreshDisseminationPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getQueryService() );
        if ( change.isRefresh() ||
                identifiable != null && identifiable instanceof SegmentObject
                        && change.isAspect( "dissemination" ) ) {
            boolean showTargets = change.hasQualifier( "show", "targets" );
            Subject subject = (Subject) change.getQualifier( "subject" );
            addDisseminationPanel( subject, showTargets );
            target.add( disseminationPanel );
        } else if ( disseminationPanel instanceof DisseminationPanel ) {
            ( (DisseminationPanel) disseminationPanel ).refresh( target, change, updated );
        }
    }

    private void refreshModelObjectSurveysPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getQueryService() );
        if ( change.isRefresh() ||
                identifiable != null && identifiable instanceof ModelObject
                        && change.isAspect( "surveys" ) ) {
            addModelObjectSurveysPanel();
            target.add( modelObjectSurveysPanel );
        } else if ( modelObjectSurveysPanel instanceof ModelObjectSurveysPanel ) {
            ( (ModelObjectSurveysPanel) modelObjectSurveysPanel ).refresh( target, change, updated );
        }
    }

    private void refreshOverridesPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getQueryService() );
        if ( change.isRefresh() ||
                identifiable != null
                        && identifiable instanceof Part
                        && change.isAspect( "overrides" ) ) {
            addOverridesPanel();
            target.add( overridesPanel );
        } else if ( overridesPanel instanceof OverridesPanel ) {
            ( (OverridesPanel) overridesPanel ).refresh( target, change, updated );
        }
    }

/*
    private void refreshSurveysPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getQueryService() );
        if ( */
/*change.isUnknown() ||*//*

                identifiable != null
                        && change.isDisplay()
                        && identifiable instanceof Survey ) {
            Survey expandedSurvey = (Survey) identifiable;
            Survey viewedSurvey = ( expandedSurvey == null || expandedSurvey.isUnknown() )
                    ? Survey.UNKNOWN
                    : expandedSurvey;
           // addSurveysPanel( viewedSurvey );
            target.add( surveysPanel );
        } else if ( surveysPanel instanceof SurveysPanel ) {
            ( (SurveysPanel) surveysPanel ).refresh( target,
                    change,
                    updated,
                    getAspectShown( Survey.UNKNOWN ) );
        }
    }
*/

    private void refreshRequirementsPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getQueryService() );
        if ( change.isRefresh() ||
                identifiable != null
                        && change.isDisplay()
                        && identifiable instanceof Requirement ) {
            Requirement viewedRequirement = null;
            if ( change.hasQualifier( "requirement" ) ) {
                long id = (Long) change.getQualifier( "requirement" );
                try {
                    viewedRequirement = getQueryService().find( Requirement.class, id );
                } catch ( NotFoundException e ) {
                    LOG.warn( "Requirement not found: " + id );
                }
            }
            if ( viewedRequirement == null )
                viewedRequirement = Requirement.UNKNOWN;
            addRequirementsPanel( viewedRequirement );
            target.add( planRequirementsPanel );
        } else if ( planRequirementsPanel instanceof PlanRequirementsFloatingPanel ) {
            ( (PlanRequirementsFloatingPanel) planRequirementsPanel ).refresh( target,
                    change,
                    updated,
                    getAspectShown( Requirement.UNKNOWN ) );
        }
    }

    private void refreshAllEventsPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( change.isRefresh() ||
                ( id == Channels.ALL_EVENTS
                        && change.isDisplay() ) ) {
            addAllEventsPanel();
            target.add( allEventsPanel );
        } else if ( allEventsPanel instanceof PlanEventsFloatingPanel ) {
            ( (PlanEventsFloatingPanel) allEventsPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshAllOrganizationsPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( change.isRefresh() ||
                id == Channels.ALL_ORGANIZATIONS
                        && change.isDisplay() ) {
            addAllOrganizationsPanel();
            target.add( allOrganizationsPanel );
        } else if ( allOrganizationsPanel instanceof PlanOrganizationsFloatingPanel ) {
            ( (PlanOrganizationsFloatingPanel) allOrganizationsPanel ).refresh( target,
                    change,
                    updated,
                    getAspectShown( Organization.UNKNOWN ) );
        }
    }

    private void refreshAllSegmentsPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( change.isRefresh() ||
                id == Channels.ALL_SEGMENTS
                        && change.isDisplay() ) {
            addAllSegmentsPanel();
            target.add( allSegmentsPanel );
        } else if ( allSegmentsPanel instanceof PlanSegmentsFloatingPanel ) {
            ( (PlanSegmentsFloatingPanel) allSegmentsPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshAllClassificationsPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( change.isRefresh() ||
                id == Channels.ALL_CLASSIFICATIONS
                        && change.isDisplay() ) {
            addAllClassificationsPanel();
            target.add( allClassificationsPanel );
        } else if ( allClassificationsPanel instanceof PlanClassificationsFloatingPanel ) {
            ( (PlanClassificationsFloatingPanel) allClassificationsPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshTaskMoverPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( change.isRefresh() ||
                id == Channels.TASK_MOVER
                        && change.isDisplay() ) {
            addTaskMoverPanel();
            target.add( taskMoverPanel );
        } else if ( taskMoverPanel instanceof TaskMoverFloatingPanel ) {
            ( (TaskMoverFloatingPanel) taskMoverPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshProtocolsMapPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( change.isRefresh() ||
                id == Channels.PROTOCOLS_MAP
                        && change.isDisplay() ) {
            addProtocolsMapPanel();
            target.add( protocolsMapPanel );
        } else if ( protocolsMapPanel instanceof ProtocolsMapFloatingPanel ) {
            ( (ProtocolsMapFloatingPanel) protocolsMapPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshPlanEvaluationPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( change.isRefresh() ||
                id == Channels.PLAN_EVALUATION
                        && change.isDisplay() ) {
            addPlanEvaluationPanel();
            target.add( planEvaluationPanel );
        } else if ( planEvaluationPanel instanceof PlanEvaluationFloatingPanel ) {
            ( (PlanEvaluationFloatingPanel) planEvaluationPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshAllIssuesPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( change.isRefresh() ||
                id == Channels.ALL_ISSUES
                        && change.isDisplay() ) {
            addAllIssuesPanel();
            target.add( allIssuesPanel );
        } else if ( allIssuesPanel instanceof AllIssuesFloatingPanel ) {
            ( (AllIssuesFloatingPanel) allIssuesPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshPlanVersionsPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( change.isRefresh() ||
                id == Channels.PLAN_VERSIONS
                        && change.isDisplay() ) {
            addPlanVersionsPanel();
            target.add( planVersionsPanel );
        } else if ( planVersionsPanel instanceof PlanVersionsFloatingPanel ) {
            ( (PlanVersionsFloatingPanel) planVersionsPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshPlanParticipationPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( change.isRefresh() ||
                id == Channels.PLAN_PARTICIPATION
                        && change.isDisplay() ) {
            addPlanParticipationPanel();
            target.add( planParticipationPanel );
        } else if ( planParticipationPanel instanceof PlanParticipationFloatingPanel ) {
            ( (PlanParticipationFloatingPanel) planParticipationPanel ).refresh( target,
                    change,
                    updated );
        }
    }


    private void refreshAllFeedbackPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getQueryService() );
        if ( change.isRefresh() ||
                identifiable != null
                        && change.isDisplay()
                        && identifiable instanceof Feedback ) {
            Feedback expandedFeedback = (Feedback) identifiable;
            Feedback viewedFeedback = ( expandedFeedback == null || expandedFeedback.isUnknown() )
                    ? Feedback.UNKNOWN
                    : expandedFeedback;
            addAllFeedbackPanel( viewedFeedback );
            target.add( allFeedbackPanel );
        } else if ( allFeedbackPanel instanceof AllFeedbackFloatingPanel ) {
            ( (AllFeedbackFloatingPanel) allFeedbackPanel ).refresh( target,
                    change,
                    updated,
                    getAspectShown( Feedback.UNKNOWN ) );
        }
    }

    private void refreshDataCollectionPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getQueryService() );
        if ( change.isRefresh() ||
                identifiable != null
                        && change.isDisplay()
                        && identifiable instanceof RFISurvey ) {
            RFISurvey rfiSurvey = (RFISurvey) identifiable;
            RFISurvey viewedRFISurvey = ( rfiSurvey == null || rfiSurvey.isUnknown() )
                    ? RFISurvey.UNKNOWN
                    : rfiSurvey;
            addDataCollectionPanel( viewedRFISurvey, change );
            target.add( allSurveyPanel );
        } else if ( allSurveyPanel instanceof DataCollectionPanel ) {
            ( (DataCollectionPanel) allSurveyPanel ).refresh( target,
                    change,
                    updated,
                    getAspectShown( RFISurvey.UNKNOWN ) );
        }
    }

    private void refreshPlanIndexPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( change.isRefresh() ||
                id == Channels.PLAN_INDEX
                        && change.isDisplay() ) {
            addPlanIndexPanel();
            target.add( planIndexPanel );
        } else if ( planIndexPanel instanceof PlanIndexFloatingPanel ) {
            ( (PlanIndexFloatingPanel) planIndexPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshAllTagsPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( change.isRefresh() ||
                id == Channels.ALL_TAGS
                        && change.isDisplay() ) {
            addAllTagsPanel();
            target.add( allTagsPanel );
        } else if ( planIndexPanel instanceof AllTagsFloatingPanel ) {
            ( (AllTagsFloatingPanel) allTagsPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshAllTypesPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( change.isRefresh() ||
                id == Channels.ALL_TYPES
                        && change.isDisplay() ) {
            addAllTypesPanel();
            target.add( allTypesPanel );
        } else if ( allTypesPanel instanceof AllTypesFloatingPanel ) {
            ( (AllTypesFloatingPanel) allTypesPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshWhosWhoPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( change.isRefresh() ||
                id == Channels.WHOS_WHO
                        && change.isDisplay() ) {
            addWhosWhoPanel();
            target.add( whosWhoPanel );
        } else if ( whosWhoPanel instanceof WhosWhoFloatingPanel ) {
            ( (WhosWhoFloatingPanel) whosWhoPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshBibliographyPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( change.isRefresh() ||
                id == Channels.BIBLIOGRAPHY
                        && change.isDisplay() ) {
            addBibliographyPanel();
            target.add( bibliographyPanel );
        } else if ( bibliographyPanel instanceof BibliographyFloatingPanel ) {
            ( (BibliographyFloatingPanel) bibliographyPanel ).refresh( target,
                    change,
                    updated );
        }
    }


    /**
     * Get all plans that the current can modify.
     *
     * @return a list of plans
     */
    public List<Plan> getPlannablePlans() {
        return getPlanManager().getPlannablePlans( getUser() );
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

/*
/// PAGE HISTORY

    public void importSegment( AjaxRequestTarget target ) {
        segmentImportPanel.open( target );
    }
*/

/*
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

    */
/**
     * Collapse what's no longer expanded, expand what's not yet expanded,
     * change aspects viewed if needed for expanded,
     * set segment if different and exists, set part if different and exists
     *
     * @param pageState a page state
     * @param target    an ajax request target
     *//*

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
                expand( new Change( Change.Type.Expanded, id ) );
            }
        }
        for ( Long id : collapseSet ) {
            try {
                ModelObject toCollapse = getQueryService().find( ModelObject.class, id );
                collapse( toCollapse );
            } catch ( NotFoundException e ) {
                collapse( new Change( Change.Type.Collapsed, id ) );
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
*/

    /**
     * Dialog panel.
     */
    private class WarningPanel extends AbstractUpdatablePanel {

        private IModel<String> iModel;

        private WarningPanel( String id, IModel<String> iModel ) {
            super( id );
            this.iModel = iModel;
            Label alertLabel = new Label( "alert", iModel );
            add( alertLabel );
            AjaxLink<String> okLink = new AjaxLink<String>( "ok" ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    hideDialog( target );
                    redirectToPlan();
                }
            };
            add( okLink );
        }
    }

    /**
     * Page state.
     */
/*
    private class PageState implements Serializable {

        */
/**
         * Segment id.
         *//*

        private long segmentId;

        */
/**
         * Part id.
         *//*

        private long partId;

        */
/**
         * Expansions
         *//*

        private Set<Long> expanded;

        */
/**
         * Aspects viewed.
         *//*

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
*/
}


