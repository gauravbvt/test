package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.segment.SegmentPartMoverPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Task mover floating panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 5:29 PM
 */
public class TaskMoverFloatingPanel  extends AbstractFloatingCommandablePanel {

    private SegmentPartMoverPanel segmentPartMoverPanel;

    public TaskMoverFloatingPanel( String id, IModel<Segment> segmentModel ) {
        super( id, segmentModel );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "improving";
    }

    @Override
    public String getHelpTopicId() {
        return "moving-tasks";
    }

    private void init() {
        addHeading();
        addPlanEventsPanel();
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "Task mover" ) );
    }

    private void addPlanEventsPanel() {
        segmentPartMoverPanel = new SegmentPartMoverPanel(
                "mover",
                new Model<Segment>( getSegment() ),
                null );
        getContentContainer().add( segmentPartMoverPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        segmentPartMoverPanel.releaseLocks();
        Change change = new Change( Change.Type.Collapsed, Channels.TASK_MOVER);
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "Task mover";
    }

    private Segment getSegment() {
        return (Segment)getModel().getObject();
    }
}
