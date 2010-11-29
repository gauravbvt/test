package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.diagrams.PlanMapDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import com.mindalliance.channels.pages.components.support.FeedbackWidget;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * The plan SOPs report.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 5, 2009
 * Time: 5:13:56 PM
 */
public class SOPsReportPage extends WebPage {

    /**
     * Plan manager.
     */
    @SpringBean
    private PlanManager planManager;

    /**
     * The query service.
     */
    @SpringBean
    private QueryService queryService;

    /**
     * The user.
     */
    @SpringBean
    private User user;

    /**
     * The current plan.
     */
    private Plan plan;

    /**
     * Restrictions to report generation.
     */
    private SelectorPanel selector;

    private static final double[] DIAGRAM_SIZE = new double[]{ 478L, 400L };

    public SOPsReportPage( PageParameters parameters ) {
        super( parameters );

        setDefaultModel( new CompoundPropertyModel<Object>( this ) );

        plan = user.getPlan();
        PropertyModel<ModelObject> planModel = new PropertyModel<ModelObject>( this, "plan" );

        selector = new SelectorPanel( "selector", parameters );
        if ( !selector.isValid() ) {
            setRedirect( true );
            throw new RestartResponseException( getClass(), selector.getParameters() );
        }

        IModel<List<Segment>> segmentsModel =
                new PropertyModel<List<Segment>>( this, "selector.segments" );
        add(
            newFeedbackWidget(),
            selector,

            new Label( "title" ),
            new Label( "plan.name" ),
            new Label( "actorName" ).setVisible( !!selector.isActorSelected() ),
            new Label( "organizationName" ).setVisible( !!selector.isOrgSelected() ),
            new Label( "plan.client" ).setVisible( !plan.getClient().isEmpty() ),
            new Label( "planDescription" ).setRenderBodyOnly( true ),

            new Label( "date",
                       DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.LONG )
                          .format( new Date() ) ),

            new DocumentsReportPanel( "documents", planModel ),
            new IssuesReportPanel( "issues", planModel ).setVisible( selector.isShowingIssues() ),

            new ListView<Segment>( "selector.segments" ) {
                @Override
                protected void populateItem( ListItem<Segment> item ) {
                    item.add( new SegmentReportPanel( "segment",
                            item.getModel(),
                            !selector.isOrgSelected() ? null : selector.getOrganization(),
                            !selector.isActorSelected() ? null : selector.getActor(),
                            selector.isShowingIssues(),
                            selector.isShowingDiagrams()) );
                }
            },

            new WebMarkupContainer( "plan-map-link" ).setVisible( user.isPlanner() )
                .add( new AttributeModifier( "href", true, new Model<String>( getPlanMapLink() ) ) )
                .add( new AttributeModifier( "target", true, new Model<String>( "_blank" ) ) )
                .setVisible( selector.isShowingDiagrams() ),

            newOrgHeaderPanel(),
            newActorBanner(),

            new WebMarkupContainer( "sg-list-container" )
                .add( new ListView<Segment>( "sg-list", segmentsModel ) {
                        @Override
                        protected void populateItem( ListItem<Segment> item ) {
                            Segment segment = item.getModelObject();
                            item.add( new ExternalLink( "sc-link",
                                                        "#" + segment.getId(),
                                                        segment.getName() ) );
                        }
                    } )
                .setVisible( !selector.isSegmentSelected() ),

            newDiagramPanel( segmentsModel )
        );
    }

    public String getTitle() {
        return MessageFormat.format( "Report: {0}", plan.getName() );
    }

    public SelectorPanel getSelector() {
        return selector;
    }

    private Component newActorBanner() {
        return !selector.isActorSelected() ?
               new Label( "actor-details", "" ).setVisible( false )
             : new ActorBannerPanel( "actor-details",
                                     selector.getSegment(),
                                     new ResourceSpec( selector.getActor(),
                                                       null,
                                                       !selector.isOrgSelected() ?
                                                            null : selector.getOrganization(),
                                                       null ),
                                     false,
                                     "../" );
    }

    private Component newOrgHeaderPanel() {
        return !selector.isOrgSelected() ?
                    new Label( "org-details", "" ).setVisible( false )
                  : new OrganizationHeaderPanel( "org-details",
                                                 selector.getOrganization(),
                                                 selector.isShowingIssues() );
    }

    private Component newFeedbackWidget() {
        FeedbackWidget feedbackWidget = new FeedbackWidget(
                "feedback-widget",
                new Model<String>(
                    plan.getUserSupportCommunityUri( planManager.getDefaultSupportCommunity() ) ),
                true );
        makeVisible( feedbackWidget, false );
        return feedbackWidget;
    }

    private Component newDiagramPanel( IModel<List<Segment>> segments ) {
        return user.isPlanner() && selector.isShowingDiagrams() ?
               new PlanMapDiagramPanel(
                    "planMap",
                    segments,
                    false,                    // group segments by phase
                    true,                     // group segments by event
                    null,                    // selected phase or event
                    !selector.isSegmentSelected() ? null : selector.getSegment(),
                    null,
                    new Settings( "#plan-map",
                                  DiagramFactory.LEFT_RIGHT,
                                  DIAGRAM_SIZE,
                                  false,
                                  false )
                    )
             : new Label( "planMap", "" ).setVisible( false );
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan( Plan plan ) {
        this.plan = plan;
    }

    public String getActorName() {
        Actor actor = selector.getActor();
        return actor == null ? "" : actor.getName();
    }

    public String getOrganizationName() {
        Organization organization = selector.getOrganization();
        String name = organization == null ? "" : organization.getName();
        if ( !selector.getActor().isUnknown() ) name = name + ",";
        return name;
    }

    private String getPlanMapLink() {
        return "/plan.png";
    }

    public String getPlanDescription() {
        String label = plan.getDescription();
        return label.isEmpty() || label.endsWith( "." ) ? label
                : label + ".";
    }

    /**
     * Set the headers of the Page being served.
     *
     * @param response the response.
     */
    @Override
    protected void setHeaders( WebResponse response ) {
        super.setHeaders( response );

        Channels channels = (Channels) getApplication();
        Commander commander = channels.getCommander( plan );
        long longTime = commander.getLastModified();
        long now = System.currentTimeMillis();

        response.setDateHeader( "Date", now );
//        response.setDateHeader( "Expires", now + 24L*60*60*1000 );
        response.setDateHeader( "Last-Modified", longTime );
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


}
