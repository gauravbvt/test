package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.PlanBibliographyPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * Bibliography Floating Panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 7:31 PM
 */
public class BibliographyFloatingPanel extends AbstractFloatingCommandablePanel {

    private PlanBibliographyPanel planBibliographyPanel;

    public BibliographyFloatingPanel( String id, IModel<Plan> planModel ) {
        super( id, planModel );
        init();
    }

    private void init() {
        addHeading();
        addPlanBibliographyPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "All documents" ) );
    }

    private void addPlanBibliographyPanel() {
        planBibliographyPanel = new PlanBibliographyPanel( "bibliography" );
        getContentContainer().add( planBibliographyPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.BIBLIOGRAPHY);
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "All documents";
    }

}

