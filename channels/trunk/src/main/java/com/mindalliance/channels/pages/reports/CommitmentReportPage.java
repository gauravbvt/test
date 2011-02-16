// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages.reports;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

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
                new Label( "assignment.part.segment.event.name" ),
                new Label( "assignment.part.segment.phase.name" ),
                new Label( "type" ),
                new Label( "assignment.part.task" ),
                new Label( "flow.name" ),
                new CommitmentReportPanel( "commitment", this ),
                new Label( "year", "" + Calendar.getInstance().get( Calendar.YEAR ) ),
                new Label( "client", getService().getPlan().getClient() )
        );
    }


}
