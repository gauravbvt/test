package com.mindalliance.channels.pages.reports;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.AttributeModifier;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.text.Collator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 6, 2009
 * Time: 11:46:56 AM
 */
public class OrganizationReportPanel extends Panel {
    /**
     * An organization
     */
    private Organization organization;
    /**
     * The scenario in context
     */
    private Scenario scenario;

    public OrganizationReportPanel( String id, IModel<Organization> model, Scenario scenario ) {
        super( id, model );
        organization = model.getObject();
        this.scenario = scenario;
        init();
    }

    private void init() {
        WebMarkupContainer orgDiv = new WebMarkupContainer("organizationDiv");
        add (orgDiv);
        String styleClass = organization.getParent() == null ? "top-organization" : "sub-organization";
        orgDiv.add (new AttributeModifier("class", true, new Model<String>(styleClass)) );
        orgDiv.add( new Label("name", organization.getName()));
        orgDiv.add( new Label("description", organization.getDescription()));
        orgDiv.add( new Label("parentage", organization.parentage()));
        List<Role> roles = findRolesInScenario();
        Collections.sort( roles, new Comparator<Role>() {
            /** {@inheritDoc} */
            public int compare( Role role1, Role role2 ) {
                return Collator.getInstance().compare( role1.getName(), role2.getName() );
            }
        } );
        orgDiv.add( new ListView<Role>( "roles", roles ) {
            protected void populateItem( ListItem<Role> item ) {
                Role role = item.getModelObject();
                item.add( new RoleReportPanel( "role", new Model<Role>( role ), scenario ) );
            }
        } );
        List<Organization> subOrganizations = findSubOrganizationsInScenario();
        orgDiv.add( new ListView<Organization>("sub-organizations", subOrganizations) {
            protected void populateItem( ListItem<Organization> item ) {
                Organization subOrganization = item.getModelObject();
                item.add( new OrganizationReportPanel(
                        "sub-organization",
                        new Model<Organization>(subOrganization),
                        scenario));
            }
        } );
    }
    // TODO - inefficient, C&P from ScenarioReportPanel:findOrganizationsInScenario
    private List<Organization> findSubOrganizationsInScenario() {
        Set<Organization> organizations = new HashSet<Organization>();
        Iterator<Part> parts = scenario.parts();
        while(parts.hasNext()) {
            Part part = parts.next();
            if (part.getOrganization() != null && part.getOrganization().getParent() == organization) {
                organizations.add(part.getOrganization());
            }
        }
        List<Organization> results = new ArrayList<Organization>();
        results.addAll(organizations);
        Collections.sort(results, new Comparator<Organization>() {
            /** {@inheritDoc} */
            public int compare( Organization org1, Organization org2 ) {
                return Collator.getInstance().compare(org1.getName(), org2.getName());
            }
        } );
        return results;
    }

    private List<Role> findRolesInScenario() {
        Set<Role> roles = new HashSet<Role>();
        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( part.getRole() != null ) roles.add( part.getRole() );
        }
        List<Role> list = new ArrayList<Role>();
        list.addAll( roles );
        return list;
    }

}
