package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
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

    private static final String ALL = "all";

    /** The query service. */
    @SpringBean
    private QueryService queryService;

    /** The current plan. */
    private Plan plan = Channels.getPlan();

    public PlanReportPage( PageParameters parameters ) {
        super( parameters );

        add( new Label( "title",                                                          // NON-NLS
                        MessageFormat.format( "Report: {0}", plan.getName() ) ) );
        add( new Label( "plan-name", plan.getName() ) );                                  // NON-NLS
        add( new Label( "plan-client", plan.getClient() ) );                              // NON-NLS
        add( new Label( "plan-description", plan.getDescription() ) );                    // NON-NLS
        add( new Label( "date", DateFormat.getDateTimeInstance(                           // NON-NLS
            DateFormat.LONG, DateFormat.LONG ).format( new Date() ) ) );

        List<Scenario> scenarios = getScenarios( parameters );
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

    private List<Scenario> getScenarios( PageParameters parameters ) {
        List<Scenario> scenarios;

        String selector = parameters.getString( "0", ALL );
        if ( ALL.equals( selector ) ) {
            scenarios = queryService.list( Scenario.class );
            Collections.sort( scenarios );

        } else {
            scenarios = new ArrayList<Scenario>();
            try {
                scenarios.add( queryService.findScenario( selector ) );

            } catch ( NotFoundException ignored ) {
                // Redirect to use all scenarios
                Logger logger = LoggerFactory.getLogger( getClass() );
                logger.warn( "Unable to find scenario {}", selector );

                parameters.put( "0", ALL );
                getRequestCycle().setRedirect( true );
                throw new RestartResponseException( getClass(), parameters );
            }
        }

        return scenarios;
    }
}
