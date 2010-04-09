package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.components.diagrams.PlanMapDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import org.apache.wicket.AttributeModifier;
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
 * The plan report.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 5, 2009
 * Time: 5:13:56 PM
 */
public class PlanReportPage extends WebPage {

    /** The parameter that specifies all segments. */
    private static final String ALL = "all";

    /**
     * Plan manager.
     */
    @SpringBean
    private PlanManager planManager;

    /** The query service. */
    @SpringBean
    private QueryService queryService;

    /** The current plan. */
    @SpringBean
    private Plan plan;

    /** Restrictions to report generation. */
    private SelectorPanel selector;

    public PlanReportPage( PageParameters parameters ) {
        super( parameters );

        setStatelessHint( true );

        selector = new SelectorPanel( "selector", parameters );
        if ( !selector.isValid() ) {
            setRedirect( true );
            throw new RestartResponseException( getClass(), selector.getParameters() );
        }

        String reportDate = DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.LONG )
                                .format( new Date() );
        List<Segment> segments = selector.getSegments();

        double[] size = { 478L, 400L };
        PlanMapDiagramPanel diagramPanel = new PlanMapDiagramPanel( "planMap",            // NON-NLS
            new Model<ArrayList<Segment>>( (ArrayList<Segment>) segments ),
            false, // group segments by phase
            false, // group segments by event
            null,  // selected phase or event
            selector.isAllSegments() ? null : selector.getSegment(),
            null,
            new Settings( "#plan-map", DiagramFactory.LEFT_RIGHT, size, false, false ) );

        add( selector,
             new Label( "title",                                                          // NON-NLS
                        MessageFormat.format( "Report: {0}", plan.getName() ) ),
             new Label( "plan-name", plan.getName() ),                                    // NON-NLS
             new Label( "plan-client", plan.getClient() )                                 // NON-NLS
                     .setVisible( !plan.getClient().isEmpty() ),
             new Label( "plan-description", getPlanDescription() )                        // NON-NLS
                     .setRenderBodyOnly( true ),

             new Label( "date", reportDate ),                                             // NON-NLS

             new ListView<Segment>( "sg-list", segments ) {                             // NON-NLS
                    @Override
                    protected void populateItem( ListItem<Segment> item ) {
                        Segment segment = item.getModelObject();
                        item.add( new ExternalLink( "sc-link",
                                        "#" + segment.getId(), segment.getName() ) );
                    }
                },

             new ListView<Segment>( "segments", segments ) {                           // NON-NLS
                    @Override
                    protected void populateItem( ListItem<Segment> item ) {
                        item.add( new SegmentReportPanel( "segment",                    // NON-NLS
                                        item.getModel(),
                                        selector.isAllActors() ? null : selector.getActor(),
                                        selector.isShowingIssues() ) );
                    }
                },

             diagramPanel,

             new WebMarkupContainer( "plan-map-link" )
                .add( new AttributeModifier( "href", true, new Model<String>( getPlanMapLink() ) ) )
                .add( new AttributeModifier( "target", true, new Model<String>( "_" ) ) )
        );


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
}
