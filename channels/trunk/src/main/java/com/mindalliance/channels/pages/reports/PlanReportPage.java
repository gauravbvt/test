package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.pages.components.diagrams.PlanMapDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
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

    /** The parameter that specifies all scenarios. */
    private static final String ALL = "all";

    /**
     * Plan manager.
     */
    @SpringBean
    private PlanManager planManager;

    /** The query service. */
    @SpringBean
    private QueryService queryService;

    /** The current plan. */
    private Plan plan = planManager.getCurrentPlan();

    /** Restrictions to report generation. */
    private SelectorPanel selector;

    public PlanReportPage( PageParameters parameters ) {
        super( parameters );

        selector = new SelectorPanel( "selector", parameters );
        if ( !selector.isValid() ) {
            setRedirect( true );
            throw new RestartResponseException( getClass(), selector.getParameters() );
        }

        String reportDate = DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.LONG )
                                .format( new Date() );
        List<Scenario> scenarios = selector.getScenarios();

        add( selector,
             new Label( "title",                                                          // NON-NLS
                        MessageFormat.format( "Report: {0}", plan.getName() ) ),
             new Label( "plan-name", plan.getName() ),                                    // NON-NLS
             new Label( "plan-client", plan.getClient() ),                                // NON-NLS
             new Label( "plan-description", plan.getDescription() ),                      // NON-NLS
             new Label( "date", reportDate ),

             new ListView<Scenario>( "scenarios", scenarios ) {                           // NON-NLS
                    @Override
                    protected void populateItem( ListItem<Scenario> item ) {
                        item.add( new ScenarioReportPanel( "scenario",                    // NON-NLS
                                        item.getModel(),
                                        selector.isAllActors() ? null : selector.getActor(),
                                        selector.isShowingIssues() ) );
                    }
                },

             new PlanMapDiagramPanel( "planMap",                                          // NON-NLS
                new Model<ArrayList<Scenario>>( (ArrayList<Scenario>) scenarios ),
                selector.isAllScenarios() ? null : selector.getScenario(),
                null,
                new Settings() )
        );
    }
}
