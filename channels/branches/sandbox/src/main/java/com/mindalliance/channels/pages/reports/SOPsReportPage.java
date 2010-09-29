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
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
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
     * The current plan.
     */
    private Plan plan;

    /**
     * Restrictions to report generation.
     */
    private SelectorPanel selector;

    public SOPsReportPage( PageParameters parameters ) {
        super( parameters );
        plan = getPlan();

        setStatelessHint( true );
        addFeedbackWidget();
        selector = new SelectorPanel( "selector", parameters );
        if ( !selector.isValid() ) {
            setRedirect( true );
            throw new RestartResponseException( getClass(), selector.getParameters() );
        }

        String reportDate = DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.LONG )
                .format( new Date() );
        List<Segment> segments = selector.getSegments();


        add( selector,
                new Label( "title",
                        MessageFormat.format( "Report: {0}", plan.getName() ) ),
                new Label( "plan-name", plan.getName() ),
                new Label( "actor-name", getActorName() ).setVisible( !selector.isAllActors() ),
                new Label( "organization-name", getOrganizationName() ).setVisible( !selector.isAllOrganizations() ),
                new Label( "plan-client", plan.getClient() )
                        .setVisible( !plan.getClient().isEmpty() ),
                new Label( "plan-description", getPlanDescription() )
                        .setRenderBodyOnly( true ),

                new Label( "date", reportDate ),

                new DocumentsReportPanel( "documents", new Model<ModelObject>( plan ) ),
                new IssuesReportPanel( "issues", new Model<ModelObject>( plan ) )
                        .setVisible( selector.isShowingIssues() ),

                new ListView<Segment>( "segments", segments ) {
                    @Override
                    protected void populateItem( ListItem<Segment> item ) {
                        item.add( new SegmentReportPanel( "segment",
                                item.getModel(),
                                selector.isAllOrganizations() ? null : selector.getOrganization(),
                                selector.isAllActors() ? null : selector.getActor(),
                                selector.isShowingIssues(),
                                selector.isShowingDiagrams()) );
                    }
                },

                new WebMarkupContainer( "plan-map-link" ).setVisible( User.current().isPlanner() )
                        .add( new AttributeModifier( "href", true, new Model<String>( getPlanMapLink() ) ) )
                        .add( new AttributeModifier( "target", true, new Model<String>( "_blank" ) ) )
                        .setVisible( selector.isShowingDiagrams() )
        );
        if ( selector.isAllOrganizations() ) {
            add( new Label( "org-details", "" ).setVisible( false ) );
        } else {
            add( new OrganizationHeaderPanel(
                    "org-details",
                    selector.getOrganization(),
                    selector.isShowingIssues() ) );

        }
        if ( selector.isAllActors() ) {
            add( new Label( "actor-details", "" ).setVisible( false ) );
        } else {
            ResourceSpec actorSpec = ResourceSpec.with( selector.getActor() );
            if ( !selector.isAllOrganizations() )
                actorSpec.setOrganization( selector.getOrganization() );
            add( new ActorBannerPanel(
                    "actor-details",
                    selector.getSegment(),
                    actorSpec,
                    false,
                    "../"
            ) );
        }
        if ( User.current().isPlanner() ) {
            addDiagramPanel( segments );
        } else {
            add( new Label( "planMap", "" ) );
        }
        WebMarkupContainer segmentlistContainer = new WebMarkupContainer( "sg-list-container" );
        segmentlistContainer.setVisible( selector.isAllSegments() );
        segmentlistContainer.add( new ListView<Segment>( "sg-list", segments ) {
            @Override
            protected void populateItem( ListItem<Segment> item ) {
                Segment segment = item.getModelObject();
                item.add( new ExternalLink( "sc-link",
                        "#" + segment.getId(), segment.getName() ) );
            }
        } );
        add( segmentlistContainer );
    }

    private void addFeedbackWidget() {
        FeedbackWidget feedbackWidget = new FeedbackWidget(
                "feedback-widget",
                new Model<String>(
                        getPlan().getUserSupportCommunityUri( planManager.getDefaultSupportCommunity() ) ),
                true );
        makeVisible( feedbackWidget, false );
        add( feedbackWidget );
    }

    private Plan getPlan() {
        return User.current().getPlan();
    }

    private void addDiagramPanel( List<Segment> segments ) {
        Component diagramPanel;
        double[] size = {478L, 400L};
        if ( User.current().isPlanner() && selector.isShowingDiagrams() ) {
            diagramPanel = new PlanMapDiagramPanel( "planMap",
                    new Model<ArrayList<Segment>>( (ArrayList<Segment>) segments ),
                    false, // group segments by phase
                    true, // group segments by event
                    null,  // selected phase or event
                    selector.isAllSegments() ? null : selector.getSegment(),
                    null,
                    new Settings( "#plan-map", DiagramFactory.LEFT_RIGHT, size, false, false ) );
        } else {
            diagramPanel = new Label( "planMap", "" );
            diagramPanel.setVisible( false );
        }
        add( diagramPanel );
    }

    private String getActorName() {
        Actor actor = selector.getActor();
        return actor == null ? "" : actor.getName();
    }

    private String getOrganizationName() {
        Organization organization = selector.getOrganization();
        String name = organization == null ? "" : organization.getName();
        if ( !selector.getActor().isUnknown() ) name = name + ",";
        return name;
    }

    private String getPlanMapLink() {
        return "/plan.png";
    }

    private String getPlanDescription() {
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
