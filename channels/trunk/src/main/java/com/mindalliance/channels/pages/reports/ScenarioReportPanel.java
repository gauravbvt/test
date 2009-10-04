package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.DiagramFactory;
import com.mindalliance.channels.model.Actor;
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

        addScenarioPage( scenario, showingIssues );

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

    private void addScenarioPage( Scenario s, boolean showIssues ) {
        List<Risk> riskList = s.getRisks();
        add( new Label( "name", s.getName() )
                .add( new AttributeModifier( "name", true,
                                             new Model<String>( String.valueOf( s.getId() ) ) ) ),

             new Label( "description", getScenarioDesc( s ) ).setRenderBodyOnly( true ),  // NON-NLS

             new Label( "event", s.getPhaseEventTitle() ).setVisible( s.getEvent() != null ),

             new WebMarkupContainer( "risk-lead" )
                     .setRenderBodyOnly( true )
                     .setVisible( !riskList.isEmpty() ),

             new WebMarkupContainer( "risk-section" )
                .add( new ListView<Risk>( "risks", riskList ) {
                        @Override
                        protected void populateItem( ListItem<Risk> item ) {
                            Risk risk = item.getModelObject();
                            item.add( new Label( "risk", risk.getLabel() )
                                            .setRenderBodyOnly( true ),
                                      new Label( "risk-desc", risk.getDescription() )
                                            .setRenderBodyOnly( true ) );
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

    private static String getScenarioDesc( Scenario s ) {
        String desc = s.getDescription();
        return desc.isEmpty() || !desc.endsWith( "." ) ?
               desc + "." : desc;
    }   
}
