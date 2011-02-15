package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.pages.Channels;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;

import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;

/**
 * The plan SOPs report.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 5, 2009
 * Time: 5:13:56 PM
 */
public class ProceduresReportPage extends WebPage {

    /**
     * Restrictions to report generation.
     */
    private SelectorPanel selector;

    public ProceduresReportPage( PageParameters parameters ) {
        super( parameters );
        setDefaultModel( new CompoundPropertyModel<Object>( this ) );

        selector = new SelectorPanel( "selector", parameters );
        if ( !selector.isValid() ) {
            if ( selector.getPlans().isEmpty() )
                throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_FORBIDDEN );

            setRedirect( true );
            throw new RestartResponseException( getClass(), selector.getParameters() );
        }

        add( new Label( "pageTitle" ),

                new Label( "reportTitle" ),

                selector.newPlanSelector()
                        .setVisible( !selector.isPlanner() && selector.getPlans().size() > 1 ),
                selector.setVisible( selector.isPlanner() ),
                new AssignmentsReportPanel( "assignments", (AssignmentsSelector) selector ),
                new Label( "year", "" + Calendar.getInstance().get( Calendar.YEAR ) ),
                new Label( "client", selector.getPlan().getClient() )
        );
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
        Commander commander = channels.getCommander( selector.getPlan() );
        long longTime = commander.getLastModified();
        long now = System.currentTimeMillis();

        response.setDateHeader( "Date", now );
//        response.setDateHeader( "Expires", now + 24L*60*60*1000 );
        response.setDateHeader( "Last-Modified", longTime );
    }

    public String getReportTitle() {
        return "Procedures - " + selector.getSelection().toString();
    }

    public String getPageTitle() {
        return "Channels - " + getReportTitle();
    }


}
