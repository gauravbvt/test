package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * The project report
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 5, 2009
 * Time: 5:13:56 PM
 */
public class ProjectReportPage extends WebPage {

    public ProjectReportPage( PageParameters parameters ) {
        super( parameters );
        final Service service = ( (Project) getApplication() ).getService();
        init( service );
    }

    private void init( Service service ) {
        add( new Label( "title", "Report: " + Project.getProject().getProjectName() ) );
        add( new Label( "project-name", Project.getProject().getProjectName() ) );
        add( new Label( "project-client", Project.getProject().getClient() ) );
        add( new Label( "project-description", Project.getProject().getDescription() ) );
        add( new Label( "date", new SimpleDateFormat("E M d y, HH:mm:ss z").format(new Date()) ) );
        List<Scenario> scenarios = service.list( Scenario.class );
        add (new ListView<Scenario>("scenarios", scenarios ){
            protected void populateItem( ListItem<Scenario> item ) {
                item.add( new ScenarioReportPanel("scenario", item.getModel()));
            }
        } );
    }

}
