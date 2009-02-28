package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.text.Collator;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
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
        init();
    }

    private void init() {
        Project project = Project.getProject();

        add( new Label( "title", "Report: " + project.getProjectName() ) );
        add( new Label( "project-name", project.getProjectName() ) );
        add( new Label( "project-client", project.getClient() ) );
        add( new Label( "project-description", project.getDescription() ) );
        add( new Label( "date", DateFormat.getDateTimeInstance(
            DateFormat.LONG, DateFormat.LONG ).format( new Date() ) ) );

        List<Scenario> scenarios = Project.service().list( Scenario.class );
        Collections.sort( scenarios, new Comparator<Scenario>() {
            public int compare( Scenario o1, Scenario o2 ) {
                return Collator.getInstance().compare( o1.getName(), o2.getName() );
            }
        } );
        add( new ListView<Scenario>( "scenarios", scenarios ) {
            @Override
            protected void populateItem( ListItem<Scenario> item ) {
                item.add( new ScenarioReportPanel( "scenario", item.getModel() ) );
            }
        } );
    }

}
