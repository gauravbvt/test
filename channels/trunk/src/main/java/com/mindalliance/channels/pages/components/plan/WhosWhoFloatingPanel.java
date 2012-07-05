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
 * Who's Who Floating Panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 7:28 PM
 */
public class WhosWhoFloatingPanel extends FloatingCommandablePanel {

    private PlanWhosWhoPanel planWhosWhoPanel;

    public WhosWhoFloatingPanel( String id, IModel<Plan> planModel ) {
        super( id, planModel );
        init();
    }

    private void init() {
        addHeading();
        addPlanWhosWhoPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "Who's who" ) );
    }

    private void addPlanWhosWhoPanel() {
        planWhosWhoPanel = new PlanWhosWhoPanel( "whosWho", new Model<Plan>( getPlan()  ), null );
        getContentContainer().add( planWhosWhoPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.WHOS_WHO);
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "Who's who";
    }

}


