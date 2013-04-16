package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.PlanChecklistsMapPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * Protocols Map Floating Panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 5:35 PM
 */
public class ChecklistsMapFloatingPanel extends AbstractFloatingCommandablePanel {

    private PlanChecklistsMapPanel protocolsMapPanel;

    public ChecklistsMapFloatingPanel( String id, IModel<Plan> planModel ) {
        super( id, planModel );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "improving";
    }

    @Override
    public String getHelpTopicId() {
        return "verify-checklists";
    }

    private void init() {
        addHeading();
        addProtocolsMapPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "Checklists map" ) );
    }

    private void addProtocolsMapPanel() {
        protocolsMapPanel = new PlanChecklistsMapPanel( "map" );
        getContentContainer().add( protocolsMapPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.CHECKLISTS_MAP );
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "Checklists map";
    }

    @Override
    protected int getWidth() {
        return 1000;
    }
}
