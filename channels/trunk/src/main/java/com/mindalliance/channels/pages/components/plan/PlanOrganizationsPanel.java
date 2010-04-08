package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.GeomapLinkPanel;
import com.mindalliance.channels.pages.components.entities.EntitiesPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Plan organizatons panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 6, 2010
 * Time: 2:44:16 PM
 */
public class PlanOrganizationsPanel extends AbstractCommandablePanel {

    private static final String PREFIX_DOM_IDENTIFIER = ".entities";

    public PlanOrganizationsPanel(
            String id,
            IModel<? extends Identifiable> model,
            Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        addGeomapLink();
        addTabPanel();
    }

    private void addGeomapLink() {
        GeomapLinkPanel geomapLink = new GeomapLinkPanel(
                "geomapLink",
                new Model<String>( "Organizations with known locations" ),
                getActualOrganizations(),
                new Model<String>( "Locate organizations on a map" ) );
        geomapLink.setOutputMarkupId( true );
        makeVisible( geomapLink, !getActualOrganizations().isEmpty() );
        addOrReplace( geomapLink );
    }

    private void addTabPanel() {
        add( new AjaxTabbedPanel( "tabs", getTabs() ) );
    }

    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add( new AbstractTab( new Model<String>( "Scope" ) ) {
            public Panel getPanel( String id ) {
                return new PlanScopePanel(
                        id,
                        new Model<Plan>( getPlan() ),
                        getExpansions() );
            }
        } );
        tabs.add( new AbstractTab( new Model<String>( "Network" ) ) {
            public Panel getPanel( String id ) {
                return new EntitiesPanel<Organization>(
                        id,
                        new PropertyModel<List<Organization>>(
                                PlanOrganizationsPanel.this,
                                "actualOrganizations" ),
                        getExpansions(),
                        PREFIX_DOM_IDENTIFIER );
            }
        } );
        return tabs;
    }

    @SuppressWarnings( "unchecked" )
    public List<Organization> getActualOrganizations() {
        return (List<Organization>) CollectionUtils.select(
                getQueryService().listActualEntities( Organization.class ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return !( (Organization) object ).isUnknown();
                    }
                }
        );
    }

}
