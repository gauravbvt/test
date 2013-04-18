package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.PlanGoalsPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;

/**
 * All goals floating panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/17/13
 * Time: 9:31 PM
 */
public class AllGoalsFloatingPanel extends AbstractFloatingCommandablePanel {

    private PlanGoalsPanel planGoalsPanel;

    public AllGoalsFloatingPanel( String id ) {
        super( id );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "scoping";
    }

    @Override
    public String getHelpTopicId() {
        return "all-goals";
    }


    private void init() {
        addHeading();
        addPlanGoalsPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "All goals" ) );
    }

    private void addPlanGoalsPanel() {
        planGoalsPanel = new PlanGoalsPanel( "goals" );
        getContentContainer().add( planGoalsPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.ALL_GOALS);
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "All goals";
    }

}
