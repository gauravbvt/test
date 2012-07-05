package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.pages.components.FloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.requirements.PlanRequirementsPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/3/12
 * Time: 3:03 PM
 */
public class PlanRequirementsFloatingPanel extends FloatingCommandablePanel {

    private PlanRequirementsPanel requirementsPanel;

    public PlanRequirementsFloatingPanel( String id, Model<Requirement> requirementModel ) {
        super( id, requirementModel);
        init();
    }

    private void init() {
        addHeading();
        addRequirementsPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "Plan requirements" ) );
    }

    private void addRequirementsPanel() {
       requirementsPanel = new PlanRequirementsPanel(
               "requirements",
               new Model<Requirement>( (Requirement)getModel().getObject()  ) );
       getContentContainer().add( requirementsPanel );
    }

    public void select( Requirement requirement ) {
        requirementsPanel.select( requirement );
    }


    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Requirement.UNKNOWN );
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "Requirements";
    }
}
