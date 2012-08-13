package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.PlanIssuesPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * All Issues Floating Panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 5:46 PM
 */
public class AllIssuesFloatingPanel extends AbstractFloatingCommandablePanel {

    private PlanIssuesPanel allIssuesPanel;

    public AllIssuesFloatingPanel( String id, IModel<Plan> planModel ) {
        super( id, planModel );
        init();
    }

    private void init() {
        addHeading();
        addPlanIssuesPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "All issues" ) );
    }

    private void addPlanIssuesPanel() {
        allIssuesPanel = new PlanIssuesPanel( "issues" );
        getContentContainer().add( allIssuesPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.ALL_ISSUES);
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "All issues";
    }

    @Override
    protected int getWidth() {
        return 1000;
    }
}
