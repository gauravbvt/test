package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.components.diagrams.PlanMapDiagramPanel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import java.text.Collator;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * The project report.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 5, 2009
 * Time: 5:13:56 PM
 */
public class ProjectReportPage extends WebPage {

    public ProjectReportPage( PageParameters parameters ) {
        super( parameters );

        Project project = Project.getProject();
        add( new Label( "title",                                                          // NON-NLS
                        MessageFormat.format( "Report: {0}", project.getProjectName() ) ) );
        add( new Label( "project-name", project.getProjectName() ) );                     // NON-NLS
        add( new Label( "project-client", project.getClient() ) );                        // NON-NLS
        add( new Label( "project-description", project.getDescription() ) );              // NON-NLS
        add( new Label( "date", DateFormat.getDateTimeInstance(                           // NON-NLS
            DateFormat.LONG, DateFormat.LONG ).format( new Date() ) ) );
        List<Scenario> scenarios = Project.dqo().list( Scenario.class );
        Collections.sort( scenarios, new Comparator<Scenario>() {
            public int compare( Scenario o1, Scenario o2 ) {
                return Collator.getInstance().compare( o1.getName(), o2.getName() );
            }
        } );
        add( new ListView<Scenario>( "scenarios", scenarios ) {                           // NON-NLS
            @Override
            protected void populateItem( ListItem<Scenario> item ) {
                item.add( new ScenarioReportPanel( "scenario", item.getModel() ) );       // NON-NLS
            }
        } );
        add( new PlanMapDiagramPanel( "planMap",                                          // NON-NLS
            new Model<ArrayList<Scenario>>( (ArrayList) scenarios ),
            null, null ) );
    }
}
