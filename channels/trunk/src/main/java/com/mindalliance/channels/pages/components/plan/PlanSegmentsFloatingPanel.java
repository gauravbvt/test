package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.FloatingCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 4:46 PM
 */
public class PlanSegmentsFloatingPanel extends FloatingCommandablePanel {

    private PlanSegmentsMapPanel planSegmentsPanel;

    public PlanSegmentsFloatingPanel( String id, Model<Plan> planModel ) {
        super( id, planModel );
        init();
    }
    private void init() {
        addHeading();
        addPlanSegmentsPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "All segments" ) );
    }

    private void addPlanSegmentsPanel() {
        planSegmentsPanel = new PlanSegmentsMapPanel(
                "segments",
                new Model<Plan>(getPlan() ),
                null );
        getContentContainer().add( planSegmentsPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.ALL_SEGMENTS );
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "All segments";
    }

}
