/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages;

import com.google.code.jqwicket.ui.notifier.NotifierWebMarkupContainer;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.EOIsHolder;
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
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.model.Subject;
import com.mindalliance.channels.core.model.UserIssue;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.db.data.surveys.RFISurvey;
import com.mindalliance.channels.engine.analysis.Doctor;
import com.mindalliance.channels.pages.components.AbstractFloatingMultiAspectPanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.DisseminationPanel;
import com.mindalliance.channels.pages.components.GeomapLinkPanel;
import com.mindalliance.channels.pages.components.IndicatorAwareWebContainer;
import com.mindalliance.channels.pages.components.ModelObjectSurveysPanel;
import com.mindalliance.channels.pages.components.entities.EntityPanel;
import com.mindalliance.channels.pages.components.guide.IGuidePanel;
import com.mindalliance.channels.pages.components.help.HelpPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.ModelEditPanel;
import com.mindalliance.channels.pages.components.plan.floating.AllChecklistsFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.AllFeedbackFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.AllGoalsFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.AllIssuesFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.ChecklistsMapFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.CollaborationRhythmFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.ModelClassificationsFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.ModelEvaluationFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.ModelEventsAndPhasesFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.ModelInvolvementsFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.ModelSearchingFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.ModelSegmentsFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.ModelVersionsFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.SupplyChainsFloatingPanel;
import com.mindalliance.channels.pages.components.plan.floating.TaskMoverFloatingPanel;
import com.mindalliance.channels.pages.components.plan.menus.LearningMenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.ModelActionsMenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.ModelImprovingMenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.ModelScopingMenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.ModelSearchingMenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.ModelShowMenuPanel;
import com.mindalliance.channels.pages.components.segment.ExpandedFlowPanel;
import com.mindalliance.channels.pages.components.segment.FailureImpactsPanel;
import com.mindalliance.channels.pages.components.segment.FlowsEOIsFloatingPanel;
import com.mindalliance.channels.pages.components.segment.MaximizedFlowPanel;
import com.mindalliance.channels.pages.components.segment.OverridesPanel;
import com.mindalliance.channels.pages.components.segment.PartAssignmentsPanel;
import com.mindalliance.channels.pages.components.segment.SegmentEditPanel;
import com.mindalliance.channels.pages.components.segment.SegmentPanel;
import com.mindalliance.channels.pages.components.segment.SharingCommitmentsPanel;
import com.mindalliance.channels.pages.components.segment.checklist.ChecklistFloatingPanel;
import com.mindalliance.channels.pages.components.segment.checklist.ChecklistFlowFloatingPanel;
import com.mindalliance.channels.pages.components.social.rfi.AllSurveysPanel;
import com.mindalliance.channels.pages.components.support.FlowLegendPanel;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
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
public final class ModelPage extends AbstractChannelsWebPage {
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
    public static final String PART_PARM = "node";

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ModelPage.class );

    /**
     * Length a segment name is abbreviated to.
     */
    private static final int SEGMENT_NAME_MAX_LENGTH = 50;

    /**
     * Length a plan name is abbreviated to.
     */
    private static final int PLAN_NAME_MAX_LENGTH = 60;

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

    private ModelScopingMenuPanel scopingMenu;
    private ModelSearchingMenuPanel searchingMenu;
    private LearningMenuPanel participationMenu;
    private ModelImprovingMenuPanel improvingMenu;

    /**
     * Segments action menu.
     */
    private MenuPanel modelActionsMenu;

    /**
     * Segments show menu.
     */
    private MenuPanel modelShowMenu;

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
     * The part checklist panel.
     */
    private Component checklistPanel;
    /**
     * The part checklist flow panel.
     */
    private Component checklistFlowPanel;

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

    private Component allEventsPanel;
    private Component allOrganizationsPanel;
    private Component allSegmentsPanel;
    private Component allClassificationsPanel;
    private Component protocolsMapPanel;
    private Component supplyChainsPanel;
    private Component planEvaluationPanel;
    private Component taskMoverPanel;
    private Component allIssuesPanel;
    private Component planVersionsPanel;
    private Component planSearchingPanel;
    private Component allChecklistsPanel;
    private Component allGoalsPanel;

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

    private BreadcrumbsPanel breadCrumbs;

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

    private boolean showingQuickHelp;
    private AjaxLink<String> quickHelpLink;
    private HelpPanel helpPanel;

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
     * Help section id.
     */
    private String sectionId;
    /**
     * Help topic id.
     */
    private String topicId;
    private AjaxLink<String> allSegmentsButton;
    private AjaxLink<String> rhythmButton;
    private Component collaborationRhythmPanel;

    /**
     * Used when page is called without parameters.
     * Set to default segment, default part, all collapsed.
     */
    public ModelPage() {
        this( new PageParameters() );
    }

    public ModelPage( PageParameters parameters ) {
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
    public ModelPage( Segment segment ) {
        this( segment, segment.getDefaultPart() );
    }

    /**
     * Utility constructor for tests.
     *
     * @param sc a segment
     * @param p  a part in the segment
     */
    public ModelPage( Segment sc, Part p ) {
        super();
        init( sc, p, new HashSet<Long>() );
    }

    @Override
    protected boolean isDomainPage() {
        return true;
    }

    @Override
    protected boolean canTimeOut() {
        return true;
    }

    protected IGuidePanel getGuidePanel() {
        return null; // todo
    }

    // Guide scripting support
    public SegmentPanel getSegmentPanel() {
        return segmentPanel;
    }

    public void renderHead( HtmlHeaderContainer container ) {
        container.getHeaderResponse().renderJavaScript( ModelPage.IE7CompatibilityScript, null );
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
        setExpansions( expanded );
        // addExpansion( Channels.ALL_SEGMENTS );
        for ( Long id : getExpansions() ) {
            commander.requestLockOn( getUser().getUsername(), id );
        }
        setVersioned( false );
        expanded.add( Channels.SOCIAL_ID );
        add( new Label( "sg-title",
                new Model<String>( "Channels: " + getCollaborationModel().getVersionedName() ) ) );
        addBody();
        commander.resynced( getUser().getUsername() );
        LOG.debug( "Segment page generated" );
//        rememberState();
    }

    @Override
    public String getPageName() {
        return "";
    }

    private void addBody() {
        WebMarkupContainer body = new IndicatorAwareWebContainer( "indicator", "spinner" );
        add( body );
        addNotifier( body );
        addModalDialog( "dialog", null, body );
        addGalleryModalWindow( "gallery", null, body );
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
        addQuickHelp();
    }

    private void addQuickHelp() {
        addQuickHelpButton();
        addQuickHelpPanel();
    }

    private void addQuickHelpButton() {
        quickHelpLink = new AjaxLink<String>( "quickHelpButton" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                helpPanel.selectTopicInSection( "model-editor", "about-model-editor", target );
                toggleQuickHelp( target );
            }
        };
        quickHelpLink.setOutputMarkupId( true );
        addTipTitle( quickHelpLink, "Opens online help" );
        form.add( quickHelpLink );
    }

    private void toggleQuickHelp( AjaxRequestTarget target ) {
        showingQuickHelp = !showingQuickHelp;
        updateQuickHelpVisibility( target );
    }

    private void updateQuickHelpVisibility( AjaxRequestTarget target ) {
        makeVisible( quickHelpLink, !showingQuickHelp );
        makeVisible( helpPanel, showingQuickHelp );
        target.add( quickHelpLink );
        target.add( helpPanel );
    }

    private void showHelp( Change change, AjaxRequestTarget target ) {
        showingQuickHelp = true;
        updateQuickHelpVisibility( target );
        sectionId = (String) change.getQualifier( "sectionId" );
        topicId = (String) change.getQualifier( "topicId" );
        helpPanel.selectTopicInSection( sectionId, topicId, target );
    }

    private void addQuickHelpPanel() {
        helpPanel = new HelpPanel( "quickHelp", getGuideName(), getDefaultUserRoleId(), getHelpContext() );
        makeVisible( helpPanel, false );
        form.add( helpPanel );
    }

    private String getDefaultUserRoleId() {
        return "developer";
    }

    private Map<String, Object> getHelpContext() {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put( "page", this );
        return context;
    }

    protected String getGuideName() {
        return "channels_guide";
    }

    private void addHeader() {
        addHomeLink();
        addModelLock();
        addRefreshNow();
        addActivitiesMenubar();
        addModelMenubar();
        addFeedback();
        addBreadCrumbs();
        addSpinner();
        addPartsMapLink();
        addSegmentIssuesLink();
        updateNavigation();
    }

    private void addModelLock() {
        WebMarkupContainer productionLockImg = new WebMarkupContainer( "productionLock" );
        productionLockImg.setVisible( getCollaborationModel().isProduction() || !getUser().isDeveloperOrAdmin( getCollaborationModel().getUri() ) );
        addTipTitle(
                productionLockImg,
                getCollaborationModel().isProduction()
                        ? "Production version. Can not be modified."
                        : "You are not authorized to modify this collaboration model" );
        form.add( productionLockImg );
    }

    private void addHomeLink() {
        WebMarkupContainer homeLink = new WebMarkupContainer( "homeLink" );
        homeLink.add( new AttributeModifier(
                "href",
                makeHomeUrl() ) );
        form.add( homeLink );
    }


    private void addFloatingPanels() {
        updateEntityPanel( null, null );
        addAssignmentsPanel();
        addChecklistPanel();
        addChecklistFlowPanel( null );
        addCommitmentsPanel();
        addEOIsPanel();
        addFailureImpactsPanel();
        addDisseminationPanel( null, false );
        addModelObjectSurveysPanel();
        addOverridesPanel();
        addSegmentEditPanel();
        addModelEditPanel( null );
        addFlowLegendPanel();
        addCollaborationRhythmPanel();
        addAllSegmentsPanel();
        // scoping
        addAllEventsPanel();
        addAllGoalsPanel();
        addAllOrganizationsPanel( null );
        addAllClassificationsPanel();
        // improving
        addTaskMoverPanel();
        addProtocolsMapPanel();
        addSupplyChainsPanel();
        addModelEvaluationPanel();
        addAllIssuesPanel();
        addModelVersionsPanel();
        addAllChecklistsPanel();
        // learning
        // addUserParticipationPanel();
        addAllFeedbackPanel();
        addDataCollectionPanel();
        // searching
        addModelSearchingPanel( null );
    }

    private void addAllSegmentsButton() {
        allSegmentsButton = new AjaxLink<String>( "allSegmentsButton" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                boolean isExpanded = getExpansions().contains( Channels.ALL_SEGMENTS );
                Change change = new Change( isExpanded
                        ? Change.Type.Collapsed
                        : Change.Type.Expanded,
                        Channels.ALL_SEGMENTS );
                update( target, change );
            }
        };
        allSegmentsButton.setOutputMarkupId( true );
        addTipTitle( allSegmentsButton, "Opens (or closes) a map of all segments in this collaboration model" );
        form.addOrReplace( allSegmentsButton );
    }

    private void addRhythmButton() {
        rhythmButton = new AjaxLink<String>( "rhythmButton" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                boolean isExpanded = getExpansions().contains( Channels.RHYTHM );
                Change change = new Change( isExpanded
                        ? Change.Type.Collapsed
                        : Change.Type.Expanded,
                        Channels.RHYTHM );
                update( target, change );
            }
        };
        rhythmButton.setOutputMarkupId( true );
        addTipTitle( rhythmButton, "Opens (or closes) the collaboration rhythm panel" );
        form.addOrReplace( rhythmButton );
    }


    private void addRefreshNow() {
        refreshNeededComponent = new AjaxLink( "refresh-needed" ) {
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
        addTipTitle( segmentIssuesLink, new Model<String>( issuesSummary ) );
        addPartsMapLink();
    }

    private String getSegmentIssuesSummary() {
        Doctor doctor = getApp().getAnalyst().getDoctor();
        return doctor.getIssuesSummary( getCommunityService(), segment, Doctor.INCLUDE_PROPERTY_SPECIFIC );
    }


    private void updateNavigation( AjaxRequestTarget target ) {
        updateNavigation();
        // target.add( goBackLink );
        // target.add( goForwardLink );
        target.add( segmentIssuesLink );
        target.add( geomapLinkPanel );
        addBreadCrumbs();
        target.add( breadCrumbs );
    }

    private void addActivitiesMenubar() {
        addScopingMenu();
        addImprovingMenu();
        addParticipationMenu();
        addSearchingMenu();
    }

    private void addScopingMenu() {
        scopingMenu = new ModelScopingMenuPanel(
                "scopingMenu",
                new PropertyModel<Segment>( this, "segment" ),
                getReadOnlyExpansions() );
        form.addOrReplace( scopingMenu );
    }

    private void addImprovingMenu() {
        improvingMenu = new ModelImprovingMenuPanel(
                "improvingMenu",
                new PropertyModel<Segment>( this, "segment" ),
                getReadOnlyExpansions() );
        form.addOrReplace( improvingMenu );
    }

    private void addParticipationMenu() {
        participationMenu = new LearningMenuPanel(
                "participationMenu",
                new PropertyModel<Segment>( this, "segment" ),
                getReadOnlyExpansions() );
        form.addOrReplace( participationMenu );
    }

    private void addSearchingMenu() {
        searchingMenu = new ModelSearchingMenuPanel(
                "searchingMenu",
                new PropertyModel<Segment>( this, "segment" ),
                getReadOnlyExpansions() );
        form.addOrReplace( searchingMenu );
    }

    private void addModelMenubar() {
        addRhythmButton();
        addAllSegmentsButton();
        addModelActionsMenu();
        addModelShowMenu();
    }

    private void addModelShowMenu() {
        modelShowMenu = new ModelShowMenuPanel(
                "modelShowMenu",
                new PropertyModel<Segment>( this, "segment" ),
                getReadOnlyExpansions() );
        form.addOrReplace( modelShowMenu );
    }

    private void addModelActionsMenu() {
        modelActionsMenu = new ModelActionsMenuPanel( "modelActionsMenu",
                new PropertyModel<Segment>( this, "segment" ),
                getReadOnlyExpansions() );
        form.addOrReplace( modelActionsMenu );
    }

    private void addPartsMapLink() {
        List<GeoLocatable> geoLocatables = new ArrayList<GeoLocatable>();
        for ( Iterator<Part> parts = segment.parts(); parts.hasNext(); )
            geoLocatables.add( parts.next() );
        geomapLinkPanel = new GeomapLinkPanel( "geomapLink",
                new Model<String>(
                        "Tasks with known locations in segment "
                                + segment.getName() ),
                geoLocatables,
                new Model<String>( "Show tasks in map" ) );
        geomapLinkPanel.setOutputMarkupId( true );
        partsMapLink = geomapLinkPanel;
        makeVisible( geomapLinkPanel, geomapLinkPanel.hasMappableContent() );
        form.addOrReplace( geomapLinkPanel );
    }

    private void addFeedback() {
        form.add( new UserFeedbackPanel( "feedback", Feedback.MODELS ) );
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


    private void addBreadCrumbs() {
        breadCrumbs = new BreadcrumbsPanel( "contextPath", this );
        breadCrumbs.setOutputMarkupId( true );
        form.addOrReplace( breadCrumbs );
    }

    @Override
    public PagePathItem getCurrentContextPagePathItem() {
        return new PagePathItem( getCurrentPlanLink() );
    }

    private AjaxLink<String> getCurrentPlanLink() {
        AjaxLink<String> selectedPlanLink = new AjaxLink<String>( Breadcrumbable.PAGE_ITEM_LINK_ID ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                setResponsePage( ModelsPage.class, makePlanParameters() );
                // update( target, new Change( Change.Type.Expanded, getPlan() ) );
            }
        };
        // planPath.add( selectedSegmentLink );
        String planName = getCollaborationModel().getVersionedName() + " model";
        Label selectedPlanNameLabel = new Label(
                Breadcrumbable.PAGE_ITEM_LINK_NAME,
                StringUtils.abbreviate( planName, PLAN_NAME_MAX_LENGTH )
        );
        if ( planName.length() > PLAN_NAME_MAX_LENGTH ) {
            addTipTitle( selectedPlanNameLabel, planName );
        }/* else {
            addTipTitle( selectedPlanNameLabel, "Click to edit the model's details" );
        }*/
        selectedPlanLink.add( selectedPlanNameLabel );
        return selectedPlanLink;
    }

    @Override
    public PagePathItem getSelectedInnerPagePathItem() {
        return new PagePathItem( getSelectedSegmentLink(), !getSegment().isModifiableBy( getUser(), getCommunityService() ) );
    }

    @Override
    public List<PagePathItem> getOtherInnerPagePathItems() {
        List<PagePathItem> pagePathItems = new ArrayList<PagePathItem>();
        for ( AjaxLink link : getOtherSegmentsLinks() ) {
            pagePathItems.add( new PagePathItem( link ) );
        }
        return pagePathItems;
    }


    private AjaxLink getSelectedSegmentLink() {
        AjaxLink<String> selectedSegmentLink = new AjaxLink<String>( Breadcrumbable.PAGE_ITEM_LINK_ID ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Expanded, getSegment() ) );
            }
        };
        String segmentName = getSegment().getName();
        Label selectedSegmentNameLabel = new Label(
                Breadcrumbable.PAGE_ITEM_LINK_NAME,
                StringUtils.abbreviate( segmentName, SEGMENT_NAME_MAX_LENGTH )
        );
        String title = "";
        if ( segmentName.length() > SEGMENT_NAME_MAX_LENGTH ) {
            title = segmentName + " - ";
        }
        title += "Click to see the segment's details.";
        if ( !getSegment().isModifiableBy( getUser(), getCommunityService() ) ) {
            title +=" (You are not allowed to modify it)";
        }
        addTipTitle( selectedSegmentNameLabel, title );
        selectedSegmentLink.add( selectedSegmentNameLabel );
        return selectedSegmentLink;
    }


    private List<AjaxLink> getOtherSegmentsLinks() {
        List<AjaxLink> segmentLinks = new ArrayList<AjaxLink>();
        for ( final Segment segment : getOtherSegments() ) {
            AjaxLink<String> otherPlanLink = new AjaxLink<String>( Breadcrumbable.PAGE_ITEM_LINK_ID ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    setSegment( segment );
                    update( target, new Change( Change.Type.Selected, getSegment() ) );
                }
            };
            otherPlanLink.add( new Label( Breadcrumbable.PAGE_ITEM_LINK_NAME, segment.getName() ) );
            segmentLinks.add( otherPlanLink );
        }
        return segmentLinks;
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
            boolean simplify;
            boolean topBottom;
            boolean showAssets;
            String props = change.getProperty();
            showGoals = props != null && props.contains( "showGoals" );
            showAssets = props != null && props.contains( "showAssets" );
            showConnectors = props != null && props.contains( "showConnectors" );
            hideNoop = props != null && props.contains( "hideNoop" );
            simplify = props != null && props.contains( "simplify" );
            topBottom = props == null || !props.contains( "leftRight" );
            maximizedFlowPanel = new MaximizedFlowPanel(
                    "maximized-flow",
                    new PropertyModel<Segment>( this, "segment" ),
                    new PropertyModel<Part>( this, "part" ),
                    showGoals,
                    showConnectors,
                    hideNoop,
                    simplify,
                    topBottom,
                    showAssets,
                    getExpansions() );
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
        if ( !getExpansions().contains( Feedback.UNKNOWN.getId() ) ) {
            allFeedbackPanel = new Label( "feedbacks", "" );
            allFeedbackPanel.setOutputMarkupId( true );
            makeVisible( allFeedbackPanel, false );
        } else {
            allFeedbackPanel = new AllFeedbackFloatingPanel(
                    "feedbacks",
                    new Model<CollaborationModel>( getCollaborationModel() ),
                    true );
            if ( !feedback.isUnknown() ) {
                ( (AllFeedbackFloatingPanel) allFeedbackPanel ).select( feedback );
            }
        }
        form.addOrReplace( allFeedbackPanel );
    }

    private void addDataCollectionPanel( RFISurvey rfiSurvey, Change change ) {
        if ( !getExpansions().contains( RFISurvey.UNKNOWN.getId() ) ) {
            allSurveyPanel = new Label( "dataCollection", "" );
            allSurveyPanel.setOutputMarkupId( true );
            makeVisible( allSurveyPanel, false );
        } else {
            if ( change == null )
                allSurveyPanel = new AllSurveysPanel(
                        "dataCollection",
                        new Model<RFISurvey>( rfiSurvey ) );
            else
                allSurveyPanel = new AllSurveysPanel(
                        "dataCollection",
                        new Model<RFISurvey>( rfiSurvey ),
                        change.getProperty() );
        }
        form.addOrReplace( allSurveyPanel );
    }

/*
    private void addRequirementsPanel() {
        addRequirementsPanel( null, null );
    }
*/

/*    private void addRequirementsPanel( Requirement requirement, Change change ) {
        if ( !expansions.contains( Requirement.UNKNOWN.getId() ) ) {
            planRequirementsPanel = new Label( "requirements", "" );
            planRequirementsPanel.setOutputMarkupId( true );
            makeVisible( planRequirementsPanel, false );
        } else {
            if ( change == null ) {
                planRequirementsPanel = new PlanRequirementsPanel(
                        "requirements",
                        new Model<Requirement>( requirement ),
                        getReadOnlyExpansions() );
            } else {
                planRequirementsPanel = new PlanRequirementsPanel(
                        "requirements",
                        new Model<Requirement>( requirement ),
                        getReadOnlyExpansions(),
                        change.getProperty() );
            }
        }
        form.addOrReplace( planRequirementsPanel );
    }*/

    private void addAllEventsPanel() {
        if ( !getExpansions().contains( Channels.ALL_EVENTS ) ) {
            allEventsPanel = new Label( "allEvents", "" );
            allEventsPanel.setOutputMarkupId( true );
            makeVisible( allEventsPanel, false );
        } else {
            allEventsPanel = new ModelEventsAndPhasesFloatingPanel(
                    "allEvents",
                    new Model<Event>( Event.UNKNOWN ) );
        }
        form.addOrReplace( allEventsPanel );
    }

    private void addAllGoalsPanel() {
        if ( !getExpansions().contains( Channels.ALL_GOALS ) ) {
            allGoalsPanel = new Label( "allGoals", "" );
            allGoalsPanel.setOutputMarkupId( true );
            makeVisible( allGoalsPanel, false );
        } else {
            allGoalsPanel = new AllGoalsFloatingPanel(
                    "allGoals" );
        }
        form.addOrReplace( allGoalsPanel );
    }


    private void addAllOrganizationsPanel( Change change ) {
        if ( !getExpansions().contains( Channels.ALL_INVOLVEMENTS ) ) {
            allOrganizationsPanel = new Label( "allOrganizations", "" );
            allOrganizationsPanel.setOutputMarkupId( true );
            makeVisible( allOrganizationsPanel, false );
        } else {
            allOrganizationsPanel = change == null
                    ? new ModelInvolvementsFloatingPanel(
                    "allOrganizations",
                    new Model<Organization>( Organization.UNKNOWN ) )
                    : new ModelInvolvementsFloatingPanel(
                    "allOrganizations",
                    new Model<Organization>( Organization.UNKNOWN ),
                    change.getProperty() );
        }
        form.addOrReplace( allOrganizationsPanel );
    }

    private void addAllSegmentsPanel() {
        if ( !getExpansions().contains( Channels.ALL_SEGMENTS ) ) {
            allSegmentsPanel = new Label( "allSegments", "" );
            allSegmentsPanel.setOutputMarkupId( true );
            makeVisible( allSegmentsPanel, false );
        } else {
            allSegmentsPanel = new ModelSegmentsFloatingPanel(
                    "allSegments",
                    new Model<CollaborationModel>( getCollaborationModel() ) );
        }
        form.addOrReplace( allSegmentsPanel );
    }

    private void addCollaborationRhythmPanel() {
        if ( !getExpansions().contains( Channels.RHYTHM ) ) {
            collaborationRhythmPanel = new Label( "rhythm", "" );
            collaborationRhythmPanel.setOutputMarkupId( true );
            makeVisible( collaborationRhythmPanel, false );
        } else {
            collaborationRhythmPanel = new CollaborationRhythmFloatingPanel( "rhythm" );
        }
        form.addOrReplace( collaborationRhythmPanel );
    }


    private void addAllClassificationsPanel() {
        if ( !getExpansions().contains( Channels.ALL_CLASSIFICATIONS ) ) {
            allClassificationsPanel = new Label( "classifications", "" );
            allClassificationsPanel.setOutputMarkupId( true );
            makeVisible( allClassificationsPanel, false );
        } else {
            allClassificationsPanel = new ModelClassificationsFloatingPanel(
                    "classifications",
                    new Model<CollaborationModel>( getCollaborationModel() ) );
        }
        form.addOrReplace( allClassificationsPanel );
    }

    private void addTaskMoverPanel() {
        if ( !getExpansions().contains( Channels.TASK_MOVER ) ) {
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
        if ( !getExpansions().contains( Channels.CHECKLISTS_MAP ) ) {
            protocolsMapPanel = new Label( "checklistsMap", "" );
            protocolsMapPanel.setOutputMarkupId( true );
            makeVisible( protocolsMapPanel, false );
        } else {
            protocolsMapPanel = new ChecklistsMapFloatingPanel(
                    "checklistsMap",
                    new Model<CollaborationModel>( getCollaborationModel() ) );
        }
        form.addOrReplace( protocolsMapPanel );
    }

    private void addSupplyChainsPanel() {
        if ( !getExpansions().contains( Channels.SUPPLY_CHAINS ) ) {
            supplyChainsPanel = new Label( "supplyChains", "" );
            supplyChainsPanel.setOutputMarkupId( true );
            makeVisible( supplyChainsPanel, false );
        } else {
            supplyChainsPanel = new SupplyChainsFloatingPanel(
                    "supplyChains",
                    new Model<CollaborationModel>( getCollaborationModel() ) );
        }
        form.addOrReplace( supplyChainsPanel );
    }


    private void addModelEvaluationPanel() {
        if ( !getExpansions().contains( Channels.MODEL_EVALUATION ) ) {
            planEvaluationPanel = new Label( "modelEvaluation", "" );
            planEvaluationPanel.setOutputMarkupId( true );
            makeVisible( planEvaluationPanel, false );
        } else {
            planEvaluationPanel = new ModelEvaluationFloatingPanel(
                    "modelEvaluation",
                    new Model<CollaborationModel>( getCollaborationModel() ) );
        }
        form.addOrReplace( planEvaluationPanel );
    }

    private void addAllIssuesPanel() {
        if ( !getExpansions().contains( Channels.ALL_ISSUES ) ) {
            allIssuesPanel = new Label( "allIssues", "" );
            allIssuesPanel.setOutputMarkupId( true );
            makeVisible( allIssuesPanel, false );
        } else {
            allIssuesPanel = new AllIssuesFloatingPanel(
                    "allIssues",
                    new Model<CollaborationModel>( getCollaborationModel() ) );
        }
        form.addOrReplace( allIssuesPanel );
    }

    private void addAllChecklistsPanel() {
        if ( !getExpansions().contains( Channels.ALL_CHECKLISTS ) ) {
            allChecklistsPanel = new Label( "allChecklists", "" );
            allChecklistsPanel.setOutputMarkupId( true );
            makeVisible( allChecklistsPanel, false );
        } else {
            allChecklistsPanel = new AllChecklistsFloatingPanel(
                    "allChecklists",
                    new Model<CollaborationModel>( getCollaborationModel() ) );
        }
        form.addOrReplace( allChecklistsPanel );
    }


    private void addModelVersionsPanel() {
        if ( !getExpansions().contains( Channels.MODEL_VERSIONS ) ) {
            planVersionsPanel = new Label( "modelVersions", "" );
            planVersionsPanel.setOutputMarkupId( true );
            makeVisible( planVersionsPanel, false );
        } else {
            planVersionsPanel = new ModelVersionsFloatingPanel(
                    "modelVersions",
                    new Model<CollaborationModel>( getCollaborationModel() ) );
        }
        form.addOrReplace( planVersionsPanel );
    }

/*
    private void addUserParticipationPanel() {
        if ( !getExpansions().contains( Channels.PLAN_PARTICIPATION ) ) {
            userParticipationPanel = new Label( "userParticipation", "" );
            userParticipationPanel.setOutputMarkupId( true );
            makeVisible( userParticipationPanel, false );
        } else {
            userParticipationPanel = new UserParticipationFloatingPanel(
                    "userParticipation",
                    new Model<Plan>( getPlan() ) );
        }
        form.addOrReplace( userParticipationPanel );
    }
*/

    private void addModelSearchingPanel( String aspect ) {
        if ( !getExpansions().contains( Channels.MODEL_SEARCHING ) ) {
            planSearchingPanel = new Label( "modelSearching", "" );
            planSearchingPanel.setOutputMarkupId( true );
            makeVisible( planSearchingPanel, false );
        } else {
            planSearchingPanel = new ModelSearchingFloatingPanel(
                    "modelSearching",
                    new Model<CollaborationModel>( getCollaborationModel() ),
                    getReadOnlyExpansions(),
                    aspect );
        }
        form.addOrReplace( planSearchingPanel );
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

    private void addNotifier( WebMarkupContainer body ) {
        notifier = new NotifierWebMarkupContainer( "notifier" );
        body.add( notifier );
    }

    private String getMessage() {
        return message == null ? "" : message;
    }


    private void doTimedUpdate( AjaxRequestTarget target ) {
        if ( getCommander().isPlanOutOfSync( getUser().getUsername() ) ) {
            showNewPlanVersionWarningDialog( target );
        }
        getCommander().keepAlive( getUser().getUsername(), REFRESH_DELAY );
        getCommander().processTimeOuts();
        if ( getCommander().isTimedOut( getUser().getUsername() ) ) {
            if ( getCollaborationModel().isDevelopment() ) refreshAll( target );
            getCommander().clearTimeOut( getUser().getUsername() );
        } else {
            updateRefreshNowNotice();
            if ( getCollaborationModel().isDevelopment() ) {
                target.add( refreshNeededComponent );
            }
        }
        segmentPanel.updateSocialPanel( target );
        // segmentPanel.updateGuidePanel( target );   // Webkit browsers and JQuery accordion seem to have timing conflicts. Force a timed redisplay. -->  Does not work.
    }

    private void showNewPlanVersionWarningDialog( AjaxRequestTarget target ) {
        WarningPanel warningPanel = new WarningPanel( getModalContentId(), new Model<String>(
                "There is a new version of the collaboration model. "
                        + "Closing this alert will switch you to it." ) );
        showDialog(
                "Warning: New Collaboration Model Version",
                200,
                300,
                warningPanel,
                ModelPage.this,
                target
        );
    }

    private void updateRefreshNowNotice() {
        String reasonsToRefresh = getReasonsToRefresh();
        if ( !reasonsToRefresh.isEmpty() ) {
            LOG.debug( "Refresh now requested" );
        }
        makeVisible( refreshNeededComponent, !reasonsToRefresh.isEmpty() );
        addTipTitle( refreshNeededComponent, new Model<String>(
                "Refresh:" + reasonsToRefresh ) );
    }

    private String getReasonsToRefresh() {
        String reasons = "";
        if ( getCollaborationModel().isDevelopment() ) {
            String lastModifier = getCommander().getLastModifier();
            long lastModified = getCommander().getLastModified();
            if ( lastModified > lastRefreshed && !lastModifier.isEmpty() && !lastModifier.equals(
                    getUser().getUsername() ) )
                reasons = " -- Collaboration model was modified by " + lastModifier;

            // Find expansions that were locked and are now unlocked
            for ( ModelObject mo : getEditableModelObjects( getExpansions() ) ) {
                if ( getUser().isDeveloperOrAdmin( getCollaborationModel().getUri() )
                        && !getCommander().isLockedByUser( getUser().getUsername(), mo ) ) {
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

    private void updateEntityPanel( AjaxRequestTarget target, Change change ) {
        ModelEntity entity = findExpandedEntity();
        if ( entity == null ) {
            entityPanel = new Label( "entity", "" );
            entityPanel.setOutputMarkupId( true );
            makeVisible( entityPanel, false );
        } else {
            if ( entityPanel instanceof EntityPanel ) {
                ( (EntityPanel) entityPanel ).displayEntity(
                        entity,
                        getReadOnlyExpansions(),
                        getAspectShown( entity ),
                        target,
                        change );
            } else {
                entityPanel = new EntityPanel( "entity",
                        new Model<ModelEntity>( entity ),
                        getReadOnlyExpansions(),
                        getAspectShown( entity ) );
            }
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

    private void addChecklistPanel() {
        Part partViewed = getModelObjectViewed( Part.class, "checklist" );
        if ( partViewed == null ) {
            checklistPanel = new Label( "checklist", "" );
            checklistPanel.setOutputMarkupId( true );
            makeVisible( checklistPanel, false );
        } else {
            checklistPanel = new ChecklistFloatingPanel( "checklist",
                    new Model<Part>( partViewed ) );
        }
        form.addOrReplace( checklistPanel );

    }

    private void addChecklistFlowPanel( Part partViewed ) {
        if ( partViewed == null ) {
            checklistFlowPanel = new Label( "checklist-flow", "" );
            checklistFlowPanel.setOutputMarkupId( true );
            makeVisible( checklistFlowPanel, false );
        } else {
            checklistFlowPanel = new ChecklistFlowFloatingPanel( "checklist-flow",
                    new Model<Part>( partViewed ) );
        }
        form.addOrReplace( checklistFlowPanel );

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
            eoisPanel = new FlowsEOIsFloatingPanel( "eois",
                    new Model<EOIsHolder>( flowViewed ),
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
        boolean showSegmentEdit = getExpansions().contains( getSegment().getId() );
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

    private void addModelEditPanel( Change change ) {
        CollaborationModel collaborationModel = getCollaborationModel();
        boolean showPlanEdit = getExpansions().contains( collaborationModel.getId() );
        if ( showPlanEdit ) {
            planEditPanel = new ModelEditPanel( "model",
                    new Model<CollaborationModel>( collaborationModel ),
                    getReadOnlyExpansions(),
                    getAspectShown( collaborationModel ),
                    change );
        } else {
            planEditPanel = new Label( "model", "" );
            planEditPanel.setOutputMarkupId( true );
            makeVisible( planEditPanel, false );
        }
        form.addOrReplace( planEditPanel );
    }

    private ModelEntity findExpandedEntity() {
        for ( long id : getExpansions() ) {
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
     * Redirect to current plan page.
     */
    public void redirectToPlan() {
        setResponsePage( ModelPage.class, planParameters( getCollaborationModel() ) );
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
//            getCommander().requestLockOn( getUser().getUsername(), part );
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
        if ( !part.getSegment().equals( segment ) )
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
        for ( Long id : getExpansions() ) {
            if ( id >= 0 ) {
                try {
                    ModelObject expanded = getQueryService().find( ModelObject.class, id );
                    String aspect = getAspectShown( expanded );
                    if ( aspect != null )
                        tryAcquiringLockForAspect( new Change( Change.Type.NeedsRefresh, expanded ), aspect );
                    else
                        tryAcquiringLock( new Change( Change.Type.NeedsRefresh, expanded ) );
                    // getCommander().requestLockOn( expanded );
                } catch ( NotFoundException e ) {
                    LOG.info( "Expanded model object not found at: " + id );
                }
            } else {
                tryAcquiringLock( new Change( Change.Type.NeedsRefresh, id ) );
            }
        }
    }


    private void expandOtherSegmentIfNeeded( Segment toExpand ) {
        Segment expanded = null;
        Iterator<Long> iter = getExpansions().iterator();
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
        return getExpansions().contains( id );
    }

    private void collapse( Identifiable identifiable ) {
        if ( identifiable != null )
            collapse( new Change( Change.Type.None, identifiable ) );
    }

    protected void collapse( Change change ) {
        super.collapse( change );
        // Close aspects of collapsed object
        if ( change.isForInstanceOf( Flow.class ) ) {
            closeAspect( change, ExpandedFlowPanel.EOIS );
        } else if ( !( change.isForInstanceOf( Part.class ) ) )
            closeAspect( change, null );
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
            if ( !isExpanded( change.getId() ) ) {
                expand( change );
            }
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
        return aspectRequiresLock( change.getSubject( getCommunityService() ), aspect );
    }

    // TODO - deal with Channels.ALL_EVENTS etc., consolidate test in the *EditPanels
    private boolean aspectRequiresLock( Identifiable identifiable, String aspect ) {
        if ( aspect == null ) {
            return false;
        } else if ( aspect.equals( AbstractFloatingMultiAspectPanel.DETAILS ) ) {
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
        for ( long id : getExpansions() ) {
            try {
                ModelObject expanded = getQueryService().find( ModelObject.class, id );
                if ( expanded instanceof SegmentObject ) {
                    if ( ( (SegmentObject) expanded ).getSegment().equals( segment ) ) {
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
        Flow flowToExpand = (Flow) change.getSubject( getCommunityService() );
        // collapse other flows
        List<Identifiable> toCollapse = new ArrayList<Identifiable>();
        for ( long id : getExpansions() ) {
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
        for ( long id : getExpansions() ) {
            try {
                ModelObject expanded = getQueryService().find( ModelObject.class, id );
                if ( expanded instanceof Flow ) {
                    toCollapse.add( expanded );
                } else {
                    if ( expanded instanceof Issue ) {
                        Issue issue = (Issue) expanded;
                        Identifiable about = issue.getAbout();
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
            if ( !getCollaborationModel().getSegments().contains( segment ) ) {
                segment = getCollaborationModel().getDefaultSegment();
                setPart( null );
            }
        } else if ( change.isCollapsed() || change.isRemoved() ) {
            collapse( change );
        } else if ( change.isExpanded() || change.isAdded() ) {
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
            Segment changedSegment = (Segment) change.getSubject( getCommunityService() );
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
            Part changedPart = (Part) change.getSubject( getCommunityService() );
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
            Flow changedFlow = (Flow) change.getSubject( getCommunityService() );
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
            UserIssue userIssue = (UserIssue) change.getSubject( getCommunityService() );
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
        if ( target == null || change == null || updated == null )
            return;   // protect against showing a panel "in other tab or page"
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
            if ( change.isForInstanceOf( CollaborationModel.class ) && change.isSelected() ) {  // Not caused anymore
                redirectToPlan();
            } else if ( change.getId() == Channels.GALLERY_ID ) {
                if ( change.isExpanded() )
                    showGallery( change.getProperty(), target );
                else if ( change.isCollapsed() )
                    hideGallery( target );
            } else if ( change.isCollapsed() && change.getId() == Channels.GUIDE_ID ) {
                toggleQuickHelp( target );
            } else if ( change.isGuide() ) {
                showHelp( change, target );
            } else if ( change.isAspectReplaced() ) {
                replaceAspect( change, target );
            } else if ( change.isAspectViewed()
                    || change.isAspectClosed()
                    || change.isExpanded()
                    || ( change.isCollapsed() && changes.get( change.getId() ) == null ) ) {
                openOrCloseChild( change, target );
                if ( change.isAspectClosed() && change.isForProperty( "checklist" ) ) {
                    refresh( target, change, updated );
                }
            } else if ( change.isUndoing() || change.isUnknown() || change.isRecomposed()
                    || change.isAdded() && change.isForInstanceOf( Part.class ) ) {
                refreshAll( target );
            } else if ( change.isUpdated() && isExpanded( change.getId() ) ) {
                if ( change.isForInstanceOf( Part.class ) && change.isForProperty( "checklist" ) ) {
                    refreshChecklistFlowPanel( target, change, updated );
                }
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
            } else if ( change.isRefreshNeeded() ) {
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
        } else if ( change.getId() == Channels.ALL_EVENTS ) {
            refreshAllEventsPanel( target, change, updated );
        } else if ( change.getId() == Channels.ALL_INVOLVEMENTS ) {
            refreshAllOrganizationsPanel( target, change, updated );
        } else if ( change.getId() == Channels.ALL_SEGMENTS ) {
            refreshAllSegmentsPanel( target, change, updated );
        } else if ( change.getId() == Channels.RHYTHM ) {
            refreshCollaborationRhythmPanel( target, change, updated );
        } else if ( change.getId() == Channels.ALL_CLASSIFICATIONS ) {
            refreshAllClassificationsPanel( target, change, updated );
        } else if ( change.getId() == Channels.TASK_MOVER ) {
            refreshTaskMoverPanel( target, change, updated );
        } else if ( change.getId() == Channels.CHECKLISTS_MAP ) {
            refreshProtocolsMapPanel( target, change, updated );
        } else if ( change.getId() == Channels.SUPPLY_CHAINS ) {
            refreshSupplyChainsPanel( target, change, updated );
        } else if ( change.getId() == Channels.MODEL_EVALUATION ) {
            refreshPlanEvaluationPanel( target, change, updated );
        } else if ( change.getId() == Channels.ALL_ISSUES ) {
            refreshAllIssuesPanel( target, change, updated );
        } else if ( change.getId() == Channels.ALL_CHECKLISTS ) {
            refreshAllChecklistsPanel( target, change, updated );
        } else if ( change.getId() == Channels.ALL_GOALS ) {
            refreshAllGoalsPanel( target, change, updated );
        } else if ( change.getId() == Channels.MODEL_VERSIONS ) {
            refreshPlanVersionsPanel( target, change, updated );
        } else if ( change.getId() == Channels.MODEL_SEARCHING ) {
            refreshPlanSearchingPanel( target, change, updated );
        } else if ( change.isForInstanceOf( RFISurvey.class ) ) {
            refreshDataCollectionPanel( target, change, updated );
        } else if ( change.isForInstanceOf( CollaborationModel.class ) ) {
            refreshPlanEditPanel( target, change, updated );
        } else if ( change.isForInstanceOf( Segment.class ) ) {
            refreshSegmentEditPanel( target, change, updated );
        } else if ( change.isForInstanceOf( ModelEntity.class ) ) {
            refreshEntityPanel( target, change, updated );
        } else if ( change.isForInstanceOf( Part.class ) && change.isForProperty( "assignments" ) ) {
            refreshAssignmentsPanel( target, change, updated );
        } else if ( change.isForInstanceOf( Part.class ) && change.isForProperty( "checklist" ) ) {
            refreshChecklistPanel( target, change, updated );
            refreshChecklistFlowPanel( target, change, updated );
        } else if ( change.isForInstanceOf( Part.class ) && change.isForProperty( "checklist-flow" ) ) {
            refreshChecklistFlowPanel( target, change, updated );
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
        } else if ( change.isForInstanceOf( Feedback.class ) ) {
            refreshAllFeedbackPanel( target, change, updated );
        } /*else if ( change.isForInstanceOf( Requirement.class ) ) {
            refreshRequirementsPanel( target, change, updated );
        }*/
        refreshHeadersMenusAndNavigation( target, change, updated );
    }

    private void replaceAspect( Change change, AjaxRequestTarget target ) {
        String aspect = change.getProperty();
        Component maPanel =
                change.isForInstanceOf( CollaborationModel.class )
                        ? planEditPanel
                        : change.isForInstanceOf( Segment.class )
                        ? segmentEditPanel
                        : change.isForInstanceOf( ModelEntity.class )
                        ? entityPanel
                        : null;
        if ( maPanel != null && maPanel instanceof AbstractFloatingMultiAspectPanel ) {
            ( (AbstractFloatingMultiAspectPanel) maPanel ).showAspect( aspect, change, target );
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
        addBreadCrumbs();
        target.add( breadCrumbs );
        updateNavigation( target );
        addPartsMapLink();
        target.add( partsMapLink );
    }

    private void refreshPlanMenus( AjaxRequestTarget target ) {
        addActivitiesMenubar();
        addModelActionsMenu();
        addModelShowMenu();
        target.add( modelActionsMenu );
        target.add( modelShowMenu );
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
        refreshChecklistPanel( target, change, updated );
        refreshChecklistFlowPanel( target, change, updated );
        refreshCommitmentsPanel( target, change, updated );
        refreshEOIsPanel( target, change, updated );
        refreshSegmentPanel( target, change, updated );
        refreshFailureImpactsPanel( target, change, updated );
        refreshDisseminationPanel( target, change, updated );
        refreshModelObjectSurveysPanel( target, change, updated );
        refreshOverridesPanel( target, change, updated );
        // refreshRequirementsPanel( target, change, updated );
        refreshAllEventsPanel( target, change, updated );
        refreshAllOrganizationsPanel( target, change, updated );
        refreshAllSegmentsPanel( target, change, updated );
        refreshCollaborationRhythmPanel( target, change, updated );
        refreshAllClassificationsPanel( target, change, updated );
        refreshTaskMoverPanel( target, change, updated );
        refreshProtocolsMapPanel( target, change, updated );
        refreshSupplyChainsPanel( target, change, updated );
        refreshPlanEvaluationPanel( target, change, updated );
        refreshAllIssuesPanel( target, change, updated );
        refreshAllChecklistsPanel( target, change, updated );
        refreshAllGoalsPanel( target, change, updated );
        refreshPlanVersionsPanel( target, change, updated );
        // refreshUserParticipationPanel( target, change, updated );
        refreshPlanSearchingPanel( target, change, updated );
    }


    private void refreshChildrenMenus( AjaxRequestTarget target ) {
        if ( planEditPanel instanceof ModelEditPanel )
            ( (ModelEditPanel) planEditPanel ).refreshMenus( target );
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
        Identifiable identifiable = change.getSubject( getCommunityService() );
        CollaborationModel collaborationModel = getCollaborationModel();
        if ( change.isDisplay() && identifiable instanceof CollaborationModel ) {
            addModelEditPanel( change );
            target.add( planEditPanel );
        } else if ( planEditPanel instanceof ModelEditPanel ) {
            ( (ModelEditPanel) planEditPanel ).refresh( target,
                    change,
                    updated,
                    getAspectShown( collaborationModel ) );
        }
    }

    private void refreshSegmentEditPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getCommunityService() );
        if ( ( change.isDisplay() || change.isAdded() )
                && identifiable != null
                && identifiable instanceof Segment
                ||
                identifiable != null && change.isSelected() && identifiable instanceof Part ) {
            if ( !( segmentEditPanel instanceof SegmentEditPanel
                    && ( (SegmentEditPanel) segmentEditPanel ).isMinimized() ) ) {
                addSegmentEditPanel();
                target.add( segmentEditPanel );
            }
            addBreadCrumbs();
            target.add( breadCrumbs );
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
        if ( entity == null ||
                change.isDisplay()
                        && change.isForInstanceOf( ModelEntity.class ) ) {
            updateEntityPanel( target, change );
            if ( !( entityPanel instanceof EntityPanel )
                    || !((EntityPanel)entityPanel).isMinimized() )
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
        Identifiable identifiable = change.getSubject( getCommunityService() );
        if ( identifiable != null
                && identifiable instanceof Part
                && change.isAspect( "assignments" ) ) {
            addAssignmentsPanel();
            target.add( assignmentsPanel );
        } else if ( assignmentsPanel instanceof PartAssignmentsPanel ) {
            ( (PartAssignmentsPanel) assignmentsPanel ).refresh( target, change, updated );
        }
    }

    private void refreshChecklistPanel(
            AjaxRequestTarget target,
            Change change,
            List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getCommunityService() );
        if ( identifiable != null
                && identifiable instanceof Part
                && change.isAspect( "checklist" ) ) {
            addChecklistPanel();
            target.add( checklistPanel );
        } else if ( checklistPanel instanceof ChecklistFloatingPanel ) {
            ( (ChecklistFloatingPanel) checklistPanel ).refresh( target, change, updated );
        }
    }

    private void refreshChecklistFlowPanel(
            AjaxRequestTarget target,
            Change change,
            List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getCommunityService() );
        if ( identifiable != null
                && identifiable instanceof Part
                && change.isAspect( "checklist-flow" ) ) {
            addChecklistFlowPanel( change.isAspectClosed() ? null : (Part) identifiable );
            target.add( checklistFlowPanel );
        } else if ( checklistFlowPanel instanceof ChecklistFlowFloatingPanel ) {
            ( (ChecklistFlowFloatingPanel) checklistFlowPanel ).refresh( target, change, updated );
        }
    }

    private void refreshCommitmentsPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getCommunityService() );
        if ( identifiable != null
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
        Identifiable identifiable = change.getSubject( getCommunityService() );
        if ( identifiable != null
                && identifiable instanceof Flow
                && ( change.isCollapsed()
                || change.isAspect( "eois" ) ) ) {
            addEOIsPanel();
            target.add( eoisPanel );
        } else if ( eoisPanel instanceof FlowsEOIsFloatingPanel ) {
            ( (FlowsEOIsFloatingPanel) eoisPanel ).refresh( target, change, updated );
        }
    }

    private void refreshFailureImpactsPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getCommunityService() );
        if ( identifiable != null
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
        Identifiable identifiable = change.getSubject( getCommunityService() );
        if ( identifiable != null && identifiable instanceof SegmentObject
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
        Identifiable identifiable = change.getSubject( getCommunityService() );
        if ( identifiable != null && identifiable instanceof ModelObject
                && change.isAspect( "surveys" ) ) {
            addModelObjectSurveysPanel();
            target.add( modelObjectSurveysPanel );
        } else if ( modelObjectSurveysPanel instanceof ModelObjectSurveysPanel ) {
            ( (ModelObjectSurveysPanel) modelObjectSurveysPanel ).refresh( target, change, updated );
        }
    }

    private void refreshOverridesPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getCommunityService() );
        if ( identifiable != null
                && identifiable instanceof Part
                && change.isAspect( "overrides" ) ) {
            addOverridesPanel();
            target.add( overridesPanel );
        } else if ( overridesPanel instanceof OverridesPanel ) {
            ( (OverridesPanel) overridesPanel ).refresh( target, change, updated );
        }
    }

/*
    private void refreshRequirementsPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getCommunityService() );
        if ( identifiable != null
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
            addRequirementsPanel( viewedRequirement, change );
            target.add( planRequirementsPanel );
        } else if ( planRequirementsPanel instanceof PlanRequirementsPanel ) {
            ( (PlanRequirementsPanel) planRequirementsPanel ).refresh( target,
                    change,
                    updated,
                    getAspectShown( Requirement.UNKNOWN ) );
        }
    }
*/

    private void refreshAllEventsPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( id == Channels.ALL_EVENTS
                && change.isDisplay() ) {
            addAllEventsPanel();
            target.add( allEventsPanel );
        } else if ( allEventsPanel instanceof ModelEventsAndPhasesFloatingPanel ) {
            ( (ModelEventsAndPhasesFloatingPanel) allEventsPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshAllOrganizationsPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( id == Channels.ALL_INVOLVEMENTS
                && change.isDisplay() ) {
            addAllOrganizationsPanel( change );
            target.add( allOrganizationsPanel );
        } else if ( allOrganizationsPanel instanceof ModelInvolvementsFloatingPanel ) {
            ( (ModelInvolvementsFloatingPanel) allOrganizationsPanel ).refresh( target,
                    change,
                    updated,
                    getAspectShown( Organization.UNKNOWN ) );
        }
    }

    private void refreshAllSegmentsPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( id == Channels.ALL_SEGMENTS
                && change.isDisplay() ) {
            addAllSegmentsPanel();
            target.add( allSegmentsPanel );
        } else if ( allSegmentsPanel instanceof ModelSegmentsFloatingPanel ) {
            ( (ModelSegmentsFloatingPanel) allSegmentsPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshCollaborationRhythmPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( id == Channels.RHYTHM
                && change.isDisplay() ) {
            addCollaborationRhythmPanel();
            target.add( collaborationRhythmPanel );
        } else if ( collaborationRhythmPanel instanceof CollaborationRhythmFloatingPanel ) {
            ( (CollaborationRhythmFloatingPanel) collaborationRhythmPanel ).refresh( target,
                    change,
                    updated );
        }
    }


    private void refreshAllClassificationsPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( id == Channels.ALL_CLASSIFICATIONS
                && change.isDisplay() ) {
            addAllClassificationsPanel();
            target.add( allClassificationsPanel );
        } else if ( allClassificationsPanel instanceof ModelClassificationsFloatingPanel ) {
            ( (ModelClassificationsFloatingPanel) allClassificationsPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshTaskMoverPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( id == Channels.TASK_MOVER
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
        if ( id == Channels.CHECKLISTS_MAP
                && change.isDisplay() ) {
            addProtocolsMapPanel();
            target.add( protocolsMapPanel );
        } else if ( protocolsMapPanel instanceof ChecklistsMapFloatingPanel ) {
            ( (ChecklistsMapFloatingPanel) protocolsMapPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshSupplyChainsPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( id == Channels.SUPPLY_CHAINS
                && change.isDisplay() ) {
            addSupplyChainsPanel();
            target.add( supplyChainsPanel );
        } else if ( supplyChainsPanel instanceof SupplyChainsFloatingPanel ) {
            ( (SupplyChainsFloatingPanel) supplyChainsPanel ).refresh( target,
                    change,
                    updated );
        }
    }


    private void refreshPlanEvaluationPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( id == Channels.MODEL_EVALUATION
                && change.isDisplay() ) {
            addModelEvaluationPanel();
            target.add( planEvaluationPanel );
        } else if ( planEvaluationPanel instanceof ModelEvaluationFloatingPanel ) {
            ( (ModelEvaluationFloatingPanel) planEvaluationPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshAllIssuesPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( id == Channels.ALL_ISSUES
                && change.isDisplay() ) {
            addAllIssuesPanel();
            target.add( allIssuesPanel );
        } else if ( allIssuesPanel instanceof AllIssuesFloatingPanel ) {
            ( (AllIssuesFloatingPanel) allIssuesPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshAllChecklistsPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( id == Channels.ALL_CHECKLISTS
                && change.isDisplay() ) {
            addAllChecklistsPanel();
            target.add( allChecklistsPanel );
        } else if ( allChecklistsPanel instanceof AllChecklistsFloatingPanel ) {
            ( (AllChecklistsFloatingPanel) allChecklistsPanel ).refresh( target,
                    change,
                    updated );
        }
    }

    private void refreshAllGoalsPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( id == Channels.ALL_GOALS
                && change.isDisplay() ) {
            addAllGoalsPanel();
            target.add( allGoalsPanel );
        } else if ( allGoalsPanel instanceof AllGoalsFloatingPanel ) {
            ( (AllGoalsFloatingPanel) allGoalsPanel ).refresh( target,
                    change,
                    updated );
        }
    }


    private void refreshPlanVersionsPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( id == Channels.MODEL_VERSIONS
                && change.isDisplay() ) {
            addModelVersionsPanel();
            target.add( planVersionsPanel );
        } else if ( planVersionsPanel instanceof ModelVersionsFloatingPanel ) {
            ( (ModelVersionsFloatingPanel) planVersionsPanel ).refresh( target,
                    change,
                    updated );
        }
    }

/*
    private void refreshUserParticipationPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( id == Channels.PLAN_PARTICIPATION
                        && change.isDisplay() ) {
            addUserParticipationPanel();
            target.add( userParticipationPanel );
        } else if ( userParticipationPanel instanceof UserParticipationFloatingPanel ) {
            ( (UserParticipationFloatingPanel) userParticipationPanel ).refresh( target,
                    change,
                    updated );
        }
    }
*/


    private void refreshAllFeedbackPanel(
            AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Identifiable identifiable = change.getSubject( getCommunityService() );
        if ( identifiable != null
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
        Identifiable identifiable = change.getSubject( getCommunityService() );
        if ( identifiable != null
                && change.isDisplay()
                && identifiable instanceof RFISurvey ) {
            RFISurvey rfiSurvey = (RFISurvey) identifiable;
            RFISurvey viewedRFISurvey = ( rfiSurvey == null || rfiSurvey.isUnknown() )
                    ? RFISurvey.UNKNOWN
                    : rfiSurvey;
            addDataCollectionPanel( viewedRFISurvey, change );
            target.add( allSurveyPanel );
        } else if ( allSurveyPanel instanceof AllSurveysPanel ) {
            ( (AllSurveysPanel) allSurveyPanel ).refresh( target,
                    change,
                    updated,
                    getAspectShown( RFISurvey.UNKNOWN ) );
        }
    }

    private void refreshPlanSearchingPanel( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        long id = change.getId();
        if ( id == Channels.MODEL_SEARCHING && change.isDisplay() ) {
            addModelSearchingPanel( change.getProperty() );
            target.add( planSearchingPanel );

        } else if ( planSearchingPanel instanceof ModelSearchingFloatingPanel ) {
            ( (ModelSearchingFloatingPanel) planSearchingPanel ).refresh( target,
                    change,
                    updated,
                    change.getProperty() );
        }
    }

    /**
     * Get all plans that the current can modify.
     *
     * @return a list of plans
     */
    public List<CollaborationModel> getPlannablePlans() {
        return getModelManager().getModelsModifiableBy( getUser() );
    }

    private void update( AjaxRequestTarget target, Change change ) {
        changed( change );
        updateWith( target, change, new ArrayList<Updatable>() );
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


