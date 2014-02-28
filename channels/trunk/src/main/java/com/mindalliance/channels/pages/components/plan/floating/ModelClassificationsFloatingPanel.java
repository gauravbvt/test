package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.ModelClassificationSystemsPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 5:12 PM
 */
public class ModelClassificationsFloatingPanel extends AbstractFloatingCommandablePanel {

    private ModelClassificationSystemsPanel modelClassificationSystemsPanel;

    public ModelClassificationsFloatingPanel( String id, IModel<CollaborationModel> planModel ) {
        super( id, planModel );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "scoping";
    }

    @Override
    public String getHelpTopicId() {
        return "classifications";
    }

    private void init() {
        addHeading();
        addPlanEventsPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "Classification systems" ) );
    }

    private void addPlanEventsPanel() {
        modelClassificationSystemsPanel = new ModelClassificationSystemsPanel(
                "classifications" );
        getContentContainer().add( modelClassificationSystemsPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.ALL_CLASSIFICATIONS);
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "Classification systems";
    }
}
