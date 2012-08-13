// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages.procedures;

import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.pages.reports.AnchoredLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.Calendar;

/**
 * Report L4.
 */
public class CommitmentReportPage extends AbstractReportPage {


    public CommitmentReportPage( PageParameters parameters ) {
        super( parameters );

        add(
                new Label( "pageTitle" ),
                new Label( "reportTitle" ),

                new BookmarkablePageLink<ProceduresReportPage>( "top-link",
                        ProceduresReportPage.class, getTopParameters() ),
                new AnchoredLink<ProceduresReportPage>( "event-link",
                             ProceduresReportPage.class,
                            getTopParameters(),
                            getEvent() )
                    .add( new Label( "assignment.part.segment.event.name" )
                            .setRenderBodyOnly( true ) ),

                new AnchoredLink<ProceduresReportPage>( "phase-link",
                             ProceduresReportPage.class, getTopParameters(), getPhase() )
                    .add( new Label( "assignment.part.segment.phase.name" )
                            .setRenderBodyOnly( true ) ),

                new AnchoredLink<ProceduresReportPage>( "type-link",
                             ProceduresReportPage.class, getTopParameters(), getTypePrefix() + getPhase().getId() )
                    .add( new Label( "type" ).setRenderBodyOnly( true ) ),

                newTaskLink( getPart(), getActor() ),
                new Label( "flow.name" ),
                new CommitmentReportPanel( "commitment", this ),
                new Label( "year", "" + Calendar.getInstance().get( Calendar.YEAR ) ),
                new Label( "client", getPlanService().getPlan().getClient() )
        );
    }

    private PageParameters getTaskParameters() {
        PageParameters parameters = getTopParameters();
        return parameters;
    }

    private Identifiable getEvent() {
        return getAssignment().getPart().getSegment().getEvent();
    }

    private Identifiable getPhase() {
        return getAssignment().getPart().getSegment().getPhase();
    }


}
