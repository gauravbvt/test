package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.Channels;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * The plan SOPs report.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 5, 2009
 * Time: 5:13:56 PM
 */
public class ProceduresReportPage extends AbstractReportPage {

    /**
     * Restrictions to report generation.
     */
    private SelectorPanel selector;

    public ProceduresReportPage( PageParameters parameters ) {
        super( parameters );
        setDefaultModel( new CompoundPropertyModel<Object>( this ) );
        addChannelsLogo();
        selector = new SelectorPanel( "selector",this );
        if ( !selector.isValid() ) {
            if ( selector.getPlans().isEmpty() )
                throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_FORBIDDEN );

            setRedirect( true );
            throw new RestartResponseException( getClass(), selector.getParameters() );
        }

        add( new BookmarkablePageLink<ProceduresReportPage>( "top-link", ProceduresReportPage.class ) );
        add( new ListView<Organization>(
                "breadcrumbs",
                new PropertyModel<List<Organization>>( this, "breadcrumbs" )
        ) {
            @Override
            protected void populateItem( ListItem<Organization> item ) {
                Organization organization = item.getModelObject();
                item.add( new WebMarkupContainer( "crumb" )
                        .add( new Label( "text", organization.getName() ) )
                        .setRenderBodyOnly( true ) );
            }
        } );


        add( new Label( "selector.actor.name" ) );
        add( new Label( "pageTitle" ),

                new Label( "reportTitle" ),

                selector.newPlanSelector()
                        .setVisible( !selector.isPlanner() && selector.getPlans().size() > 1 ),
                selector.setVisible( selector.isPlanner() ),
                new AssignmentsReportPanel( "assignments", (AssignmentsSelector) selector, ProceduresReportPage.this ),
                /*new Label( "year", "" + Calendar.getInstance().get( Calendar.YEAR ) ),*/
                new Label( "client", selector.getPlan().getClient() )
        );
    }

    private void addChannelsLogo() {
        WebMarkupContainer channels_logo = new WebMarkupContainer( "channelsHome");
        channels_logo.add( new AjaxEventBehavior( "onclick") {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                String homeUrl =  AbstractChannelsWebPage.redirectUrl( "home", getPlan() );
                RedirectPage page =  new RedirectPage( homeUrl );
                setResponsePage( page );
            }
        });
        add( channels_logo );
    }


    /**
     * Set the headers of the Page being served.
     *
     * @param response the response.
     */
    @Override
    protected void setHeaders( WebResponse response ) {
//        super.setHeaders( response );

        Channels channels = (Channels) getApplication();
        Commander commander = channels.getCommander( selector.getPlan() );
        long longTime = commander.getLastModified();
        long now = System.currentTimeMillis();

        response.setDateHeader( "Date", now );
        response.setHeader( "Cache-Control", "max-age=0, private, must-revalidate" );
//        response.setDateHeader( "Expires", now );
//        response.setDateHeader( "Expires", now + 24L*60*60*1000 );
        response.setDateHeader( "Last-Modified", longTime );
    }

    public List<Organization> getBreadcrumbs() {
         List<Organization> result = new ArrayList<Organization>();
         if ( selector.isOrgSelected() )
             for ( Organization o = selector.getOrganization(); o != null; o = o.getParent() )
                 result.add( 0, o );
         return result;
     }



    public String getReportTitle() {
        return "Procedures - " + selector.getSelection().toString();
    }

    public String getPageTitle() {
        return "Channels - " + getReportTitle();
    }


}
