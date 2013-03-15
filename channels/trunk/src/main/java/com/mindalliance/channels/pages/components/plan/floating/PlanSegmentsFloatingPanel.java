package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.PlanSegmentsMapPanel;
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
public class PlanSegmentsFloatingPanel extends AbstractFloatingCommandablePanel {

    @SpringBean
    private ImagingService imagingService;

    private PlanSegmentsMapPanel planSegmentsPanel;

    public PlanSegmentsFloatingPanel( String id, Model<Plan> planModel ) {
        super( id, planModel );
        init();
    }

    @Override
    public String getSectionId() {
        return "concepts";
    }

    @Override
    public String getTopicId() {
        return "segment";
    }

    private void init() {
        addHeading();
        addPlanName();
        addPlanImage();
        addPlanDescription();
        addPlanSegmentsPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "All segments of " + getPlan().getName() ) );
    }

    private void addPlanName() {
        getContentContainer().add( new Label( "planName", getPlan().getName() ) );
    }

    private void addPlanImage() {
        WebMarkupContainer planImage = new WebMarkupContainer( "planImage" );
        planImage.add( new AttributeModifier( "src", getPlanImagePath() ) );
        getContentContainer().add( planImage );
    }

    private void addPlanDescription() {
        String description = getPlan().getDescription().trim();
        getContentContainer().add( new Label(
                "planDescription",
                description.isEmpty() ? "No description" : description ) );
    }

    private void addPlanSegmentsPanel() {
        planSegmentsPanel = new PlanSegmentsMapPanel(
                "segments",
                new Model<Plan>( getPlan() ),
                null );
        getContentContainer().add( planSegmentsPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.ALL_SEGMENTS );
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "Plan map";
    }

    private String getPlanImagePath() {
        Plan plan = getPlan();
        String path = imagingService.getSquareIconUrl( getCommunityService(), plan );
        return path == null ? "images/plan.png" : path;
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
