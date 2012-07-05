package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.FloatingCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Plan Index Floating Panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 7:17 PM
 */
public class PlanIndexFloatingPanel extends FloatingCommandablePanel {

    private PlanIndexPanel planIndexPanel;

    public PlanIndexFloatingPanel( String id, IModel<Plan> planModel ) {
        super( id, planModel );
        init();
    }

    private void init() {
        addHeading();
        addPlanIndexPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "Index" ) );
    }

    private void addPlanIndexPanel() {
        planIndexPanel = new PlanIndexPanel(
                "index",
                new Model<Plan>(getPlan() ),
                null );
        getContentContainer().add( planIndexPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.PLAN_INDEX);
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "Index";
    }

}
