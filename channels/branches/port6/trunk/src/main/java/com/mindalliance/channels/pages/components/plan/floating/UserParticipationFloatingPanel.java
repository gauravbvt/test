package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.PlanUsersParticipationPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * OBSOLETE.
 * Plan Participation Floating Panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 6:55 PM
 */
public class UserParticipationFloatingPanel extends AbstractFloatingCommandablePanel {  // todo - COMMUNITY - remove

    public UserParticipationFloatingPanel( String id, IModel<Plan> planModel ) {
        super( id, planModel );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return null;  // Todo
    }

    @Override
    public String getHelpTopicId() {
        return null;  // Todo
    }

    private void init() {
        addHeading();
        addUserParticipationPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "User participation as agents" ) );
    }

    private void addUserParticipationPanel() {
        PlanUsersParticipationPanel usersParticipationPanel = new PlanUsersParticipationPanel(
                "participation" );
        getContentContainer().add( usersParticipationPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.PLAN_PARTICIPATION);
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "User participation as agents";
    }

}

