package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Scenario;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
        setRenderBodyOnly( true );
        organization = model.getObject();
        this.scenario = scenario;
        init();
    }

    private void init() {
        add( new Label( "name", MessageFormat.format(                                     // NON-NLS
                "Organization: {0}", organization.toString() ) ) );
        String desc = organization.getDescription();
        Label descLabel = new Label( "description", desc );                               // NON-NLS
        descLabel.setVisible( desc != null && !desc.isEmpty() );
        add( descLabel );

        List<Role> roles = findRolesInScenario();
        Collections.sort( roles, new Comparator<Role>() {
            /** {@inheritDoc} */
            public int compare( Role o1, Role o2 ) {
                return Collator.getInstance().compare( o1.getName(), o2.getName() );
            }
        } );
        add( new ListView<Role>( "roles", roles ) {                                       // NON-NLS
            @Override
            protected void populateItem( ListItem<Role> item ) {
                Role role = item.getModelObject();
                item.add( new RoleReportPanel( "role",                                    // NON-NLS
                                               new Model<Role>( role ), scenario, organization ) );
            }
        } );
    }

    private List<Role> findRolesInScenario() {
        Set<Role> roles = new HashSet<Role>();
        Iterator<Part> parts = scenario.parts();
        while ( parts.hasNext() ) {
            Part part = parts.next();
            if ( part.getRole() != null && organization.equals( part.getOrganization() ) )
                roles.add( part.getRole() );
        }
        List<Role> list = new ArrayList<Role>();
        list.addAll( roles );
        return list;
    }

}
