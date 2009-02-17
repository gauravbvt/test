package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.graph.DiagramMaker;
import com.mindalliance.channels.pages.components.FlowDiagramPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.AttributeModifier;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.text.Collator;
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
        add( new Label( "name", MessageFormat.format(
                        "Scenario: {0}", scenario.getName() ) ) );
        add( new Label( "description", scenario.getDescription() ) );

        double[] size = { 7.5, 10.0 };
        add( new FlowDiagramPanel(
                        "flowMap",
                        new Model<Scenario>( scenario ),
                        size,
                        DiagramMaker.TOP_BOTTOM ) );

        List<Organization> organizations = findTopOrganizationsInScenario();
        add( new ListView<Organization>( "organizations", organizations ) {
            @Override
            protected void populateItem( ListItem<Organization> item ) {
                Organization organization = item.getModelObject();
                item.add( new AttributeModifier( "class", true, new Model<String>(
                        organization.getParent() == null ? "top-organization"
                                                         : "sub-organization" ) ) );
                item.add( new OrganizationReportPanel(
                        "organization",
                        new Model<Organization>( organization ),
                        scenario ) );
            }
        } );
        add( new IssuesReportPanel( "issues", new Model<ModelObject>( scenario ) ) );
    }

    /**
     * Find organizations involved in scenario but without parent organizations.
     *
     * @return a list of organizations
     */
    private List<Organization> findTopOrganizationsInScenario() {
        Set<Organization> organizations = new HashSet<Organization>();
        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            Organization organization = part.getOrganization();
            if ( organization != null && organization.getParent() == null ) {
                organizations.add( part.getOrganization() );
            }
        }
        List<Organization> results = new ArrayList<Organization>();
        results.addAll( organizations );
        Collections.sort( results, new Comparator<Organization>() {
            /** {@inheritDoc} */
            public int compare( Organization org1, Organization org2 ) {
                return Collator.getInstance().compare( org1.getName(), org2.getName() );
            }
        } );
        return results;
    }

}
