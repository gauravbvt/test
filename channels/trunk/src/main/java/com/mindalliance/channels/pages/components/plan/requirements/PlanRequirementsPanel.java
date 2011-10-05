package com.mindalliance.channels.pages.components.plan.requirements;

import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
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
 * Plan requirements panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 9/29/11
 * Time: 1:54 PM
 */
public class PlanRequirementsPanel extends AbstractCommandablePanel {

    public PlanRequirementsPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }

    private void init() {
        addTabsPanel();
    }

    private void addTabsPanel() {
        AjaxTabbedPanel tabbedPanel = new AjaxTabbedPanel( "tabs", getTabs() );
        tabbedPanel.setOutputMarkupId( true );
        addOrReplace( tabbedPanel );
    }

    private List<ITab> getTabs() {
         List<ITab> tabs = new ArrayList<ITab>();
         tabs.add( new AbstractTab( new Model<String>( "Definitions" ) ) {
             public Panel getPanel( String id ) {
                 return new PlanRequirementDefinitionsPanel( id, new Model<Plan>( getPlan() ), getExpansions() );
             }
         } );
        tabs.add( new AbstractTab( new Model<String>( "Required network" ) ) {
            public Panel getPanel( String id ) {
                return new PlanRequiredNetworkingPanel( id, new Model<Plan>( getPlan() ), getExpansions() );
            }
        } );
         return tabs;
     }

}
