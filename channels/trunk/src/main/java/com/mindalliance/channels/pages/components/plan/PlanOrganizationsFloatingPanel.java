package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.FloatingCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Plan organizations floating panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 4:06 PM
 */
public class PlanOrganizationsFloatingPanel extends FloatingCommandablePanel {
    private PlanOrganizationsPanel planOrgsPanel;

    public PlanOrganizationsFloatingPanel( String id, IModel<Organization> orgModel ) {
        super( id, orgModel );
        init();
    }

    private void init() {
        addHeading();
        addPlanOrgsPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "Organizations in scope" ) );
    }

    private void addPlanOrgsPanel() {
        planOrgsPanel = new PlanOrganizationsPanel(
                "organizations",
                new Model<Plan>(getPlan() ),
                null );
        getContentContainer().add( planOrgsPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.ALL_ORGANIZATIONS );
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "Organizations in scope";
    }

}
