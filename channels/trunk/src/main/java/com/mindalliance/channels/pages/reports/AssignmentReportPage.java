// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Identifiable;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import java.util.Calendar;

/**
 * Task details for an assignment.
 */
public class AssignmentReportPage extends AbstractReportPage {


    //--------------------------------
    public AssignmentReportPage( PageParameters parameters ) {
        super( parameters );

        add(
            new Label( "pageTitle" ),
            new Label( "reportTitle" ),

            new BookmarkablePageLink<ProceduresReportPage>( "top-link",
                         ProceduresReportPage.class, getTopParameters() ),

            new AnchoredLink<ProceduresReportPage>( "event-link",
                         ProceduresReportPage.class, getTopParameters(), getEvent() )
                .add( new Label( "assignment.part.segment.event.name" )
                        .setRenderBodyOnly( true ) ),

            new AnchoredLink<ProceduresReportPage>( "phase-link",
                         ProceduresReportPage.class, getTopParameters(), getPhase() )
                .add( new Label( "assignment.part.segment.phase.name" )
                        .setRenderBodyOnly( true ) ),

            new AnchoredLink<ProceduresReportPage>( "type-link",
                         ProceduresReportPage.class, getTopParameters(), getTypePrefix() + getPhase().getId() )
                .add( new Label( "type" )
                        .setRenderBodyOnly( true ) ),

            new Label( "assignment.part.task" ),
            new AssignmentReportPanel("assignment", this),
            new Label("year", "" + Calendar.getInstance().get(Calendar.YEAR) ),
            new Label("client", getPlanService().getPlan().getClient() )

        );
    }

    private Identifiable getEvent() {
        return getAssignment().getPart().getSegment().getEvent();
    }

    private Identifiable getPhase() {
        return getAssignment().getPart().getSegment().getPhase();
    }
}
