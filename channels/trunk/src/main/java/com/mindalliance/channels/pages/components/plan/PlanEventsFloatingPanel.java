package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.FloatingCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Plan event floating panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 3:47 PM
 */
public class PlanEventsFloatingPanel extends FloatingCommandablePanel {

    private PlanEventsPanel planEventsPanel;

    public PlanEventsFloatingPanel( String id, IModel<Event> eventModel ) {
        super( id, eventModel );
        init();
    }

    private void init() {
        addHeading();
        addPlanEventsPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "Events in scope" ) );
    }

    private void addPlanEventsPanel() {
        planEventsPanel = new PlanEventsPanel(
                "events",
                new Model<Plan>(getPlan() ),
                null );
        getContentContainer().add( planEventsPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.ALL_EVENTS);
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "Events in scope";
    }

}
