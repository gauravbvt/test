package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.ModelSegmentsMapPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 4:46 PM
 */
public class ModelSegmentsFloatingPanel extends AbstractFloatingCommandablePanel {

    @SpringBean
    private ImagingService imagingService;

    public ModelSegmentsFloatingPanel( String id, Model<CollaborationModel> collaborationModelModel ) {
        super( id, collaborationModelModel );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "scoping";
    }

    @Override
    public String getHelpTopicId() {
        return "all-segments";
    }

    private void init() {
        addHeading();
       // addModelName();
        addModelImage();
        addModelDescription();
        addModelSegmentsPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "All segments of collaboration model " + getCollaborationModel().getName() ) );
    }

  /*  private void addModelName() {
        getContentContainer().add( new Label( "modelName", getCollaborationModel().getName() ) );
    }*/

    private void addModelImage() {
        WebMarkupContainer planImage = new WebMarkupContainer( "modelImage" );
        String path =  getPlanImagePath();
        planImage.add( new AttributeModifier( "src", path == null ? "images/plan.png" : path  ) );
        makeVisible( planImage, path != null );
        getContentContainer().add( planImage );
    }

    private void addModelDescription() {
        String description = getCollaborationModel().getDescription().trim();
        Label descriptionLabel = new Label(
                "modelDescription",
                description.isEmpty() ? "No description" : description );
        makeVisible( descriptionLabel, !description.isEmpty() );
        getContentContainer().add( descriptionLabel );
    }

    private void addModelSegmentsPanel() {
        ModelSegmentsMapPanel modelSegmentsPanel = new ModelSegmentsMapPanel(
                "segments",
                new Model<CollaborationModel>( getCollaborationModel() ),
                null );
        getContentContainer().add( modelSegmentsPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.ALL_SEGMENTS );
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "All Segments";
    }

    private String getPlanImagePath() {
        CollaborationModel collaborationModel = getCollaborationModel();
        return imagingService.getSquareIconUrl( getCommunityService(), collaborationModel );
    }

    @Override
    protected int getTop() {
        return 100;
    }

    @Override
    protected int getWidth() {
        return 788;
    }

    @Override
    protected int getLeft() {
        return 286;
    }

    @Override
    protected int getBottom() {
        return 20;
    }
}
