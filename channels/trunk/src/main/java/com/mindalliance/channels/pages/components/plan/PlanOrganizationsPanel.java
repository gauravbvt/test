/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.GeomapLinkPanel;
import com.mindalliance.channels.pages.components.entities.EntitiesPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Plan organizations panel.
 */
public class PlanOrganizationsPanel extends AbstractCommandablePanel {

    private static final String PREFIX_DOM_IDENTIFIER = ".entities";

    public PlanOrganizationsPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }

    private void init() {
        addGeomapLink();
        addTabPanel();
    }

    private void addGeomapLink() {
        GeomapLinkPanel geomapLink = new GeomapLinkPanel( "geomapLink",
                                                          new Model<String>( "Organizations with known locations" ),
                                                          getActualOrganizations(),
                                                          new Model<String>( "Locate organizations on a map" ) );
        geomapLink.setOutputMarkupId( true );
        makeVisible( geomapLink, !getActualOrganizations().isEmpty() );
        addOrReplace( geomapLink );
    }

    private void addTabPanel() {
        AjaxTabbedPanel tabbedPanel = new AjaxTabbedPanel( "tabs", getTabs() );
        tabbedPanel.setOutputMarkupId( true );
        addOrReplace( tabbedPanel );
    }

    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add( new AbstractTab( new Model<String>( "Scope" ) ) {
            public Panel getPanel( String id ) {
                return new PlanOrganizationScopePanel( id, new Model<Plan>( getPlan() ), getExpansions() );
            }
        } );
        tabs.add( new AbstractTab( new Model<String>( "Network" ) ) {
            public Panel getPanel( String id ) {
                return new EntitiesPanel<Organization>( id,
                                                        Organization.class,
                                                        null,
                                                        getExpansions(),
                                                        PREFIX_DOM_IDENTIFIER );
            }
        } );
        return tabs;
    }

    /**
     * Get network's domain.
     *
     * @return a list or organizations
     */
    @SuppressWarnings( "unchecked" )
    public List<Organization> getActualOrganizations() {
        return (List<Organization>) CollectionUtils.select( getQueryService().listActualEntities( Organization.class ),
                                                            new Predicate() {
                                                                public boolean evaluate( Object object ) {
                                                                    return !( (Organization) object ).isUnknown();
                                                                }
                                                            } );
    }

    /**
     * Get relationships defining the network.
     *
     * @return a list of (sharing flow) relationships between organizations
     */
    public List<EntityRelationship<Organization>> getOrganizationRelationships() {
        List<EntityRelationship<Organization>> orgRels = new ArrayList<EntityRelationship<Organization>>();
        List<Organization> orgs = getActualOrganizations();
        for ( Organization org : orgs ) {
            for ( Organization other : orgs ) {
                if ( org != other ) {
                    EntityRelationship<Organization> sendRel =
                            getAnalyst().findEntityRelationship( getQueryService(), org, other );
                    if ( sendRel != null )
                        orgRels.add( sendRel );
                }
            }
        }
        return orgRels;
    }
}
