package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.DiagramFactory;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Risk;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.pages.components.diagrams.FlowMapDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.text.MessageFormat;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 5, 2009
 * Time: 7:18:56 PM
 */
public class ScenarioReportPanel extends Panel {

    /**
     * A scenario
     */
    private Scenario scenario;

    public ScenarioReportPanel(
            String id, IModel<Scenario> model, final Actor actor, final boolean showingIssues ) {
        super( id, model );
        setRenderBodyOnly( true );
        scenario = model.getObject();

        addScenarioPage( scenario, scenario.getEvent(), showingIssues );

        add( new ListView<Organization>( "organizations", scenario.getOrganizations() ) { // NON-NLS
            @Override
            protected void populateItem( ListItem<Organization> item ) {
                Organization organization = item.getModelObject();
                item.add( new AttributeModifier( "class", true, new Model<String>(        // NON-NLS
                        organization.getParent() == null ? "top organization"             // NON-NLS
                                                         : "sub organization" ) ) );      // NON-NLS
                item.add( new OrganizationReportPanel( "organization",                    // NON-NLS
                        organization, scenario, actor, true, showingIssues ) );
            }
        } );
    }

    private void addScenarioPage( Scenario s, Event event, boolean showIssues ) {
        String eventName = event == null ? "" : event.getName().toLowerCase();
        List<Risk> riskList = s.getRisks();
        add( new Label( "name", MessageFormat.format( "Scenario: {0}", s.getName() ) ),

             new Label( "description", s.getDescription() ),                              // NON-NLS

             new WebMarkupContainer( "event-section" )
                .add( new Label( "event", eventName ) ).setVisible( event != null ),

             new WebMarkupContainer( "risk-section" )
                .add( new ListView<Risk>( "risks", riskList ) {
                        @Override
                        protected void populateItem( ListItem<Risk> item ) {
                            Risk risk = item.getModelObject();
                            item.add( new Label( "risk", risk.getLabel() ),
                                      new Label( "risk-desc", risk.getDescription() ) );
                        }
                    } ).setVisible( !riskList.isEmpty() ),

             new FlowMapDiagramPanel( "flowMap",                                          // NON-NLS
                        new Model<Scenario>( s ),
                        null,
                        //size,
                        new Settings( null, DiagramFactory.TOP_BOTTOM, null, true, false ) ),

            new IssuesReportPanel( "issues", new Model<ModelObject>( s ) )                // NON-NLS
                    .setVisible( showIssues )
        );
    }
}
