package com.mindalliance.channels.pages.components.plan.requirements;

import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Requirement;
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

/**
 * Plan requirements panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 9/29/11
 * Time: 1:54 PM
 */
public class PlanRequirementsPanel extends AbstractCommandablePanel {

    private AjaxTabbedPanel tabbedPanel;

    public PlanRequirementsPanel(
            String id,
            IModel<? extends Identifiable> model ) {
        super( id, model );
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
        tabbedPanel = new AjaxTabbedPanel( "tabs", getTabs() );
        tabbedPanel.setOutputMarkupId( true );
        addOrReplace( tabbedPanel );
    }

    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add( new AbstractTab( new Model<String>( "Definitions" ) ) {
            public Panel getPanel( String id ) {
                return new PlanRequirementDefinitionsPanel(
                        id,
                        new Model<Requirement>( getRequirement()) );
            }
        } );
        tabs.add( new AbstractTab( new Model<String>( "Required network" ) ) {
            public Panel getPanel( String id ) {
                return new PlanRequiredNetworkingPanel( id, new Model<Plan>( getPlan() ), getExpansions() );
            }
        } );
        return tabs;
    }


    public void select( Requirement requirement ) {
        ( (PlanRequirementDefinitionsPanel) tabbedPanel.getTabs( ).get( 0 ) ).select( requirement );
        tabbedPanel.setSelectedTab( 0 );
    }

    private Requirement getRequirement() {
        return (Requirement) getModel().getObject();
    }
}
