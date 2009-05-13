package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.Collator;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
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

    /** The query service. */
    @SpringBean
    private QueryService queryService;

    public PlanReportPage( PageParameters parameters ) {
        super( parameters );

        Plan plan = Channels.getPlan();

        add( new Label( "title",                                                          // NON-NLS
                        MessageFormat.format( "Report: {0}", plan.getName() ) ) );
        add( new Label( "plan-name", plan.getName() ) );                                  // NON-NLS
        add( new Label( "plan-client", plan.getClient() ) );                              // NON-NLS
        add( new Label( "plan-description", plan.getDescription() ) );                    // NON-NLS
        add( new Label( "date", DateFormat.getDateTimeInstance(                           // NON-NLS
            DateFormat.LONG, DateFormat.LONG ).format( new Date() ) ) );

        List<Scenario> scenarios = queryService.list( Scenario.class );
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
/*        add( new PlanMapDiagramPanel( "planMap",                                          // NON-NLS
            new Model<ArrayList<Scenario>>( (ArrayList<Scenario>) scenarios ),
            null, null, null, null, false, null ) );*/
    }
}
