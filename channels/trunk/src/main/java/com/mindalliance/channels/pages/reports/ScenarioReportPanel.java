package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.DiagramFactory;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Risk;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.ModelObject;
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

    public ScenarioReportPanel( String id, IModel<Scenario> model, final Actor actor ) {
        super( id, model );
        setRenderBodyOnly( true );
        scenario = model.getObject();

        addScenarioPage();

        add( new ListView<Organization>( "organizations", scenario.getOrganizations() ) { // NON-NLS
            @Override
            protected void populateItem( ListItem<Organization> item ) {
                Organization organization = item.getModelObject();
                item.add( new AttributeModifier( "class", true, new Model<String>(        // NON-NLS
                        organization.getParent() == null ? "top organization"             // NON-NLS
                                                         : "sub organization" ) ) );      // NON-NLS
                item.add( new OrganizationReportPanel(
                        "organization", organization, scenario, actor, true ) );          // NON-NLS
            }
        } );
    }

    private void addScenarioPage() {
        add( new Label( "name", MessageFormat.format(                                     // NON-NLS
                        "Scenario: {0}", scenario.getName() ) ) );
        add( new Label( "description", scenario.getDescription() ) );                     // NON-NLS
        WebMarkupContainer eventSection = new WebMarkupContainer( "event-section" );
        Event event = scenario.getEvent();
        eventSection.add( new Label( "event",
                                     event == null ? "" : event.getName().toLowerCase() ) );
        eventSection.setVisible( event != null );
        add( eventSection );
        WebMarkupContainer risks = new WebMarkupContainer( "risk-section" );
        List<Risk> riskList = scenario.getRisks();
        risks.add( new ListView<Risk>( "risks", riskList ) {
            @Override
            protected void populateItem( ListItem<Risk> item1 ) {
                Risk risk = item1.getModelObject();
                item1.add( new Label( "risk", risk.getLabel() ) );
                item1.add( new Label( "risk-desc", item1.getModelObject().getDescription() ) );
            }
        } );
        risks.setVisible( !riskList.isEmpty() );
        add( risks );

//        double[] size = { 7.5, 9.0 };
        add( new FlowMapDiagramPanel(
                        "flowMap",                                                        // NON-NLS
                        new Model<Scenario>( scenario ),
                        null,
                        //size,
                        new Settings( null, DiagramFactory.TOP_BOTTOM, null, true, false ) ) );

        add( new IssuesReportPanel( "issues", new Model<ModelObject>( scenario ) ) );     // NON-NLS
    }
}
