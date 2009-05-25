package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.text.MessageFormat;

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

    public ScenarioReportPanel( String id, IModel<Scenario> model ) {
        super( id, model );
        setRenderBodyOnly( true );
        scenario = model.getObject();
        init();
    }

    private void init() {
        add( new Label( "name", MessageFormat.format(                                     // NON-NLS
                        "Scenario: {0}", scenario.getName() ) ) );
        add( new Label( "description", scenario.getDescription() ) );                     // NON-NLS

        double[] size = { 7.5, 9.0 };
/*
        add( new FlowMapDiagramPanel(
                        "flowMap",                                                        // NON-NLS
                        new Model<Scenario>( scenario ),
                        null,
                        size,
                        DiagramFactory.TOP_BOTTOM,
                        false,
                        null ) );
*/

        add( new ListView<Organization>( "organizations", scenario.getOrganizations() ) { // NON-NLS
            @Override
            protected void populateItem( ListItem<Organization> item ) {
                Organization organization = item.getModelObject();
                item.add( new AttributeModifier( "class", true, new Model<String>(        // NON-NLS
                        organization.getParent() == null ? "top organization"             // NON-NLS
                                                         : "sub organization" ) ) );      // NON-NLS
                item.add( new OrganizationReportPanel(
                        "organization", organization, scenario, true ) );                 // NON-NLS
            }
        } );
        add( new IssuesReportPanel( "issues", new Model<ModelObject>( scenario ) ) );     // NON-NLS
    }
}
