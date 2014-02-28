package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.ModelVersionsPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Plan Versions Floating Panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 5:55 PM
 */
public class ModelVersionsFloatingPanel extends AbstractFloatingCommandablePanel {

    private ModelVersionsPanel modelVersionsPanel;

    public ModelVersionsFloatingPanel( String id, IModel<CollaborationModel> planModel ) {
        super( id, planModel );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "improving";
    }

    @Override
    public String getHelpTopicId() {
        return "model-versions";
    }

    private void init() {
        addHeading();
        addModelVersionsPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "Collaboration model versions" ) );
    }

    private void addModelVersionsPanel() {
        modelVersionsPanel = new ModelVersionsPanel(
                "versions",
                new Model<CollaborationModel>( getCollaborationModel() ),
                null );
        getContentContainer().add( modelVersionsPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.MODEL_VERSIONS );
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "Collaboration model versions";
    }

}
