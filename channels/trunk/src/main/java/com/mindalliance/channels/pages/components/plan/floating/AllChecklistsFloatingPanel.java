package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.PlanChecklistsPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * All checklists floating panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/29/13
 * Time: 8:13 AM
 */
public class AllChecklistsFloatingPanel  extends AbstractFloatingCommandablePanel {

    private PlanChecklistsPanel planChecklistsPanel;

    public AllChecklistsFloatingPanel( String id, IModel<Plan> planModel ) {
        super( id, planModel );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "improving";
    }

    @Override
    public String getHelpTopicId() {
        return "all-checklists";
    }

    private void init() {
        addHeading();
        addPlanChecklistsPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "All checklists" ) );
    }

    private void addPlanChecklistsPanel() {
        planChecklistsPanel = new PlanChecklistsPanel( "checklists" );
        getContentContainer().add( planChecklistsPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.ALL_CHECKLISTS);
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "All checklists";
    }


}
