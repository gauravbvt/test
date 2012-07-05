package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.FloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.PlanTagsPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * All Tags Floating Panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 7:21 PM
 */
public class AllTagsFloatingPanel extends FloatingCommandablePanel {

    private PlanTagsPanel planTagsPanel;

    public AllTagsFloatingPanel( String id, IModel<Plan> planModel ) {
        super( id, planModel );
        init();
    }

    private void init() {
        addHeading();
        addPlanEventsPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "All tags" ) );
    }

    private void addPlanEventsPanel() {
        planTagsPanel = new PlanTagsPanel( "tags" );
        getContentContainer().add( planTagsPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.ALL_TAGS);
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "All tags";
    }

}

