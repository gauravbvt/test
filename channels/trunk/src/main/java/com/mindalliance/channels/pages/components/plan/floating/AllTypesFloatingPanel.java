package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.PlanTypologiesPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * All Types Floating Panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 7:24 PM
 */
public class AllTypesFloatingPanel extends AbstractFloatingCommandablePanel {

    private PlanTypologiesPanel planTypologiesPanel;

    public AllTypesFloatingPanel( String id, IModel<Plan> planModel ) {
        super( id, planModel );
        init();
    }

    private void init() {
        addHeading();
        addPlanTypesPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "Taxonomies" ) );
    }

    private void addPlanTypesPanel() {
        planTypologiesPanel = new PlanTypologiesPanel( "typologies" );
        getContentContainer().add( planTypologiesPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.ALL_TYPES );
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "Taxonomies";
    }

}

